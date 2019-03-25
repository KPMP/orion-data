package org.kpmp.releases;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;

public class ReleaseTest extends Release {

    private Release release;

    @Before
    public void setUp() {
        release = new Release();
    }

    @After
    public void tearDown() throws Exception {
        release = null;
    }

    @Test
    public void testGetVersion() {
        String version = "1.01";
        release.setVersion(version);
        assertEquals(version, release.getVersion());
    }

    @Test
    public void testGetDate() {
        String date = "2019-03-25";
        release.setDate(date);
        assertEquals(date, release.getDate());
    }

    @Test
    public void testGetDesc() {
        String desc = "Lorem ipsum dolor amet godard fam irony mumblecore typewriter. Franzen hot chicken lomo, yuccie live-edge jean shorts copper mug cliche glossier activated charcoal. Hexagon hell of shabby chic ugh. Ramps leggings XOXO, whatever cronut next level pour-over poke. 8-bit farm-to-table gentrify, flannel tbh microdosing adaptogen williamsburg lo-fi mlkshk seitan normcore fixie retro coloring book. Semiotics gluten-free keytar DIY gastropub, gochujang iceland.\n";
        release.setDesc(desc);
        assertEquals(desc, release.getDesc());
    }

    @Test
    public void testGetTypeSpecificNotes() {
        HashMap<String, Object> typeSpecificNotes = new HashMap<>();
        release.setTypeSpecificNotes(typeSpecificNotes);
        assertEquals(typeSpecificNotes, release.getTypeSpecificNotes());
    }
}
