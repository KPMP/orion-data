package org.miktmc.packages;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Date;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class StateTest {

	private State state;

	@BeforeEach
	public void setUp() throws Exception {
		state = new State("packageId", "state", null, "codicil");
	}

	@AfterEach
	public void tearDown() throws Exception {
		state = null;
	}

	@Test
	public void testSetPackageId() {
		state.setPackageId("coolPackage");
		assertEquals("coolPackage", state.getPackageId());
	}

	@Test
	public void testSetState() {
		state.setState("newState");
		assertEquals("newState", state.getState());
	}

	@Test
	public void testSetCodicil() {
		state.setCodicil("this is why we can't have nice things");
		assertEquals("this is why we can't have nice things", state.getCodicil());
	}

	@Test
	public void testLargeUploadChecked() {
		state.setLargeUploadChecked("true");
		assertEquals("true", state.getLargeUploadChecked());
	}

	@Test
	public void testConstructor() throws Exception {
		State constructorTest = new State("a package id", "a state", null, "reasons");
		assertEquals("a package id", constructorTest.getPackageId());
		assertEquals("a state", constructorTest.getState());
		assertEquals("reasons", constructorTest.getCodicil());
	}

	@Test
	public void testSetStateChangeDate() throws Exception {
		Date date = new Date();
		state.setStateChangeDate(date);
		assertEquals(date, state.getStateChangeDate());
	}

}
