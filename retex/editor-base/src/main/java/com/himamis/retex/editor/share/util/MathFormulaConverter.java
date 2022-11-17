package com.himamis.retex.editor.share.util;

import com.himamis.retex.editor.share.editor.AddPlaceholders;
import com.himamis.retex.editor.share.io.latex.ParseException;
import com.himamis.retex.editor.share.io.latex.Parser;
import com.himamis.retex.editor.share.meta.MetaModel;
import com.himamis.retex.editor.share.model.MathFormula;
import com.himamis.retex.editor.share.serializer.TeXSerializer;

public class MathFormulaConverter {
	private final Parser parser;
	private MathFormula formula;
	private final TeXSerializer texSerializer;
	private AddPlaceholders placeholders;
	/**
	 * Constructor
	 */
	public MathFormulaConverter() {
		MetaModel model = new MetaModel();
		formula = MathFormula.newFormula(model);
		parser = new Parser(model);
		texSerializer = new TeXSerializer();
		placeholders = new AddPlaceholders();
	}

	/**
	 * Converst from GGB to MathML style latex.
	 * @param text ggb text.
	 * @return MathML styled text
	 */
	public String convert(String text) {
		buildFormula(text);
		return texSerializer.serialize(formula);
	}

	private void buildFormula(String text) {
		try {
			formula = parser.parse(text);
			placeholders.process(formula.getRootComponent().getArgument(0));
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}
}
