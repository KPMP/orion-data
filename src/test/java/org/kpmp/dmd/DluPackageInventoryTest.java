package org.kpmp.dmd;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kpmp.packages.Package;
import org.kpmp.users.User;
import org.mockito.MockitoAnnotations;

import java.util.Date;

import static org.junit.Assert.assertEquals;

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
    public void testConstructor() {
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
        DluPackageInventory dluPackageInventory = new DluPackageInventory(myPackage);
        assertEquals("123", dluPackageInventory.getDluPackageId());
        assertEquals("name", dluPackageInventory.getDluSubmitter());
        assertEquals("type", dluPackageInventory.getDluPackageType());
        assertEquals("tis", dluPackageInventory.getDluTis());
        assertEquals(user.getDisplayName(), dluPackageInventory.getDluSubmitter());
        assertEquals("subjid", dluPackageInventory.getDluSubjectId());
    }

}
