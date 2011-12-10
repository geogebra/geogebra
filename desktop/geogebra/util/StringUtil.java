package geogebra.util;

public class StringUtil extends geogebra.common.util.StringUtil{
	@Override
	protected boolean isRightToLeftChar( char c ) {
    	//CharTableImpl c;
    	return (Character.getDirectionality(c) == Character.DIRECTIONALITY_RIGHT_TO_LEFT); 
    }
}
