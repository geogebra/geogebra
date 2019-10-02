package org.geogebra.common.kernel.geos;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.FunctionalNVar;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.main.MyError;
import org.geogebra.common.main.MyError.Errors;
import org.geogebra.common.plugin.Operation;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.common.util.debug.Log;

import com.himamis.retex.editor.share.util.Unicode;

/**
 * Updates linked element for an input box from user input
 */
public class InputBoxProcessor implements AsyncOperation<GeoElementND> {
	private GeoInputBox inputBox;
	private GeoElementND linkedGeo;
	private Kernel kernel;

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
	}

	/**
	 * @param inputText
	 *            user input
	 * @param tpl
	 *            template
	 * @param useRounding
	 *            whether to use rounding
	 */
	public void updateLinkedGeo(String inputText, StringTemplate tpl, boolean useRounding) {
		if (!linkedGeo.isLabelSet() && linkedGeo.isGeoText()) {
			((GeoText)linkedGeo).setTextString(inputText);
			return;
		}
		String defineText = preprocess(inputText, tpl);

		ExpressionNode parsed = null;

		if (linkedGeo.isGeoNumeric()) {
			try {
				parsed = kernel.getParser().parseExpression(inputText);
			} catch (Throwable e) {
				// nothing to do
			}
		}

		// for a simple number, round it to the textfield setting (if set)
		if (parsed != null && parsed.isConstant() && !linkedGeo.isGeoAngle()
				&& useRounding) {
			try {
				// can be a calculation eg 1/2+3
				// so use full GeoGebra parser
				double num = kernel.getAlgebraProcessor()
						.evaluateToDouble(inputText, false, null);
				defineText = kernel.format(num, tpl);

			} catch (Exception e) {
				// user has entered eg 33+
				// do nothing
				e.printStackTrace();
			}
		}

		try {
			if (linkedGeo instanceof GeoNumeric && linkedGeo.isIndependent() && parsed != null
					&& parsed.isConstant()) {
				// can be a calculation eg 1/2+3
				// so use full GeoGebra parser
				kernel.getAlgebraProcessor().evaluateToDouble(defineText, false,
						(GeoNumeric) linkedGeo);

				// setValue -> avoid slider range changing

				linkedGeo.updateRepaint();

			} else {
				EvalInfo info = new EvalInfo(!kernel.getConstruction().isSuppressLabelsActive(),
						linkedGeo.isIndependent(), false).withSliders(false);

				kernel.getAlgebraProcessor().changeGeoElementNoExceptionHandling(linkedGeo,
						defineText, info, true, this, kernel.getApplication().getErrorHandler());
				return;
			}
		} catch (MyError e1) {
			kernel.getApplication().showError(e1);
			return;
		} catch (Exception e1) {
			Log.error(e1.getMessage());
			showError();
			return;
		}
		inputBox.setLinkedGeo(linkedGeo);
	}

	private String preprocess(String inputText, StringTemplate tpl) {
		String defineText = inputText;

		if (linkedGeo.isGeoText()) {
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

			// z=2 doesn't work for complex numbers (parses to
			// GeoNumeric)
			defineText = defineText + "+0" + Unicode.IMAGINARY;

		} else if (linkedGeo instanceof FunctionalNVar) {
			// string like f(x,y)=x^2
			// or f(\theta) = \theta
			defineText = linkedGeo.getLabel(tpl) + "("
					+ ((FunctionalNVar) linkedGeo).getVarString(tpl) + ")=" + defineText;
		}
		return defineText;
	}

	private boolean isComplexNumber() {
		return linkedGeo.isGeoPoint()
				&& ((GeoPointND) linkedGeo).getToStringMode() == Kernel.COORD_COMPLEX;
	}

	@Override
	public void callback(GeoElementND obj) {
		if (isComplexNumber()) {
			ExpressionNode def = obj.getDefinition();
			if (def != null && def.getOperation() == Operation.PLUS && def.getRight()
					.toString(StringTemplate.defaultTemplate).equals("0" + Unicode.IMAGINARY)) {
				obj.setDefinition(def.getLeftTree());
				inputBox.setLinkedGeo(obj);
				obj.updateRepaint();
				return;
			}

		}
		inputBox.setLinkedGeo(obj);
	}

	private void showError() {
		kernel.getApplication().showError(Errors.InvalidInput);
	}
}
