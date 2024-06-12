package org.kpmp.dmd;

import org.kpmp.packages.Attachment;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

public class DluFileTest {

    DluFile dluFile;

    @BeforeEach
    public void setUp() throws Exception {
        dluFile = new DluFile();
    }

    @AfterEach
    public void tearDown() throws Exception {
        dluFile = null;
    }

    @Test
    public void testConstructor() {
        Attachment attachment = new Attachment();
        attachment.setFileName("filename");
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
