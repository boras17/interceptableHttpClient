package com.company;

import javax.net.ssl.SSLSession;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;

public class DecoratedResponse<T> implements HttpResponse<T> {
    public static int decorated_resonse_handler=0;
    private HttpResponse<T> response;
    private DecoratedResponse<T> proviousResponse;

    public DecoratedResponse(HttpResponse<T> response){
        this.response=response;
        DecoratedResponse.decorated_resonse_handler+=1;
    }

    public void setPreviousDecoration(DecoratedResponse<T> decoration){
        this.proviousResponse=decoration;
    }

    public Optional<DecoratedResponse<T>> prevDecoratedResponse(){
        return Optional.ofNullable(this.proviousResponse);
    }

    @Override
    public int statusCode() {
        return response.statusCode();
    }

    @Override
    public HttpRequest request() {
        return response.request();
    }

    @Override
    public Optional<HttpResponse<T>> previousResponse() {
        return response.previousResponse();
    }

    @Override
    public HttpHeaders headers() {
        return response.headers();
    }

    @Override
    public T body() {
        return this.response.body();
    }

    @Override
    public Optional<SSLSession> sslSession() {
        return response.sslSession();
    }

    @Override
    public URI uri() {
        return response.uri();
    }

    @Override
    public HttpClient.Version version() {
        return response.version();
    }
}

