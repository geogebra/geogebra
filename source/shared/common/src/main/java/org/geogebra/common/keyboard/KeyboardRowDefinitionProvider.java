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

package org.geogebra.common.keyboard;

import java.util.HashMap;
import java.util.Map;

import org.geogebra.common.main.LocalizationI;

public class KeyboardRowDefinitionProvider {

    private static final String DEFAULT_TOP_ROW = "qwertyuiop";
    private static final String DEFAULT_MIDDLE_ROW = "asdfghjkl";
    private static final String DEFAULT_BOTTOM_ROW = "zxcvbnm";

    private LocalizationI localization;

    public KeyboardRowDefinitionProvider(LocalizationI localization) {
        this.localization = localization;
    }

    /**
     * Queries the keyboard row definitions for the current locale.
     *
     * @return an array of two with corresponding for small and capital letters
     * each containing an array of three corresponding to top, middle and bottom rows.
     */
    @Deprecated
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
     * Queries the lower keys for the current locale.
     *
     * @return an array of three corresponding to top, middle and bottom rows
     */
    public String[] getLowerKeys() {
        return getKeyboardDefinition()[0];
    }

    /**
     * Queries a map, which associates lower keys with upper keys.
     *
     * @return a map which associates lower keys with upper keys
     */
    public Map<String, String> getUpperKeys() {
		Map<String, String> map = new HashMap<>();
        String[][] keyboardDefinition = getKeyboardDefinition();
		for (int i = 0; i < 3; i++) {
            associateKeys(keyboardDefinition[0][i], keyboardDefinition[1][i], map);
        }
        return map;
    }

    /**
     * Queries the lower keys for a default latin ABC keyboard.
     *
     * @return an array of three corresponding to top, middle and bottom rows
     */
    public String[] getDefaultLowerKeys() {
        return new String[]{DEFAULT_TOP_ROW, DEFAULT_MIDDLE_ROW, DEFAULT_BOTTOM_ROW};
    }

    private void associateKeys(String lowerKeys, String upperKeys, Map<String, String> map) {
        for (int i = 0; i < lowerKeys.length() && i < upperKeys.length(); i++) {
            map.put(lowerKeys.substring(i, i + 1), upperKeys.substring(i, i + 1));
        }
    }
}
