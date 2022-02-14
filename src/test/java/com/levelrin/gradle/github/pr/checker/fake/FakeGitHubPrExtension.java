/*
 * Copyright (c) 2022 Rin (https://www.levelrin.com)
 *
 * This file has been created under the terms of the MIT License.
 * See the details at https://github.com/levelrin/gradle-github-pr-checker/blob/main/LICENSE
 */

package com.levelrin.gradle.github.pr.checker.fake;

import com.levelrin.gradle.github.pr.checker.GitHubPrExtension;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.provider.Property;
import org.mockito.Mockito;

/**
 * It contains predefined configuration values for testing.
 */
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public final class FakeGitHubPrExtension extends GitHubPrExtension {

    @Override
    @SuppressWarnings("unchecked")
    public Property<String> getDomain() {
        final Property<String> property = Mockito.mock(Property.class);
        Mockito.doReturn("https://api.github.com").when(property).get();
        return property;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Property<String> getOwner() {
        final Property<String> property = Mockito.mock(Property.class);
        Mockito.doReturn("levelrin").when(property).get();
        return property;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Property<String> getRepo() {
        final Property<String> property = Mockito.mock(Property.class);
        Mockito.doReturn("gradle-github-pr-checker").when(property).get();
        return property;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Property<String> getToken() {
        final Property<String> property = Mockito.mock(Property.class);
        Mockito.doReturn("token").when(property).get();
        return property;
    }

    @Override
    public DirectoryProperty getOutputDir() {
        return Mockito.mock(DirectoryProperty.class);
    }

}
