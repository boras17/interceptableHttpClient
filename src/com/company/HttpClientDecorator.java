package com.company;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import java.io.IOException;
import java.net.Authenticator;
import java.net.CookieHandler;
import java.net.ProxySelector;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BiFunction;
import java.util.function.Function;

public class HttpClientDecorator extends HttpClient {

    static enum Types{
        REQUEST,RESPONSE
    }

    private final HttpClient client;
    private final Map<  Types, Map<Integer, Interceptor<?,?>>  > interceptorsMap = new EnumMap<Types, Map<Integer, Interceptor<?, ?>>>(Types.class);

    public HttpClientDecorator(final HttpClient client){
        this.client=client;
        this.interceptorsMap.put(Types.REQUEST, new TreeMap<>());
        this.interceptorsMap.put(Types.RESPONSE,new TreeMap<>());
    }

    @Override
    public Optional<CookieHandler> cookieHandler() {
        return this.client.cookieHandler();
    }

    @Override
    public Optional<Duration> connectTimeout() {
        return this.client.connectTimeout();
    }

    @Override
    public Redirect followRedirects() {
        return client.followRedirects();
    }

    @Override
    public Optional<ProxySelector> proxy() {
        return this.client.proxy();
    }

    @Override
    public SSLContext sslContext() {
        return this.client.sslContext();
    }

    @Override
    public SSLParameters sslParameters() {
        return this.client.sslParameters();
    }

    @Override
    public Optional<Authenticator> authenticator() {
        return this.client.authenticator();
    }

    @Override
    public Version version() {
        return this.client.version();
    }

    @Override
    public Optional<Executor> executor() {
        return this.client.executor();
    }

    // additional content for decorating requests and responses

    //--------------------------

    public void decorate(Interceptor<?,?> interceptor, int order){
        if(interceptor instanceof ResponseInterceptor){

            Map<Integer,Interceptor<?,?>> interceptors = this.interceptorsMap.get(Types.RESPONSE);
            interceptors.put(order, interceptor);

            this.interceptorsMap.computeIfPresent(Types.RESPONSE, new BiFunction<Types,
                    Map<Integer, Interceptor<?, ?>>, Map<Integer, Interceptor<?, ?>>>() {
                @Override
                public Map<Integer, Interceptor<?, ?>> apply(Types types, Map<Integer, Interceptor<?, ?>> integerInterceptorMap) {
                    return interceptors;
                }
            });
        }else{
            Map<Integer,Interceptor<?,?>> interceptors = this.interceptorsMap.get(Types.REQUEST);
            interceptors.put(order, interceptor);

            this.interceptorsMap.computeIfPresent(Types.REQUEST, new BiFunction<Types,
                    Map<Integer, Interceptor<?, ?>>, Map<Integer, Interceptor<?, ?>>>() {
                @Override
                public Map<Integer, Interceptor<?, ?>> apply(Types types, Map<Integer, Interceptor<?, ?>> integerInterceptorMap) {
                    return interceptors;
                }
            });
        }
    }
    private HttpRequest interceptRequest(HttpRequest request){
        Map<Integer,Interceptor<?,?>> requestInterceptors=this.interceptorsMap.get(Types.REQUEST);

        Set<Map.Entry<Integer,Interceptor<?,?>>> interceptorsEntrySet=requestInterceptors.entrySet();

        for(Map.Entry<Integer,Interceptor<?,?>> entry: interceptorsEntrySet){
            RequestInterceptor interceptor_re=(RequestInterceptor)entry.getValue();
            interceptor_re.handle(request);
        }
        return request;
    }

    public Map<Types, Map<Integer, Interceptor<?,?>>> getInterceptorsMap() {
        return interceptorsMap;
    }

    private <T>HttpResponse<T> interceptionResponse(HttpResponse<T> response){
        for(Map.Entry<Integer,Interceptor<?,?>> entry: this.interceptorsMap.get(Types.RESPONSE).entrySet()){
            ResponseInterceptor responseInterceptor=(ResponseInterceptor) entry.getValue();
            // tutajjest problem trzbea umozliwisc kolejnym interceptorom
            // operacje na poprzednim wyniku a nie kazdy na nowym

            responseInterceptor.handle(response);
        }
        return new DecoratedResponse<>(response);
    }


    // additional conetntforhandlingrequests and repsonses abowe
    //-------------------------------------------------


    @Override
    public <T> HttpResponse<T> send(HttpRequest request, HttpResponse.BodyHandler<T> responseBodyHandler) throws IOException, InterruptedException {
        HttpRequest modified_request=this.interceptRequest(request);

        HttpResponse<T> response=this.client.send(modified_request,responseBodyHandler);
        return this.interceptionResponse(response);
    }

    @Override
    public <T> CompletableFuture<HttpResponse<T>> sendAsync(HttpRequest request, HttpResponse.BodyHandler<T> responseBodyHandler) {
        return this.client.sendAsync(request,responseBodyHandler)
                .thenApply(new Function<HttpResponse<T>, HttpResponse<T>>() {
                    @Override
                    public HttpResponse<T> apply(HttpResponse<T> tHttpResponse) {
                        return interceptionResponse(tHttpResponse);
                    }
                });
    }
    // interceptor HttpResonseDecorator powinein pamietac porpzedni wynik trasnformacji
    // podobnie jak HttpRequestDecorator powinien pamietac poprzedni wynik modyfikacji

    @Override
    public <T> CompletableFuture<HttpResponse<T>> sendAsync(HttpRequest request, HttpResponse.BodyHandler<T> responseBodyHandler, HttpResponse.PushPromiseHandler<T> pushPromiseHandler) {
        return this.client.sendAsync(request,responseBodyHandler);
    }
}

