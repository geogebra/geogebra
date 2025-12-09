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

package org.geogebra.web.html5.factories;

import org.geogebra.common.factories.Factory;
import org.geogebra.common.javax.swing.RelationPane;
import org.geogebra.web.html5.main.AppW;

public class FactoryW extends Factory {
	private AppW app;

	public FactoryW(AppW appW) {
		this.app = appW;
	}

	@Override
	public RelationPane newRelationPane(String subTitle) {
		return app.getRelationDialog(subTitle);
	}
}