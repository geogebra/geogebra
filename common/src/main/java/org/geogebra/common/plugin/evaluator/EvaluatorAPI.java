package org.geogebra.common.plugin.evaluator;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.SymbolicMode;
import org.geogebra.common.kernel.arithmetic.ValidExpression;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.parser.Parser;
import org.geogebra.common.move.ggtapi.models.json.JSONException;
import org.geogebra.common.move.ggtapi.models.json.JSONObject;

import com.himamis.retex.editor.share.editor.MathFieldInternal;
import com.himamis.retex.editor.share.model.MathFormula;
import com.himamis.retex.editor.share.serializer.GeoGebraSerializer;
import com.himamis.retex.editor.share.serializer.Serializer;
import com.himamis.retex.editor.share.serializer.TeXSerializer;

/**
 * API class for the Evaluator object.
 */
public class EvaluatorAPI {

	private static final String LATEX_KEY = "latex";
	private static final String ASCII_CONTENT_KEY = "content";
	private static final String EVAL_KEY = "eval";
	private static final String NAN = "NaN";

	private MathFieldInternal mathFieldInternal;
	private Serializer flatSerializer;
	private Serializer latexSerializer;
	private AlgebraProcessor algebraProcessor;
	private Parser parser;
	private EvalInfo evalInfo;

	/**
	 * Create a new Evaluator API
	 *
	 * @param kernel kernel for processing
	 * @param mathFieldInternal Math Field to create API for
	 */
	public EvaluatorAPI(Kernel kernel, MathFieldInternal mathFieldInternal) {
		this.mathFieldInternal = mathFieldInternal;
		this.algebraProcessor = kernel.getAlgebraProcessor();
		this.parser = kernel.getParser();
		this.flatSerializer = new GeoGebraSerializer();
		this.latexSerializer = new TeXSerializer();
		this.evalInfo = createEvalInfo();
	}

	private EvalInfo createEvalInfo() {
		return new EvalInfo(false, false, false).withCAS(false)
				.withSliders(false).withSymbolicMode(SymbolicMode.NONE);
	}

	/**
	 * Get the value for the evaluator API.
	 *
	 * @return JSON string that contains values from the editor
	 */
	public String getEvaluatorValue() {
		MathFormula formula = getMathFormula();
		String flatString = getFlatString(formula);
		String latexString = getLatexString(formula);
		String evalString = getEvalString(flatString);
		return buildJSONString(flatString, latexString, evalString);
	}

	private MathFormula getMathFormula() {
		return mathFieldInternal.getFormula();
	}

	private String getFlatString(MathFormula formula) {
		return flatSerializer.serialize(formula);
	}

	private String getLatexString(MathFormula formula) {
		return latexSerializer.serialize(formula);
	}

	private ValidExpression parseString(String flatString) {
		try {
			return parser.parseGeoGebraExpression(flatString);
		} catch (Throwable e) {
			return null;
		}
	}

	private String getEvalString(String formula) {
		ValidExpression expression = parseString(formula);
		if (expression == null || !expression.isNumberValue()) {
			return NAN;
		}
		return evaluateExpression(expression);
	}

	private String evaluateExpression(ValidExpression expression) {
		try {
			GeoElementND[] elements = algebraProcessor.processAlgebraCommandNoExceptionHandling(
					expression, false, null, null, evalInfo);
			return processElements(elements);
		} catch (Throwable e) {
			return NAN;
		}
	}

	private String processElements(GeoElementND[] elements) {
		if (elements == null || elements.length > 1) {
			return NAN;
		}
		GeoElementND element = elements[0];
		return element.toValueString(StringTemplate.defaultTemplate);
	}

	private String buildJSONString(String flatString, String latexString, String evalString) {
		JSONObject object = new JSONObject();
		try {
			object.put(LATEX_KEY, latexString).put(ASCII_CONTENT_KEY, flatString)
					.put(EVAL_KEY, evalString);
		} catch (JSONException exception) {
			// Can throw exception for numbers, can be ignored for Strings
		}
		return object.toString();
	}
}
