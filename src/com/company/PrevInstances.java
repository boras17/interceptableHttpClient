package com.company;

import java.util.Optional;

public class PrevInstances<T> {
    private DecoratedResponse<T> prevResponse;
    private DecoreatedRequest prevRequest;

    public PrevInstances() {

    }

    public Optional<DecoratedResponse<T>> getPrevResponse() {
        return Optional.of(prevResponse);
    }

    public void setPrevResponse(DecoratedResponse<T> prevResponse) {
        this.prevResponse = prevResponse;
    }

    public Optional<DecoreatedRequest> getPrevRequest() {
        return Optional.of(prevRequest);
    }

    public void setPrevRequest(DecoreatedRequest prevRequest) {
        this.prevRequest = prevRequest;
    }
}
