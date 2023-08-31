package org.kpmp.dmd;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kpmp.packages.Package;
import org.kpmp.users.User;
import org.mockito.MockitoAnnotations;

import java.util.Date;

import static org.junit.Assert.*;

public class DluPackageInventoryTest {

    DluPackageInventory dluPackageInventory;

    @Before
    public void setUp() throws Exception {
        dluPackageInventory = new DluPackageInventory();
    }

    @After
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
        DluPackageInventory dluPackageInventory = new DluPackageInventory(myPackage);
        assertEquals("123", dluPackageInventory.getDluPackageId());
        assertEquals("name", dluPackageInventory.getDluSubmitter());
        assertEquals("type", dluPackageInventory.getDluPackageType());
        assertEquals("tis", dluPackageInventory.getDluTis());
        assertEquals(user.getDisplayName(), dluPackageInventory.getDluSubmitter());
        assertEquals("subjid", dluPackageInventory.getDluSubjectId());
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
        DluPackageInventory dluPackageInventory = new DluPackageInventory(myPackage);
        assertEquals("123", dluPackageInventory.getDluPackageId());
        assertEquals("name", dluPackageInventory.getDluSubmitter());
        assertEquals("type", dluPackageInventory.getDluPackageType());
        assertEquals("tis", dluPackageInventory.getDluTis());
        assertEquals(user.getDisplayName(), dluPackageInventory.getDluSubmitter());
        assertEquals("subjid", dluPackageInventory.getDluSubjectId());
        assertFalse(dluPackageInventory.getDluLfu());
        assertEquals("Y", dluPackageInventory.getUserPackageReady());
    }

}
