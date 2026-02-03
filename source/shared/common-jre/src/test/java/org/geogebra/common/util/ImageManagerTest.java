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

package org.geogebra.common.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ImageManagerTest {

	@Test
	public void fixSVGShouldPreserveSufficientInput() {
		assertEquals("<svg width=42 height=42 viewBox=\"0 0 42 42\"></svg>",
				ImageManagerCommon.fixSVG("<svg width=42 height=42 viewBox=\"0 0 42 42\"></svg>"));
	}

	@Test
	public void fixSVGShouldAddMissingData() {
		assertEquals("<svg viewBox=\"0 0 42 42\" height=\"42.0\" width=\"42.0\"></svg>",
				ImageManagerCommon.fixSVG("<svg viewBox=\"0 0 42 42\"></svg>"));
	}

	@Test
	public void testFixSVGRemoveAspectRatio() {
		assertEquals("<svg preserveAspectRatio=\"none\" viewBox=\"0 0 42 42\""
						+ " height=\"42\" width=\"42\"></svg>",
				ImageManagerCommon.fixAndRemoveAspectRatio(
						"<svg width=\"42\" height=\"42\" viewBox=\"0 0 42 42\"></svg>"));
		assertEquals("<svg preserveAspectRatio=\"none\" viewBox=\"0 0 42 42\" "
						+ "height=\"42.0\" width=\"42.0\"></svg>",
				ImageManagerCommon.fixAndRemoveAspectRatio("<svg viewBox=\"0 0 42 42\"></svg>"));
	}
}
