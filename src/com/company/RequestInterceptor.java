package com.company;

import java.net.http.HttpRequest;

public interface RequestInterceptor extends Interceptor<RequestWrapper, HttpRequest> {
}