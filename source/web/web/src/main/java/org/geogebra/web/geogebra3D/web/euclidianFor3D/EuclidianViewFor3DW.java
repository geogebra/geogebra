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

package org.geogebra.web.geogebra3D.web.euclidianFor3D;

import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.euclidian.EuclidianViewCompanion;
import org.geogebra.common.geogebra3D.euclidianFor3D.EuclidianViewFor3DCompanion;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.web.html5.euclidian.EuclidianPanelWAbstract;
import org.geogebra.web.html5.euclidian.EuclidianViewW;

/**
 * Simple extension of EuclidianView to implement handling of 3D objects
 * 
 * @author mathieu
 * 
 */
public class EuclidianViewFor3DW extends EuclidianViewW {

	/**
	 * @param euclidianViewPanel
	 *            parent panel
	 * @param euclidianController
	 *            controller
	 * @param evNo
	 *            view number
	 * @param settings
	 *            settings
	 */
	public EuclidianViewFor3DW(EuclidianPanelWAbstract euclidianViewPanel,
			EuclidianController euclidianController, int evNo,
			EuclidianSettings settings) {
		super(euclidianViewPanel, euclidianController, evNo, settings);
	}

	@Override
	protected EuclidianViewCompanion newEuclidianViewCompanion() {
		return new EuclidianViewFor3DCompanion(this);
	}

}
