package com.himamis.retex.editor.share.model.traverse;

import com.himamis.retex.editor.share.model.MathComponent;

/**
 * An object that can be traversed and processed by a Traversing object.
 */
public interface Traversable {

    /**
     * Traverse and process this object.
     * @param traversing The object that processes.
     * @return resulting object
     */
    MathComponent traverse(Traversing traversing);

}
