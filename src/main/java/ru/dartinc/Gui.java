package ru.dartinc;

import java.awt.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

public class Gui {

    private final TrayIcon trayIcon;
    private final SimpleUrlBrowser browser=new SimpleUrlBrowser();

    public Gui() {
        try {
            SystemTray tray = SystemTray.getSystemTray();
            Image image = Toolkit.getDefaultToolkit()
                    .createImage(getClass().getResource("/github-logo.png"));
            trayIcon = new TrayIcon(image, "GitHub helper");
            trayIcon.setImageAutoSize(true);
            trayIcon.setToolTip("GitHub helper");

            tray.add(trayIcon);
        } catch (AWTException e) {
            throw new RuntimeException(e);
        }
    }

    public void setMenu(String login, List<RepositoryDescription> repos) {
        PopupMenu popupMenu = new PopupMenu();

        MenuItem accountMI = new MenuItem(login);
        accountMI.addActionListener(e -> openInBrowser("https://github.com/" + login));
        popupMenu.add(accountMI);
        popupMenu.addSeparator();

        MenuItem notificationMI = new MenuItem("Notifications");
        accountMI.addActionListener(e -> openInBrowser("https://github.com/notifications"));
        popupMenu.add(notificationMI);
        popupMenu.addSeparator();

        Menu repositoriesMI = new Menu("Repositories");

        repos
                .forEach(repo -> {
                    String name = repo.getPrs().size() > 0 ?
                            String.format("(%2d)%s", repo.getPrs().size(), repo.getName()) : repo.getName();
                    Menu repoSM = new Menu(name);
                    MenuItem openInBrowserMI = new MenuItem("Open in Browser");
                    openInBrowserMI.addActionListener(x->{
                        openInBrowser(repo.getRepository().getHtmlUrl().toString());
                    });

                    if(repo.getPrs().size()>0){
                        repoSM.addSeparator();
                    }

                    repo.getPrs().forEach(pr ->{
                        MenuItem prMI = new MenuItem(pr.getTitle());
                        prMI.addActionListener(x->{
                            openInBrowser(pr.getHtmlUrl().toString());
                        });
                        repoSM.add(prMI);
                    });

                    repoSM.add(openInBrowserMI);
                    repositoriesMI.add(repoSM);
                });
        popupMenu.add(repositoriesMI);
        trayIcon.setPopupMenu(popupMenu);
    }

    public void openInBrowser(String url) {
//            Desktop.getDesktop().browse(new URL(url).toURI());
        browser.browse(url);
    }

    public void showNotification(String title, String text) {
        trayIcon.displayMessage(title, text, TrayIcon.MessageType.INFO);
        System.out.println(title);
        System.out.println(text);

    }
}
