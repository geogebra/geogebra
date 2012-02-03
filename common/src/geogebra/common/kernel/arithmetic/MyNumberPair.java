/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

*/

/*
 * GeoVec2D.java
 *
 * Created on 31. August 2001, 11:34
 */

package geogebra.common.kernel.arithmetic;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.StringTemplate;

/** 
 * 
 * @author  Michael
 * @version 
 */
public class MyNumberPair extends MyVecNode {
    
            
    public MyNumberPair(Kernel kernel, ExpressionValue en, ExpressionValue en2) {
		super(kernel, en, en2);
	}

    @Override
	public ExpressionValue deepCopy(Kernel kernel) {
        return new MyNumberPair(kernel, x.deepCopy(kernel), y.deepCopy(kernel));
    }
    

    @Override
	final public String toString(StringTemplate tpl) {         
        StringBuilder sb = new StringBuilder();  
				sb.append(x.toString(tpl));
				sb.append(", ");
				sb.append(y.toString(tpl));
		        
		 return sb.toString();      
    }    
	
}
