package geogebra.kernel.arithmetic;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public interface ExpressionNodeConstants {

	

	public static final int STRING_TYPE_GEOGEBRA_XML = 0;
	public static final int STRING_TYPE_GEOGEBRA = 1;
	public static final int STRING_TYPE_MAXIMA = 2;
	public static final int STRING_TYPE_MATH_PIPER = 3;
	public static final int STRING_TYPE_LATEX = 4;
	public static final int STRING_TYPE_PSTRICKS = 5;
	public static final int STRING_TYPE_PGF = 6;
	public static final int STRING_TYPE_JASYMCA = 7;
	public static final int STRING_TYPE_MPREDUCE = 8;
	
	public static final String CAS_ROW_REFERENCE_PREFIX = "$";
	public static final String UNICODE_PREFIX = "unicode";
	public static final String UNICODE_DELIMITER = "u";  
	
	public static final int NO_OPERATION = Integer.MIN_VALUE; 
    
	// boolean
	public static final int NOT_EQUAL = -15;
	public static final int NOT = -14;
	public static final int OR = -13;
    public static final int AND = -12;
    public static final int EQUAL_BOOLEAN = -11;
    public static final int LESS = -10;
    public static final int GREATER = -9;
    public static final int LESS_EQUAL = -8;
    public static final int GREATER_EQUAL = -7;    
    public static final int PARALLEL = -6;  
    public static final int PERPENDICULAR = -5;
    public static final int IS_ELEMENT_OF = -4;
    public static final int IS_SUBSET_OF = -3;
    public static final int IS_SUBSET_OF_STRICT = -2;
    public static final int SET_DIFFERENCE = -1;
    
    public static final String strNOT = "\u00ac";
    public static final String strAND = "\u2227";
    public static final String strOR = "\u2228";
    public static final String strLESS_EQUAL = "\u2264";
    public static final String strGREATER_EQUAL = "\u2265";
    public static final String strEQUAL_BOOLEAN = "\u225f";
    public static final String strNOT_EQUAL = "\u2260";
    public static final String strPARALLEL = "\u2225";
    public static final String strPERPENDICULAR = "\u22a5";
    public static final String strVECTORPRODUCT = "\u2297";
    public static final String strIS_ELEMENT_OF = "\u2208";
    public static final String strIS_SUBSET_OF = "\u2286";
    public static final String strIS_SUBSET_OF_STRICT = "\u2282";
    public static final String strSET_DIFFERENCE = "\\";
        
    // arithmetic
    public static final int PLUS = 0;
    public static final int MINUS = 1;
    public static final int VECTORPRODUCT = 2;
    
    // these next three must be adjacent
    // so that brackets work for eg a/(b/c)
    // and are removed in (a/b)/c
    // see case DIVIDE in ExpressionNode
    public static final int MULTIPLY = 10;
    public static final int DIVIDE = 11;
    public static final int POWER = 12;            
    
    
    public static final int COS = 50;   
    public static final int SIN = 60;   
    public static final int TAN = 70;   
    public static final int EXP = 80;   
    public static final int LOG = 90;   
    public static final int ARCCOS = 100;   
    public static final int ARCSIN = 110;   
    public static final int ARCTAN = 120;   
    public static final int ARCTAN2 = 130;   
    public static final int SQRT = 140;   
    public static final int ABS = 150;   
    public static final int SGN = 160;   
    public static final int XCOORD = 170; 
    public static final int YCOORD = 180;  
    public static final int ZCOORD = 190;  
    public static final int COSH = 200;
    public static final int SINH = 210;
    public static final int TANH = 220;
    public static final int ACOSH = 230;
    public static final int ASINH = 240;
    public static final int ATANH = 250;
    public static final int CSC = 260;
    public static final int SEC = 270;
    public static final int COT = 280;
    public static final int CSCH = 290;
    public static final int SECH = 300;
    public static final int COTH = 310;
    public static final int FLOOR = 320;
    public static final int CEIL = 330;  
    public static final int FACTORIAL = 340;
    public static final int ROUND = 350;  
    public static final int GAMMA = 360;    
    public static final int GAMMA_INCOMPLETE = 370;    
    public static final int GAMMA_INCOMPLETE_REGULARIZED = 380;    
    public static final int BETA = 390;    
    public static final int BETA_INCOMPLETE = 400;    
    public static final int BETA_INCOMPLETE_REGULARIZED = 410;    
    public static final int ERF = 420;
    public static final int LOG10 = 430;  
    public static final int LOG2 = 440; 
    public static final int CBRT = 450;   
    public static final int RANDOM = 460;
    public static final int CONJUGATE = 480;
    public static final int ARG = 490; 
     
    public static final int FUNCTION = 500;
    public static final int FUNCTION_NVAR = 510;
    public static final int VEC_FUNCTION = 520;
    public static final int DERIVATIVE = 530;  
    public static final int ELEMENT_OF = 540;  
    
    // spreadsheet absolute reference using $ signs
    public static final int $VAR_ROW = 550;
    public static final int $VAR_COL = 560;
    public static final int $VAR_ROW_COL = 570;
	
    // logarithm for arbitrary base log(b, x)
    public static final int LOGB = 580;
    
    /*
     * these should also be documented here:
     * http://wiki.geogebra.org/en/Manual:Naming_Objects
     */
    public static final Set<String> RESERVED_FUNCTION_NAMES = new HashSet<String>(Arrays.asList(
    		"x", "y", "abs",
		"sgn", "sqrt", "exp", "log", "ln", "ld", "lg", "cos", "sin", "tan",
		"acos", "arcos", "arccos", "asin", "arcsin", "atan", "arctan", 
		"cosh", "sinh", "tanh", "acosh", "arcosh", "arccosh", "asinh",
		"arcsinh", "atanh", "arctanh", "atan2", "erf",
		"floor", "ceil", "round", "random", "conjugate", "arg",
		"gamma", "gammaRegularized", "beta", "betaRegularized", 
		"sec", "csc", "cosec", "cot", "sech", "csch", "coth"));
}
