/* 
 GeoGebra - Dynamic Mathematics for Everyone
 http://www.geogebra.org

 This file is part of GeoGebra.

 This program is free software; you can redistribute it and/or modify it 
 under the terms of the GNU General Public License as published by 
 the Free Software Foundation.
 
 */

/*
 * Util.java
 *
 * Created on 17. November 2001, 18:23
 */

package geogebra.util;

import geogebra.main.Application;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Comparator;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;

/**
 *
 * @author  Markus Hohenwarter
 * @version 
 */
public class Util extends Object {

    /** Creates new Util */
    public Util() {
    }

    /**
     * copy an object (deep copy)
     *
     final public static Object copy(Object ob) {
     Object ret = null;
     
     try {
     // write object to memory
     ByteArrayOutputStream out = new ByteArrayOutputStream();
     ObjectOutputStream os = new ObjectOutputStream(out);
     os.writeObject(ob);
     os.flush();
     os.close();
     out.close();
     
     // get object from memory
     ByteArrayInputStream in =
     new ByteArrayInputStream(out.toByteArray());
     ObjectInputStream is = new ObjectInputStream(in);
     ret = is.readObject();
     is.close();
     in.close();
     } catch (Exception exc) {
     Application.debug(
     "deep copy of " + ob + " failed:\n" + exc.toString());
     }
     return ret;
     }*/

    /**
     * searches the classpath for a filename and returns a File object
     */
    final public static File findFile(String filename) {
	// search file
	URL url = ClassLoader.getSystemResource(filename);
	return new File(url.getFile());
    }

    /**
     * searches the classpath for a filename and returns an URL object
     */
    final public static URL findURL(String filename) {
	// search file
	URL url = ClassLoader.getSystemResource(filename);
	return url;
    }

    final public static boolean existsHttpURL(URL url) {
	try {
	    HttpURLConnection.setFollowRedirects(false);
	    // note : you may also need
	    //        HttpURLConnection.setInstanceFollowRedirects(false)
	    HttpURLConnection con = (HttpURLConnection) url.openConnection();
	    con.setRequestMethod("HEAD");
	    return (con.getResponseCode() == HttpURLConnection.HTTP_OK);
	} catch (Exception e) {
	    Application.debug("Exception: existsHttpURL: " + url);
	    return false;
	}
    }
    
    private static StringBuilder hexSB = null;

    /**
     * converts Color to hex String with RGB values
     * @param Color
     * @return
     */
    final public static String toHexString(Color col) {
    	byte r = (byte) col.getRed();
    	byte g = (byte) col.getGreen();
    	byte b = (byte) col.getBlue();

    	if (hexSB == null) hexSB = new StringBuilder(8);
    	else hexSB.setLength(0);
    	// RED      
    	hexSB.append(hexChar[(r & 0xf0) >>> 4]);
    	// look up high nibble char             
    	hexSB.append(hexChar[r & 0x0f]); // look up low nibble char
    	// GREEN
    	hexSB.append(hexChar[(g & 0xf0) >>> 4]);
    	// look up high nibble char             
    	hexSB.append(hexChar[g & 0x0f]); // look up low nibble char
    	// BLUE     
    	hexSB.append(hexChar[(b & 0xf0) >>> 4]);
    	// look up high nibble char             
    	hexSB.append(hexChar[b & 0x0f]); // look up low nibble char
    	return hexSB.toString();
        }

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

    /**
     * Returns the main version number of the current JRE, e.g.
     * 1.4 for version 1.4.2_03
     */
    public static double getJavaVersion() {
	String vm_version = System.getProperty("java.version");
	if (vm_version == null)
	    return Double.NaN;

	if (vm_version.startsWith("1.4.2"))
	    return 1.42;
	else
	    vm_version = vm_version.substring(0, 3);
	try {
	    return Double.parseDouble(vm_version);
	} catch (Exception e) {
	    return Double.NaN;
	}
    }

    /**
     * Returns the index of ob in array a
     * @return -1 if ob is not in a
     */
    public static int arrayContains(Object[] a, Object ob) {
	if (a == null)
	    return -1;
	for (int i = 0; i < a.length; i++) {
	    if (a[i] == ob)
		return i;
	}
	return -1;
    }

    /**
     * Adds keylistener recursivley to all subcomponents of container.
     * @param comp
     * @param l
     */
    public static void addKeyListenerToAll(Container cont, KeyListener l) {
	cont.addKeyListener(l);
	Component[] comps = cont.getComponents();
	for (int i = 0; i < comps.length; i++) {
	    if (comps[i] instanceof Container)
		addKeyListenerToAll((Container) comps[i], l);
	    else {
		comps[i].addKeyListener(l);
	    }
	}
    }

    /**
     *  Writes all contents of the given InputStream to a byte array.
     */
    public static byte[] loadIntoMemory(InputStream is) throws IOException {
	ByteArrayOutputStream bos = new ByteArrayOutputStream();
	copyStream(is, bos);
	bos.close();
	return bos.toByteArray();
    }

    /**
     *  Writes all contents of the given InputStream to a String
     */
    public static String loadIntoString(InputStream is) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF8"));
        StringBuilder sb = new StringBuilder();
 
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } 
 
        return sb.toString();
    }

    public static void copyStream(InputStream in, OutputStream out)
	    throws IOException {
	byte[] buf = new byte[4096];
	int len;
	while ((len = in.read(buf)) > -1) {
	    out.write(buf, 0, len);
	}
    }

    /**
     * Replaces special unicode letters (e.g. greek letters)
     * in str by LaTeX strings.
     */
    public static synchronized String toLaTeXString(String str,
	    boolean convertGreekLetters) {
	int length = str.length();
	sbReplaceExp.setLength(0);
	for (int i = 0; i < length; i++) {
	    char c = str.charAt(i);
	    
	    // Guy Hed 30.8.2009
	    // Fix Hebrew 'undefined' problem in Latex text.
	    if( isRightToLeftChar(c) ) {
	      int j;
	      for( j=i; j<length && (isRightToLeftChar(str.charAt(j)) || str.charAt(j)=='\u00a0'); j++ );
	      for( int k=j-1; k>=i ; k-- )
	    	  sbReplaceExp.append(str.charAt(k));
	      sbReplaceExp.append(' ');
	      i=j-1;
	      continue;
	    }
	    // Guy Hed 30.8.2009
	    
	    switch (c) {
	    /*
	     case '(':
	     sbReplaceExp.append("\\left(");
	     break;
	     
	     case ')':
	     sbReplaceExp.append("\\right)");
	     break;
	     */

	    // Exponents
		// added by Loïc Le Coq 2009/11/04
	    case '\u2070': // ^0
		sbReplaceExp.append("^0");
		break;
		
	    case '\u00b9': // ^1
	    sbReplaceExp.append("^1");
		break;
		// end Loïc
	    case '\u00b2': // ^2
			sbReplaceExp.append("^2");
			break;

	    case '\u00b3': // ^3
		sbReplaceExp.append("^3");
		break;

	    case '\u2074': // ^4
		sbReplaceExp.append("^4");
		break;

	    case '\u2075': // ^5
		sbReplaceExp.append("^5");
		break;

	    case '\u2076': // ^6
		sbReplaceExp.append("^6");
		break;
		// added by Loïc Le Coq 2009/11/04
	    case '\u2077': // ^7
	    sbReplaceExp.append("^7");
		break;
	    
	    case '\u2078': // ^8
		sbReplaceExp.append("^8");
		break;
	    
	    case '\u2079': // ^9
		sbReplaceExp.append("^9");
		break;
		// end Loïc Le Coq

	    default:
		if (!convertGreekLetters) {
		    sbReplaceExp.append(c);
		} else {
		    switch (c) {
		    // greek letters
		    case '\u03b1':
			sbReplaceExp.append("\\alpha");
			break;

		    case '\u03b2':
			sbReplaceExp.append("\\beta");
			break;

		    case '\u03b3':
			sbReplaceExp.append("\\gamma");
			break;

		    case '\u03b4':
			sbReplaceExp.append("\\delta");
			break;

		    case '\u03b5':
			sbReplaceExp.append("\\varepsilon");
			break;

		    case '\u03b6':
			sbReplaceExp.append("\\zeta");
			break;

		    case '\u03b7':
			sbReplaceExp.append("\\eta");
			break;

		    case '\u03b8':
			sbReplaceExp.append("\\theta");
			break;

		    case '\u03b9':
			sbReplaceExp.append("\\iota");
			break;

		    case '\u03ba':
			sbReplaceExp.append("\\kappa");
			break;

		    case '\u03bb':
			sbReplaceExp.append("\\lambda");
			break;

		    case '\u03bc':
			sbReplaceExp.append("\\mu");
			break;

		    case '\u03bd':
			sbReplaceExp.append("\\nu");
			break;

		    case '\u03be':
			sbReplaceExp.append("\\xi");
			break;

		    case '\u03bf':
			sbReplaceExp.append("o");
			break;

		    case '\u03c0':
			sbReplaceExp.append("\\pi");
			break;

		    case '\u03c1':
			sbReplaceExp.append("\\rho");
			break;

		    case '\u03c3':
			sbReplaceExp.append("\\sigma");
			break;

		    case '\u03c4':
			sbReplaceExp.append("\\tau");
			break;

		    case '\u03c5':
			sbReplaceExp.append("\\upsilon");
			break;

		    case '\u03c6':
				sbReplaceExp.append("\\phi");
				break;

		    case '\u03d5':
				sbReplaceExp.append("\\varphi");
				break;

		    case '\u03c7':
			sbReplaceExp.append("\\chi");
			break;

		    case '\u03c8':
			sbReplaceExp.append("\\psi");
			break;

		    case '\u03c9':
			sbReplaceExp.append("\\omega");
			break;

		    // GREEK upper case letters				
		    case '\u0393':
			sbReplaceExp.append("\\Gamma");
			break;

		    case '\u0394':
			sbReplaceExp.append("\\Delta");
			break;

		    case '\u0398':
			sbReplaceExp.append("\\Theta");
			break;

		    case '\u039b':
			sbReplaceExp.append("\\Lambda");
			break;

		    case '\u039e':
			sbReplaceExp.append("\\Xi");
			break;

		    case '\u03a0':
			sbReplaceExp.append("\\Pi");
			break;

		    case '\u03a3':
			sbReplaceExp.append("\\Sigma");
			break;

		    case '\u03a6':
			sbReplaceExp.append("\\Phi");
			break;

		    case '\u03a8':
			sbReplaceExp.append("\\Psi");
			break;

		    case '\u03a9':
			sbReplaceExp.append("\\Omega");
			break;

		    default:
			sbReplaceExp.append(c);
		    }

		}
	    }
	}
	return sbReplaceExp.toString();
    }

    private static StringBuilder sbReplaceExp = new StringBuilder(200);
    
    //Guy Hed 30.08.2009
    private static boolean isRightToLeftChar( char c ) {
    	return (Character.getDirectionality(c) == Character.DIRECTIONALITY_RIGHT_TO_LEFT); 
    }
    //Guy Hed 30.08.2009
    
    /**
     * Registers dialog for disposal on escape key-press.
     * 
     * @param dialog JDialog to be closed on escape
     */
    public static void registerForDisposeOnEscape(JDialog dialog) {
	JRootPane root = dialog.getRootPane();
	
	root.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("ESCAPE"), "dispose-on-escape");
	root.getActionMap().put("dispose-on-escape", new DisposeDialogAction(dialog));
    }
    
    /**
     * Removes all characters that are neither letters nor digits from the filename
     * and changes the given file accordingly.
     * @param file
     */
    public static String keepOnlyLettersAndDigits(String name) {
		int length = name != null ? name.length() : 0;
    	
    	StringBuilder sb = new StringBuilder();
		for (int i=0; i < length ; i++) {
			char c = name.charAt(i);
			if (Character.isLetterOrDigit(c) ||
				c == '.' || // Michael Borcherds 2007-11-23
				c == '_')  // underscore
			{
				sb.append(c);
			}
			else
			{
				sb.append('_'); // Michael Borcherds 2007-11-23
			}
		}
		
		if (sb.length() == 0) {
			sb.append("geogebra");
		}
		
		return sb.toString();
	}       
    /**
     * Removes < > " * / ? | \ and replaces them with underscore (_)
	 * Michael Borcherds 2007-11-23
     * @param file
     */
    public static String processFilename(String name) {
		int length = name != null ? name.length() : 0;
    	
    	StringBuilder sb = new StringBuilder();
		for (int i=0; i < length ; i++) {
			char c = name.charAt(i);
			if     (c == '<' ||
					c == '>' ||
					c == '"' ||
					c == ':' ||
					c == '*' ||
					c == '/' ||
					c == '\\' ||
					c == '?' ||
					c == '\u00a3' || // seems to turn into '�' inside zips
					c == '|' )
			{
				sb.append("_");
			}
			else
			{
				sb.append(c);
			}
		}
		
		if (sb.length() == 0) {
			sb.append("geogebra");
		}
		
		return sb.toString();
	}       
    
    private static StringBuilder sb;
	private static Comparator<File> comparator;
    /*
     * returns a string with n instances of s
     * eg string("hello",2) -> "hellohello";
     */
    public static String string(String s, int n) {
    	
    	if (n == 1) return s; // most common, check first
    	if (n < 1) return "";
    	
    	if (sb == null)
    		sb = new StringBuilder(); 
    	
    	sb.setLength(0);
    	
    	for (int i = 0 ; i < n ; i++) {
    		sb.append(s);
    	}
    	
    	return sb.toString();
    }
    
    public static String removeSpaces(String str) {
    	
    	if (str == null || str.length() == 0) return "";

    	if (sb == null)
    		sb = new StringBuilder(); 
    	
    	sb.setLength(0);
    	char c;
    	
    	for (int i = 0 ; i < str.length() ; i++) {
    		c = str.charAt(i);
    		if (c != ' ')
    			sb.append(c);
    	}
    	
    	return sb.toString();
   	
    }

    /**
     * Removes spaces from the start and end
     * Not the same as trim - it removes ASCII control chars eg tab
	 * Michael Borcherds 2007-11-23
     * @param str
     */
    public static String trimSpaces(String str) {

    	int len = str.length();
    	
    	if (len == 0) return "";
    	
    	int start = 0;
    	while (str.charAt(start) == ' ' && start < len - 1) start++;
    	
    	int end = len;
    	while (str.charAt(end - 1) == ' ' && end > start) end --;
    	
    	if (start == end)
    		return "";
    	
    	return str.substring(start, end);
    	
	}

	public static StringBuffer resetStringBuffer(StringBuffer high) {
		if (high == null) return new StringBuffer();
		high.setLength(0);
		return high;
	}       
    
	/**
	 * Returns a comparator for GeoText objects.
	 * If equal, doesn't return zero (otherwise TreeSet deletes duplicates)
	 */
	public static Comparator<File> getFileComparator() {
		if (comparator == null) {
			comparator = new Comparator<File>() {
			      public int compare(File itemA, File itemB) {

				        return itemA.getName().compareTo(itemB.getName());
			      }
				        
			};
		}
		
		return comparator;
	}


}

