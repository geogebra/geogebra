package com.himamis.retex.editor.share.meta;

import static com.himamis.retex.editor.share.meta.MetaArray.CLOSE;
import static com.himamis.retex.editor.share.meta.MetaArray.FIELD;
import static com.himamis.retex.editor.share.meta.MetaArray.OPEN;
import static com.himamis.retex.editor.share.meta.MetaArray.ROW;

import java.util.ArrayList;
import java.util.List;

import com.himamis.retex.editor.share.util.Unicode;

public class MetaModelArrays {

	private static MetaCharacter createArrayComponent(String name, char cas,
			String defaultTex) {
		String tex = defaultTex == null ? cas + "" : defaultTex;

		return new MetaCharacter(name, tex, cas, cas,
				MetaCharacter.CHARACTER);
    }

	private static MetaCharacter createArrayComponent(String name, char cas) {
        return createArrayComponent(name, cas, null);
    }

	ListMetaGroup createArraysGroup() {
		List<MetaComponent> components = new ArrayList<>();
		MetaArray curly = new MetaArray(1, Tag.CURLY);
		curly.setOpen(createArrayComponent(OPEN, '{', "\\left\\{"));
		curly.setClose(createArrayComponent(CLOSE, '}', "\\right\\}"));
		curly.setField(createArrayComponent(FIELD, ','));
		curly.setRow(createArrayComponent(ROW, ';'));
		components.add(curly);

		MetaArray regular = new MetaArray(1, Tag.REGULAR);
		regular.setOpen(createArrayComponent(OPEN, '(', "\\left("));
		regular.setClose(createArrayComponent(CLOSE, ')', "\\right)"));
		regular.setField(createArrayComponent(FIELD, ','));
		regular.setRow(createArrayComponent(ROW, ';'));
		components.add(regular);

		MetaArray square = new MetaArray(1, Tag.SQUARE);
		square.setOpen(createArrayComponent(OPEN, '[', "\\left["));
		square.setClose(createArrayComponent(CLOSE, ']', "\\right]"));
		square.setField(createArrayComponent(FIELD, ','));
		square.setRow(createArrayComponent(ROW, ';'));
		components.add(square);

		MetaArray apostrophes = new MetaArray(1, Tag.APOSTROPHES);
		apostrophes.setOpen(createArrayComponent(OPEN, '\"',
				" \\text{" + Unicode.OPEN_DOUBLE_QUOTE));
		apostrophes.setClose(createArrayComponent(CLOSE, '\"',
				Unicode.CLOSE_DOUBLE_QUOTE + "} "));
		apostrophes.setField(createArrayComponent(FIELD, '\0'));
		apostrophes.setRow(createArrayComponent(ROW, '\0'));
		components.add(apostrophes);

		MetaArray ceil = new MetaArray(1, Tag.CEIL);
		ceil.setOpen(
				createArrayComponent(OPEN, Unicode.LCEIL, "\\left\\lceil "));
		ceil.setClose(
				createArrayComponent(CLOSE, Unicode.RCEIL, "\\right\\rceil "));
		ceil.setField(createArrayComponent(FIELD, ','));
		ceil.setRow(createArrayComponent(ROW, ';'));
		components.add(ceil);

		MetaArray floor = new MetaArray(1, Tag.FLOOR);
		floor.setOpen(
				createArrayComponent(OPEN, Unicode.LFLOOR, "\\left\\lfloor "));
		floor.setClose(
				createArrayComponent(CLOSE, Unicode.RFLOOR,
						"\\right\\rfloor "));
		floor.setField(createArrayComponent(FIELD, ','));
		floor.setRow(createArrayComponent(ROW, ';'));
		components.add(floor);

		return new ListMetaGroup<>(components);
    }

	MetaArray createMatrixGroup() {
		MetaArray matrix = new MetaArray(2, Tag.MATRIX);
		matrix.setOpen(createArrayComponent(OPEN, '{', "\\begin{pmatrix} "));
		matrix.setClose(createArrayComponent(CLOSE, '}', " \\end{pmatrix}"));
		matrix.setField(createArrayComponent(FIELD, ',', " & "));
		matrix.setRow(createArrayComponent(ROW, ',', " \\\\ "));

		return matrix;
    }
}
