package org.geogebra.common.main.exam.restriction;

import static org.geogebra.common.GeoGebraConstants.CAS_APPCODE;
import static org.geogebra.common.GeoGebraConstants.G3D_APPCODE;

import org.geogebra.common.main.Localization;

/**
 * Class to create restrictions for exams based on the locale.
 */
public class ExamRestrictionFactory {

	/**
	 *
	 * @param loc {@link Localization}
	 * @return the restriction object created based on localization.
	 */
	public static RestrictExam create(Localization loc) {
		ExamRestrictionModel model = createModel(loc);
		return new RestrictExamImpl(model);
	}

	@SuppressWarnings("unused")
	private static ExamRestrictionModel createModel(Localization loc) {
		// here comes the creation of the different models depending on loc.
		return createDummyRestrictionModel();
	}

	private static ExamRestrictionModel createDummyRestrictionModel() {
		ExamRestrictionModel model = new ExamRestrictionModel();
		model.setSubAppCodes(CAS_APPCODE, G3D_APPCODE);
		return model;
	}

}
