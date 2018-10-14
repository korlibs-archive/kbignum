# KBigNum

[![Build Status](https://travis-ci.org/korlibs/kbignum.svg?branch=master)](https://travis-ci.org/korlibs/kbignum)
[![Maven Version](https://img.shields.io/github/tag/korlibs/kbignum.svg?style=flat&label=maven)](http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22kbignum%22)
[![Gitter](https://img.shields.io/gitter/room/korlibs/korlibs.svg)](https://gitter.im/korlibs/Lobby)

## BigInt

Provides a portable implementation of an arbitrary sized Big Integer in Kotlin Common.

Exposes `expect` and `actual` for targets including a native BigInteger library,
and uses the common pure kotlin implementation in other targets.

## BigDecimal

Using BigInt implements a BigDecimal class.
