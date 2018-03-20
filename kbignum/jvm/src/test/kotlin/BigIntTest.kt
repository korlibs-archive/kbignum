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
	}

	@Test
	fun testAddSmall() {
		assertEquals("10", (1.n + 1.n).toString2())
		assertEquals("11", (1.n + 1.n + 1.n).toString2())
		assertEquals("10000000000000000", ("1000000000000000".n(2) + "1000000000000000".n(2)).toString2())
	}
}