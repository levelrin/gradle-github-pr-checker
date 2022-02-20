/*
 * Copyright (c) 2022 Rin (https://www.levelrin.com)
 *
 * This file has been created under the terms of the MIT License.
 * See the details at https://github.com/levelrin/gradle-github-pr-checker/blob/main/LICENSE
 */

package com.levelrin.gradle.github.pr.checker.api.json;

import com.levelrin.gradle.github.pr.checker.fake.api.json.FakeJsonPull;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;

/**
 * The test of {@link BaseJsonPull}.
 */
final class BaseJsonPullTest {

    @Test
    public void shouldParseNumber() {
        final int expected = 1347;
        MatcherAssert.assertThat(
            new BaseJsonPull(
                new FakeJsonPull().toString()
            ).number(),
            CoreMatchers.equalTo(expected)
        );
    }

    @Test
    public void shouldParseHead() {
        MatcherAssert.assertThat(
            new BaseJsonPull(
                new FakeJsonPull().toString()
            ).head(),
            CoreMatchers.instanceOf(JsonHead.class)
        );
    }

    @Test
    public void shouldParseBase() {
        MatcherAssert.assertThat(
            new BaseJsonPull(
                new FakeJsonPull().toString()
            ).base(),
            CoreMatchers.instanceOf(JsonBase.class)
        );
    }

    @Test
    public void shouldReturnRawJsonWhenToStringMethodIsUsed() {
        final String raw = new FakeJsonPull().toString();
        MatcherAssert.assertThat(
            new BaseJsonPull(raw).toString(),
            CoreMatchers.equalTo(raw)
        );
    }

}
