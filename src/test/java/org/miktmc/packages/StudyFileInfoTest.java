package org.miktmc.packages;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class StudyFileInfoTest {
    
    private StudyFileInfo studyFileInfo;

    @Before
    public void setUp() throws Exception {
        studyFileInfo = new StudyFileInfo();
    }

    @After
    public void tearDown() throws Exception {
        studyFileInfo = null;
    }

    @Test
    public void testStudy() {
        studyFileInfo.setStudy("study");
        assertEquals("study", studyFileInfo.getStudy());
    }

    @Test
    public void testFileCounter() {
        studyFileInfo.setFileCounter(59);
        assertEquals(59, studyFileInfo.getFileCounter());
    }

    @Test
    public void testUploadSourceLetter() {
        studyFileInfo.setUploadSourceLetter("F");
        assertEquals("F", studyFileInfo.getUploadSourceLetter());
    }

    @Test
    public void testShouldRename() {
        studyFileInfo.setShouldRename(true);
        assertEquals(true, studyFileInfo.getShouldRename());
    }
}
