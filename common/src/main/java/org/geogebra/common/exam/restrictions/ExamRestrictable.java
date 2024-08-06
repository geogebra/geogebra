package org.geogebra.common.exam.restrictions;

import java.util.Set;

import javax.annotation.Nonnull;

import org.geogebra.common.exam.ExamType;

/**
 * Apply custom restrictions during exams.
 */
public interface ExamRestrictable {

	/**
	 * Apply the restrictions when the exam starts.
	 *
	 * @param examType The exam type.
	 * @param featureRestrictions The feature restrictions for the exam.
	 */
	void applyRestrictions(ExamType examType,
			@Nonnull Set<ExamFeatureRestriction> featureRestrictions);

	/**
	 * Reverse the effects of {@link #applyRestrictions(ExamType, Set)}.
	 *
	 * @param examType The exam type.
	 * @param featureRestrictions The feature restrictions for the exam.
	 */
	void removeRestrictions(ExamType examType,
			@Nonnull Set<ExamFeatureRestriction> featureRestrictions);
}
