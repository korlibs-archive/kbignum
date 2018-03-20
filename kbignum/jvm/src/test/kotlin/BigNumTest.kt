package com.soywiz

import org.junit.Test
import kotlin.test.*

class BigNumTest {
	@Test
	fun testToString() {
		assertEquals("0.019", BigNum(19.bi, 3).toString())
		assertEquals("0.19", BigNum(19.bi, 2).toString())
		assertEquals("1.9", BigNum(19.bi, 1).toString())
		assertEquals("19", BigNum(19.bi, 0).toString())
	}

	@Test
	fun testAddSameScale() {
		assertEquals("0.050", (BigNum(20.bi, 3) + BigNum(30.bi, 3)).toString())
	}

	@Test
	fun testAddDifferentScale() {
		assertEquals("11.51", (BigNum("1.2") + BigNum("10.31")).toString())
	}

	@Test
	fun testMultiply() {
		assertEquals("2.4".bd, "1.2".bd * "2".bd)
	}

	@Test
	fun testFromString() {
		//assertEquals("-50", BigNum("-050").toString())
		assertEquals("50", BigNum("050").toString())
		assertEquals("0.00005", BigNum("0.00005000000").toString())
		assertEquals("0.05", BigNum("0.050").toString())
		assertEquals("0.05", BigNum(".050").toString())
	}

	@Test
	fun testCompare() {
		assertTrue("1.5".bd < "3.0".bd)
		assertTrue("1.5".bd <= "1.5".bd)
	}
}