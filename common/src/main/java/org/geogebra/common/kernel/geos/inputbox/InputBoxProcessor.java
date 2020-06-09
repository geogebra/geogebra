package org.geogebra.common.kernel.geos.inputbox;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.FunctionalNVar;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.commands.redefinition.RedefinitionRule;
import org.geogebra.common.kernel.commands.redefinition.RedefinitionRules;
import org.geogebra.common.kernel.geos.GeoInputBox;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.GeoVectorND;
import org.geogebra.common.main.error.ErrorHandler;
import org.geogebra.common.plugin.GeoClass;

import com.himamis.retex.editor.share.util.Unicode;

/**
 * Updates linked element for an input box from user input
 */
public class InputBoxProcessor {

	private GeoInputBox inputBox;
	private GeoElementND linkedGeo;
	private Kernel kernel;
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
	public void updateLinkedGeo(String inputText, StringTemplate tpl) {
		if (!linkedGeo.isLabelSet() && linkedGeo.isGeoText()) {
			((GeoText) linkedGeo).setTextString(inputText);
			return;
		}

		InputBoxErrorHandler errorHandler = new InputBoxErrorHandler();
		updateLinkedGeoNoErrorHandling(inputText, tpl, errorHandler);

		String tempUserDisplayInput = getAndClearTempUserDisplayInput(inputText);

		if (errorHandler.errorOccured) {
			inputBox.setTempUserDisplayInput(tempUserDisplayInput);
			inputBox.setTempUserEvalInput(inputText);
			linkedGeo.setUndefined();
			linkedGeo.updateRepaint();
		}
	}

	private String getAndClearTempUserDisplayInput(String inputText) {
		String tempUserInput = inputBox.getTempUserDisplayInput();
		inputBox.clearTempUserInput();
		return tempUserInput == null ? inputText : tempUserInput;
	}

	private void updateLinkedGeoNoErrorHandling(String inputText,
			StringTemplate tpl, ErrorHandler errorHandler) {
		String defineText = preprocess(inputText, tpl);

		EvalInfo info = new EvalInfo(!kernel.getConstruction().isSuppressLabelsActive(),
				false, false).withSliders(false)
				.withNoRedefinitionAllowed().withPreventingTypeChange()
				.withRedefinitionRule(createRedefinitionRule())
				.withSimplifiedMultiplication();

		algebraProcessor.changeGeoElementNoExceptionHandling(linkedGeo,
				defineText, info, false,
				new InputBoxCallback(this, inputBox), errorHandler);
	}

	private String  preprocess(String inputText, StringTemplate tpl) {
		String defineText = inputText;

		if (linkedGeo instanceof GeoVectorND && ((GeoVectorND) linkedGeo).isColumnEditable()) {
			defineText = "(" + inputText.replace("{", "")
					.replace("}", "") + ")";
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
		} else if (isComplexNumber()) {

			// make sure user can enter regular "i"
			defineText = defineText.replace('i', Unicode.IMAGINARY);

		}
		if (linkedGeo instanceof FunctionalNVar) {
			// string like f(x,y)=x^2
			// or f(\theta) = \theta
			defineText = linkedGeo.getLabel(tpl) + "("
					+ ((FunctionalNVar) linkedGeo).getVarString(tpl) + ")=" + defineText;
		}
		return defineText;
	}

	private RedefinitionRule createRedefinitionRule() {
		RedefinitionRule same = RedefinitionRules.sameClassRule();
		RedefinitionRule point = RedefinitionRules.oneWayRule(
				GeoClass.POINT3D, GeoClass.POINT);
		RedefinitionRule vector = RedefinitionRules.oneWayRule(
				GeoClass.VECTOR3D, GeoClass.VECTOR);
		return RedefinitionRules.anyRule(same, point, vector);
	}

	boolean isComplexNumber() {
		return linkedGeo.isGeoPoint()
				&& ((GeoPointND) linkedGeo).getToStringMode() == Kernel.COORD_COMPLEX;
	}
}
