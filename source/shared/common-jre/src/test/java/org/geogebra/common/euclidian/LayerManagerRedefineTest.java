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

import static org.junit.Assert.assertEquals;

import java.util.Collections;

import org.geogebra.common.AppCommonFactory;
import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.jre.headless.AppCommon;
import org.geogebra.common.kernel.algos.AlgoPolygon;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoPolygon;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.main.settings.config.AppConfigNotes;
import org.junit.Test;

public class LayerManagerRedefineTest extends BaseUnitTest {

	@Override
	public AppCommon createAppCommon() {
		AppCommon app = AppCommonFactory.create();
		app.setConfig(new AppConfigNotes());
		return app;
	}

	@Test
	public void redefineShouldNotChangeOrder() {
		createPoly("q1");
		createPoly("q2");
		assertEquals("q1,q2", getLayerManager().getOrder());
		getLayerManager().moveForward(Collections.singletonList(lookup("q1")));
		assertEquals("q2,q1", getLayerManager().getOrder());
		add("q2=Polygon((0,0),(3,0),(0,1))");
		assertEquals("q2,q1", getLayerManager().getOrder());
		add("q1=Polygon((0,0),(4,0),(0,1))");
		assertEquals("q2,q1", getLayerManager().getOrder());
		add("q2=Polygon((0,0),(5,0),(0,1))");
		assertEquals("q2,q1", getLayerManager().getOrder());
		add("q2=Polygon((0,0),(6,0),(0,1))");
		assertEquals("q2,q1", getLayerManager().getOrder());
	}

	private void createPoly(String label) {
		GeoPointND[] pointArray = new GeoPointND[] {
				new GeoPoint(getConstruction(), 0, 0, 1),
				new GeoPoint(getConstruction(), 0, 1, 1),
				new GeoPoint(getConstruction(), 1, 0, 1),
				new GeoPoint(getConstruction(), 1, 0, 1)
		};
		AlgoPolygon algo = new AlgoPolygon(getConstruction(), pointArray,
				null, null, false, null, null);
		GeoPolygon poly = algo.getPoly();
		poly.setLabel(label);
	}

	private LayerManager getLayerManager() {
		return getApp().getKernel().getConstruction().getLayerManager();
	}
}
