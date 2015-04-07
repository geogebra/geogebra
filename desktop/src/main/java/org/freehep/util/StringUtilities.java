// Copyright Freehep 2006.
package org.freehep.util;

import java.util.regex.Pattern;

/**
 * 
 * @author Mark Donszelmann
 * @version $Id: StringUtilities.java,v 1.3 2008-05-04 12:22:35 murkle Exp $
 */
public class StringUtilities {

	private StringUtilities() {
		// static methods only
	}
	
    public static String replace(CharSequence target, CharSequence replacement, String string) {
        return Pattern.compile(quote(target.toString()) /* Pattern.LITERAL jdk 1.4 */).matcher(
            string).replaceAll(/* Matcher. jdk 1.4 */ quoteReplacement(replacement.toString()));
    }	

    /* for jdk 1.4 */
    private static String quote(String s) {
        int slashEIndex = s.indexOf("\\E");
        if (slashEIndex == -1)
            return "\\Q" + s + "\\E";

        StringBuffer sb = new StringBuffer(s.length() * 2);
        sb.append("\\Q");
        slashEIndex = 0;
        int current = 0;
        while ((slashEIndex = s.indexOf("\\E", current)) != -1) {
            sb.append(s.substring(current, slashEIndex));
            current = slashEIndex + 2;
            sb.append("\\E\\\\E\\Q");
        }
        sb.append(s.substring(current, s.length()));
        sb.append("\\E");
        return sb.toString();
    }

    /* for jdk 1.4 */
    private static String quoteReplacement(String s) {
        if ((s.indexOf('\\') == -1) && (s.indexOf('$') == -1))
            return s;
        StringBuffer sb = new StringBuffer();
        for (int i=0; i<s.length(); i++) {
            char c = s.charAt(i);
            if (c == '\\') {
                sb.append('\\'); sb.append('\\');
            } else if (c == '$') {
                sb.append('\\'); sb.append('$');
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}
