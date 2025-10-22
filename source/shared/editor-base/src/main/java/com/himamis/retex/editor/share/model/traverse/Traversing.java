package com.himamis.retex.editor.share.model.traverse;

import com.himamis.retex.editor.share.model.MathComponent;

/**
 * Visitor for the tree of {@link MathComponent}s
 */
@FunctionalInterface
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
