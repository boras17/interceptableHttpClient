package com.company;

import java.util.Optional;

public class PrevInstances<T> {
    private DecoratedResponse<T> prevResponse;

    public PrevInstances() {

    }

    public Optional<DecoratedResponse<T>> getPrevResponse() {
        return Optional.of(prevResponse);
    }

    public void setPrevResponse(DecoratedResponse<T> prevResponse) {
        this.prevResponse = prevResponse;
    }
}
