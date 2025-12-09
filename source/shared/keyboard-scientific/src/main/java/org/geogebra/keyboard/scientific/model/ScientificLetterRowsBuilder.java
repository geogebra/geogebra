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

package org.geogebra.keyboard.scientific.model;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;

import org.geogebra.keyboard.base.Accents;

class ScientificLetterRowsBuilder {

    private StringBuilder builder = new StringBuilder();
    private Map<String, String> upperKeys;

    String[] rowsFrom(String topRow, String middleRow, String bottomRow) {
        String[] rows = { topRow, middleRow, bottomRow };
        String[][] possibleAccents = new String[rows.length][2];
        int[] accentsLength = new int[rows.length];
        int allAccentsLength = 0;
        for (int i = 0; i < rows.length; i++) {
            possibleAccents[i][0] = getAccents(rows[i], 0, 1);
            possibleAccents[i][1] = getAccents(rows[i], rows[i].length() - 1, -1);
            accentsLength[i] = possibleAccents[i][0].length() + possibleAccents[i][1].length();
            allAccentsLength += accentsLength[i];
        }

        builder.setLength(0);
        for (int i = 0; i < rows.length; i++) {
            String row = rows[i];
            builder.append(row, possibleAccents[i][0].length(),
                    row.length() - possibleAccents[i][1].length());
        }

        String allButtons = builder.toString();
        String[] characters = splitIntoCharacters(allButtons);
        if (upperKeys != null) {
            Arrays.sort(characters, Comparator.comparing(s -> upperKeys.getOrDefault(s, s)));
        } else {
            Arrays.sort(characters);
        }
        int length = characters.length + allAccentsLength;
        int[] lengths = new int[rows.length + 1];
        int rowLength = (int) Math.ceil(length / 3.0f);
        for (int i = 0; i < rows.length; i++) {
            lengths[i + 1] = rowLength - accentsLength[i];
        }
        if (length % 3 == 2) {
            lengths[3] -= 1;
        } else if (length % 3 == 1) {
            lengths[1] -= 1;
            lengths[3] -= 1;
        }

        String[] newRows = new String[rows.length];
        for (int i = 0; i < rows.length; i++) {
            lengths[i + 1] += lengths[i];
            newRows[i] = possibleAccents[i][0] + subrangeToString(characters, lengths[i],
                    lengths[i + 1]) + possibleAccents[i][1];
        }

        return newRows;
    }

    private String getAccents(String string, int from, int direction) {
        builder.setLength(0);
        for (int i = from; i < string.length() && i < 2; i += direction) {
            String c = String.valueOf(string.charAt(i));
            if (Accents.isAccent(c)) {
                builder.append(c);
            } else {
                break;
            }
        }
        return builder.toString();
    }

    private String[] splitIntoCharacters(String text) {
        String[] characters = new String[text.length()];
        int i = 0;
        for (char c : text.toCharArray()) {
            characters[i] = String.valueOf(c);
            i++;
        }
        return characters;
    }

    private String subrangeToString(String[] chars, int from, int to) {
        return String.join("", Arrays.copyOfRange(chars, from, to));
    }

    void setUpperKeys(Map<String, String> upperKeys) {
        this.upperKeys = upperKeys;
    }
}
