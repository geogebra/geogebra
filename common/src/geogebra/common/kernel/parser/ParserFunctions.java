package geogebra.common.kernel.parser;

import geogebra.common.plugin.Operation;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.TreeMap;

public class ParserFunctions {
private static final List<Map<String,Operation>> stringToOp = new ArrayList<Map<String,Operation>>();

static {
	for(int i=0;i<4;i++)
		stringToOp.add(new TreeMap<String,Operation>());
	stringToOp.get(1).put("sin", Operation.SIN);
	stringToOp.get(1).put("Sin", Operation.SIN);

	stringToOp.get(1).put("cos", Operation.COS);
	stringToOp.get(1).put("Cos", Operation.COS);
	
	stringToOp.get(1).put("tan", Operation.TAN);
	stringToOp.get(1).put("Tan", Operation.TAN);
	
	stringToOp.get(1).put("csc", Operation.CSC);
	stringToOp.get(1).put("Csc", Operation.CSC);
	stringToOp.get(1).put("cosec", Operation.CSC);
	stringToOp.get(1).put("Cosec", Operation.CSC);
	
	stringToOp.get(1).put("sec", Operation.SEC);
	stringToOp.get(1).put("Sec", Operation.SEC);
	
	stringToOp.get(1).put("cot", Operation.COT);
	stringToOp.get(1).put("Cot", Operation.COT);
	
	stringToOp.get(1).put("csch", Operation.CSCH);
	stringToOp.get(1).put("Csch", Operation.CSCH);
	
	stringToOp.get(1).put("sech", Operation.SECH);
	stringToOp.get(1).put("Sech", Operation.SECH);
	
	stringToOp.get(1).put("coth", Operation.COTH);
	stringToOp.get(1).put("Coth", Operation.COTH);
	
	stringToOp.get(1).put("acos", Operation.ARCCOS);
	stringToOp.get(1).put("arccos", Operation.ARCCOS);
	stringToOp.get(1).put("arcos", Operation.ARCCOS);
	stringToOp.get(1).put("ArcCos", Operation.ARCCOS);
	
	stringToOp.get(1).put("asin", Operation.ARCSIN);
	stringToOp.get(1).put("arcsin", Operation.ARCSIN);
	stringToOp.get(1).put("ArcSin", Operation.ARCSIN);
	
	stringToOp.get(1).put("atan", Operation.ARCTAN);
	stringToOp.get(1).put("arctan", Operation.ARCTAN);
	stringToOp.get(1).put("ArcTan", Operation.ARCTAN);
	
	stringToOp.get(1).put("atan2", Operation.ARCTAN2);
	stringToOp.get(1).put("arctan2", Operation.ARCTAN2);
	stringToOp.get(1).put("ArcTan2", Operation.ARCTAN2);
	
	stringToOp.get(1).put("erf", Operation.ERF);
	stringToOp.get(1).put("Erf", Operation.ERF);
	
	stringToOp.get(1).put("psi", Operation.PSI);
	
	stringToOp.get(1).put("polygamma", Operation.POLYGAMMA);
	
	stringToOp.get(1).put("cosh", Operation.COSH);
	stringToOp.get(1).put("Cosh", Operation.COSH);
	
	stringToOp.get(1).put("sinh", Operation.SINH);
	stringToOp.get(1).put("Sinh", Operation.SINH);
	
	stringToOp.get(1).put("tanh", Operation.TANH);
	stringToOp.get(1).put("Tanh", Operation.TANH);
	
	stringToOp.get(1).put("acosh", Operation.ACOSH);
	stringToOp.get(1).put("Acosh", Operation.ACOSH);
	
	stringToOp.get(1).put("asinh", Operation.ASINH);
	stringToOp.get(1).put("Asinh", Operation.ASINH);
	
	stringToOp.get(1).put("atanh", Operation.ATANH);
	stringToOp.get(1).put("Atanh", Operation.ATANH);
	
	stringToOp.get(1).put("exp", Operation.EXP);
	stringToOp.get(1).put("Exp", Operation.EXP);
	
	stringToOp.get(1).put("log", Operation.LOG);
	stringToOp.get(1).put("ln", Operation.LOG);
	stringToOp.get(1).put("Ln", Operation.LOG);
	
	stringToOp.get(2).put("log", Operation.LOGB);
	stringToOp.get(2).put("ln", Operation.LOGB);
	stringToOp.get(2).put("Ln", Operation.LOGB);
	
	stringToOp.get(1).put("ld", Operation.LOG2);
	stringToOp.get(1).put("log2", Operation.LOG2);
	
	stringToOp.get(1).put("lg", Operation.LOG10);
	stringToOp.get(1).put("log10", Operation.LOG10);
	
	stringToOp.get(2).put("beta", Operation.BETA);
	stringToOp.get(2).put("Beta", Operation.BETA);
	
	stringToOp.get(3).put("beta", Operation.BETA_INCOMPLETE);
	stringToOp.get(3).put("Beta", Operation.BETA_INCOMPLETE);
	
	stringToOp.get(3).put("betaRegularized", Operation.BETA_INCOMPLETE_REGULARIZED);
	stringToOp.get(3).put("ibeta", Operation.BETA_INCOMPLETE_REGULARIZED);
	
	stringToOp.get(1).put("gamma", Operation.GAMMA);
	stringToOp.get(1).put("igamma", Operation.GAMMA);
	stringToOp.get(1).put("Gamma", Operation.GAMMA);
	
	stringToOp.get(2).put("gamma", Operation.GAMMA_INCOMPLETE);
	stringToOp.get(2).put("igamma", Operation.GAMMA_INCOMPLETE);
	stringToOp.get(2).put("Gamma", Operation.GAMMA_INCOMPLETE);
	
	stringToOp.get(1).put("gammaRegularized", Operation.GAMMA_INCOMPLETE_REGULARIZED);
	
	stringToOp.get(1).put("cosIntegral", Operation.CI);
	stringToOp.get(1).put("CosIntegral", Operation.CI);
	
	stringToOp.get(1).put("sinIntegral", Operation.SI);
	stringToOp.get(1).put("SinIntegral", Operation.SI);
	
	stringToOp.get(1).put("expIntegral", Operation.EI);
	stringToOp.get(1).put("ExpIntegral", Operation.EI);
	
	stringToOp.get(1).put("gGbInTeGrAl", Operation.INTEGRAL);
	stringToOp.get(1).put("gGbSuBsTiTuTiOn", Operation.SUBSTITUTION);
	
	stringToOp.get(1).put("arbint", Operation.ARBINT);
	
	stringToOp.get(1).put("arbconst", Operation.ARBCONST);
	
	stringToOp.get(1).put("arbcomplex", Operation.ARBCOMPLEX);
	
	stringToOp.get(1).put("sqrt", Operation.SQRT);
	stringToOp.get(1).put("Sqrt", Operation.SQRT);
	
	stringToOp.get(1).put("cbrt", Operation.CBRT);
	stringToOp.get(1).put("Cbrt", Operation.CBRT);
	
	stringToOp.get(1).put("abs", Operation.ABS);
	stringToOp.get(1).put("Abs", Operation.ABS);
	
	stringToOp.get(1).put("sgn", Operation.SGN);
	stringToOp.get(1).put("sign", Operation.SGN);
	stringToOp.get(1).put("Sign", Operation.SGN);
	
	stringToOp.get(1).put("floor", Operation.FLOOR);
	stringToOp.get(1).put("Floor", Operation.FLOOR);
	
	stringToOp.get(1).put("ceil", Operation.CEIL);
	stringToOp.get(1).put("Ceil", Operation.CEIL);
	
	stringToOp.get(1).put("conjugate", Operation.CONJUGATE);
	stringToOp.get(1).put("Conjugate", Operation.CONJUGATE);
	
	stringToOp.get(1).put("arg", Operation.ARG);
	stringToOp.get(1).put("Arg", Operation.ARG);
	
}
public static Operation get(String s,int size){
	return stringToOp.get(size).get(s);
}
}

