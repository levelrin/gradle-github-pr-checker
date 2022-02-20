/*
 * Copyright (c) 2022 Rin (https://www.levelrin.com)
 *
 * This file has been created under the terms of the MIT License.
 * See the details at https://github.com/levelrin/gradle-github-pr-checker/blob/main/LICENSE
 */

package com.levelrin.gradle.github.pr.checker.task;

import com.jayway.jsonpath.JsonPath;
import com.levelrin.gradle.github.pr.checker.fake.api.json.FakeJsonPull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;

/**
 * The test of {@link GenerateCommitList}.
 */
final class GenerateCommitListTest {

    @Test
    public void shouldGetCommitListAndWriteToFile() {
        final AtomicReference<String> actualContent = new AtomicReference<>();
        final Project project = ProjectBuilder.builder().build();
        project.getTasks().register("generateCommitList", GenerateCommitList.class, task -> {
            task.constructor(
                FakeJsonPull::new,
                pullRequest -> {
                    final List<Map<String, String>> commits = new ArrayList<>();
                    final Map<String, String> commit = new HashMap<>();
                    commit.put("sha", "12345");
                    commit.put("authorName", "Rin");
                    commit.put("authorEmail", "levelrin@gmail.com");
                    commit.put("message", "commit message");
                    commits.add(commit);
                    return commits;
                },
                actualContent::set
            );
        });
        final GenerateCommitList task = (GenerateCommitList) project.getTasks().getByName(
            "generateCommitList"
        );
        task.run();
        MatcherAssert.assertThat(
            JsonPath.parse(
                actualContent.get()
            ).json(),
            CoreMatchers.equalTo(
                JsonPath.parse(
                    """
                    [
                       {
                          "sha":"12345",
                          "author":{
                             "name":"Rin",
                             "email":"levelrin@gmail.com"
                          },
                          "message":"commit message"
                       }
                    ]
                    """
                ).json()
            )
        );
    }

}
