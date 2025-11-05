/*
GeoGebra - Dynamic Mathematics for Schools
Copyright (c) GeoGebra GmbH, Altenbergerstr 69, 4040 Linz, Austria
https://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it
under the terms of the GNU General Public License as published by
the Free Software Foundation.
*/

package org.geogebra.editor.share.input.adapter;

import java.util.Arrays;
import java.util.List;

import org.geogebra.editor.share.editor.MathFieldInternal;

public class FunctionsAdapter extends StringInput {

    private static final List<String> fnList = Arrays.asList("sin", "cos", "tan", "ln", "log",
            "sinh", "cosh", "tanh", "asin", "acos", "atan", "arcsin", "arccos", "arctan", "sen",
            "arcsen", "tg", "tgh", "senh", "random", "nroot", "$defint", "$prodeq", "$sumeq",
            "$limeq", "$vec", "$atomicpost", "$atomicpre");

    @Override
    public void commit(MathFieldInternal mfi, String input) {
        commitFunction(mfi, input);
    }

    @Override
    public boolean test(String input) {
        return fnList.contains(input);
    }
}
