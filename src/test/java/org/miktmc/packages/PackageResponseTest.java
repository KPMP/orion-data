package org.miktmc.packages;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PackageResponseTest {

    private PackageResponse packageResponse;

    @BeforeEach
    public void setUp() throws Exception {
        packageResponse = new PackageResponse();
    }

    @AfterEach
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
