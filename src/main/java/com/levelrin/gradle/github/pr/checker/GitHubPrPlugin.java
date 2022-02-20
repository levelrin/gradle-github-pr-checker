/*
 * Copyright (c) 2022 Rin (https://www.levelrin.com)
 *
 * This file has been created under the terms of the MIT License.
 * See the details at https://github.com/levelrin/gradle-github-pr-checker/blob/main/LICENSE
 */

package com.levelrin.gradle.github.pr.checker;

import com.levelrin.gradle.github.pr.checker.api.BaseApiPulls;
import com.levelrin.gradle.github.pr.checker.task.GenerateCommitList;
import com.levelrin.gradle.github.pr.checker.task.GenerateRawPrInfo;
import java.io.IOException;
import java.net.http.HttpClient;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.jetbrains.annotations.NotNull;

/**
 * It's the entrypoint of the plugin.
 */
public final class GitHubPrPlugin implements Plugin<Project> {

    @Override
    @SuppressWarnings("ExecutableStatementCount")
    public void apply(final @NotNull Project project) {
        final GitHubPrExtension extension = project.getExtensions().create(
            "githubPr",
            GitHubPrExtension.class
        );
        final String githubGroup = "github pr";
        final String prTask = "generateRawPrInfo";
        project.getTasks().register(prTask, GenerateRawPrInfo.class, task -> {
            task.setGroup(githubGroup);
            task.constructor(
                new BaseApiPulls(
                    HttpClient.newHttpClient(),
                    extension
                ),
                () -> {
                    try {
                        return Git.open(
                            project.getRootDir()
                        ).log().call().iterator().next().getName();
                    } catch (final GitAPIException | IOException exception) {
                        throw new IllegalStateException(
                            "Failed to get the local HEAD sha.",
                            exception
                        );
                    }
                }
            );
            if (!extension.getDomain().isPresent()) {
                extension.getDomain().set("https://api.github.com");
            }
            task.getDomain().set(extension.getDomain().get());
            task.getOwner().set(extension.getOwner().get());
            task.getRepo().set(extension.getRepo().get());
            task.getToken().set(extension.getToken().get());
            task.getOutputDir().set(extension.getOutputDir());
        });
        project.getTasks().register("generateCommitList", GenerateCommitList.class, task -> {
            task.setGroup(githubGroup);
            task.dependsOn(prTask);
            task.constructor(project.getRootDir().toString());
            task.getOutputDir().set(extension.getOutputDir());
        });
    }

}
