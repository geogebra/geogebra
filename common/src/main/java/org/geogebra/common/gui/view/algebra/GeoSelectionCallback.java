package org.geogebra.common.gui.view.algebra;

import javax.annotation.Nonnull;

import org.geogebra.common.exam.restrictions.ExamFeatureRestriction;
import org.geogebra.common.exam.restrictions.ExamRestrictable;
import org.geogebra.common.exam.restrictions.ExamRestrictions;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.util.AsyncOperation;

public class GeoSelectionCallback implements AsyncOperation<GeoElementND[]>, ExamRestrictable {

	private boolean restrictGraphSelectionForFunctions = false;

	@Override
	public void applyRestrictions(@Nonnull ExamRestrictions examRestrictions) {
		restrictGraphSelectionForFunctions = examRestrictions.getFeatureRestrictions()
				.contains(ExamFeatureRestriction.AUTOMATIC_GRAPH_SELECTION_FOR_FUNCTIONS);
	}

	@Override
	public void removeRestrictions(@Nonnull ExamRestrictions examRestrictions) {
		restrictGraphSelectionForFunctions = false;
	}

	@Override
	public void callback(GeoElementND[] obj) {
		if (restrictGraphSelectionForFunctions) {
			return;
		}
		if (obj != null && obj.length == 1) {
			GeoElementND geoElement = obj[0];
			AlgebraItem.addSelectedGeoWithSpecialPoints(geoElement, geoElement.getApp());
		}
	}
}
