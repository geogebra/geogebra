/* $Id: SingleLinearOutput.java 5 2010-03-19 15:40:39Z davemckain $
 *
 * Copyright (c) 2010, The University of Edinburgh.
 * All Rights Reserved
 */
package geogebra.cas.maxima.jacomax.utilities;

import java.io.Serializable;

/**
 * Encapsulates all of the components from a single Maxima call generating linear output,
 * such as <tt>string(...);</tt>
 *
 * @author  David McKain
 * @version $Revision: 5 $
 */
public class SingleLinearOutput implements Serializable {
    
    private static final long serialVersionUID = 710345629577430456L;

    private String output;
    private String outputPrompt;
    private String result;

    
    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    
    public String getOutputPrompt() {
        return outputPrompt;
    }
    
    public void setOutputPrompt(String outputPrompt) {
        this.outputPrompt = outputPrompt;
    }

    
    public String getResult() {
        return result;
    }
    
    public void setResult(String result) {
        this.result = result;
    }

    
    @Override
    public String toString() {
        return getClass().getSimpleName()
            + "(output=" + output
            + ",outputPrompt=" + outputPrompt
            + ",result=" + result
            + ")";
    }
}
