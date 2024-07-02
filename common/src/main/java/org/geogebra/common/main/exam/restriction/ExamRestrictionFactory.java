package org.geogebra.common.main.exam.restriction;

import org.geogebra.common.exam.ExamType;

/**
 * Class to create restrictions for exams based on the locale.
 */
@Deprecated // use org.geogebra.common.exam API instead
public class ExamRestrictionFactory {

	/**
	 *
	 * @param examType {@link ExamType}
	 * @return the restriction object created based on localization.
	 */
	public static RestrictExam create(ExamType examType) {
		ExamRestrictionModel model = new ExamRestrictionModel();
		if (examType != null) {
			examType.setDefaultSubAppCode(model);
			examType.applyRestrictions(model);
		}
		return new RestrictExamImpl(model);
	}

}
