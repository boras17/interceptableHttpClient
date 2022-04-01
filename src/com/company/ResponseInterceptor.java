package com.company;

import java.net.http.HttpResponse;

public interface ResponseInterceptor extends Interceptor<DecoratedResponse<?>, HttpResponse<?>>{

}

