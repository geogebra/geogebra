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

package org.geogebra.common.euclidian.draw;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import java.util.Objects;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.kernel.geos.GeoText;
import org.junit.Test;

public class DrawTextTest extends BaseUnitTest {

	@Test
	public void testAlignLeft() {
		add("ZoomIn(-5,-5,5,5)");
		GeoText txt = add("Text(\"short text\",(0,0),false,false,-1,0)");
		DrawText drawable = (DrawText) getDrawable(txt);
		assertThat(drawable, notNullValue());
		GRectangle bounds = Objects.requireNonNull(drawable.getBounds());
		assertThat(drawable.xLabel + bounds.getWidth(), equalTo(401.0));
	}

	@Test
	public void testAlignLeftVerticalDefault() {
		add("ZoomIn(-5,-5,5,5)");
		GeoText txt = add("Text(\"short text\",(0,0),false,false,-1)");
		DrawText drawable = (DrawText) getDrawable(txt);
		assertThat(drawable, notNullValue());
		GRectangle bounds = Objects.requireNonNull(drawable.getBounds());
		assertThat(drawable.xLabel + bounds.getWidth(), equalTo(401.0));
	}

	@Test
	public void testCenter() {
		add("ZoomIn(-5,-5,5,5)");
		GeoText txt = add("Text(\"short text\",(0,0),false,false,0,0)");
		DrawText drawable = (DrawText) getDrawable(txt);
		assertThat(drawable, notNullValue());
		GRectangle bounds = Objects.requireNonNull(drawable.getBounds());
		assertThat(drawable.xLabel + bounds.getWidth() / 2, equalTo(403.0));
	}

	@Test
	public void testAlignRight() {
		add("ZoomIn(-5,-5,5,5)");
		GeoText txt = add("Text(\"short text\",(0,0),false,false,1,0)");
		DrawText drawable = (DrawText) getDrawable(txt);
		assertThat(drawable, notNullValue());
		assertThat(drawable.xLabel, equalTo(406));
	}

}
