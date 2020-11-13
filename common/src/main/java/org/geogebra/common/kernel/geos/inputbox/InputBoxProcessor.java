package org.geogebra.common.kernel.geos.inputbox;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.VarString;
import org.geogebra.common.kernel.arithmetic.FunctionalNVar;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.commands.redefinition.RedefinitionRule;
import org.geogebra.common.kernel.commands.redefinition.RedefinitionRules;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoInputBox;
import org.geogebra.common.kernel.geos.GeoInterval;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.kernelND.GeoSurfaceCartesianND;
import org.geogebra.common.kernel.kernelND.GeoVectorND;
import org.geogebra.common.main.error.ErrorHandler;
import org.geogebra.common.plugin.GeoClass;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.debug.Log;

/**
 * Updates linked element for an input box from user input
 */
public class InputBoxProcessor {

	private GeoInputBox inputBox;
	private GeoElementND linkedGeo;
	private final Kernel kernel;
	private AlgebraProcessor algebraProcessor;

	/**
	 * @param inputBox
	 *            parent input box
	 * @param linkedGeo
	 *            linked element
	 */
	public InputBoxProcessor(GeoInputBox inputBox, GeoElementND linkedGeo) {
		this.inputBox = inputBox;
		this.linkedGeo = linkedGeo;
		this.kernel = inputBox.getKernel();
		this.algebraProcessor = kernel.getAlgebraProcessor();
	}

	/**
	 * @param inputText
	 *            user input
	 * @param tpl
	 *            template
	 */
	public void updateLinkedGeo(String inputText, StringTemplate tpl, String[] parts) {
		if (!linkedGeo.isLabelSet() && linkedGeo.isGeoText()) {
			((GeoText) linkedGeo).setTextString(inputText);
			return;
		}

		// first clear temp input, so that the string representation of the input
		// box is correct when updating dependencies
		String tempUserDisplayInput = getAndClearTempUserDisplayInput(inputText);

		InputBoxErrorHandler errorHandler = new InputBoxErrorHandler();
		removeCommas(parts);
		updateLinkedGeoNoErrorHandling(inputText, tpl, errorHandler, parts);

		if (errorHandler.errorOccured) {
			if ("?".equals(inputText)) {
				updateTempInput("", "");
			} else {
				String errorText = parts.length > 0 ? buildMatrixText(parts, getRows()) : inputText;
				updateTempInput(errorText, tempUserDisplayInput);
			}
			linkedGeo.setUndefined();
			makeGeoIndependent();
			linkedGeo.resetDefinition(); // same as SetValue(linkedGeo, ?)
			linkedGeo.updateRepaint();
		}
	}

	private int getRows() {
		return linkedGeo instanceof GeoList  ? ((GeoList) linkedGeo).size()
				: ((GeoVectorND) linkedGeo).getDimension();
	}

	private void removeCommas(String[] parts) {
		for (int i = 0; i < parts.length; i++) {
			parts[i] = StringUtil.preprocessForParser(parts[i],
					kernel.getLocalization().isUsingDecimalComma());
		}
	}

	private String maybeClampInputForNumeric(String inputText, StringTemplate tpl) {
		if (!inputBox.isSymbolicMode() && linkedGeo instanceof GeoNumeric) {
			GeoNumeric number = (GeoNumeric) linkedGeo;
			double num = kernel.getAlgebraProcessor()
					.evaluateToDouble(inputText, true, null);

			if (num < number.getIntervalMin()) {
				return kernel.format(number.getIntervalMin(), tpl);
			} else if (num > number.getIntervalMax()) {
				return kernel.format(number.getIntervalMax(), tpl);
			}
		}

		return inputText;
	}

	private void updateTempInput(String inputText, String tempUserDisplayInput) {
		inputBox.setTempUserDisplayInput(tempUserDisplayInput);
		inputBox.setTempUserEvalInput(inputText);
	}

	/**
	 * Make sure linked geo is independent; otherwise null definition causes NPE
	 */
	private void makeGeoIndependent() {
		try {
			if (!linkedGeo.isIndependent()) {
				GeoElement newGeo = linkedGeo.copy().toGeoElement();
				kernel.getConstruction().replace(linkedGeo.toGeoElement(),
						newGeo);
				linkedGeo = newGeo;
			}
		} catch (Throwable e) {
			Log.warn(e.getMessage());
		}
	}

	private String getAndClearTempUserDisplayInput(String inputText) {
		String tempUserInput = inputBox.getTempUserDisplayInput();
		inputBox.clearTempUserInput();
		return tempUserInput == null ? inputText : tempUserInput;
	}

	private void updateLinkedGeoNoErrorHandling(String inputText,
			StringTemplate tpl, ErrorHandler errorHandler, String[] parts) {
		String defineText = preprocess(inputText, tpl, parts);

		EvalInfo info = new EvalInfo(!kernel.getConstruction().isSuppressLabelsActive(),
				false, false).withSliders(false)
				.withNoRedefinitionAllowed().withPreventingTypeChange()
				.withRedefinitionRule(createRedefinitionRule())
				.withMultipleUnassignedAllowed();

		algebraProcessor.changeGeoElementNoExceptionHandling(linkedGeo,
				defineText, info, false,
				new InputBoxCallback(inputBox), errorHandler);
	}

	private String preprocess(String inputText, StringTemplate tpl, String[] parts) {
		String defineText = maybeClampInputForNumeric(inputText, tpl);
		if (linkedGeo.hasSpecialEditor() && parts.length > 0) {
			defineText = buildListText(parts);
		} else if (linkedGeo.isGeoText()) {
			defineText = "\"" + defineText + "\"";
		} else if ("?".equals(inputText.trim()) || "".equals(inputText.trim())) {
			defineText = "?";
		} else if (linkedGeo.isGeoLine()) {

			// not y=
			// and not Line[A,B]
			if ((defineText.indexOf('=') == -1) && (defineText.indexOf('[') == -1)) {
				// x + 1 changed to
				// y = x + 1
				defineText = "y=" + defineText;
			}

			String prefix = linkedGeo.getLabel(tpl) + ":";
			// need a: in front of
			// X = (-0.69, 0) + \lambda (1, -2)
			if (!defineText.startsWith(prefix)) {
				defineText = prefix + defineText;
			}
		}

		if (linkedGeo instanceof FunctionalNVar	|| isComplexFunction()) {
			if (linkedGeo instanceof GeoInterval
				|| (linkedGeo instanceof GeoFunction
					&& ((GeoFunction) linkedGeo).forceInequality())) {
				defineText = linkedGeo.getLabel(tpl) + ":"
						+ defineText;
			} else {
				// string like f(x,y)=x^2
				// or f(\theta) = \theta
				defineText = linkedGeo.getLabel(tpl) + "("
						+ ((VarString) linkedGeo).getVarString(tpl) + ")=" + defineText;
			}
		}

		if (GeoPoint.isComplexNumber(linkedGeo)) {
			defineText = defineText.replace('I', 'i');
		}

		return defineText;
	}

	private String buildListText(String[] parts) {
		if (linkedGeo instanceof GeoVectorND) {
			return "(" + StringUtil.join(",", parts) + ")";
		} else {
			return buildMatrixText(parts,  ((GeoList) linkedGeo).size());
		}
	}

	private String buildMatrixText(String[] parts, int rows) {
		Log.error(StringUtil.join("::", parts));
		StringBuilder sb = new StringBuilder("{{");
		int cells = parts.length;
		int columns = cells / rows;
		for (int cell = 0; cell < cells; cell++) {
			sb.append(parts[cell]);
			if (cell == cells - 1) {
				sb.append("}}");
			} else if ((cell + 1) % columns == 0) {
				sb.append("},{");
			} else {
				sb.append(',');
			}
		}
		return sb.toString();
	}

	private boolean isComplexFunction() {
		return linkedGeo.isGeoSurfaceCartesian()
				&& ((GeoSurfaceCartesianND) linkedGeo).getComplexVariable() != null;
	}

	private RedefinitionRule createRedefinitionRule() {
		RedefinitionRule same = RedefinitionRules.sameClassRule();
		RedefinitionRule point = RedefinitionRules.oneWayRule(
				GeoClass.POINT3D, GeoClass.POINT);
		RedefinitionRule vector = RedefinitionRules.oneWayRule(
				GeoClass.VECTOR3D, GeoClass.VECTOR);
		RedefinitionRule inequality = RedefinitionRules.oneWayRule(
				GeoClass.INTERVAL, GeoClass.FUNCTION);
		RedefinitionRule inequality2 = RedefinitionRules.oneWayRule(
				GeoClass.FUNCTION, GeoClass.INTERVAL);
		return RedefinitionRules.anyRule(same, point, vector, inequality, inequality2);
	}
}
