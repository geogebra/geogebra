package org.geogebra.common.main.exam.restriction;

/**
 * Implement this to restrict components/subsystems during exam.
 * When exam finishes, permit() method should restore the normal
 * state/behaviour.
 * */
@Deprecated // use org.geogebra.common.exam.restrictions.ExamRestrictable instead
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
	 * Apply restriction rules that the model contains.
	 */
	void applyExamRestrictions();

}
