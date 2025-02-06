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
	 * @param featureRestrictions The feature restrictions for the exam.
	 * @param examType The type of exam.
	 */
	void applyRestrictions(@Nonnull Set<ExamFeatureRestriction> featureRestrictions,
			@Nonnull ExamType examType);

	/**
	 * Reverse the effects of {@link #applyRestrictions(Set, ExamType)}.
	 *
	 * @param featureRestrictions The feature restrictions for the exam.
	 * @param examType The type of exam
	 */
	void removeRestrictions(@Nonnull Set<ExamFeatureRestriction> featureRestrictions,
			@Nonnull ExamType examType);
}
