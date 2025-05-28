package org.geogebra.common.kernel.arithmetic.simplifiers;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;

public class Rationalization {

	/**
	 * Rationalize the fraction.
	 *
	 * @param node to rationalize
	 * @return the rationalized fraction.
	 */
	public @CheckForNull ExpressionValue getResolution(@Nonnull ExpressionNode node) {
        RationalizableFraction fraction = new RationalizableFraction(node);
        return fraction.simplify();
    }
}
