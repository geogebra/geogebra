package com.himamis.retex.editor.share.input;

public class Character {

    public static final String DECIMAL_DIGIT_NUMBER = "Nd";
    public static final String OTHER_NUMBER = "No";
    public static final String LOWERCASE_LETTER = "Ll";
    public static final String UPPERCASE_LETTER = "Lu";
    public static final String OTHER_LETTER = "Lo";
    public static final String OTHER_PUNCTUATION = "Po";
    public static final String START_PUNCTUATION = "Ps";
    public static final String END_PUNCTUATION = "Pe";
    public static final String MATH_SYMBOL = "Sm";
    public static final String CONNECTOR_PUNCTUATION = "Pc";
    public static final String SPACE_SEPARATOR = "Zs";
    public static final String LETTER_NUMBER = "Nl";
    public static final String DASH_PUNCTUATION = "Pd";

    public static boolean charIsTypeOf(char character, String category) {
        return (character + "").matches("\\p{" + category + "}");
    }

}
