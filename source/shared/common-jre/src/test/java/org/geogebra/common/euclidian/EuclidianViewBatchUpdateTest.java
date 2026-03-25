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

package org.geogebra.common.euclidian;

import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import java.util.Objects;

import org.geogebra.common.SuiteSubApp;
import org.geogebra.common.kernel.arithmetic.Function;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.test.BaseAppTestSetup;
import org.geogebra.test.annotation.Issue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class EuclidianViewBatchUpdateTest extends BaseAppTestSetup {

	@BeforeEach
	void setup() {
		setupApp(SuiteSubApp.GRAPHING);
	}

	/**
	 * Verifies that a second batch update cycle with no deferred update request
	 * does not trigger a redundant allDrawableList.updateAll() call.
	 */
	@Test
	@Issue("APPS-7293")
	public void secondBatchUpdateDoesNotTriggerRedundantDrawablesUpdate() {
		EuclidianView view = getApp().getEuclidianView1();
		GeoFunction geo = evaluateGeoElement("x+sin(x)", GeoFunction.class);
		Function spyFunction = spy(Objects.requireNonNull(geo.getFunction()));
		geo.setFunction(spyFunction);
		// First batch: deferred update is requested and should be processed exactly once
		view.startBatchUpdate();
		view.updateAllDrawables(true);
		verify(spyFunction, never()).value(anyDouble());
		view.endBatchUpdate();
		verify(spyFunction, atLeast(10)).value(anyDouble());
		clearInvocations(spyFunction);
		// Second batch: no update requested, endBatchUpdate should not call updateAll again
		view.startBatchUpdate();
		view.endBatchUpdate();
		verify(spyFunction, never()).value(anyDouble());
	}
}
