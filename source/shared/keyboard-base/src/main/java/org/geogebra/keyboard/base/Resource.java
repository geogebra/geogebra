package org.geogebra.keyboard.base;

/**
 * The items correspond to {@link ResourceType#DEFINED_CONSTANT}.
 */
public enum Resource {
    POWA2("altText.Square"),

    POWAB("altText.Power"),

    EMPTY_IMAGE(""),

    BACKSPACE_DELETE("altText.Backspace"),

    RETURN_ENTER("altText.Enter"),

    LEFT_ARROW("altText.LeftArrow"),

    RIGHT_ARROW("altText.RightArrow"),

    UP_ARROW("altText.UpArrow"),

    DOWN_ARROW("altText.DownArrow"),

    LOG_10("altText.log10"),

    LOG_B("altText.LogB"),

    POWE_X("altText.PowE"),

    POW10_X("altText.PowTen"),

    N_ROOT("altText.Root"),

    A_N("altText.Subscript"),

    ABS("altText.Abs"),

    FLOOR("altText.Floor"),

    CEIL("altText.Ceil"),

    CAPS_LOCK("altText.CapsLockInactive"),

    CAPS_LOCK_ENABLED("altText.CapsLockActive"),

    INTEGRAL("Integral"),

    DERIVATIVE("Derivative"),

    ROOT("altText.SquareRoot"),

    LANGUAGE(""),

    FRACTION("altText.Fraction"),

    INVERSE("altText.Inverse"),

    DEFINITE_INTEGRAL("altText.DefiniteIntegral"),

    SUM("altText.Sum"),

    PRODUCT("altText.Product"),

    LIM("altText.Lim"),

    VECTOR("altText.Vector"),

    ATOMIC_POST("altText.AtomicPost"),

    ATOMIC_PRE("altText.AtomicPre"),

    MIXED_NUMBER("altText.MixedNumber"),

    RECURRING_DECIMAL("altText.RecurringDecimal"),

    POINT_TEMPLATE("Point"),

    VECTOR_TEMPLATE("altText.Vector"),

    MATRIX_TEMPLATE("Matrix");

    public final String altText;

    Resource(String altText) {
        this.altText = altText;
    }

}