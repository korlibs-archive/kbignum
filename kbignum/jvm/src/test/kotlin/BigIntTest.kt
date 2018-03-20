package com.soywiz

import kotlin.test.*

class BigIntTest {
	@Test
	fun testMultiplyPowerOfTwo() {
		assertEquals("1", (1.n * 1.n).toString2())
		assertEquals("10", (1.n * 2.n).toString2())
		assertEquals("100", (1.n * 4.n).toString2())
		assertEquals("1000", (1.n * 8.n).toString2())
		assertEquals("1000000000000000", (1.n * (1 shl 15)).toString2())
		assertEquals("10000000000000000", (1.n * (1 shl 16)).toString2())
		assertEquals("100000000000000000", (1.n * (1 shl 17)).toString2())
		assertEquals("100000000000000000000000000000000000000000000000000000000000000", (1.n * (1L shl 62)).toString2())
		assertEquals("1${"0".repeat(128)}", (1.n * (1.n shl 128)).toString2())
	}

	@Test
	fun testAddSmall() {
		assertEquals("10", (1.n + 1.n).toString2())
		assertEquals("11", (1.n + 1.n + 1.n).toString2())
		assertEquals(108888887.n, 99999999.n + 8888888.n)
		assertEquals("108888887", (99999999.n + 8888888.n).toString())
	}

	@Test
	fun testSub() {
		assertEquals("25", "${100.n - 75.n}")
		assertEquals("-25", "${75.n - 100.n}")
		assertEquals("0", "${100.n - 100.n}")
		assertEquals("0", "${(-100).n - (-100).n}")
		assertEquals("-50", "${(-100).n - (-50).n}")
		assertEquals("-150", "${(-100).n - (50).n}")
		assertEquals("150", "${(100).n - (-50).n}")
	}

	@Test
	fun testSubInt() {
		val res = (-9999999).n - (-8888888).n
		println("$res")

		val items = listOf(-9999999, -8888888, -100, -50, 0, +50, +100, +8888888, +9999999)
		for (l in items) for (r in items) {
			//println("$l - $r = ${l - r}")
			//println("${l.n} - ${r.n} = ${(l - r).n}")
			//println("${l.n} - ${r.n} = ${(l.n - r.n)}")
			assertEquals((l - r).n, l.n - r.n)
		}
	}

	@Test
	fun testToString2() {
		assertEquals("0", "0".n(2).toString2())
		assertEquals("101011", "101011".n(2).toString2())
		assertEquals("1000000010000001", "1000000010000001".n(2).toString2())
		assertEquals("1000000000000000", "1000000000000000".n(2).toString2())
	}

	@Test
	fun testToString10() {
		assertEquals("0", "${0.n}")
		assertEquals("1", "${1.n}")
		assertEquals("10", "${10.n}")
		assertEquals("100", "${100.n}")
		assertEquals("999", "${999.n}")
	}

	@Test
	fun testCompare() {
		assertTrue(1.n == 1.n)
		assertTrue(0.n < 1.n)
		assertTrue(1.n > 0.n)
		assertTrue(0.n >= 0.n)
		assertTrue(1.n >= 0.n)
		assertTrue(0.n <= 0.n)
		assertTrue(0.n <= 1.n)

		assertTrue((-1).n < 1.n)
		assertTrue((1).n > (-1).n)

		assertTrue((-2).n < (-1).n)
	}

	@Test
	fun testBitwise() {
		assertEquals("${0b101 xor 0b110}", "${0b101.n xor 0b110.n}")
		assertEquals("${0b101 and 0b110}", "${0b101.n and 0b110.n}")
		assertEquals("${0b101 or 0b110}", "${0b101.n or 0b110.n}")

	}
}