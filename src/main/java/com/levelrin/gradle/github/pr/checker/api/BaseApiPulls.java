/*
 * Copyright (c) 2022 Rin (https://www.levelrin.com)
 *
 * This file has been created under the terms of the MIT License.
 * See the details at https://github.com/levelrin/gradle-github-pr-checker/blob/main/LICENSE
 */

package com.levelrin.gradle.github.pr.checker.api;

import com.jayway.jsonpath.JsonPath;
import com.levelrin.gradle.github.pr.checker.GitHubPrExtension;
import com.levelrin.gradle.github.pr.checker.api.json.JsonPull;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import net.minidev.json.JSONArray;

/**
 * A base implementation of {@link ApiPulls}.
 */
public final class BaseApiPulls implements ApiPulls {

    /**
     * It's for calling the API.
     */
    private final HttpClient http;

    /**
     * It contains the necessary information to call the API.
     */
    private final GitHubPrExtension apiParams;

    /**
     * Constructor.
     * @param http See {@link BaseApiPulls#http}.
     * @param apiParams See {@link BaseApiPulls#apiParams}.
     */
    public BaseApiPulls(final HttpClient http, final GitHubPrExtension apiParams) {
        this.http = http;
        this.apiParams = apiParams;
    }

    @Override
    public List<JsonPull> list() {
        final List<JsonPull> result = new ArrayList<>();
        final int max = 100;
        int page = 1;
        List<JsonPull> current;
        do {
            current = this.list(page);
            result.addAll(current);
            page = page + 1;
        } while (current.size() == max);
        return result;
    }

    /**
     * Call the API and return the list of pull requests of the specified page.
     * @param page The page number.
     * @return List of pull requests.
     */
    private List<JsonPull> list(final int page) {
        try {
            final HttpResponse<String> response = this.http.send(
                HttpRequest
                    .newBuilder(
                        URI.create(
                            String.format(
                                "%s/repos/%s/%s/pulls?per_page=100&page=%d",
                                this.apiParams.getDomain().get(),
                                this.apiParams.getOwner().get(),
                                this.apiParams.getRepo().get(),
                                page
                            )
                        )
                    )
                    .GET()
                    .header("Accept", "application/vnd.github.v3+json")
                    .header(
                        "Authorization",
                        String.format(
                            "token %s",
                            this.apiParams.getToken().get()
                        )
                    )
                    .build(),
                HttpResponse.BodyHandlers.ofString()
            );
            final JSONArray pulls = JsonPath.read(response.body(), "$.[*]");
            final List<JsonPull> result = new ArrayList<>();
            for (final Object pull : pulls) {
                result.add(
                    new JsonPull(
                        JsonPath.parse(pull).jsonString()
                    )
                );
            }
            return result;
        } catch (final IOException | InterruptedException exception) {
            throw new IllegalStateException(
                "Failed to get the list of pull requests via API",
                exception
            );
        }
    }

}
