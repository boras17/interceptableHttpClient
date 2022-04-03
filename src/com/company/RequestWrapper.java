package com.company;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class RequestWrapper extends HttpRequest {
    private HttpRequest httpRequest;

    public RequestWrapper(HttpRequest request){
        this.httpRequest=request;
    }

    public void addHeader(String key, String value){

    }

    public RequestWrapper addHeaders(Map<String, List<String>> headers){
        HttpHeaders prev_headers = this.httpRequest.headers();

        Map<String, List<String>> headersList = new HashMap<>(prev_headers.map());
        headersList.putAll(headers);

        Builder newHttpRequest = HttpRequest.newBuilder()
                .method(this.method(),this.bodyPublisher().orElse(BodyPublishers.noBody()))
                .uri(this.uri())
                .version(version().orElse(HttpClient.Version.HTTP_1_1))
                .timeout(this.timeout().orElse(Duration.ofSeconds(30)))
                .expectContinue(this.expectContinue());

        for(Map.Entry<String, List<String>> entry: headersList.entrySet()){
            newHttpRequest.header(entry.getKey(), String.join(" ", entry.getValue()));
        }

        HttpRequest httpRequest = newHttpRequest.build();

        return new RequestWrapper(httpRequest);
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