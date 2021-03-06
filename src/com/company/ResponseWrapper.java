package com.company;


import javax.net.ssl.SSLSession;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;

public class ResponseWrapper<T> implements HttpResponse<T> {
    public static int decorated_resonse_handler=0;
    private HttpResponse<T> response;
    private T body;

    public ResponseWrapper(HttpResponse<T> response){
        this.response=response;
        ResponseWrapper.decorated_resonse_handler+=1;
        this.body = response.body();
    }

    public void updateBody(T newBody) {
        this.body = newBody;
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
        return this.body;
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

