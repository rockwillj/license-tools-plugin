package com.cookpad.android.licensetools

import groovy.text.SimpleTemplateEngine
import groovy.transform.CompileStatic

import java.util.zip.ZipFile

@CompileStatic
public class Templates {

    static final SimpleTemplateEngine templateEngine = new SimpleTemplateEngine()

    public static String buildLicenseHtml(LibraryInfo library, File projectDir) {
        assertLicenseAndStatement(library)

        def templateFile = "template/licenses/${library.normalizedLicense}.html"
        return templateEngine.createTemplate(readResourceContent(templateFile, projectDir)).make([
                "library": library
        ])
    }

    public static void assertLicenseAndStatement(LibraryInfo library) {
        if (!library.license) {
            throw new NotEnoughInformationException(library)
        }
        if (!library.copyrightStatement) {
            throw new NotEnoughInformationException(library)
        }
    }

    public static String wrapWithLayout(CharSequence content, File projectDir, ApplicationInfo appInfo) {
        def templateFile = "template/layout.html"
        def templateCssFile = "template/layout.css"
        def templateHeaderFile = "template/header.html"
        def templateFooterFile = "template/footer.html"
        def cssContent = readResourceContent(templateCssFile, projectDir)
        def headerContent = templateEngine.createTemplate(readResourceContent(templateHeaderFile, projectDir)).make([
                "application": appInfo
        ]).toString()
        def footerContent = readResourceContent(templateFooterFile, projectDir)
        return templateEngine.createTemplate(readResourceContent(templateFile, projectDir)).make([
                "css": cssContent,
                "header": headerContent,
                "content": content,
                "footer": footerContent
        ])
    }

    static String readResourceContent(String filename, File projectDir) {
        def templateFileUrl
        def templateFile = new File(projectDir, filename)
        if (templateFile.exists()) {
            templateFileUrl = templateFile.toURI().toURL()
        } else {
            templateFileUrl = Templates.class.getClassLoader().getResource(filename)
            if (templateFileUrl == null) {
                throw new FileNotFoundException("File not found: $filename")
            }
            templateFileUrl = new URL(templateFileUrl.toString())
        }

        try {
            return templateFileUrl.openStream().getText("UTF-8")
        } catch (FileNotFoundException e) {
            // fallback to read JAR directly
            URI jarFile = (templateFileUrl.openConnection() as JarURLConnection).jarFileURL.toURI()
            ZipFile zip
            try {
                zip = new ZipFile(new File(jarFile))
            } catch (FileNotFoundException ex) {
                System.err.println("[plugin] no plugin.jar. run `./gradlew plugin:jar` first.")
                throw ex
            }
            return zip.getInputStream((zip.getEntry(filename))).getText("UTF-8")
        }
    }
}
