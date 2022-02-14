/*
 * Copyright (c) 2022 Rin (https://www.levelrin.com)
 *
 * This file has been created under the terms of the MIT License.
 * See the details at https://github.com/levelrin/gradle-github-pr-checker/blob/main/LICENSE
 */

package com.levelrin.gradle.github.pr.checker.api.json;

import com.jayway.jsonpath.JsonPath;

/**
 * It represents the 'head' of a pull request in JSON.
 */
public final class JsonHead {

    /**
     * The 'head' of a pull request in JSON.
     */
    private final String raw;

    /**
     * Constructor.
     * @param raw See {@link JsonHead#raw}.
     */
    public JsonHead(final String raw) {
        this.raw = raw;
    }

    /**
     * Parse the JSON and return the sha.
     * @return The sha.
     */
    public String sha() {
        return JsonPath.read(this.raw, "$.sha");
    }

}
