package org.kpmp.packages;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PackageResponseTest {

    private PackageResponse packageResponse;

    @Before
    public void setUp() throws Exception {
        packageResponse = new PackageResponse();
    }

    @After
    public void tearDown() throws Exception {
        packageResponse = null;
    }

    @Test
    public void testSetPackageId() {
        packageResponse.setPackageId("packageId");
        assertEquals("packageId", packageResponse.getPackageId());
    }

    @Test
    public void testSetGlobusURL()
    {
        packageResponse.setGlobusURL("globusURL");
        assertEquals("globusURL", packageResponse.getGlobusURL());
    }

}
