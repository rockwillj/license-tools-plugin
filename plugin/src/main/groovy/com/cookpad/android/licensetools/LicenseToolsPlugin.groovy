package com.cookpad.android.licensetools

import groovy.json.JsonBuilder
import groovy.util.slurpersupport.GPathResult
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.ResolvedArtifact
import org.xml.sax.helpers.DefaultHandler
import org.yaml.snakeyaml.Yaml

class LicenseToolsPlugin implements Plugin<Project> {

    final yaml = new Yaml()

    final DependencySet librariesYaml = new DependencySet() // based on libraries.yml
    final DependencySet dependencyLicenses = new DependencySet() // based on license plugin's dependency-license.xml

    @Override
    void apply(Project project) {
        project.extensions.add(LicenseToolsExtension.NAME, LicenseToolsExtension)

        def checkLicenses = project.task('checkLicenses').doLast {
            initialize(project)

            def notDocumented = dependencyLicenses.notListedIn(librariesYaml)
            def notInDependencies = librariesYaml.notListedIn(dependencyLicenses)
            def licensesNotMatched = dependencyLicenses.licensesNotMatched(librariesYaml)

            if (notDocumented.empty && notInDependencies.empty && licensesNotMatched.empty) {
                project.logger.info("checkLicenses: ok")
                return
            }

            LicenseToolsExtension ext = project.extensions.findByType(LicenseToolsExtension)

            if (notDocumented.size() > 0) {
                project.logger.warn("# Libraries not listed in ${ext.licensesYaml}:")
                notDocumented.each { libraryInfo ->
                    def message = new StringBuffer()
                    message.append("- artifact: ${libraryInfo.artifactId.withWildcardVersion()}\n")
                    message.append("  name: ${libraryInfo.escapedName ?: "(NAME)"}\n")
                    message.append("  copyrightHolder: ${libraryInfo.copyrightHolder ?: "(COPYRIGHT_HOLDER)"}\n")
                    message.append("  license: ${libraryInfo.license ?: "(LICENSE)"}\n")
                    if (libraryInfo.licenseUrl) {
                        message.append("  licenseUrl: ${libraryInfo.licenseUrl ?: "(LICENSEURL)"}\n")
                    }
                    if (libraryInfo.url) {
                        message.append("  url: ${libraryInfo.url ?: "(URL)"}\n")
                    }
                    project.logger.warn(message.toString().trim())

                    appendLicenseYaml(project, message.toString().trim())
                }
            }
            if (notInDependencies.size() > 0) {
                project.logger.warn("# Libraries listed in ${ext.licensesYaml} but not in dependencies:")
                notInDependencies.each { libraryInfo ->
                    project.logger.warn("- artifact: ${libraryInfo.artifactId}\n")
                }
            }
            if (licensesNotMatched.size() > 0) {
                project.logger.warn("# Licenses not matched with pom.xml in dependencies:")
                licensesNotMatched.each { libraryInfo ->
                    project.logger.warn("- artifact: ${libraryInfo.artifactId}\n  license: ${libraryInfo.license}")
                }
            }
            project.logger.error("checkLicenses: missing libraries in ${ext.licensesYaml}")
        }

        checkLicenses.configure {
            group = "Verification"
            description = 'Check whether dependency licenses are listed in licenses.yml'
        }

        def generateLicenseYaml = project.task('generateLicenseYaml').doLast {
            initialize(project)
            generateLicenseYaml(project)
        }
        generateLicenseYaml.dependsOn('checkLicenses')

        def generateLicensePage = project.task('generateLicensePage').doLast {
            initialize(project)
            generateLicensePage(project)
        }
        generateLicensePage.dependsOn('checkLicenses')

        def generateLicenseJson = project.task('generateLicenseJson').doLast {
            initialize(project)
            generateLicenseJson(project)
        }
        generateLicenseJson.dependsOn('checkLicenses')

        project.tasks.findByName("check").dependsOn('checkLicenses')
    }

    void initialize(Project project) {
        LicenseToolsExtension ext = project.extensions.findByType(LicenseToolsExtension)
        loadLibrariesYaml(project.file(ext.licensesYaml))
        loadDependencyLicenses(project)
    }

    void loadLibrariesYaml(File licensesYaml) {
        if (!licensesYaml.exists()) {
            return
        }

        def libraries = loadYaml(licensesYaml)
        for (lib in libraries) {
            def libraryInfo = LibraryInfo.fromYaml(lib)
            librariesYaml.add(libraryInfo)
        }
    }

    void loadDependencyLicenses(Project project) {
        resolveProjectDependencies(project).each { d ->
            if (d.moduleVersion.id.version == "unspecified") {
                return
            }

            def dependencyDesc = "$d.moduleVersion.id.group:$d.moduleVersion.id.name:$d.moduleVersion.id.version"

            def libraryInfo = new LibraryInfo()
            libraryInfo.artifactId = ArtifactId.parse(dependencyDesc)
            libraryInfo.filename = d.file
            dependencyLicenses.add(libraryInfo)

            Dependency pomDependency = project.dependencies.create("$dependencyDesc@pom")
            Configuration pomConfiguration = project.configurations.detachedConfiguration(pomDependency)

            pomConfiguration.resolve().each {
                project.logger.info("POM: ${it}")
            }

            File pStream
            try {
                pStream = pomConfiguration.resolve().asList().first()
            } catch (Exception e) {
                project.logger.warn("Unable to retrieve license for $dependencyDesc")
                return
            }

            XmlSlurper slurper = new XmlSlurper(true, false)
            slurper.setErrorHandler(new DefaultHandler())
            GPathResult xml = slurper.parse(pStream)

            libraryInfo.libraryName = xml.name.text()
            libraryInfo.url = xml.url.text()

            xml.licenses.license.each {
                if (!libraryInfo.license) {
                    // takes the first license
                    libraryInfo.license = it.name.text().trim()
                    libraryInfo.licenseUrl = it.url.text().trim()
                }
            }
        }
    }

    Map<String, ?> loadYaml(File yamlFile) {
        return yaml.load(yamlFile.text) as Map<String, ?> ?: [:]
    }

    void generateLicenseYaml(Project project) {
        def ext = project.extensions.getByType(LicenseToolsExtension)

        def assetsDir = project.file("src/main/assets")
        if (!assetsDir.exists()) {
            assetsDir.mkdirs()
        }

        project.logger.info("render ${assetsDir}/${ext.outputYaml}")
        project.file("${assetsDir}/${ext.outputYaml}").write(project.file(ext.licensesYaml).text)
    }

    void appendLicenseYaml(Project project, String content) {
        def ext = project.extensions.getByType(LicenseToolsExtension)

        project.file(ext.licensesYaml).append("\n${content}")
    }

    void generateLicensePage(Project project) {
        def ext = project.extensions.getByType(LicenseToolsExtension)

        def noLicenseLibraries = new ArrayList<LibraryInfo>()
        def content = new StringBuilder()

        librariesYaml.each { libraryInfo ->
            if (libraryInfo.skip) {
                project.logger.info("generateLicensePage: skip ${libraryInfo.name}")
                return
            }

            // merge dependencyLicenses's libraryInfo into librariesYaml's
            def o = dependencyLicenses.find(libraryInfo.artifactId)
            if (o) {
                libraryInfo.license = libraryInfo.license ?: o.license
                libraryInfo.filename = o.filename
                libraryInfo.artifactId = o.artifactId
                libraryInfo.url = libraryInfo.url ?: o.url
            }
            try {
                content.append(Templates.buildLicenseHtml(libraryInfo, project.projectDir));
            } catch (NotEnoughInformationException e) {
                noLicenseLibraries.add(e.libraryInfo)
            } catch (FileNotFoundException e) {
                project.logger.error(e.message)
            }
        }

        assertEmptyLibraries(project, noLicenseLibraries)

        def assetsDir = project.file("src/main/assets")
        if (!assetsDir.exists()) {
            assetsDir.mkdirs()
        }

        def applicationInfo = ApplicationInfo.fromProperty(project.applicationInfo)

        project.logger.info("render ${assetsDir}/${ext.outputHtml}")
        try {
            project.file("${assetsDir}/${ext.outputHtml}").write(Templates.wrapWithLayout(content, project.projectDir, applicationInfo))
        } catch (FileNotFoundException e) {
            project.logger.error(e.message)
        }
    }

    void generateLicenseJson(Project project) {
        def ext = project.extensions.getByType(LicenseToolsExtension)
        def noLicenseLibraries = new ArrayList<LibraryInfo>()

        def json = new JsonBuilder()
        def librariesArray = []

        librariesYaml.each { libraryInfo ->
            if (libraryInfo.skip) {
                project.logger.info("generateLicensePage: skip ${libraryInfo.name}")
                return
            }

            // merge dependencyLicenses's libraryInfo into librariesYaml's
            def o = dependencyLicenses.find(libraryInfo.artifactId)
            if (o) {
                libraryInfo.license = libraryInfo.license ?: o.license
                // libraryInfo.filename = o.filename
                libraryInfo.artifactId = o.artifactId
                libraryInfo.url = libraryInfo.url ?: o.url
            }
            try {
                Templates.assertLicenseAndStatement(libraryInfo)
                librariesArray << libraryInfo
            } catch (NotEnoughInformationException e) {
                noLicenseLibraries.add(e.libraryInfo)
            }
        }

        assertEmptyLibraries(project, noLicenseLibraries)

        def assetsDir = project.file("src/main/assets")
        if (!assetsDir.exists()) {
            assetsDir.mkdirs()
        }

        json {
            libraries librariesArray.collect {
                l ->
                    return [
                        notice: l.notice,
                        copyrightHolder: l.copyrightHolder,
                        copyrightStatement: l.copyrightStatement,
                        license: l.license,
                        licenseUrl: l.licenseUrl,
                        normalizedLicense: l.normalizedLicense,
                        year: l.year,
                        url: l.url,
                        libraryName: l.libraryName,
                        // I don't why artifactId won't serialize, and this is the only way
                        // I've found -- vishna
                        artifactId: [
                                name: l.artifactId.name,
                                group: l.artifactId.group,
                                version: l.artifactId.version,
                        ]
                    ]
            }
        }

        project.logger.info("render ${assetsDir}/${ext.outputJson}")
        project.file("${assetsDir}/${ext.outputJson}").write(json.toString())
    }

    static void assertEmptyLibraries(Project project, ArrayList<LibraryInfo> noLicenseLibraries) {
        if (noLicenseLibraries.empty) return;
        StringBuilder message = new StringBuilder();
        message.append("Not enough information for:\n")
        message.append("---\n")
        noLicenseLibraries.each { libraryInfo ->
            message.append("- artifact: ${libraryInfo.artifactId}\n")
            message.append("  name: ${libraryInfo.name}\n")
            message.append("  license: ${libraryInfo.license}\n")
            message.append("  copyrightHolder: ${libraryInfo.copyrightHolder}\n")
        }
        project.logger.error(message.toString())
    }

    // originated from https://github.com/hierynomus/license-gradle-plugin DependencyResolver.groovy
    Set<ResolvedArtifact> resolveProjectDependencies(Project project) {
        def subprojects = project.rootProject.subprojects.groupBy { Project p -> "$p.group:$p.name:$p.version" }

        List<ResolvedArtifact> runtimeDependencies = project.configurations.all.findAll { Configuration c ->
            c.name.matches(/^(?:release\w*)?[cC]ompile$/) // compile, releaseCompile, releaseProductionCompile, and so on.
        }.collect {
            it.resolvedConfiguration.resolvedArtifacts
        }.flatten() as List<ResolvedArtifact>

        def seen = new HashSet<String>()
        def dependenciesToHandle = new HashSet<ResolvedArtifact>()
        runtimeDependencies.each { ResolvedArtifact d ->
            String dependencyDesc = "$d.moduleVersion.id.group:$d.moduleVersion.id.name:$d.moduleVersion.id.version"
            if (!seen.contains(dependencyDesc)) {
                dependenciesToHandle.add(d)

                Project subproject = subprojects[dependencyDesc]?.first()
                if (subproject) {
                    dependenciesToHandle.addAll(resolveProjectDependencies(subproject))
                }
            }
        }

        return dependenciesToHandle
    }
}
