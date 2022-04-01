package com.company;

public interface Interceptor<T,A> {
    T handle(A content);
}

