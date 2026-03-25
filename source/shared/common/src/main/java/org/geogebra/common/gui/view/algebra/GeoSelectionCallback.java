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

package org.geogebra.common.gui.view.algebra;

import java.util.Set;

import javax.annotation.Nonnull;

import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.restrictions.FeatureRestriction;
import org.geogebra.common.restrictions.Restrictable;
import org.geogebra.common.util.AsyncOperation;

public class GeoSelectionCallback implements AsyncOperation<GeoElementND[]>, Restrictable {

	private boolean restrictGraphSelectionForFunctions = false;

	@Override
	public void applyRestrictions(@Nonnull Set<FeatureRestriction> featureRestrictions) {
		restrictGraphSelectionForFunctions = featureRestrictions
				.contains(FeatureRestriction.AUTOMATIC_GRAPH_SELECTION_FOR_FUNCTIONS);
	}

	@Override
	public void removeRestrictions(@Nonnull Set<FeatureRestriction> featureRestrictions) {
		restrictGraphSelectionForFunctions = false;
	}

	@Override
	public void callback(GeoElementND[] geoElements) {
		if (restrictGraphSelectionForFunctions) {
			return;
		}
		if (geoElements != null && geoElements.length == 1 && geoElements[0] != null) {
			GeoElementND geoElement = geoElements[0];
			AlgebraItem.addSelectedGeoWithSpecialPoints(geoElement, geoElement.getApp());
		}
	}
}
