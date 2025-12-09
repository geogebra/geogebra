/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

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
