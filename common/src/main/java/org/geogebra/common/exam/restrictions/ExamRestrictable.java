package org.geogebra.common.exam.restrictions;

/**
 * Apply custom restrictions during exams.
 */
public interface ExamRestrictable {

	/**
	 * Apply the restrictions when the exam starts.
	 *
	 * @param examRestrictions The restrictions for the current exam.
	 */
	void applyRestrictions(ExamRestrictions examRestrictions);

	/**
	 * Reverse the side effects of {@link #applyRestrictions(ExamRestrictions)}.
	 *
	 * @param examRestrictions The restrictions for the current exam.
	 */
	void unapplyRestrictions(ExamRestrictions examRestrictions);
}
