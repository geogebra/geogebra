package org.geogebra.common.gui.view.algebra;

import org.geogebra.common.gui.inputfield.HasLastItem;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.algos.AlgoFractionText;
import org.geogebra.common.kernel.algos.Algos;
import org.geogebra.common.kernel.arithmetic.EquationValue;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.cas.AlgoSolve;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.DescriptionMode;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoSymbolic;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.kernel.geos.HasSymbolicMode;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.kernelND.GeoPlaneND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.main.App;
import org.geogebra.common.util.DoubleUtil;
import org.geogebra.common.util.IndexHTMLBuilder;
import org.geogebra.common.util.IndexLaTeXBuilder;

import com.himamis.retex.editor.share.util.Unicode;

/**
 * Utitlity class for AV items
 */
public class AlgebraItem {

	/**
	 * Changes the symbolic flag of a geo or its parent algo
	 *
	 * @param geo
	 *            element that we want to change
	 * @return whether it's symbolic after toggle
	 */
	public static boolean toggleSymbolic(GeoElement geo) {

		if (geo instanceof HasSymbolicMode) {
			if (geo.getParentAlgorithm() instanceof AlgoSolve) {
				return !((AlgoSolve) geo.getParentAlgorithm()).toggleNumeric();
			}
			((HasSymbolicMode) geo).setSymbolicMode(
					!((HasSymbolicMode) geo).isSymbolicMode(), true);
			geo.updateRepaint();
			return ((HasSymbolicMode) geo).isSymbolicMode();

		}
		return false;
	}

	/**
	 * @param geo
	 *            element
	 * @return arrow or approx, depending on symbolic/numeric nature of the
	 *         element
	 */
	public static String getOutputPrefix(GeoElement geo) {
		if (geo instanceof HasSymbolicMode
				&& !((HasSymbolicMode) geo).isSymbolicMode()) {
			if (!(geo.getParentAlgorithm() instanceof AlgoSolve)
					|| ((AlgoSolve) geo.getParentAlgorithm())
							.getClassName() == Commands.NSolve) {
				return Unicode.CAS_OUTPUT_NUMERIC + "";
			}
		}

		return getSymbolicPrefix(geo.getKernel());
	}

	/**
	 * @param geo
	 *            element
	 * @return whether changing symbolic/numeric for this geo will have any
	 *         effect
	 */
	public static boolean isSymbolicDiffers(GeoElement geo) {
		if (!(geo instanceof HasSymbolicMode)) {
			return false;
		}
		if (geo instanceof GeoSymbolic) {
			GeoSymbolic symbolic = (GeoSymbolic) geo;
			if (!(symbolic.getTwinGeo() instanceof HasSymbolicMode)) {
				return false;
			}
		}

		if (geo.getParentAlgorithm() instanceof AlgoSolve) {
			return !allRHSareIntegers((GeoList) geo);
		}
		if (geo.isGeoNumeric()) {
			ExpressionNode def = geo.getDefinition() == null ? null
					: geo.getDefinition().asFraction();
			return def != null && def.unwrap().isExpressionNode();
		}
		HasSymbolicMode sm = (HasSymbolicMode) geo;
		boolean orig = sm.isSymbolicMode();
		String text1 = geo.getLaTeXAlgebraDescription(true,
				StringTemplate.latexTemplate);
		sm.setSymbolicMode(!orig, false);
		String text2 = geo.getLaTeXAlgebraDescription(true,
				StringTemplate.latexTemplate);

		sm.setSymbolicMode(orig, false);
		if (text1 == null) {
			return true;
		}

		return !text1.equals(text2);
	}

	private static boolean allRHSareIntegers(GeoList geo) {
		for (int i = 0; i < geo.size(); i++) {
			if (geo.get(i) instanceof GeoLine
					&& !DoubleUtil.isInteger(((GeoLine) geo.get(i)).getZ())) {
				return false;
			}
			if (geo.get(i) instanceof GeoPlaneND
					&& !DoubleUtil.isInteger(((GeoPlaneND) geo.get(i))
							.getCoordSys().getEquationVector().getW())) {
				return false;
			}
			if (geo.get(i) instanceof GeoList
					&& !allRHSareIntegers(((GeoList) geo.get(i)))) {
				return false;
			}
		}
		return true;
	}

	/**
	 * @param geo
	 *            element
	 * @return whether element is a numeric that can be written as a fraction
	 */
	public static boolean isGeoFraction(GeoElement geo) {
		return geo instanceof GeoNumeric && geo.getDefinition() != null
				&& geo.getDefinition().isFraction();
	}

	/**
	 * @param geo
	 *            element
	 * @param app
	 *            application requesting suggestion (may disallow it)
	 * @return most relevant suggestion
	 */
	public static Suggestion getSuggestions(GeoElement geo, App app) {
		if (app.getConfig().allowsSuggestions()) {
			return getSuggestions(geo);
		}
		return null;
	}

	/**
	 * @param geo
	 *            element
	 * @return most relevant suggestion
	 */
	public static Suggestion getSuggestions(GeoElement geo) {
		if (geo == null || geo.getKernel()
				.getAlgebraStyle() != Kernel.ALGEBRA_STYLE_DEFINITION_AND_VALUE) {
			return null;
		}

		boolean casEnabled = geo.getKernel().getAlgebraProcessor()
				.getCommandDispatcher().isCASAllowed();
		if (casEnabled) {
			return SuggestionSolveForSymbolic.isValid(geo)
					? SuggestionSolveForSymbolic.get(geo)
					: SuggestionSolve.get(geo);
		}
		return null;
	}

	/**
	 * @param kernel
	 *            kernel
	 * @return symbolic prefix (depends on RTL/LTR)
	 */
	public static String getSymbolicPrefix(Kernel kernel) {
		return kernel.getLocalization().rightToLeftReadingOrder
				? Unicode.CAS_OUTPUT_PREFIX_RTL + ""
				: Unicode.CAS_OUTPUT_PREFIX + "";
	}

	/**
	 * @param geo
	 *            element
	 * @return whether element is part of packed output (including header)
	 */
	public static boolean needsPacking(GeoElement geo) {
		return geo != null && geo.getPackedIndex() >= 0;
	}

	/**
	 * @param element
	 *            element
	 * @return whether element is part of packed output; exclude header
	 */
	public static boolean isCompactItem(GeoElement element) {
		return element != null && element.getPackedIndex() > 0;
	}

	/**
	 * @param element
	 *            element
	 * @return formula for "Duplicate"
	 */
	public static String getDuplicateFormulaForGeoElement(GeoElement element) {
		String duplicate = "";
		if ("".equals(element.getDefinition(StringTemplate.defaultTemplate))) {
			duplicate = element.getValueForInputBar();
		} else {
			duplicate = element
					.getDefinitionNoLabel(StringTemplate.editorTemplate);
		}

		return duplicate;
	}

	/**
	 * @param element
	 *            element
	 * @return output text (LaTex or plain)
	 */
	public static String getOutputTextForGeoElement(GeoElement element) {
		String outputText = "";
		if (element.isLaTeXDrawableGeo()
				|| AlgebraItem.isGeoFraction(element)) {
			outputText = element.getLaTeXDescriptionRHS(true,
					StringTemplate.latexTemplate);
		} else {
			if (needsPacking(element)) {
				outputText = element.getAlgebraDescriptionLaTeX();
			} else {
				outputText = element.getAlgebraDescriptionRHSLaTeX();
			}
		}

		return outputText;
	}

	/**
	 * @param geo1
	 *            element
	 * @param builder
	 *            index builder
	 * @param stringTemplate
	 *            string template
	 * @return whether we did append something to the index builder
	 */
	public static boolean buildPlainTextItemSimple(GeoElement geo1,
			IndexHTMLBuilder builder, StringTemplate stringTemplate) {
		int avStyle = geo1.getKernel().getAlgebraStyle();
		if (geo1.isIndependent() && geo1.isGeoPoint()
				&& avStyle == Kernel.ALGEBRA_STYLE_DESCRIPTION) {
			builder.clear();
			builder.append(
					((GeoPointND) geo1).toStringDescription(stringTemplate));
			return true;
		}
		if (geo1.isIndependent() && geo1.getDefinition() == null) {
			geo1.getAlgebraDescriptionTextOrHTMLDefault(builder);
			return true;
		}
		switch (avStyle) {
		case Kernel.ALGEBRA_STYLE_VALUE:
			if (shouldShowOnlyDefinitionForGeo(geo1)) {
				buildDefinitionString(geo1, builder, stringTemplate);
			} else {
				geo1.getAlgebraDescriptionTextOrHTMLDefault(builder);
			}
			return true;

		case Kernel.ALGEBRA_STYLE_DESCRIPTION:
			if (needsPacking(geo1)) {
				geo1.getAlgebraDescriptionTextOrHTMLDefault(builder);
			} else {
				geo1.addLabelTextOrHTML(
						geo1.getDefinitionDescription(stringTemplate), builder);
			}
			return true;

		case Kernel.ALGEBRA_STYLE_DEFINITION:
			buildDefinitionString(geo1, builder, stringTemplate);
			return true;
		default:
		case Kernel.ALGEBRA_STYLE_DEFINITION_AND_VALUE:
			if (needsPacking(geo1)) {
				geo1.getAlgebraDescriptionTextOrHTMLDefault(builder);
				return true;
			}
			return false;
		}
	}

	/**
	 * @param geoElement
	 *            construction element
	 * @param stringBuilder
	 *            builder
	 * @param stringTemplate
	 *            template
	 */
	public static void buildDefinitionString(GeoElement geoElement,
			IndexHTMLBuilder stringBuilder, StringTemplate stringTemplate) {
		String desc = geoElement.getDefinition(stringTemplate);
		if (geoElement.isAlgebraLabelVisible()) {
			geoElement.addLabelTextOrHTML(desc, stringBuilder);
		} else {
			IndexHTMLBuilder.convertIndicesToHTML(desc, stringBuilder);
		}

	}

	/**
	 * @param geo1
	 *            element
	 * @param builder
	 *            index builder
	 * @return whether we did append something to the index builder
	 */
	public static boolean buildPlainTextItemSimple(GeoElement geo1,
			IndexHTMLBuilder builder) {
		return buildPlainTextItemSimple(geo1, builder,
				StringTemplate.defaultTemplate);
	}

	/**
	 * @param geoElement
	 *            element
	 * @param style
	 *            Kenel.ALGEBRA_STYLE_*
	 * @param sb
	 *            builder
	 * @param stringTemplateForPlainText
	 *            string template for building simple plain text item
	 */
	private static void buildText(GeoElement geoElement, int style,
			IndexHTMLBuilder sb, StringTemplate stringTemplateForPlainText) {

		if (style == Kernel.ALGEBRA_STYLE_DESCRIPTION
				&& needsPacking(geoElement)) {
			String value = geoElement
					.getDefinitionDescription(StringTemplate.editorTemplate);
			sb.clear();
			sb.append(value);
		} else {
			buildPlainTextItemSimple(geoElement, sb,
					stringTemplateForPlainText);
		}
	}

	/**
	 * @param geo
	 *            element
	 * @return whether element should be represented by simple text item
	 */
	public static boolean isTextItem(GeoElementND geo) {
		return geo instanceof GeoText && !((GeoText) geo).isLaTeX()
				&& !((GeoText) geo).isTextCommand();
	}

	/**
	 * @param geo
	 *            element
	 * @return whether we should show symbolic switch for the geo
	 */
	public static boolean shouldShowSymbolicOutputButton(GeoElement geo) {
		return isSymbolicDiffers(geo) && !isTextItem(geo);
	}

	/**
	 * add geo to selection with its special points.
	 *
	 * @param geo
	 *            The geo element to add.
	 * @param app
	 *            application
	 */
	public static void addSelectedGeoWithSpecialPoints(GeoElementND geo,
			App app) {
		if (!app.getConfig().hasPreviewPoints()) {
			return;
		}
		app.getSelectionManager().clearSelectedGeos(false, false);
		app.getSelectionManager().addSelectedGeo(geo, false, false);
	}

	/**
	 * @param geoElement
	 *            about we should decide if the outputrow should be shown or not
	 * @param style
	 *            current algebrastyle
	 * @return whether the output should be shown or not
	 */
	public static DescriptionMode getDescriptionModeForGeo(
			GeoElement geoElement, int style) {
		switch (style) {
		case Kernel.ALGEBRA_STYLE_DEFINITION_AND_VALUE:
			return geoElement.needToShowBothRowsInAV();

		case Kernel.ALGEBRA_STYLE_DESCRIPTION:
			if (geoElement.getPackedIndex() == 0) {
				return DescriptionMode.DEFINITION_VALUE;
			}
			if (geoElement.getPackedIndex() > 0) {
				return DescriptionMode.VALUE;
			}
			return geoElement instanceof GeoNumeric
					&& (!geoElement.isIndependent() || (geoElement
							.needToShowBothRowsInAV() == DescriptionMode.DEFINITION_VALUE
							&& geoElement.getParentAlgorithm() == null))
									? DescriptionMode.DEFINITION_VALUE
									: DescriptionMode.DEFINITION;
		case Kernel.ALGEBRA_STYLE_DEFINITION:
			return DescriptionMode.DEFINITION;
		default:
		case Kernel.ALGEBRA_STYLE_VALUE:
			return DescriptionMode.VALUE;
		}
	}

	/**
	 * @param geoElement
	 *            about we should decide if the outputrow should be shown or not
	 * @param style
	 *            current algebrastyle
	 * @return whether the output should be shown or not
	 */
	public static boolean shouldShowOutputRowForAlgebraStyle(
			GeoElement geoElement, int style) {
		if (style == Kernel.ALGEBRA_STYLE_DESCRIPTION) {
			return getDescriptionModeForGeo(geoElement,
					style) != DescriptionMode.DEFINITION;
		} else if ((style == Kernel.ALGEBRA_STYLE_DEFINITION_AND_VALUE
				|| style == Kernel.ALGEBRA_STYLE_VALUE)
				&& shouldShowOnlyDefinitionForGeo(geoElement)) {
			return false;
		}
		return style != Kernel.ALGEBRA_STYLE_VALUE
				&& style != Kernel.ALGEBRA_STYLE_DEFINITION;
	}

	/**
	 * Tells whether AV should show two rows for a geo element.
	 *
	 * @param element
	 *            the element
	 * @param style
	 *            the algebra style
	 * @return true if both rows should be shown.
	 */
	public static boolean shouldShowBothRows(GeoElement element, int style) {
		return ((element
				.needToShowBothRowsInAV() == DescriptionMode.DEFINITION_VALUE
				|| (AlgebraItem.isTextItem(element)
						&& !element.isIndependent()))
				&& shouldShowOutputRowForAlgebraStyle(element, style));
	}

	/**
	 *
	 * @param element
	 *            geo
	 * @param style
	 *            AV style
	 * @param stringTemplate
	 *            string template
	 * @return description string for element to show in AV row; null if element
	 *         prefers showing definition
	 */
	public static String getDescriptionString(GeoElement element, int style,
			StringTemplate stringTemplate) {

		if (element.mayShowDescriptionInsteadOfDefinition()) {
			IndexLaTeXBuilder builder = new IndexLaTeXBuilder();
			buildText(element, style, builder, stringTemplate);
			return getLatexText(builder.toString().replace("^", "\\^{\\;}"));
		}
		return null;
	}

	/**
	 *
	 * @param element
	 *            geo
	 * @param style
	 *            AV style
	 * @return description string for element to show in AV row; null if element
	 *         prefers showing definition
	 */
	public static String getDescriptionString(GeoElement element, int style) {
		return getDescriptionString(element, style,
				StringTemplate.defaultTemplate);
	}

	private static String getLatexText(String text) {
		return "\\text{" + text + '}';
	}

	/**
	 * @param geo1
	 *            geo
	 * @param limit
	 *            max length: fallback to plain text otherwise
	 * @param output
	 *            whether to substitute numbers
	 * @return LaTEX string
	 */
	public static String getLatexString(GeoElement geo1, Integer limit,
			boolean output) {
		Kernel kernel = geo1.getKernel();
		if (!geo1.isDefinitionValid()
				|| (output && !geo1.isLaTeXDrawableGeo())) {
			return null;
		}
		if (geo1.getParentAlgorithm() instanceof AlgoFractionText) {
			return geo1.getAlgebraDescription(StringTemplate.latexTemplate);
		} else if ((kernel.getAlgebraStyle() != Kernel.ALGEBRA_STYLE_VALUE
				&& kernel
						.getAlgebraStyle() != Kernel.ALGEBRA_STYLE_DEFINITION_AND_VALUE)) {
			if (geo1.isIndependent()) {
				return getLatexStringValue(geo1, limit);
			} else if (Algos.isUsedFor(Algos.Expression, geo1)) {
				return geo1.getAssignmentLHS(StringTemplate.latexTemplate)
						+ geo1.getLabelDelimiter() + geo1.getDefinition(
								StringTemplate.latexTemplateHideLHS);
			} else {
				return null;
			}
		}
		return getLatexStringValue(geo1, limit);
	}

	private static String getLatexStringValue(GeoElement geo1, Integer limit) {
		String text = geo1.getLaTeXAlgebraDescription(
				geo1.needToShowBothRowsInAV() != DescriptionMode.DEFINITION,
				StringTemplate.latexTemplate);

		if ((text != null) && (limit == null || (text.length() < limit))) {
			return text;
		}

		return null;
	}

	/**
	 * Tells whether the equation was typed directly from the user
	 *
	 * @param geoElement
	 *            geoElement
	 * @return true if the equation was typed by the user (and not created via
	 *         command or tool)
	 */
	public static boolean isFunctionOrEquationFromUser(
			GeoElementND geoElement) {
		if (geoElement instanceof EquationValue
				|| geoElement instanceof GeoFunction) {
			AlgoElement parentAlgorithm = geoElement.getParentAlgorithm();
			return parentAlgorithm == null
					|| parentAlgorithm.getClassName().equals(Algos.Expression);
		}
		return false;
	}

	/**
	 * Tells whether the output row should be visible for the given object. We
	 * want to show only the definition for implicit equations, functions and
	 * conics created by tool or command in Exam mode
	 *
	 * @param geoElement
	 *            geoElement
	 * @return true if we should only show the definition for the object but not
	 *         output row
	 */
	public static boolean shouldShowOnlyDefinitionForGeo(
			GeoElementND geoElement) {
		if (geoElement instanceof EquationValue
				&& !(geoElement instanceof GeoLine)
				&& geoElement.getKernel().getApplication().isExamStarted()) {

			return !isFunctionOrEquationFromUser(geoElement);
		}
		return false;
	}

	/**
	 * Create provider of texts for ANS button
	 * 
	 * @param app
	 *            app
	 * @return provider of last AV item
	 */
	public static HasLastItem getLastItemProvider(final App app) {
		return new ConstructionItemProvider(app.getKernel().getConstruction());
	}

	/**
	 *
	 * @param element
	 *            the GeoElement for what we need to get the preview for AV
	 * @return the preview string for the given geoelement if there is any
	 */
	public static String getPreviewFormula(GeoElement element,
			StringTemplate stringTemplate) {
		int algebraStyle = element.getKernel().getAlgebraStyle();
		if (element.getParentAlgorithm() instanceof AlgoFractionText) {
			return element.getAlgebraDescription(stringTemplate);
		} else if ((algebraStyle == Kernel.ALGEBRA_STYLE_DESCRIPTION
				|| algebraStyle == Kernel.ALGEBRA_STYLE_VALUE)
				&& !AlgebraItem.isTextItem(element)) {
			return AlgebraItem.getDescriptionString(element, algebraStyle,
					stringTemplate);
		} else {
			return null;
		}
	}

}
