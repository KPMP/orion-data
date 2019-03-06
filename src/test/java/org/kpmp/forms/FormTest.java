package org.kpmp.forms;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class FormTest extends Form {

	private Form dtd;

	@BeforeEach
	private void setUp() throws Exception {
		dtd = new Form();
	}

	@AfterEach
	private void tearDown() throws Exception {
		dtd = null;
	}

	@Test
	public void testGetForm() {
//		DBObject expectedDtd = mock(DBObject.class);
//		dtd.setForm(expectedDtd);
//		assertEquals(expectedDtd, dtd.getForm());
	}

}
