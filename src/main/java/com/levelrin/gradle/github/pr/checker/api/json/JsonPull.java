/*
 * Copyright (c) 2022 Rin (https://www.levelrin.com)
 *
 * This file has been created under the terms of the MIT License.
 * See the details at https://github.com/levelrin/gradle-github-pr-checker/blob/main/LICENSE
 */

package com.levelrin.gradle.github.pr.checker.api.json;

/**
 * It represents the JSON of a pull request.
 */
public interface JsonPull {

    /**
     * Parse and return the pull request number.
     * @return Pull request number.
     */
    int number();

    /**
     * Parse and return the object that represents the 'head' value.
     * @return The 'head' value.
     */
    JsonHead head();

    /**
     * Parse and return the object that represents the 'base' value.
     * @return The 'base' value.
     */
    JsonBase base();

}
