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

package org.geogebra.editor.share.util;

import java.util.function.IntPredicate;

import org.geogebra.editor.share.catalog.Tag;
import org.geogebra.editor.share.catalog.TemplateCatalog;
import org.geogebra.editor.share.tree.FunctionNode;
import org.geogebra.editor.share.tree.Node;
import org.geogebra.editor.share.tree.SequenceNode;

/** Layout, creation and state helpers for editable integral commands. */
public final class IntegralHelper {
	public static final int LOWER_LIMIT = 0;
	public static final int UPPER_LIMIT = 1;
	public static final int INTEGRAND = 2;
	public static final int VARIABLE = 3;

	/** Possible formats of an Integral command. */
	public enum IntegralForm {
		/** Integral|NIntegral|IntegralSymbolic(<Function>). */
		INTEGRAND_ONLY,
		/** Integral|IntegralSymbolic(<Function>, <Variable>). */
		INTEGRAND_VARIABLE,
		/** Integral|NIntegral(<Function>, <Start x-value>, <End x-value>). */
		INTEGRAND_LIMITS,
		/** Integral|NIntegral(<Function>, <Variable>, <Start value>, <End value>). */
		INTEGRAND_VARIABLE_LIMITS,
		/** Integral(<Function>, <Start x-value>, <End x-value>, <Boolean evaluate>). */
		INTEGRAND_LIMITS_EVALUATE,
		/** NIntegral(<Function>, <Start x-value>, <Start y-value>, <End x-value>). */
		INTEGRAND_LIMITS_CURVE,
	}

	private IntegralHelper() {
		// utility class
	}

	/**
	 * @param tag template tag
	 * @return whether the tag represents an integral command
	 */
	public static boolean isIntegral(Tag tag) {
		return tag == Tag.INTEGRAL || tag == Tag.N_INTEGRAL || tag == Tag.INTEGRAL_SYMBOLIC;
	}

	/**
	 * @param node editor tree node
	 * @return whether the node is an integral command
	 */
	public static boolean isIntegral(Node node) {
		return node instanceof FunctionNode function && isIntegral(function.getName());
	}

	/**
	 * @param tag template tag
	 * @return whether the integral command has lower and upper limit fields
	 */
	public static boolean hasLimits(Tag tag) {
		return tag == Tag.INTEGRAL || tag == Tag.N_INTEGRAL;
	}

	/**
	 * @param tag integral command tag
	 * @param argumentCount number of command arguments, excluding the command name
	 * @param isVariableArgument predicate whether the argument at given index is a variable
	 * @param isBooleanArgument predicate whether the argument at a given index is a boolean
	 * @return integral argument form, or {@code null} for unsupported argument patterns
	 */
	public static IntegralForm getIntegralForm(Tag tag, int argumentCount,
			IntPredicate isVariableArgument, IntPredicate isBooleanArgument) {
		if (argumentCount == 1) {
			return IntegralForm.INTEGRAND_ONLY;
		}
		if ((tag == Tag.INTEGRAL || tag == Tag.INTEGRAL_SYMBOLIC) && argumentCount == 2
				&& isVariableArgument.test(1)) {
			return IntegralForm.INTEGRAND_VARIABLE;
		}
		if ((tag == Tag.INTEGRAL || tag == Tag.N_INTEGRAL) && argumentCount == 3) {
			return IntegralForm.INTEGRAND_LIMITS;
		}
		if ((tag == Tag.INTEGRAL || tag == Tag.N_INTEGRAL) && argumentCount == 4
				&& isVariableArgument.test(1) && !isBooleanArgument.test(3)) {
			return IntegralForm.INTEGRAND_VARIABLE_LIMITS;
		}
		if (tag == Tag.INTEGRAL && argumentCount == 4 && isBooleanArgument.test(3)) {
			return IntegralForm.INTEGRAND_LIMITS_EVALUATE;
		}
		if (tag == Tag.N_INTEGRAL && argumentCount == 4) {
			return IntegralForm.INTEGRAND_LIMITS_CURVE;
		}
		return null;
	}

	/**
	 * @param tag integral command tag
	 * @param syntaxIndex index of a command syntax line for the tag
	 * @param cas whether the syntax line is from CAS syntax
	 * @return integral argument form, or {@code null} for unsupported syntax lines
	 */
	public static IntegralForm getIntegralForm(Tag tag, int syntaxIndex, boolean cas) {
		/*
		 * Integral CAS syntaxes:
		 * 0: Integral( <Function> )
		 * 1: Integral( <Function>, <Variable> )
		 * 2: Integral( <Function>, <Start x-Value>, <End x-Value> )
		 * 3: Integral( <Function>, <Variable>, <Start Value>, <End Value> )
		 *
		 * Integral non-CAS syntaxes:
		 * 0: Integral( <Function> )
		 * 1: Integral( <Function>, <Variable> )
		 * 2: Integral( <Function>, <Start x-Value>, <End x-Value> )
		 * 3: Integral( <Function>, <Start x-Value>, <End x-Value>, <Boolean Evaluate> )
		 *
		 * NIntegral CAS syntaxes:
		 * 0: NIntegral( <Function>, <Start x-Value>, <End x-Value> )
		 * 1: NIntegral( <Function>, <Variable>, <Start Value>, <End Value> )
		 *
		 * NIntegral non-CAS syntaxes:
		 * 0: NIntegral( <Function> )
		 * 1: NIntegral( <Function>, <Start x-Value>, <End x-Value> )
		 * 2: NIntegral( <Function>, <Start x-Value>, <Start y-Value>, <End x-Value> )
		 *
		 * IntegralSymbolic CAS syntaxes:
		 * 0: IntegralSymbolic( <Function> )
		 * 1: IntegralSymbolic( <Function>, <Variable> )
		 *
		 * IntegralSymbolic non-CAS syntaxes:
		 * 0: IntegralSymbolic( <Function> )
		 * 1: IntegralSymbolic( <Function>, <Variable> )
		 */
		if (tag == Tag.INTEGRAL || tag == Tag.INTEGRAL_SYMBOLIC) {
			if (syntaxIndex == 0) {
				return IntegralForm.INTEGRAND_ONLY;
			}
			if (syntaxIndex == 1) {
				return IntegralForm.INTEGRAND_VARIABLE;
			}
		}
		if (tag == Tag.INTEGRAL) {
			if (syntaxIndex == 2) {
				return IntegralForm.INTEGRAND_LIMITS;
			}
			if (syntaxIndex == 3) {
				return cas ? IntegralForm.INTEGRAND_VARIABLE_LIMITS
						: IntegralForm.INTEGRAND_LIMITS_EVALUATE;
			}
		}
		if (tag == Tag.N_INTEGRAL) {
			if (syntaxIndex == 0) {
				return cas ? IntegralForm.INTEGRAND_LIMITS : IntegralForm.INTEGRAND_ONLY;
			}
			if (syntaxIndex == 1) {
				return cas ? IntegralForm.INTEGRAND_VARIABLE_LIMITS
						: IntegralForm.INTEGRAND_LIMITS;
			}
			if (!cas && syntaxIndex == 2) {
				return IntegralForm.INTEGRAND_LIMITS_CURVE;
			}
		}
		return null;
	}

	/**
	 * @param fieldIndex index of a field in an integral command
	 * @return whether the field is a lower or upper limit
	 */
	public static boolean isLimit(int fieldIndex) {
		return fieldIndex == LOWER_LIMIT || fieldIndex == UPPER_LIMIT;
	}

	/**
	 * @param integral integral command node
	 * @param fieldIndex index of the active field
	 * @return whether focusing the field should make the limits visible
	 */
	public static boolean shouldRevealLimits(FunctionNode integral, int fieldIndex) {
		return hasLimits(integral.getName()) && isLimit(fieldIndex);
	}

	/**
	 * Make the limits visible when the integral command supports limits.
	 * @param integral integral command node
	 */
	public static void revealLimits(FunctionNode integral) {
		if (hasLimits(integral.getName())) {
			integral.setIntegralLimitsVisible(true);
		}
	}

	/**
	 * Determines whether integral limits should be rendered for the current editor state.
	 * Limits are rendered when they are persistently visible, contain content, or the cursor is in
	 * a lower or upper limit field. The cursor case is render-only: it allows hidden limit
	 * placeholders to be drawn without changing the integral's persistent visibility state.
	 * @param integral integral command node
	 * @param currentField field with cursor, or null when rendering without a cursor
	 * @return whether the lower and upper limits should be rendered for this render pass
	 */
	public static boolean shouldRenderLimits(FunctionNode integral, Node currentField) {
		return hasLimits(integral.getName()) && (integral.isIntegralLimitsVisible()
				|| integral.getChild(LOWER_LIMIT).size() > 0
				|| integral.getChild(UPPER_LIMIT).size() > 0
				|| currentField == integral.getChild(LOWER_LIMIT)
				|| currentField == integral.getChild(UPPER_LIMIT));
	}

	/**
	 * Create an integral command node matching a command syntax form.
	 * @param catalog template catalog
	 * @param tag integral command tag
	 * @param form command syntax form
	 * @return new integral command node
	 */
	public static FunctionNode create(TemplateCatalog catalog, Tag tag, IntegralForm form) {
		FunctionNode integral = new FunctionNode(catalog.getGeneral(tag));
		for (int i = 0; i < integral.size(); i++) {
			integral.setChild(i, new SequenceNode());
		}
		if (form == IntegralForm.INTEGRAND_ONLY || form == IntegralForm.INTEGRAND_LIMITS) {
			integral.getChild(VARIABLE).append(catalog.getCharacter("x"));
			integral.setIntegralAutoDefaultVariable(true);
		} else {
			integral.setIntegralAutoDefaultVariable(false);
		}
		integral.setIntegralLimitsVisible(form == IntegralForm.INTEGRAND_LIMITS
				|| form == IntegralForm.INTEGRAND_VARIABLE_LIMITS);
		return integral;
	}
}
