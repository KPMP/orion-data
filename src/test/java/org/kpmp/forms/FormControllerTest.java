package org.kpmp.forms;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class FormControllerTest {

	@Mock
	private FormRepository repository;
	private FormController controller;

	@BeforeEach
	private void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		controller = new FormController(repository);
	}

	@AfterEach
	private void tearDown() throws Exception {
		controller = null;
	}

	@Test
	public void testGetFormDTD() {
		Form expectedForm = mock(Form.class);
		when(repository.findAll()).thenReturn(Arrays.asList(expectedForm));

		assertEquals(expectedForm, controller.getFormDTD());
	}

}
