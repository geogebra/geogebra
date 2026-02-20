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

package org.geogebra.web.full.gui.view.algebra.compositefocus;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.geogebra.common.factories.FormatFactory;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.full.main.TestFormatFactory;
import org.geogebra.web.test.AppMocker;

public class AddGeosSetup {
	private AppWFull app;

	/**
	 * Setup application as Geometry.
	 */
	public void initApp() {
		FormatFactory.setPrototypeIfNull(new TestFormatFactory());
		app = AppMocker.mockGeometry();
	}

	protected GeoElement add(String cmd) {
		GeoElementND geo = processAlgebraCommand(cmd);
		assertNotNull("Command did not produce any geos: " + cmd, geo);
		assertTrue("geo is not a GeoElement: " + geo.getClass(), geo instanceof GeoElement);
		return (GeoElement) geo;
	}

	private GeoElementND processAlgebraCommand(String cmd) {
		GeoElementND[] geos = app.getKernel().getAlgebraProcessor()
				.processAlgebraCommand(cmd, false);
		return geos != null && geos.length > 0 ? geos[0] : null;

	}

	public AppWFull getApp() {
		return app;
	}
}
