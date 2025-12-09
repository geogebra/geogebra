/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
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
