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

package org.geogebra.common.jre.headless;

import org.geogebra.common.geogebra3D.euclidianForPlane.EuclidianViewForPlaneCompanion;
import org.geogebra.common.geogebra3D.main.App3DCompanion;
import org.geogebra.common.gui.layout.DockPanel;
import org.geogebra.common.kernel.kernelND.ViewCreator;
import org.geogebra.common.main.App;
import org.geogebra.common.main.settings.EuclidianSettings;

public final class App3DCompanionHeadless extends App3DCompanion {
	public App3DCompanionHeadless(App app) {
		super(app);
	}

	@Override
	protected EuclidianViewForPlaneCompanion createEuclidianViewForPlane(
			ViewCreator plane, EuclidianSettings evSettings,
			boolean panelSettings) {
		return null;
	}

	@Override
	public DockPanel getPanelForPlane() {
		return null;
	}
}