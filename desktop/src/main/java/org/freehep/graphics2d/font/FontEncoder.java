// Copyright 2005, FreeHEP.
package org.freehep.graphics2d.font;


public class FontEncoder {

    private FontEncoder() {
    }

    public static String getEncodedString(String string, String tableName) {
        CharTable charTable = Lookup.getInstance().getTable(tableName);
        return getEncodedString(string, charTable);
    }

    /**
     * Returns an unicode encoded string from an ascii encoded string, using the
     * supplied table.
     */
    public static String getEncodedString(String string, CharTable charTable) {
        if (charTable == null)
            return string;

        StringBuffer s = new StringBuffer();
        for (int i = 0; i < string.length(); i++) {
            int enc = string.charAt(i);
            String name = charTable.toName(enc);
            s.append((name != null) ? charTable.toUnicode(name) : (char) enc);
        }
        return s.toString();
    }

}