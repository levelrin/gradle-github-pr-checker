/*
 * Copyright (c) 2022 Rin (https://www.levelrin.com)
 *
 * This file has been created under the terms of the MIT License.
 * See the details at https://github.com/levelrin/gradle-github-pr-checker/blob/main/LICENSE
 */

package com.levelrin.gradle.github.pr.checker;

import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.provider.Property;

/**
 * It's for getting the user input from their build configuration.
 */
public abstract class GitHubPrExtension {

    /**
     * The domain of the API.
     * You may think it's unnecessary, but the user may use some off-brand GitHub such as GitBucket.
     * In such case, the domain of the API may be different from the GitHub API domain.
     * It should have the scheme.
     * For example, it should be 'https://api.github.com' instead of 'api.github.com'.
     * Note, it should not end with '/'.
     * @return The domain of the API.
     */
    public abstract Property<String> getDomain();

    /**
     * We need the repository owner to use the GitHub API.
     * The value is coming from the user's build configuration.
     * The 'get' prefix is required by the Gradle tool.
     * @return Repository owner.
     */
    public abstract Property<String> getOwner();

    /**
     * Similar to {@link GitHubPrExtension#getOwner()}.
     * @return Repository name.
     */
    public abstract Property<String> getRepo();

    /**
     * Similar to {@link GitHubPrExtension#getOwner()}.
     * It's an authorization token for calling the GitHub API.
     * Consumers of the plugin are recommended to use environment variable to set this value.
     * @return GitHub access token.
     */
    public abstract Property<String> getToken();

    /**
     * We will generate the PR information in this directory.
     * @return Output directory.
     */
    public abstract DirectoryProperty getOutputDir();

}
