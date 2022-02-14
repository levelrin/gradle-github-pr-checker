/*
 * Copyright (c) 2022 Rin (https://www.levelrin.com)
 *
 * This file has been created under the terms of the MIT License.
 * See the details at https://github.com/levelrin/gradle-github-pr-checker/blob/main/LICENSE
 */

package com.levelrin.gradle.github.pr.checker.fake.http;

import java.net.Authenticator;
import java.net.CookieHandler;
import java.net.ProxySelector;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import org.mockito.Mockito;

/**
 * We can use this when we want to return a fake response body.
 */
public final class FakeHttpClient extends HttpClient {

    /**
     * We will throw exceptions with this message
     * when the unexpected method is called.
     */
    private static final String ERROR_MESSAGE = "You should not call this method.";

    /**
     * It's a fake response body generator.
     * The parameter is the request object.
     */
    private final Function<HttpRequest, String> resGenerator;

    /**
     * Constructor.
     * @param resGenerator See {@link FakeHttpClient#resGenerator}.
     */
    public FakeHttpClient(final Function<HttpRequest, String> resGenerator) {
        super();
        this.resGenerator = resGenerator;
    }

    @Override
    public Optional<CookieHandler> cookieHandler() {
        throw new UnsupportedOperationException(ERROR_MESSAGE);
    }

    @Override
    public Optional<Duration> connectTimeout() {
        throw new UnsupportedOperationException(ERROR_MESSAGE);
    }

    @Override
    public Redirect followRedirects() {
        throw new UnsupportedOperationException(ERROR_MESSAGE);
    }

    @Override
    public Optional<ProxySelector> proxy() {
        throw new UnsupportedOperationException(ERROR_MESSAGE);
    }

    @Override
    public SSLContext sslContext() {
        throw new UnsupportedOperationException(ERROR_MESSAGE);
    }

    @Override
    public SSLParameters sslParameters() {
        throw new UnsupportedOperationException(ERROR_MESSAGE);
    }

    @Override
    public Optional<Authenticator> authenticator() {
        throw new UnsupportedOperationException(ERROR_MESSAGE);
    }

    @Override
    public Version version() {
        throw new UnsupportedOperationException(ERROR_MESSAGE);
    }

    @Override
    public Optional<Executor> executor() {
        throw new UnsupportedOperationException(ERROR_MESSAGE);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> HttpResponse<T> send(final HttpRequest httpRequest, final HttpResponse.BodyHandler<T> bodyHandler) {
        final HttpResponse<T> response = Mockito.mock(HttpResponse.class);
        Mockito.when(
            response.body()
        ).thenReturn(
            (T) this.resGenerator.apply(httpRequest)
        );
        return response;
    }

    @Override
    public <T> CompletableFuture<HttpResponse<T>> sendAsync(final HttpRequest httpRequest, final HttpResponse.BodyHandler<T> bodyHandler) {
        throw new UnsupportedOperationException(ERROR_MESSAGE);
    }

    @Override
    public <T> CompletableFuture<HttpResponse<T>> sendAsync(final HttpRequest httpRequest, final HttpResponse.BodyHandler<T> bodyHandler, final HttpResponse.PushPromiseHandler<T> pushHandler) {
        throw new UnsupportedOperationException(ERROR_MESSAGE);
    }

}
