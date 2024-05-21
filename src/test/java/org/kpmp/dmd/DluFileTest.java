package org.kpmp.dmd;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kpmp.packages.Attachment;

import static org.junit.Assert.assertEquals;

public class DluFileTest {

    DluFile dluFile;

    @Before
    public void setUp() throws Exception {
        dluFile = new DluFile();
    }

    @After
    public void tearDown() throws Exception {
        dluFile = null;
    }

    @Test
    public void testConstructor() {
        Attachment attachment = new Attachment();
        attachment.setOriginalFileName("filename");
        attachment.setSize(12345);
        attachment.setId("123456");
        attachment.setMd5checksum("checksum");
        DluFile file = new DluFile(attachment, "packageId");
        assertEquals(12345, file.getDluFileSize());
        assertEquals("123456", file.getDluFileId());
        assertEquals("checksum", file.getDluMd5Checksum());
        assertEquals("packageId", file.getDluPackageId());
    }

}
