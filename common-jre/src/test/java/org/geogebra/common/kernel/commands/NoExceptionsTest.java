package org.geogebra.common.kernel.commands;

import org.junit.Assert;
import org.junit.Test;

public class NoExceptionsTest extends NoExceptionsTestCommon {

	@Test
	public void implicitSurface() {
		t("x^3+y^3+z^3=1");
	}

	@Test
	public void localizationTest() {
		Assert.assertNull(app.getLocalization().getReverseCommand("x"));
	}
}
