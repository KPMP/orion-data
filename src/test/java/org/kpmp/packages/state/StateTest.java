package org.kpmp.packages.state;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class StateTest {

	private State state;

	@Before
	public void setUp() throws Exception {
		state = new State("packageId", "state", "codicil");
	}

	@After
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
	public void testConstructor() throws Exception {
		State constructorTest = new State("a package id", "a state", "reasons");
		assertEquals("a package id", constructorTest.getPackageId());
		assertEquals("a state", constructorTest.getState());
		assertEquals("reasons", constructorTest.getCodicil());
	}

}
