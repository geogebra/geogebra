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

package org.geogebra.common.gui.view.probcalculator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.gui.view.probcalculator.ProbabilityCalculatorTableValuesViewModel.ButtonState;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.main.settings.ProbabilityCalculatorSettings.Dist;
import org.geogebra.test.BaseAppTestSetup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ProbabilityCalculatorTableValuesViewModelTests extends BaseAppTestSetup {
	private ProbabilityCalculatorView view;
	private ProbabilityCalculatorTableValuesViewModel tableValuesView;

	@BeforeEach
	void setUp() {
		setupApp(SuiteSubApp.PROBABILITY);
		view = new HeadlessProbabilityCalculatorView(getApp());
		setDistribution(Dist.NORMAL);
		tableValuesView = new ProbabilityCalculatorTableValuesViewModel(view);
	}

	@Test
	void testInitialUnavailableState() {
		assertEquals(new ButtonState.Hidden(), tableValuesView.getButtonState());
		assertNull(tableValuesView.getContent());
	}

	@Test
	void testInitialAvailableState() {
		setDistribution(Dist.BINOMIAL);
		assertEquals(new ButtonState.Visible(false), tableValuesView.getButtonState());
		assertNull(tableValuesView.getContent());
	}

	@Test
	void testOpeningView() {
		setDistribution(Dist.BINOMIAL);
		tableValuesView.onButtonTapped();

		assertEquals(new ButtonState.Visible(true), tableValuesView.getButtonState());
		assertNotNull(tableValuesView.getContent());
	}

	@Test
	void testOpeningAndClosingViewWithButton() {
		setDistribution(Dist.BINOMIAL);
		tableValuesView.onButtonTapped();
		tableValuesView.onButtonTapped();

		assertEquals(new ButtonState.Visible(false), tableValuesView.getButtonState());
		assertNull(tableValuesView.getContent());
	}

	@Test
	void testClosingViewWithoutButton() {
		setDistribution(Dist.BINOMIAL);
		tableValuesView.onButtonTapped();
		tableValuesView.onClosed();

		assertEquals(new ButtonState.Visible(false), tableValuesView.getButtonState());
		assertNull(tableValuesView.getContent());
	}

	@Test
	void testClosingViewWhenContinuousDistributionSelected() {
		setDistribution(Dist.BINOMIAL);
		tableValuesView.onButtonTapped();
		view.setProbabilityCalculator(Dist.NORMAL, new GeoNumberValue[] {
				new GeoNumeric(getKernel().getConstruction(), 0),
				new GeoNumeric(getKernel().getConstruction(), 1)
		}, false);

		assertEquals(new ButtonState.Hidden(), tableValuesView.getButtonState());
		assertNull(tableValuesView.getContent());
	}

	private void setDistribution(Dist distribution) {
		view.setProbabilityCalculator(distribution, new GeoNumberValue[] {
				new GeoNumeric(getKernel().getConstruction(), 0),
				new GeoNumeric(getKernel().getConstruction(), 1)
		}, false);
	}
}
