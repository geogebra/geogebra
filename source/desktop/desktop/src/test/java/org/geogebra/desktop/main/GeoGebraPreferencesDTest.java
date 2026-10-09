/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.desktop.main;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.prefs.Preferences;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GeoGebraPreferencesDTest {

	private static final int MAX_CHUNK = (int) Math.floor(Preferences.MAX_VALUE_LENGTH * 0.75);

	private Preferences prefs;

	@BeforeEach
	void setUp() {
		prefs = Preferences.userRoot().node("geogebra-test-" + System.nanoTime());
	}

	@AfterEach
	void tearDown() throws Exception {
		prefs.removeNode();
		prefs.flush();
	}

	@Test
	void smallStringUsesBaseKey() {
		GeoGebraPreferencesD.putLargeString(prefs, "k", "hello");
		assertEquals("hello", prefs.get("k", null));
		assertNull(prefs.get("k1", null));
	}

	@Test
	void smallStringRoundTrips() {
		GeoGebraPreferencesD.putLargeString(prefs, "k", "hello");
		assertEquals("hello", GeoGebraPreferencesD.getLargeString(prefs, "k", "fallback"));
	}

	@Test
	void stringLongerThanPrefsLimitCanBeStoredAndReadBack() {
		String value = repeat('x', Preferences.MAX_VALUE_LENGTH + 100);
		GeoGebraPreferencesD.putLargeString(prefs, "k", value);
		assertEquals(value, GeoGebraPreferencesD.getLargeString(prefs, "k", null));
	}

	@Test
	void chunkedStringIsRemovedFromBaseKey() {
		String value = repeat('x', Preferences.MAX_VALUE_LENGTH + 100);
		GeoGebraPreferencesD.putLargeString(prefs, "k", value);
		assertNull(prefs.get("k", null));
	}

	@Test
	void overwritingChunkedWithSmallRemovesStaleParts() {
		GeoGebraPreferencesD.putLargeString(prefs, "k", repeat('x', 2 * Preferences.MAX_VALUE_LENGTH));
		GeoGebraPreferencesD.putLargeString(prefs, "k", "small");
		assertNull(prefs.get("k1", null));
		assertEquals("small", GeoGebraPreferencesD.getLargeString(prefs, "k", null));
	}

	@Test
	void overwritingWithFewerChunksRemovesStaleChunks() {
		GeoGebraPreferencesD.putLargeString(
				prefs, "k", repeat('x', 2 * Preferences.MAX_VALUE_LENGTH + 1));
		GeoGebraPreferencesD.putLargeString(prefs, "k", repeat('y', Preferences.MAX_VALUE_LENGTH + 1));
		assertEquals(
				repeat('y', Preferences.MAX_VALUE_LENGTH + 1),
				GeoGebraPreferencesD.getLargeString(prefs, "k", null));
	}

	@Test
	void missingKeyReturnsDefault() {
		assertEquals("fallback", GeoGebraPreferencesD.getLargeString(prefs, "missing", "fallback"));
	}

	@Test
	void legacyUnchunkedValueIsReadBack() {
		prefs.put("k", "legacy");
		assertEquals("legacy", GeoGebraPreferencesD.getLargeString(prefs, "k", null));
	}

	@Test
	void nullValueIsIgnored() {
		GeoGebraPreferencesD.putLargeString(prefs, "k", null);
		assertNull(prefs.get("k", null));
	}

	@Test
	void smallByteArrayUsesBaseKey() {
		byte[] value = bytes(10);
		GeoGebraPreferencesD.putByteArray(prefs, "k", value);
		assertArrayEquals(value, prefs.getByteArray("k", null));
	}

	@Test
	void largeByteArrayRoundTrips() {
		byte[] value = bytes(2 * MAX_CHUNK);
		GeoGebraPreferencesD.putByteArray(prefs, "k", value);
		assertArrayEquals(value, GeoGebraPreferencesD.getByteArray(prefs, "k", null));
	}

	@Test
	void overwritingChunkedBytesWithSmallRemovesStaleParts() {
		GeoGebraPreferencesD.putByteArray(prefs, "k", bytes(2 * MAX_CHUNK));
		GeoGebraPreferencesD.putByteArray(prefs, "k", bytes(10));
		assertNull(prefs.getByteArray("k1", null));
		assertArrayEquals(bytes(10), GeoGebraPreferencesD.getByteArray(prefs, "k", null));
	}

	@Test
	void overwritingWithFewerByteChunksDropsStaleTail() {
		GeoGebraPreferencesD.putByteArray(prefs, "k", bytes(3 * MAX_CHUNK));
		GeoGebraPreferencesD.putByteArray(prefs, "k", bytes(2 * MAX_CHUNK));
		assertNull(prefs.getByteArray("k3", null));
		assertArrayEquals(bytes(2 * MAX_CHUNK), GeoGebraPreferencesD.getByteArray(prefs, "k", null));
	}

	@Test
	void missingByteArrayReturnsDefault() {
		byte[] def = bytes(3);
		assertArrayEquals(def, GeoGebraPreferencesD.getByteArray(prefs, "missing", def));
	}

	private static byte[] bytes(int count) {
		byte[] b = new byte[count];
		for (int i = 0; i < count; i++) {
			b[i] = (byte) (i % 251);
		}
		return b;
	}

	private static String repeat(char c, int count) {
		StringBuilder sb = new StringBuilder(count);
		for (int i = 0; i < count; i++) {
			sb.append(c);
		}
		return sb.toString();
	}
}
