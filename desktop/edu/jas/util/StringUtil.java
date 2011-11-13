/*
 * $Id: StringUtil.java 2996 2010-02-07 13:32:42Z kredel $
 */

package edu.jas.util;


import java.io.Reader;
import java.io.StringWriter;
import java.io.IOException;

import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;


/**
 * Static String and Reader methods.
 * @author Heinz Kredel
 */

public class StringUtil {


    /**
     * Parse variable list from String.
     * @param s String. Syntax: (n1,...,nk) or (n1 ... nk), brackest are also
     *            optional.
     * @return array of variable names found in s.
     */
    public static String[] variableList(String s) {
        String[] vl = null;
        if (s == null) {
            return vl;
        }
        String st = s.trim();
        if (st.length() == 0) {
            return new String[0];
        }
        if (st.charAt(0) == '(') {
            st = st.substring(1);
        }
        if (st.charAt(st.length() - 1) == ')') {
            st = st.substring(0, st.length() - 1);
        }
        st = st.replaceAll(",", " ");
        List<String> sl = new ArrayList<String>();
        Scanner sc = new Scanner(st);
        while (sc.hasNext()) {
            String sn = sc.next();
            sl.add(sn);
        }
        vl = new String[sl.size()];
        int i = 0;
        for (String si : sl) {
            vl[i] = si;
            i++;
        }
        return vl;
    }


    /**
     * Parse white space delimited String from Reader.
     * @param r Reader.
     * @return next non white space String from r.
     */
    public static String nextString(Reader r) {
        StringWriter sw = new StringWriter();
        try {
            char buffer;
            int i;
            // skip white space
            while ((i = r.read()) > -1) {
                buffer = (char) i;
                if (!Character.isWhitespace(buffer)) {
                    sw.write(buffer);
                    break;
                }
            }
            // read non white space, ignore new lines ?
            while ((i = r.read()) > -1) {
                buffer = (char) i;
                if (Character.isWhitespace(buffer)) {
                    break;
                }
                sw.write(buffer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sw.toString();
    }


    /**
     * Parse String with given delimiter from Reader.
     * @param c delimiter.
     * @param r Reader.
     * @return next String up to c from r.
     */
    public static String nextString(Reader r, char c) {
        StringWriter sw = new StringWriter();
        try {
            char buffer;
            int i;
            // read chars != c, ignore new lines ?
            while ((i = r.read()) > -1) {
                buffer = (char) i;
                if (buffer == c) {
                    break;
                }
                sw.write(buffer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sw.toString();
    }


    /**
     * Parse paired String with given delimiters from Reader.
     * @param b opposite delimiter.
     * @param c delimiter.
     * @param r Reader.
     * @return next nested matching String up to c from r.
     */
    public static String nextPairedString(Reader r, char b, char c) {
        StringWriter sw = new StringWriter();
        try {
            int level = 0;
            char buffer;
            int i;
            // read chars != c, ignore new lines ?
            while ((i = r.read()) > -1) {
                buffer = (char) i;
                if (buffer == b) {
                    level++;
                }
                if (buffer == c) {
                    level--;
                    if (level < 0) {
                        break; // skip last closing 'brace' 
                    }
                }
                sw.write(buffer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sw.toString();
    }

}
