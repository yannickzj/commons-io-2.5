/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.io;

import java.lang.String;
import org.apache.commons.io.testtools.FileBasedTestCase;
import org.apache.commons.io.testtools.TestUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * This is used to test FilenameUtils for correctness.
 *
 * @version $Id$
 * @see FilenameUtils
 */
public class FilenameUtilsTestCase extends FileBasedTestCase {

    private static final String SEP = "" + File.separatorChar;
    private static final boolean WINDOWS = File.separatorChar == '\\';

    private final File testFile1;
    private final File testFile2;

    private final int testFile1Size;
    private final int testFile2Size;

    public FilenameUtilsTestCase() {
        testFile1 = new File(getTestDirectory(), "file1-test.txt");
        testFile2 = new File(getTestDirectory(), "file1a-test.txt");

        testFile1Size = (int) testFile1.length();
        testFile2Size = (int) testFile2.length();
    }

    /**
     * @see junit.framework.TestCase#setUp()
     */
    @Before
    public void setUp() throws Exception {
        getTestDirectory();
        if (!testFile1.getParentFile().exists()) {
            throw new IOException("Cannot create file " + testFile1
                    + " as the parent directory does not exist");
        }
        final BufferedOutputStream output3 =
                new BufferedOutputStream(new FileOutputStream(testFile1));
        try {
            TestUtils.generateTestData(output3, (long) testFile1Size);
        } finally {
            IOUtils.closeQuietly(output3);
        }
        if (!testFile2.getParentFile().exists()) {
            throw new IOException("Cannot create file " + testFile2
                    + " as the parent directory does not exist");
        }
        final BufferedOutputStream output2 =
                new BufferedOutputStream(new FileOutputStream(testFile2));
        try {
            TestUtils.generateTestData(output2, (long) testFile2Size);
        } finally {
            IOUtils.closeQuietly(output2);
        }
        FileUtils.deleteDirectory(getTestDirectory());
        getTestDirectory();
        if (!testFile1.getParentFile().exists()) {
            throw new IOException("Cannot create file " + testFile1
                    + " as the parent directory does not exist");
        }
        final BufferedOutputStream output1 =
                new BufferedOutputStream(new FileOutputStream(testFile1));
        try {
            TestUtils.generateTestData(output1, (long) testFile1Size);
        } finally {
            IOUtils.closeQuietly(output1);
        }
        if (!testFile2.getParentFile().exists()) {
            throw new IOException("Cannot create file " + testFile2
                    + " as the parent directory does not exist");
        }
        final BufferedOutputStream output =
                new BufferedOutputStream(new FileOutputStream(testFile2));
        try {
            TestUtils.generateTestData(output, (long) testFile2Size);
        } finally {
            IOUtils.closeQuietly(output);
        }
    }

    /**
     * @see junit.framework.TestCase#tearDown()
     */
    @After
    public void tearDown() throws Exception {
        FileUtils.deleteDirectory(getTestDirectory());
    }

    //-----------------------------------------------------------------------
    @Test
    public void testNormalize() throws Exception {
        assertEquals(null, FilenameUtils.normalize(null));
        assertEquals(null, FilenameUtils.normalize(":"));
        assertEquals(null, FilenameUtils.normalize("1:\\a\\b\\c.txt"));
        assertEquals(null, FilenameUtils.normalize("1:"));
        assertEquals(null, FilenameUtils.normalize("1:a"));
        assertEquals(null, FilenameUtils.normalize("\\\\\\a\\b\\c.txt"));
        assertEquals(null, FilenameUtils.normalize("\\\\a"));

        assertEquals("a" + SEP + "b" + SEP + "c.txt", FilenameUtils.normalize("a\\b/c.txt"));
        assertEquals("" + SEP + "a" + SEP + "b" + SEP + "c.txt", FilenameUtils.normalize("\\a\\b/c.txt"));
        assertEquals("C:" + SEP + "a" + SEP + "b" + SEP + "c.txt", FilenameUtils.normalize("C:\\a\\b/c.txt"));
        assertEquals("" + SEP + "" + SEP + "server" + SEP + "a" + SEP + "b" + SEP + "c.txt", FilenameUtils.normalize("\\\\server\\a\\b/c.txt"));
        assertEquals("~" + SEP + "a" + SEP + "b" + SEP + "c.txt", FilenameUtils.normalize("~\\a\\b/c.txt"));
        assertEquals("~user" + SEP + "a" + SEP + "b" + SEP + "c.txt", FilenameUtils.normalize("~user\\a\\b/c.txt"));

        assertEquals("a" + SEP + "c", FilenameUtils.normalize("a/b/../c"));
        assertEquals("c", FilenameUtils.normalize("a/b/../../c"));
        assertEquals("c" + SEP, FilenameUtils.normalize("a/b/../../c/"));
        assertEquals(null, FilenameUtils.normalize("a/b/../../../c"));
        assertEquals("a" + SEP, FilenameUtils.normalize("a/b/.."));
        assertEquals("a" + SEP, FilenameUtils.normalize("a/b/../"));
        assertEquals("", FilenameUtils.normalize("a/b/../.."));
        assertEquals("", FilenameUtils.normalize("a/b/../../"));
        assertEquals(null, FilenameUtils.normalize("a/b/../../.."));
        assertEquals("a" + SEP + "d", FilenameUtils.normalize("a/b/../c/../d"));
        assertEquals("a" + SEP + "d" + SEP, FilenameUtils.normalize("a/b/../c/../d/"));
        assertEquals("a" + SEP + "b" + SEP + "d", FilenameUtils.normalize("a/b//d"));
        assertEquals("a" + SEP + "b" + SEP, FilenameUtils.normalize("a/b/././."));
        assertEquals("a" + SEP + "b" + SEP, FilenameUtils.normalize("a/b/./././"));
        assertEquals("a" + SEP, FilenameUtils.normalize("./a/"));
        assertEquals("a", FilenameUtils.normalize("./a"));
        assertEquals("", FilenameUtils.normalize("./"));
        assertEquals("", FilenameUtils.normalize("."));
        assertEquals(null, FilenameUtils.normalize("../a"));
        assertEquals(null, FilenameUtils.normalize(".."));
        assertEquals("", FilenameUtils.normalize(""));

        assertEquals(SEP + "a", FilenameUtils.normalize("/a"));
        assertEquals(SEP + "a" + SEP, FilenameUtils.normalize("/a/"));
        assertEquals(SEP + "a" + SEP + "c", FilenameUtils.normalize("/a/b/../c"));
        assertEquals(SEP + "c", FilenameUtils.normalize("/a/b/../../c"));
        assertEquals(null, FilenameUtils.normalize("/a/b/../../../c"));
        assertEquals(SEP + "a" + SEP, FilenameUtils.normalize("/a/b/.."));
        assertEquals(SEP + "", FilenameUtils.normalize("/a/b/../.."));
        assertEquals(null, FilenameUtils.normalize("/a/b/../../.."));
        assertEquals(SEP + "a" + SEP + "d", FilenameUtils.normalize("/a/b/../c/../d"));
        assertEquals(SEP + "a" + SEP + "b" + SEP + "d", FilenameUtils.normalize("/a/b//d"));
        assertEquals(SEP + "a" + SEP + "b" + SEP, FilenameUtils.normalize("/a/b/././."));
        assertEquals(SEP + "a", FilenameUtils.normalize("/./a"));
        assertEquals(SEP + "", FilenameUtils.normalize("/./"));
        assertEquals(SEP + "", FilenameUtils.normalize("/."));
        assertEquals(null, FilenameUtils.normalize("/../a"));
        assertEquals(null, FilenameUtils.normalize("/.."));
        assertEquals(SEP + "", FilenameUtils.normalize("/"));

        assertEquals("~" + SEP + "a", FilenameUtils.normalize("~/a"));
        assertEquals("~" + SEP + "a" + SEP, FilenameUtils.normalize("~/a/"));
        assertEquals("~" + SEP + "a" + SEP + "c", FilenameUtils.normalize("~/a/b/../c"));
        assertEquals("~" + SEP + "c", FilenameUtils.normalize("~/a/b/../../c"));
        assertEquals(null, FilenameUtils.normalize("~/a/b/../../../c"));
        assertEquals("~" + SEP + "a" + SEP, FilenameUtils.normalize("~/a/b/.."));
        assertEquals("~" + SEP + "", FilenameUtils.normalize("~/a/b/../.."));
        assertEquals(null, FilenameUtils.normalize("~/a/b/../../.."));
        assertEquals("~" + SEP + "a" + SEP + "d", FilenameUtils.normalize("~/a/b/../c/../d"));
        assertEquals("~" + SEP + "a" + SEP + "b" + SEP + "d", FilenameUtils.normalize("~/a/b//d"));
        assertEquals("~" + SEP + "a" + SEP + "b" + SEP, FilenameUtils.normalize("~/a/b/././."));
        assertEquals("~" + SEP + "a", FilenameUtils.normalize("~/./a"));
        assertEquals("~" + SEP, FilenameUtils.normalize("~/./"));
        assertEquals("~" + SEP, FilenameUtils.normalize("~/."));
        assertEquals(null, FilenameUtils.normalize("~/../a"));
        assertEquals(null, FilenameUtils.normalize("~/.."));
        assertEquals("~" + SEP, FilenameUtils.normalize("~/"));
        assertEquals("~" + SEP, FilenameUtils.normalize("~"));

        assertEquals("~user" + SEP + "a", FilenameUtils.normalize("~user/a"));
        assertEquals("~user" + SEP + "a" + SEP, FilenameUtils.normalize("~user/a/"));
        assertEquals("~user" + SEP + "a" + SEP + "c", FilenameUtils.normalize("~user/a/b/../c"));
        assertEquals("~user" + SEP + "c", FilenameUtils.normalize("~user/a/b/../../c"));
        assertEquals(null, FilenameUtils.normalize("~user/a/b/../../../c"));
        assertEquals("~user" + SEP + "a" + SEP, FilenameUtils.normalize("~user/a/b/.."));
        assertEquals("~user" + SEP + "", FilenameUtils.normalize("~user/a/b/../.."));
        assertEquals(null, FilenameUtils.normalize("~user/a/b/../../.."));
        assertEquals("~user" + SEP + "a" + SEP + "d", FilenameUtils.normalize("~user/a/b/../c/../d"));
        assertEquals("~user" + SEP + "a" + SEP + "b" + SEP + "d", FilenameUtils.normalize("~user/a/b//d"));
        assertEquals("~user" + SEP + "a" + SEP + "b" + SEP, FilenameUtils.normalize("~user/a/b/././."));
        assertEquals("~user" + SEP + "a", FilenameUtils.normalize("~user/./a"));
        assertEquals("~user" + SEP + "", FilenameUtils.normalize("~user/./"));
        assertEquals("~user" + SEP + "", FilenameUtils.normalize("~user/."));
        assertEquals(null, FilenameUtils.normalize("~user/../a"));
        assertEquals(null, FilenameUtils.normalize("~user/.."));
        assertEquals("~user" + SEP, FilenameUtils.normalize("~user/"));
        assertEquals("~user" + SEP, FilenameUtils.normalize("~user"));

        assertEquals("C:" + SEP + "a", FilenameUtils.normalize("C:/a"));
        assertEquals("C:" + SEP + "a" + SEP, FilenameUtils.normalize("C:/a/"));
        assertEquals("C:" + SEP + "a" + SEP + "c", FilenameUtils.normalize("C:/a/b/../c"));
        assertEquals("C:" + SEP + "c", FilenameUtils.normalize("C:/a/b/../../c"));
        assertEquals(null, FilenameUtils.normalize("C:/a/b/../../../c"));
        assertEquals("C:" + SEP + "a" + SEP, FilenameUtils.normalize("C:/a/b/.."));
        assertEquals("C:" + SEP + "", FilenameUtils.normalize("C:/a/b/../.."));
        assertEquals(null, FilenameUtils.normalize("C:/a/b/../../.."));
        assertEquals("C:" + SEP + "a" + SEP + "d", FilenameUtils.normalize("C:/a/b/../c/../d"));
        assertEquals("C:" + SEP + "a" + SEP + "b" + SEP + "d", FilenameUtils.normalize("C:/a/b//d"));
        assertEquals("C:" + SEP + "a" + SEP + "b" + SEP, FilenameUtils.normalize("C:/a/b/././."));
        assertEquals("C:" + SEP + "a", FilenameUtils.normalize("C:/./a"));
        assertEquals("C:" + SEP + "", FilenameUtils.normalize("C:/./"));
        assertEquals("C:" + SEP + "", FilenameUtils.normalize("C:/."));
        assertEquals(null, FilenameUtils.normalize("C:/../a"));
        assertEquals(null, FilenameUtils.normalize("C:/.."));
        assertEquals("C:" + SEP + "", FilenameUtils.normalize("C:/"));

        assertEquals("C:" + "a", FilenameUtils.normalize("C:a"));
        assertEquals("C:" + "a" + SEP, FilenameUtils.normalize("C:a/"));
        assertEquals("C:" + "a" + SEP + "c", FilenameUtils.normalize("C:a/b/../c"));
        assertEquals("C:" + "c", FilenameUtils.normalize("C:a/b/../../c"));
        assertEquals(null, FilenameUtils.normalize("C:a/b/../../../c"));
        assertEquals("C:" + "a" + SEP, FilenameUtils.normalize("C:a/b/.."));
        assertEquals("C:" + "", FilenameUtils.normalize("C:a/b/../.."));
        assertEquals(null, FilenameUtils.normalize("C:a/b/../../.."));
        assertEquals("C:" + "a" + SEP + "d", FilenameUtils.normalize("C:a/b/../c/../d"));
        assertEquals("C:" + "a" + SEP + "b" + SEP + "d", FilenameUtils.normalize("C:a/b//d"));
        assertEquals("C:" + "a" + SEP + "b" + SEP, FilenameUtils.normalize("C:a/b/././."));
        assertEquals("C:" + "a", FilenameUtils.normalize("C:./a"));
        assertEquals("C:" + "", FilenameUtils.normalize("C:./"));
        assertEquals("C:" + "", FilenameUtils.normalize("C:."));
        assertEquals(null, FilenameUtils.normalize("C:../a"));
        assertEquals(null, FilenameUtils.normalize("C:.."));
        assertEquals("C:" + "", FilenameUtils.normalize("C:"));

        assertEquals(SEP + SEP + "server" + SEP + "a", FilenameUtils.normalize("//server/a"));
        assertEquals(SEP + SEP + "server" + SEP + "a" + SEP, FilenameUtils.normalize("//server/a/"));
        assertEquals(SEP + SEP + "server" + SEP + "a" + SEP + "c", FilenameUtils.normalize("//server/a/b/../c"));
        assertEquals(SEP + SEP + "server" + SEP + "c", FilenameUtils.normalize("//server/a/b/../../c"));
        assertEquals(null, FilenameUtils.normalize("//server/a/b/../../../c"));
        assertEquals(SEP + SEP + "server" + SEP + "a" + SEP, FilenameUtils.normalize("//server/a/b/.."));
        assertEquals(SEP + SEP + "server" + SEP + "", FilenameUtils.normalize("//server/a/b/../.."));
        assertEquals(null, FilenameUtils.normalize("//server/a/b/../../.."));
        assertEquals(SEP + SEP + "server" + SEP + "a" + SEP + "d", FilenameUtils.normalize("//server/a/b/../c/../d"));
        assertEquals(SEP + SEP + "server" + SEP + "a" + SEP + "b" + SEP + "d", FilenameUtils.normalize("//server/a/b//d"));
        assertEquals(SEP + SEP + "server" + SEP + "a" + SEP + "b" + SEP, FilenameUtils.normalize("//server/a/b/././."));
        assertEquals(SEP + SEP + "server" + SEP + "a", FilenameUtils.normalize("//server/./a"));
        assertEquals(SEP + SEP + "server" + SEP + "", FilenameUtils.normalize("//server/./"));
        assertEquals(SEP + SEP + "server" + SEP + "", FilenameUtils.normalize("//server/."));
        assertEquals(null, FilenameUtils.normalize("//server/../a"));
        assertEquals(null, FilenameUtils.normalize("//server/.."));
        assertEquals(SEP + SEP + "server" + SEP + "", FilenameUtils.normalize("//server/"));
    }

    @Test
    public void testNormalize_with_nullbytes() throws Exception {
        try {
            assertEquals("a" + SEP + "b" + SEP + "c.txt", FilenameUtils.normalize("a\\b/c\u0000.txt"));
        } catch (IllegalArgumentException ignore) {
        }

        try {
            assertEquals("a" + SEP + "b" + SEP + "c.txt", FilenameUtils.normalize("\u0000a\\b/c.txt"));
        } catch (IllegalArgumentException ignore) {
        }
    }

    @Test
	public void testNormalizeUnixWin() throws Exception {
		this.filenameUtilsTestCaseTestNormalizeUnixWinTemplate(
				new FilenameUtilsTestCaseTestNormalizeUnixWinAdapterImpl(), "/a/c/", "/a/c/", "\\a\\c\\", "\\a\\c\\");
	}

    //-----------------------------------------------------------------------
    @Test
    public void testNormalizeNoEndSeparator() throws Exception {
        assertEquals(null, FilenameUtils.normalizeNoEndSeparator(null));
        assertEquals(null, FilenameUtils.normalizeNoEndSeparator(":"));
        assertEquals(null, FilenameUtils.normalizeNoEndSeparator("1:\\a\\b\\c.txt"));
        assertEquals(null, FilenameUtils.normalizeNoEndSeparator("1:"));
        assertEquals(null, FilenameUtils.normalizeNoEndSeparator("1:a"));
        assertEquals(null, FilenameUtils.normalizeNoEndSeparator("\\\\\\a\\b\\c.txt"));
        assertEquals(null, FilenameUtils.normalizeNoEndSeparator("\\\\a"));

        assertEquals("a" + SEP + "b" + SEP + "c.txt", FilenameUtils.normalizeNoEndSeparator("a\\b/c.txt"));
        assertEquals("" + SEP + "a" + SEP + "b" + SEP + "c.txt", FilenameUtils.normalizeNoEndSeparator("\\a\\b/c.txt"));
        assertEquals("C:" + SEP + "a" + SEP + "b" + SEP + "c.txt", FilenameUtils.normalizeNoEndSeparator("C:\\a\\b/c.txt"));
        assertEquals("" + SEP + "" + SEP + "server" + SEP + "a" + SEP + "b" + SEP + "c.txt", FilenameUtils.normalizeNoEndSeparator("\\\\server\\a\\b/c.txt"));
        assertEquals("~" + SEP + "a" + SEP + "b" + SEP + "c.txt", FilenameUtils.normalizeNoEndSeparator("~\\a\\b/c.txt"));
        assertEquals("~user" + SEP + "a" + SEP + "b" + SEP + "c.txt", FilenameUtils.normalizeNoEndSeparator("~user\\a\\b/c.txt"));

        assertEquals("a" + SEP + "c", FilenameUtils.normalizeNoEndSeparator("a/b/../c"));
        assertEquals("c", FilenameUtils.normalizeNoEndSeparator("a/b/../../c"));
        assertEquals("c", FilenameUtils.normalizeNoEndSeparator("a/b/../../c/"));
        assertEquals(null, FilenameUtils.normalizeNoEndSeparator("a/b/../../../c"));
        assertEquals("a", FilenameUtils.normalizeNoEndSeparator("a/b/.."));
        assertEquals("a", FilenameUtils.normalizeNoEndSeparator("a/b/../"));
        assertEquals("", FilenameUtils.normalizeNoEndSeparator("a/b/../.."));
        assertEquals("", FilenameUtils.normalizeNoEndSeparator("a/b/../../"));
        assertEquals(null, FilenameUtils.normalizeNoEndSeparator("a/b/../../.."));
        assertEquals("a" + SEP + "d", FilenameUtils.normalizeNoEndSeparator("a/b/../c/../d"));
        assertEquals("a" + SEP + "d", FilenameUtils.normalizeNoEndSeparator("a/b/../c/../d/"));
        assertEquals("a" + SEP + "b" + SEP + "d", FilenameUtils.normalizeNoEndSeparator("a/b//d"));
        assertEquals("a" + SEP + "b", FilenameUtils.normalizeNoEndSeparator("a/b/././."));
        assertEquals("a" + SEP + "b", FilenameUtils.normalizeNoEndSeparator("a/b/./././"));
        assertEquals("a", FilenameUtils.normalizeNoEndSeparator("./a/"));
        assertEquals("a", FilenameUtils.normalizeNoEndSeparator("./a"));
        assertEquals("", FilenameUtils.normalizeNoEndSeparator("./"));
        assertEquals("", FilenameUtils.normalizeNoEndSeparator("."));
        assertEquals(null, FilenameUtils.normalizeNoEndSeparator("../a"));
        assertEquals(null, FilenameUtils.normalizeNoEndSeparator(".."));
        assertEquals("", FilenameUtils.normalizeNoEndSeparator(""));

        assertEquals(SEP + "a", FilenameUtils.normalizeNoEndSeparator("/a"));
        assertEquals(SEP + "a", FilenameUtils.normalizeNoEndSeparator("/a/"));
        assertEquals(SEP + "a" + SEP + "c", FilenameUtils.normalizeNoEndSeparator("/a/b/../c"));
        assertEquals(SEP + "c", FilenameUtils.normalizeNoEndSeparator("/a/b/../../c"));
        assertEquals(null, FilenameUtils.normalizeNoEndSeparator("/a/b/../../../c"));
        assertEquals(SEP + "a", FilenameUtils.normalizeNoEndSeparator("/a/b/.."));
        assertEquals(SEP + "", FilenameUtils.normalizeNoEndSeparator("/a/b/../.."));
        assertEquals(null, FilenameUtils.normalizeNoEndSeparator("/a/b/../../.."));
        assertEquals(SEP + "a" + SEP + "d", FilenameUtils.normalizeNoEndSeparator("/a/b/../c/../d"));
        assertEquals(SEP + "a" + SEP + "b" + SEP + "d", FilenameUtils.normalizeNoEndSeparator("/a/b//d"));
        assertEquals(SEP + "a" + SEP + "b", FilenameUtils.normalizeNoEndSeparator("/a/b/././."));
        assertEquals(SEP + "a", FilenameUtils.normalizeNoEndSeparator("/./a"));
        assertEquals(SEP + "", FilenameUtils.normalizeNoEndSeparator("/./"));
        assertEquals(SEP + "", FilenameUtils.normalizeNoEndSeparator("/."));
        assertEquals(null, FilenameUtils.normalizeNoEndSeparator("/../a"));
        assertEquals(null, FilenameUtils.normalizeNoEndSeparator("/.."));
        assertEquals(SEP + "", FilenameUtils.normalizeNoEndSeparator("/"));

        assertEquals("~" + SEP + "a", FilenameUtils.normalizeNoEndSeparator("~/a"));
        assertEquals("~" + SEP + "a", FilenameUtils.normalizeNoEndSeparator("~/a/"));
        assertEquals("~" + SEP + "a" + SEP + "c", FilenameUtils.normalizeNoEndSeparator("~/a/b/../c"));
        assertEquals("~" + SEP + "c", FilenameUtils.normalizeNoEndSeparator("~/a/b/../../c"));
        assertEquals(null, FilenameUtils.normalizeNoEndSeparator("~/a/b/../../../c"));
        assertEquals("~" + SEP + "a", FilenameUtils.normalizeNoEndSeparator("~/a/b/.."));
        assertEquals("~" + SEP + "", FilenameUtils.normalizeNoEndSeparator("~/a/b/../.."));
        assertEquals(null, FilenameUtils.normalizeNoEndSeparator("~/a/b/../../.."));
        assertEquals("~" + SEP + "a" + SEP + "d", FilenameUtils.normalizeNoEndSeparator("~/a/b/../c/../d"));
        assertEquals("~" + SEP + "a" + SEP + "b" + SEP + "d", FilenameUtils.normalizeNoEndSeparator("~/a/b//d"));
        assertEquals("~" + SEP + "a" + SEP + "b", FilenameUtils.normalizeNoEndSeparator("~/a/b/././."));
        assertEquals("~" + SEP + "a", FilenameUtils.normalizeNoEndSeparator("~/./a"));
        assertEquals("~" + SEP, FilenameUtils.normalizeNoEndSeparator("~/./"));
        assertEquals("~" + SEP, FilenameUtils.normalizeNoEndSeparator("~/."));
        assertEquals(null, FilenameUtils.normalizeNoEndSeparator("~/../a"));
        assertEquals(null, FilenameUtils.normalizeNoEndSeparator("~/.."));
        assertEquals("~" + SEP, FilenameUtils.normalizeNoEndSeparator("~/"));
        assertEquals("~" + SEP, FilenameUtils.normalizeNoEndSeparator("~"));

        assertEquals("~user" + SEP + "a", FilenameUtils.normalizeNoEndSeparator("~user/a"));
        assertEquals("~user" + SEP + "a", FilenameUtils.normalizeNoEndSeparator("~user/a/"));
        assertEquals("~user" + SEP + "a" + SEP + "c", FilenameUtils.normalizeNoEndSeparator("~user/a/b/../c"));
        assertEquals("~user" + SEP + "c", FilenameUtils.normalizeNoEndSeparator("~user/a/b/../../c"));
        assertEquals(null, FilenameUtils.normalizeNoEndSeparator("~user/a/b/../../../c"));
        assertEquals("~user" + SEP + "a", FilenameUtils.normalizeNoEndSeparator("~user/a/b/.."));
        assertEquals("~user" + SEP + "", FilenameUtils.normalizeNoEndSeparator("~user/a/b/../.."));
        assertEquals(null, FilenameUtils.normalizeNoEndSeparator("~user/a/b/../../.."));
        assertEquals("~user" + SEP + "a" + SEP + "d", FilenameUtils.normalizeNoEndSeparator("~user/a/b/../c/../d"));
        assertEquals("~user" + SEP + "a" + SEP + "b" + SEP + "d", FilenameUtils.normalizeNoEndSeparator("~user/a/b//d"));
        assertEquals("~user" + SEP + "a" + SEP + "b", FilenameUtils.normalizeNoEndSeparator("~user/a/b/././."));
        assertEquals("~user" + SEP + "a", FilenameUtils.normalizeNoEndSeparator("~user/./a"));
        assertEquals("~user" + SEP + "", FilenameUtils.normalizeNoEndSeparator("~user/./"));
        assertEquals("~user" + SEP + "", FilenameUtils.normalizeNoEndSeparator("~user/."));
        assertEquals(null, FilenameUtils.normalizeNoEndSeparator("~user/../a"));
        assertEquals(null, FilenameUtils.normalizeNoEndSeparator("~user/.."));
        assertEquals("~user" + SEP, FilenameUtils.normalizeNoEndSeparator("~user/"));
        assertEquals("~user" + SEP, FilenameUtils.normalizeNoEndSeparator("~user"));

        assertEquals("C:" + SEP + "a", FilenameUtils.normalizeNoEndSeparator("C:/a"));
        assertEquals("C:" + SEP + "a", FilenameUtils.normalizeNoEndSeparator("C:/a/"));
        assertEquals("C:" + SEP + "a" + SEP + "c", FilenameUtils.normalizeNoEndSeparator("C:/a/b/../c"));
        assertEquals("C:" + SEP + "c", FilenameUtils.normalizeNoEndSeparator("C:/a/b/../../c"));
        assertEquals(null, FilenameUtils.normalizeNoEndSeparator("C:/a/b/../../../c"));
        assertEquals("C:" + SEP + "a", FilenameUtils.normalizeNoEndSeparator("C:/a/b/.."));
        assertEquals("C:" + SEP + "", FilenameUtils.normalizeNoEndSeparator("C:/a/b/../.."));
        assertEquals(null, FilenameUtils.normalizeNoEndSeparator("C:/a/b/../../.."));
        assertEquals("C:" + SEP + "a" + SEP + "d", FilenameUtils.normalizeNoEndSeparator("C:/a/b/../c/../d"));
        assertEquals("C:" + SEP + "a" + SEP + "b" + SEP + "d", FilenameUtils.normalizeNoEndSeparator("C:/a/b//d"));
        assertEquals("C:" + SEP + "a" + SEP + "b", FilenameUtils.normalizeNoEndSeparator("C:/a/b/././."));
        assertEquals("C:" + SEP + "a", FilenameUtils.normalizeNoEndSeparator("C:/./a"));
        assertEquals("C:" + SEP + "", FilenameUtils.normalizeNoEndSeparator("C:/./"));
        assertEquals("C:" + SEP + "", FilenameUtils.normalizeNoEndSeparator("C:/."));
        assertEquals(null, FilenameUtils.normalizeNoEndSeparator("C:/../a"));
        assertEquals(null, FilenameUtils.normalizeNoEndSeparator("C:/.."));
        assertEquals("C:" + SEP + "", FilenameUtils.normalizeNoEndSeparator("C:/"));

        assertEquals("C:" + "a", FilenameUtils.normalizeNoEndSeparator("C:a"));
        assertEquals("C:" + "a", FilenameUtils.normalizeNoEndSeparator("C:a/"));
        assertEquals("C:" + "a" + SEP + "c", FilenameUtils.normalizeNoEndSeparator("C:a/b/../c"));
        assertEquals("C:" + "c", FilenameUtils.normalizeNoEndSeparator("C:a/b/../../c"));
        assertEquals(null, FilenameUtils.normalizeNoEndSeparator("C:a/b/../../../c"));
        assertEquals("C:" + "a", FilenameUtils.normalizeNoEndSeparator("C:a/b/.."));
        assertEquals("C:" + "", FilenameUtils.normalizeNoEndSeparator("C:a/b/../.."));
        assertEquals(null, FilenameUtils.normalizeNoEndSeparator("C:a/b/../../.."));
        assertEquals("C:" + "a" + SEP + "d", FilenameUtils.normalizeNoEndSeparator("C:a/b/../c/../d"));
        assertEquals("C:" + "a" + SEP + "b" + SEP + "d", FilenameUtils.normalizeNoEndSeparator("C:a/b//d"));
        assertEquals("C:" + "a" + SEP + "b", FilenameUtils.normalizeNoEndSeparator("C:a/b/././."));
        assertEquals("C:" + "a", FilenameUtils.normalizeNoEndSeparator("C:./a"));
        assertEquals("C:" + "", FilenameUtils.normalizeNoEndSeparator("C:./"));
        assertEquals("C:" + "", FilenameUtils.normalizeNoEndSeparator("C:."));
        assertEquals(null, FilenameUtils.normalizeNoEndSeparator("C:../a"));
        assertEquals(null, FilenameUtils.normalizeNoEndSeparator("C:.."));
        assertEquals("C:" + "", FilenameUtils.normalizeNoEndSeparator("C:"));

        assertEquals(SEP + SEP + "server" + SEP + "a", FilenameUtils.normalizeNoEndSeparator("//server/a"));
        assertEquals(SEP + SEP + "server" + SEP + "a", FilenameUtils.normalizeNoEndSeparator("//server/a/"));
        assertEquals(SEP + SEP + "server" + SEP + "a" + SEP + "c", FilenameUtils.normalizeNoEndSeparator("//server/a/b/../c"));
        assertEquals(SEP + SEP + "server" + SEP + "c", FilenameUtils.normalizeNoEndSeparator("//server/a/b/../../c"));
        assertEquals(null, FilenameUtils.normalizeNoEndSeparator("//server/a/b/../../../c"));
        assertEquals(SEP + SEP + "server" + SEP + "a", FilenameUtils.normalizeNoEndSeparator("//server/a/b/.."));
        assertEquals(SEP + SEP + "server" + SEP + "", FilenameUtils.normalizeNoEndSeparator("//server/a/b/../.."));
        assertEquals(null, FilenameUtils.normalizeNoEndSeparator("//server/a/b/../../.."));
        assertEquals(SEP + SEP + "server" + SEP + "a" + SEP + "d", FilenameUtils.normalizeNoEndSeparator("//server/a/b/../c/../d"));
        assertEquals(SEP + SEP + "server" + SEP + "a" + SEP + "b" + SEP + "d", FilenameUtils.normalizeNoEndSeparator("//server/a/b//d"));
        assertEquals(SEP + SEP + "server" + SEP + "a" + SEP + "b", FilenameUtils.normalizeNoEndSeparator("//server/a/b/././."));
        assertEquals(SEP + SEP + "server" + SEP + "a", FilenameUtils.normalizeNoEndSeparator("//server/./a"));
        assertEquals(SEP + SEP + "server" + SEP + "", FilenameUtils.normalizeNoEndSeparator("//server/./"));
        assertEquals(SEP + SEP + "server" + SEP + "", FilenameUtils.normalizeNoEndSeparator("//server/."));
        assertEquals(null, FilenameUtils.normalizeNoEndSeparator("//server/../a"));
        assertEquals(null, FilenameUtils.normalizeNoEndSeparator("//server/.."));
        assertEquals(SEP + SEP + "server" + SEP + "", FilenameUtils.normalizeNoEndSeparator("//server/"));
    }

    @Test
	public void testNormalizeNoEndSeparatorUnixWin() throws Exception {
		this.filenameUtilsTestCaseTestNormalizeUnixWinTemplate(
				new FilenameUtilsTestCaseTestNormalizeNoEndSeparatorUnixWinAdapterImpl(), "/a/c", "/a/c", "\\a\\c",
				"\\a\\c");
	}

    //-----------------------------------------------------------------------
    @Test
    public void testConcat() {
        assertEquals(null, FilenameUtils.concat("", null));
        assertEquals(null, FilenameUtils.concat(null, null));
        assertEquals(null, FilenameUtils.concat(null, ""));
        assertEquals(null, FilenameUtils.concat(null, "a"));
        assertEquals(SEP + "a", FilenameUtils.concat(null, "/a"));

        assertEquals(null, FilenameUtils.concat("", ":")); // invalid prefix
        assertEquals(null, FilenameUtils.concat(":", "")); // invalid prefix

        assertEquals("f" + SEP, FilenameUtils.concat("", "f/"));
        assertEquals("f", FilenameUtils.concat("", "f"));
        assertEquals("a" + SEP + "f" + SEP, FilenameUtils.concat("a/", "f/"));
        assertEquals("a" + SEP + "f", FilenameUtils.concat("a", "f"));
        assertEquals("a" + SEP + "b" + SEP + "f" + SEP, FilenameUtils.concat("a/b/", "f/"));
        assertEquals("a" + SEP + "b" + SEP + "f", FilenameUtils.concat("a/b", "f"));

        assertEquals("a" + SEP + "f" + SEP, FilenameUtils.concat("a/b/", "../f/"));
        assertEquals("a" + SEP + "f", FilenameUtils.concat("a/b", "../f"));
        assertEquals("a" + SEP + "c" + SEP + "g" + SEP, FilenameUtils.concat("a/b/../c/", "f/../g/"));
        assertEquals("a" + SEP + "c" + SEP + "g", FilenameUtils.concat("a/b/../c", "f/../g"));

        assertEquals("a" + SEP + "c.txt" + SEP + "f", FilenameUtils.concat("a/c.txt", "f"));

        assertEquals(SEP + "f" + SEP, FilenameUtils.concat("", "/f/"));
        assertEquals(SEP + "f", FilenameUtils.concat("", "/f"));
        assertEquals(SEP + "f" + SEP, FilenameUtils.concat("a/", "/f/"));
        assertEquals(SEP + "f", FilenameUtils.concat("a", "/f"));

        assertEquals(SEP + "c" + SEP + "d", FilenameUtils.concat("a/b/", "/c/d"));
        assertEquals("C:c" + SEP + "d", FilenameUtils.concat("a/b/", "C:c/d"));
        assertEquals("C:" + SEP + "c" + SEP + "d", FilenameUtils.concat("a/b/", "C:/c/d"));
        assertEquals("~" + SEP + "c" + SEP + "d", FilenameUtils.concat("a/b/", "~/c/d"));
        assertEquals("~user" + SEP + "c" + SEP + "d", FilenameUtils.concat("a/b/", "~user/c/d"));
        assertEquals("~" + SEP, FilenameUtils.concat("a/b/", "~"));
        assertEquals("~user" + SEP, FilenameUtils.concat("a/b/", "~user"));
    }

    //-----------------------------------------------------------------------
    @Test
    public void testSeparatorsToUnix() {
        assertEquals(null, FilenameUtils.separatorsToUnix(null));
        assertEquals("/a/b/c", FilenameUtils.separatorsToUnix("/a/b/c"));
        assertEquals("/a/b/c.txt", FilenameUtils.separatorsToUnix("/a/b/c.txt"));
        assertEquals("/a/b/c", FilenameUtils.separatorsToUnix("/a/b\\c"));
        assertEquals("/a/b/c", FilenameUtils.separatorsToUnix("\\a\\b\\c"));
        assertEquals("D:/a/b/c", FilenameUtils.separatorsToUnix("D:\\a\\b\\c"));
    }

    @Test
    public void testSeparatorsToWindows() {
        assertEquals(null, FilenameUtils.separatorsToWindows(null));
        assertEquals("\\a\\b\\c", FilenameUtils.separatorsToWindows("\\a\\b\\c"));
        assertEquals("\\a\\b\\c.txt", FilenameUtils.separatorsToWindows("\\a\\b\\c.txt"));
        assertEquals("\\a\\b\\c", FilenameUtils.separatorsToWindows("\\a\\b/c"));
        assertEquals("\\a\\b\\c", FilenameUtils.separatorsToWindows("/a/b/c"));
        assertEquals("D:\\a\\b\\c", FilenameUtils.separatorsToWindows("D:/a/b/c"));
    }

    @Test
    public void testSeparatorsToSystem() {
        if (WINDOWS) {
            assertEquals(null, FilenameUtils.separatorsToSystem(null));
            assertEquals("\\a\\b\\c", FilenameUtils.separatorsToSystem("\\a\\b\\c"));
            assertEquals("\\a\\b\\c.txt", FilenameUtils.separatorsToSystem("\\a\\b\\c.txt"));
            assertEquals("\\a\\b\\c", FilenameUtils.separatorsToSystem("\\a\\b/c"));
            assertEquals("\\a\\b\\c", FilenameUtils.separatorsToSystem("/a/b/c"));
            assertEquals("D:\\a\\b\\c", FilenameUtils.separatorsToSystem("D:/a/b/c"));
        } else {
            assertEquals(null, FilenameUtils.separatorsToSystem(null));
            assertEquals("/a/b/c", FilenameUtils.separatorsToSystem("/a/b/c"));
            assertEquals("/a/b/c.txt", FilenameUtils.separatorsToSystem("/a/b/c.txt"));
            assertEquals("/a/b/c", FilenameUtils.separatorsToSystem("/a/b\\c"));
            assertEquals("/a/b/c", FilenameUtils.separatorsToSystem("\\a\\b\\c"));
            assertEquals("D:/a/b/c", FilenameUtils.separatorsToSystem("D:\\a\\b\\c"));
        }
    }

    //-----------------------------------------------------------------------
    @Test
    public void testGetPrefixLength() {
        assertEquals(-1, FilenameUtils.getPrefixLength(null));
        assertEquals(-1, FilenameUtils.getPrefixLength(":"));
        assertEquals(-1, FilenameUtils.getPrefixLength("1:\\a\\b\\c.txt"));
        assertEquals(-1, FilenameUtils.getPrefixLength("1:"));
        assertEquals(-1, FilenameUtils.getPrefixLength("1:a"));
        assertEquals(-1, FilenameUtils.getPrefixLength("\\\\\\a\\b\\c.txt"));
        assertEquals(-1, FilenameUtils.getPrefixLength("\\\\a"));

        assertEquals(0, FilenameUtils.getPrefixLength(""));
        assertEquals(1, FilenameUtils.getPrefixLength("\\"));
        assertEquals(2, FilenameUtils.getPrefixLength("C:"));
        assertEquals(3, FilenameUtils.getPrefixLength("C:\\"));
        assertEquals(9, FilenameUtils.getPrefixLength("//server/"));
        assertEquals(2, FilenameUtils.getPrefixLength("~"));
        assertEquals(2, FilenameUtils.getPrefixLength("~/"));
        assertEquals(6, FilenameUtils.getPrefixLength("~user"));
        assertEquals(6, FilenameUtils.getPrefixLength("~user/"));

        assertEquals(0, FilenameUtils.getPrefixLength("a\\b\\c.txt"));
        assertEquals(1, FilenameUtils.getPrefixLength("\\a\\b\\c.txt"));
        assertEquals(2, FilenameUtils.getPrefixLength("C:a\\b\\c.txt"));
        assertEquals(3, FilenameUtils.getPrefixLength("C:\\a\\b\\c.txt"));
        assertEquals(9, FilenameUtils.getPrefixLength("\\\\server\\a\\b\\c.txt"));

        assertEquals(0, FilenameUtils.getPrefixLength("a/b/c.txt"));
        assertEquals(1, FilenameUtils.getPrefixLength("/a/b/c.txt"));
        assertEquals(3, FilenameUtils.getPrefixLength("C:/a/b/c.txt"));
        assertEquals(9, FilenameUtils.getPrefixLength("//server/a/b/c.txt"));
        assertEquals(2, FilenameUtils.getPrefixLength("~/a/b/c.txt"));
        assertEquals(6, FilenameUtils.getPrefixLength("~user/a/b/c.txt"));

        assertEquals(0, FilenameUtils.getPrefixLength("a\\b\\c.txt"));
        assertEquals(1, FilenameUtils.getPrefixLength("\\a\\b\\c.txt"));
        assertEquals(2, FilenameUtils.getPrefixLength("~\\a\\b\\c.txt"));
        assertEquals(6, FilenameUtils.getPrefixLength("~user\\a\\b\\c.txt"));

        assertEquals(9, FilenameUtils.getPrefixLength("//server/a/b/c.txt"));
        assertEquals(-1, FilenameUtils.getPrefixLength("\\\\\\a\\b\\c.txt"));
        assertEquals(-1, FilenameUtils.getPrefixLength("///a/b/c.txt"));
    }

    @Test
    public void testIndexOfLastSeparator() {
        assertEquals(-1, FilenameUtils.indexOfLastSeparator(null));
        assertEquals(-1, FilenameUtils.indexOfLastSeparator("noseperator.inthispath"));
        assertEquals(3, FilenameUtils.indexOfLastSeparator("a/b/c"));
        assertEquals(3, FilenameUtils.indexOfLastSeparator("a\\b\\c"));
    }

    @Test
    public void testIndexOfExtension() {
        assertEquals(-1, FilenameUtils.indexOfExtension(null));
        assertEquals(-1, FilenameUtils.indexOfExtension("file"));
        assertEquals(4, FilenameUtils.indexOfExtension("file.txt"));
        assertEquals(13, FilenameUtils.indexOfExtension("a.txt/b.txt/c.txt"));
        assertEquals(-1, FilenameUtils.indexOfExtension("a/b/c"));
        assertEquals(-1, FilenameUtils.indexOfExtension("a\\b\\c"));
        assertEquals(-1, FilenameUtils.indexOfExtension("a/b.notextension/c"));
        assertEquals(-1, FilenameUtils.indexOfExtension("a\\b.notextension\\c"));
    }

    //-----------------------------------------------------------------------
    @Test
    public void testGetPrefix() {
        assertEquals(null, FilenameUtils.getPrefix(null));
        assertEquals(null, FilenameUtils.getPrefix(":"));
        assertEquals(null, FilenameUtils.getPrefix("1:\\a\\b\\c.txt"));
        assertEquals(null, FilenameUtils.getPrefix("1:"));
        assertEquals(null, FilenameUtils.getPrefix("1:a"));
        assertEquals(null, FilenameUtils.getPrefix("\\\\\\a\\b\\c.txt"));
        assertEquals(null, FilenameUtils.getPrefix("\\\\a"));

        assertEquals("", FilenameUtils.getPrefix(""));
        assertEquals("\\", FilenameUtils.getPrefix("\\"));
        assertEquals("C:", FilenameUtils.getPrefix("C:"));
        assertEquals("C:\\", FilenameUtils.getPrefix("C:\\"));
        assertEquals("//server/", FilenameUtils.getPrefix("//server/"));
        assertEquals("~/", FilenameUtils.getPrefix("~"));
        assertEquals("~/", FilenameUtils.getPrefix("~/"));
        assertEquals("~user/", FilenameUtils.getPrefix("~user"));
        assertEquals("~user/", FilenameUtils.getPrefix("~user/"));

        assertEquals("", FilenameUtils.getPrefix("a\\b\\c.txt"));
        assertEquals("\\", FilenameUtils.getPrefix("\\a\\b\\c.txt"));
        assertEquals("C:\\", FilenameUtils.getPrefix("C:\\a\\b\\c.txt"));
        assertEquals("\\\\server\\", FilenameUtils.getPrefix("\\\\server\\a\\b\\c.txt"));

        assertEquals("", FilenameUtils.getPrefix("a/b/c.txt"));
        assertEquals("/", FilenameUtils.getPrefix("/a/b/c.txt"));
        assertEquals("C:/", FilenameUtils.getPrefix("C:/a/b/c.txt"));
        assertEquals("//server/", FilenameUtils.getPrefix("//server/a/b/c.txt"));
        assertEquals("~/", FilenameUtils.getPrefix("~/a/b/c.txt"));
        assertEquals("~user/", FilenameUtils.getPrefix("~user/a/b/c.txt"));

        assertEquals("", FilenameUtils.getPrefix("a\\b\\c.txt"));
        assertEquals("\\", FilenameUtils.getPrefix("\\a\\b\\c.txt"));
        assertEquals("~\\", FilenameUtils.getPrefix("~\\a\\b\\c.txt"));
        assertEquals("~user\\", FilenameUtils.getPrefix("~user\\a\\b\\c.txt"));
    }

    @Test
    public void testGetPrefix_with_nullbyte() {
        try {
            assertEquals("~user\\", FilenameUtils.getPrefix("~u\u0000ser\\a\\b\\c.txt"));
        } catch (IllegalArgumentException ignore) {

        }
    }

    @Test
    public void testGetPath() {
        assertEquals(null, FilenameUtils.getPath(null));
        assertEquals("", FilenameUtils.getPath("noseperator.inthispath"));
        assertEquals("", FilenameUtils.getPath("/noseperator.inthispath"));
        assertEquals("", FilenameUtils.getPath("\\noseperator.inthispath"));
        assertEquals("a/b/", FilenameUtils.getPath("a/b/c.txt"));
        assertEquals("a/b/", FilenameUtils.getPath("a/b/c"));
        assertEquals("a/b/c/", FilenameUtils.getPath("a/b/c/"));
        assertEquals("a\\b\\", FilenameUtils.getPath("a\\b\\c"));

        assertEquals(null, FilenameUtils.getPath(":"));
        assertEquals(null, FilenameUtils.getPath("1:/a/b/c.txt"));
        assertEquals(null, FilenameUtils.getPath("1:"));
        assertEquals(null, FilenameUtils.getPath("1:a"));
        assertEquals(null, FilenameUtils.getPath("///a/b/c.txt"));
        assertEquals(null, FilenameUtils.getPath("//a"));

        assertEquals("", FilenameUtils.getPath(""));
        assertEquals("", FilenameUtils.getPath("C:"));
        assertEquals("", FilenameUtils.getPath("C:/"));
        assertEquals("", FilenameUtils.getPath("//server/"));
        assertEquals("", FilenameUtils.getPath("~"));
        assertEquals("", FilenameUtils.getPath("~/"));
        assertEquals("", FilenameUtils.getPath("~user"));
        assertEquals("", FilenameUtils.getPath("~user/"));

        assertEquals("a/b/", FilenameUtils.getPath("a/b/c.txt"));
        assertEquals("a/b/", FilenameUtils.getPath("/a/b/c.txt"));
        assertEquals("", FilenameUtils.getPath("C:a"));
        assertEquals("a/b/", FilenameUtils.getPath("C:a/b/c.txt"));
        assertEquals("a/b/", FilenameUtils.getPath("C:/a/b/c.txt"));
        assertEquals("a/b/", FilenameUtils.getPath("//server/a/b/c.txt"));
        assertEquals("a/b/", FilenameUtils.getPath("~/a/b/c.txt"));
        assertEquals("a/b/", FilenameUtils.getPath("~user/a/b/c.txt"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetPath_with_nullbyte() {
        assertEquals("a/b/", FilenameUtils.getPath("~user/a/\u0000b/c.txt"));
    }


    @Test
    public void testGetPathNoEndSeparator() {
        assertEquals(null, FilenameUtils.getPath(null));
        assertEquals("", FilenameUtils.getPath("noseperator.inthispath"));
        assertEquals("", FilenameUtils.getPathNoEndSeparator("/noseperator.inthispath"));
        assertEquals("", FilenameUtils.getPathNoEndSeparator("\\noseperator.inthispath"));
        assertEquals("a/b", FilenameUtils.getPathNoEndSeparator("a/b/c.txt"));
        assertEquals("a/b", FilenameUtils.getPathNoEndSeparator("a/b/c"));
        assertEquals("a/b/c", FilenameUtils.getPathNoEndSeparator("a/b/c/"));
        assertEquals("a\\b", FilenameUtils.getPathNoEndSeparator("a\\b\\c"));

        assertEquals(null, FilenameUtils.getPathNoEndSeparator(":"));
        assertEquals(null, FilenameUtils.getPathNoEndSeparator("1:/a/b/c.txt"));
        assertEquals(null, FilenameUtils.getPathNoEndSeparator("1:"));
        assertEquals(null, FilenameUtils.getPathNoEndSeparator("1:a"));
        assertEquals(null, FilenameUtils.getPathNoEndSeparator("///a/b/c.txt"));
        assertEquals(null, FilenameUtils.getPathNoEndSeparator("//a"));

        assertEquals("", FilenameUtils.getPathNoEndSeparator(""));
        assertEquals("", FilenameUtils.getPathNoEndSeparator("C:"));
        assertEquals("", FilenameUtils.getPathNoEndSeparator("C:/"));
        assertEquals("", FilenameUtils.getPathNoEndSeparator("//server/"));
        assertEquals("", FilenameUtils.getPathNoEndSeparator("~"));
        assertEquals("", FilenameUtils.getPathNoEndSeparator("~/"));
        assertEquals("", FilenameUtils.getPathNoEndSeparator("~user"));
        assertEquals("", FilenameUtils.getPathNoEndSeparator("~user/"));

        assertEquals("a/b", FilenameUtils.getPathNoEndSeparator("a/b/c.txt"));
        assertEquals("a/b", FilenameUtils.getPathNoEndSeparator("/a/b/c.txt"));
        assertEquals("", FilenameUtils.getPathNoEndSeparator("C:a"));
        assertEquals("a/b", FilenameUtils.getPathNoEndSeparator("C:a/b/c.txt"));
        assertEquals("a/b", FilenameUtils.getPathNoEndSeparator("C:/a/b/c.txt"));
        assertEquals("a/b", FilenameUtils.getPathNoEndSeparator("//server/a/b/c.txt"));
        assertEquals("a/b", FilenameUtils.getPathNoEndSeparator("~/a/b/c.txt"));
        assertEquals("a/b", FilenameUtils.getPathNoEndSeparator("~user/a/b/c.txt"));
    }

    @Test
    public void testGetPathNoEndSeparator_with_null_byte() {
        try {
            assertEquals("a/b", FilenameUtils.getPathNoEndSeparator("~user/a\u0000/b/c.txt"));
        } catch (IllegalArgumentException ignore) {

        }
    }

    @Test
	public void testGetFullPath() {
		this.filenameUtilsTestCaseTestGetFullPathTemplate(new FilenameUtilsTestCaseTestGetFullPathAdapterImpl(), "a/b/",
				"a/b/", "a/b/c/", "a\\b\\", "~/", "~user/", "a/b/", "/a/b/", "C:a/b/", "C:/a/b/", "//server/a/b/",
				"~/a/b/", "~user/a/b/");
	}

    @Test
	public void testGetFullPathNoEndSeparator() {
		this.filenameUtilsTestCaseTestGetFullPathTemplate(
				new FilenameUtilsTestCaseTestGetFullPathNoEndSeparatorAdapterImpl(), "a/b", "a/b", "a/b/c", "a\\b", "~",
				"~user", "a/b", "/a/b", "C:a/b", "C:/a/b", "//server/a/b", "~/a/b", "~user/a/b");
	}

    /**
     * Test for https://issues.apache.org/jira/browse/IO-248
     */
    @Test
    public void testGetFullPathNoEndSeparator_IO_248() {

        // Test single separator
        assertEquals("/", FilenameUtils.getFullPathNoEndSeparator("/"));
        assertEquals("\\", FilenameUtils.getFullPathNoEndSeparator("\\"));

        // Test one level directory
        assertEquals("/", FilenameUtils.getFullPathNoEndSeparator("/abc"));
        assertEquals("\\", FilenameUtils.getFullPathNoEndSeparator("\\abc"));

        // Test one level directory
        assertEquals("/abc", FilenameUtils.getFullPathNoEndSeparator("/abc/xyz"));
        assertEquals("\\abc", FilenameUtils.getFullPathNoEndSeparator("\\abc\\xyz"));
    }

    @Test
    public void testGetName() {
        assertEquals(null, FilenameUtils.getName(null));
        assertEquals("noseperator.inthispath", FilenameUtils.getName("noseperator.inthispath"));
        assertEquals("c.txt", FilenameUtils.getName("a/b/c.txt"));
        assertEquals("c", FilenameUtils.getName("a/b/c"));
        assertEquals("", FilenameUtils.getName("a/b/c/"));
        assertEquals("c", FilenameUtils.getName("a\\b\\c"));
    }

    @Test
    public void testInjectionFailure() {
        try {
            assertEquals("c", FilenameUtils.getName("a\\b\\\u0000c"));
        } catch (IllegalArgumentException ignore) {

        }
    }

    @Test
    public void testGetBaseName() {
        assertEquals(null, FilenameUtils.getBaseName(null));
        assertEquals("noseperator", FilenameUtils.getBaseName("noseperator.inthispath"));
        assertEquals("c", FilenameUtils.getBaseName("a/b/c.txt"));
        assertEquals("c", FilenameUtils.getBaseName("a/b/c"));
        assertEquals("", FilenameUtils.getBaseName("a/b/c/"));
        assertEquals("c", FilenameUtils.getBaseName("a\\b\\c"));
        assertEquals("file.txt", FilenameUtils.getBaseName("file.txt.bak"));
    }

    @Test
    public void testGetBaseName_with_nullByte() {
        try {
            assertEquals("file.txt", FilenameUtils.getBaseName("fil\u0000e.txt.bak"));
        } catch (IllegalArgumentException ignore) {

        }
    }

    @Test
	public void testGetExtension() {
		this.filenameUtilsTestCaseTestExtensionTemplate(new FilenameUtilsTestCaseTestGetExtensionAdapterImpl(), "ext",
				"", "com", "jpeg", "", "txt", "", "", "txt", "", "", "ext");
	}

    @Test
	public void testRemoveExtension() {
		this.filenameUtilsTestCaseTestExtensionTemplate(new FilenameUtilsTestCaseTestRemoveExtensionAdapterImpl(),
				"file", "README", "domain.dot", "image", "a.b/c", "a.b/c", "a/b/c", "a.b\\c", "a.b\\c", "a\\b\\c",
				"C:\\temp\\foo.bar\\README", "../filename");
	}

    //-----------------------------------------------------------------------
    @Test
    public void testEquals() {
        assertTrue(FilenameUtils.equals(null, null));
        assertFalse(FilenameUtils.equals(null, ""));
        assertFalse(FilenameUtils.equals("", null));
        assertTrue(FilenameUtils.equals("", ""));
        assertTrue(FilenameUtils.equals("file.txt", "file.txt"));
        assertFalse(FilenameUtils.equals("file.txt", "FILE.TXT"));
        assertFalse(FilenameUtils.equals("a\\b\\file.txt", "a/b/file.txt"));
    }

    @Test
    public void testEqualsOnSystem() {
        assertTrue(FilenameUtils.equalsOnSystem(null, null));
        assertFalse(FilenameUtils.equalsOnSystem(null, ""));
        assertFalse(FilenameUtils.equalsOnSystem("", null));
        assertTrue(FilenameUtils.equalsOnSystem("", ""));
        assertTrue(FilenameUtils.equalsOnSystem("file.txt", "file.txt"));
        assertEquals(WINDOWS, FilenameUtils.equalsOnSystem("file.txt", "FILE.TXT"));
        assertFalse(FilenameUtils.equalsOnSystem("a\\b\\file.txt", "a/b/file.txt"));
    }

    //-----------------------------------------------------------------------
    @Test
    public void testEqualsNormalized() {
        assertTrue(FilenameUtils.equalsNormalized(null, null));
        assertFalse(FilenameUtils.equalsNormalized(null, ""));
        assertFalse(FilenameUtils.equalsNormalized("", null));
        assertTrue(FilenameUtils.equalsNormalized("", ""));
        assertTrue(FilenameUtils.equalsNormalized("file.txt", "file.txt"));
        assertFalse(FilenameUtils.equalsNormalized("file.txt", "FILE.TXT"));
        assertTrue(FilenameUtils.equalsNormalized("a\\b\\file.txt", "a/b/file.txt"));
        assertFalse(FilenameUtils.equalsNormalized("a/b/", "a/b"));
    }

    @Test
    public void testEqualsNormalizedOnSystem() {
        assertTrue(FilenameUtils.equalsNormalizedOnSystem(null, null));
        assertFalse(FilenameUtils.equalsNormalizedOnSystem(null, ""));
        assertFalse(FilenameUtils.equalsNormalizedOnSystem("", null));
        assertTrue(FilenameUtils.equalsNormalizedOnSystem("", ""));
        assertTrue(FilenameUtils.equalsNormalizedOnSystem("file.txt", "file.txt"));
        assertEquals(WINDOWS, FilenameUtils.equalsNormalizedOnSystem("file.txt", "FILE.TXT"));
        assertTrue(FilenameUtils.equalsNormalizedOnSystem("a\\b\\file.txt", "a/b/file.txt"));
        assertFalse(FilenameUtils.equalsNormalizedOnSystem("a/b/", "a/b"));
    }

    /**
     * Test for https://issues.apache.org/jira/browse/IO-128
     */
    @Test
    public void testEqualsNormalizedError_IO_128() {
        try {
            FilenameUtils.equalsNormalizedOnSystem("//file.txt", "file.txt");
            fail("Invalid normalized first file");
        } catch (final NullPointerException e) {
            // expected result
        }
        try {
            FilenameUtils.equalsNormalizedOnSystem("file.txt", "//file.txt");
            fail("Invalid normalized second file");
        } catch (final NullPointerException e) {
            // expected result
        }
        try {
            FilenameUtils.equalsNormalizedOnSystem("//file.txt", "//file.txt");
            fail("Invalid normalized both filse");
        } catch (final NullPointerException e) {
            // expected result
        }
    }

    @Test
    public void testEquals_fullControl() {
        assertFalse(FilenameUtils.equals("file.txt", "FILE.TXT", true, IOCase.SENSITIVE));
        assertTrue(FilenameUtils.equals("file.txt", "FILE.TXT", true, IOCase.INSENSITIVE));
        assertEquals(WINDOWS, FilenameUtils.equals("file.txt", "FILE.TXT", true, IOCase.SYSTEM));
        assertFalse(FilenameUtils.equals("file.txt", "FILE.TXT", true, null));
    }

    //-----------------------------------------------------------------------
    @Test
    public void testIsExtension() {
        assertFalse(FilenameUtils.isExtension(null, (String) null));
        assertFalse(FilenameUtils.isExtension("file.txt", (String) null));
        assertTrue(FilenameUtils.isExtension("file", (String) null));
        assertFalse(FilenameUtils.isExtension("file.txt", ""));
        assertTrue(FilenameUtils.isExtension("file", ""));
        assertTrue(FilenameUtils.isExtension("file.txt", "txt"));
        assertFalse(FilenameUtils.isExtension("file.txt", "rtf"));

        assertFalse(FilenameUtils.isExtension("a/b/file.txt", (String) null));
        assertFalse(FilenameUtils.isExtension("a/b/file.txt", ""));
        assertTrue(FilenameUtils.isExtension("a/b/file.txt", "txt"));
        assertFalse(FilenameUtils.isExtension("a/b/file.txt", "rtf"));

        assertFalse(FilenameUtils.isExtension("a.b/file.txt", (String) null));
        assertFalse(FilenameUtils.isExtension("a.b/file.txt", ""));
        assertTrue(FilenameUtils.isExtension("a.b/file.txt", "txt"));
        assertFalse(FilenameUtils.isExtension("a.b/file.txt", "rtf"));

        assertFalse(FilenameUtils.isExtension("a\\b\\file.txt", (String) null));
        assertFalse(FilenameUtils.isExtension("a\\b\\file.txt", ""));
        assertTrue(FilenameUtils.isExtension("a\\b\\file.txt", "txt"));
        assertFalse(FilenameUtils.isExtension("a\\b\\file.txt", "rtf"));

        assertFalse(FilenameUtils.isExtension("a.b\\file.txt", (String) null));
        assertFalse(FilenameUtils.isExtension("a.b\\file.txt", ""));
        assertTrue(FilenameUtils.isExtension("a.b\\file.txt", "txt"));
        assertFalse(FilenameUtils.isExtension("a.b\\file.txt", "rtf"));

        assertFalse(FilenameUtils.isExtension("a.b\\file.txt", "TXT"));
    }

    @Test
    public void testIsExtension_injection() {
        try {
            FilenameUtils.isExtension("a.b\\fi\u0000le.txt", "TXT");
            fail("Should throw IAE");
        } catch (IllegalArgumentException ignore) {
        }
    }

    @Test
    public void testIsExtensionArray() {
        assertFalse(FilenameUtils.isExtension(null, (String[]) null));
        assertFalse(FilenameUtils.isExtension("file.txt", (String[]) null));
        assertTrue(FilenameUtils.isExtension("file", (String[]) null));
        assertFalse(FilenameUtils.isExtension("file.txt", new String[0]));
        assertTrue(FilenameUtils.isExtension("file.txt", new String[]{"txt"}));
        assertFalse(FilenameUtils.isExtension("file.txt", new String[]{"rtf"}));
        assertTrue(FilenameUtils.isExtension("file", new String[]{"rtf", ""}));
        assertTrue(FilenameUtils.isExtension("file.txt", new String[]{"rtf", "txt"}));

        assertFalse(FilenameUtils.isExtension("a/b/file.txt", (String[]) null));
        assertFalse(FilenameUtils.isExtension("a/b/file.txt", new String[0]));
        assertTrue(FilenameUtils.isExtension("a/b/file.txt", new String[]{"txt"}));
        assertFalse(FilenameUtils.isExtension("a/b/file.txt", new String[]{"rtf"}));
        assertTrue(FilenameUtils.isExtension("a/b/file.txt", new String[]{"rtf", "txt"}));

        assertFalse(FilenameUtils.isExtension("a.b/file.txt", (String[]) null));
        assertFalse(FilenameUtils.isExtension("a.b/file.txt", new String[0]));
        assertTrue(FilenameUtils.isExtension("a.b/file.txt", new String[]{"txt"}));
        assertFalse(FilenameUtils.isExtension("a.b/file.txt", new String[]{"rtf"}));
        assertTrue(FilenameUtils.isExtension("a.b/file.txt", new String[]{"rtf", "txt"}));

        assertFalse(FilenameUtils.isExtension("a\\b\\file.txt", (String[]) null));
        assertFalse(FilenameUtils.isExtension("a\\b\\file.txt", new String[0]));
        assertTrue(FilenameUtils.isExtension("a\\b\\file.txt", new String[]{"txt"}));
        assertFalse(FilenameUtils.isExtension("a\\b\\file.txt", new String[]{"rtf"}));
        assertTrue(FilenameUtils.isExtension("a\\b\\file.txt", new String[]{"rtf", "txt"}));

        assertFalse(FilenameUtils.isExtension("a.b\\file.txt", (String[]) null));
        assertFalse(FilenameUtils.isExtension("a.b\\file.txt", new String[0]));
        assertTrue(FilenameUtils.isExtension("a.b\\file.txt", new String[]{"txt"}));
        assertFalse(FilenameUtils.isExtension("a.b\\file.txt", new String[]{"rtf"}));
        assertTrue(FilenameUtils.isExtension("a.b\\file.txt", new String[]{"rtf", "txt"}));

        assertFalse(FilenameUtils.isExtension("a.b\\file.txt", new String[]{"TXT"}));
        assertFalse(FilenameUtils.isExtension("a.b\\file.txt", new String[]{"TXT", "RTF"}));
    }

    @Test
    public void testIsExtensionCollection() {
        assertFalse(FilenameUtils.isExtension(null, (Collection<String>) null));
        assertFalse(FilenameUtils.isExtension("file.txt", (Collection<String>) null));
        assertTrue(FilenameUtils.isExtension("file", (Collection<String>) null));
        assertFalse(FilenameUtils.isExtension("file.txt", new ArrayList<String>()));
        assertTrue(FilenameUtils.isExtension("file.txt", new ArrayList<String>(Arrays.asList(new String[]{"txt"}))));
        assertFalse(FilenameUtils.isExtension("file.txt", new ArrayList<String>(Arrays.asList(new String[]{"rtf"}))));
        assertTrue(FilenameUtils.isExtension("file", new ArrayList<String>(Arrays.asList(new String[]{"rtf", ""}))));
        assertTrue(FilenameUtils.isExtension("file.txt", new ArrayList<String>(Arrays.asList(new String[]{"rtf", "txt"}))));

        assertFalse(FilenameUtils.isExtension("a/b/file.txt", (Collection<String>) null));
        assertFalse(FilenameUtils.isExtension("a/b/file.txt", new ArrayList<String>()));
        assertTrue(FilenameUtils.isExtension("a/b/file.txt", new ArrayList<String>(Arrays.asList(new String[]{"txt"}))));
        assertFalse(FilenameUtils.isExtension("a/b/file.txt", new ArrayList<String>(Arrays.asList(new String[]{"rtf"}))));
        assertTrue(FilenameUtils.isExtension("a/b/file.txt", new ArrayList<String>(Arrays.asList(new String[]{"rtf", "txt"}))));

        assertFalse(FilenameUtils.isExtension("a.b/file.txt", (Collection<String>) null));
        assertFalse(FilenameUtils.isExtension("a.b/file.txt", new ArrayList<String>()));
        assertTrue(FilenameUtils.isExtension("a.b/file.txt", new ArrayList<String>(Arrays.asList(new String[]{"txt"}))));
        assertFalse(FilenameUtils.isExtension("a.b/file.txt", new ArrayList<String>(Arrays.asList(new String[]{"rtf"}))));
        assertTrue(FilenameUtils.isExtension("a.b/file.txt", new ArrayList<String>(Arrays.asList(new String[]{"rtf", "txt"}))));

        assertFalse(FilenameUtils.isExtension("a\\b\\file.txt", (Collection<String>) null));
        assertFalse(FilenameUtils.isExtension("a\\b\\file.txt", new ArrayList<String>()));
        assertTrue(FilenameUtils.isExtension("a\\b\\file.txt", new ArrayList<String>(Arrays.asList(new String[]{"txt"}))));
        assertFalse(FilenameUtils.isExtension("a\\b\\file.txt", new ArrayList<String>(Arrays.asList(new String[]{"rtf"}))));
        assertTrue(FilenameUtils.isExtension("a\\b\\file.txt", new ArrayList<String>(Arrays.asList(new String[]{"rtf", "txt"}))));

        assertFalse(FilenameUtils.isExtension("a.b\\file.txt", (Collection<String>) null));
        assertFalse(FilenameUtils.isExtension("a.b\\file.txt", new ArrayList<String>()));
        assertTrue(FilenameUtils.isExtension("a.b\\file.txt", new ArrayList<String>(Arrays.asList(new String[]{"txt"}))));
        assertFalse(FilenameUtils.isExtension("a.b\\file.txt", new ArrayList<String>(Arrays.asList(new String[]{"rtf"}))));
        assertTrue(FilenameUtils.isExtension("a.b\\file.txt", new ArrayList<String>(Arrays.asList(new String[]{"rtf", "txt"}))));

        assertFalse(FilenameUtils.isExtension("a.b\\file.txt", new ArrayList<String>(Arrays.asList(new String[]{"TXT"}))));
        assertFalse(FilenameUtils.isExtension("a.b\\file.txt", new ArrayList<String>(Arrays.asList(new String[]{"TXT", "RTF"}))));
    }

	public void filenameUtilsTestCaseTestGetFullPathTemplate(FilenameUtilsTestCaseTestGetFullPathAdapter adapter,
			String string1, String string2, String string3, String string4, String string5, String string6,
			String string7, String string8, String string9, String string10, String string11, String string12,
			String string13) {
		assertEquals(null, adapter.getFullPath(null));
		assertEquals("", adapter.getFullPath("noseperator.inthispath"));
		assertEquals(string1, adapter.getFullPath("a/b/c.txt"));
		assertEquals(string2, adapter.getFullPath("a/b/c"));
		assertEquals(string3, adapter.getFullPath("a/b/c/"));
		assertEquals(string4, adapter.getFullPath("a\\b\\c"));
		assertEquals(null, adapter.getFullPath(":"));
		assertEquals(null, adapter.getFullPath("1:/a/b/c.txt"));
		assertEquals(null, adapter.getFullPath("1:"));
		assertEquals(null, adapter.getFullPath("1:a"));
		assertEquals(null, adapter.getFullPath("///a/b/c.txt"));
		assertEquals(null, adapter.getFullPath("//a"));
		assertEquals("", adapter.getFullPath(""));
		assertEquals("C:", adapter.getFullPath("C:"));
		assertEquals("C:/", adapter.getFullPath("C:/"));
		assertEquals("//server/", adapter.getFullPath("//server/"));
		assertEquals(string5, adapter.getFullPath("~"));
		assertEquals("~/", adapter.getFullPath("~/"));
		assertEquals(string6, adapter.getFullPath("~user"));
		assertEquals("~user/", adapter.getFullPath("~user/"));
		assertEquals(string7, adapter.getFullPath("a/b/c.txt"));
		assertEquals(string8, adapter.getFullPath("/a/b/c.txt"));
		assertEquals("C:", adapter.getFullPath("C:a"));
		assertEquals(string9, adapter.getFullPath("C:a/b/c.txt"));
		assertEquals(string10, adapter.getFullPath("C:/a/b/c.txt"));
		assertEquals(string11, adapter.getFullPath("//server/a/b/c.txt"));
		assertEquals(string12, adapter.getFullPath("~/a/b/c.txt"));
		assertEquals(string13, adapter.getFullPath("~user/a/b/c.txt"));
	}

	interface FilenameUtilsTestCaseTestGetFullPathAdapter {
		String getFullPath(String string1);
	}

	class FilenameUtilsTestCaseTestGetFullPathAdapterImpl implements FilenameUtilsTestCaseTestGetFullPathAdapter {
		public String getFullPath(String string1) {
			return FilenameUtils.getFullPath(string1);
		}
	}

	class FilenameUtilsTestCaseTestGetFullPathNoEndSeparatorAdapterImpl
			implements FilenameUtilsTestCaseTestGetFullPathAdapter {
		public String getFullPath(String string1) {
			return FilenameUtils.getFullPathNoEndSeparator(string1);
		}
	}

	public void filenameUtilsTestCaseTestExtensionTemplate(FilenameUtilsTestCaseTestExtensionAdapter adapter,
			String string1, String string2, String string3, String string4, String string5, String string6,
			String string7, String string8, String string9, String string10, String string11, String string12) {
		assertEquals(null, adapter.extension(null));
		assertEquals(string1, adapter.extension("file.ext"));
		assertEquals(string2, adapter.extension("README"));
		assertEquals(string3, adapter.extension("domain.dot.com"));
		assertEquals(string4, adapter.extension("image.jpeg"));
		assertEquals(string5, adapter.extension("a.b/c"));
		assertEquals(string6, adapter.extension("a.b/c.txt"));
		assertEquals(string7, adapter.extension("a/b/c"));
		assertEquals(string8, adapter.extension("a.b\\c"));
		assertEquals(string9, adapter.extension("a.b\\c.txt"));
		assertEquals(string10, adapter.extension("a\\b\\c"));
		assertEquals(string11, adapter.extension("C:\\temp\\foo.bar\\README"));
		assertEquals(string12, adapter.extension("../filename.ext"));
	}

	interface FilenameUtilsTestCaseTestExtensionAdapter {
		String extension(String string1);
	}

	class FilenameUtilsTestCaseTestGetExtensionAdapterImpl implements FilenameUtilsTestCaseTestExtensionAdapter {
		public String extension(String string1) {
			return FilenameUtils.getExtension(string1);
		}
	}

	class FilenameUtilsTestCaseTestRemoveExtensionAdapterImpl implements FilenameUtilsTestCaseTestExtensionAdapter {
		public String extension(String string1) {
			return FilenameUtils.removeExtension(string1);
		}
	}

	public void filenameUtilsTestCaseTestNormalizeUnixWinTemplate(
			FilenameUtilsTestCaseTestNormalizeUnixWinAdapter adapter, String string1, String string2, String string3,
			String string4) throws Exception {
		assertEquals(string1, adapter.normalize("/a/b/../c/", true));
		assertEquals(string2, adapter.normalize("\\a\\b\\..\\c\\", true));
		assertEquals(string3, adapter.normalize("/a/b/../c/", false));
		assertEquals(string4, adapter.normalize("\\a\\b\\..\\c\\", false));
	}

	interface FilenameUtilsTestCaseTestNormalizeUnixWinAdapter {
		String normalize(String string1, boolean b1);
	}

	class FilenameUtilsTestCaseTestNormalizeUnixWinAdapterImpl
			implements FilenameUtilsTestCaseTestNormalizeUnixWinAdapter {
		public String normalize(String string1, boolean b1) {
			return FilenameUtils.normalize(string1, b1);
		}
	}

	class FilenameUtilsTestCaseTestNormalizeNoEndSeparatorUnixWinAdapterImpl
			implements FilenameUtilsTestCaseTestNormalizeUnixWinAdapter {
		public String normalize(String string1, boolean b1) {
			return FilenameUtils.normalizeNoEndSeparator(string1, b1);
		}
	}

}
