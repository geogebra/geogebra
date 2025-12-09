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

package org.geogebra.editor.share.util;

import org.geogebra.editor.share.catalog.TemplateCatalog;
import org.geogebra.editor.share.editor.AddPlaceholders;
import org.geogebra.editor.share.io.latex.ParseException;
import org.geogebra.editor.share.io.latex.Parser;
import org.geogebra.editor.share.serializer.TeXSerializer;
import org.geogebra.editor.share.tree.Formula;
import org.geogebra.editor.share.tree.SequenceNode;

public class FormulaConverter {
	private final Parser parser;
	private final TeXSerializer texSerializer;
	private final AddPlaceholders placeholders;
	private boolean temporaryInput;
	private String lastInput = "";
	private String lastOutput = "";

	public FormulaConverter() {
		this(new TemplateCatalog());
	}

	/**
	 * Constructor
	 */
	public FormulaConverter(TemplateCatalog catalog) {
		parser = new Parser(catalog);
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
		Formula formula;
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
	public Formula buildFormula(String text) throws ParseException {
		Formula formula = parser.parse(text);
		SequenceNode rootComponent = formula.getRootNode();
		placeholders.process(rootComponent.getChild(0));
		return formula;
	}

	public void setTemporaryInput(boolean value) {
		this.temporaryInput = value;
	}
}