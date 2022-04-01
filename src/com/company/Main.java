package com.company;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class Main {

    public static class RequestPrinter implements RequestInterceptor{
        @Override
        public DecoreatedRequest handle(HttpRequest content) {
            return new DecoreatedRequest(content);
        }
    }

    public static class ResponseInterceptor implements com.company.ResponseInterceptor{

        @Override
        public DecoratedResponse<?> handle(HttpResponse<?> content) {
            return new DecoratedResponse<>(content);
        }
    }


    public static void main(String[] args) {
        URI test = URI.create("https://jsonplaceholder.typicode.com/todos/1");

        HttpClientDecorator httpClientDecorator
                = new HttpClientDecorator(HttpClient.newBuilder()
                .build());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(test)
                .GET()
                .build();

        httpClientDecorator.decorate(new ResponseInterceptor(),2);
        httpClientDecorator.decorate(new ResponseInterceptor(),1);
        httpClientDecorator.decorate(new ResponseInterceptor(),3);
        httpClientDecorator.decorate(new RequestPrinter(), 4);
        httpClientDecorator.decorate(new RequestPrinter(), 3);
        httpClientDecorator.decorate(new RequestPrinter(), 2);

        System.out.println(httpClientDecorator.getInterceptorsMap());

        try{
            HttpResponse<String> response = httpClientDecorator.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

    }
}
