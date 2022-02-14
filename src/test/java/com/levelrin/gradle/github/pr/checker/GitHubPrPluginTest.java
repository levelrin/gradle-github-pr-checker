/*
 * Copyright (c) 2022 Rin (https://www.levelrin.com)
 *
 * This file has been created under the terms of the MIT License.
 * See the details at https://github.com/levelrin/gradle-github-pr-checker/blob/main/LICENSE
 */

package com.levelrin.gradle.github.pr.checker;

import com.levelrin.gradle.github.pr.checker.fake.FakeGitHubPrExtension;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.testfixtures.ProjectBuilder;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;

/**
 * The test of {@link GitHubPrPlugin}.
 */
final class GitHubPrPluginTest {

    @Test
    public void generateRawPrInfoTaskShouldBeRegistered() {
        final Project project = ProjectBuilder.builder().build();
        new GitHubPrPlugin().apply(project);
        final GitHubPrExtension extension = project
            .getExtensions()
            .findByType(GitHubPrExtension.class);
        final FakeGitHubPrExtension fakeExtension = new FakeGitHubPrExtension();
        assert extension != null;
        extension.getDomain().set(fakeExtension.getDomain().get());
        extension.getOwner().set(fakeExtension.getOwner().get());
        extension.getRepo().set(fakeExtension.getRepo().get());
        extension.getToken().set(fakeExtension.getToken().get());
        extension.getOutputDir().set(fakeExtension.getOutputDir().get());
        final Task task = project.getTasks().getByName("generateRawPrInfo");
        MatcherAssert.assertThat(
            task.getGroup(),
            CoreMatchers.equalTo("github pr")
        );
    }

}
