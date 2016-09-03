package com.cookpad.android.licensetools

public class LibraryInfo implements Comparable<LibraryInfo> {

    String libraryName = ""

    ArtifactId artifactId;

    String filename = ""

    String year = "";

    String copyrightHolder = "";

    String notice = ""

    String license = ""

    String url = "";

    boolean skip = false

    // from libraries.yml
    public static LibraryInfo fromYaml(Object lib) {
        def libraryInfo = new LibraryInfo()
        libraryInfo.artifactId = ArtifactId.parse(lib.artifact as String)
        libraryInfo.filename = lib.filename as String
        libraryInfo.year = lib.year ?: ""
        libraryInfo.libraryName = lib.name as String
        if (lib.copyrightHolder) {
            libraryInfo.copyrightHolder = lib.copyrightHolder
        } else if (lib.copyrightHolders) {
            libraryInfo.copyrightHolder = joinWords(lib.copyrightHolders)
        } else if (lib.authors) {
            libraryInfo.copyrightHolder = joinWords(lib.authors)
        } else if (lib.author) {
            libraryInfo.copyrightHolder = lib.author
        }
        libraryInfo.license = lib.license ?: ""
        libraryInfo.notice = lib.notice as String
        libraryInfo.skip = lib.skip as boolean
        libraryInfo.url = lib.url ?: ""
        return libraryInfo
    }

    private String getNameFromArtifactId() {
        if (artifactId) {
            if (artifactId.name != "+") {
                return artifactId.name
            } else {
                return artifactId.group
            }
        } else {
            return null
        }
    }

    public String getName() {
        return libraryName ?: getNameFromArtifactId() ?: filename
    }

    public String getEscapedName() {
        return name ? (name.contains(": ") ? "\"${name}\"" : name) : null
    }

    // called from HTML templates
    public String getCopyrightStatement() {
        if (notice) {
            return notice;
        } else if (!copyrightHolder) {
            return "";
        } else {
            return buildCopyrightStatement(copyrightHolder)
        }
    }

    private String buildCopyrightStatement(String copyrightHolder) {
        def dot = copyrightHolder.endsWith(".") ? "" : "."
        if (year) {
            return "Copyright &copy; ${year} ${copyrightHolder}${dot} All rights reserved."
        } else {
            return "Copyright &copy; ${copyrightHolder}${dot} All rights reserved."
        }
    }

    public String getNormalizedLicense() {
        return normalizeLicense(license ?: "")
    }

    static String normalizeLicense(String name) {
        switch (name) {
            case ~/(?i).*\bapache.*1\.0.*/:
                return "Apache-1.0"
            case ~/(?i).*\bapache.*1.*/:
                return "Apache-1.1"
            case ~/(?i).*\bapache.*2.*/:
            case ~/(?i).*\bapache.*/:
                return "Apache-2.0"

            case ~/(?i).*\bbsd\b.*\b2\b.*/:
            case ~/(?i).*\bbsd_2.*/:
            case ~/(?i).*\bsimplified\b.*\bbsd\b.*/:
            case ~/(?i).*\bfreebsd\b.*/:
                return "BSD-2-Clause"
            case ~/(?i).*\bbsd\b.*\b3\b.*/:
            case ~/(?i).*\bbsd_3.*/:
            case ~/(?i).*\brevised\b.*\bbsd\b.*/:
            case ~/(?i).*\bnew\b.*\bbsd\b.*/:
            case ~/(?i).*\bmodified\b.*\bbsd\b.*/:
                return "BSD-3-Clause"
            case ~/(?i).*\bbsd\b.*\b4\b.*/:
            case ~/(?i).*\bbsd_4.*/:
            case ~/(?i).*\boriginal\b.*\bbsd\b.*/:
                return "BSD-4-Clause"
            case ~/(?i).*\bbsd\b.*\b0\b.*/:
            case ~/(?i).*\bbsd_0.*/:
            case ~/(?i).*\bbsd\b.*\bzero\b.*/:
                return "FPL"
            case ~/(?i).*\bbsd\b.*/:
                return "BSD-3-Clause"

            case ~/(?i).*\bcommon\b.*\bdevelopment\b.*\bdistribution\b.*/:
            case ~/(?i).*\bcddl\b.*/:
                return "CDDL"

            case ~/(?i).*\bcommon\b.*\bpublic\b.*/:
            case ~/(?i).*\bcpl\b.*/:
                return "CPL"
            case ~/(?i).*\beclipse\b.*\bpublic\b.*/:
            case ~/(?i).*\bepl\b.*/:
                return "EPL"

            case ~/(?i).*\bfree\b.*\bpublic\b.*/:
            case ~/(?i).*\bfpl\b.*/:
                return "FPL"

            case ~/(?i).*\bgnu\b.*\blesser\b.*\bgeneral\b.*\bpublic\b.*2.*/:
            case ~/(?i).*\blgpl\b.*2.*/:
                return "LGPL-2.1"
            case ~/(?i).*\bgnu\b.*\blesser\b.*\bgeneral\b.*\bpublic\b.*3.*/:
            case ~/(?i).*\bgnu\b.*\blesser\b.*\bgeneral\b.*\bpublic\b.*/:
            case ~/(?i).*\blgpl\b.*3.*/:
            case ~/(?i).*\blgpl\b.*/:
                return "LGPL-3.0"

            case ~/(?i).*\bgnu\b.*\bgeneral\b.*\bpublic\b.*1.*/:
            case ~/(?i).*\bgpl\b.*1.*/:
                return "GPL-1.0"
            case ~/(?i).*\bgnu\b.*\bgeneral\b.*\bpublic\b.*2.*/:
            case ~/(?i).*\bgpl\b.*2.*/:
                return "GPL-2.0"
            case ~/(?i).*\bgnu\b.*\bgeneral\b.*\bpublic\b.*3.*/:
            case ~/(?i).*\bgnu\b.*\bgeneral\b.*\bpublic\b.*/:
            case ~/(?i).*\bgpl\b.*3.*/:
            case ~/(?i).*\bgpl\b.*/:
                return "GPL-3.0"

            case ~/(?i).*\bisc\b.*/:
                return "ISC"

            case ~/(?i).*\bmit\b.*/:
            case ~/(?i).*\bx11\b.*/:
            case ~/(?i).*\bx\b.*/:
                return "MIT"

            case ~/(?i).*\bmozilla\b.*\bpublic\b.*1\.0.*/:
            case ~/(?i).*\bmpl\b.*1\.0.*/:
                return "MPL-1.0"
            case ~/(?i).*\bmozilla\b.*\bpublic\b.*1.*/:
            case ~/(?i).*\bmpl\b.*1.*/:
                return "MPL-1.1"
            case ~/(?i).*\bmozilla\b.*\bpublic\b.*2.*/:
            case ~/(?i).*\bmozilla\b.*\bpublic\b.*/:
            case ~/(?i).*\bmpl\b.*2.*/:
            case ~/(?i).*\bmpl\b.*/:
                return "MPL-2.0"

            default:
                return name
        }
    }

    static String joinWords(List<String> words) {
        if (words.size() == 0) {
            return ""
        } else if (words.size() == 1) {
            return words.first()
        } else if (words.size() == 2) {
            return "${words.first()} and ${words.last()}"
        } else {
            // example: a, b, c, and d
            String last = words.last()
            return words.subList(0, words.size() - 1).join(", ") + ", and " + last
        }
    }

    boolean equals(o) {
        if (this.is(o)) {
            return true
        }
        if (getClass() != o.class) {
            return false
        }

        LibraryInfo that = (LibraryInfo) o

        if (name != that.name) {
            return false
        }

        return true
    }

    int hashCode() {
        return name.hashCode()
    }

    @Override
    int compareTo(LibraryInfo o) {
        return name.compareToIgnoreCase(o.name)
    }
}
