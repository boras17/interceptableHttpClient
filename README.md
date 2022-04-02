# interceptableHttpClient
This simple wrapper allows you to add some interceptors to you HttpClient with specified order.
Exmaple:
```java 
class ResponseToUpperCase implements ResponseInterceptor<String>{
      @Override
      public DecoratedResponse<String> handle(HttpResponse<String> content) {
          DecoratedResponse<String> decoratedResponse = new DecoratedResponse<String>(content);
          String previousBody = decoratedResponse.body();
          decoratedResponse.updateBody(previousBody.toUpperCase());
          return decoratedResponse;
      }
}
```
You can create Request interceptor too:
