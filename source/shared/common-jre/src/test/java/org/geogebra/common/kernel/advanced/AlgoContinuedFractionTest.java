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

package org.geogebra.common.kernel.advanced;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.AnimationManager;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.test.annotation.Issue;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link AlgoContinuedFraction}.
 */
class AlgoContinuedFractionTest extends BaseUnitTest {

	@Test
	@Issue("APPS-8139")
	void sixteenQuotientsShouldNotCrash() {
		GeoElement text = add("ContinuedFraction(sqrt(3)/3, true)");
		assertThat(
				text.toValueString(StringTemplate.defaultTemplate),
				is("[0;1,1,2,1,2,1,2,1,2,1,2,1,2,1,2,\\ldots]"));
	}

	@Test
	@Issue("APPS-8139")
	void animatedSliderReachingSixteenQuotientsShouldNotCrash() {
		GeoNumeric slider = add("a = Slider(4, 44, 1)");
		GeoElement text = add("text1 = ContinuedFraction(sqrt(a), true)");
		slider.setValue(43);
		slider.updateCascade();
		slider.setAnimationType(GeoElementND.ANIMATION_OSCILLATING);
		slider.setAnimationSpeed(10);
		slider.setAnimating(true);
		AnimationManager animationManager = getKernel().getAnimationManager();
		animationManager.startAnimation();

		animationManager.onRun();

		assertThat(slider.getValue(), is(44.0));
		assertThat(
				text.toValueString(StringTemplate.defaultTemplate),
				is("[6;1,1,1,2,1,1,1,12,1,1,1,2,1,1,1,\\ldots]"));
	}
}
