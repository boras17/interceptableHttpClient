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
import java.util.function.Function;

public class InterceptableHttpClient extends HttpClient {

    private final HttpClient client;
    private RequestInterceptor requestInterceptor;
    private final Map<Integer, ResponseInterceptor<?>> response_interceptors_map = new TreeMap<>();

    public enum Type{
        REQUEST_INTERCEPTOR, RESPONSE_INTERCEPTOR
    }

    public InterceptableHttpClient(final HttpClient client){
        this.client=client;
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

    public void interceptor(ResponseInterceptor<?> interceptor,
                         int order){
        this.response_interceptors_map.computeIfAbsent(order,
                new Function<Integer, ResponseInterceptor<?>>() {
            @Override
            public ResponseInterceptor<?> apply(Integer integer) {
                return (ResponseInterceptor<?>) interceptor;
            }
        });

    }
    public void interceptor(RequestInterceptor requestInterceptor) {
        Optional.ofNullable(this.requestInterceptor)
                .ifPresentOrElse(interceptor -> {
                    String registeredRequestInterceptorName = interceptor.getClass().getName();
                    String registeredRequestInterceptorLocation = interceptor.getClass().getPackageName();
                    StringBuilder constructErrorMsg = new StringBuilder();

                    constructErrorMsg.append("You already registered request interceptor in: ")
                            .append(registeredRequestInterceptorLocation)
                            .append(" package")
                            .append("\n")
                            .append(" interceptor class name: ")
                            .append(registeredRequestInterceptorName);

                    System.err.print(constructErrorMsg);
                }, () ->{
                    this.requestInterceptor = requestInterceptor;
                });
    }
    private HttpRequest interceptRequest(HttpRequest request){
        return this.requestInterceptor.handle(request);
    }

    public Map<Type, Interceptor<?,?>> getInterceptorsMap() {
        Map<Type, Interceptor<?,?>> interceptors = new EnumMap<Type, Interceptor<?, ?>>(Type.class);

        for(Map.Entry<Integer, ResponseInterceptor<?>> entry: this.response_interceptors_map.entrySet()){
            interceptors.put(Type.RESPONSE_INTERCEPTOR, entry.getValue());
        }
        Optional.ofNullable(this.requestInterceptor).ifPresent(interceptor -> {
            interceptors.put(Type.REQUEST_INTERCEPTOR, interceptor);
        });
        return interceptors;
    }

    private <T> ResponseWrapper<T> interceptionResponse(HttpResponse<T> response){
        PrevInstances<T> prevInstances = new PrevInstances<>();
        ResponseWrapper<T> modifiedResponse = null;

        prevInstances.setPrevResponse(new ResponseWrapper<>(response));

        for(Map.Entry<Integer,ResponseInterceptor<?>> entry: this.response_interceptors_map.entrySet()){
            ResponseInterceptor<T> responseInterceptor= (ResponseInterceptor<T>) entry.getValue();
            modifiedResponse = responseInterceptor.handle(prevInstances.getPrevResponse().orElseThrow());
            prevInstances.setPrevResponse(modifiedResponse);
        }

        return modifiedResponse;
    }

    @Override
    public <T> HttpResponse<T> send(HttpRequest request,
                                    HttpResponse.BodyHandler<T> responseBodyHandler) throws IOException, InterruptedException {
        HttpRequest modified_request=this.interceptRequest(request);
        System.out.println("modified req: " + modified_request.headers().toString());
        HttpResponse<T> response=this.client.send(modified_request,responseBodyHandler);
        return this.interceptionResponse(response);
    }

    @Override
    public <T> CompletableFuture<HttpResponse<T>> sendAsync(HttpRequest request,
                                                            HttpResponse.BodyHandler<T> responseBodyHandler) {
        return this.client.sendAsync(request,responseBodyHandler)
                .thenApply(new Function<HttpResponse<T>, HttpResponse<T>>() {
                    @Override
                    public HttpResponse<T> apply(HttpResponse<T> tHttpResponse) {
                        return interceptionResponse(tHttpResponse);
                    }
                });
    }

    @Override
    public <T> CompletableFuture<HttpResponse<T>> sendAsync(HttpRequest request,
                                                            HttpResponse.BodyHandler<T> responseBodyHandler,
                                                            HttpResponse.PushPromiseHandler<T> pushPromiseHandler) {
        return this.client.sendAsync(request,responseBodyHandler);
    }
}

