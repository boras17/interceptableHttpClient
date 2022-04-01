package com.company;

import java.net.http.HttpResponse;

public  interface ResponseInterceptor<T> extends Interceptor<DecoratedResponse<T>, HttpResponse<T>>{

}

