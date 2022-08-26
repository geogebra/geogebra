package org.geogebra.common.main.exam.restriction;

/**
 * Class to create restrictions for exams based on the locale.
 */
public class ExamRestrictionFactory {

	/**
	 *
	 * @param examRegion {@link ExamRegion}
	 * @return the restriction object created based on localization.
	 */
	public static RestrictExam create(ExamRegion examRegion) {
		ExamRestrictionModel model = new ExamRestrictionModel();
		if (examRegion != null) {
			examRegion.applyRestrictions(model);
		}
		return new RestrictExamImpl(model);
	}

}
