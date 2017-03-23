package com.himamis.retex.editor.share.meta;

import static com.himamis.retex.editor.share.meta.MetaArray.APOSTROPHES;
import static com.himamis.retex.editor.share.meta.MetaArray.ARRAY;
import static com.himamis.retex.editor.share.meta.MetaArray.CLOSE;
import static com.himamis.retex.editor.share.meta.MetaArray.CURLY;
import static com.himamis.retex.editor.share.meta.MetaArray.FIELD;
import static com.himamis.retex.editor.share.meta.MetaArray.LINE;
import static com.himamis.retex.editor.share.meta.MetaArray.MATRIX;
import static com.himamis.retex.editor.share.meta.MetaArray.OPEN;
import static com.himamis.retex.editor.share.meta.MetaArray.REGULAR;
import static com.himamis.retex.editor.share.meta.MetaArray.ROW;
import static com.himamis.retex.editor.share.meta.MetaArray.SQUARE;

import java.util.ArrayList;
import java.util.List;

class MetaModelArrays {

    private static MetaCharacter createArrayComponent(String name, String cas, String defaultTex) {
        String tex = defaultTex == null ? cas : defaultTex;
        char key = cas.length() > 0 ? cas.charAt(0) : 0;
        return new MetaCharacter(name, cas, tex, key, key, MetaCharacter.CHARACTER);
    }

    private static MetaCharacter createArrayComponent(String name, String cas) {
        return createArrayComponent(name, cas, null);
    }

    MetaGroup createArraysGroup() {
        List<MetaComponent> components = new ArrayList<MetaComponent>();

        List<MetaComponent> arrayComponents = new ArrayList<MetaComponent>();
		arrayComponents.add(createArrayComponent(OPEN, "{", "\\lbrace "));
		arrayComponents.add(createArrayComponent(CLOSE, "}", "\\rbrace "));
        arrayComponents.add(createArrayComponent(FIELD, ","));
        arrayComponents.add(createArrayComponent(ROW, ";"));
        components.add(new MetaArray(ARRAY, CURLY, arrayComponents));

        arrayComponents = new ArrayList<MetaComponent>();
        arrayComponents.add(createArrayComponent(OPEN, "(", "\\left("));
        arrayComponents.add(createArrayComponent(CLOSE, ")", "\\right)"));
        arrayComponents.add(createArrayComponent(FIELD, ","));
        arrayComponents.add(createArrayComponent(ROW, ";"));
        components.add(new MetaArray(ARRAY, REGULAR, arrayComponents));

        arrayComponents = new ArrayList<MetaComponent>();
        arrayComponents.add(createArrayComponent(OPEN, "[", "\\left["));
        arrayComponents.add(createArrayComponent(CLOSE, "]", "\\right]"));
        arrayComponents.add(createArrayComponent(FIELD, ","));
        arrayComponents.add(createArrayComponent(ROW, ";"));
        components.add(new MetaArray(ARRAY, SQUARE, arrayComponents));

        arrayComponents = new ArrayList<MetaComponent>();
        arrayComponents.add(createArrayComponent(OPEN, "\"", " \\text{\""));
        arrayComponents.add(createArrayComponent(CLOSE, "\"", "\"} "));
        arrayComponents.add(createArrayComponent(FIELD, "\0"));
        arrayComponents.add(createArrayComponent(ROW, "\0"));
        components.add(new MetaArray(ARRAY, APOSTROPHES, arrayComponents));

        arrayComponents = new ArrayList<MetaComponent>();
        arrayComponents.add(createArrayComponent(OPEN, "|", "|"));
        arrayComponents.add(createArrayComponent(CLOSE, "|", "|"));
        arrayComponents.add(createArrayComponent(FIELD, ","));
        arrayComponents.add(createArrayComponent(ROW, ";"));
        components.add(new MetaArray(ARRAY, LINE, arrayComponents));

        return new ListMetaGroup(MetaModel.ARRAYS, MetaModel.ARRAYS, components);
    }

    MetaGroup createMatrixGroup() {
        List<MetaComponent> arrayComponents = new ArrayList<MetaComponent>();
        arrayComponents.add(createArrayComponent(OPEN, "{", "\\begin{pmatrix} "));
        arrayComponents.add(createArrayComponent(CLOSE, "}", " \\end{pmatrix}"));
        arrayComponents.add(createArrayComponent(FIELD, ",", " & "));
		arrayComponents.add(createArrayComponent(ROW, ",", " \\\\ "));

        return new MetaArray(MATRIX, MATRIX, arrayComponents);
    }
}
