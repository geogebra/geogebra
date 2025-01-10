package com.himamis.retex.editor.share.meta;

import java.util.ArrayList;
import java.util.List;

import com.himamis.retex.editor.share.util.Unicode;

public class MetaModelArrays {

	private static MetaArrayComponent createArrayComponent(char key,
			String tex) {
		return new MetaArrayComponent(key, tex);
	}

	private static MetaArrayComponent createArrayComponent(char cas) {
		return createArrayComponent(cas, String.valueOf(cas));
	}

	ListMetaGroup<MetaArray> createArraysGroup() {
		List<MetaArray> components = new ArrayList<>();
		MetaArray curly = new MetaArray(1, Tag.CURLY);
		curly.setOpen(createArrayComponent('{', "\\left\\{"));
		curly.setClose(createArrayComponent('}', "\\right\\}"));
		curly.setField(createArrayComponent(','));
		curly.setRow(createArrayComponent(';'));
		components.add(curly);

		MetaArray regular = new MetaArray(1, Tag.REGULAR);
		regular.setOpen(createArrayComponent('(', "\\left("));
		regular.setClose(createArrayComponent(')', "\\right)"));
		regular.setField(createArrayComponent(','));
		regular.setRow(createArrayComponent(';'));
		components.add(regular);

		MetaArray square = new MetaArray(1, Tag.SQUARE);
		square.setOpen(createArrayComponent('[', "\\left["));
		square.setClose(createArrayComponent(']', "\\right]"));
		square.setField(createArrayComponent(','));
		square.setRow(createArrayComponent(';'));
		components.add(square);

		MetaArray apostrophes = new MetaArray(1, Tag.APOSTROPHES);
		apostrophes.setOpen(createArrayComponent('\"',
				" \\text{" + Unicode.OPEN_DOUBLE_QUOTE));
		apostrophes.setClose(createArrayComponent('\"',
				Unicode.CLOSE_DOUBLE_QUOTE + "} "));
		apostrophes.setField(createArrayComponent('\0'));
		apostrophes.setRow(createArrayComponent('\0'));
		components.add(apostrophes);

		MetaArray ceil = new MetaArray(1, Tag.CEIL);
		ceil.setOpen(
				createArrayComponent(Unicode.LCEIL, "\\left\\lceil "));
		ceil.setClose(
				createArrayComponent(Unicode.RCEIL, "\\right\\rceil "));
		ceil.setField(createArrayComponent('\0'));
		ceil.setRow(createArrayComponent(';'));
		components.add(ceil);

		MetaArray floor = new MetaArray(1, Tag.FLOOR);
		floor.setOpen(
				createArrayComponent(Unicode.LFLOOR, "\\left\\lfloor "));
		floor.setClose(
				createArrayComponent(Unicode.RFLOOR,
						"\\right\\rfloor "));
		floor.setField(createArrayComponent(','));
		floor.setRow(createArrayComponent(';'));
		components.add(floor);

		return new ListMetaGroup<>(components);
    }

	MetaArray createMatrixGroup() {
		MetaArray matrix = new MetaArray(2, Tag.MATRIX);
		matrix.setOpen(createArrayComponent('{', "\\begin{pmatrix} "));
		matrix.setClose(createArrayComponent('}', " \\end{pmatrix}"));
		matrix.setField(createArrayComponent(',', " & "));
		matrix.setRow(createArrayComponent(',', " \\\\ "));

		return matrix;
    }
}
