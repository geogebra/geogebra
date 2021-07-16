package com.himamis.retex.editor.share.model.inspect;

import com.himamis.retex.editor.share.model.MathComponent;

/**
 * Checks if a MathContainer contains a specific component.
 */
public class ContainsComponent implements Inspecting {

    private MathComponent mComponent;

    public ContainsComponent(MathComponent component) {
        mComponent = component;
    }

    @Override
    public boolean check(MathComponent mathComponent) {
        return mComponent == mathComponent;
    }
}
