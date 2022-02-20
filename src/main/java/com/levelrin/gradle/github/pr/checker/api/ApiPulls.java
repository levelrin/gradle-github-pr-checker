/*
 * Copyright (c) 2022 Rin (https://www.levelrin.com)
 *
 * This file has been created under the terms of the MIT License.
 * See the details at https://github.com/levelrin/gradle-github-pr-checker/blob/main/LICENSE
 */

package com.levelrin.gradle.github.pr.checker.api;

import com.levelrin.gradle.github.pr.checker.api.json.BaseJsonPull;
import java.util.List;

/**
 * It's responsible for getting the list of pull requests.
 * https://docs.github.com/en/rest/reference/pulls#list-pull-requests
 */
public interface ApiPulls {

    /**
     * Call the API and return the list of pull requests.
     * It will fetch all the pull requests even if there are multiple pages.
     * @return List of JSON objects.
     */
    List<BaseJsonPull> list();

}
