/*
 * Copyright (c) 2022 Rin (https://www.levelrin.com)
 *
 * This file has been created under the terms of the MIT License.
 * See the details at https://github.com/levelrin/gradle-github-pr-checker/blob/main/LICENSE
 */

package com.levelrin.gradle.github.pr.checker.task;

import com.levelrin.gradle.github.pr.checker.GitHubPrExtension;
import com.levelrin.gradle.github.pr.checker.api.ApiPulls;
import com.levelrin.gradle.github.pr.checker.api.json.JsonPull;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import org.gradle.api.Action;
import org.gradle.api.DefaultTask;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.TaskAction;

/**
 * It will obtain the information of the pull request.
 * The information will be recorded at the output directory.
 * The file name will be raw.json.
 */
public abstract class GenerateRawPrInfo extends DefaultTask {

    /**
     * It's for getting the list of pull requests from GitHub.
     * Unfortunately, we cannot pass this object via constructor
     * because {@link org.gradle.api.tasks.TaskContainer#register(String, Action)}
     * doesn't allow us to use constructor to instantiate this object.
     * We will set the value via a setter method.
     */
    private ApiPulls pulls;

    /**
     * It's for getting the local sha of HEAD in git.
     * We will find the pull request that match the HEAD sha.
     */
    private Supplier<String> headSha;

    /**
     * It takes a raw JSON of the pull request as a parameter.
     * We will create a file or overwrite the existing one with the parameter as the content.
     * We will put the file under the {@link GenerateRawPrInfo#getOutputDir()}.
     * The file name will be 'pr.json'.
     */
    private Consumer<String> generateFile;

    /**
     * It's an off-brand constructor because we cannot use the real one.
     * See the reason at {@link GenerateRawPrInfo#pulls}.
     * It's a primary constructor.
     * @param pulls See {@link GenerateRawPrInfo#pulls}.
     * @param headSha See {@link GenerateRawPrInfo#headSha}.
     * @param generateFile See {@link GenerateRawPrInfo#generateFile}.
     */
    @SuppressWarnings("HiddenField")
    public void constructor(final ApiPulls pulls, final Supplier<String> headSha, final Consumer<String> generateFile) {
        this.pulls = pulls;
        this.headSha = headSha;
        this.generateFile = generateFile;
    }

    /**
     * Secondary off-brand constructor.
     * @param pulls See {@link GenerateRawPrInfo#pulls}.
     * @param headSha See {@link GenerateRawPrInfo#headSha}.
     */
    @SuppressWarnings("HiddenField")
    public void constructor(final ApiPulls pulls, final Supplier<String> headSha) {
        this.constructor(pulls, headSha, content -> {
            try {
                Files.writeString(
                    Paths.get(
                        URI.create(
                            String.format(
                                "file://%s/pr.json",
                                this.getOutputDir().get()
                            )
                        )
                    ),
                    content
                );
            } catch (final IOException exception) {
                throw new IllegalStateException(
                    "Failed to generate the 'pr.json' file.",
                    exception
                );
            }
        });
    }

    /**
     * See {@link GitHubPrExtension#getDomain()}.
     * We don't really use this method in a meaningful way in this class.
     * However, we still have this method for the incremental tasks.
     * @return API domain.
     */
    @Input
    public abstract Property<String> getDomain();

    /**
     * See {@link GitHubPrExtension#getOwner()}.
     * @return Repository owner.
     */
    @Input
    public abstract Property<String> getOwner();

    /**
     * See {@link GitHubPrExtension#getRepo()}.
     * @return Repository name.
     */
    @Input
    public abstract Property<String> getRepo();

    /**
     * See {@link GitHubPrExtension#getToken()}.
     * @return GitHub access token.
     */
    @Input
    public abstract Property<String> getToken();

    /**
     * See {@link GitHubPrExtension#getOutputDir()}.
     * @return Output directory.
     */
    @OutputDirectory
    public abstract DirectoryProperty getOutputDir();

    /**
     * Execute the task.
     * It will do the following:
     * 1. Get the list of pull requests from GitHub.
     * 2. Find the pull request that match the HEAD sha.
     * 3. Generate the 'pr.json' file.
     */
    @TaskAction
    @SuppressWarnings("RegexpSingleline")
    public void run() {
        // We will store the matched pull request here.
        // The matched one represents the target pull request.
        final List<JsonPull> matched = new ArrayList<>(1);
        final String sha = this.headSha.get();
        for (final JsonPull pull : this.pulls.list()) {
            if (pull.head().sha().equals(sha)) {
                matched.add(pull);
                break;
            }
        }
        if (matched.isEmpty()) {
            throw new IllegalStateException(
                String.format(
                    """
                    We couldn't find the pull request that match the local HEAD SHA.
                    Domain: %s
                    Owner: %s
                    Repo: %s
                    Local HEAD SHA: %s
                    """,
                    this.getDomain().get(),
                    this.getOwner().get(),
                    this.getRepo().get(),
                    sha
                )
            );
        } else {
            this.generateFile.accept(
                matched.get(0).toString()
            );
        }
    }

}
