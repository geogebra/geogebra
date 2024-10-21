package org.geogebra.common.gui.view.algebra;

import java.util.Set;

import javax.annotation.Nonnull;

import org.geogebra.common.exam.restrictions.ExamFeatureRestriction;
import org.geogebra.common.exam.restrictions.ExamRestrictable;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.util.AsyncOperation;

public class GeoSelectionCallback implements AsyncOperation<GeoElementND[]>, ExamRestrictable {

	private boolean restrictGraphSelectionForFunctions = false;

	@Override
	public void applyRestrictions(@Nonnull Set<ExamFeatureRestriction> featureRestrictions) {
		restrictGraphSelectionForFunctions = featureRestrictions
				.contains(ExamFeatureRestriction.AUTOMATIC_GRAPH_SELECTION_FOR_FUNCTIONS);
	}

	@Override
	public void removeRestrictions(@Nonnull Set<ExamFeatureRestriction> featureRestrictions) {
		restrictGraphSelectionForFunctions = false;
	}

	@Override
	public void callback(GeoElementND[] geoElements) {
		if (restrictGraphSelectionForFunctions) {
			return;
		}
		if (geoElements != null && geoElements.length == 1) {
			GeoElementND geoElement = geoElements[0];
			AlgebraItem.addSelectedGeoWithSpecialPoints(geoElement, geoElement.getApp());
		}
	}
}
