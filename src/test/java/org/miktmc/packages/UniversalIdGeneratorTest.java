package org.miktmc.packages;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class UniversalIdGeneratorTest {

	private UniversalIdGenerator generator;

	@BeforeEach
	public void setUp() throws Exception {
		generator = new UniversalIdGenerator();
	}

	@AfterEach
	public void tearDown() throws Exception {
		generator = null;
	}

	@Test
	public void testGenerateUniversalId() throws Exception {
		String uuid = generator.generateUniversalId();

		System.err.println(uuid);
		assertNotNull(uuid);
		assertEquals(4, UUID.fromString(uuid).version());
	}

}
