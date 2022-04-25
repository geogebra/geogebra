package org.geogebra.common.main.exam.restriction;

/**
 * Implement this to restrict components/subsystems during exam.
 * When exam finishes, permit() method should restore the normal
 * state/behaviour.
 * */
public interface Restrictable {

	/**
	 *
	 * @param model contains all the restriction rules.
	 * @return if the model is accepted or not.
	 */
	boolean isExamRestrictionModelAccepted(ExamRestrictionModel model);

	/**
	 * Sets the restriction model.
	 *
	 * @param model contains all the restriction rules.
	 */
	void setExamRestrictionModel(ExamRestrictionModel model);

	/**
	 * Restrict component due to the model.
	 * Apply restriction rules that the model contains..
	 */
	void applyExamRestrictions();

	/**
	 * Clear all restrictions that applyExamRestrictions(model) has made.
	 */
	void cancelExamRestrictions();

}
