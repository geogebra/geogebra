package org.geogebra.common.exam.restrictions;

import java.util.Set;

import javax.annotation.Nonnull;

/**
 * Apply custom restrictions during exams.
 */
public interface ExamRestrictable {

	/**
	 * Apply the restrictions when the exam starts.
	 *
	 * @param featureRestrictions The feature restrictions for the exam.
	 */
	void applyRestrictions(@Nonnull Set<ExamFeatureRestriction> featureRestrictions);

	/**
	 * Reverse the effects of {@link #applyRestrictions(Set)}.
	 *
	 * @param featureRestrictions The feature restrictions for the exam.
	 */
	void removeRestrictions(@Nonnull Set<ExamFeatureRestriction> featureRestrictions);
}
