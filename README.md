# interceptableHttpClient
This simple wrapper allows you to add some interceptors to your HttpClient with specified order.
Exmaple:
```java 
class ResponseToUpperCase implements ResponseInterceptor<String>{
  @Override
  public ResponseWrapper<String> handle(HttpResponse<String> content) {
      ResponseWrapper<String> decoratedResponse = new ResponseWrapper<>(content);
      String previousBody = decoratedResponse.body();
      decoratedResponse.updateBody(previousBody.toUpperCase());
      return decoratedResponse;
  }
}
```

You can create Request interceptor too:
```java 
class AddJwtHeaderForRequest implements RequestInterceptor{
  @Override
  public RequestWrapper handle(HttpRequest content) {
      RequestWrapper requestWrapper = new RequestWrapper(content);
      Map<String, List<String>> headers = new HashMap<>();
      headers.put("test", Arrays.asList("jeden", "dwa"));
      headers.put("Content-Type", Collections.singletonList("application/json"));
      return requestWrapper.addHeaders(headers);
  }
}
```
And the you have to register your interceptors. For this purpose create new HttpClient instance:
```java
HttpClient client = HttpClient.newBuilder()
                .build();
 ```
 And then pass this instance as a constructor parameter for InterceptableHttpClient:
 ```java
 InterceptableHttpClient interceptable= new InterceptableHttpClient(client);
```
The final step is to register interceptors via 'interceptor' method from InterceptableHttpClient:
 ```java
interceptable.interceptor(new ResponseToUpperCase(),1);
interceptable.interceptor(new AddJwtHeaderForRequest());
 ```
Warning! When you are registering response interceptor you have to specify his order which is second parameter of 'interceptor' method. Second thing is that you can register only one request interceptor and then you do not define his order.
How to send requests? 
InterceptableHttpClient inherits HttpClient so it has the same methods as HttpClient has:
 ```java
try{
    HttpResponse<String> response = interceptable.send(request, HttpResponse.BodyHandlers.ofString());
    System.out.println(response.body());
} catch (IOException | InterruptedException e) {
    e.printStackTrace();
}
 ```
