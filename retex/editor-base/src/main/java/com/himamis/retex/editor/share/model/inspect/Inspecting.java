package com.himamis.retex.editor.share.model.inspect;

import com.himamis.retex.editor.share.model.MathComponent;

/**
 * An object that looks for a certain property of an object.
 */
public interface Inspecting {

    boolean check(MathComponent mathComponent);

}
