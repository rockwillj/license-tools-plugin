package com.cookpad.android.licensetools

import org.junit.Test

import static com.cookpad.android.licensetools.LibraryInfo.joinWords
import static com.cookpad.android.licensetools.LibraryInfo.normalizeLicense
import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertNotEquals

public class LibraryInfoTest {

    @Test
    public void testJoinWords() throws Exception {
        assertEquals("foo", joinWords(["foo"]))
        assertEquals("foo and bar", joinWords(["foo", "bar"]))
        assertEquals("foo, bar, and baz", joinWords(["foo", "bar", "baz"]))
    }

    @Test
    void testNormalizeLicense() {
        assertEquals("Apache-2.0", normalizeLicense("apache"))
        assertEquals("Apache-2.0", normalizeLicense("Apache"))
        assertEquals("Apache-2.0", normalizeLicense("apache license"))
        assertEquals("Apache-2.0", normalizeLicense("Apache License"))
        assertEquals("Apache-2.0", normalizeLicense("apache license2"))
        assertEquals("Apache-2.0", normalizeLicense("Apache License2"))
        assertEquals("Apache-2.0", normalizeLicense("apache license2.0"))
        assertEquals("Apache-2.0", normalizeLicense("Apache License2.0"))
        assertEquals("Apache-2.0", normalizeLicense("apache license 2.0"))
        assertEquals("Apache-2.0", normalizeLicense("Apache License 2.0"))
        assertEquals("Apache-2.0", normalizeLicense("The Apache Software License"))
        assertEquals("Apache-2.0", normalizeLicense("The Apache Software License, Version 2.0"))
        assertNotEquals("Apache-2.0", normalizeLicense("apache license1"))
        assertNotEquals("Apache-2.0", normalizeLicense("Apache License1"))
        assertNotEquals("Apache-2.0", normalizeLicense("apache license1.0"))
        assertNotEquals("Apache-2.0", normalizeLicense("Apache License1.0"))
        assertNotEquals("Apache-2.0", normalizeLicense("apache license 1.0"))
        assertNotEquals("Apache-2.0", normalizeLicense("Apache License 1.0"))

        assertEquals("BSD-3-Clause", normalizeLicense("bsd"))
        assertEquals("BSD-3-Clause", normalizeLicense("BSD"))
        assertEquals("BSD-3-Clause", normalizeLicense("BSD 3 Clauses"))
        assertEquals("BSD-3-Clause", normalizeLicense("BSD 3-Clause"))
        assertEquals("BSD-3-Clause", normalizeLicense("The BSD License"))
        assertEquals("BSD-3-Clause", normalizeLicense("The BSD 3 Clauses License"))
        assertEquals("BSD-3-Clause", normalizeLicense("The BSD 3-Clause License"))
        assertEquals("BSD-3-Clause", normalizeLicense("Revised BSD License"))
        assertEquals("BSD-3-Clause", normalizeLicense("New BSD License"))
        assertEquals("BSD-3-Clause", normalizeLicense("Modified BSD License"))

        assertEquals("BSD-2-Clause", normalizeLicense("BSD 2 Clauses"))
        assertEquals("BSD-2-Clause", normalizeLicense("BSD 2-Clause"))
        assertEquals("BSD-2-Clause", normalizeLicense("The BSD 2 Clauses License"))
        assertEquals("BSD-2-Clause", normalizeLicense("The BSD 2-Clause License"))
        assertEquals("BSD-2-Clause", normalizeLicense("Simplified BSD License"))
        assertEquals("BSD-2-Clause", normalizeLicense("FreeBSD License"))

        assertEquals("MIT", normalizeLicense("mit"))
        assertEquals("MIT", normalizeLicense("MIT"))
        assertEquals("MIT", normalizeLicense("The MIT License"))
        assertEquals("MIT", normalizeLicense("x license"))
        assertEquals("MIT", normalizeLicense("x11 license"))
        assertEquals("MIT", normalizeLicense("X License"))
        assertEquals("MIT", normalizeLicense("X11 License"))

        assertEquals("ISC", normalizeLicense("isc"))
        assertEquals("ISC", normalizeLicense("ISC"))
        assertEquals("ISC", normalizeLicense("The ISC License"))

        assertEquals("Other", normalizeLicense("Other"))
        assertEquals("No license found", normalizeLicense("No license found"))

        // compatibility
        assertEquals("Apache-2.0", normalizeLicense("apache_2"))
        assertEquals("BSD-2-Clause", normalizeLicense("bsd_2_clauses"))
        assertEquals("BSD-3-Clause", normalizeLicense("bsd_3_clauses"))
    }

    @Test
    public void testCopyrightStatementWithEndDot() throws Exception {
        LibraryInfo libraryInfo = LibraryInfo.fromYaml([
                "copyrightHolder": "Foo Inc.",
        ])

        assertEquals("Copyright &copy; Foo Inc. All rights reserved.", libraryInfo.copyrightStatement)
    }

    @Test
    public void testCopyrightStatementWithoutEndDot() throws Exception {
        LibraryInfo libraryInfo = LibraryInfo.fromYaml([
                "copyrightHolder": "Foo",
        ])

        assertEquals("Copyright &copy; Foo. All rights reserved.", libraryInfo.copyrightStatement)
    }

}
