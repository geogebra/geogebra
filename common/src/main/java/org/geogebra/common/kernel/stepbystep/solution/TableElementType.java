package org.geogebra.common.kernel.stepbystep.solution;

import org.geogebra.common.main.Localization;

public enum TableElementType implements TableElement {

    POSITIVE("+"),

    NEGATIVE("-"),

    INCREASING("\\nearrow"),

    DECREASING("\\searrow"),

    CONVEX("\\smile"),

    CONCAVE("\\frown"),

    CONVEX_INCREASING("\\rotatebox{90}{\u2937}"),

    CONVEX_DECREASING("\\text{\u2937}"),

    CONCAVE_INCREASING("\\rotatebox{90}{\u2937}"),

    CONCAVE_DECREASING("\\rotatebox{90}{\u2936}"),

    INVALID("|"),

    ZERO("0"),

    VSPACE(" \\; ");

    private final String latex;

    TableElementType(String latex) {
        this.latex = latex;
    }

    public String toLaTeXString(Localization loc) {
        return latex;
    }

}
