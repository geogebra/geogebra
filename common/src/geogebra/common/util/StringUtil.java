package geogebra.common.util;

public class StringUtil {
    private static StringBuilder hexSB = null;

    /**
     * converts Color to hex String with RGB values
     * @return
     */


    final public static String toHexString(char c) {
    	int i = c + 0;

    	if (hexSB == null) hexSB = new StringBuilder(8);
    	else hexSB.setLength(0);
    	hexSB.append("\\u");
    	hexSB.append(hexChar[(i & 0xf000) >>> 12]);
    	hexSB.append(hexChar[(i & 0x0f00) >> 8]); // look up low nibble char
    	hexSB.append(hexChar[(i & 0xf0) >>> 4]);
    	hexSB.append(hexChar[i & 0x0f]); // look up low nibble char
    	return hexSB.toString();
    }
    
    final public static String toHexString(String s) {
    	StringBuilder sb = new StringBuilder(s.length() * 6);
    	for (int i = 0 ; i < s.length() ; i++) {
    		sb.append(toHexString(s.charAt(i)));
    	}
    	
    	return sb.toString();
    }

    //     table to convert a nibble to a hex char.
    private static char[] hexChar = { '0', '1', '2', '3', '4', '5', '6', '7',
	    '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

    /**
     * Converts the given unicode string 
     * to an html string where special characters are
     * converted to <code>&#xxx;</code> sequences (xxx is the unicode value
     * of the character)
     * 
     * @author Markus Hohenwarter
     */
    final public static String toHTMLString(String str) {
	if (str == null)
	    return null;

	StringBuilder sb = new StringBuilder();

	// convert every single character and append it to sb
	int len = str.length();
	for (int i = 0; i < len; i++) {
	    char c = str.charAt(i);
	    int code = c;

	    //  standard characters have code 32 to 126
	    if ((code >= 32 && code <= 126)) {
		switch (code) {
		case 60:
		    sb.append("&lt;");
		    break; //   <
		case 62:
		    sb.append("&gt;");
		    break; // >

		default:
		    //do not convert                
		    sb.append(c);
		}
	    }
	    // special characters
	    else {
		switch (code) {
		case 10:
		case 13: // replace LF or CR with <br/>
		    sb.append("<br/>\n");
		    break;

		case 9: // replace TAB with space
		    sb.append("&nbsp;"); // space
		    break;

		default:
		    //  convert special character to escaped HTML               
		    sb.append("&#");
		    sb.append(code);
		    sb.append(';');
		}
	    }
	}
	return sb.toString();
    }

    /**
     * Converts the given unicode string 
     * to a string where special characters are
     * converted to <code>&#encoding;</code> sequences . 
     * The resulting string can be used in XML files.
     */
    public static String encodeXML(String str) {
	if (str == null)
	    return "";

	//  convert every single character and append it to sb
	StringBuilder sb = new StringBuilder();
	int len = str.length();
	for (int i = 0; i < len; i++) {
	    char c = str.charAt(i);
	    switch (c) {
	    case '>':
		sb.append("&gt;");
		break;
	    case '<':
		sb.append("&lt;");
		break;
	    case '"':
		sb.append("&quot;");
		break;
	    case '\'':
		sb.append("&apos;");
		break;
	    case '&':
		sb.append("&amp;");
		break;
	    case '\n':
		sb.append("&#10;");
		break;

	    default:
		sb.append(c);
	    }
	}
	return sb.toString();
    }
}
