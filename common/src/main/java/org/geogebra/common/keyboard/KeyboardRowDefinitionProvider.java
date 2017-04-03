package org.geogebra.common.keyboard;

import org.geogebra.common.main.Localization;

public class KeyboardRowDefinitionProvider {

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
        String topRow = localization.getMenu("Keyboard.row1");
        String middleRow = localization.getMenu("Keyboard.row2");
        String bottomRow = localization.getMenu("Keyboard.row3");

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
}
