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

package org.geogebra.common.exam.restrictions.expression;

import static org.geogebra.common.util.StreamUtils.flatMap;

import java.util.Set;

import javax.annotation.Nonnull;

import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.filter.ExpressionFilter;

/**
 * Restriction for {@link ExpressionValue}s (used in exam restrictions).
 */
public interface ExpressionRestriction {
	/**
	 * Returns the set of sub-expressions that should be restricted in the given expression.
	 * <p>
	 * If there is at least one sub-expression that is restricted,
	 * then the entire expression should be restricted (unless it's allowed
	 * by at least one other restriction, see {@code getAllowedSubExpressions}).
	 * @param expression the expression to check
	 * @return set of restricted sub-expressions
	 */
	default @Nonnull Set<ExpressionValue> getRestrictedSubExpressions(@Nonnull ExpressionValue expression) {
        return Set.of();
    }

	/**
	 * Return the set of sub-expressions that should be allowed.
	 * <p>
	 * An allowed sub-expression overrides any restrictions placed on an expression.
	 * It serves as an exception for overlapping restriction conditions,
	 * such as restricted booleans, with the exception of boolean arguments
	 * ({@code getRestrictedSubExpressions} should return all booleans,
	 * {@code getAllowedSubExpressions} should return boolean arguments).
	 * @param expression the expression to check
	 * @return set of allowed sub-expressions
	 */
	default @Nonnull Set<ExpressionValue> getAllowedSubExpressions(@Nonnull ExpressionValue expression) {
        return Set.of();
    }

    /**
     * Determine whether an expression is restricted
     * for the combined effect of a set of expression restrictions.
     * @param expression The expression to check.
     * @param restrictions The set of expression restrictions to apply.
     * @return {@code true} if the expression is restricted, {@code false} otherwise.
     */
    static boolean isExpressionRestricted(
            @Nonnull ExpressionValue expression,
            @Nonnull Set<ExpressionRestriction> restrictions
    ) {
        Set<ExpressionValue> restrictedSubExpressions = flatMap(restrictions,
                restriction -> restriction.getRestrictedSubExpressions(expression));
        if (restrictedSubExpressions.isEmpty()) {
            return false;
        }
        Set<ExpressionValue> allowedSubExpressions = flatMap(restrictions,
                restriction -> restriction.getAllowedSubExpressions(expression));
        // If a sub-expression is restricted by any expression restriction
        // and none allows it, then the expression is restricted.
        for (ExpressionValue restrictedSubExpression : restrictedSubExpressions) {
            if (!allowedSubExpressions.contains(restrictedSubExpression)) {
                return true;
            }
        }
        return false;
    }

	/**
	 * Constructs an {@link ExpressionFilter} from a set of {@code ExpressionRestriction}.
	 * @param restrictions the set of expression restrictions
	 * @return the expression filter with the combined effect of the expression restrictions
	 */
	static @Nonnull ExpressionFilter toFilter(@Nonnull Set<ExpressionRestriction> restrictions) {
        return expression -> !isExpressionRestricted(expression, restrictions);
    }

	/**
	 * Constructs an {@link ExpressionFilter} from {@code ExpressionRestriction}s.
	 * @param restrictions expression restrictions
	 * @return the expression filter with the combined effect of the expression restrictions
	 */
	static @Nonnull ExpressionFilter toFilter(@Nonnull ExpressionRestriction... restrictions) {
        return toFilter(Set.of(restrictions));
    }
}
