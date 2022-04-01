package com.company;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class Main {

    public static class ResponseInterceptor implements com.company.ResponseInterceptor<String>{
        @Override
        public DecoratedResponse<String> handle(HttpResponse<String> content) {
            DecoratedResponse<String> decoratedResponse = new DecoratedResponse<String>(content);
            return decoratedResponse;
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

        System.out.println(httpClientDecorator.getInterceptorsMap());

        try{
            HttpResponse<String> response = httpClientDecorator.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(response.body());
            HttpResponse<String> response2 = httpClientDecorator.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(response2.body());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

    }
}
