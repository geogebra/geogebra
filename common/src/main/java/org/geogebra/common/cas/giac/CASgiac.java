package org.geogebra.common.cas.giac;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

import org.geogebra.common.cas.CASparser;
import org.geogebra.common.cas.CasParserTools;
import org.geogebra.common.cas.GeoGebraCAS;
import org.geogebra.common.kernel.AsynchronousCommand;
import org.geogebra.common.kernel.CASException;
import org.geogebra.common.kernel.CASGenericInterface;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.AssignmentType;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.FunctionNVar;
import org.geogebra.common.kernel.arithmetic.MyArbitraryConstant;
import org.geogebra.common.kernel.arithmetic.Traversing.ArbconstReplacer;
import org.geogebra.common.kernel.arithmetic.Traversing.DiffReplacer;
import org.geogebra.common.kernel.arithmetic.Traversing.PowerRootReplacer;
import org.geogebra.common.kernel.arithmetic.Traversing.PrefixRemover;
import org.geogebra.common.kernel.arithmetic.ValidExpression;
import org.geogebra.common.kernel.geos.GeoCasCell;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.prover.polynomial.Polynomial;
import org.geogebra.common.kernel.prover.polynomial.Variable;
import org.geogebra.common.main.App;
import org.geogebra.common.main.settings.AbstractSettings;
import org.geogebra.common.main.settings.CASSettings;
import org.geogebra.common.plugin.Operation;
import org.geogebra.common.util.StringUtil;

import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;

/**
 * Platform (Java / GWT) independent part of giac CAS
 */
public abstract class CASgiac implements CASGenericInterface {
	/** parser tools */
	protected CasParserTools parserTools;

	protected static Random rand = new Random();

	/**
	 * string to put Giac into GeoGebra mode (not affected by 'restart')
	 * 
	 * caseval needed here for giac.js
	 */
	public final static String initString = "caseval(\"init geogebra\")";

	/**
	 * string to put Giac off GeoGebra mode
	 */
	public final static String closeString = "caseval(\"close geogebra\")";

	/**
	 * define extra functions needed in Giac
	 * 
	 * must be run (with restart) before each command (as assumptions can be set
	 * by previous commands)
	 */
	protected final static String specialFunctions = "restart;"
			+
			// "proba_epsilon:=0;"+
			// used for sorting output of Solve/Solutions/NSolve/NSolutions
			// sort() doesn't work for list of lists
			"ggbsort(x):=when(length(x)==0,{},when(type(x[0])==DOM_LIST,x,sort(x)));"
			+ "atan2(y,x):=arg(x+i*y);"
			+ "sech(x):=1/cosh(x);"
			+ "csch(x):=1/sinh(x);"
			// Giac now supports coth()
			// + "coth(x):=1/tanh(x);"
			+
			// Giac's fPart has problems, so use this
			// http://wiki.geogebra.org/en/FractionalPart_Function
			"fractionalPart(x):=sign(x)*(abs(x)-floor(abs(x)));"
			+

	// eg Integral[x ln(5x)^2]
	// DOESN'T WORK eg Integral[sec(x)] gives x
	// "regroupAndNormalIfShorter(x):=[[[ggbevalans:=regroup(normal(x))],[ggbevalans2:=regroup(x)]],when(length(\"\"+ggbevalans)<=length(\"\"+ggbevalans2),ggbevalans,ggbevalans2)][1];"

			// these both give 3
			// @size(point(1,2,3)[1]) gives 3
			// @size(point((-(5))+(ggbtmpvark),(-(5))+(ggbtmpvark))[1]) gives 3
			// so need to check subtype(x[1])==20 to distinguish 2D and 3D
			"is3dpoint(x):=when(size(x[1])==3 && subtype(x[1])==20,true,false);"
			+
			// xcoordsymb(A) converted back to x(A) in CommandDispatcherGiac
			"xcoord(a):=when(type(a)==DOM_IDENT,xcoordsymb(a),when(a[0]=='pnt',when(is3dpoint(a),a[1][0],real(a[1])),when(a[0]=='=',coeff(a[1]-a[2],x,1),a[0])));"
			// altsymb(P) converted back to alt(P) in CommandDispatcherGiac
			+ "ggbalt(x):=when(type(x)==DOM_IDENT,altsymb(x),"
			+ "when(x[0]=='pnt',when(is3dpoint(x),atan2(x[1][2],sqrt(x[1][0]^2+x[1][1]^2)),0),?));"

			+ "ycoord(a):=when(type(a)==DOM_IDENT,ycoordsymb(a),when(a[0]=='pnt',when(is3dpoint(a),a[1][1],im(a[1])),when(a[0]=='=',coeff(a[1]-a[2],y,1),a[1])));"
			+
			// make sure z((1,2)) = 0
			"zcoord(a):=when(type(a)==DOM_IDENT,zcoordsymb(a),when(a[0]=='pnt',when(is3dpoint(a),a[1][2],0),when(length(a)<3,0,when(a[0]=='=',coeff(a[1]-a[2],z,1),a[2]))));"
			+
			// unicode0176u passes unaltered through Giac
			// then gets decoded to degree sign in GeoGebra
			// needed for "return angle from inverse trig function"
			// see ExpressionNode.degFix()
			"degasin(x):=normal(asin(x)/pi*180)*unicode0176u;"
			+ "degacos(x):=normal(acos(x)/pi*180)*unicode0176u;"
			+ "degatan(x):=normal(atan(x)/pi*180)*unicode0176u;"
			+ "degatan2(y,x):=normal(arg(x+i*y)/pi*180)*unicode0176u;"
			+
			// subtype 27 is ggbvect[]
			"ggbabs(x):=when(x[0]=='pnt' || (type(x)==DOM_LIST && subtype(x)==27),l2norm(x),abs(x));"
			+ "ggb_is_zero(x):=when(x==0,true,when(x[0]=='=',lhs(x)==0&&rhs(x)==0,when(type(x)=='DOM_LIST',max(x)==min(x)&&min(x)==0,false)));";
	/**
	 * whether Giac has been set to GeoGebra mode yet
	 */
	protected boolean giacSetToGeoGebraMode;

	/** CAS parser */
	public CASparser casParser;

	private static int nrOfReplacedConst = 0;

	/**
	 * Creates new Giac CAS
	 * 
	 * @param casParser
	 *            parser
	 */
	public CASgiac(CASparser casParser) {
		this.casParser = casParser;
	}

	/**
	 * @param exp
	 *            Giac command
	 * @return value returned from CAS
	 */
	public abstract String evaluateCAS(String exp);

	final public String evaluateRaw(final String input) throws Throwable {

		String exp = input;

		App.debug("giac eval: " + exp);
		String result = evaluate(exp, getTimeoutMilliseconds());

		// FIXME: This check is too heuristic: in giac.js we can get results
		// starting with \"
		// and they are still correct (e.g. from eliminateFactorized).
		// TODO: Find a better way for checking, now we assume that \"[ start is
		// OK (or \"\").
		String rtrimmed = result.trim();
		if (rtrimmed.startsWith("\"")) {
			if (!rtrimmed.startsWith("\"[") && !"\"\"".equals(rtrimmed)) {
				// eg
				// "Index outside range : 5, vector size is 3, syntax compatibility mode xcas Error: Invalid dimension"
				// assume error
				App.debug("message from giac (assuming error) " + result);
				result = "?";
			} else { // this is a special string output (only for the prover at
						// the moment)
				result = result.substring(1, result.length() - 1); // removing
																	// \" from
																	// left and
																	// right
			}
		}

		App.debug("CASgiac.evaluateRaw: result: " + result);
		return result;
	}

	/**
	 * @param exp
	 *            expression string
	 * @param timeoutMilliseconds
	 *            timeout in milliseconds
	 * @return result in Giac syntax
	 * @throws Throwable
	 *             for CAS error
	 */
	protected abstract String evaluate(String exp, long timeoutMilliseconds)
			throws Throwable;

	final public synchronized String evaluateGeoGebraCAS(
			final ValidExpression inputExpression,
			MyArbitraryConstant arbconst, StringTemplate tpl, GeoCasCell cell, Kernel kernel)
			throws CASException {
		ValidExpression casInput = inputExpression;
		Command cmd = casInput.getTopLevelCommand();
		boolean keepInput = (cell != null && cell.isKeepInputUsed())
				|| (cmd != null && "KeepInput".equals(cmd.getName()));
		String plainResult = getPlainResult(casInput, kernel);

		if (keepInput) {
			// remove KeepInput[] command and take argument
			if (cmd != null && cmd.getName().equals("KeepInput")) {
				// use argument of KeepInput as casInput
				if (cmd.getArgumentNumber() > 0) {
					casInput = cmd.getArgument(0);
				}
			}
		}

		// convert result back into GeoGebra syntax
		if (casInput instanceof FunctionNVar) {
			// delayed function definition f(x)::= Derivative[x^2] should return
			// Derivative[x^2]
			if (cell != null && cell.getAssignmentType() == AssignmentType.DELAYED) {
				return casInput.toString(StringTemplate.numericNoLocal);
			}
			// function definition f(x) := x^2 should return x^2
			// f(x):=Derivative[x^2] should return 2x
			// do not return directly, must check keepinput
			/*
			 * plainResult = evaluateMPReduce(plainResult + "(" +
			 * ((FunctionNVar)
			 * casInput).getVarString(StringTemplate.casTemplate) + ")");
			 */
		}

		String result = plainResult;

		if (keepInput && (cell == null || !cell.isSubstitute())) {
			// assume keepinput was not treated in CAS
			return casParser.toGeoGebraString(casInput, tpl);
		}

		// standard case
		if (result.isEmpty()) {
			return null;
		}
		return toGeoGebraString(result, arbconst, tpl, kernel);

	}

	final public synchronized ExpressionValue evaluateToExpression(
			final ValidExpression inputExpression,
			MyArbitraryConstant arbconst, Kernel kernel) throws CASException {
		String result = getPlainResult(inputExpression, kernel);
		// standard case
		if ("".equals(result)) {
			return null;
		}
		return replaceRoots(casParser.parseGiac(result), arbconst, kernel);

	}

	private String getPlainResult(ValidExpression casInput, Kernel kernel) {
		// KeepInput[] command should set flag keepinput!!:=1
		// so that commands like Substitute can work accordingly
		Command cmd = casInput.getTopLevelCommand();

		if (cmd != null && "Delete".equals(cmd.getName())) {
			ExpressionValue toDelete = cmd.getArgument(0).unwrap();
			if(toDelete.isExpressionNode() && (((ExpressionNode)toDelete).getOperation() == Operation.FUNCTION
					|| ((ExpressionNode)toDelete).getOperation() == Operation.FUNCTION_NVAR)){
				toDelete = ((ExpressionNode)toDelete).getLeft();
			}
			String label = toDelete.toString(
					StringTemplate.defaultTemplate);
			GeoElement geo = kernel.lookupLabel(label);
			if (geo == null)
				geo = kernel.lookupCasCellLabel(label);
			if (geo != null) {
				geo.remove();
			}
			return "true";
		}

		// convert parsed input to Giac string
		String giacInput = casParser.translateToCAS(casInput,
				StringTemplate.giacTemplate, this);

		// App.error(casInput+"\n\n"+giacInput );

		/*
		 * // tell MPReduce whether it should use the keep input flag, // e.g.
		 * important for Substitute StringBuilder sb = new StringBuilder();
		 * 
		 * sb.append("<<resetsettings("); sb.append(keepInput ? 1 : 0);
		 * sb.append(","); sb.append(taylorToStd ? 1 : 0); sb.append(","); //
		 * sb.append(
		 * "$ numeric!!:=0$ precision 30$ print\\_precision 16$ on pri, rationalize  $ off complex, rounded, numval, factor, exp, allfac, div, combinelogs, expandlogs, revpri $ currentx!!:= "
		 * ); sb.append("ggbtmpvarx,ggbtmpvary);");
		 * 
		 * 
		 * sb.append(mpreduceInput); sb.append(">>");
		 */

		// evaluate in Giac
		String plainResult = evaluateCAS(giacInput);
		return plainResult;
	}

	/**
	 * Tries to parse a given Giac string and returns a String in GeoGebra
	 * syntax.
	 * 
	 * @param giacString
	 *            String in Giac syntax
	 * @param arbconst
	 *            arbitrary constant handler
	 * @param tpl
	 *            template that should be used for serialization. Should be
	 *            casCellTemplate for CAS and defaultTemplate for input bar
	 * @return String in Geogebra syntax.
	 * @throws CASException
	 *             Throws if the underlying CAS produces an error
	 */
	final public synchronized String toGeoGebraString(String giacString,
			MyArbitraryConstant arbconst, StringTemplate tpl, Kernel kernel)
			throws CASException {
		boolean ggbvect = giacString.startsWith("ggbvect");
		ExpressionValue ve = replaceRoots(casParser.parseGiac(giacString),
				arbconst, kernel);
		// replace rational exponents by roots or vice versa

		String geogebraString = casParser.toGeoGebraString(ve, tpl);
		if (ggbvect) {
			geogebraString = "ggbvect(" + geogebraString + ")";
		}
		return geogebraString;
	}

	private static ExpressionValue replaceRoots(ExpressionValue ve,
			MyArbitraryConstant arbconst, Kernel kernel) {
		if (ve != null) {
			boolean toRoot = kernel.getApplication().getSettings()
					.getCasSettings().getShowExpAsRoots();
			ve = ve.traverse(DiffReplacer.INSTANCE);
			ve.traverse(PowerRootReplacer.getReplacer(toRoot));
			if (arbconst != null) {
				arbconst.reset();
				ve.traverse(ArbconstReplacer.getReplacer(arbconst));
			}
			PrefixRemover pr = PrefixRemover.getCollector();
			ve.traverse(pr);
		}
		return ve;
	}

	/**
	 */
	public synchronized void reset() {
		// TODO
	}

	/**
	 * Timeout for CAS in milliseconds. This can be changed in the CAS options.
	 */
	protected long timeoutMillis = 5000;

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
	 *            output as string (for caching)
	 * @param exception
	 *            exception which stopped the computation (null if there wasn't
	 *            one)
	 * @param c
	 *            command that called the CAS asynchronously
	 * @param input
	 *            input string (for caching)
	 */
	public void CASAsyncFinished(ValidExpression exp, String result2,
			Throwable exception, AsynchronousCommand c, String input, GeoCasCell cell) {
		String result = result2;
		// pass on exception
		if (exception != null) {
			c.handleException(exception, input.hashCode());
			return;
		}
		// check if keep input command was successful
		// e.g. for KeepInput[Substitute[...]]
		// otherwise return input
		if (cell.isKeepInputUsed() && ("?".equals(result))) {
			// return original input
			c.handleCASoutput(exp.toString(StringTemplate.maxPrecision),
					input.hashCode());
		}

		// success
		if (result2 != null) {
			c.getKernel();
			// get names of escaped global variables right
			// e.g. "ggbcasvar1a" needs to be changed to "a"
			// e.g. "ggbtmpvara" needs to be changed to "a"
			result = Kernel.removeCASVariablePrefix(result, " ");
		}

		c.handleCASoutput(result, input.hashCode());
		if (c.useCacheing())
			c.getKernel().putToCasCache(input, result);
	}

	public void appendListStart(StringBuilder sbCASCommand) {
		sbCASCommand.append("[");
	}

	public void appendListEnd(StringBuilder sbCASCommand) {
		sbCASCommand.append("]");
	}

	public String createLocusEquationScript(String constructRestrictions,
			String vars, String varsToEliminate) {

		StringBuilder script = new StringBuilder();

		String eliminateCommand = "eliminate([" + constructRestrictions + "],["
				+ varsToEliminate + "])";

		return script
				.append("[")
				.append("[aa:=")
				.append(eliminateCommand)
				.append("],")
				.
				// Creating a matrix from the output to satisfy Sergio:
				append("[bb:=coeffs(aa[0],x)], [sx:=size(bb)], [sy:=size(coeffs(aa[0],y))],")
				.append("[cc:=[sx,sy]], [for ii from sx-1 to 0 by -1 do dd:=coeff(bb[ii],y);")
				.append("sd:=size(dd); for jj from sd-1 to 0 by -1 do ee:=dd[jj];")
				.append("cc:=append(cc,ee); od; for kk from sd to sy-1 do ee:=0;")
				.append("cc:=append(cc,ee); od; od],cc][6]")
				// See CASTranslator.createSingularScript for more details.

				.toString();

	}

	public String createEliminateFactorizedScript(String polys, String elimVars) {
		/*
		 * Some examples to understand the code below:
		 * 
		 * [[aa:=eliminate([-1*v1,-1*v11*v10+v12*v9+v11*v8+-1*v9*v8+-1*v12*v7+v10
		 * *v7,v13*v8+-1*v14*v7,-1*v13+v13*v10+v9+-1*v14*v9,
		 * -1*v15*v10+v16*v9+v15
		 * *v2+-1*v9*v2+-1*v16*v1+v10*v1,v15*v12+-1*v16*v11,
		 * v17+-1*v17*v12+-1*v11+v18*v11,
		 * -1*v17*v8+v18*v7+v17*v2+-1*v7*v2+-1*v18
		 * *v1+v8*v1,-1+-1*v19*v17*v16+v19
		 * *v18*v15+v19*v17*v14+-1*v19*v15*v14+-1*v19*v18*v13+v19*v16*v13],
		 * [v17,v16,v19,v1,v18,v8,v13,v14,v15])],[bb:=size(aa)],[for ii from 0
		 * to bb-1 do cc[ii]:=factors(aa[ii]); od], cc][3]
		 * 
		 * table( 1 = [v2-1,1,v7,1,v12-1,1], 2 = [v2,1,v9,1,v12,1], 3 =
		 * [v7,1,v10-1,1], 4 = [v12,1,v12-1,1,-1,1,v2,1,v10-1,1,v10-v2,1], 5 =
		 * [-v2+1,1,v7,1,v11,1], 6 = [v2,1,v9,1,v11,1], 7 =
		 * [-v11*v10+v11*v2+v12*v9,1], 0 = [v7,1,v9,1] )
		 * 
		 * But we need the same output as Singular does, so we use this code
		 * instead:
		 * 
		 * [[aa:=eliminate([-1*v1,-1*v11*v10+v12*v9+v11*v8+-1*v9*v8+-1*v12*v7+v10
		 * *v7,v13*v8+-1*v14*v7,-1*v13+v13*v10+v9+-1*v14*v9,
		 * -1*v15*v10+v16*v9+v15
		 * *v2+-1*v9*v2+-1*v16*v1+v10*v1,v15*v12+-1*v16*v11,
		 * v17+-1*v17*v12+-1*v11+v18*v11,
		 * -1*v17*v8+v18*v7+v17*v2+-1*v7*v2+-1*v18
		 * *v1+v8*v1,-1+-1*v19*v17*v16+v19
		 * *v18*v15+v19*v17*v14+-1*v19*v15*v14+-1*v19*v18*v13+v19*v16*v13],
		 * [v17,v16,v19,v2,v18,v8,v13,v14,v15])],[bb:=size(aa)],[for ii from 0
		 * to bb-1 do
		 * print("["+(ii+1)+"]:");print(" [1]:");print("  _[1]=1");cc:
		 * =factors(aa[ii]);dd:=size(cc); for jj from 0 to dd-1 by 2 do
		 * print("  _["+(jj/2+2)+"]="+(cc[jj])); od; print(" [2]:");
		 * print("  "+cc[1]);for kk from 1 to dd-1 by 2 do
		 * print("   ,"+cc[kk]);od;od],0][3]
		 * 
		 * which gives
		 * 
		 * [1]: [1]: _[1]=1 _[2]=v7 _[3]=v9 [2]: 1 ,1 ,1 [2]: [1]: _[1]=1
		 * _[2]=v7 _[3]=v10-1 [2]: 1 ,1 ,1 [3]: [1]: _[1]=1 _[2]=v9 _[3]=-1
		 * _[4]=v11*v10-v9*v12 [2]: 1 ,1 ,1 ,1 [4]: [1]: _[1]=1 _[2]=v1 [2]: 1
		 * ,1 0
		 * 
		 * in giac with CoCoA support on command line and runs forever in
		 * giac.js.
		 */
		StringBuilder script = new StringBuilder();

		/*
		 * return script.append("[[aa:=eliminate(["). append(polys).
		 * append("],["). append(elimVars). append(
		 * "])],[bb:=size(aa)],[for ii from 0 to bb-1 do print(\"[\"+(ii+1)+\"]:\");print(\" [1]:\");"
		 * ). append("print(\"  _[1]=1\");cc:=factors(aa[ii]);dd:=size(cc);").
		 * append(
		 * "for jj from 0 to dd-1 by 2 do print(\"  _[\"+(jj/2+2)+\"]=\"+(cc[jj])); od; print(\" [2]:\");"
		 * ). append(
		 * "print(\"  \"+cc[1]);for kk from 1 to dd-1 by 2 do print(\"   ,\"+cc[kk]);od;od],0][3]"
		 * )
		 * 
		 * .toString();
		 */

		String eliminateCommand = "eliminate([" + polys + "],revlist(["
				+ elimVars + "]))";

		return script
				.append("[" +
				"[ff:=\"\"],[aa:=")
				.append(eliminateCommand)
				.append("],")
				.append("[bb:=size(aa)],[for ii from 0 to bb-1 do ff+=(\"[\"+(ii+1)+\"]: [1]: ")
				.append(" _[1]=1\");cc:=factors(aa[ii]);dd:=size(cc);")
				.append("for jj from 0 to dd-1 by 2 do ff+=(\"  _[\"+(jj/2+2)+\"]=\"+cc[jj]); od; ff+=(\" [2]: ")
				.append("\"+cc[1]);for kk from 1 to dd-1 by 2 do ff+=(\",\"+cc[kk]);od;od],[if(ff==\"\") begin ff:=[0] end],ff][5]")

				.toString();

		// We return text from the CAS here.

	}

	public String createGroebnerSolvableScript(
			HashMap<Variable, Integer> substitutions, String polys,
			String freeVars, String dependantVars, boolean transcext) {
		/*
		 * Example syntax (from Groebner basis tester; but in GeoGebra v1, v2,
		 * ... are used for variables):
		 * 
		 * [[ii:=gbasis(subst([2*d1-b1-c1, 2*d2-b2-c2,2*e1-a1-c1,
		 * 2*e2-a2-c2,2*f1-a1-b1, 2*f2-a2-b2 ,
		 * (d1-o1)*(b1-c1)+(d2-o2)*(b2-c2),(e1-o1)*(c1-a1)+(e2-o2)*(c2-a2),
		 * s1*d2
		 * +a1*(s2-d2)-d1*s2-a2*(s1-d1),s1*e2+b1*(s2-e2)-e1*s2-b2*(s1-e1),(a1
		 * -m1)*(b1-c1)+(a2-m2)*(b2-c2),
		 * (b1-m1)*(c1-a1)+(b2-m2)*(c2-a2),z1*(b1*c2
		 * +a1*(b2-c2)-c1*b2-a2*(b1-c1))-1, z2
		 * *(s1*m2+o1*(s2-m2)-m1*s2-o2*(s1-m1
		 * ))-1],[d1=0,b1=3]),[a1,a2,b1,b2,c1,c2,d1,d2,e1,e2,f1,f2,o1,
		 * o2,s1,s2,m1,m2,z1,z2],revlex)],(degree(ii[0])!=0)||(ii[0]==0)][1]
		 * 
		 * In the last part we check if the Groebner basis is a constant neq 0,
		 * i.e. its degree is 0 but it is not 0. If yes, there is no solution.
		 * 
		 * The giac implementation does not handle the case for request for
		 * polynomial ring with coefficients from a transcendental extension. We
		 * silently use a polynomial ring instead.
		 */

		String idealVar = "ii";

		String ret = "[[" + idealVar + ":=gbasis(";

		if (substitutions != null) {
			ret += "subst(";
		}

		ret += "[" + polys + "]";

		if (substitutions != null) {
			String substParams = substitutionsString(substitutions);
			ret += ",[" + substParams + "])";
		}

		String vars = freeVars + Polynomial.addLeadingComma(dependantVars);

		// ret += ",[" + vars + "],revlex)],(degree(" +
		// idealVar + "[0])!=0)||(" + idealVar + "[0]==0)][2]";
		ret += ",[" + vars + "],revlex)],(" + idealVar + "[0]!=1)&&("
				+ idealVar + "[0]!=-1)][1]";

		return ret;
	}

	/**
	 * Converts substitutions to giac strings
	 * 
	 * @param subst
	 *            input as a HashMap
	 * @return the parameters for giac (e.g. "v1=0,v2=0,v3=0,v4=1")
	 * 
	 *         Taken from prover.Polynomial, one character difference. Maybe
	 *         commonize.
	 */
	static String substitutionsString(HashMap<Variable, Integer> subst) {
		String ret = "";
		Iterator<Variable> it = subst.keySet().iterator();
		while (it.hasNext()) {
			Variable v = it.next();
			ret += "," + v.toString() + "=" + subst.get(v);
		}
		if (ret.length() > 0)
			return ret.substring(1);
		return "";
	}

	public double[][] getBivarPolyCoefficients(String rawResult, GeoGebraCAS cas) {
		String numbers = rawResult.substring(1, rawResult.length() - 1);
		String[] flatData = numbers.split(",");
		int xLength = Integer.parseInt(flatData[0]);
		int yLength = Integer.parseInt(flatData[1]);
		double[][] result = new double[xLength][yLength];

		int counter = 2;
		for (int x = 0; x < xLength; x++) {
			for (int y = 0; y < yLength; y++) {
				result[x][y] = Double.parseDouble(flatData[counter]);
				App.debug("[LocusEqu] result[" + x + "," + y + "]="
						+ result[x][y]);
				++counter;
			}
		}

		return result;
	}

	// eg {(ggbtmpvarx>(-sqrt(110)/5)) && ((sqrt(110)/5)>ggbtmpvarx)}
	// eg {(ggbtmpvarx>=(-sqrt(110)/5)) && ((sqrt(110)/5)>=ggbtmpvarx)}
	// eg (ggbtmpvarx>3) && (4>ggbtmpvarx)
	// private final static RegExp inequality =
	// RegExp.compile("(.*)\\((ggbtmpvar[^,}\\(\\)]+)>(=*)(.+)\\) && \\((.+)>(=*)(ggbtmpvar[^,}\\(\\)]+)\\)(.*)");
	// works only for variables in form [A-Za-z]+
	public final static String expression = "(([^\\(\\)]|\\([^\\(\\)]+\\)|\\(([^\\(\\)]|\\([^\\(\\)]+\\))+\\))+)";
	public final static RegExp inequality = RegExp
.compile(
			"^(.*)\\(([A-Za-z]+)>(=*)" + expression + "\\) && \\("
 + expression
					+ ">(=*)([A-Za-z]+)\\)(.*)$", "");


	// eg 3.7 > ggbtmpvarx
	// eg (37/10) > ggbtmpvarx
	// eg 333 > ggbtmpvarx
	// eg (-33) > ggbtmpvarx
	// private final static RegExp inequalitySimple =
	// RegExp.compile("([-0-9.E/\\(\\)]+)>(=*)(ggbtmpvar.+)");
	// works only for variables in form [A-Za-z]+
	private final static RegExp inequalitySimple = RegExp
			.compile("^([-0-9.E/\\(\\)]+)>(=*)([A-Za-z]+)$");

	// eg {3, 3>ggbtmpvarx, x^2}
	// eg {3, 3>ggbtmpvarx}
	// eg {3>ggbtmpvarx, x^2}
	// eg {3>ggbtmpvarx}
	// works only for variables in form [A-Za-z]+ and if it's a simple number
	private final static RegExp inequalitySimpleInList = RegExp
			.compile("(.*)([,{])(\\(*)?([-0-9.E]+)(\\)*)?>(=*)([A-Za-z]+)([,}\\)])(.*)");

	// old version, causes problems with eg Solve[exp(x)<2]
	// private final static RegExp inequalitySimpleInList =
	// RegExp.compile("(.*)([,{\\(])([-0-9.E/\\(\\)]+)>(=*)([A-Za-z]+)([,}\\)])(.*)");

	/**
	 * convert x>3 && x<7 into 3<x<7 convert 3>x into x<3 convert {3>x} into
	 * {x<3} eg output from Solve[x (x-1)(x-2)(x-3)(x-4)(x-5) < 0]
	 * 
	 * Giac's normal command converts inequalities to > or >= so we don't need
	 * to check <, <=
	 * 
	 * @param exp
	 *            expression
	 * @return converted expression if changed
	 */
	protected String checkInequalityInterval(String exp) {

		String ret = exp;

		MatchResult matcher = inequalitySimple.exec(ret);

		// swap 3>x into x<3
		if (matcher != null && exp.startsWith(matcher.getGroup(1))) {
			// App.debug(matcher.getGroup(1));
			// App.debug(matcher.getGroup(2));
			// App.debug(matcher.getGroup(3));
			// App.debug(matcher.getGroup(4));
			ret = matcher.getGroup(3) + "<" + matcher.getGroup(2)
					+ matcher.getGroup(1);
			App.debug("giac output (with simple inequality converted): " + ret);
			return ret;
		}

		// swap 5 > x && x > 3 into 3<x<5
		while ((matcher = inequality.exec(ret)) != null &&
		// TODO: check not x<3 && x<4

				// check variable the same
				// ie not x>5 && y<4
				matcher.getGroup(2).equals(matcher.getGroup(11))) {

			ret = matcher.getGroup(1) + matcher.getGroup(4) + "<"
					+ matcher.getGroup(3) + matcher.getGroup(2) + "<"
					+ matcher.getGroup(10) + matcher.getGroup(7)
					+ matcher.getGroup(12);
		}

		// swap {3>x, 6>y} into {x<3, y<6}
		while ((matcher = inequalitySimpleInList.exec(ret)) != null) {

			// matcher.getGroup(6) is either "" or "="

			// App.debug("1 "+matcher.getGroup(1));
			// App.debug("2 "+matcher.getGroup(2));
			// App.debug("3XX"+matcher.getGroup(3)+"XX");
			// App.debug(""+matcher.getGroup(3).equals("undefined"));
			// App.debug("4 "+matcher.getGroup(4));
			// App.debug("5XX"+matcher.getGroup(5)+"XX");
			// App.debug("6 "+matcher.getGroup(6));
			// App.debug("7 "+matcher.getGroup(7));
			// App.debug("8 "+matcher.getGroup(8));
			// App.debug("9 "+matcher.getGroup(9));

			String g3 = matcher.getGroup(3);
			String g5 = matcher.getGroup(5);

			// GWT regex bug? eg Solve[(2exp(x)-4)/(exp(x)-1) > 1],
			// Solve[(x^2-x-2)/(-x^3+7x^2-14x+8)<2]
			// #4710 GWT 2.7 beta 1 & RC1 needs the +"" as well
			if ((g3 + "").equals("undefined")) {
				g3 = "";
			}
			if ((g5 + "").equals("undefined")) {
				g5 = "";
			}

			// App.debug("g3= "+g3);
			// App.debug("g5= "+g5);

			// eg "(" + "-2" + ")"
			String g345 = g3 + matcher.getGroup(4) + g5;
			String g7 = matcher.getGroup(7);

			ret = matcher.getGroup(1) + matcher.getGroup(2) + g7 + "<"
					+ matcher.getGroup(6) + g345 + matcher.getGroup(8)
					+ matcher.getGroup(9);
		}

		if (!exp.equals(ret)) {
			App.debug("giac output (with inequality converted): " + ret);
		}

		return ret;

	}

	/**
	 * various improvements and hack for Giac's output
	 * 
	 * @param s
	 *            output from Giac
	 * @return result that GeoGebra can parse
	 */
	protected String postProcess(String s) {

		if (s.indexOf("GIAC_ERROR:") > -1) {
			// GIAC_ERROR: canonical_form(3*ggbtmpvarx^4+ggbtmpvarx^2) Error:
			// Bad Argument Value
			App.debug("error from Giac: " + s);

			return "?";
		}

		String ret = s.trim();
		// output from ifactor can be wrapped to stop simplification
		// eg js giac output:-('3*5')

		int primeOpen = ret.indexOf('\'');
		while (primeOpen >= 0) {
			int primeClose = ret.indexOf('\'', primeOpen + 1);
			if (primeClose < 0) {
				break;
			}
			// ((a')') -- delete brackets
			if (primeClose == primeOpen + 2 && ret.charAt(primeOpen + 1) == ')') {
				int bracketOpen = ret.lastIndexOf('(', primeOpen);

				if (bracketOpen >= 0) {
					StringBuilder sb = new StringBuilder(ret);
					sb = sb.replace(primeOpen + 1, primeOpen + 2, "");
					sb = sb.replace(bracketOpen, bracketOpen + 1, "");
					ret = sb.toString();
				}
				// primeOpen = primeClose;
			} else {
				int check = StringUtil.checkBracketsBackward(ret.substring(
						primeOpen, primeClose));
				// -('3*5') will have check = -1
				if (check < 0) {
					StringBuilder sb = new StringBuilder(ret);
					sb = sb.replace(primeOpen, primeOpen + 1, "");
					sb = sb.replace(primeClose - 1, primeClose, "");
					ret = sb.toString();
					App.debug(ret);
					primeOpen = ret.indexOf('\'', primeClose);
				} else {
					primeOpen = primeClose;
				}
			}
		}
		
		// #5099 GIAC_ERROR: string missing
		if (ret.indexOf("Unable to solve differential equation") > 0) {
			return "?";
		}

		if (ret.indexOf("integrate(") > -1) {
			// eg Integral[sqrt(sin(x))]
			return "?";
		}

		if (ret.indexOf("c_") > -1) {
			nrOfReplacedConst += ret.length() / 3; // upper bound on number of
													// constants in result
			App.debug("replacing arbitrary constants in " + ret);
			ret = ret.replaceAll("c_([0-9]*)", "arbconst($1+"
					+ nrOfReplacedConst
					+ ")");
		}

		if (ret.indexOf("n_") > -1) {
			App.debug("replacing arbitrary integers in " + ret);
			ret = ret.replaceAll("n_([0-9]*)", "arbint($1)");
		}

		// convert Giac's scientific notation from e.g. 3.24e-4 to
		// 3.2E-4
		// not needed, Giac now outputs E
		// ret = parserTools.convertScientificFloatNotation(ret);

		ret = casParser.insertSpecialChars(ret); // undo special character
													// handling

		// don't do check for long strings eg 7^99999
		if (ret.length() < 200) {

			// convert x>3 && x<7 into 3<x<7
			ret = checkInequalityInterval(ret);
		}

		return ret;
	}

	final private static String EVALFA = "evalfa(";
	private StringBuilder expSB = new StringBuilder(EVALFA);

	/**
	 * evalfa makes sure rootof() converted to decimal eg
	 * @rootof({{-4,10,-440,2025},{1,0,10,-200,375}})
	 * 
	 * @param s
	 *            input
	 * @return "evalfa(" + s + ")"
	 */
	protected String wrapInevalfa(String s) {
		expSB.setLength(EVALFA.length());
		expSB.append(s);
		expSB.append(")");

		return expSB.toString();
	}

}
