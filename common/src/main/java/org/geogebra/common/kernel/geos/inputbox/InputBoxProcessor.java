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
import org.geogebra.common.kernel.commands.redefinition.RuleCollection;
import org.geogebra.common.kernel.commands.redefinition.RuleCollectionSymbolic;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoInputBox;
import org.geogebra.common.kernel.geos.GeoList;
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

import com.himamis.retex.editor.share.serializer.TeXSerializer;
import com.himamis.retex.editor.share.util.Unicode;

/**
 * Updates linked element for an input box from user input
 */
public class InputBoxProcessor {

	private final GeoInputBox inputBox;
	private GeoElementND linkedGeo;
	private final Kernel kernel;
	private final AlgebraProcessor algebraProcessor;

	private final UserInputConverter userInputConverter = new UserInputConverter();

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

		// first clear temp input, so that the string representation of the input
		// box is correct when updating dependencies
		inputBox.clearTempUserInput();
		InputBoxErrorHandler errorHandler = new InputBoxErrorHandler();
		updateLinkedGeoNoErrorHandling(tpl, errorHandler, content);

		if (errorHandler.errorOccured) {
			if (content.isEmpty(inputBox.isListEditor())) {
				inputBox.setTempUserInput("", "");
			} else {
				inputBox.setTempUserInput(processPlaceholders(content.getEditorInput()),
						processLatexPlaceholders(content.getLaTeX()));
			}

			linkedGeo.setUndefined();
			makeGeoIndependent();
			linkedGeo.resetDefinition(); // same as SetValue(linkedGeo, ?)
			linkedGeo.updateRepaint();
		}
	}

	private String processPlaceholders(String content) {
		if (content == null) {
			return null;
		}
		return content.replaceAll("\\{\\?}",
				"{}");
	}

	private String processLatexPlaceholders(String contentLaTeX) {
		if (contentLaTeX == null) {
			return null;
		}
		return contentLaTeX.replace("?",
				TeXSerializer.PLACEHOLDER);
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

	private void updateLinkedGeoNoErrorHandling(
			StringTemplate tpl, ErrorHandler errorHandler, EditorContent content) {
		if (linkedGeo.isGeoText()) {
			// set content first, make independent later, otherwise there is a conflict
			// between Enter and blur handlers in Web
			String editorInput = content.getEditorInput().replace(Unicode.MINUS, '-');
			((GeoText) linkedGeo).setTextString(editorInput);
			makeGeoIndependent();
			linkedGeo.updateRepaint();
			return;
		}

		String defineText = prependLabel(preprocess(content, tpl), tpl);
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
				.withMultipleUnassignedAllowed().withPreventVariable().withAutocreate(false);
	}

	private String prependLabel(String text, StringTemplate tpl) {
		String defineText = text;
		if (linkedGeo.isGeoLine()) {
			String prefix = linkedGeo.getLabelSimple() + ":";
			// need a: in front of
			// X = (-0.69, 0) + \lambda (1, -2)
			if (!defineText.startsWith(prefix)) {
				defineText = prefix + defineText;
			}
		} else if (linkedGeo instanceof FunctionalNVar || isComplexFunction()) {
			// string like f(x,y)=x^2
			// or f(\theta) = \theta
			defineText = linkedGeo.getLabelSimple() + "("
					+ ((VarString) linkedGeo).getVarString(tpl) + ")=" + defineText;
		}
		return defineText;
	}

	private String preprocess(EditorContent content, StringTemplate tpl) {
		String defineText;
		if (inputBox.isSymbolicModeWithSpecialEditor() && content.hasEntries()) {
			defineText = buildListText(content);
		} else if (content.isEmpty(inputBox.isListEditor())) {
			defineText = "?";
		} else if (linkedGeo.isGeoLine()) {
			defineText = content.getEditorInput();
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
		} else {
			defineText = maybeClampInputForNumeric(content.getEditorInput(), tpl);
		}

		if (inputBox.isSymbolicMode() && inputBox.isListEditor()) {
			defineText = "{" + defineText + "}";
		}

		if (GeoPoint.isComplexNumber(linkedGeo)) {
			defineText = defineText.replace('I', 'i');
		}

		return emptyToUndefined(defineText);
	}

	private String emptyToUndefined(String text) {
		if (!inputBox.isSymbolicModeWithSpecialEditor()) {
			return text;
		}
		if (linkedGeo.isGeoPoint() || linkedGeo.isGeoVector()) {
			return userInputConverter.pointToUndefined(text);
		}
		if (linkedGeo.isGeoList() && ((GeoList) linkedGeo).isMatrix()) {
			return userInputConverter.matrixToUndefined(text);
		}
		return text;
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

	private RuleCollection createRedefinitionRule() {
		RedefinitionRule same = RedefinitionRules.sameClassRule();
		RedefinitionRule point = RedefinitionRules.oneWayRule(
				GeoClass.POINT3D, GeoClass.POINT);
		RedefinitionRule vector = RedefinitionRules.oneWayRule(
				GeoClass.VECTOR3D, GeoClass.VECTOR);
		RedefinitionRule numericAngle = RedefinitionRules.oneWayRule(
				GeoClass.NUMERIC, GeoClass.ANGLE);
		if (inputBox.isSymbolicMode()) {
			return new RuleCollectionSymbolic(same, point, vector, numericAngle);
		} else {
			return new RuleCollection(same, point, vector, numericAngle);
		}
	}

	/**
	 * @param editorState editor content
	 * @param sb builder to append processed result
	 * @return whether it's valid
	 */
	public boolean validate(EditorContent editorState, StringBuilder sb) {
		String toCheck = preprocess(editorState, StringTemplate.defaultTemplate);
		EvalInfo evalInfo = buildEvalInfo();
		GeoElementND el = algebraProcessor.evaluateToGeoElement(toCheck, false,
				evalInfo, linkedGeo);
		sb.append(toCheck);
		return el != null && evalInfo.getRedefinitionRule().allowed(linkedGeo, el);
	}
}
