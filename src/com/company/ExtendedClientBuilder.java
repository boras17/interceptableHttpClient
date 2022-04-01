package com.company;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import java.net.Authenticator;
import java.net.CookieHandler;
import java.net.ProxySelector;
import java.net.http.HttpClient;
import java.time.Duration;
import java.util.concurrent.Executor;

public class ExtendedClientBuilder implements HttpClient.Builder{
    private CookieHandler cookieHandler;
    private Duration duration;
    private SSLContext sslContext;
    private SSLParameters sslParameters;
    private Executor executor;
    private HttpClient.Redirect redirect;
    private HttpClient.Version version;
    private int priority;
    private ProxySelector proxySelector;
    private Authenticator authenticator;

    @Override
    public HttpClient.Builder cookieHandler(CookieHandler cookieHandler) {
        this.cookieHandler=cookieHandler;
        return this;
    }

    @Override
    public HttpClient.Builder connectTimeout(Duration duration) {
        this.duration=duration;
        return this;
    }

    @Override
    public HttpClient.Builder sslContext(SSLContext sslContext) {
        this.sslContext=sslContext;
        return this;
    }

    @Override
    public HttpClient.Builder sslParameters(SSLParameters sslParameters) {
        this.sslParameters=sslParameters;
        return this;
    }

    @Override
    public HttpClient.Builder executor(Executor executor) {
        this.executor=executor;
        return this;
    }

    @Override
    public HttpClient.Builder followRedirects(HttpClient.Redirect policy) {
        this.redirect=policy;

        return this;
    }

    @Override
    public HttpClient.Builder version(HttpClient.Version version) {
        this.version=version;
        return this;
    }

    @Override
    public HttpClient.Builder priority(int priority) {
        this.priority=priority;
        return this;
    }

    @Override
    public HttpClient.Builder proxy(ProxySelector proxySelector) {
        this.proxySelector=proxySelector;
        return this;
    }

    @Override
    public HttpClient.Builder authenticator(Authenticator authenticator) {
        this.authenticator=authenticator;
        return this;
    }

    @Override
    public HttpClient build() {
        return HttpClient.newBuilder()
                .cookieHandler(cookieHandler)
                .executor(executor)
                .priority(priority)
                .sslParameters(sslParameters)
                .version(version)
                .sslContext(sslContext)
                .authenticator(authenticator)
                .followRedirects(redirect)
                .connectTimeout(duration)
                .proxy(proxySelector)
                .build();
    }
}
