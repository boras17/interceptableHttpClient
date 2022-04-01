package com.company;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.time.Duration;
import java.util.Optional;

public class DecoreatedRequest extends HttpRequest {
    private HttpRequest httpRequest;
    public static int decorated_request_counter=0;

    public DecoreatedRequest(HttpRequest request){
        this.httpRequest=request;
    }

    @Override
    public Optional<BodyPublisher> bodyPublisher() {
        return httpRequest.bodyPublisher();
    }

    @Override
    public String method() {
        return httpRequest.method();
    }

    @Override
    public Optional<Duration> timeout() {
        return this.httpRequest.timeout();
    }

    @Override
    public boolean expectContinue() {
        return this.httpRequest.expectContinue();
    }

    @Override
    public URI uri() {
        return this.httpRequest.uri();
    }

    @Override
    public Optional<HttpClient.Version> version() {
        return this.httpRequest.version();
    }

    @Override
    public HttpHeaders headers() {
        return this.httpRequest.headers();
    }
}