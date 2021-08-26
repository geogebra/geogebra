package com.himamis.retex.editor.share.parser;

import com.himamis.retex.editor.share.meta.MetaModel;
import com.himamis.retex.editor.share.model.MathFormula;

/**
 * Parses the text and creates a {@link MathFormula}
 */
public interface Parser {

    MathFormula parse(MetaModel metaModel, String input);

}
