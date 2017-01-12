package com.cookpad.android.licensetools

public class ApplicationInfo {

    String name = ""

    String year = "";

    String copyrightHolder = "";

    String url = "";

    public static ApplicationInfo fromProperty(Object app) {
        def applicationInfo = new ApplicationInfo()
        applicationInfo.name = app.name as String
        applicationInfo.year = app.year as String
        applicationInfo.copyrightHolder = app.copyrightHolder
        applicationInfo.url = app.url as String
        return applicationInfo
    }

    public String getCopyrightHolder() {
        return copyrightHolder.replaceFirst(/\.$/, "")
    }

}
