/*
GeoGebra - Dynamic Mathematics for Schools
Copyright (c) GeoGebra GmbH, Altenbergerstr 69, 4040 Linz, Austria
https://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it
under the terms of the GNU General Public License as published by
the Free Software Foundation.
*/

package org.geogebra.editor.share.catalog;

import java.util.Arrays;
import java.util.List;

import org.geogebra.editor.share.util.Unicode;

/**
 * Factory for creating array and matrix templates.
 */
final class ArrayTemplateFactory {

	List<ArrayTemplate> createArrays() {
		return Arrays.asList(
				new ArrayTemplate(Tag.CURLY, 1,
						new ArrayDelimiter('{', "\\left\\{"),
						new ArrayDelimiter('}', "\\right\\}"),
						new ArrayDelimiter(','),
						new ArrayDelimiter(';')),

				new ArrayTemplate(Tag.REGULAR, 1,
						new ArrayDelimiter('(', "\\left("),
						new ArrayDelimiter(')', "\\right)"),
						new ArrayDelimiter(','),
						new ArrayDelimiter(';')),

				new ArrayTemplate(Tag.SQUARE, 1,
						new ArrayDelimiter('[', "\\left["),
						new ArrayDelimiter(']', "\\right]"),
						new ArrayDelimiter(','),
						new ArrayDelimiter(';')),

				new ArrayTemplate(Tag.APOSTROPHES, 1,
						new ArrayDelimiter('\"', " \\text{" + Unicode.OPEN_DOUBLE_QUOTE),
						new ArrayDelimiter('\"', Unicode.CLOSE_DOUBLE_QUOTE + "} "),
						new ArrayDelimiter('\0'),
						new ArrayDelimiter('\0')),

				new ArrayTemplate(Tag.CEIL, 1,
						new ArrayDelimiter(Unicode.LCEIL, "\\left\\lceil "),
						new ArrayDelimiter(Unicode.RCEIL, "\\right\\rceil "),
						new ArrayDelimiter('\0'),
						new ArrayDelimiter(';')),

				new ArrayTemplate(Tag.FLOOR, 1,
						new ArrayDelimiter(Unicode.LFLOOR, "\\left\\lfloor "),
						new ArrayDelimiter(Unicode.RFLOOR, "\\right\\rfloor "),
						new ArrayDelimiter(','),
						new ArrayDelimiter(';'))
		);
	}

	ArrayTemplate createMatrix() {
		return new ArrayTemplate(Tag.MATRIX, 2,
				new ArrayDelimiter('{', "\\begin{pmatrix} "),
				new ArrayDelimiter('}', " \\end{pmatrix}"),
				new ArrayDelimiter(',', " & "),
				new ArrayDelimiter(',', " \\\\ "));
	}
}
