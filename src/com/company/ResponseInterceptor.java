package com.company;

import java.net.http.HttpResponse;

public  interface ResponseInterceptor<T> extends Interceptor<ResponseWrapper<T>, HttpResponse<T>>{

}

