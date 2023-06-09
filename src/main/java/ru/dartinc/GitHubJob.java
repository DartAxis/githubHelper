package ru.dartinc;

import org.kohsuke.github.GHMyself;
import org.kohsuke.github.GHPullRequest;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;


import java.awt.*;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class GitHubJob {
    private final GitHub github;
    private final Gui gui = new Gui();
    private final Set<Long> allPrsIds = new HashSet<>();

    public GitHubJob() throws AWTException {
        try {
            github = new GitHubBuilder()
                    .withAppInstallationToken(System.getenv("GITHUB_TOKEN"))
                    .build();
            init();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void init() throws IOException {
        System.out.println("Получаем майселф");
        GHMyself myself = github.getMyself();
        System.out.println("Получили майселф");
        String login = myself.getLogin();
        System.out.println(login);
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    HashSet<GHPullRequest> newPrs = new HashSet<>();
                    List<RepositoryDescription> repos = myself.getAllRepositories()
                            .values()
                            .stream()
                            .map(repository -> {
                                try {

                                    List<GHPullRequest> prs = repository.queryPullRequests().list().toList();
                                    Set<Long> prsIDs = prs.stream().map(GHPullRequest::getId).collect(Collectors.toSet());
                                    prsIDs.removeAll(allPrsIds);
                                    allPrsIds.addAll(prsIDs);

                                    prs.forEach(pr -> {
                                        if (prsIDs.contains(pr.getId())) {
                                            newPrs.add(pr);
                                        }
                                    });
                                    return new RepositoryDescription(repository.getFullName(), repository, prs);
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            })
                            .collect(Collectors.toList());
                    gui.setMenu(login,repos);
                    if (!allPrsIds.isEmpty()) {
                        newPrs.forEach(pr -> {
                            gui.showNotification("New PR in " + pr.getRepository().getFullName(), pr.getTitle());
                        });
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }, 1000, 300000);
    }
}
