package com.himamis.retex.editor.share.model.traverse;

import com.himamis.retex.editor.share.model.MathComponent;

public interface Traversing {

    /**
     * Process a value locally, without recursion.
     * @param mathComponent value
     * @return
     */
    MathComponent process(MathComponent mathComponent);

}
