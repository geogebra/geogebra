package com.himamis.retex.editor.share.model.traverse;

import com.himamis.retex.editor.share.model.MathComponent;

public interface Traversing {

    /**
	 * Process a value locally, without recursion.
	 * 
	 * @param mathComponent
	 *            value
	 * @return replacement of processed component
	 */
    MathComponent process(MathComponent mathComponent);

}
