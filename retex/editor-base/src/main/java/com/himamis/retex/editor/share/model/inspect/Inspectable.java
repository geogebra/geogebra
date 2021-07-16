package com.himamis.retex.editor.share.model.inspect;

/**
 * An object that can be inspected if it has a certain property.
 */
public interface Inspectable {

    /**
     * Traverse and inspect this object.
     *
     * @param inspecting The object that is being inspected.
     * @return true if it has the property check by the inspecting object.
     */
    boolean inspect(Inspecting inspecting);

}
