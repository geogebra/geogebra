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

package org.geogebra.common.gui.slider;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.gui.util.slider.SliderBuilder;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.algos.ConstructionElement;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.main.error.ErrorHandler;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class SliderBuilderTest extends BaseUnitTest {

	private SliderBuilder sliderBuilder;
	private Construction construction;

	@Before
	public void setupSliderBuilder() {
		Kernel kernel = getApp().getKernel();
		construction = kernel.getConstruction();
		AlgebraProcessor algebraProcessor = kernel.getAlgebraProcessor();
		ErrorHandler errorHandler = mock(ErrorHandler.class);

		sliderBuilder = new SliderBuilder(algebraProcessor, errorHandler);
		sliderBuilder.withMin("-5").withMax("5").withStep("1").withLocation(0, 0);
	}

	@After
	public void tearDown() {
		construction.clearConstruction();
	}

	@Test
	public void createSimple() {
		assertNotNull(sliderBuilder.create());
		assertTrue(isSliderInConstructionList());
	}

	private boolean isSliderInConstructionList() {
		ConstructionElement slider = construction.getConstructionElement(0);
		return slider instanceof GeoNumeric;
	}

	@Test
	public void createWithEmptyInput() {
		sliderBuilder.withMin("");
		assertNull(sliderBuilder.create());
		assertFalse(isSliderInConstructionList());
	}

	@Test
	public void testSuppressLabelFlagAfterCreated() {
		assertFalse(construction.isSuppressLabelsActive());
		createSimple();
		assertFalse(construction.isSuppressLabelsActive());
	}

	@Test
	public void testSuppressLabelFlagAfterEmptyInput() {
		assertFalse(construction.isSuppressLabelsActive());
		createWithEmptyInput();
		assertFalse(construction.isSuppressLabelsActive());
	}
}