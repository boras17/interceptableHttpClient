package com.company;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class Main {

    public static class ResponseToUpperCase implements ResponseInterceptor<String>{
        @Override
        public ResponseWrapper<String> handle(HttpResponse<String> content) {
            ResponseWrapper<String> decoratedResponse = new ResponseWrapper<String>(content);
            String previousBody = decoratedResponse.body();
            decoratedResponse.updateBody(previousBody.toUpperCase());
            return decoratedResponse;
        }
    }

    public static class AddJwtHeaderForRequest implements RequestInterceptor{
        @Override
        public RequestWrapper handle(HttpRequest content) {
            RequestWrapper requestWrapper = new RequestWrapper(content);
            return new RequestWrapper(content);
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
        Method[] methods = request.getClass().getDeclaredMethods();

        for (Method method: methods){
            System.out.println(method.getName());
        }

        httpClientDecorator.decorate(new ResponseToUpperCase(),1);

        System.out.println(httpClientDecorator.getInterceptorsMap());

        try{
            HttpResponse<String> response = httpClientDecorator.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(response.body());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

    }
}
