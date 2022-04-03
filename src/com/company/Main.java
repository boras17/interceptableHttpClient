package com.company;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

public class Main {

    public static class ResponseToUpperCase implements ResponseInterceptor<String>{
        @Override
        public ResponseWrapper<String> handle(HttpResponse<String> content) {
            ResponseWrapper<String> decoratedResponse = new ResponseWrapper<>(content);
            String previousBody = decoratedResponse.body();
            decoratedResponse.updateBody(previousBody.toUpperCase());
            return decoratedResponse;
        }
    }

    public static class AddJwtHeaderForRequest implements RequestInterceptor{
        @Override
        public RequestWrapper handle(HttpRequest content) {
            RequestWrapper requestWrapper = new RequestWrapper(content);
            Map<String, List<String>> headers = new HashMap<>();
            headers.put("test", Arrays.asList("jeden", "dwa"));
            headers.put("Content-Type", Collections.singletonList("application/json"));
            return requestWrapper.addHeaders(headers);
        }
    }


    public static void main(String[] args) {
        URI test = URI.create("http://localhost:7777/data");

        HttpClient client = HttpClient.newBuilder()
                .build();

        InterceptableHttpClient interceptable
                = new InterceptableHttpClient(client);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(test)
                .GET()
                .build();

        interceptable.interceptor(new ResponseToUpperCase(),1);
        interceptable.interceptor(new AddJwtHeaderForRequest());


        try{
            HttpResponse<String> response = interceptable.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(response.body());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

    }
}
