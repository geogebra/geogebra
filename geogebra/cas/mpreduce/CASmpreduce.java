package geogebra.cas.mpreduce;

import geogebra.cas.CASgeneric;
import geogebra.cas.CASparser;
import geogebra.cas.CasParserTools;
import geogebra.cas.GeoGebraCAS;
import geogebra.cas.error.CASException;
import geogebra.kernel.arithmetic.Command;
import geogebra.kernel.arithmetic.ExpressionNode;
import geogebra.kernel.arithmetic.ExpressionNodeConstants;
import geogebra.kernel.arithmetic.FunctionNVar;
import geogebra.kernel.arithmetic.ValidExpression;
import geogebra.main.Application;

import java.util.Set;
import java.util.StringTokenizer;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.mathpiper.mpreduce.Interpreter2;

public class CASmpreduce extends CASgeneric {
	
	private final static String RB_GGB_TO_MPReduce = "/geogebra/cas/mpreduce/ggb2mpreduce";
	private int significantNumbers=-1;
	
	// using static CAS instance as a workaround for the MPReduce deadlock with multiple application windows
	// see http://www.geogebra.org/trac/ticket/1415 
	private static Interpreter2 mpreduce_static;
	private Interpreter2 mpreduce;
	private static StringBuilder varOrder=new StringBuilder();
	private final CasParserTools parserTools;

	
	// We escape any upper-letter words so Reduce doesn't switch them to lower-letter,
	// however the following function-names should not be escaped
	// (note: all functions here must be in lowercase!)
	final private Set<String> predefinedFunctions = ExpressionNodeConstants.RESERVED_FUNCTION_NAMES;

	public CASmpreduce(CASparser casParser, CasParserTools parserTools) {
		super(casParser, RB_GGB_TO_MPReduce);
		this.parserTools = parserTools;
		getMPReduce();
	}

	/**
	 * @return Static MPReduce interpreter shared by all
	 * CASmpreduce instances.
	 */
	private static synchronized Interpreter2 getStaticInterpreter() {
		if (mpreduce_static == null) {
			mpreduce_static = new Interpreter2();

			// the first command sent to mpreduce produces an error
			try {
				loadMyMPReduceFunctions(mpreduce_static);
			} catch (Throwable e)
			{}
			
			Application.setCASVersionString(getVersionString(mpreduce_static));
		}
		
		return mpreduce_static;
	}
	
	/**
	 * @return MPReduce interpreter using static interpreter with local kernel initialization.
	 */
	private synchronized Interpreter2 getMPReduce() {
		if (mpreduce == null) {
			// create mpreduce as a private reference to mpreduce_static
			mpreduce = getStaticInterpreter();
			
			try {
				// make sure to call initMyMPReduceFunctions() for each CASmpreduce instance
				// because it depends on the current kernel's ggbcasvar prefix, see #1443
				initMyMPReduceFunctions(mpreduce);
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
		
		return mpreduce;
	}

	/**
	 * Evaluates a valid expression in GeoGebraCAS syntax and returns the resulting String in GeoGebra notation.
	 * @param casInput in GeoGebraCAS syntax
	 * @return evaluation result
	 * @throws CASException
	 */
	public synchronized String evaluateGeoGebraCAS(ValidExpression casInput) throws CASException {
		
		// KeepInput[] command should set flag keepinput!!:=1
		// so that commands like Substitute can work accordingly
		boolean keepInput = casInput.isKeepInputUsed();
		if (keepInput) {
			// remove KeepInput[] command and take argument	
			Command cmd = casInput.getTopLevelCommand();
			if (cmd != null && cmd.getName().equals("KeepInput")) {
				// use argument of KeepInput as casInput
				if (cmd.getArgumentNumber() > 0)
					casInput = cmd.getArgument(0);
			}
		}
		
		// convert parsed input to MPReduce string
		String mpreduceInput = translateToCAS(casInput, ExpressionNode.STRING_TYPE_MPREDUCE);
		
		// tell MPReduce whether it should use the keep input flag, 
		// e.g. important for Substitute
		StringBuilder sb = new StringBuilder();
		sb.append("<<keepinput!!:=");
		sb.append(keepInput ? 1 : 0);
		sb.append("$ numeric!!:=0$ precision 30$ print\\_precision 16$ off complex, rounded, numval, factor, div, combinelogs, expandlogs, pri$ currentx!!:= ");
		sb.append(casParser.getKernel().getCasVariablePrefix());
		sb.append("x; currenty!!:= ");
		sb.append(casParser.getKernel().getCasVariablePrefix());
		sb.append("y;");
		sb.append(mpreduceInput);
		sb.append(">>");
		
		// evaluate in MPReduce
		String result = evaluateMPReduce(sb.toString());		
		
		if (keepInput) {
			// when keepinput was treated in MPReduce, it is now > 1
			String keepinputVal = evaluateMPReduce("keepinput!!;");
			boolean keepInputUsed = !"1".equals(keepinputVal);
			if (!keepInputUsed)
				result =  casParser.toGeoGebraString(casInput);
		}				

		// convert result back into GeoGebra syntax
		if (casInput instanceof FunctionNVar) {
			// function definition f(x) := x^2 should return x^2
			int oldPrintForm = casParser.getKernel().getCASPrintForm();
			casParser.getKernel().setCASPrintForm(ExpressionNode.STRING_TYPE_GEOGEBRA);
			String ret = casInput.toString();
			casParser.getKernel().setCASPrintForm(oldPrintForm);
			return ret;
		}
		else
		{	
			// standard case
			return toGeoGebraString(result);
		}
	}

	/**
	 * Tries to parse a given MPReduce string and returns a String in GeoGebra syntax.
	 * @param mpreduceString String in MPReduce syntax
	 * @return String in Geogebra syntax.
	 * @throws CASException Throws if the underlying CAS produces an error
	 */
	public synchronized String toGeoGebraString(String mpreduceString) throws CASException {
		ValidExpression ve = casParser.parseMPReduce(mpreduceString);
		return casParser.toGeoGebraString(ve);
	}

	/**
	 * Evaluates an expression and returns the result as a string in MPReduce
	 * syntax, e.g. evaluateMathPiper("D(x) (x^2)") returns "2*x".
	 * 
     * @param exp expression (with command names already translated to MPReduce syntax).
	 * @return result string (null possible)
	 * @throws CASException 
	 */
	public final String evaluateMPReduce(String exp) throws CASException {
		try {
			exp = casParser.replaceIndices(exp);
			String ret = evaluateRaw(exp);
			ret = casParser.insertSpecialChars(ret); // undo special character handling
			
			// convert MPReduce's scientific notation from e.g. 3.24e-4 to 3.2E-4
			ret = parserTools.convertScientificFloatNotation(ret);
			
			return ret;
		} catch (TimeoutException toe) {
			throw new geogebra.cas.error.TimeoutException(toe.getMessage());
		} catch (Throwable e) {
			System.err.println("evaluateMPReduce: " + e.getMessage());
			return "?";
		}
	}
	
	
	public String translateFunctionDeclaration(String label, String parameters, String body)
	{
		StringBuilder sb = new StringBuilder();
		sb.append(" procedure ");
		sb.append(label);
		sb.append("(");
		sb.append(parameters);
		sb.append("); begin return ");
		sb.append(body);
		sb.append(" end ");

		return sb.toString();
	}

	@Override
	public String evaluateRaw(String exp) throws Throwable {
		// we need to escape any upper case letters and non-ascii codepoints with '!'
		StringTokenizer tokenizer = new StringTokenizer(exp, "(),;[] ", true);
		StringBuilder sb = new StringBuilder();
		while (tokenizer.hasMoreElements()) {
			String t = tokenizer.nextToken();
			if (predefinedFunctions.contains(t.toLowerCase()))
				sb.append(t);
			else {
				for (int i = 0; i < t.length(); ++i) {
					char c = t.charAt(i);
					if (Character.isLetter(c) && (((int) c) < 97 || ((int) c) > 122)) {
						sb.append('!');
						sb.append(c);
					} else {
						switch (c) {
							case '\'':
								sb.append('!');
								sb.append(c);
								break;
								
							case '\\':
								if (i<(t.length()+1))
									sb.append(t.charAt(++i));
								break;
						
							default:
								sb.append(c);
								break;
						}					
					}
						
				}
			}
		}
		exp = sb.toString();

		System.out.println("eval with MPReduce: " + exp);
		String result = getMPReduce().evaluate(exp, getTimeoutMilliseconds());

		sb.setLength(0);
		for (String s : result.split("\n")) {
			s = s.trim();
			if (s.length() == 0)
				continue;
			else if (s.startsWith("***")) { // MPReduce comment
				Application.debug("MPReduce comment: " + s);
				continue;
			}
			else if (s.startsWith("Unknown")){ 
				Application.debug("Assumed "+s);
				continue;
			} else {
				// look for any trailing $
				int len = s.length();
				while (len > 0 && s.charAt(len - 1) == '$')
					--len;

				// remove the !
				for (int i = 0; i < len; ++i) {
					char character = s.charAt(i);
					if (character == '!') {
						if (i + 1 < len) {
							character = s.charAt(++i);
						}
					}
					sb.append(character);
				}
			}
		}

		result = sb.toString();

		// TODO: remove
		System.out.println("   result: " + result);
		return result;
	}

	@Override
	public String getEvaluateGeoGebraCASerror() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public synchronized void reset() {
		if (mpreduce == null) return;
		
		try {
			getMPReduce().evaluate("resetreduce;");
			getMPReduce().initialize();
			initMyMPReduceFunctions(getMPReduce());
		} catch (Throwable e) {
			Application.debug("failed to reset MPReduce");
			e.printStackTrace();
		}
	}

	@Override
	public void unbindVariable(String var) {
		try {
			StringBuilder sb = new StringBuilder();
			sb.append("clear(");
			sb.append(var);
			sb.append(");");
			getMPReduce().evaluate(sb.toString());
			
			// TODO: remove
			System.out.println("Cleared variable: " + sb.toString());
		} catch (Throwable e) {
			System.err.println("Failed to clear variable from MPReduce: " + var);
		}
	}

	private static synchronized void loadMyMPReduceFunctions(Interpreter2 mpreduce_static) throws Throwable {
		mpreduce_static.evaluate("load_package rsolve;");
		mpreduce_static.evaluate("load_package numeric;");
		mpreduce_static.evaluate("load_package specfn;");
		mpreduce_static.evaluate("load_package odesolve;");
		mpreduce_static.evaluate("load_package defint;");
		mpreduce_static.evaluate("load_package linalg;");
		mpreduce_static.evaluate("load_package reset;");
		mpreduce_static.evaluate("load_package taylor;");
		mpreduce_static.evaluate("load_package groebner;");		
		mpreduce_static.evaluate("load_package trigsimp;");	
		mpreduce_static.evaluate("load_package polydiv;");	
	}

	private synchronized void initMyMPReduceFunctions(Interpreter2 mpreduce) throws Throwable {
		
		// user variable ordering
		String varOrder = "ggbcasvarx, ggbcasvary, ggbcasvarz, ggbcasvara, " +
				"ggbcasvarb, ggbcasvarc, ggbcasvard, ggbcasvare, ggbcasvarf, " +
				"ggbcasvarg, ggbcasvarh, ggbcasvari, ggbcasvarj, ggbcasvark, " +
				"ggbcasvarl, ggbcasvarm, ggbcasvarn, ggbcasvaro, ggbcasvarp, " +
				"ggbcasvarq, ggbcasvarr, ggbcasvars, ggbcasvart, ggbcasvaru, " +
				"ggbcasvarv, ggbcasvarw";
		// make sure to use current kernel's variable prefix
		varOrder = varOrder.replace("ggbcasvar", casParser.getKernel().getCasVariablePrefix());
		if (CASmpreduce.varOrder.length()>0)
			CASmpreduce.varOrder.append(',');
		CASmpreduce.varOrder.append(varOrder);
		mpreduce.evaluate("varorder!!:= list("+CASmpreduce.varOrder+");");
		mpreduce.evaluate("order varorder!!;");
		mpreduce.evaluate("korder varorder!!;");
		
		// access functions for elements of a vector
		String xyzCoordFunctions = 
			"procedure ggbcasvarx(a); first(a);" +
			"procedure ggbcasvary(a); second(a);" +
			"procedure ggbcasvarz(a); third(a);";
		// make sure to use current kernel's variable prefix
		xyzCoordFunctions = xyzCoordFunctions.replace("ggbcasvar", casParser.getKernel().getCasVariablePrefix());
		mpreduce.evaluate(xyzCoordFunctions);
		
		// Initialize MPReduce
		mpreduce.evaluate("off nat;");
		mpreduce.evaluate("off pri;");

		mpreduce.evaluate("off numval;");
		mpreduce.evaluate("linelength 50000;");
		mpreduce.evaluate("scientific_notation {16,5};");
		mpreduce.evaluate("on fullroots;");
		mpreduce.evaluate("printprecision!!:=5;");
		
		mpreduce.evaluate("intrules!!:={" +
				"int(~w/~x,~x) => w*log(abs(x)) when freeof(w,x)," +
				"int(~w/(~x+~a),~x) => w*log(abs(x+a)) when freeof(w,x) and freeof(a,x)," +
				"int((~b*~x+~w)/(~x+~a),~x) => int((b*xw)/(x+a),x)+w*log(abs(x+a)) when freeof(w,x) and freeof(a,x) and freeof(b,x)," +
				"int((~a*~x+~w)/~x,~x) => int(a,x)+w*log(abs(x)) when freeof(w,x) and freeof(a,x)," +
				"int((~x+~w)/~x,~x) => x+w*log(abs(x)) when freeof(w,x)," +
				"int(tan(~x),~x) => log(abs(sec(x)))," +
				"int(~w*tan(~x),~x) => w*log(abs(sec(x))) when freeof(w,x)," +
				"int(~w+tan(~x),~x) => int(w,x)+log(abs(sec(x)))," +
				"int(~a+~w*tan(~x),~x) => int(a,x)+w*log(abs(sec(x))) when freeof(w,x)," +
				"int(cot(~x),~x) => log(abs(sin(x)))," +
				"int(~w*cot(~x),~x) => w*log(abs(sin(x))) when freeof(w,x)," +
				"int(~a+cot(~x),~x) => int(a,x)+log(abs(sin(x)))," +
				"int(~a+~w*cot(~x),~x) => int(a,x)+w*log(abs(sin(x))) when freeof(w,x)," +
				"int(sec(~x),~x) => -log(abs(tan(x / 2) - 1)) + log(abs(tan(x / 2) + 1))," +
				"int(~w*sec(~x),~x) => -log(abs(tan(x / 2) - 1))*w + log(abs(tan(x / 2) + 1) )*w when freeof(w,x)," +
				"int(~w+sec(~x),~x) => -log(abs(tan(x / 2) - 1)) + log(abs(tan(x / 2) + 1) )+int(w,x)," +
				"int(~a+w*sec(~x),~x) => -log(abs(tan(x / 2) - 1))*w + log(abs(tan(x / 2) + 1) )*w+int(a,x) when freeof(w,x)," +
				"int(csc(~x),~x) => log(abs(tan(x / 2)))," +
				"int(~w*csc(~x),~x) => w*log(abs(tan(x / 2))) when freeof(w,x)," +
				"int(~w+csc(~x),~x) => int(w,x)+log(abs(tan(x / 2)))," +
				"int(~a+~w*csc(~x),~x) => int(a,x)+w*log(abs(tan(x / 2))) when freeof(w,x)" +
				"};"
				);
		
		mpreduce.evaluate("let {impart(arbint(~w)) => 0, arbint(~w)*i =>  0};");
		mpreduce.evaluate("let {atan(sin(~x)/cos(~x))=>x, " +
				"acos(1/sqrt(2)) => pi/4" +
				"};");
		
		mpreduce.evaluate("solverules:={" +
				//"tan(~x) => sin(x)/cos(x)" +
				"};");
		
		mpreduce.evaluate("procedure myatan2(y,x);" +
				" begin scalar xinput, yinput;" +
				" xinput:=x; yinput:=y;" +
				" on rounded, roundall, numval;" +
				" x:=x+0; y:=y+0;" +
				" return " +
				" if numberp(y) and numberp(x) then" +
				"   if x>0 then <<if numeric!!=0 then off rounded, roundall, numval; atan(yinput/xinput)>>" +
				"   else if x<0 and y>=0 then <<if numeric!!=0 then off rounded, roundall, numval; atan(yinput/xinput)+pi>>" +
				"   else if x<0 and y<0 then <<if numeric!!=0 then off rounded, roundall, numval; atan(yinput/xinput)-pi>>" +
				"   else if x=0 and y>0 then <<if numeric!!=0 then off rounded, roundall, numval; pi/2>>" +
				"   else if x=0 and y<0 then <<if numeric!!=0 then off rounded, roundall, numval; -pi/2>>" +
				"   else if x=0 and y=0 then <<if numeric!!=0 then off rounded, roundall, numval; 0>>" +
				"   else '?" +
				" else" +
				"   '? end;");
		
		mpreduce.evaluate(" Degree := pi/180;");

		mpreduce.evaluate("procedure myround(x);" 
				+ "floor(x+0.5);");
		
		mpreduce.evaluate("procedure harmonic(n,m); for i:=1:n sum 1/(i**m);");
		mpreduce.evaluate("procedure uigamma(n,m); gamma(n)-igamma(n,m);");
		mpreduce.evaluate("procedure beta!Regularized(a,b,x); ibeta(a,b,x);");
		mpreduce.evaluate("procedure myarg(x);" +
				" if arglength(x)>-1 and part(x,0)='list then myatan2(part(x,2), part(x,1)) " +
				" else if arglength(x)>-1 and part(x,0)='mat then <<" +
				"   clear x!!;" +
				"   x!!:=x;" +
				"   if row_dim(x!!)=1 then myatan2(x!!(1,2),x!!(1,1))" +
				"   else if column_dim(x!!)=1 then myatan2(x!!(2,1),x!!(2,1))" +
				"   else arg(x!!) >>" +
				" else myatan2(impart(x),repart(x));");
		mpreduce.evaluate("procedure polartocomplex(r,phi); r*(cos(phi)+i*sin(phi));");
		mpreduce.evaluate("procedure polartopoint!\u00a7(r,phi); list(r*cos(phi),r*sin(phi));");
		mpreduce.evaluate("procedure complexexponential(r,phi); r*(cos(phi)+i*sin(phi));");
		mpreduce.evaluate("procedure conjugate(x); conj(x);");
		mpreduce.evaluate("procedure myrandom(); <<on rounded; random(100000001)/(random(100000000)+1)>>;");
		mpreduce.evaluate("procedure gamma!Regularized(a,x); igamma(a,x);");
		mpreduce.evaluate("procedure gamma2(a,x); gamma(a)*igamma(a,x);");
		mpreduce.evaluate("procedure beta3(a,b,x); beta(a,b)*ibeta(a,b,x);");
		mpreduce.evaluate("symbolic procedure isbound!! x; if get(x, 'avalue) then 1 else 0;");	
		mpreduce.evaluate("procedure myabs(x);" +
				" if arglength(x!!)>-1 and part(x,0)='list then sqrt(for each elem!! in x sum elem!!^2)" +
				" else if arglength(x)>-1 and part(x,0)='mat then <<" +
				"   clear x!!;" +
				"   x!!:=x;" +
				"   if row_dim(x!!)=1 then sqrt(for i:=1:column_dim(x!!) sum x!!(1,i)^2)" +
				"   else if column_dim(x!!)=1 then sqrt(for i:=1:row_dim(x!!) sum x!!(i,1)^2)" +
				"   else abs(x!!) >>" +
				" else if freeof(x,i) then abs(x)" +
				" else sqrt(repart(x)^2+impart(x)^2);");

		mpreduce.evaluate("procedure flattenlist a;" +
				"if 1=for each elem!! in a product length(elem!!) then for each elem!! in a join elem!! else a;");
		
		mpreduce.evaluate("procedure depth a; if arglength(a)>0 and part(a,0)='list then 1+depth(part(a,1)) else 0;");
		
		mpreduce.evaluate("procedure mysolve(eqn, var);"
				+ " begin scalar solutions!!, bool!!;"
				+ "  eqn:=mkdepthone({eqn});"
				+ "  let solverules;"
				+ "  if arglength(eqn)>-1 and part(eqn,0)='list then"
				+ "    eqn:=for each x in eqn collect"
				+ "      if freeof(x,=) then x else subtraction(lhs(x),rhs(x))"
				+ "  else if freeof(eqn,=) then 1 else eqn:=subtraction(lhs(eqn),rhs(eqn));"
				+ "  solutions!!:=solve(eqn,var);"
				+ "	 if depth(solutions!!)<2 then"
				+ "		solutions!!:=for each x in solutions!! collect {x};"
				+ "	 solutions!!:=for each sol in solutions!! join <<"
				+ "    bool!!:=1;"
				+ "    for each solution!! in sol do"
				+ "      if freeof(solution!!,'root_of) and freeof(solution!!,'one_of) then <<"
				+ "		   on rounded, roundall, numval, complex;"
				+ "		   if freeof(solution!!,'i) or aeval(impart(rhs(solution!!)))=0 then 1 else bool!!:=0;"
				+ "		   off complex;"
				+ "		   if numeric!!=0 then off rounded, roundall, numval"
				+ "      >>" 
				+ "      else" 
				+ "	       bool!!:=2*bool!!;"
				+ "    if bool!!=1 then" 
				+ "  	 {sol}"
				+ "	   else if bool!!>1 then " 
				+ "  	 {{var='?}}" 
				+ "    else "
				+ "		 {} >>;"
				+ "  clearrules solverules;"
				+ "  return mkset(solutions!!);" 
				+ " end;");
		
		mpreduce.evaluate("procedure mycsolve(eqn, var);" +
				" begin scalar solutions!!, bool!!;" +
				"  eqn:=mkdepthone({eqn});" +
				"  let solverules;" +
				"  if arglength(eqn)>-1 and part(eqn,0)='list then" +
				"    eqn:=for each x in eqn collect" +
				"      if freeof(x,=) then x else subtraction(lhs(x),rhs(x))" +
				"  else if freeof(eqn,=) then 1 else eqn:=subtraction(lhs(eqn),rhs(eqn));" +
				"    solutions!!:=solve(eqn,var);" +
				"    if depth(solutions!!)<2 then" +
				"      solutions!!:=for each x in solutions!! collect {x};" +
				"    solutions!!:= for each sol in solutions!! join <<" +
				"      bool!!:=1;" +
				"      for each solution!! in sol do" +
				"        if freeof(solution!!,'root_of) and freeof(solution!!,'one_of) then 1 else" +
				"      		bool!!:=0;" +
				"      if bool!!=1 then" +
				"        {sol}" +
				"      else if bool!!=0 then" +
				"        {{var='?}}" +
				"      >>;" +
				"  clearrules solverules;" +
				"  return mkset(solutions!!);" +
				" end;");
		
		mpreduce.evaluate("procedure mysolve1(eqn);"
				+ " begin scalar solutions!!, bool!!;"
				+ "  eqn:=mkdepthone({eqn});"
				+ "  let solverules;"
				+ "  if arglength(eqn)>-1 and part(eqn,0)='list then"
				+ "    eqn:=for each x in eqn collect"
				+ "      if freeof(x,=) then x else lhs(x)-rhs(x)"
				+ "  else if freeof(eqn,=) then 1 else eqn:=lhs(eqn)-rhs(eqn);"
				+ "  solutions!!:=solve(eqn);"
				+ "	 if depth(solutions!!)<2 then"
				+ "		solutions!!:=for each x in solutions!! collect {x};"
				+ "	 solutions!!:=for each sol in solutions!! join <<"
				+ "    bool!!:=1;"
				+ "    for each solution!! in sol do"
				+ "      if freeof(solution!!,'root_of) then <<"
				+ "		   on rounded, roundall, numval, complex;"
				+ "		   if freeof(solution!!,'i) or aeval(impart(rhs(solution!!)))=0 then 1 else bool!!:=0;"
				+ "		   off complex;"
				+ "		   if numeric!!=0 then off rounded, roundall, numval"
				+ "      >>" 
				+ "      else" 
				+ "	       bool!!:=2*bool!!;"
				+ "    if bool!!=1 then" 
				+ "  	 {sol}"
				+ "	   else if bool!!>1 then " 
				+ "  	 {{'?}}" 
				+ "    else "
				+ "		 {} >>;" 
				+ "  clearrules solverules;"
				+ "  return mkset(solutions!!);" 
				+ " end;");
		
		mpreduce.evaluate("procedure mycsolve1(eqn);" +
				" begin scalar solutions!!, bool!!;" +
				"  let solverules;" +
				"  eqn:=mkdepthone({eqn});" +
				"  if arglength(eqn)>-1 and part(eqn,0)='list then" +
				"    eqn:=for each x in eqn collect" +
				"      if freeof(x,=) then x else lhs(x)-rhs(x)" +
				"  else if freeof(eqn,=) then 1 else eqn:=lhs(eqn)-rhs(eqn);" +
				"    solutions!!:=solve(eqn);" +
				"    if depth(solutions!!)<2 then" +
				"      solutions!!:=for each x in solutions!! collect {x};" +
				"    solutions!!:= for each sol in solutions!! join <<" +
				"      bool!!:=1;" +
				"      for each solution!! in sol do" +
				"        if freeof(solution!!,'root_of) then 1 else" +
				"      		bool!!:=0;" +
				"      if bool!!=1 then" +
				"        {sol}" +
				"      else if bool!!=0 then" +
				"        {{var='?}}" +
				"      >>;" +
				"  clearrules solverules;" +
				"  return mkset(solutions!!);" +
				" end;");
		
		mpreduce.evaluate("procedure dot(vec1,vec2); "
				+ "	begin scalar tmplength; "
				+ "  if arglength(vec1)>-1 and part(vec1,0)='mat and column_dim(vec1)=1 then "
				+ "    vec1:=tp(vec1);"
				+ "  if arglength(vec2)>-1 and part(vec2,0)='mat and column_dim(vec2)=1 then "
				+ "    vec2:=tp(vec2); "
				+ "  return  "
				+ "  if arglength(vec1)>-1 and part(vec1,0)='list then << "
				+ "    if arglength(vec2)>-1 and part(vec2,0)='list then  "
				+ "      <<tmplength:=length(vec1);  "
				+ "      for i:=1:tmplength  "
				+ "			sum part(vec1,i)*part(vec2,i) >> "
				+ "    else if arglength(vec2)>-1 and part(vec2,0)='mat and row_dim(vec2)=1 then"
				+ "      <<tmplength:=length(vec1);  "
				+ "      for i:=1:tmplength  "
				+ "	sum part(vec1,i)*vec2(1,i)>> "
				+ "      else "
				+ "	'? "
				+ "  >> "
				+ "  else <<if arglength(vec1)>-1 and part(vec1,0)='mat and row_dim(vec1)=1 then << "
				+ "    if arglength(vec2)>-1 and part(vec2,0)='list then  "
				+ "      <<tmplength:=length(vec2); "
				+ "      for i:=1:tmplength  "
				+ "			sum vec1(1,i)*part(vec2,i)>> "
				+ "    else if arglength(vec2)>-1 and part(vec2,0)='mat and row_dim(vec2)=1 then"
				+ "      <<tmplength:=column_dim(vec1);  "
				+ "      for i:=1:tmplength  " 
				+ "			sum vec1(1,i)*vec2(1,i) "
				+ "      >> " 
				+ "      else " 
				+ "		'? " 
				+ "    >> " 
				+ "  else "
				+ "    '? " 
				+ "  >> " 
				+ "end;");
		
		mpreduce.evaluate("procedure cross(atmp,btmp); " +
				"begin;" +
				"  a:=atmp; b:= btmp;" +
				"  if arglength(a)=-1 or (length(a) neq 3 and length(a) neq 2 and length(a) neq {1,3} and length(a) neq {3,1} and length(a) neq {1,2} and length(a) neq {2,1}) then return '?;" +
				"  if arglength(b)=-1 or (length(b) neq 3 and length(b) neq 2 and length(b) neq {1,3} and length(b) neq {3,1} and length(b) neq {1,2} and length(b) neq {2,1}) then return '?;" +
				"  if length(a)={1,3} or length(b)={1,2} then a:=tp(a);" +
				"  if length(b)={1,3} or length(b)={1,2} then b:=tp(b);" +
				"  return" +
				"  if arglength(a)>-1 and part(a,0)='mat then <<" +
				"    if arglength(b)>-1 and part(b,0)='mat then <<" +
				"      if length(a)={3,1} and length(b)={3,1} then" +
				"        mat((a(2,1)*b(3,1)-a(3,1)*b(2,1))," +
				"        (a(3,1)*b(1,1)-a(1,1)*b(3,1))," +
				"        (a(1,1)*b(2,1)-a(2,1)*b(1,1)))" +
				"      else if length(a)={2,1} and length(b)={2,1} then" +
				"        mat((0)," +
				"        (0)," +
				"        (a(1,1)*b(2,1)-a(2,1)*b(1,1)))" +
				"      else '?" +
				"    >> else if arglength(b)>-1 and part(b,0)='list then <<" +
				"      if length(a)={3,1} and length(b)=3 then" +
				"        list(a(2,1)*part(b,3)-a(3,1)*part(b,2)," +
				"        a(3,1)*part(b,1)-a(1,1)*part(b,3)," +
				"        a(1,1)*part(b,2)-a(2,1)*part(b,1))" +
				"      else if length(a)={2,1} and length(b)=2 then" +
				"        list(0," +
				"        0," +
				"        a(1,1)*part(b,2)-a(2,1)*part(b,1))" +
				"      else '?" +
				"    >> else << '? >>" +
				"  >> else if arglength(a)>-1 and part(a,0)='list then <<" +
				"    if arglength(b)>-1 and part(b,0)='mat then <<" +
				"      if length(a)=3 and length(b)={3,1} then" +
				"        list(part(a,2)*b(3,1)-part(a,3)*b(2,1)," +
				"        part(a,3)*b(1,1)-part(a,1)*b(3,1)," +
				"        part(a,1)*b(2,1)-part(a,2)*b(1,1))" +
				"      else if length(a)=2 and length(b)={2,1} then" +
				"        list(0," +
				"        0," +
				"        part(a,1)*b(2,1)-part(a,2)*b(1,1))" +
				"      else '?" +
				"    >> else if arglength(b)>-1 and part(b,0)='list then <<" +
				"      if length(a)=3 and length(b)=3 then" +
				"        list(part(a,2)*part(b,3)-part(a,3)*part(b,2)," +
				"        part(a,3)*part(b,1)-part(a,1)*part(b,3)," +
				"        part(a,1)*part(b,2)-part(a,2)*part(b,1))" +
				"      else if length(a)=2 and length(b)=2 then" +
				"        list(0," +
				"        0," +
				"        part(a,1)*part(b,2)-part(a,2)*part(b,1))" +
				"      else '?" +
				"    >> else << '? >>" +
				"  >> else << '? >> " +
				"end;");

		mpreduce.evaluate("procedure mattoscalar(m);"
				+ " if length(m)={1,1} then trace(m) else m;");

		mpreduce.evaluate("procedure multiplication(a,b);"
				+ "  if arglength(a)>-1 and part(a,0)='mat then"
				+ "    if arglength(b)>-1 and part(b,0)='mat then"
				+ "      mattoscalar(a*b)"
				+ "    else if arglength(b)>-1 and part(b,0)='list then"
				+ "      mattoscalar(a*<<listtocolumnvector(b)>>)"
				+ "    else"
				+ "      a*b"
				+ "  else if arglength(a)>-1 and part(a,0)='list then"
				+ "    if arglength(b)>-1 and part(b,0)='mat then"
				+ "      mattoscalar(<<listtorowvector(a)>>*b)"
				+ "    else if arglength(b)>-1 and part(b,0)='list then"
				+ "      mattoscalar(<<listtorowvector(a)>>*<<listtocolumnvector(b)>>)"
				+ "    else" 
				+ "      map(~w!!*b,a)" 
				+ "  else"
				+ "    if arglength(b)>-1 and part(b,0)='list then" 
				+ "      map(a*~w!!,b)"
				+ "    else"
				+ "		 if a=infinity then"
				+ "		   if (numberp(b) and b>0) or b=infinity then infinity"
				+ "		   else if (numberp(b) and b<0) or b=-infinity then -infinity"
				+ "		   else '?"
				+ "		 else if a=-infinity then"
				+ "		   if (numberp(b) and b>0) or b=infinity then -infinity"
				+ "		   else if (numberp(b) and b<0) or b=-infinity then infinity"
				+ "		   else '?"
				+ "		 else if b=infinity then"
				+ "		   if (numberp(a) and a>0) or a=infinity then infinity"
				+ "		   else if (numberp(a) and a<0) or a=-infinity then -infinity"
				+ "		   else '?"
				+ "		 else if b=-infinity then"
				+ "		   if (numberp(a) and a>0) or a=infinity then -infinity"
				+ "		   else if (numberp(a) and a<0) or a=infinity then infinity"
				+ "		   else '?"
				+ "		 else"
				+ "        a*b;");
		
		mpreduce.evaluate("operator multiplication;");

		mpreduce.evaluate("procedure addition(a,b);"
				+ "  if arglength(a)>-1 and part(a,0)='list and arglength(b)>-1 and part(b,0)='list then"
				+ "    for i:=1:length(a) collect part(a,i)+part(b,i)"
				+ "  else if arglength(a)>-1 and part(a,0)='list then" 
				+ "    map(~w!!+b,a)"
				+ "  else if arglength(b)>-1 and part(b,0)='list then" 
				+ "    map(a+~w!!,b)"
				+ "  else if (a=infinity and b neq -infinity) or (b=infinity and a neq -infinity) then"
				+ "    infinity" 
				+ "  else if (a=-infinity and b neq infinity) or (b=-infinity and a neq infinity) then"
				+ "    -infinity"
				+ "  else"
				+ "    a+b;");
		
		mpreduce.evaluate("operator addition;");

		mpreduce.evaluate("procedure subtraction(a,b);"
				+ "  if arglength(a)>-1 and part(a,0)='list and arglength(b)>-1 and part(b,0)='list then"
				+ "    for i:=1:length(a) collect part(a,i)-part(b,i)"
				+ "  else if arglength(a)<-1 and part(a,0)='list then" 
				+ "    map(~w!!-b,a)"
				+ "  else if arglength(b)>-1 and part(b,0)='list then" 
				+ "    map(a-~w!!,b)"
				+ "  else if (a=infinity and b neq infinity) or (b=-infinity and a neq -infinity) then "
				+ "    infinity"
				+ "  else if (a=-infinity and b neq -infinity) or (b=infinity and a neq infinity) then "
				+ "    -infinity"
				+ "  else"
				+ "    a-b;");
		
		mpreduce.evaluate("operator subtraction;");
		
		// erf in Reduce is currently broken:
		// http://sourceforge.net/projects/reduce-algebra/forums/forum/899364/topic/4546339
		// this is a numeric approximation according to Abramowitz & Stegun
		// 7.1.26.
		mpreduce.evaluate("procedure myerf(x); "
				+ "begin scalar a1!!, a2!!, a3!!, a4!!, a5!!, p!!, x!!, t!!, y!!, sign!!, result!!;"
				+ "     on rounded;"
				+ "		if numberp(x) then 1 else return !*hold(erf(x));"
				+ "     if x=0 then return 0;"
				+ "     a1!! :=  0.254829592; "
				+ "     a2!! := -0.284496736; "
				+ "     a3!! :=  1.421413741; "
				+ "     a4!! := -1.453152027; "
				+ "     a5!! :=  1.061405429; "
				+ "     p!!  :=  0.3275911; "
				+ "     sign!! := 1; "
				+ "     if x < 0 then sign!! := -1; "
				+ "     x!! := Abs(x); "
				+ "     t!! := 1.0/(1.0 + p!!*x!!); "
				+ "     y!! := 1.0 - (((((a5!!*t!! + a4!!)*t!!) + a3!!)*t!! + a2!!)*t!! + a1!!)*t!!*Exp(-x!!*x!!); "
				+ "     result!! := sign!!*y!!;"
				+ "     if numeric!!=1 then off rounded;"
				+ "     return result!! " 
				+ "end;");


		
		mpreduce.evaluate("procedure mkdepthone(liste);" +
				"	for each x in liste join " +
				"	if arglength(x)>-1 and part(x,0)='list then" +
				"	mkdepthone(x) else {x};");
		
		mpreduce.evaluate("procedure listtocolumnvector(list); "
				+ "begin scalar lengthoflist; "
				+ "lengthoflist:=length(list); "
				+ "matrix m!!(lengthoflist,1); " 
				+ "for i:=1:lengthoflist do "
				+ "m!!(i,1):=part(list,i); " 
				+ "return m!! " 
				+ "end;");

		mpreduce.evaluate("procedure listtorowvector(list); "
				+ "begin scalar lengthoflist; "
				+ "	lengthoflist:=length(list); "
				+ "	matrix m!!(1,lengthoflist); "
				+ "	for i:=1:lengthoflist do " 
				+ "		m!!(1,i):=part(list,i); "
				+ "	return m!!; " 
				+ "end;");

		mpreduce.evaluate("procedure mod!!(a,b);" +
				" a-b*div(a,b);");
		
		mpreduce.evaluate("procedure div(a,b);" +
				" begin scalar a!!, b!!, result!!;" +
				"  a!!:=a; b!!:=b;" +
				"  on rounded, roundall, numval;" +
				"  return " +
				"  if numberp(a!!) and numberp(b!!) then <<" +
				"    if numeric!!=0 then" +
				"      off rounded, roundall, numval;" +
				"    if b!!>0 then " +
				"	   floor(a/b)" +
				"    else" +
				"      ceiling(a/b)" +
				"  >> else << " +
				"    if numeric!!=0 then" +
				"      off rounded, roundall, numval;" +
				"    part(divide(a,b),1)>>" +
				" end;");
		
		// to avoid using the package assist
		mpreduce.evaluate("procedure mkset a;" +
				" begin scalar result, bool;" +
				"  result:=list();" +
				"  for each elem in a do <<" +
				"  bool:=1;" +
				"  for each x in result do" +
				"    if elem=x then bool:=0;" +
				"  if bool=1 then" +
				"    result:=elem . result;" +
				"  >>;" +
				"  return reverse(result)" +
				" end;");
		
		mpreduce.evaluate("procedure shuffle a;" +
				"begin scalar lengtha,s,tmp;" +
				" lengtha:=length(a);" +
				" if lengtha>1 then" +
				"  for i:=lengtha step -1 until 1 do <<" +
				"   s:=random(i)+1;" +
				"   tmp:= part(a,i);" +
				"   a:=(part(a,i):=part(a,s));" +
				"   a:=(part(a,s):=tmp);" +
				"  >>;" +
				" return a " +
				"end;");
		
		mpreduce.evaluate("procedure listofliststomat(a); " +
				" begin scalar length!!, bool!!, i!!, elem!!;" +
				"  return" +
				"  if arglength(a)>-1 and part(a,0)='list then <<" +
				"    length!!:=-1;" +
				"    bool!!:=1;" +
				"    i!!:=0;" +
				"    while i!!<length(a) and bool!!=1 do <<" +
				"      i!!:=i!!+1;" +
				"      elem!!:=part(a,i!!);" +
				"      if arglength(elem!!)<0 or part(elem!!,0) neq 'list or (length(elem!!) neq length!! and length!! neq -1) then" +
				"        bool!!:=0" +
				"      else <<" +
				"        length!!:=length(elem!!);" +
				"        if 0=(for i:=1:length(elem!!) product if freeof(elem!!,=) then 1 else 0) then" +
				"          bool!!:=0;" +
				"      >>" +
				"    >>;" +
				"    if bool!!=0 or length(a)=0 then a" +
				"    else <<" +
				"      matrix matrix!!(length(a),length(part(a,1)));" +
				"      for i:=1:length(a) do" +
				"        for j!!:=1:length(part(a,1)) do" +
				"          matrix!!(i,j!!):=part(part(a,i),j!!);" +
				"      matrix!!>>" +
				"    >>" +
				" else" +
				"    a;" +
				" end;");
		
		mpreduce.evaluate("procedure mattolistoflists(a);" +
				" begin scalar list!!, j!!;" +
				"  tmpmatrix!!:=a;" +
				"  return" +
				"  if arglength(a)<0 or part(a,0) neq 'mat then" +
				"    tmpmatrix" +
				"  else" +
				"    for i:=1:part(length(a),1) collect" +
				"      for j!!:=1:part(length(a),2) collect" +
				"        tmpmatrix!!(i,j!!)" +
				" end;");
		
		mpreduce.evaluate("procedure mysort a;" +
				"begin scalar leftlist, rightlist, eqlist;" +
				" leftlist:=list();" +
				" rightlist:=list();" +
				" eqlist:=list();" +
				" return" +
				" if length(a)<2 then a" +
				" else <<" +
				"  for each elem in a do" +
				"    if elem<part(a,1) then" +
				"     leftlist:=elem . leftlist" +
				"    else if elem=part(a,1) then" +
				"     eqlist:=elem . eqlist" +
				"    else" +
				"     rightlist:=elem . rightlist;" +
				"  if length(leftlist)=0 and length(rightlist)=0 then" +
				"    eqlist" +
				"  else if length(leftlist)=0 then" +
				"    append(eqlist, mysort(rightlist))" +
				"  else if length(rightlist)=0 then" +
				"    append(mysort(leftlist), eqlist)" +
				"  else" +
				"    append(append(mysort(leftlist),eqlist),mysort(rightlist))" +
				" >> " +
				"end;");
		
		mpreduce.evaluate("procedure getkernels(a);" +
				"	for each element in a sum" +
				"	  if arglength(element)=-1 then" +
				"	    element" +
				"	  else" +
				"	    getkernels(part(element,0):=list);");

		mpreduce.evaluate("procedure mymainvaraux a;" +
				"if numberp(a) then currentx!! else a;");
		
		mpreduce.evaluate("procedure mymainvar a;" +
				"mainvar(mymainvaraux(getkernels(list(a))));");
		
		mpreduce.evaluate("procedure myint(exp!!, var!!, from!!, to!!);" +
				"begin scalar integrand!!;" +
				"antiderivative!!:=int(exp!!, var!!);" +
				"return sub(var!!=to!!,antiderivative!!)-sub(var!!=from!!,antiderivative!!)" +
				"end;");
	}

	private static String getVersionString(Interpreter2 mpreduce) {
		Pattern p = Pattern.compile("version (\\S+)");
		Matcher m = p.matcher(mpreduce.getStartMessage());
		if (!m.find())
			return "MPReduce";
		else {
			StringBuilder sb = new StringBuilder();
			sb.append("MPReduce ");
			sb.append(m.group(1));
			return sb.toString();
		}
	}
	
	/**
	 * Sets the number of signficiant figures (digits) that should be used as print precision for the
	 * output of Numeric[] commands.
	 * 
	 * @param significantNumbers
	 */
	public void setSignificantFiguresForNumeric(int significantNumbers) {
		if (this.significantNumbers==significantNumbers)
			return;
		this.significantNumbers=significantNumbers;
		try{
			getMPReduce().evaluate("printprecision!!:=" + significantNumbers);
		} catch (Throwable th) {
			th.printStackTrace();
		}
	}
}
