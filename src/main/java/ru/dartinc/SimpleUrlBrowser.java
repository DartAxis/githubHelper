package ru.dartinc;

import java.io.IOException;

public class SimpleUrlBrowser {

    private static final String[] browsers = {"chromium-browser","yandex-browser","google-chrome", "firefox", "mozilla", "epiphany",
            "konqueror", "netscape", "opera", "links",   "brave-browser"};

    public void browse(String url) {
        System.out.println("Please open the following address in your browser: ");
        System.out.println(url);
        try {
            if (isMacOperatingSystem()) {
                openUrlInDefaultMacOsBrowser(url);
            } else if (isWindowsOperatingSystem()) {
                openUrlInDefaultWindowsBrowser(url);
            } else {
                openUrlInDefaultUnixBrowser(url);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isMacOperatingSystem() {
        return getOperatingSystemName().startsWith("Mac OS");
    }

    private boolean isWindowsOperatingSystem() {
        return getOperatingSystemName().startsWith("Windows");
    }

    private String getOperatingSystemName() {
        return System.getProperty("os.name");
    }

    private void openUrlInDefaultMacOsBrowser(String url) throws IOException {
        System.out.println("Attempting to open that address in the default browser now...");
        Runtime.getRuntime().exec(String.format("open %s", url));
    }

    private void openUrlInDefaultWindowsBrowser(String url) throws IOException {
        System.out.println("Attempting to open that address in the default browser now...");
        Runtime.getRuntime().exec(String.format("rundll32 url.dll,FileProtocolHandler %s", url));
    }

    private void openUrlInDefaultUnixBrowser(String url) throws Exception {
        String browser = null;
        for (String b : browsers) {
            if (browser == null && Runtime.getRuntime().exec(new String[]{"which", b}).getInputStream().read() != -1) {
                System.out.println("Attempting to open that address in the default browser now...");
                Runtime.getRuntime().exec(new String[]{browser = b, url});
            }
        }
        if (browser == null) {
            throw new Exception("No web browser found");
        }
    }
}