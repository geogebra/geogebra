package com.himamis.retex.editor.share.input.adapter;

import com.himamis.retex.editor.share.editor.MathFieldInternal;
import com.himamis.retex.editor.share.meta.MetaModel;

public class SymbolsAdapter extends StringInput {

    private MetaModel metaModel;

    public SymbolsAdapter(MetaModel metaModel) {
        this.metaModel = metaModel;
    }

    @Override
    public void commit(MathFieldInternal mfi, String input) {
        typeCharacter(mfi, input.charAt(0));
    }

    @Override
    public boolean test(String input) {
        return input.length() == 1 && metaModel.isSymbol(input);
    }
}
