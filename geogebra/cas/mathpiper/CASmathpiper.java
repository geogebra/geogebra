package geogebra.cas.mathpiper;

import geogebra.cas.CASgeneric;
import geogebra.cas.CASparser;
import geogebra.cas.CasParserTools;
import geogebra.cas.GeoGebraCAS;
import geogebra.cas.error.CASException;
import geogebra.kernel.arithmetic.ExpressionNode;
import geogebra.kernel.arithmetic.ValidExpression;
import geogebra.main.Application;

import java.util.Timer;
import java.util.TimerTask;

import org.mathpiper.interpreters.EvaluationResponse;
import org.mathpiper.interpreters.Interpreter;

public class CASmathpiper extends CASgeneric {
	
	private final static String RB_GGB_TO_MathPiper = "/geogebra/cas/mathpiper/ggb2mathpiper";
	private final CasParserTools parserTools;
	
	private Interpreter ggbMathPiper;
	private EvaluationResponse response;
	
	public CASmathpiper(CASparser casParser, CasParserTools parserTools) {
		super(casParser, RB_GGB_TO_MathPiper);
		this.parserTools = parserTools;
		
		getMathPiper();
	}
	
	private synchronized Interpreter getMathPiper() {				
		if (ggbMathPiper == null) {
			ggbMathPiper = org.mathpiper.interpreters.Interpreters.newSynchronousInterpreter();
			initMyMathPiperFunctions();
			evaluateMathPiper("Factor(42)"); // this solves ticket #320
			Application.setCASVersionString("MathPiper "+org.mathpiper.Version.version); 
		}
		
		return ggbMathPiper;
	}	
	
	/**
	 * Resets the cas and unbinds all variable and function definitions.
	 */
	public void reset() {
		ggbMathPiper = null;
		getMathPiper();
	}
	
//	/**
//	 * Returns whether var is a defined variable in MathPiper.
//	 */
//	public boolean isVariableBound(String var) {
//		StringBuilder exp = new StringBuilder("IsBound(");
//		exp.append(var);
//		exp.append(')');
//		return "True".equals(evaluateMathPiper(exp.toString()));
//	}
	
	/**
	 * Unbinds (deletes) var in MathPiper.
	 * @param var
	 */
	public void unbindVariable(String var) {
		StringBuilder sb = new StringBuilder();
		
		// clear function variable, e.g. Retract("f", *)
		sb.append("[Retract(\"");
		sb.append(var);
		sb.append("\", *);");	

		// clear variable, e.g. Unbind(f)
		sb.append("Unbind(");
		sb.append(var);
		sb.append(");]");
		
		evaluateMathPiper(sb.toString());
	}
	
	/**
	 * Evaluates a valid expression in GeoGebraCAS syntax and returns the resulting String in GeoGebra notation.
	 * @param casInput in GeoGebraCAS syntax
	 * @return evaluation result
	 * @throws CASException
	 */
	public synchronized String evaluateGeoGebraCAS(ValidExpression casInput) throws CASException {
		// convert parsed input to MathPiper string
		String MathPiperString = translateToCAS(casInput, ExpressionNode.STRING_TYPE_MATH_PIPER);
			
		// EVALUATE input in MathPiper 
		String result = evaluateMathPiper(MathPiperString);

		// convert MathPiper result back into GeoGebra syntax
		String ggbString;
		ggbString = toGeoGebraString(result);
		
		// TODO: remove
		System.out.println("eval with MathPiper: " + MathPiperString);
		System.out.println("   result: " + result);
		System.out.println("   ggbString: " + ggbString);
		
		return ggbString;
	}
	
	/** 
	 * Evaluates an expression in the syntax of MathPiper.
	 * 
     * @return result string (null possible)
	 * @throws Throwable 
     */
	public String evaluateRaw(String exp) throws Throwable {
		return evaluateMathPiper(exp);
	}
	
	/**
	 * Tries to parse a given MathPiper string and returns a String in GeoGebra syntax.
	 * @param MathPiperString String in MP syntax.
	 * @return String in ggb syntax.
	 * @throws CASException 
	 */
	public synchronized String toGeoGebraString(String MathPiperString) throws CASException {
		ValidExpression ve = casParser.parseMathPiper(MathPiperString);
		return casParser.toGeoGebraString(ve);
	}
	
    /**
	 * Evaluates a MathPiper expression and returns the result as a string in MathPiper syntax, 
	 * e.g. evaluateMathPiper("D(x) (x^2)") returns "2*x".
     * @param exp The expression.
	 * 
	 * @return result string (null possible)
	 */
	final synchronized public String evaluateMathPiper(String exp) {
		try {
			String result;

			// MathPiper has problems with indices like a_3, b_{12}
			exp = casParser.replaceIndices(exp);
//			
//			final boolean debug = true;
//			if (debug) Application.debug("Expression for mathPiper: "+exp);
			
			// evaluate the MathPiper expression
			final Interpreter mathpiper = getMathPiper();
			
			
			 EvaluationResponse response;


			 // timeout needed for eg Limit((Sin(1/x)*x^2-x*Cos(1/x))/x^2,-Infinity)
		       final Timer timer = new Timer();

		       timer.schedule(new TimerTask() {
		           public void run() {
		        	   mathpiper.haltEvaluation();
		               timer.cancel();
		           }

		       }, getTimeoutMilliseconds());
		       
		       response = mathpiper.evaluate(exp);
		       timer.cancel();
		    	          
			
			if (response.isExceptionThrown())
			{
				System.err.println("evaluateMathPiper: "+exp+"\n  Exception: "+response.getExceptionMessage());
				return "?";
			}
			result = response.getResult();
	
			//if (debug) System.out.println("Result: "+result);
					
			// undo special character handling
			result = casParser.insertSpecialChars(result);
			
			// convert MathPiper's scientific notation from e.g. 3.24e-4 to 3.2E-4
			result = parserTools.convertScientificFloatNotation(result);

			return result;
		} catch (Throwable th) {
			//MathPiper.Evaluate("restart;");
			th.printStackTrace();
			return null;
		} 
	}
	
	final synchronized public String getEvaluateGeoGebraCASerror() {
		if (response != null)
			return response.getExceptionMessage();
		else 
			return null;
	}
	
	public String translateFunctionDeclaration(String label, String parameters, String body)
	{
		return label + '(' + parameters + ") := " + body;
	}

	
	/**
	 * Initialize special commands needed in our ggbMathPiper instance,e.g.
	 * getPolynomialCoeffs(exp,x).
	 */
	private synchronized boolean initMyMathPiperFunctions() {		
// Expand expression and get polynomial coefficients using MathPiper:
//		getPolynomialCoeffs(expr,x) :=
//			       If( CanBeUni(expr),
//			           [
//							Coef(MakeUni(expr,x),x, 0 .. Degree(expr,x));			           ],
//			           {};
//			      );
		String strGetPolynomialCoeffs = "getPolynomialCoeffs(expr,x) :=If(CanBeUni(x,expr),[ Coef(MakeUni(expr,x),x, 0 .. Degree(expr,x));],{});";
		EvaluationResponse resp = ggbMathPiper.evaluate(strGetPolynomialCoeffs);
		if (resp.isExceptionThrown()) {
			return false;
		}
		
		// define constant for Degree
		response = ggbMathPiper.evaluate("Degree := Pi/180;");
		
		// set default numeric precision to 16 significant figures
		ggbMathPiper.evaluate("BuiltinPrecisionSet(16);");
		
		// user defined function
		ggbMathPiper.evaluate("log10(x) := Ln(x) / Ln(10);");
		ggbMathPiper.evaluate("log2(x) := Ln(x) / Ln(2);");
		ggbMathPiper.evaluate("logB(b, x) := Ln(x) / Ln(b);");
		ggbMathPiper.evaluate("cbrt(x) := x^(1/3);");
		ggbMathPiper.evaluate("RandomNormal(mu,sigma) := N(Cos(2*Pi*Random())*Sqrt(-2*LogN(Random()))*Sqrt(sigma)+mu);");
		
		// Rules for equation manipulation
		// allow certain commands for equations
		ggbMathPiper.evaluate("NotEqu(exp) := Not( IsEquation(exp));");
		
		//ggbMathPiper.evaluate("KeepInput( (x_NotEqu == y_NotEqu) + z_NotEqu ) <-- Subst(a, x) Subst(b, y) Subst(c, z) (Hold(a + c) ==  Hold(b + c)) ;");
		
		
		// standard commands for equations
		ggbMathPiper.evaluate("Simplify(x_NotEqu == y_NotEqu)  <-- Simplify(x) == Simplify(y);");
		ggbMathPiper.evaluate("Factor(x_NotEqu == y_NotEqu)  <-- Factor(x) == Factor(y);");
		ggbMathPiper.evaluate("Expand(x_NotEqu == y_NotEqu)  <-- Expand(x) == Expand(y);");
		ggbMathPiper.evaluate("ExpandBrackets(x_NotEqu == y_NotEqu)  <-- ExpandBrackets(x) == ExpandBrackets(y);");
		ggbMathPiper.evaluate("Sqrt(x_NotEqu == y_NotEqu)  <-- Sqrt(x) == Sqrt(y);");
		ggbMathPiper.evaluate("Exp(x_NotEqu == y_NotEqu)  <-- Exp(x) == Exp(y);");
		ggbMathPiper.evaluate("Ln(x_NotEqu == y_NotEqu)  <-- Ln(x) == Ln(y);");
		
		// arithmetic for equations and scalars
		ggbMathPiper.evaluate("(x_NotEqu == y_NotEqu) + z_NotEqu <-- x + z == y + z;");
		ggbMathPiper.evaluate("z_NotEqu + (x_NotEqu == y_NotEqu) <-- z + x == z + y;");
		ggbMathPiper.evaluate("(x_NotEqu == y_NotEqu) - z_NotEqu <-- x - z == y - z;");
		ggbMathPiper.evaluate("z_NotEqu - (x_NotEqu == y_NotEqu) <-- z - x == z - y;");
		ggbMathPiper.evaluate("(x_NotEqu == y_NotEqu) * z_NotEqu <-- x * z == y * z;");
		ggbMathPiper.evaluate("z_NotEqu * (x_NotEqu == y_NotEqu) <-- z * x == z * y;");
		ggbMathPiper.evaluate("(x_NotEqu == y_NotEqu) / z_NotEqu <-- x / z == y / z;");
		ggbMathPiper.evaluate("z_NotEqu / (x_NotEqu == y_NotEqu) <-- z / x == z / y;");
		ggbMathPiper.evaluate("(x_NotEqu == y_NotEqu) ^ z_NotEqu <-- x ^ z == y ^ z;");
		ggbMathPiper.evaluate("z_NotEqu ^ (x_NotEqu == y_NotEqu) <-- z ^ x == z ^ y;");
		
		// arithmetic for two equations
		ggbMathPiper.evaluate("(a_NotEqu == b_NotEqu) + (c_NotEqu == d_NotEqu) <-- a + c == b + d;");
		ggbMathPiper.evaluate("(a_NotEqu == b_NotEqu) - (c_NotEqu == d_NotEqu) <-- a - c == b - d;");
		ggbMathPiper.evaluate("(a_NotEqu == b_NotEqu) * (c_NotEqu == d_NotEqu) <-- a * c == b * d;");
		ggbMathPiper.evaluate("(a_NotEqu == b_NotEqu) / (c_NotEqu == d_NotEqu) <-- a / c == b / d;");
		
		
		//ggbMathPiper.evaluate("KeepInput(x_IsAtom)  <-- Simplify(x) == Simplify(y);");
		
		// access functions for elements of a vector  (for the "if" part, see #556)
		ggbMathPiper.evaluate("x(a) := If(IsList(a), Nth(a, 1), x*a);");
		ggbMathPiper.evaluate("y(a) := If(IsList(a), Nth(a, 2), y*a);");
		ggbMathPiper.evaluate("z(a) := If(IsList(a), Nth(a, 3), z*a);");
		
		return true;
	}

	@Override
	public void setSignificantFiguresForNumeric(int significantNumbers) {
		// TODO Auto-generated method stub
		
	}
}
