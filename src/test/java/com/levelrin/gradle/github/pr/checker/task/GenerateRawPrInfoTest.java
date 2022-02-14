/*
 * Copyright (c) 2022 Rin (https://www.levelrin.com)
 *
 * This file has been created under the terms of the MIT License.
 * See the details at https://github.com/levelrin/gradle-github-pr-checker/blob/main/LICENSE
 */

package com.levelrin.gradle.github.pr.checker.task;

import com.jayway.jsonpath.JsonPath;
import com.levelrin.gradle.github.pr.checker.GitHubPrExtension;
import com.levelrin.gradle.github.pr.checker.fake.FakeGitHubPrExtension;
import com.levelrin.gradle.github.pr.checker.fake.api.FakeApiPulls;
import java.util.concurrent.atomic.AtomicReference;
import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;

/**
 * The test of {@link GenerateRawPrInfo}.
 */
final class GenerateRawPrInfoTest {

    @Test
    @SuppressWarnings({"LineLength", "PMD.ExcessiveMethodLength"})
    public void shouldFindPrThatMatchesLocalHeadShaAndWritePrContentToFile() {
        // It represents the file that contains the output of the task.
        // We will store the pull request JSON into this.
        final AtomicReference<String> prContent = new AtomicReference<>();
        final Project project = ProjectBuilder.builder().build();
        // Gradle doesn't allow direct instantiation of Task objects.
        // We have to go through the registration.
        project.getTasks().register("generateRawPrInfo", GenerateRawPrInfo.class, task -> {
            task.constructor(
                new FakeApiPulls(),
                () -> "f7c7f903d77554ea320a9759af3fbfbac84ecc94",
                prContent::set
            );
            final GitHubPrExtension extension = new FakeGitHubPrExtension();
            if (extension.getDomain().isPresent()) {
                task.getDomain().set(extension.getDomain().get());
            } else {
                task.getDomain().set("https://api.github.com");
            }
            task.getOwner().set(extension.getOwner().get());
            task.getRepo().set(extension.getRepo().get());
            task.getToken().set(extension.getToken().get());
            task.getOutputDir().set(extension.getOutputDir().get());
        });
        final GenerateRawPrInfo task = (GenerateRawPrInfo) project.getTasks().getByName("generateRawPrInfo");
        task.run();
        MatcherAssert.assertThat(
            JsonPath.parse(
                prContent.get()
            ).json(),
            CoreMatchers.equalTo(
                JsonPath.parse(
                    """
                        {
                          "url":"https://api.github.com/repos/flutter/flutter/pulls/98305",
                          "id":848506161,
                          "node_id":"PR_kwDOAeUeuM4yky0x",
                          "html_url":"https://github.com/flutter/flutter/pull/98305",
                          "diff_url":"https://github.com/flutter/flutter/pull/98305.diff",
                          "patch_url":"https://github.com/flutter/flutter/pull/98305.patch",
                          "issue_url":"https://api.github.com/repos/flutter/flutter/issues/98305",
                          "number":98305,
                          "state":"open",
                          "locked":false,
                          "title":"Dump app on keyboard_textfield_test timeout",
                          "user":{
                             "login":"LongCatIsLooong",
                             "id":31859944,
                             "node_id":"MDQ6VXNlcjMxODU5OTQ0",
                             "avatar_url":"https://avatars.githubusercontent.com/u/31859944?v=4",
                             "gravatar_id":"",
                             "url":"https://api.github.com/users/LongCatIsLooong",
                             "html_url":"https://github.com/LongCatIsLooong",
                             "followers_url":"https://api.github.com/users/LongCatIsLooong/followers",
                             "following_url":"https://api.github.com/users/LongCatIsLooong/following{/other_user}",
                             "gists_url":"https://api.github.com/users/LongCatIsLooong/gists{/gist_id}",
                             "starred_url":"https://api.github.com/users/LongCatIsLooong/starred{/owner}{/repo}",
                             "subscriptions_url":"https://api.github.com/users/LongCatIsLooong/subscriptions",
                             "organizations_url":"https://api.github.com/users/LongCatIsLooong/orgs",
                             "repos_url":"https://api.github.com/users/LongCatIsLooong/repos",
                             "events_url":"https://api.github.com/users/LongCatIsLooong/events{/privacy}",
                             "received_events_url":"https://api.github.com/users/LongCatIsLooong/received_events",
                             "type":"User",
                             "site_admin":false
                          },
                          "body":"Help investigate https://github.com/flutter/flutter/issues/96787\\r\\n\\r\\n## Pre-launch Checklist\\r\\n\\r\\n- [ ] I read the [Contributor Guide] and followed the process outlined there for submitting PRs.\\r\\n- [ ] I read the [Tree Hygiene] wiki page, which explains my responsibilities.\\r\\n- [ ] I read and followed the [Flutter Style Guide], including [Features we expect every widget to implement].\\r\\n- [ ] I signed the [CLA].\\r\\n- [ ] I listed at least one issue that this PR fixes in the description above.\\r\\n- [ ] I updated/added relevant documentation (doc comments with `///`).\\r\\n- [ ] I added new tests to check the change I am making, or this PR is [test-exempt].\\r\\n- [ ] All existing and new tests are passing.\\r\\n\\r\\nIf you need help, consider asking for advice on the #hackers-new channel on [Discord].\\r\\n\\r\\n<!-- Links -->\\r\\n[Contributor Guide]: https://github.com/flutter/flutter/wiki/Tree-hygiene#overview\\r\\n[Tree Hygiene]: https://github.com/flutter/flutter/wiki/Tree-hygiene\\r\\n[test-exempt]: https://github.com/flutter/flutter/wiki/Tree-hygiene#tests\\r\\n[Flutter Style Guide]: https://github.com/flutter/flutter/wiki/Style-guide-for-Flutter-repo\\r\\n[Features we expect every widget to implement]: https://github.com/flutter/flutter/wiki/Style-guide-for-Flutter-repo#features-we-expect-every-widget-to-implement\\r\\n[CLA]: https://cla.developers.google.com/\\r\\n[flutter/tests]: https://github.com/flutter/tests\\r\\n[breaking change policy]: https://github.com/flutter/flutter/wiki/Tree-hygiene#handling-breaking-changes\\r\\n[Discord]: https://github.com/flutter/flutter/wiki/Chat\\r\\n",
                          "created_at":"2022-02-12T02:31:36Z",
                          "updated_at":"2022-02-12T03:09:48Z",
                          "closed_at":null,
                          "merged_at":null,
                          "merge_commit_sha":"2a9d64c0f0e10b732ed06d197695dd502b9b431a",
                          "assignee":null,
                          "assignees":[
                            \s
                          ],
                          "requested_reviewers":[
                            \s
                          ],
                          "requested_teams":[
                            \s
                          ],
                          "labels":[
                             {
                                "id":283479178,
                                "node_id":"MDU6TGFiZWwyODM0NzkxNzg=",
                                "url":"https://api.github.com/repos/flutter/flutter/labels/a:%20text%20input",
                                "name":"a: text input",
                                "color":"38d8c8",
                                "default":false,
                                "description":"Entering text in a text field or keyboard related problems."
                             },
                             {
                                "id":283479307,
                                "node_id":"MDU6TGFiZWwyODM0NzkzMDc=",
                                "url":"https://api.github.com/repos/flutter/flutter/labels/team",
                                "name":"team",
                                "color":"d4c5f9",
                                "default":false,
                                "description":"Infra upgrades, team productivity, code health, technical debt. See also team: labels."
                             }
                          ],
                          "milestone":null,
                          "draft":false,
                          "commits_url":"https://api.github.com/repos/flutter/flutter/pulls/98305/commits",
                          "review_comments_url":"https://api.github.com/repos/flutter/flutter/pulls/98305/comments",
                          "review_comment_url":"https://api.github.com/repos/flutter/flutter/pulls/comments{/number}",
                          "comments_url":"https://api.github.com/repos/flutter/flutter/issues/98305/comments",
                          "statuses_url":"https://api.github.com/repos/flutter/flutter/statuses/f7c7f903d77554ea320a9759af3fbfbac84ecc94",
                          "head":{
                             "label":"LongCatIsLooong:keyboard-textfield-test-dump-on-fail",
                             "ref":"keyboard-textfield-test-dump-on-fail",
                             "sha":"f7c7f903d77554ea320a9759af3fbfbac84ecc94",
                             "user":{
                                "login":"LongCatIsLooong",
                                "id":31859944,
                                "node_id":"MDQ6VXNlcjMxODU5OTQ0",
                                "avatar_url":"https://avatars.githubusercontent.com/u/31859944?v=4",
                                "gravatar_id":"",
                                "url":"https://api.github.com/users/LongCatIsLooong",
                                "html_url":"https://github.com/LongCatIsLooong",
                                "followers_url":"https://api.github.com/users/LongCatIsLooong/followers",
                                "following_url":"https://api.github.com/users/LongCatIsLooong/following{/other_user}",
                                "gists_url":"https://api.github.com/users/LongCatIsLooong/gists{/gist_id}",
                                "starred_url":"https://api.github.com/users/LongCatIsLooong/starred{/owner}{/repo}",
                                "subscriptions_url":"https://api.github.com/users/LongCatIsLooong/subscriptions",
                                "organizations_url":"https://api.github.com/users/LongCatIsLooong/orgs",
                                "repos_url":"https://api.github.com/users/LongCatIsLooong/repos",
                                "events_url":"https://api.github.com/users/LongCatIsLooong/events{/privacy}",
                                "received_events_url":"https://api.github.com/users/LongCatIsLooong/received_events",
                                "type":"User",
                                "site_admin":false
                             },
                             "repo":{
                                "id":177637136,
                                "node_id":"MDEwOlJlcG9zaXRvcnkxNzc2MzcxMzY=",
                                "name":"flutter",
                                "full_name":"LongCatIsLooong/flutter",
                                "private":false,
                                "owner":{
                                   "login":"LongCatIsLooong",
                                   "id":31859944,
                                   "node_id":"MDQ6VXNlcjMxODU5OTQ0",
                                   "avatar_url":"https://avatars.githubusercontent.com/u/31859944?v=4",
                                   "gravatar_id":"",
                                   "url":"https://api.github.com/users/LongCatIsLooong",
                                   "html_url":"https://github.com/LongCatIsLooong",
                                   "followers_url":"https://api.github.com/users/LongCatIsLooong/followers",
                                   "following_url":"https://api.github.com/users/LongCatIsLooong/following{/other_user}",
                                   "gists_url":"https://api.github.com/users/LongCatIsLooong/gists{/gist_id}",
                                   "starred_url":"https://api.github.com/users/LongCatIsLooong/starred{/owner}{/repo}",
                                   "subscriptions_url":"https://api.github.com/users/LongCatIsLooong/subscriptions",
                                   "organizations_url":"https://api.github.com/users/LongCatIsLooong/orgs",
                                   "repos_url":"https://api.github.com/users/LongCatIsLooong/repos",
                                   "events_url":"https://api.github.com/users/LongCatIsLooong/events{/privacy}",
                                   "received_events_url":"https://api.github.com/users/LongCatIsLooong/received_events",
                                   "type":"User",
                                   "site_admin":false
                                },
                                "html_url":"https://github.com/LongCatIsLooong/flutter",
                                "description":"Flutter makes it easy and fast to build beautiful mobile apps.",
                                "fork":true,
                                "url":"https://api.github.com/repos/LongCatIsLooong/flutter",
                                "forks_url":"https://api.github.com/repos/LongCatIsLooong/flutter/forks",
                                "keys_url":"https://api.github.com/repos/LongCatIsLooong/flutter/keys{/key_id}",
                                "collaborators_url":"https://api.github.com/repos/LongCatIsLooong/flutter/collaborators{/collaborator}",
                                "teams_url":"https://api.github.com/repos/LongCatIsLooong/flutter/teams",
                                "hooks_url":"https://api.github.com/repos/LongCatIsLooong/flutter/hooks",
                                "issue_events_url":"https://api.github.com/repos/LongCatIsLooong/flutter/issues/events{/number}",
                                "events_url":"https://api.github.com/repos/LongCatIsLooong/flutter/events",
                                "assignees_url":"https://api.github.com/repos/LongCatIsLooong/flutter/assignees{/user}",
                                "branches_url":"https://api.github.com/repos/LongCatIsLooong/flutter/branches{/branch}",
                                "tags_url":"https://api.github.com/repos/LongCatIsLooong/flutter/tags",
                                "blobs_url":"https://api.github.com/repos/LongCatIsLooong/flutter/git/blobs{/sha}",
                                "git_tags_url":"https://api.github.com/repos/LongCatIsLooong/flutter/git/tags{/sha}",
                                "git_refs_url":"https://api.github.com/repos/LongCatIsLooong/flutter/git/refs{/sha}",
                                "trees_url":"https://api.github.com/repos/LongCatIsLooong/flutter/git/trees{/sha}",
                                "statuses_url":"https://api.github.com/repos/LongCatIsLooong/flutter/statuses/{sha}",
                                "languages_url":"https://api.github.com/repos/LongCatIsLooong/flutter/languages",
                                "stargazers_url":"https://api.github.com/repos/LongCatIsLooong/flutter/stargazers",
                                "contributors_url":"https://api.github.com/repos/LongCatIsLooong/flutter/contributors",
                                "subscribers_url":"https://api.github.com/repos/LongCatIsLooong/flutter/subscribers",
                                "subscription_url":"https://api.github.com/repos/LongCatIsLooong/flutter/subscription",
                                "commits_url":"https://api.github.com/repos/LongCatIsLooong/flutter/commits{/sha}",
                                "git_commits_url":"https://api.github.com/repos/LongCatIsLooong/flutter/git/commits{/sha}",
                                "comments_url":"https://api.github.com/repos/LongCatIsLooong/flutter/comments{/number}",
                                "issue_comment_url":"https://api.github.com/repos/LongCatIsLooong/flutter/issues/comments{/number}",
                                "contents_url":"https://api.github.com/repos/LongCatIsLooong/flutter/contents/{+path}",
                                "compare_url":"https://api.github.com/repos/LongCatIsLooong/flutter/compare/{base}...{head}",
                                "merges_url":"https://api.github.com/repos/LongCatIsLooong/flutter/merges",
                                "archive_url":"https://api.github.com/repos/LongCatIsLooong/flutter/{archive_format}{/ref}",
                                "downloads_url":"https://api.github.com/repos/LongCatIsLooong/flutter/downloads",
                                "issues_url":"https://api.github.com/repos/LongCatIsLooong/flutter/issues{/number}",
                                "pulls_url":"https://api.github.com/repos/LongCatIsLooong/flutter/pulls{/number}",
                                "milestones_url":"https://api.github.com/repos/LongCatIsLooong/flutter/milestones{/number}",
                                "notifications_url":"https://api.github.com/repos/LongCatIsLooong/flutter/notifications{?since,all,participating}",
                                "labels_url":"https://api.github.com/repos/LongCatIsLooong/flutter/labels{/name}",
                                "releases_url":"https://api.github.com/repos/LongCatIsLooong/flutter/releases{/id}",
                                "deployments_url":"https://api.github.com/repos/LongCatIsLooong/flutter/deployments",
                                "created_at":"2019-03-25T17:54:11Z",
                                "updated_at":"2021-07-23T15:20:33Z",
                                "pushed_at":"2022-02-12T02:13:09Z",
                                "git_url":"git://github.com/LongCatIsLooong/flutter.git",
                                "ssh_url":"git@github.com:LongCatIsLooong/flutter.git",
                                "clone_url":"https://github.com/LongCatIsLooong/flutter.git",
                                "svn_url":"https://github.com/LongCatIsLooong/flutter",
                                "homepage":"https://flutter.io",
                                "size":184456,
                                "stargazers_count":2,
                                "watchers_count":2,
                                "language":"Dart",
                                "has_issues":false,
                                "has_projects":true,
                                "has_downloads":true,
                                "has_wiki":true,
                                "has_pages":false,
                                "forks_count":0,
                                "mirror_url":null,
                                "archived":false,
                                "disabled":false,
                                "open_issues_count":0,
                                "license":{
                                   "key":"bsd-3-clause",
                                   "name":"BSD 3-Clause \\"New\\" or \\"Revised\\" License",
                                   "spdx_id":"BSD-3-Clause",
                                   "url":"https://api.github.com/licenses/bsd-3-clause",
                                   "node_id":"MDc6TGljZW5zZTU="
                                },
                                "allow_forking":true,
                                "is_template":false,
                                "topics":[
                                  \s
                                ],
                                "visibility":"public",
                                "forks":0,
                                "open_issues":0,
                                "watchers":2,
                                "default_branch":"master"
                             }
                          },
                          "base":{
                             "label":"flutter:master",
                             "ref":"master",
                             "sha":"0551117ea5c0bf9a0d563b8f3822891bf58c8ba6",
                             "user":{
                                "login":"flutter",
                                "id":14101776,
                                "node_id":"MDEyOk9yZ2FuaXphdGlvbjE0MTAxNzc2",
                                "avatar_url":"https://avatars.githubusercontent.com/u/14101776?v=4",
                                "gravatar_id":"",
                                "url":"https://api.github.com/users/flutter",
                                "html_url":"https://github.com/flutter",
                                "followers_url":"https://api.github.com/users/flutter/followers",
                                "following_url":"https://api.github.com/users/flutter/following{/other_user}",
                                "gists_url":"https://api.github.com/users/flutter/gists{/gist_id}",
                                "starred_url":"https://api.github.com/users/flutter/starred{/owner}{/repo}",
                                "subscriptions_url":"https://api.github.com/users/flutter/subscriptions",
                                "organizations_url":"https://api.github.com/users/flutter/orgs",
                                "repos_url":"https://api.github.com/users/flutter/repos",
                                "events_url":"https://api.github.com/users/flutter/events{/privacy}",
                                "received_events_url":"https://api.github.com/users/flutter/received_events",
                                "type":"Organization",
                                "site_admin":false
                             },
                             "repo":{
                                "id":31792824,
                                "node_id":"MDEwOlJlcG9zaXRvcnkzMTc5MjgyNA==",
                                "name":"flutter",
                                "full_name":"flutter/flutter",
                                "private":false,
                                "owner":{
                                   "login":"flutter",
                                   "id":14101776,
                                   "node_id":"MDEyOk9yZ2FuaXphdGlvbjE0MTAxNzc2",
                                   "avatar_url":"https://avatars.githubusercontent.com/u/14101776?v=4",
                                   "gravatar_id":"",
                                   "url":"https://api.github.com/users/flutter",
                                   "html_url":"https://github.com/flutter",
                                   "followers_url":"https://api.github.com/users/flutter/followers",
                                   "following_url":"https://api.github.com/users/flutter/following{/other_user}",
                                   "gists_url":"https://api.github.com/users/flutter/gists{/gist_id}",
                                   "starred_url":"https://api.github.com/users/flutter/starred{/owner}{/repo}",
                                   "subscriptions_url":"https://api.github.com/users/flutter/subscriptions",
                                   "organizations_url":"https://api.github.com/users/flutter/orgs",
                                   "repos_url":"https://api.github.com/users/flutter/repos",
                                   "events_url":"https://api.github.com/users/flutter/events{/privacy}",
                                   "received_events_url":"https://api.github.com/users/flutter/received_events",
                                   "type":"Organization",
                                   "site_admin":false
                                },
                                "html_url":"https://github.com/flutter/flutter",
                                "description":"Flutter makes it easy and fast to build beautiful apps for mobile and beyond",
                                "fork":false,
                                "url":"https://api.github.com/repos/flutter/flutter",
                                "forks_url":"https://api.github.com/repos/flutter/flutter/forks",
                                "keys_url":"https://api.github.com/repos/flutter/flutter/keys{/key_id}",
                                "collaborators_url":"https://api.github.com/repos/flutter/flutter/collaborators{/collaborator}",
                                "teams_url":"https://api.github.com/repos/flutter/flutter/teams",
                                "hooks_url":"https://api.github.com/repos/flutter/flutter/hooks",
                                "issue_events_url":"https://api.github.com/repos/flutter/flutter/issues/events{/number}",
                                "events_url":"https://api.github.com/repos/flutter/flutter/events",
                                "assignees_url":"https://api.github.com/repos/flutter/flutter/assignees{/user}",
                                "branches_url":"https://api.github.com/repos/flutter/flutter/branches{/branch}",
                                "tags_url":"https://api.github.com/repos/flutter/flutter/tags",
                                "blobs_url":"https://api.github.com/repos/flutter/flutter/git/blobs{/sha}",
                                "git_tags_url":"https://api.github.com/repos/flutter/flutter/git/tags{/sha}",
                                "git_refs_url":"https://api.github.com/repos/flutter/flutter/git/refs{/sha}",
                                "trees_url":"https://api.github.com/repos/flutter/flutter/git/trees{/sha}",
                                "statuses_url":"https://api.github.com/repos/flutter/flutter/statuses/{sha}",
                                "languages_url":"https://api.github.com/repos/flutter/flutter/languages",
                                "stargazers_url":"https://api.github.com/repos/flutter/flutter/stargazers",
                                "contributors_url":"https://api.github.com/repos/flutter/flutter/contributors",
                                "subscribers_url":"https://api.github.com/repos/flutter/flutter/subscribers",
                                "subscription_url":"https://api.github.com/repos/flutter/flutter/subscription",
                                "commits_url":"https://api.github.com/repos/flutter/flutter/commits{/sha}",
                                "git_commits_url":"https://api.github.com/repos/flutter/flutter/git/commits{/sha}",
                                "comments_url":"https://api.github.com/repos/flutter/flutter/comments{/number}",
                                "issue_comment_url":"https://api.github.com/repos/flutter/flutter/issues/comments{/number}",
                                "contents_url":"https://api.github.com/repos/flutter/flutter/contents/{+path}",
                                "compare_url":"https://api.github.com/repos/flutter/flutter/compare/{base}...{head}",
                                "merges_url":"https://api.github.com/repos/flutter/flutter/merges",
                                "archive_url":"https://api.github.com/repos/flutter/flutter/{archive_format}{/ref}",
                                "downloads_url":"https://api.github.com/repos/flutter/flutter/downloads",
                                "issues_url":"https://api.github.com/repos/flutter/flutter/issues{/number}",
                                "pulls_url":"https://api.github.com/repos/flutter/flutter/pulls{/number}",
                                "milestones_url":"https://api.github.com/repos/flutter/flutter/milestones{/number}",
                                "notifications_url":"https://api.github.com/repos/flutter/flutter/notifications{?since,all,participating}",
                                "labels_url":"https://api.github.com/repos/flutter/flutter/labels{/name}",
                                "releases_url":"https://api.github.com/repos/flutter/flutter/releases{/id}",
                                "deployments_url":"https://api.github.com/repos/flutter/flutter/deployments",
                                "created_at":"2015-03-06T22:54:58Z",
                                "updated_at":"2022-02-12T14:21:43Z",
                                "pushed_at":"2022-02-12T13:24:00Z",
                                "git_url":"git://github.com/flutter/flutter.git",
                                "ssh_url":"git@github.com:flutter/flutter.git",
                                "clone_url":"https://github.com/flutter/flutter.git",
                                "svn_url":"https://github.com/flutter/flutter",
                                "homepage":"https://flutter.dev",
                                "size":183893,
                                "stargazers_count":136262,
                                "watchers_count":136262,
                                "language":"Dart",
                                "has_issues":true,
                                "has_projects":true,
                                "has_downloads":true,
                                "has_wiki":true,
                                "has_pages":false,
                                "forks_count":20656,
                                "mirror_url":null,
                                "archived":false,
                                "disabled":false,
                                "open_issues_count":10327,
                                "license":{
                                   "key":"bsd-3-clause",
                                   "name":"BSD 3-Clause \\"New\\" or \\"Revised\\" License",
                                   "spdx_id":"BSD-3-Clause",
                                   "url":"https://api.github.com/licenses/bsd-3-clause",
                                   "node_id":"MDc6TGljZW5zZTU="
                                },
                                "allow_forking":true,
                                "is_template":false,
                                "topics":[
                                   "android",
                                   "app-framework",
                                   "dart",
                                   "dart-platform",
                                   "desktop",
                                   "fuchsia",
                                   "ios",
                                   "linux-desktop",
                                   "macos",
                                   "material-design",
                                   "mobile",
                                   "skia",
                                   "web",
                                   "web-framework",
                                   "windows"
                                ],
                                "visibility":"public",
                                "forks":20656,
                                "open_issues":10327,
                                "watchers":136262,
                                "default_branch":"master"
                             }
                          },
                          "_links":{
                             "self":{
                                "href":"https://api.github.com/repos/flutter/flutter/pulls/98305"
                             },
                             "html":{
                                "href":"https://github.com/flutter/flutter/pull/98305"
                             },
                             "issue":{
                                "href":"https://api.github.com/repos/flutter/flutter/issues/98305"
                             },
                             "comments":{
                                "href":"https://api.github.com/repos/flutter/flutter/issues/98305/comments"
                             },
                             "review_comments":{
                                "href":"https://api.github.com/repos/flutter/flutter/pulls/98305/comments"
                             },
                             "review_comment":{
                                "href":"https://api.github.com/repos/flutter/flutter/pulls/comments{/number}"
                             },
                             "commits":{
                                "href":"https://api.github.com/repos/flutter/flutter/pulls/98305/commits"
                             },
                             "statuses":{
                                "href":"https://api.github.com/repos/flutter/flutter/statuses/f7c7f903d77554ea320a9759af3fbfbac84ecc94"
                             }
                          },
                          "author_association":"CONTRIBUTOR",
                          "auto_merge":null,
                          "active_lock_reason":null
                       }
                    """
                ).json()
            )
        );
    }

}
