package org.geogebra.common.kernel.geos.inputbox;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.VarString;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.FunctionalNVar;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.commands.redefinition.RedefinitionRule;
import org.geogebra.common.kernel.commands.redefinition.RedefinitionRules;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoInputBox;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.GeoSurfaceCartesianND;
import org.geogebra.common.kernel.kernelND.GeoVectorND;
import org.geogebra.common.main.MyError;
import org.geogebra.common.main.error.ErrorHandler;
import org.geogebra.common.plugin.GeoClass;
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
	 * @param content
	 *            user input
	 * @param tpl
	 *            template
	 */
	public void updateLinkedGeo(EditorContent content, StringTemplate tpl) {
		content.removeCommas(kernel.getLocalization());
		String inputText = content.getEditorInput();
		if (!linkedGeo.isLabelSet() && linkedGeo.isGeoText()) {
			((GeoText) linkedGeo).setTextString(inputText);
			return;
		}

		// first clear temp input, so that the string representation of the input
		// box is correct when updating dependencies
		String tempUserDisplayInput = getAndClearTempUserDisplayInput(inputText);
		InputBoxErrorHandler errorHandler = new InputBoxErrorHandler();
		updateLinkedGeoNoErrorHandling(tpl, errorHandler, content);

		if (errorHandler.errorOccured) {
			if ("?".equals(inputText)) {
				updateTempInput("", "");
			} else {
				updateTempInput(inputText, tempUserDisplayInput);
			}
			linkedGeo.setUndefined();
			makeGeoIndependent();
			linkedGeo.resetDefinition(); // same as SetValue(linkedGeo, ?)
			linkedGeo.updateRepaint();
		}
	}

	private String maybeClampInputForNumeric(String inputText, StringTemplate tpl) {
		if (!inputBox.isSymbolicMode() && linkedGeo instanceof GeoNumeric) {
			GeoNumeric number = (GeoNumeric) linkedGeo;

			double num = Double.NaN;
			try {
				ExpressionNode en = kernel.getParser().parseExpression(inputText);
				en.resolveVariables(buildEvalInfo());
				num = en.evaluateDouble();
			} catch (Exception | MyError e) {
				Log.debug("Invalid number " + inputText);
			}
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

	private void updateLinkedGeoNoErrorHandling(
			StringTemplate tpl, ErrorHandler errorHandler, EditorContent content) {
		String defineText = preprocess(content, tpl);
		if (linkedGeo.isPointOnPath() || linkedGeo.isPointInRegion()) {
			GeoPointND val = algebraProcessor.evaluateToPoint(defineText, errorHandler, true);
			if (val != null) {
				((GeoPointND) linkedGeo).setCoords(val.getCoords(), true);
				linkedGeo.updateRepaint();
			}
			return;
		}
		EvalInfo info = buildEvalInfo();

		algebraProcessor.changeGeoElementNoExceptionHandling(linkedGeo,
				defineText, info, false,
				new InputBoxCallback(inputBox), errorHandler);
	}

	private EvalInfo buildEvalInfo() {
		return new EvalInfo(!kernel.getConstruction().isSuppressLabelsActive(),
				false, false).withSliders(false)
				.withNoRedefinitionAllowed().withPreventingTypeChange()
				.withRedefinitionRule(createRedefinitionRule())
				.withMultipleUnassignedAllowed().withPreventVariable();
	}

	private String preprocess(EditorContent content, StringTemplate tpl) {
		String defineText = maybeClampInputForNumeric(content.getEditorInput(), tpl);
		if (linkedGeo.hasSpecialEditor() && content.hasEntries()) {
			defineText = buildListText(content);
		} else if (linkedGeo.isGeoText()) {
			defineText = "\"" + defineText + "\"";
		} else if ("?".equals(content.getEditorInput()) || "".equals(content.getEditorInput())) {
			defineText = "?";
		} else if (linkedGeo.isGeoLine()) {

			if (defineText.startsWith("f(x)=")) {
				defineText = defineText.replace("f(x)=", "y=");
			}

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
			// string like f(x,y)=x^2
			// or f(\theta) = \theta
			defineText = linkedGeo.getLabel(tpl) + "("
					+ ((VarString) linkedGeo).getVarString(tpl) + ")=" + defineText;
		}

		if (GeoPoint.isComplexNumber(linkedGeo)) {
			defineText = defineText.replace('I', 'i');
		}

		return defineText;
	}

	private String buildListText(EditorContent content) {
		if (linkedGeo instanceof GeoVectorND) {
			return content.buildVectorText();
		} else {
			return content.buildMatrixText();
		}
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
		return RedefinitionRules.anyRule(same, point, vector);
	}
}
