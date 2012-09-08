package geogebra.common.gui.inputfield;

import geogebra.common.kernel.arithmetic.ExpressionNodeConstants;
import geogebra.common.main.App;
import geogebra.common.util.Unicode;

import java.util.HashMap;

public class AltKeys {
	
	public static HashMap<Character, String> LookupLower = null, LookupUpper = null;

	static {
		LookupLower = new HashMap<Character, String>();
		LookupUpper = new HashMap<Character, String>();
		
		LookupLower.put('A', Unicode.alpha+"");
		LookupUpper.put('A', Unicode.Alpha+"");
		LookupLower.put('B', Unicode.beta+"");
		LookupUpper.put('B', Unicode.Beta+"");
		LookupLower.put('D', Unicode.delta+"");
		LookupUpper.put('D', Unicode.Delta+"");
		LookupLower.put('E', Unicode.EULER_STRING);
		LookupUpper.put('E', Unicode.EULER_STRING);
		LookupLower.put('F', Unicode.phi+"");
		LookupUpper.put('F', Unicode.Phi+"");
		LookupLower.put('G', Unicode.gamma+"");
		LookupUpper.put('G', Unicode.Gamma+"");
		LookupLower.put('I', Unicode.IMAGINARY);
		LookupUpper.put('I', Unicode.IMAGINARY);
		LookupLower.put('L', Unicode.lambda+"");
		LookupUpper.put('L', Unicode.Lambda+"");
		LookupLower.put('M', Unicode.mu+"");
		LookupUpper.put('M', Unicode.Mu+"");
		LookupLower.put('O', Unicode.degree);
		LookupUpper.put('O', Unicode.degree);
		LookupLower.put('P', Unicode.pi+"");
		LookupUpper.put('P', Unicode.Pi+"");
		LookupLower.put('R', Unicode.SQUARE_ROOT);
		LookupUpper.put('R', Unicode.SQUARE_ROOT);
		LookupLower.put('S', Unicode.sigma+"");
		LookupUpper.put('S', Unicode.Sigma+"");
		LookupLower.put('T', Unicode.theta+"");
		LookupUpper.put('T', Unicode.Theta+"");
		LookupLower.put('U', Unicode.Infinity+"");
		LookupUpper.put('U', Unicode.Infinity+"");
		LookupLower.put('W', Unicode.omega+"");
		LookupUpper.put('W', Unicode.Omega+"");
		
		LookupLower.put('0', Unicode.Superscript_0+"");
		LookupLower.put('1', Unicode.Superscript_1+"");
		LookupLower.put('2', Unicode.Superscript_2+"");
		LookupLower.put('3', Unicode.Superscript_3+"");
		LookupLower.put('4', Unicode.Superscript_4+"");
		LookupLower.put('5', Unicode.Superscript_5+"");
		LookupLower.put('6', Unicode.Superscript_6+"");
		LookupLower.put('7', Unicode.Superscript_7+"");
		LookupLower.put('8', Unicode.Superscript_8+"");
		LookupUpper.put('8', ExpressionNodeConstants.strVECTORPRODUCT);
		LookupLower.put('9', Unicode.Superscript_9+"");
		
		
		
		LookupUpper.put('*', ExpressionNodeConstants.strVECTORPRODUCT);
		LookupLower.put('*', ExpressionNodeConstants.strVECTORPRODUCT);
		
		LookupUpper.put('+', Unicode.PLUSMINUS);
		LookupLower.put('+', Unicode.PLUSMINUS);
		
		LookupUpper.put(Unicode.eGrave, "{"); // Italian keyboards
		LookupLower.put(Unicode.eGrave, "["); // Italian keyboards
		
		LookupUpper.put(Unicode.eAcute, "{"); // Italian keyboards
		LookupLower.put(Unicode.eAcute, "["); // Italian keyboards
		
		LookupUpper.put('=', Unicode.NOTEQUAL);
		LookupLower.put('=', Unicode.NOTEQUAL);
		
		LookupUpper.put('-', Unicode.Superscript_Minus+"");
		LookupLower.put('-', Unicode.Superscript_Minus+"");
		
		LookupUpper.put(',', Unicode.LESS_EQUAL+"");
		LookupLower.put(',', Unicode.LESS_EQUAL+"");
		
		LookupUpper.put('<', Unicode.LESS_EQUAL+"");
		LookupLower.put('<', Unicode.LESS_EQUAL+"");
		
		LookupUpper.put('.', Unicode.GREATER_EQUAL+"");
		LookupLower.put('.', Unicode.GREATER_EQUAL+"");
		
		LookupUpper.put('>', Unicode.GREATER_EQUAL+"");
		LookupLower.put('>', Unicode.GREATER_EQUAL+"");
		
		if (App.isFullAppGui()) {
			
			// these keycodes also work in Safari 5.1.2 (Win 7), Firefox 12 (Win 7) and Chrome 20 on Chromebook
				
			// on Chrome 18 (Win 7), Alt-Keypad* gives character 106  
			LookupUpper.put((char)106, ExpressionNodeConstants.strVECTORPRODUCT);
			LookupLower.put((char)106, ExpressionNodeConstants.strVECTORPRODUCT);
			
			// on Chrome, Alt-Keypad+ gives character 107
			LookupUpper.put((char)107, Unicode.PLUSMINUS);
			LookupLower.put((char)107, Unicode.PLUSMINUS);
			
			// on Chrome, Alt-Keypad- gives character 109
			LookupUpper.put((char)109, Unicode.Superscript_Minus+"");
			LookupLower.put((char)109, Unicode.Superscript_Minus+"");
			
			// on Chrome, Alt-= gives character 187 (>>) 
			LookupUpper.put((char)187, Unicode.NOTEQUAL+"");
			LookupLower.put((char)187, Unicode.NOTEQUAL+"");
	
			// on Chrome, Alt-, gives character 188 (1/4) 
			LookupUpper.put((char)188, Unicode.LESS_EQUAL+"");
			LookupLower.put((char)188, Unicode.LESS_EQUAL+"");
			
			// on Chrome, Alt-- gives character 189 (1/2) 
			LookupUpper.put((char)189, Unicode.Superscript_Minus+"");
			LookupLower.put((char)189, Unicode.Superscript_Minus+"");
			
			// on Chrome, Alt-. gives character 190 (3/4) 
			LookupUpper.put((char)190, Unicode.GREATER_EQUAL+"");
			LookupLower.put((char)190, Unicode.GREATER_EQUAL+"");
		}
		
		
		
	}

}
