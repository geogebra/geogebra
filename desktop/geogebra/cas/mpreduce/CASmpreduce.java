package geogebra.cas.mpreduce;

import geogebra.common.cas.CASException;
import geogebra.common.cas.CASparser;
import geogebra.common.cas.CasParserTools;
import geogebra.common.cas.Evaluate;
import geogebra.common.cas.mpreduce.AbstractCASmpreduce;
import geogebra.common.kernel.arithmetic.ExpressionNodeConstants;
import geogebra.common.kernel.arithmetic.ValidExpression;
import geogebra.common.kernel.cas.AsynchronousCommand;
import geogebra.common.main.AbstractApplication;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.mathpiper.mpreduce.Interpreter2;

public class CASmpreduce extends AbstractCASmpreduce {

	private final static String RB_GGB_TO_MPReduce = "/geogebra/cas/mpreduce/ggb2mpreduce";
	// using static CAS instance as a workaround for the MPReduce deadlock with
	// multiple application windows
	// see http://www.geogebra.org/trac/ticket/1415
	private static Interpreter2 mpreduce_static;
	private Interpreter2 mpreduce;

	public CASmpreduce(CASparser casParser, CasParserTools parserTools) {
		super(casParser);
		this.parserTools = parserTools;
		getMPReduce();
	}

	/**
	 * @return Static MPReduce interpreter shared by all CASmpreduce instances.
	 */
	public static synchronized Interpreter2 getStaticInterpreter() {
		if (mpreduce_static == null) {
			mpreduce_static = new Interpreter2();

			// the first command sent to mpreduce produces an error
			try {
				loadMyMPReduceFunctions(mpreduce_static);
			} catch (Throwable e) {
			}

			AbstractApplication.setCASVersionString(getVersionString(mpreduce_static));
		}

		return mpreduce_static;
	}

	/**
	 * @return MPReduce interpreter using static interpreter with local kernel
	 *         initialization.
	 */
	protected synchronized Evaluate getMPReduce() {
		if (mpreduce == null) {
			// create mpreduce as a private reference to mpreduce_static
			mpreduce = getStaticInterpreter();

			try {
				// make sure to call initMyMPReduceFunctions() for each
				// CASmpreduce instance
				// because it depends on the current kernel's ggbcasvar prefix,
				// see #1443
				initMyMPReduceFunctions((Evaluate)mpreduce);
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}

		return (Evaluate)mpreduce;
	}

	/**
	 * Evaluates an expression and returns the result as a string in MPReduce
	 * syntax, e.g. evaluateMathPiper("D(x) (x^2)") returns "2*x".
	 * 
	 * @param exp
	 *            expression (with command names already translated to MPReduce
	 *            syntax).
	 * @return result string (null possible)
	 * @throws CASException
	 */
	@Override
	public final String evaluateMPReduce(String exp) throws CASException {
		try {
			exp = casParser.replaceIndices(exp);
			String ret = evaluateRaw(exp);
			ret = casParser.insertSpecialChars(ret); // undo special character
														// handling

			// convert MPReduce's scientific notation from e.g. 3.24e-4 to
			// 3.2E-4
			ret = parserTools.convertScientificFloatNotation(ret);

			return ret;
		} catch (TimeoutException toe) {
			throw new geogebra.cas.error.TimeoutException(toe.getMessage());
		} catch (Throwable e) {
			System.err.println("evaluateMPReduce: " + e.getMessage());
			return "?";
		}
	}

	@Override
	public synchronized void reset() {
		if (mpreduce == null)
			return;

		super.reset();
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
			System.err
					.println("Failed to clear variable from MPReduce: " + var);
		}
	}

	private static synchronized void loadMyMPReduceFunctions(
			Interpreter2 mpreduce_static1) throws Throwable {
		mpreduce_static1.evaluate("load_package rsolve;");
		mpreduce_static1.evaluate("load_package numeric;");
		mpreduce_static1.evaluate("load_package specfn;");
		mpreduce_static1.evaluate("load_package odesolve;");
		mpreduce_static1.evaluate("load_package defint;");
		mpreduce_static1.evaluate("load_package linalg;");
		mpreduce_static1.evaluate("load_package reset;");
		mpreduce_static1.evaluate("load_package taylor;");
		mpreduce_static1.evaluate("load_package groebner;");
		mpreduce_static1.evaluate("load_package trigsimp;");
		mpreduce_static1.evaluate("load_package polydiv;");
		mpreduce_static1.evaluate("load_package myvector;");
		
		// Initialize MPReduce
				mpreduce_static1.evaluate("off nat;");
				mpreduce_static1.evaluate("off pri;");

				mpreduce_static1.evaluate("off numval;");
				mpreduce_static1.evaluate("linelength 50000;");
				mpreduce_static1.evaluate("scientific_notation {16,5};");
				mpreduce_static1.evaluate("on fullroots;");
				mpreduce_static1.evaluate("printprecision!!:=15;");

				mpreduce_static1.evaluate("intrules!!:={"
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

				mpreduce_static1.evaluate("let {" + "df(asin(~x),x) => 1/sqrt(1-x^2),"
						+ "df(acosh(~x),x) => 1/(sqrt(x-1)*sqrt(x+1)),"
						+ "df(asinh(~x),x) => 1/sqrt(1+x^2),"
						+ "df(acos(~x),x) => -1/sqrt(1-x^2)};");

				mpreduce_static1.evaluate("let {impart(arbint(~w)) => 0, arbint(~w)*i =>  0};");
				mpreduce_static1.evaluate("let {atan(sin(~x)/cos(~x))=>x, "
						+ "acos(1/sqrt(2)) => pi/4" + "};");

				mpreduce_static1.evaluate("solverules:={" + "logb(~x,~b)=>log(x)/log(b),"
						+ "log10(~x)=>log(x)/log(10)" + "};");

				mpreduce_static1.evaluate("procedure myatan2(y,x);"
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
						+ "   else '?" + " else" + "   '? end;");

				mpreduce_static1.evaluate("procedure mycoeff(p,x);"
						+ " begin scalar coefflist, bool!!;"
						+ " coefflist:=coeff(p,x);"
						+ " if 1=for each elem!! in coefflist product"
						+ "   if freeof(elem!!,x) then 1 else 0 then"
						+ "   return reverse(coefflist)" + " else" + "   return '?"
						+ " end;");

				mpreduce_static1.evaluate(" Degree := pi/180;");

				mpreduce_static1.evaluate("procedure myround(x);" + "floor(x+0.5);");

				mpreduce_static1.evaluate("procedure harmonic(n,m); for i:=1:n sum 1/(i**m);");
				mpreduce_static1.evaluate("procedure uigamma(n,m); gamma(n)-igamma(n,m);");
				mpreduce_static1.evaluate("procedure beta!Regularized(a,b,x); ibeta(a,b,x);");
				mpreduce_static1.evaluate("procedure myarg(x);"
						+ " if arglength(x)>-1 and part(x,0)='list then myatan2(part(x,2), part(x,1)) "
						+ " else if arglength(x)>-1 and part(x,0)='mat then <<"
						+ "   clear x!!;"
						+ "   x!!:=x;"
						+ "   if row_dim(x!!)=1 then myatan2(x!!(1,2),x!!(1,1))"
						+ "   else if column_dim(x!!)=1 then myatan2(x!!(2,1),x!!(2,1))"
						+ "   else arg(x!!) >>" + " else myatan2(impart(x),repart(x));");
				mpreduce_static1.evaluate("procedure polartocomplex(r,phi); r*(cos(phi)+i*sin(phi));");
				mpreduce_static1.evaluate("procedure polartopoint!\u00a7(r,phi); list(r*cos(phi),r*sin(phi));");
				mpreduce_static1.evaluate("procedure complexexponential(r,phi); r*(cos(phi)+i*sin(phi));");
				mpreduce_static1.evaluate("procedure conjugate(x); conj(x);");
				mpreduce_static1.evaluate("procedure myrandom(); <<on rounded; random(100000001)/(random(100000000)+1)>>;");
				mpreduce_static1.evaluate("procedure gamma!Regularized(a,x); igamma(a,x);");
				mpreduce_static1.evaluate("procedure gamma2(a,x); gamma(a)*igamma(a,x);");
				mpreduce_static1.evaluate("procedure beta3(a,b,x); beta(a,b)*ibeta(a,b,x);");
				mpreduce_static1.evaluate("symbolic procedure isbound!! x; if get(x, 'avalue) then 1 else 0;");
				mpreduce_static1.evaluate("procedure myabs(x);"
						+ " if arglength(x!!)>-1 and part(x,0)='list then sqrt(for each elem!! in x sum elem!!^2)"
						+ " else if arglength(x)>-1 and part(x,0)='mat then <<"
						+ "   clear x!!;"
						+ "   x!!:=x;"
						+ "   if row_dim(x!!)=1 then sqrt(for i:=1:column_dim(x!!) sum x!!(1,i)^2)"
						+ "   else if column_dim(x!!)=1 then sqrt(for i:=1:row_dim(x!!) sum x!!(i,1)^2)"
						+ "   else abs(x!!) >>" + " else if freeof(x,i) then abs(x)"
						+ " else sqrt(repart(x)^2+impart(x)^2);");

				mpreduce_static1.evaluate("procedure flattenlist a;"
						+ "if 1=for each elem!! in a product length(elem!!) then for each elem!! in a join elem!! else a;");

				mpreduce_static1.evaluate("procedure depth a; if arglength(a)>0 and part(a,0)='list then 1+depth(part(a,1)) else 0;");

				mpreduce_static1.evaluate("procedure mysolve(eqn, var);"
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
						+ "      >>" + "      else" + "	       bool!!:=2*bool!!;"
						+ "    if bool!!=1 then" + "  	 {sol}"
						+ "	   else if bool!!>1 then " + "  	 {{var='?}}" + "    else "
						+ "		 {} >>;" + "  clearrules solverules;"
						+ "  return mkset(solutions!!);" + " end;");

				mpreduce_static1.evaluate("procedure mycsolve(eqn, var);"
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
						+ "  clearrules solverules;" + "  return mkset(solutions!!);"
						+ " end;");

				mpreduce_static1.evaluate("procedure mysolve1(eqn);"
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
						+ "      >>" + "      else" + "	       bool!!:=2*bool!!;"
						+ "    if bool!!=1 then" + "  	 {sol}"
						+ "	   else if bool!!>1 then " + "  	 {{'?}}" + "    else "
						+ "		 {} >>;" + "  clearrules solverules;"
						+ "  return mkset(solutions!!);" + " end;");

				mpreduce_static1.evaluate("procedure mycsolve1(eqn);"
						+ " begin scalar solutions!!, bool!!;" + "  let solverules;"
						+ "  eqn:=mkdepthone({eqn});"
						+ "  if arglength(eqn)>-1 and part(eqn,0)='list then"
						+ "    eqn:=for each x in eqn collect"
						+ "      if freeof(x,=) then x else lhs(x)-rhs(x)"
						+ "  else if freeof(eqn,=) then 1 else eqn:=lhs(eqn)-rhs(eqn);"
						+ "    solutions!!:=solve(eqn);"
						+ "    if depth(solutions!!)<2 then"
						+ "      solutions!!:=for each x in solutions!! collect {x};"
						+ "    solutions!!:= for each sol in solutions!! join <<"
						+ "      bool!!:=1;" + "      for each solution!! in sol do"
						+ "        if freeof(solution!!,'root_of) then 1 else"
						+ "      		bool!!:=0;" + "      if bool!!=1 then"
						+ "        {sol}" + "      else if bool!!=0 then"
						+ "        {{var='?}}" + "      >>;"
						+ "  clearrules solverules;" + "  return mkset(solutions!!);"
						+ " end;");

				mpreduce_static1.evaluate("procedure mydot(vec1,vec2); "
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
						+ "      for i:=1:tmplength  " + "			sum vec1(1,i)*vec2(1,i) "
						+ "      >> " + "      else " + "		'? " + "    >> " + "  else "
						+ "    '? " + "  >> " + "end;");

				mpreduce_static1.evaluate("procedure mycross(atmp,btmp); "
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
						+ "        mat((0)," + "        (0),"
						+ "        (a(1,1)*b(2,1)-a(2,1)*b(1,1)))" + "      else '?"
						+ "    >> else if arglength(b)>-1 and part(b,0)='list then <<"
						+ "      if length(a)={3,1} and length(b)=3 then"
						+ "        list(a(2,1)*part(b,3)-a(3,1)*part(b,2),"
						+ "        a(3,1)*part(b,1)-a(1,1)*part(b,3),"
						+ "        a(1,1)*part(b,2)-a(2,1)*part(b,1))"
						+ "      else if length(a)={2,1} and length(b)=2 then"
						+ "        list(0," + "        0,"
						+ "        a(1,1)*part(b,2)-a(2,1)*part(b,1))"
						+ "      else '?" + "    >> else << '? >>"
						+ "  >> else if arglength(a)>-1 and part(a,0)='list then <<"
						+ "    if arglength(b)>-1 and part(b,0)='mat then <<"
						+ "      if length(a)=3 and length(b)={3,1} then"
						+ "        list(part(a,2)*b(3,1)-part(a,3)*b(2,1),"
						+ "        part(a,3)*b(1,1)-part(a,1)*b(3,1),"
						+ "        part(a,1)*b(2,1)-part(a,2)*b(1,1))"
						+ "      else if length(a)=2 and length(b)={2,1} then"
						+ "        list(0," + "        0,"
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

				mpreduce_static1.evaluate("procedure mattoscalar(m);"
						+ " if length(m)={1,1} then trace(m) else m;");

				mpreduce_static1.evaluate("procedure multiplication(a,b);"
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
						+ "      for i:=1:length(a) collect part(a,i)*part(b,i)"
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
						+ "		   else '?" + "		 else" + "        a*b;");

				mpreduce_static1.evaluate("operator multiplication;");

				mpreduce_static1.evaluate("procedure addition(a,b);"
						+ "  if arglength(a)>-1 and part(a,0)='list and arglength(b)>-1 and part(b,0)='list then"
						+ "    for i:=1:length(a) collect part(a,i)+part(b,i)"
						+ "  else if arglength(a)>-1 and part(a,0)='list then"
						+ "    map(~w!!+b,a)"
						+ "  else if arglength(b)>-1 and part(b,0)='list then"
						+ "    map(a+~w!!,b)"
						+ "  else if (a=infinity and b neq -infinity) or (b=infinity and a neq -infinity) then"
						+ "    infinity"
						+ "  else if (a=-infinity and b neq infinity) or (b=-infinity and a neq infinity) then"
						+ "    -infinity" + "  else" + "    a+b;");

				mpreduce_static1.evaluate("operator addition;");

				mpreduce_static1.evaluate("procedure subtraction(a,b);"
						+ "  if arglength(a)>-1 and part(a,0)='list and arglength(b)>-1 and part(b,0)='list then"
						+ "    for i:=1:length(a) collect part(a,i)-part(b,i)"
						+ "  else if arglength(a)<-1 and part(a,0)='list then"
						+ "    map(~w!!-b,a)"
						+ "  else if arglength(b)>-1 and part(b,0)='list then"
						+ "    map(a-~w!!,b)"
						+ "  else if (a=infinity and b neq infinity) or (b=-infinity and a neq -infinity) then "
						+ "    infinity"
						+ "  else if (a=-infinity and b neq -infinity) or (b=infinity and a neq infinity) then "
						+ "    -infinity" + "  else" + "    a-b;");

				mpreduce_static1.evaluate("operator subtraction;");

				// erf in Reduce is currently broken:
				// http://sourceforge.net/projects/reduce-algebra/forums/forum/899364/topic/4546339
				// this is a numeric approximation according to Abramowitz & Stegun
				// 7.1.26.
				mpreduce_static1.evaluate("procedure myerf(x); "
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
						+ "     return result!! " + "end;");

				mpreduce_static1.evaluate("procedure mkdepthone(liste);"
						+ "	for each x in liste join "
						+ "	if arglength(x)>-1 and part(x,0)='list then"
						+ "	mkdepthone(x) else {x};");

				mpreduce_static1.evaluate("procedure listtocolumnvector(list); "
						+ "begin scalar lengthoflist; "
						+ "lengthoflist:=length(list); "
						+ "matrix m!!(lengthoflist,1); " + "for i:=1:lengthoflist do "
						+ "m!!(i,1):=part(list,i); " + "return m!! " + "end;");

				mpreduce_static1.evaluate("procedure listtorowvector(list); "
						+ "begin scalar lengthoflist; "
						+ "	lengthoflist:=length(list); "
						+ "	matrix m!!(1,lengthoflist); "
						+ "	for i:=1:lengthoflist do " + "		m!!(1,i):=part(list,i); "
						+ "	return m!!; " + "end;");

				mpreduce_static1.evaluate("procedure mod!!(a,b);" + " a-b*div(a,b);");

				mpreduce_static1.evaluate("procedure div(a,b);"
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
				mpreduce_static1.evaluate("procedure mkset a;" + " begin scalar result, bool;"
						+ "  result:=list();" + "  for each elem in a do <<"
						+ "  bool:=1;" + "  for each x in result do"
						+ "    if elem=x then bool:=0;" + "  if bool=1 then"
						+ "    result:=elem . result;" + "  >>;"
						+ "  return reverse(result)" + " end;");

				mpreduce_static1.evaluate("procedure shuffle a;"
						+ "begin scalar lengtha,s,tmp;" + " lengtha:=length(a);"
						+ " if lengtha>1 then"
						+ "  for i:=lengtha step -1 until 1 do <<"
						+ "   s:=random(i)+1;" + "   tmp:= part(a,i);"
						+ "   a:=(part(a,i):=part(a,s));" + "   a:=(part(a,s):=tmp);"
						+ "  >>;" + " return a " + "end;");

				mpreduce_static1.evaluate("procedure listofliststomat(a); "
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
						+ "    if bool!!=0 or length(a)=0 then a" + "    else <<"
						+ "      matrix matrix!!(length(a),length(part(a,1)));"
						+ "      for i:=1:length(a) do"
						+ "        for j!!:=1:length(part(a,1)) do"
						+ "          matrix!!(i,j!!):=part(part(a,i),j!!);"
						+ "      matrix!!>>" + "    >>" + " else" + "    a;" + " end;");

				mpreduce_static1.evaluate("procedure mattolistoflists(a);"
						+ " begin scalar list!!, j!!;" + "  tmpmatrix!!:=a;"
						+ "  return" + "  if arglength(a)<0 or part(a,0) neq 'mat then"
						+ "    tmpmatrix" + "  else"
						+ "    for i:=1:part(length(a),1) collect"
						+ "      for j!!:=1:part(length(a),2) collect"
						+ "        tmpmatrix!!(i,j!!)" + " end;");

				mpreduce_static1.evaluate("procedure mysort a;"
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

				mpreduce_static1.evaluate("procedure getkernels(a);"
						+ "	for each element in a sum"
						+ "	  if arglength(element)=-1 then" + "	    element"
						+ "	  else" + "	    getkernels(part(element,0):=list);");

				mpreduce_static1.evaluate("procedure mymainvaraux a;"
						+ "if numberp(a) then currentx!! else a;");

				mpreduce_static1.evaluate("procedure mymainvar a;"
						+ "mainvar(mymainvaraux(getkernels(list(a))));");

				mpreduce_static1.evaluate("procedure myint(exp!!, var!!, from!!, to!!);"
						+ "begin scalar integrand!!;"
						+ "antiderivative!!:=int(exp!!, var!!);"
						+ "return sub(var!!=to!!,antiderivative!!)-sub(var!!=from!!,antiderivative!!)"
						+ "end;");
	}

	private static String getVersionString(Interpreter2 mpreduce) {
		Pattern p = Pattern.compile("version (\\S+)");
		Matcher m = p.matcher(mpreduce.getStartMessage());
		if (!m.find()) {
			return "MPReduce";
		}
		StringBuilder sb = new StringBuilder();
		sb.append("MPReduce ");
		sb.append(m.group(1));
		return sb.toString();

	}
	List<AsynchronousCommand> queue =new LinkedList<AsynchronousCommand>();
	
	private Thread casThread;
	@Override
	public void evaluateGeoGebraCASAsync(
			 final AsynchronousCommand cmd 
			) {
		AbstractApplication.debug("about to start some thread");
		if(!queue.contains(cmd))
			queue.add(cmd);
		
		if(casThread == null || !casThread.isAlive()){
		casThread = new Thread(){
			@Override
			public void run(){
				AbstractApplication.debug("thread is starting");
				while(queue.size()>0){
					AsynchronousCommand command = queue.get(0);
					String input = command.getCasInput();
					String result;
					ValidExpression inVE = null;
					//remove before evaluating to ensure we don't ignore new requests meanwhile
					if(queue.size()>0)
						queue.remove(0);					
					try{
						inVE = casParser.parseGeoGebraCASInput(input);
						result = evaluateGeoGebraCAS(inVE);
						CASAsyncFinished(inVE, result, null, command,  input);
					}catch(Throwable exception){
						AbstractApplication.debug("exception handling ...");
						exception.printStackTrace();
						result ="";
						CASAsyncFinished(inVE, result,exception, command, input);
					}
					
				}
				AbstractApplication.debug("thread is quiting");
			}
		};
		}
		if(AsynchronousCommand.USE_ASYNCHRONOUS  && !casThread.isAlive()){
			casThread.start();
		}else
			casThread.run();
		
	}

	public void initCAS() {
		// TODO Auto-generated method stub
		
	}
	
}
