/*
 * Copyright (c) 2022 Rin (https://www.levelrin.com)
 *
 * This file has been created under the terms of the MIT License.
 * See the details at https://github.com/levelrin/gradle-github-pr-checker/blob/main/LICENSE
 */

package com.levelrin.gradle.github.pr.checker.task;

import com.jayway.jsonpath.JsonPath;
import com.levelrin.gradle.github.pr.checker.GitHubPrExtension;
import com.levelrin.gradle.github.pr.checker.api.json.BaseJsonPull;
import com.levelrin.gradle.github.pr.checker.api.json.JsonPull;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.PersonIdent;
import org.gradle.api.DefaultTask;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.TaskAction;

/**
 * It checks the 'pr.json' and find the commit messages in the pull request.
 * And then, it generates the file named 'commit-list.json' in the output directory.
 *
 * The content of 'commit-list.json' looks like this:
 * [
 *    {
 *       "sha":"749eae561020c740bda087d13422687747df7f5e",
 *       "author":{
 *          "name":"Rin",
 *          "email":"levelrin@gmail.com"
 *       },
 *       "message":"Merge pull request #2 from levelrin/1_raw_pr\n\nCreate a Gradle task to generate a file that contains the pull reques\u2026"
 *    },
 *    {
 *       "sha":"8c6c50470a86e728e86f2e45fc53d91823b8698a",
 *       "author":{
 *          "name":"Rin",
 *          "email":"levelrin@gmail.com"
 *       },
 *       "message":"Create a Gradle task to generate a file that contains the pull request information. fixes #1\n"
 *    }
 * ]
 *
 * This task depends on the 'generateRawPrInfo' task.
 */
public abstract class GenerateCommitList extends DefaultTask {

    /**
     * It reads the 'pr.json' in the {@link GenerateCommitList#getOutputDir()}.
     * And then, it returns the content of the file as a JSON object.
     */
    private Supplier<JsonPull> readPr;

    /**
     * It returns the list of map that represents the list of commits.
     * Each map contains the information of each commit.
     * The map contains the following key-values:
     * "sha": the commit SHA.
     * "authorName": the author name.
     * "authorEmail": the author email.
     * "message": the full commit message.
     *
     * The parameter is the JSON object of the pull request.
     */
    private Function<JsonPull, List<Map<String, String>>> obtainCommits;

    /**
     * It takes the content of commits as a parameter.
     * And then, it writes the content to the file named 'commit-list.json'
     * in the {@link GenerateCommitList#getOutputDir()}.
     */
    private Consumer<String> generateFile;

    /**
     * It's an off-brand secondary constructor.
     * @param projectDir The project root directory.
     */
    @SuppressWarnings("RegexpSingleline")
    public void constructor(final String projectDir) {
        this.constructor(
            () -> {
                try {
                    return new BaseJsonPull(
                        Files.readString(
                            Paths.get(
                                URI.create(
                                    String.format(
                                        "file://%s/pr.json",
                                        this.getOutputDir().toString()
                                    )
                                )
                            ),
                            StandardCharsets.UTF_8
                        )
                    );
                } catch (final IOException exception) {
                    throw new IllegalStateException(
                        "Failed to read the 'pr.json' file.",
                        exception
                    );
                }
            },
            pullRequest -> {
                try {
                    final List<Map<String, String>> commits = new ArrayList<>();
                    Git.open(
                        new File(projectDir)
                    ).log().addRange(
                        ObjectId.fromString(pullRequest.base().sha()),
                        ObjectId.fromString(pullRequest.head().sha())
                    ).call().forEach(commit -> {
                        final Map<String, String> map = new HashMap<>();
                        final PersonIdent author = commit.getAuthorIdent();
                        map.put("sha", commit.getName());
                        map.put("authorName", author.getName());
                        map.put("authorEmail", author.getEmailAddress());
                        map.put("message", commit.getFullMessage());
                        commits.add(map);
                    });
                    return commits;
                } catch (final GitAPIException | IOException exception) {
                    throw new IllegalStateException(
                        String.format(
                            """
                            Failed to get the commits in the pull request.
                            BASE SHA: %s
                            HEAD SHA: %s
                            """,
                            pullRequest.base().sha(),
                            pullRequest.head().sha()
                        ),
                        exception
                    );
                }
            },
            content -> {
                try {
                    Files.writeString(
                        Paths.get(
                            URI.create(
                                String.format(
                                    "file://%s/commit-list.json",
                                    this.getOutputDir().get()
                                )
                            )
                        ),
                        content,
                        StandardCharsets.UTF_8
                    );
                } catch (final IOException exception) {
                    throw new IllegalStateException(
                        "Failed to write the 'commit-list.json' file.",
                        exception
                    );
                }
            }
        );
    }

    /**
     * It's an off-brand primary constructor.
     * @param readPr See {@link GenerateCommitList#readPr}.
     * @param obtainCommits See {@link GenerateCommitList#obtainCommits}.
     * @param generateFile See {@link GenerateCommitList#generateFile}.
     */
    @SuppressWarnings("HiddenField")
    public void constructor(final Supplier<JsonPull> readPr, final Function<JsonPull, List<Map<String, String>>> obtainCommits, final Consumer<String> generateFile) {
        this.readPr = readPr;
        this.obtainCommits = obtainCommits;
        this.generateFile = generateFile;
    }

    /**
     * See {@link GitHubPrExtension#getOutputDir()}.
     * It's also the input directory because we will check the 'pr.json' file in this directory.
     * We will generate the file named 'commit-list.json' in this directory.
     * @return Output directory.
     */
    @OutputDirectory
    public abstract DirectoryProperty getOutputDir();

    /**
     * Execute the task.
     * It will do the following:
     * 1. Check the 'pr.json' file in the output directory.
     * 2. Get the HEAD and BASE SHA from the 'pr.json' file.
     * 3. Get the commit messages between the HEAD and BASE SHA
     *    (commit messages in the pull request).
     * 4. Generate the file named 'commit-list.json' in the output directory.
     */
    @TaskAction
    public void run() {
        final JsonPull pullRequest = this.readPr.get();
        final StringJoiner commitJoiner = new StringJoiner(",");
        this.obtainCommits.apply(pullRequest).forEach(commit -> commitJoiner.add(
            String.format(
                """
                {
                    "sha": "%s",
                    "author": {
                        "name": "%s",
                        "email": "%s",
                    },
                    "message": "%s"
                }
                """,
                commit.get("sha"),
                commit.get("authorName"),
                commit.get("authorEmail"),
                commit.get("message")
            )
        ));
        this.generateFile.accept(
            JsonPath.parse(
                String.format(
                    "[%s]",
                    commitJoiner
                )
            ).jsonString()
        );
    }

}
