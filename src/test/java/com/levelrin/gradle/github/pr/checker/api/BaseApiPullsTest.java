/*
 * Copyright (c) 2022 Rin (https://www.levelrin.com)
 *
 * This file has been created under the terms of the MIT License.
 * See the details at https://github.com/levelrin/gradle-github-pr-checker/blob/main/LICENSE
 */

package com.levelrin.gradle.github.pr.checker.api;

import com.levelrin.gradle.github.pr.checker.fake.FakeGitHubPrExtension;
import com.levelrin.gradle.github.pr.checker.fake.http.FakeHttpClient;
import java.net.http.HttpClient;
import java.util.StringJoiner;
import java.util.concurrent.atomic.AtomicInteger;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;

/**
 * The test of {@link BaseApiPulls}.
 */
final class BaseApiPullsTest {

    @Test
    public void shouldCallApiOnceIfThereAreLessThan100PullRequests() {
        final AtomicInteger counter = new AtomicInteger();
        final HttpClient http = new FakeHttpClient(request -> {
            counter.incrementAndGet();
            // The target class doesn't care about the JSON content.
            // To avoid having really long fake data, we just use empty JSONs.
            return "[{}, {}, {}]";
        });
        new BaseApiPulls(http, new FakeGitHubPrExtension()).list();
        MatcherAssert.assertThat(
            counter.get(),
            CoreMatchers.equalTo(1)
        );
    }

    @Test
    public void shouldCallNextPageIfThereAre100PullRequests() {
        final AtomicInteger counter = new AtomicInteger();
        final HttpClient http = new FakeHttpClient(request -> {
            final String result;
            final int currentCount = counter.getAndIncrement();
            if (currentCount == 0) {
                // We will create 100 empty JSONs like {}, {}, ..., {}
                // and put that into the array like [{}, {}, ..., {}].
                final StringJoiner firstPulls = new StringJoiner(",");
                final int maxPull = 100;
                for (int iteration = 0; iteration < maxPull; iteration = iteration + 1) {
                    firstPulls.add("{}");
                }
                result = String.format("[%s]", firstPulls);
            } else if (currentCount == 1) {
                result = "[]";
            } else {
                throw new IllegalStateException(
                    String.format(
                        "Unexpected count: %d",
                        counter.get()
                    )
                );
            }
            return result;
        });
        new BaseApiPulls(http, new FakeGitHubPrExtension()).list();
        final int expectedCount = 2;
        MatcherAssert.assertThat(
            counter.get(),
            CoreMatchers.equalTo(expectedCount)
        );
    }

}
