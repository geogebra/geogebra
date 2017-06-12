package org.geogebra.common.keyboard;

import org.geogebra.common.main.Localization;

public class KeyboardRowDefinitionProvider {

    private static final String DEFAULT_TOP_ROW = "qwertyuiop";
    private static final String DEFAULT_MIDDLE_ROW = "asdfghjkl";
    private static final String DEFAULT_BOTTOM_ROW = "zxcvbnm";

    private Localization localization;

    public KeyboardRowDefinitionProvider(Localization localization) {
        this.localization = localization;
    }

    /**
     * Queries the keyboard row definitions for the current locale.
     *
     * @return an array of two with corresponding for small and capital letters
     * each containing an array of three corresponding to top, middle and bottom rows.
     */
    public String[][] getKeyboardDefinition() {
        String topRow = localization.getKeyboardRow(1);
        String middleRow = localization.getKeyboardRow(2);
        String bottomRow = localization.getKeyboardRow(3);

        String topRowSmall = topRow.replaceAll("(.).", "$1");
        String middleRowSmall = middleRow.replaceAll("(.).", "$1");
        String bottomRowSmall = bottomRow.replaceAll("(.).", "$1");

        String topRowCapital = topRow.replaceAll(".(.)?", "$1");
        String middleRowCapital = middleRow.replaceAll(".(.)?", "$1");
        String bottomRowCapital = bottomRow.replaceAll(".(.)?", "$1");

        return new String[][]{
                new String[]{topRowSmall, middleRowSmall, bottomRowSmall},
                new String[]{topRowCapital, middleRowCapital, bottomRowCapital}
        };
    }

    /**
     * Queries the keyboard row definitions for the default locale.
     *
     * @return an array of two with corresponding for small and capital letters
     * each containing an array of three corresponding to top, middle and bottom rows.
     */
    public String[][] getDefaultKeyboardDefinition() {
        return new String[][]{
                new String[]{DEFAULT_TOP_ROW, DEFAULT_MIDDLE_ROW, DEFAULT_BOTTOM_ROW},
                new String[]{DEFAULT_TOP_ROW.toUpperCase(),
                        DEFAULT_MIDDLE_ROW.toUpperCase(),
                        DEFAULT_BOTTOM_ROW.toUpperCase()}
        };
    }
}
