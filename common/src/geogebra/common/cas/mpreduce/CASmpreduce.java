package geogebra.common.cas.mpreduce;

import geogebra.common.cas.CASException;
import geogebra.common.cas.CASparser;
import geogebra.common.cas.CasParserTools;
import geogebra.common.cas.Evaluate;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.arithmetic.ExpressionValue;
import geogebra.common.kernel.arithmetic.FunctionNVar;
import geogebra.common.kernel.arithmetic.MyArbitraryConstant;
import geogebra.common.kernel.arithmetic.Traversing.ArbconstReplacer;
import geogebra.common.kernel.arithmetic.Traversing.PowerRootReplacer;
import geogebra.common.kernel.arithmetic.ValidExpression;
import geogebra.common.kernel.cas.AsynchronousCommand;
import geogebra.common.kernel.cas.CASGenericInterface;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.App;
import geogebra.common.main.settings.AbstractSettings;
import geogebra.common.main.settings.CASSettings;
import geogebra.common.util.StringUtil;

import java.util.StringTokenizer;

/**
 * Platform (Java / GWT) independent part of MPReduce CAS
 */
public abstract class CASmpreduce implements CASGenericInterface {
	/** parser tools */
	protected CasParserTools parserTools;
	private String casPrefix;
	/** CAS parser */
	public CASparser casParser;
	/** variable ordering, e.g. for Integral[a*b] */
	protected static StringBuilder varOrder = new StringBuilder(
			"ggbtmpvarx, ggbtmpvary, ggbtmpvarz, ggbtmpvara, "
					+ "ggbtmpvarb, ggbtmpvarc, ggbtmpvard, ggbtmpvare, ggbtmpvarf, "
					+ "ggbtmpvarg, ggbtmpvarh, ggbtmpvari, ggbtmpvarj, ggbtmpvark, "
					+ "ggbtmpvarl, ggbtmpvarm, ggbtmpvarn, ggbtmpvaro, ggbtmpvarp, "
					+ "ggbtmpvarq, ggbtmpvarr, ggbtmpvars, ggbtmpvart, ggbtmpvaru, "
					+ "ggbtmpvarv, ggbtmpvarw");
	/** number of significant digits; for -1 use kernel default */
	protected int significantNumbers = -1;
	/**
	 * We escape any upper-letter words so Reduce doesn't switch them to /
	 * lower-letter, / however the following function-names should not be
	 * escaped / (note: all functions here must be in lowercase!)
	 */

	private static Evaluate mpreduce;

	/**
	 * Creates new MPReduce CAS
	 * 
	 * @param casParser
	 *            parser
	 * @param casPrefix
	 *            prefix for CAS variables
	 */
	public CASmpreduce(CASparser casParser, String casPrefix) {
		this.casParser = casParser;
		this.casPrefix = casPrefix;
	}

	/**
	 * @param exp
	 *            MPREduce command
	 * @return value returned from CAS
	 */
	public abstract String evaluateMPReduce(String exp);

	final public String evaluateRaw(final String input) throws Throwable {
		String exp = input;
		// we need to escape any upper case letters and non-ascii codepoints
		// with '!'
		StringTokenizer tokenizer = new StringTokenizer(exp, "(),;[] ", true);
		StringBuilder sb = new StringBuilder();
		while (tokenizer.hasMoreElements()) {
			String t = tokenizer.nextToken();
			if (casParser.getParserFunctions().isReserved(t))
				sb.append(t);
			else {
				for (int i = 0; i < t.length(); ++i) {
					char c = t.charAt(i);
					if (StringUtil.isLetter(c) && (c < 97 || c > 122)) {
						sb.append('!');
						sb.append(c);
					} else {
						switch (c) {
						case '\'':
							sb.append('!');
							sb.append(c);
							break;

						case '\\':
							if (i < (t.length() - 1))
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

		App.debug("eval with MPReduce: " + exp);
		String result = getMPReduce().evaluate(exp, getTimeoutMilliseconds());

		sb.setLength(0);
		for (String s : result.split("\n")) {
			s = s.trim();
			if (s.length() == 0)
				continue;
			else if (s.startsWith("***")) { // MPReduce comment
				App.debug("MPReduce comment: " + s);
				continue;
			} else if (s.startsWith("Unknown")) {
				App.debug("Assumed " + s);
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

		result = sb.toString().replaceAll("\\[", "(").replaceAll("\\]", ")");

		// TODO: remove
		App.debug("   result: " + result);
		return result;
	}

	final public synchronized String evaluateGeoGebraCAS(
			final ValidExpression inputExpression, MyArbitraryConstant arbconst,
			StringTemplate tpl) throws CASException {
		ValidExpression casInput = inputExpression;
		// KeepInput[] command should set flag keepinput!!:=1
		// so that commands like Substitute can work accordingly
		boolean keepInput = casInput.isKeepInputUsed();
		boolean taylorToStd = true;
		if (keepInput) {
			// remove KeepInput[] command and take argument
			Command cmd = casInput.getTopLevelCommand();
			if (cmd != null && cmd.getName().equals("KeepInput")) {
				// use argument of KeepInput as casInput
				if (cmd.getArgumentNumber() > 0) {
					casInput = cmd.getArgument(0);
				}
			}
		} else if (casInput.isTopLevelCommand()) {
			Command cmd = casInput.getTopLevelCommand();
			if (cmd != null && cmd.getName().equals("TaylorSeries")) {
				taylorToStd = false;
			}
		}

		// convert parsed input to MPReduce string
		String mpreduceInput = casParser.translateToCAS(casInput,
				StringTemplate.casTemplate, this);

		// tell MPReduce whether it should use the keep input flag,
		// e.g. important for Substitute
		StringBuilder sb = new StringBuilder();
		sb.append("<<keepinput!!:=");
		sb.append(keepInput ? 1 : 0);
		sb.append("$taylortostd:=");
		sb.append(taylorToStd ? 1 : 0);
		// set default switches
		// (note: off factor turns on exp, so off exp must be placed later)

		sb.append("$ numeric!!:=0$ precision 30$ print\\_precision 16$ off allfac,revpri, complex, rounded, numval, factor, div, combinelogs, expandlogs, combineexpt$ on pri, rationalize$ currentx!!:= ");

		// sb.append("$ numeric!!:=0$ precision 30$ print\\_precision 16$ on pri, rationalize  $ off complex, rounded, numval, factor, exp, allfac, div, combinelogs, expandlogs, revpri $ currentx!!:= ");
		if (arbconst == null || arbconst.isCAS()) {
			sb.append(casPrefix);
			sb.append("x; currenty!!:= ");
			sb.append(casPrefix);
			sb.append("y;");
		} else {
			sb.append("ggbtmpvarx;currenty!!:=ggbtmpvary;");
		}

		sb.append(mpreduceInput);
		sb.append(">>");

		// evaluate in MPReduce
		String result = evaluateMPReduce(sb.toString());
		if (keepInput) {
			// when keepinput was treated in MPReduce, it is now > 1
			String keepinputVal = evaluateMPReduce("keepinput!!;");
			boolean keepInputUsed = !"1".equals(keepinputVal);
			if (!keepInputUsed) {
				result = casParser.toGeoGebraString(casInput, tpl);
			}
		}

		// convert result back into GeoGebra syntax
		if (casInput instanceof FunctionNVar) {
			// function definition f(x) := x^2 should return x^2
			// f(x):=Derivative[x^2] should return 2x
			return toGeoGebraString(
					evaluateMPReduce(result
							+ "("
							+ ((FunctionNVar) casInput).getVarString(StringTemplate.casTemplate)
							+ ")"), arbconst, tpl);
		}
		Command cmd = casInput.getTopLevelCommand();
		if (cmd != null && "Delete".equals(cmd.getName())
				&& "true".equals(result)) {
			GeoElement geo = inputExpression
					.getKernel()
					.getConstruction()
					.lookupLabel(
							cmd.getArgument(0).toString(
									StringTemplate.defaultTemplate));
			if (geo != null) {
				geo.remove();
			}
		}
		// standard case
		if ("".equals(result)) {
			return null;
		}
		return toGeoGebraString(result, arbconst, tpl);
	}

	public String translateFunctionDeclaration(final String label,
			final String parameters, final String body) {
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

	/**
	 * Tries to parse a given MPReduce string and returns a String in GeoGebra
	 * syntax.
	 * 
	 * @param mpreduceString
	 *            String in MPReduce syntax
	 * @param arbconst
	 *            arbitrary constant handler
	 * @param tpl
	 *            template that should be used for serialization. Should be
	 *            casCellTemplate for CAS and defaultTemplate for input bar
	 * @return String in Geogebra syntax.
	 * @throws CASException
	 *             Throws if the underlying CAS produces an error
	 */
	final public synchronized String toGeoGebraString(String mpreduceString,
			MyArbitraryConstant arbconst, StringTemplate tpl)
			throws CASException {
		ExpressionValue ve = casParser.parseMPReduce(mpreduceString);
		// replace rational exponents by roots or vice versa

		if (ve != null) {
			boolean toRoot = ve.getKernel().getApplication().getSettings()
					.getCasSettings().getShowExpAsRoots();
			ve.traverse(PowerRootReplacer.getReplacer(toRoot));
			if (arbconst != null) {
				arbconst.reset();
				ve.traverse(ArbconstReplacer.getReplacer(arbconst));
			}
		}

		return casParser.toGeoGebraString(ve, tpl);
	}

	public void unbindVariable(final String var) {
		try {
			StringBuilder sb = new StringBuilder();
			sb.append("clear(");
			sb.append(var);
			sb.append(");");
			getMPReduce().evaluate(sb.toString());

			// TODO: remove
			App.debug("Cleared variable: " + sb.toString());
		} catch (Throwable e) {
			App.debug("Failed to clear variable from MPReduce: " + var);
		}
	}

	/**
	 * @return MPReduce evaluator
	 */
	protected abstract Evaluate getMPReduce();

	public synchronized void reset() {

		try {
			getMPReduce().evaluate("resetreduce;");
			getMPReduce().initialize();
			initDependentMyMPReduceFunctions(getMPReduce());
		} catch (Throwable e) {
			App.debug("failed to reset MPReduce");
			e.printStackTrace();
		}
	}

	/**
	 * Sets the number of significant figures (digits) that should be used as
	 * print precision for the output of Numeric[] commands.
	 * 
	 * @param significantNumbers
	 *            new number of significant digits
	 */
	public void setSignificantFiguresForNumeric(final int significantNumbers) {
		if (this.significantNumbers == significantNumbers)
			return;
		this.significantNumbers = significantNumbers;
		try {
			getMPReduce().evaluate("printprecision!!:=" + significantNumbers);
		} catch (Throwable th) {
			th.printStackTrace();
		}
	}

	/**
	 * Loads all packages and initializes all the functions which do not depend
	 * on the current kernel.
	 * 
	 * @param mpreduce1
	 *            MPReduce evaluator
	 * @throws Throwable
	 *             from evaluator when some of the initial commands fails
	 */
	protected static final void initStaticMyMPReduceFunctions(Evaluate mpreduce1)
			throws Throwable {
		mpreduce1.evaluate("load_package rsolve;");
		mpreduce1.evaluate("load_package numeric;");
		mpreduce1.evaluate("load_package specfn;");
		mpreduce1.evaluate("load_package odesolve;");
		mpreduce1.evaluate("load_package defint;");
		mpreduce1.evaluate("load_package linalg;");
		mpreduce1.evaluate("load_package reset;");
		mpreduce1.evaluate("load_package taylor;");
		mpreduce1.evaluate("load_package groebner;");
		mpreduce1.evaluate("load_package trigsimp;");
		mpreduce1.evaluate("load_package polydiv;");
		mpreduce1.evaluate("load_package myvector;");
		mpreduce1.evaluate("load_package specfn;");

		// Initialize MPReduce
		mpreduce1.evaluate("off nat;");
		mpreduce1.evaluate("off pri;");

		mpreduce1.evaluate("off numval;");
		mpreduce1.evaluate("linelength 50000;");
		mpreduce1.evaluate("scientific_notation {16,5};");
		mpreduce1.evaluate("on fullroots;");
		mpreduce1.evaluate("printprecision!!:=15;");

		mpreduce1
				.evaluate("intrules!!:={"
						+ "int(asin(~x-1),~x) => -x * asin(1 - x) + asin(1 - x) + sqrt(2 * x - x^(2)),"
						+ "int(acos(~x-1),~x) => x * acos(-1 + x) - acos(-1 + x) - sqrt(2 * a * x - x^(2)),"
						+ "int(atan(~x-1),~x) =>(-2 * x * atan(1 - x) + 2 * atan(1 - x) - log(2 - 2  * x + x^(2))) / 2,"
						+ "int(~w/~x,~x) => w*log(abs(x)) when freeof(w,x),"
						+ "int(~w/(~x+~a),~x) => w*log(abs(x+a)) when freeof(w,x) and freeof(a,x),"
						+ "int((~b*~x+~w)/(~x+~a),~x) => int((b*xw)/(x+a),x)+w*log(abs(x+a)) when freeof(w,x) and freeof(a,x) and freeof(b,x),"
						+ "int((~a*~x+~w)/~x,~x) => int(a,x)+w*log(abs(x)) when freeof(w,x) and freeof(a,x),"
						+ "int((~x+~w)/~x,~x) => x+w*log(abs(x)) when freeof(w,x),"
						+ "int(tan(~x),~x) => log(abs(sec(x))),"
						+ "int(~w*tan(~x),~x) => w*log(abs(sec(x))) when freeof(w,x),"
						+ "int(~w+tan(~x),~x) => int(w,x)+log(abs(sec(x))),"
						+ "int(~a+~w*tan(~x),~x) => int(a,x)+w*log(abs(sec(x))) when freeof(w,x),"
						+ "int(cot(~x),~x) => log(abs(sin(x))),"
						+ "int(~w*cot(~x),~x) => w*log(abs(sin(x))) when freeof(w,x),"
						+ "int(~a+cot(~x),~x) => int(a,x)+log(abs(sin(x))),"
						+ "int(~a+~w*cot(~x),~x) => int(a,x)+w*log(abs(sin(x))) when freeof(w,x),"
						+ "int(sec(~x),~x) => -log(abs(tan(x / 2) - 1)) + log(abs(tan(x / 2) + 1)),"
						+ "int(~w*sec(~x),~x) => -log(abs(tan(x / 2) - 1))*w + log(abs(tan(x / 2) + 1) )*w when freeof(w,x),"
						+ "int(~w+sec(~x),~x) => -log(abs(tan(x / 2) - 1)) + log(abs(tan(x / 2) + 1) )+int(w,x),"
						+ "int(~a+w*sec(~x),~x) => -log(abs(tan(x / 2) - 1))*w + log(abs(tan(x / 2) + 1) )*w+int(a,x) when freeof(w,x),"
						+ "int(csc(~x),~x) => log(abs(tan(x / 2))),"
						+ "int(~w*csc(~x),~x) => w*log(abs(tan(x / 2))) when freeof(w,x),"
						+ "int(~w+csc(~x),~x) => int(w,x)+log(abs(tan(x / 2))),"
						+ "int(~a+~w*csc(~x),~x) => int(a,x)+w*log(abs(tan(x / 2))) when freeof(w,x)"
						+ "};");
		mpreduce1.evaluate("operator iffun;");
		mpreduce1.evaluate("operator ifelsefun;");
		mpreduce1.evaluate("let {" + "abs(pi)=>pi,abs(e)=>e};");
		mpreduce1.evaluate("let {" + "df(asin(~x),x) => 1/sqrt(1-x^2),"
				+ "df(acosh(~x),x) => 1/(sqrt(x-1)*sqrt(x+1)),"
				+ "df(asinh(~x),x) => 1/sqrt(1+x^2),"
				+ "df(acos(~x),x) => -1/sqrt(1-x^2),"
				+ "df(ifelsefun(~a,~b,~c),~x) => ifelsefun(a,df(b,x),df(c,x)),"
				+ "df(iffun(~a,~b,~c),~x) => iffun(a,df(b,x),df(c,x))};");

		mpreduce1
				.evaluate("let {impart(arbint(~w)) => 0, arbint(~w)*i =>  0};");
		mpreduce1.evaluate("let {atan(sin(~x)/cos(~x))=>x, "
				+ "acos(1/sqrt(2)) => pi/4" + "};");

		mpreduce1.evaluate("solverules:={" + "logb(~x,~b)=>log(x)/log(b),"
				+ "log10(~x)=>log(x)/log(10)" + "};");

		mpreduce1
				.evaluate("procedure myatan2(y,x);"
						+ " begin scalar xinput, yinput;"
						+ " xinput:=x; yinput:=y;"
						+ " on rounded, roundall, numval;"
						+ " x:=x+0; y:=y+0;"
						+ " return "
						+ " if numberp(y) and numberp(x) then"
						+ "   if x>0 then <<if numeric!!=0 then off rounded, roundall, numval; atan(yinput/xinput)>>"
						+ "   else if x<0 and y>=0 then <<if numeric!!=0 then off rounded, roundall, numval; atan(yinput/xinput)+pi>>"
						+ "   else if x<0 and y<0 then <<if numeric!!=0 then off rounded, roundall, numval; atan(yinput/xinput)-pi>>"
						+ "   else if x=0 and y>0 then <<if numeric!!=0 then off rounded, roundall, numval; pi/2>>"
						+ "   else if x=0 and y<0 then <<if numeric!!=0 then off rounded, roundall, numval; -pi/2>>"
						+ "   else if x=0 and y=0 then <<if numeric!!=0 then off rounded, roundall, numval; 0>>"
						+ "   else '?" + " else" + "   atan(y/x) end;");

		mpreduce1.evaluate("procedure mycoeff(p,x);"
				+ " begin scalar coefflist, bool!!;"
				+ " on ratarg;"
				+ " coefflist:=coeff(p,x);"
				+ " off ratarg;"
				+ " if 1=for each elem!! in coefflist product"
				+ "   if freeof(elem!!,x) then 1 else 0 then"
				+ "   return reverse(coefflist)" + " else" + "   return '?"
				+ " end;");
		mpreduce1
				.evaluate("procedure myand (a,b); if a=true and b=true then true else if (a=true or a=false) and (b=true or b=false) then false else sand(a,b)");
		mpreduce1.evaluate("operator sand");
		mpreduce1
				.evaluate("procedure myor (a,b); if a=true or b=true then true else if (a=true or a=false) and (b=true or b=false) then false else sor(a,b)");
		mpreduce1.evaluate("operator sor");
		mpreduce1
				.evaluate("procedure myimplies (a,b); if a=false or b=true then true else  if (a=true or a=false) and (b=true or b=false) then false else simplies(a,b)");
		mpreduce1.evaluate("operator simplies");
		mpreduce1
				.evaluate("procedure mynot a; if a=false then true else  if (a=true or a=false) then false else sand(a,b)");
		mpreduce1.evaluate("operator snot");

		mpreduce1
				.evaluate("procedure mygreater (a,b); if numberp(a) and numberp(b) then"
						+ "(if a>b then true else false) else sgreater(a,b)");
		mpreduce1.evaluate("operator sgreater");
		mpreduce1
				.evaluate("procedure myless (a,b);if numberp(a) and numberp(b) then"
						+ "(if a<b then true else false) else sless(a,b)");

		mpreduce1.evaluate("operator sless");
		mpreduce1
				.evaluate("procedure mygreaterequal (a,b); if numberp(a) and numberp(b) then"
						+ "(if a>=b then true else false) else sgreaterequal(a,b)");
		mpreduce1.evaluate("operator sgreater");
		mpreduce1
				.evaluate("procedure myless (a,b);if numberp(a) and numberp(b) then"
						+ "(if a<=b then true else false) else slessequal(a,b)");
		mpreduce1.evaluate("operator slessequal");
		mpreduce1.evaluate(" Degree := pi/180;");

		mpreduce1
				.evaluate("procedure myround(x);"
						+ "begin; on numval, rounded; r:=floor(x+0.5); off numval, rounded; return r;end");

		mpreduce1.evaluate("procedure harmonic(n,m); for i:=1:n sum 1/(i**m);");
		mpreduce1.evaluate("procedure uigamma(n,m); gamma(n)-igamma(n,m);");
		mpreduce1.evaluate("procedure beta!Regularized(a,b,x); ibeta(a,b,x);");
		mpreduce1
				.evaluate("procedure myarg(x);"
						+ " if arglength(x)>-1 and part(x,0)='list then myatan2(part(x,2), part(x,1)) "
						+ " else if arglength(x)>-1 and part(x,0)='mat then <<"
						+ "   clear x!!;"
						+ "   x!!:=x;"
						+ "   if row_dim(x!!)=1 then myatan2(x!!(1,2),x!!(1,1))"
						+ "   else if column_dim(x!!)=1 then myatan2(x!!(2,1),x!!(2,1))"
						+ "   else arg(x!!) >>"
						+ " else myatan2(impart(x),repart(x));");
		mpreduce1
				.evaluate("procedure polartocomplex(r,phi); r*(cos(phi)+i*sin(phi));");
		mpreduce1
				.evaluate("procedure polartopoint!\u00a7(r,phi); myvect(r*cos(phi),r*sin(phi));");
		mpreduce1
				.evaluate("procedure complexexponential(r,phi); r*(cos(phi)+i*sin(phi));");
		mpreduce1.evaluate("procedure conjugate(x); conj(x);");
		mpreduce1
				.evaluate("procedure myrandom(); <<on rounded; random(100000001)/(random(100000000)+1)>>;");
		mpreduce1.evaluate("procedure gamma!Regularized(a,x); igamma(a,x);");
		mpreduce1.evaluate("procedure gamma2(a,x); gamma(a)*igamma(a,x);");
		mpreduce1.evaluate("procedure beta3(a,b,x); beta(a,b)*ibeta(a,b,x);");
		mpreduce1
				.evaluate("symbolic procedure isbound!! x; if get(x, 'avalue) then 1 else 0;");
		mpreduce1
				.evaluate("procedure myappend(x,y);"
						+ "if arglength(x)>-1 and part(x,0)='list then append(x,{y}) else append({x},y)");
		mpreduce1
				.evaluate("procedure mylength(x);"
						+ " if arglength(x)>-1 and part(x,0)='list then length(x) else sqrt(mydot(x,x));");
		mpreduce1
				.evaluate("procedure mytangent(pt,f);"
						+ "currenty!!=sub(currentx!!=pt,f)+sub(currentx!!=pt,df(f,mymainvar(f)))*(currentx!!-(pt))");
		mpreduce1.evaluate("procedure myabs(x);"
				+ " if arglength(x!!)>-1 and part(x,0)='list then abs(x)"
				+ " else if arglength(x)>-1 and part(x,0)='mat then <<"
				+ "   clear tmp;" + "   tmp:=x;"
				+ "   for i:=1:column_dim(x) do"
				+ "     for j:=1:row_dim(x) do" + "		  tmp(i,j):=myabs(tmp);"
				+ "   tmp>>" + " else if myvecp(x) then" + "   vmod x"
				+ " else if freeof(x,i) then abs(x)"
				+ " else sqrt(repart(x)^2+impart(x)^2);");

		mpreduce1
				.evaluate("procedure flattenlist a;"
						+ "if part(a,0)='list and 1=for each elem!! in a product length(elem!!) then for each elem!! in a join elem!! else a;");

		mpreduce1
				.evaluate("procedure depth a; if arglength(a)>0 and part(a,0)='list then 1+depth(part(a,1)) else 0;");

		mpreduce1.evaluate("operator ggbinterval;");
		mpreduce1
				.evaluate("procedure mkinterval(var,eqn,a,b);"
						+ "begin scalar ineqtype;"
						+ "ineqtype:= if part(eqn,0)= 'leq or part(eqn,0)= 'geq then 3 else 0;"
						+ "return ggbinterval(var,a,b,ineqtype);" + "end;");

		mpreduce1
				.evaluate("procedure xcoord(a); if myvecp(a) then xvcoord(a) else xscoord(a)");
		mpreduce1.evaluate("operator xscoord");
		mpreduce1
				.evaluate("procedure ycoord(a); if myvecp(a) then yvcoord(a) else yscoord(a)");
		mpreduce1.evaluate("operator yscoord");
		mpreduce1
				.evaluate("procedure zcoord(a); if myvecp(a) then zvcoord(a) else zscoord(a)");
		mpreduce1.evaluate("operator zscoord");

		mpreduce1
				.evaluate("procedure booltonum a; if a = true then 1 else if a = false then 0 else a;");
		mpreduce1
		.evaluate("procedure mynumsolve(a,b); " +
				" begin;scalar eqn, denumer;" +
				" a:=mkdepthone(list(a));"+
				" b:=mkdepthone(list(b));"+
				" if length(a)=1 then <<eqn:=num(lhs(part(a,1))-rhs(part(a,1)));denumer:=den(lhs(part(a,1))-rhs(part(a,1)))>>;"+
				" return if length(a)=1 and not(mycoeff(eqn,part(b,1))='?)" +
				" then mkdepthone(for each r in roots(eqn) collect if freeof(r,i) and sub(r,denumer) then list(r) else list())" +
				" else num_solve(a,b,iterations=10000);" +
				" end;");
		mpreduce1
		.evaluate("procedure listtodisjunction(v,lst);" +
				"begin scalar ret;" +
				"ret:=part(lst,1);"+
				"for i:=2:length(lst) do ret:=sor(ret,part(lst,i));" +
				"return ret; end;");
		App.debug(mpreduce1.evaluate("procedure logof(a);"
				+" if (arglength(a)>-1) and (part(a,0)='minus )then pi*i*logof(part(a,1)) else "
				+" if (arglength(a)>-1) and (part(a,0)='expt )then logof(part(a,1))*part(a,2) else "
		+" if (arglength(a)>-1) and (part(a,0)='times)then for k:=1:arglength(a) sum logof(part(a,k)) else "
		+" if (arglength(a)>-1) and (part(a,0)='quotient )then logof(part(a,1))-logof(part(a,2)) else log(a);"));
		//exptolin({7^(2*x-5)* 5^x = 9^(x+1)})
		App.debug(mpreduce1.evaluate("procedure exptolin(eqn);" +
				" if arglength(eqn)>-1 and part(eqn,0)='quotient and numberp(part(eqn,2)) then  exptolin(part(eqn,1)) else " +
			    " if arglength(eqn)>-1 and part(eqn,0)='plus then (logof(part(eqn,1))-logof(-part(eqn,2))) " +
			    " else eqn;"
				 ));
		mpreduce1
				.evaluate("procedure mysolve(eqn, var);"
						+ " begin scalar solutions!!, bool!!, isineq;" +
						"isineq:=0;"
						+ " if part(eqn,0)='sgreater then <<eqn:=part(eqn,0):='greaterp; isineq:=1>>;"
						+ " if part(eqn,0)='sgreaterequal then <<eqn:=part(eqn,0):='geq; isineq:=1>>;"
						+ " if part(eqn,0)='sless then <<eqn:=part(eqn,0):='lessp; isineq:=1>>;"
						+ " if part(eqn,0)='slessequal then <<eqn:=part(eqn,0):='leq; isineq:=1>>;"
						+ "  eqn:=mkdepthone({eqn});"
						+ "  let solverules;"
						+ "  if arglength(eqn)>-1 and part(eqn,0)='list then"
						+ "    eqn:=for each x in eqn collect"
						+ "      if freeof(x,=) then x else subtraction(lhs(x),rhs(x))"
						+ "  else if freeof(eqn,=) then 1 else eqn:=subtraction(lhs(eqn),rhs(eqn));"
						+ "  solutions!!:=solve(eqn,var);" 
						+ "  if solutions!!=list() or (arglength(solutions!!)>-1 and not freeof(solutions!!,'root_of)) then"
						+ "    solutions!!:=solve(map(exptolin(~r),eqn),var); "
						+ "  if not(arglength(solutions!!)>-1 and part(solutions!!,0)='list) then solutions!!:={solutions!!};"
						+ "	 if depth(solutions!!)<2 then"
						+ "		solutions!!:=for each x in solutions!! collect {x};"
						+ "	 solutions!!:=for each sol in solutions!! join <<"
						+ "    bool!!:=1;"
						+ "    for each solution!! in sol do"
						+ "     if freeof(solution!!,'root_of) and freeof(solution!!,'one_of) then <<"
						+ "		   on rounded, roundall, numval, complex;"
						+ "		   if freeof(solution!!,'i) or aeval(impart(rhs(solution!!)))=0 then 1 else bool!!:=0;"
						+ "		   off complex;"
						+ "		   if numeric!!=0 then off rounded, roundall, numval"
						+ "      >>"
						+ "      else"
						+ "	       bool!!:=2*bool!!;"
						+ " 	   firstsol!!:=part(sol,1);"
						+ "     if arglength(part(firstsol!!,2))>-1 and part(part(firstsol!!,2),0)=!*interval!* then {{mkinterval(var,part(eqn,1),part(part(firstsol!!,2),1),part(part(firstsol!!,2),2))}}"
						+ "    else if bool!!=1 then" + "  	 {sol}"
						+ "	   else if bool!!>1 then " + "  	 {{var='?}}"
						+ "    else " + "		 {} >>;"
						+ "  clearrules solverules;"
						+ "  return if isineq then listtodisjunction(var,flattenlist(mkset(solutions!!))) else mkset(solutions!!);" + " end;");
		
		mpreduce1
		.evaluate("procedure mysolve1(eqn);"
				+ " mysolve(eqn,mymainvars(eqn,length(mkdepthone({eqn}))));");
		mpreduce1
				.evaluate("procedure mycsolve(eqn, var);"
						+ " begin scalar solutions!!, bool!!;"
						+ "  eqn:=mkdepthone({eqn});"
						+ "  let solverules;"
						+ "  if arglength(eqn)>-1 and part(eqn,0)='list then"
						+ "    eqn:=for each x in eqn collect"
						+ "      if freeof(x,=) then x else subtraction(lhs(x),rhs(x))"
						+ "  else if freeof(eqn,=) then 1 else eqn:=subtraction(lhs(eqn),rhs(eqn));"
						+ "    solutions!!:=solve(eqn,var);"
						+ "    if depth(solutions!!)<2 then"
						+ "      solutions!!:=for each x in solutions!! collect {x};"
						+ "    solutions!!:= for each sol in solutions!! join <<"
						+ "      bool!!:=1;"
						+ "      for each solution!! in sol do"
						+ "        if freeof(solution!!,'root_of) and freeof(solution!!,'one_of) then 1 else"
						+ "      		bool!!:=0;" + "      if bool!!=1 then"
						+ "        {sol}" + "      else if bool!!=0 then"
						+ "        {{var='?}}" + "      >>;"
						+ "  clearrules solverules;"
						+ "  return mkset(solutions!!);" + " end;");
		
		mpreduce1
		.evaluate("procedure mycsolve1(eqn);"
				+ " mycsolve(eqn,mymainvars(eqn,length(mkdepthone({eqn}))));");


		mpreduce1
				.evaluate("procedure mydot(vec1,vec2); "
						+ "	begin scalar tmplength; "
						+ "  if myvecp(vec1) and myvecp(vec2) then"
						+ "    return dot(vec1,vec2);"
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
						+ "			sum vec1(1,i)*vec2(1,i) " + "      >> "
						+ "      else " + "		'? " + "    >> " + "  else "
						+ "    '? " + "  >> " + "end;");

		mpreduce1
				.evaluate("procedure mycross(atmp,btmp); "
						+ "begin;"
						+ "  if myvecp(atmp) then"
						+ "    if myvecp(btmp) then"
						+ "      return cross(atmp,btmp)"
						+ "    else"
						+ "      return cross(atmp, listtomyvect btmp)"
						+ "  else if myvecp(btmp) then"
						+ "  return cross(listtomyvect atmp,btmp);"
						+ "  a:=atmp; b:= btmp;"
						+ "  if arglength(a)=-1 or (length(a) neq 3 and length(a) neq 2 and length(a) neq {1,3} and length(a) neq {3,1} and length(a) neq {1,2} and length(a) neq {2,1}) then return '?;"
						+ "  if arglength(b)=-1 or (length(b) neq 3 and length(b) neq 2 and length(b) neq {1,3} and length(b) neq {3,1} and length(b) neq {1,2} and length(b) neq {2,1}) then return '?;"
						+ "  if length(a)={1,3} or length(b)={1,2} then a:=tp(a);"
						+ "  if length(b)={1,3} or length(b)={1,2} then b:=tp(b);"
						+ "  return"
						+ "  if arglength(a)>-1 and part(a,0)='mat then <<"
						+ "    if arglength(b)>-1 and part(b,0)='mat then <<"
						+ "      if length(a)={3,1} and length(b)={3,1} then"
						+ "        mat((a(2,1)*b(3,1)-a(3,1)*b(2,1)),"
						+ "        (a(3,1)*b(1,1)-a(1,1)*b(3,1)),"
						+ "        (a(1,1)*b(2,1)-a(2,1)*b(1,1)))"
						+ "      else if length(a)={2,1} and length(b)={2,1} then"
						+ "        mat((0),"
						+ "        (0),"
						+ "        (a(1,1)*b(2,1)-a(2,1)*b(1,1)))"
						+ "      else '?"
						+ "    >> else if arglength(b)>-1 and part(b,0)='list then <<"
						+ "      if length(a)={3,1} and length(b)=3 then"
						+ "        list(a(2,1)*part(b,3)-a(3,1)*part(b,2),"
						+ "        a(3,1)*part(b,1)-a(1,1)*part(b,3),"
						+ "        a(1,1)*part(b,2)-a(2,1)*part(b,1))"
						+ "      else if length(a)={2,1} and length(b)=2 then"
						+ "        list(0,"
						+ "        0,"
						+ "        a(1,1)*part(b,2)-a(2,1)*part(b,1))"
						+ "      else '?"
						+ "    >> else << '? >>"
						+ "  >> else if arglength(a)>-1 and part(a,0)='list then <<"
						+ "    if arglength(b)>-1 and part(b,0)='mat then <<"
						+ "      if length(a)=3 and length(b)={3,1} then"
						+ "        list(part(a,2)*b(3,1)-part(a,3)*b(2,1),"
						+ "        part(a,3)*b(1,1)-part(a,1)*b(3,1),"
						+ "        part(a,1)*b(2,1)-part(a,2)*b(1,1))"
						+ "      else if length(a)=2 and length(b)={2,1} then"
						+ "        list(0,"
						+ "        0,"
						+ "        part(a,1)*b(2,1)-part(a,2)*b(1,1))"
						+ "      else '?"
						+ "    >> else if arglength(b)>-1 and part(b,0)='list then <<"
						+ "      if length(a)=3 and length(b)=3 then"
						+ "        list(part(a,2)*part(b,3)-part(a,3)*part(b,2),"
						+ "        part(a,3)*part(b,1)-part(a,1)*part(b,3),"
						+ "        part(a,1)*part(b,2)-part(a,2)*part(b,1))"
						+ "      else if length(a)=2 and length(b)=2 then"
						+ "        list(0," + "        0,"
						+ "        part(a,1)*part(b,2)-part(a,2)*part(b,1))"
						+ "      else '?" + "    >> else << '? >>"
						+ "  >> else << '? >> " + "end;");

		mpreduce1.evaluate("procedure mattoscalar(m);"
				+ " if length(m)={1,1} then trace(m) else m;");

		mpreduce1
				.evaluate("procedure multiplication(a,b);"
						+ "begin"
						+ "  a:=booltonum(a);"
						+ "  b:=booltonum(b);"
						+ "  return if arglength(a)>-1 and part(a,0)='mat then"
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
						+ "      for i:=1:length(a) collect part(a,i)*part(b,i)"
						+ "    else if myvecp(b) then"
						+ "		 listtomyvect(a)*b"
						+ "	   else"
						+ "      map(~w!!*b,a)"
						+ "  else if myvecp(a) and arglength(b)>-1 and part(b,0)='list then"
						+ "    a*listtomyvect(b)"
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
						+ "		   else '?" + "		 else" + "        a*b;end");

		mpreduce1
				.evaluate("procedure applyfunction(a,b);"
						+ "if(arglength(b)<0) then a(b) else "
						+ "if(part(b,0)='mat) then applyfunction(a,mattolistoflists(b))"
						+ "else if (part(b,0)='list) then for i:=1:length(b) "
						+ "collect applyfunction(a,part(b,i))" + "else a(b)");
		mpreduce1
				.evaluate("procedure applyfunction2(a,b,p);"
						+ "if(arglength(b)<0) then a(b,p) else "
						+ "if(part(b,0)='mat) then applyfunction2(a,mattolistoflists(b),p)"
						+ "else if (part(b,0)='list) then for i:=1:length(b) "
						+ "collect applyfunction2(a,part(b,i),p)"
						+ "else a(b,p)");

		mpreduce1.evaluate("operator multiplication;");
		mpreduce1
				.evaluate("procedure mydivision(a,b); multiplication(a,1/booltonum(b))");
		mpreduce1.evaluate("operator mydivision;");
		mpreduce1
				.evaluate("procedure mypower(a,b); if myvecp(a) then if b=2 then multiplication(a,a) else '? else a^b;");
		mpreduce1.evaluate("operator mypower;");

		mpreduce1.evaluate("operator listtomyvect;");

		// PointList[{{x=2,y=3},{x=2,y=3,z=4}}]

		App.debug(mpreduce1
				.evaluate("procedure pointlist(lista);"
						+ "for each a in lista collect "
						+ "  if arglength(a)>-1 and part(a,0)='equal then listtomyvect({rhs(a),0})"
						+ "  else if arglength(a)=-1 or (not (part(a,0)='list)) then listtomyvect({a,0})"
						+ "  else if (arglength(part(a,1))>-1) and (part(part(a,1),0)='equal) then "
						+ "    <<begin tmp!!:=map(rhs,a); return listtomyvect(tmp!!); end>>" 
						+ " else listtomyvect(a); "));
		mpreduce1
				.evaluate("procedure rootlist(lista);"
						+ "for each a in lista collect if (arglength(a)>-1 and part(a,0)='equal) then "
						+ " listtomyvect({rhs(a),0}) else listtomyvect({a,0});");
		mpreduce1.evaluate("operator objecttomyvect;");

		mpreduce1
				.evaluate("procedure addition(a,b);"
						+ "begin"
						+ "  a:=booltonum(a);"
						+ "  b:=booltonum(b);"
						+ "  return if arglength(a)>-1 and part(a,0)='list and arglength(b)>-1 and part(b,0)='list then"
						+ "    for i:=1:length(a) collect addition(part(a,i),part(b,i))"
						+ "  else if arglength(a)>-1 and part(a,0)='list then"
						+ "    if myvecp(b) and length(a)>1 and not myvecp(part(a,1)) then"
						+ "      listtomyvect(a)+b"
						+ "    else"
						+ "      map(addition(~w!!,b),a)"
						+ "  else if arglength(b)>-1 and part(b,0)='list then"
						+ "    if myvecp(a)  and length(b)>1 and not myvecp(part(b,1)) then"
						+ "      listtomyvect(b)+a"
						+ "    else"
						+ "      map(addition(a,~w!!),b)"
						+ "else if (arglength(a)>-1 and part(a,0)='mat) or (arglength(b)>-1 and part(b,0)='mat) then"
						+ "   listofliststomat(addition(mattolistoflists(a),mattolistoflists(b)))"
						+ "  else if (a=infinity and b neq -infinity) or (b=infinity and a neq -infinity) then"
						+ "    infinity"
						+ "  else if (a=-infinity and b neq infinity) or (b=-infinity and a neq infinity) then"
						+ "    -infinity" + "  else" + "    a+b; end");

		mpreduce1.evaluate("operator addition;");

		mpreduce1
				.evaluate("procedure subtraction(a,b);"
						+ "begin"
						+ "  a:=booltonum(a);"
						+ "  b:=booltonum(b);"
						+ "  return if arglength(a)>-1 and part(a,0)='list and arglength(b)>-1 and part(b,0)='list then"
						+ "    for i:=1:length(a) collect part(a,i)-part(b,i)"
						+ "  else if arglength(a)>-1 and part(a,0)='list then"
						+ "    if myvecp b  and length(a)>1 and not myvecp(part(a,1)) then"
						+ "      listtomyvect(a)-b"
						+ "    else"
						+ "      map(~w!!-b,a)"
						+ "  else if arglength(b)>-1 and part(b,0)='list then"
						+ "    if myvecp(a)  and length(b)>1 and not myvecp(part(b,1)) then"
						+ "      a-listtomyvect(b)"
						+ "    else"
						+ "      map(a-~w!!,b)"
						+ "  else if (a=infinity and b neq infinity) or (b=-infinity and a neq -infinity) then "
						+ "    infinity"
						+ "  else if (a=-infinity and b neq -infinity) or (b=infinity and a neq infinity) then "
						+ "    -infinity" + "  else" + "    a-b;end");

		mpreduce1.evaluate("operator subtraction;");

		mpreduce1
				.evaluate("procedure myequal(a,b);begin; scalar ret;"
						+ "ret:=false;"
						+ "a:=mattolistoflists(a);"
						+ "b:=mattolistoflists(b);"
						+ "if myvecp(a) and myvecp(b) then <<ret:= myand(myequal(xvcoord(a),xvcoord(b)),myequal(yvcoord(a),yvcoord(b)));"
						+ " if(dim a = 3) then ret:=myand(ret,myequal(zvcoord(a),zvcoord(b)))>>"
						+ " else if arglength(a)>-1 and arglength(b)>-1 and part(a,0)='list and part(b,0)='list"
						+ " then <<if length(a)=length(b) then ret:=equallists(a,b) else ret:=false>>"
						+ " else ret:=if subtraction(a,b)=0 then true else false;"
						+ " return ret; end;");
		mpreduce1.evaluate("procedure equallists(a,b);begin;scalar ret,k; "
				+ "ret:=true;" + "k:=1;"
				+ "while k <= length(a) and ret=true do<<"
				+ " ret:=myequal(part(a,k),part(b,k));k:=k+1>>;"
				+ " return ret; end;");

		mpreduce1
				.evaluate("procedure myneq(a,b);if myequal(a,b)=true then false "
						+ "else true");

		mpreduce1
				.evaluate("procedure fractionalpart(a);if (a)>0 then a-floor(a) else a-ceiling(a)");
		mpreduce1
				.evaluate("procedure myreal(a);if myvecp(a) then xvcoord(a) else repart(a)");
		mpreduce1
				.evaluate("procedure imaginary(a);if myvecp(a) then yvcoord(a) else impart(a)");
		// erf in Reduce is currently broken:
		// http://sourceforge.net/projects/reduce-algebra/forums/forum/899364/topic/4546339
		// this is a numeric approximation according to Abramowitz & Stegun
		// 7.1.26.
		mpreduce1
				.evaluate("procedure myerf(x); "
						+ "begin scalar a1!!, a2!!, a3!!, a4!!, a5!!, p!!, x!!, t!!, y!!, sign!!, result!!;"
						+ "     on rounded;"
						+ "  x:=booltonum(x);"
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
						+ "     return result!! " + "end;");

		mpreduce1.evaluate("procedure mkdepthone(liste);"
				+ "	for each x in liste join "
				+ "	if arglength(x)>-1 and part(x,0)='list then"
				+ "	mkdepthone(x) else {x};");

		mpreduce1.evaluate("procedure listtocolumnvector(list); "
				+ "begin scalar lengthoflist; "
				+ "lengthoflist:=length(list); "
				+ "matrix m!!(lengthoflist,1); " + "for i:=1:lengthoflist do "
				+ "m!!(i,1):=part(list,i); " + "return m!! " + "end;");

		mpreduce1.evaluate("procedure listtorowvector(list); "
				+ "begin scalar lengthoflist; "
				+ "	lengthoflist:=length(list); "
				+ "	matrix m!!(1,lengthoflist); "
				+ "	for i:=1:lengthoflist do " + "		m!!(1,i):=part(list,i); "
				+ "	return m!!; " + "end;");

		mpreduce1.evaluate("procedure mod!!(a,b);" + " a-b*div(a,b);");

		mpreduce1.evaluate("procedure div(a,b);"
				+ " begin scalar a!!, b!!, result!!;" + "  a!!:=a; b!!:=b;"
				+ "  on rounded, roundall, numval;" + "  return "
				+ "  if numberp(a!!) and numberp(b!!) then <<"
				+ "    if numeric!!=0 then"
				+ "      off rounded, roundall, numval;" + "    if b!!>0 then "
				+ "	   floor(a/b)" + "    else" + "      ceiling(a/b)"
				+ "  >> else << " + "    if numeric!!=0 then"
				+ "      off rounded, roundall, numval;" + "    on rational;"
				+ "    result!!:=part(divide(a,b),1);" + "    off rational;"
				+ "    if numeric!!=1 then on rounded, roundall, numval;"
				+ "    result!!>>" + " end;");

		// to avoid using the package assist
		mpreduce1.evaluate("procedure mkset a;" + " begin scalar result, bool;"
				+ "  result:=list();" + "  for each elem in a do <<"
				+ "  bool:=1;" + "  for each x in result do"
				+ "    if elem=x then bool:=0;" + "  if bool=1 then"
				+ "    result:=elem . result;" + "  >>;"
				+ "  return reverse(result)" + " end;");

		mpreduce1.evaluate("procedure shuffle a;"
				+ "begin scalar lengtha,s,tmp;" + " lengtha:=length(a);"
				+ " if lengtha>1 then"
				+ "  for i:=lengtha step -1 until 1 do <<"
				+ "   s:=random(i)+1;" + "   tmp:= part(a,i);"
				+ "   a:=(part(a,i):=part(a,s));" + "   a:=(part(a,s):=tmp);"
				+ "  >>;" + " return a " + "end;");

		mpreduce1
				.evaluate("procedure listofliststomat(a); "
						+ " begin scalar length!!, bool!!, i!!, elem!!;"
						+ "  return"
						+ "  if arglength(a)>-1 and part(a,0)='list then <<"
						+ "    length!!:=-1;"
						+ "    bool!!:=1;"
						+ "    i!!:=0;"
						+ "    while i!!<length(a) and bool!!=1 do <<"
						+ "      i!!:=i!!+1;"
						+ "      elem!!:=part(a,i!!);"
						+ "      if arglength(elem!!)<0 or part(elem!!,0) neq 'list or (length(elem!!) neq length!! and length!! neq -1) then"
						+ "        bool!!:=0"
						+ "      else <<"
						+ "        length!!:=length(elem!!);"
						+ "        if 0=(for i:=1:length(elem!!) product if freeof(elem!!,=) then 1 else 0) then"
						+ "          bool!!:=0;" + "      >>" + "    >>;"
						+ "    if bool!!=0 or length(a)=0 then a"
						+ "    else <<"
						+ "      matrix matrix!!(length(a),length(part(a,1)));"
						+ "      for i:=1:length(a) do"
						+ "        for j!!:=1:length(part(a,1)) do"
						+ "          matrix!!(i,j!!):=part(part(a,i),j!!);"
						+ "      matrix!!>>" + "    >>" + " else" + "    a;"
						+ " end;");

		mpreduce1.evaluate("procedure mattolistoflists(a);"
				+ " begin scalar list!!, j!!;" + "  tmpmatrix!!:=a;"
				+ "  return" + "  if arglength(a)<0 or part(a,0) neq 'mat then"
				+ "    tmpmatrix!!" + "  else"
				+ "    for i:=1:part(length(a),1) collect"
				+ "      for j!!:=1:part(length(a),2) collect"
				+ "        tmpmatrix!!(i,j!!)" + " end;");

		mpreduce1
				.evaluate("procedure mysort a;"
						+ "begin scalar leftlist, rightlist, eqlist;"
						+ " leftlist:=list();"
						+ " rightlist:=list();"
						+ " eqlist:=list();"
						+ " return"
						+ " if length(a)<2 then a"
						+ " else <<"
						+ "  for each elem in a do"
						+ "    if elem<part(a,1) then"
						+ "     leftlist:=elem . leftlist"
						+ "    else if elem=part(a,1) then"
						+ "     eqlist:=elem . eqlist"
						+ "    else"
						+ "     rightlist:=elem . rightlist;"
						+ "  if length(leftlist)=0 and length(rightlist)=0 then"
						+ "    eqlist"
						+ "  else if length(leftlist)=0 then"
						+ "    append(eqlist, mysort(rightlist))"
						+ "  else if length(rightlist)=0 then"
						+ "    append(mysort(leftlist), eqlist)"
						+ "  else"
						+ "    append(append(mysort(leftlist),eqlist),mysort(rightlist))"
						+ " >> " + "end;");

		mpreduce1.evaluate("procedure myint(exp, var, from, upto);"
						+ "begin scalar upper, lower;"
						+ "antiderivative:=int(exp, var);"
						+ "if upto=Infinity or upto=-Infinity then upper:=limit(antiderivative,var,upto) else upper:=sub(var=upto,antiderivative);"
						+ "if from=Infinity or from=-Infinity then lower:=limit(antiderivative,var,from) else lower:=sub(var=from,antiderivative);"
						+ "return if freeof(upper,'limit) and freeof(lower,'limit) then upper-lower else '?;"
						+ "end;");
		
		mpreduce1.evaluate("procedure myfirst(l, n);" +
				"for i:=1:n collect part(l,i);");
		
		mpreduce1.evaluate("procedure getkernels(a);" +
				"for each element in a join" +
				"  if arglength(element) = -1 or numberp(element) then" +
				"    if numberp(element) then" +
				"      list()" +
				"    else" +
				"      list(element)" +
				"  else" +
				"    getkernels(part(element,0):=list);");
		
		mpreduce1.evaluate("procedure mymainvars(a,n);" +
				"begin scalar variables!!;" +
				" variables!!:=gvars(getkernels(list(a)));" +
				" return" +
				" if length(variables!!)<n then <<" +
				"   write \"*** the expression \",a,\" has less than \",n,\" variables.\";" +
						"   mymainvaraux(variables!!)" +
						" >> else <<" +
						"   myfirst(variables!!,n)" +
						" >> end;");		
		
		mpreduce1.evaluate("procedure mymainvaraux a;"
				+ "if a=list() then currentx!! else first(a);");
		
		mpreduce1.evaluate("procedure mymainvar a;"
				+ "mymainvaraux(mymainvars(a,1));");
		
		
	}

	/**
	 * Integral[sin(pi*x)/(pi*x),0,Infinity] Initializes function which depend
	 * on the current kernel.
	 * 
	 * @param mpreduce1
	 *            MPReduceevaluator
	 * @throws Throwable
	 *             from evaluator if some of the initialization commands fails
	 */
	protected final synchronized void initDependentMyMPReduceFunctions(
			geogebra.common.cas.Evaluate mpreduce1) throws Throwable {

		if (CASmpreduce.mpreduce != mpreduce1) {
			initStaticMyMPReduceFunctions(mpreduce1);
		}
		CASmpreduce.mpreduce = mpreduce1;

		// user variable ordering
		String variableOrdering = "ggbcasvarx, ggbcasvary, ggbcasvarz, ggbcasvara, "
				+ "ggbcasvarb, ggbcasvarc, ggbcasvard, ggbcasvare, ggbcasvarf, "
				+ "ggbcasvarg, ggbcasvarh, ggbcasvari, ggbcasvarj, ggbcasvark, "
				+ "ggbcasvarl, ggbcasvarm, ggbcasvarn, ggbcasvaro, ggbcasvarp, "
				+ "ggbcasvarq, ggbcasvarr, ggbcasvars, ggbcasvart, ggbcasvaru, "
				+ "ggbcasvarv, ggbcasvarw";
		// make sure to use current kernel's variable prefix
		variableOrdering = variableOrdering.replace("ggbcasvar", casPrefix);
		if (varOrder.length() > 0 && variableOrdering.length() > 0) {
			varOrder.append(',');
		}
		varOrder.append(variableOrdering);
		mpreduce1.evaluate("varorder!!:= list(" + varOrder + ");");
		mpreduce1.evaluate("order varorder!!;");
		mpreduce1.evaluate("korder varorder!!;");

		// access functions for elements of a vector
		String xyzCoordFunctions = "procedure ggbcasvarx(a); first(a);"
				+ "procedure ggbcasvary(a); second(a);"
				+ "procedure ggbcasvarz(a); third(a);";
		// make sure to use current kernel's variable prefix
		xyzCoordFunctions = xyzCoordFunctions.replace("ggbcasvar", casPrefix);
		mpreduce1.evaluate(xyzCoordFunctions);

	}

	/**
	 * Returns the ordering number of a ggbtmpvar
	 * 
	 * @param ggbtmpvar
	 *            The ggbtmpvar of which the ordering number is needed
	 * @return The ordering number if the given ggbtmpvar
	 * @throws IllegalArgumentException
	 *             if the given {@link String} is not a valid ggbtmpvar
	 */
	public static int getVarOrderingNumber(String ggbtmpvar)
			throws IllegalArgumentException {
		String varOrderNoWhitespaces = varOrder.toString().replaceAll(" ", "");
		String[] vars = varOrderNoWhitespaces.split(",");
		for (int i = 0; i < vars.length; i++) {
			if (ggbtmpvar.equals(vars[i])) {
				return i;
			}
		}
		throw new IllegalArgumentException("The given argument \"" + ggbtmpvar
				+ "\" is not a valid ggbtmpvar.");
	}

	/**
	 * Timeout for CAS in milliseconds.
	 */
	private long timeoutMillis = 5000;

	/**
	 * @return CAS timeout in seconds
	 */
	protected long getTimeoutMilliseconds() {
		return timeoutMillis;
	}

	public void settingsChanged(AbstractSettings settings) {
		CASSettings s = (CASSettings) settings;
		timeoutMillis = s.getTimeoutMilliseconds();
	}

	public String translateAssignment(final String label, final String body) {
		// default implementation works for MPReduce and MathPiper
		return label + " := " + body;
	}

	/**
	 * This method is called when asynchronous CAS call is finished. It tells
	 * the calling algo to update itself and adds the result to cache if
	 * suitable.
	 * 
	 * @param exp
	 *            parsed CAS output
	 * @param result2
	 *            output as string (for cacheing)
	 * @param exception
	 *            exception which stopped the computation (null if there wasn't
	 *            one)
	 * @param c
	 *            command that called the CAS asynchronously
	 * @param input
	 *            input string (for cacheing)
	 */
	public void CASAsyncFinished(ValidExpression exp, String result2,
			Throwable exception, AsynchronousCommand c, String input) {
		String result = result2;
		// pass on exception
		if (exception != null) {
			c.handleException(exception, input.hashCode());
			return;
		}
		// check if keep input command was successful
		// e.g. for KeepInput[Substitute[...]]
		// otherwise return input
		if (exp.isKeepInputUsed() && ("?".equals(result))) {
			// return original input
			c.handleCASoutput(exp.toString(StringTemplate.maxPrecision),
					input.hashCode());
		}

		// success
		if (result2 != null) {
			// get names of escaped global variables right
			// e.g. "ggbcasvar1a" needs to be changed to "a"
			// e.g. "ggbtmpvara" needs to be changed to "a"
			result = exp.getKernel().removeCASVariablePrefix(result, " ");
		}

		c.handleCASoutput(result, input.hashCode());
		if (c.useCacheing())
			exp.getKernel().putToCasCache(input, result);
	}
}
