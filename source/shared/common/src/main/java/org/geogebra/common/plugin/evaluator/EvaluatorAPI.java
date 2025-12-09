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

package org.geogebra.common.plugin.evaluator;

import java.util.HashMap;

import org.geogebra.common.io.EditorStateDescription;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.SymbolicMode;
import org.geogebra.common.kernel.arithmetic.ValidExpression;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.parser.Parser;
import org.geogebra.common.main.ScreenReader;
import org.geogebra.common.util.SyntaxAdapterImpl;
import org.geogebra.editor.share.editor.MathFieldInternal;
import org.geogebra.editor.share.serializer.GeoGebraSerializer;
import org.geogebra.editor.share.serializer.ScreenReaderSerializer;
import org.geogebra.editor.share.serializer.Serializer;
import org.geogebra.editor.share.serializer.SolverSerializer;
import org.geogebra.editor.share.serializer.TeXSerializer;
import org.geogebra.editor.share.tree.Formula;

import com.himamis.retex.renderer.share.serialize.SerializationAdapter;

/**
 * API class for the Evaluator object.
 */
public class EvaluatorAPI {

	private static final String LATEX_KEY = "latex";
	private static final String SOLVER_KEY = "solver";
	private static final String ASCII_CONTENT_KEY = "content";
	private static final String EVAL_KEY = "eval";
	private static final String ALT_TEXT_KEY = "altText";
	private static final String NAN = "NaN";

	private final MathFieldInternal mathFieldInternal;
	private final Serializer flatSerializer;
	private final Serializer latexSerializer;

	private final Serializer solverSerializer;
	private final AlgebraProcessor algebraProcessor;
	private final Parser parser;
	private final EvalInfo evalInfo;
	private final SerializationAdapter serializationAdapter;

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
		this.flatSerializer = new GeoGebraSerializer(null);
		this.latexSerializer = new TeXSerializer();
		this.solverSerializer = new SolverSerializer();
		this.evalInfo = createEvalInfo();
		this.serializationAdapter = ScreenReader.getSerializationAdapter(kernel.getApplication());
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
	public HashMap<String, Object> getEvaluatorValue() {
		Formula formula = getMathFormula();

		String flatString = getFlatString(formula);
		String latexString = getLatexString(formula);
		String solverString = getSolverString(formula);
		String evalString = getEvalString(flatString);
		String altTextString = getAltTextString();

		HashMap<String, Object> map = new HashMap<>();

		map.put(ASCII_CONTENT_KEY, flatString);
		map.put(LATEX_KEY, latexString);
		map.put(SOLVER_KEY, solverString);
		map.put(EVAL_KEY, evalString);
		map.put(ALT_TEXT_KEY, altTextString);

		return map;
	}

	private String getAltTextString() {
		return ScreenReaderSerializer.fullDescription(
				getMathFormula().getRootNode(), serializationAdapter);
	}

	private Formula getMathFormula() {
		return mathFieldInternal.getFormula();
	}

	private String getFlatString(Formula formula) {
		return flatSerializer.serialize(formula);
	}

	private String getLatexString(Formula formula) {
		return latexSerializer.serialize(formula);
	}

	private String getSolverString(Formula formula) {
		return solverSerializer.serialize(formula);
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

	/**
	 * @param stateStr
	 *            JSON encoded state {content: text, caret: [int, int, int]}
	 */
	public void setEditorState(String stateStr) {
		EditorStateDescription state = EditorStateDescription.fromJSON(stateStr);
		if (state != null) {
			mathFieldInternal.parse(state.getContent());
			mathFieldInternal.setCaretPath(state.getCaretPath());
		}
	}

	/**
	 * @param formula LaTeX formula
	 */
	public void evalLaTeX(String formula) {
		String plainText = new SyntaxAdapterImpl(parser.getKernel()).convertLaTeXtoGGB(formula);
		mathFieldInternal.parse(plainText);
	}

	/**
	 * @param formula LaTeX, MathML or AsciiMath formula
	 */
	public void evalInput(String formula) {
		String plainText = new SyntaxAdapterImpl(parser.getKernel()).convert(formula);
		mathFieldInternal.parse(plainText);
	}

	public String getText() {
		return mathFieldInternal.getText();
	}

}
