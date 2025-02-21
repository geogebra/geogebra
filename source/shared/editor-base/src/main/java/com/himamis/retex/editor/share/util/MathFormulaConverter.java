package com.himamis.retex.editor.share.util;

import com.himamis.retex.editor.share.editor.AddPlaceholders;
import com.himamis.retex.editor.share.io.latex.ParseException;
import com.himamis.retex.editor.share.io.latex.Parser;
import com.himamis.retex.editor.share.meta.MetaModel;
import com.himamis.retex.editor.share.model.MathFormula;
import com.himamis.retex.editor.share.model.MathSequence;
import com.himamis.retex.editor.share.serializer.TeXSerializer;

public class MathFormulaConverter {
	private final Parser parser;
	private final TeXSerializer texSerializer;
	private final AddPlaceholders placeholders;
	private boolean temporaryInput;
	private String lastInput = "";
	private String lastOutput = "";

	public MathFormulaConverter() {
		this(new MetaModel());
	}

	/**
	 * Constructor
	 */
	public MathFormulaConverter(MetaModel model) {
		parser = new Parser(model);
		texSerializer = new TeXSerializer();
		placeholders = new AddPlaceholders();
	}

	/**
	 * For testing purposes
	 * @return The TeXSerializer
	 */
	TeXSerializer getTexSerializer() {
		return texSerializer;
	}

	/**
	 * Converts from GGB to editor-style latex.
	 * @param text ggb text.
	 * @return MathML styled text
	 */
	public String convert(String text) {
		if (!text.equals(lastInput)) {
			lastInput = text;
			lastOutput = doConvert(text);
		}
		return lastOutput;
	}

	private String doConvert(String text) {
		MathFormula formula;
		try {
			formula = buildFormula(text);
		} catch (ParseException ex) {
			if (temporaryInput) {
				return text;
			}
			throw new RuntimeException(ex);
		}
		return texSerializer.serialize(formula);
	}

	/**
	 * @param text to build a formula from.
	 * @return the built formula.
	 * @throws ParseException when input invalid
	 */
	public MathFormula buildFormula(String text) throws ParseException {
		MathFormula formula = parser.parse(text);
		MathSequence rootComponent = formula.getRootComponent();
		placeholders.process(rootComponent.getArgument(0));
		return formula;
	}

	public void setTemporaryInput(boolean value) {
		this.temporaryInput = value;
	}
}