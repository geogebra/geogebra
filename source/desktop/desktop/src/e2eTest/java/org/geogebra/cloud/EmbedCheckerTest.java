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
 
package org.geogebra.cloud;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.geogebra.common.factories.UtilFactory;
import org.geogebra.common.media.EmbedURLChecker;
import org.geogebra.common.move.ggtapi.operations.URLStatus;
import org.geogebra.desktop.factories.UtilFactoryD;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;

public class EmbedCheckerTest {

	private static EmbedURLChecker checker;

	@BeforeClass
	public static void setup() {
		UtilFactory.setPrototypeIfNull(new UtilFactoryD());
		checker = new EmbedURLChecker(
				"http://notes.dlb-dev01.alp-dlg.net/notes/api");
	}

	@Test
	public void echeck() {
		Assume.assumeNotNull(System.getProperty("marvl.auth.basic"));
		TestAsyncOperation<URLStatus> check = new TestAsyncOperation<>();
		checker.check("https://news.orf.at", check);
		assertNull(check.await(5).getErrorKey());
		check = new TestAsyncOperation<>();
		checker.check("https://edition.cnn.com", check);
		assertEquals("FrameLoadError", check.await(5).getErrorKey());
	}
}
