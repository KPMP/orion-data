package org.kpmp.forms;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class FormControllerTest {

	@Mock
	private FormRepository repository;
	private FormController controller;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		controller = new FormController(repository);
	}

	@After
	public void tearDown() throws Exception {
		controller = null;
	}

	@Test
	public void testGetFormDTDNoVersion() {
		Form expectedForm = mock(Form.class);
		when(repository.findTopByOrderByVersionDesc()).thenReturn(expectedForm);

		assertEquals(expectedForm, controller.getFormDTD());
	}

	@Test
	public void testGetFormDTDWithVersion() {
		Form expectedForm = mock(Form.class);
		when(repository.findByVersion(1.0)).thenReturn(Arrays.asList(expectedForm));
		assertEquals(expectedForm, controller.getFormDTD(1.0));
	}

}
