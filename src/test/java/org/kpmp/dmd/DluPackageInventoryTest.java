package org.kpmp.dmd;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.kpmp.packages.Package;
import org.kpmp.users.User;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DluPackageInventoryTest {

    DluPackageInventory dluPackageInventory;

    @BeforeEach
    public void setUp() throws Exception {
        dluPackageInventory = new DluPackageInventory();
    }

    @AfterEach
    public void tearDown() throws Exception {
        dluPackageInventory = null;
    }

    @Test
    public void testConstructorDluTrue() {
        Date now = new Date();
        User user = new User();
        user.setDisplayName("name");
        Package myPackage = new Package();
        myPackage.setPackageId("123");
        myPackage.setPackageType("type");
        myPackage.setTisName("tis");
        myPackage.setCreatedAt(now);
        myPackage.setSubmitter(user);
        myPackage.setSubjectId("subjid");
        myPackage.setLargeFilesChecked(true);
        myPackage.setUploadType("uploadType");
        DluPackageInventory dluPackageInventory = new DluPackageInventory(myPackage);
        assertEquals("123", dluPackageInventory.getDluPackageId());
        assertEquals("name", dluPackageInventory.getDluSubmitter());
        assertEquals("type", dluPackageInventory.getDluPackageType());
        assertEquals("tis", dluPackageInventory.getDluTis());
        assertEquals("name", dluPackageInventory.getDluSubmitter());
        assertEquals("subjid", dluPackageInventory.getDluSubjectId());
        assertEquals("uploadType", dluPackageInventory.getDluUploadType());
        assertTrue(dluPackageInventory.getDluLfu());
        assertEquals("N", dluPackageInventory.getUserPackageReady());
    }

    @Test
    public void testConstructorDluFalse() {
        Date now = new Date();
        User user = new User();
        user.setDisplayName("name");
        Package myPackage = new Package();
        myPackage.setPackageId("123");
        myPackage.setPackageType("type");
        myPackage.setTisName("tis");
        myPackage.setCreatedAt(now);
        myPackage.setSubmitter(user);
        myPackage.setSubjectId("subjid");
        myPackage.setLargeFilesChecked(false);
        myPackage.setUploadType("uploadType");
        DluPackageInventory dluPackageInventory = new DluPackageInventory(myPackage);
        assertEquals("123", dluPackageInventory.getDluPackageId());
        assertEquals("name", dluPackageInventory.getDluSubmitter());
        assertEquals("type", dluPackageInventory.getDluPackageType());
        assertEquals("tis", dluPackageInventory.getDluTis());
        assertEquals("name", dluPackageInventory.getDluSubmitter());
        assertEquals("subjid", dluPackageInventory.getDluSubjectId());
        assertEquals("uploadType", dluPackageInventory.getDluUploadType());
        assertFalse(dluPackageInventory.getDluLfu());
        assertEquals("Y", dluPackageInventory.getUserPackageReady());
    }

}
