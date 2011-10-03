/* $Id: MaximaOutputUtilities.java 20 2010-04-15 13:33:05Z davemckain $
 *
 * Copyright (c) 2010, The University of Edinburgh.
 * All Rights Reserved
 */
package geogebra.cas.maxima.jacomax.utilities;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Provides some basic utility methods for handling Maxima outputs.
 * <p>
 * This might become more fully-featured in the future...
 *
 * @author  David McKain
 * @version $Revision: 20 $
 */
public final class MaximaOutputUtilities {
    
    public static final String DEFAULT_INCHAR = "%i";
    public static final String DEFAULT_OUTCHAR = "%o";
    
    public static String stripIntermediateInputPrompts(String rawOutput) {
        return stripIntermediateInputPrompts(rawOutput, "%i");
    }
    
    public static String stripIntermediateInputPrompts(String rawOutput, String inchar) {
        return rawOutput.replaceAll("\\(\\Q" + inchar + "\\E\\d+\\)", "");
    }
    
    /**
     * NOTE: This will go wrong if something gets output that looks like a prompt! Not very
     * much I can do about this...
     * 
     * FIXME: Need to test this on Windows and on older versions of Maxima.
     * 
     * @param rawOutput
     */
    public static SingleLinearOutput parseSingleLinearOutput(String rawOutput) {
        return parseSingleLinearOutput(rawOutput, DEFAULT_INCHAR, DEFAULT_OUTCHAR);
    }
    
    public static SingleLinearOutput parseSingleLinearOutput(String rawOutput, String inchar, String outchar) {
        /* Strip out any intermediate input prompts.
         * (These appear in certain Lisp/OS combinations.) */
        String withoutInputPrompts = stripIntermediateInputPrompts(rawOutput, inchar);

        /* Now split on output prompt */
        Pattern extractPattern = Pattern.compile("(?sm)(.*?)(\\(\\Q" + outchar + "\\E\\d+\\))\\s*(.*?)\\s*");
        Matcher matcher = extractPattern.matcher(withoutInputPrompts);
        if (!matcher.matches()) {
            return null;
        }
        SingleLinearOutput singleLinearOutput = new SingleLinearOutput();
        singleLinearOutput.setOutput(matcher.group(1));
        singleLinearOutput.setOutputPrompt(matcher.group(2));
        singleLinearOutput.setResult(parseLinearResult(matcher.group(3)));
        return singleLinearOutput;
    }
    
    public static String parseSingleLinearOutputResult(String rawOutput) {
        SingleLinearOutput extracted = parseSingleLinearOutput(rawOutput);
        return extracted!=null ? extracted.getResult() : null;
    }
    
    public static String parseLinearResult(String rawResult) {
        StringBuilder resultBuilder = new StringBuilder();
        boolean isInString = false;
        boolean isCompletingBackslash = false;
        char c;
        for (int i=0; i<rawResult.length(); i++) {
            c = rawResult.charAt(i);
            if (isCompletingBackslash) {
                /* Only things I'd expect Maxima to backslash are line terminators and '"' */
                
                /* (NB: I've only ever seen Maxima output single newlines as line terminators,
                 * even on Windows/GCL, and the following simplistic logic reflects this.
                 * I'm not sure if this behaviour is explicit or an underlying Lisp platform thing,
                 * so there's a chance we may have to fix this on more esoteric platforms.)
                 */
                if (c=='\n') {
                    if (isInString) {
                        /* Literal newline */
                        resultBuilder.append(c);
                    }
                    else {
                        /* Newline used to split lines, so ignore */
                    }
                }
                else {
                    resultBuilder.append(c);
                }
                isCompletingBackslash = false;
            }
            else {
                if (c=='\\') {
                    isCompletingBackslash = true;
                }
                else {
                    resultBuilder.append(c);
                    if (c=='"') {
                        isInString ^= true;
                    }
                }
            }
        }
        return resultBuilder.toString();
    }
}
