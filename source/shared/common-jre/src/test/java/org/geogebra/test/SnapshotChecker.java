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

package org.geogebra.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.MediaType;
import org.junit.jupiter.api.TestReporter;

public class SnapshotChecker {
	private final TestReporter reporter;
	private final Class<?> testClass;

	/**
	 * @param reporter test reporter
	 * @param testClass test class
	 */
	public SnapshotChecker(TestReporter reporter, Class<?> testClass) {
		this.reporter = reporter;
		this.testClass = testClass;
	}

	/**
	 * @param snapshotFileName filename of the snapshot
	 * @param actual actual text content
	 */
	public void assertMatch(String snapshotFileName, String actual) {
		try {
			String expected = Files.readString(
							Path.of(testClass.getResource(snapshotFileName).toURI()))
					.trim()
					.replace("\r", "");
			if (!expected.equals(actual)) {
				try {
					Path snapshotDir = Path.of("build", "snapshots");
					Files.createDirectories(snapshotDir);
					String[] parts = snapshotFileName.split("\\.");
					Path snap = Files.createTempFile(snapshotDir, parts[0], "." + parts[1]);
					Files.write(snap, actual.getBytes(StandardCharsets.UTF_8));
					reporter.publishFile(snap, MediaType.TEXT_PLAIN);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
				assertEquals(expected, actual);
			}
		} catch (URISyntaxException | IOException ex) {
			throw new RuntimeException(ex);
		}
	}
}
