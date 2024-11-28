package org.geogebra.common.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ImageManagerTest {

	@Test
	public void fixSVGShouldPreserveSufficientInput() {
		assertEquals("<svg width=42 height=42 viewBox=\"0 0 42 42\"></svg>",
				ImageManager.fixSVG("<svg width=42 height=42 viewBox=\"0 0 42 42\"></svg>"));
	}

	@Test
	public void fixSVGShouldAddMissingData() {
		assertEquals("<svg viewBox=\"0 0 42 42\" height=\"42.0\" width=\"42.0\"></svg>",
				ImageManager.fixSVG("<svg viewBox=\"0 0 42 42\"></svg>"));
	}

	@Test
	public void testFixSVGRemoveAspectRatio() {
		assertEquals("<svg preserveAspectRatio=\"none\" viewBox=\"0 0 42 42\""
						+ " height=\"42\" width=\"42\"></svg>",
				ImageManager.fixAndRemoveAspectRatio(
						"<svg width=\"42\" height=\"42\" viewBox=\"0 0 42 42\"></svg>"));
		assertEquals("<svg preserveAspectRatio=\"none\" viewBox=\"0 0 42 42\" "
						+ "height=\"42.0\" width=\"42.0\"></svg>",
				ImageManager.fixAndRemoveAspectRatio("<svg viewBox=\"0 0 42 42\"></svg>"));
	}
}
