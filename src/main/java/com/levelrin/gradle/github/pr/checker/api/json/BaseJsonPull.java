/*
 * Copyright (c) 2022 Rin (https://www.levelrin.com)
 *
 * This file has been created under the terms of the MIT License.
 * See the details at https://github.com/levelrin/gradle-github-pr-checker/blob/main/LICENSE
 */

package com.levelrin.gradle.github.pr.checker.api.json;

import com.jayway.jsonpath.JsonPath;
import java.util.Map;

/**
 * It represents the JSON of a pull request.
 */
public final class BaseJsonPull implements JsonPull {

    /**
     * A pull request information from GitHub.
     */
    private final String raw;

    /**
     * Constructor.
     * @param raw See {@link BaseJsonPull#raw}.
     */
    public BaseJsonPull(final String raw) {
        this.raw = raw;
    }

    /**
     * Parse and return the pull request number.
     * @return Pull request number.
     */
    @Override
    public int number() {
        return JsonPath.read(this.raw, "$.number");
    }

    /**
     * Parse and return the object that represents the 'head' value.
     * @return The 'head' value.
     */
    @Override
    public JsonHead head() {
        final Map<String, Object> json = JsonPath.read(this.raw, "$.head");
        return new JsonHead(
            JsonPath.parse(json).jsonString()
        );
    }

    @Override
    public JsonBase base() {
        final Map<String, Object> json = JsonPath.read(this.raw, "$.base");
        return new JsonBase(
            JsonPath.parse(json).jsonString()
        );
    }

    @Override
    public String toString() {
        return this.raw;
    }

}
