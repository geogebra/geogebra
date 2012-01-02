package geogebra.util;

import geogebra.common.util.NumberFormatAdapter;
import geogebra.common.util.ScientificFormatAdapter;

public class StringUtil extends geogebra.common.util.StringUtil{
	@Override
	protected boolean isRightToLeftChar( char c ) {
    	//CharTableImpl c;
    	return (Character.getDirectionality(c) == Character.DIRECTIONALITY_RIGHT_TO_LEFT); 
    }
}
