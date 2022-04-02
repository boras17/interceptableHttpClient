package com.company;

import java.util.Optional;

public class PrevInstances<T> {
    private ResponseWrapper<T> prevResponse;

    public PrevInstances() {

    }

    public Optional<ResponseWrapper<T>> getPrevResponse() {
        return Optional.of(prevResponse);
    }

    public void setPrevResponse(ResponseWrapper<T> prevResponse) {
        this.prevResponse = prevResponse;
    }
}
