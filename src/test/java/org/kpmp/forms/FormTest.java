package org.kpmp.forms;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class FormTest extends Form {

	private Form form;

	@Before
	public void setUp() throws Exception {
		form = new Form();
	}

	@After
	public void tearDown() throws Exception {
		form = null;
	}

	@Test
	public void testgetId() {
		form.setId("ere354334");
		assertEquals("ere354334", form.getId());
	}

	@Test
	public void testGetTypeSpecificElements() throws Exception {
		List<Map<String, Object>> expected = Arrays.asList(new HashMap<>());
		form.setTypeSpecificElements(expected);

		assertEquals(expected, form.getTypeSpecificElements());
	}

	@Test
	public void testGetStandardFields() throws Exception {
		HashMap<String, Object> standardFields = new HashMap<>();
		form.setStandardFields(standardFields);

		assertEquals(standardFields, form.getStandardFields());
	}

	@Test
	public void testGetVersion() throws Exception {
		form.setVersion(3.14159);
		assertEquals(3.14159, form.getVersion(), 0.00001);
	}
}
