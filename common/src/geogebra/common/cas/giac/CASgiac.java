package geogebra.common.cas.giac;

import geogebra.common.cas.CASparser;
import geogebra.common.cas.CasParserTools;
import geogebra.common.cas.GeoGebraCAS;
import geogebra.common.kernel.AsynchronousCommand;
import geogebra.common.kernel.CASException;
import geogebra.common.kernel.CASGenericInterface;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.arithmetic.AssignmentType;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.arithmetic.ExpressionValue;
import geogebra.common.kernel.arithmetic.FunctionNVar;
import geogebra.common.kernel.arithmetic.MyArbitraryConstant;
import geogebra.common.kernel.arithmetic.Traversing.ArbconstReplacer;
import geogebra.common.kernel.arithmetic.Traversing.PowerRootReplacer;
import geogebra.common.kernel.arithmetic.Traversing.PrefixRemover;
import geogebra.common.kernel.arithmetic.ValidExpression;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.App;
import geogebra.common.main.settings.AbstractSettings;
import geogebra.common.main.settings.CASSettings;

import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;

/**
 * Platform (Java / GWT) independent part of giac CAS
 */
public abstract class CASgiac implements CASGenericInterface {
	/** parser tools */
	protected CasParserTools parserTools;
	
	/**
	 * string to put Giac into GeoGebra mode
	 */
	protected final static String initString = "caseval(\"init geogebra\")";
	
	/**
	 * define extra functions needed in Giac
	 */
	protected final static String specialFunctions =
			"atan2(y,x):=arg(x+i*y);"+
			"sech(x):=1/cosh(x);"+
			"csch(x):=1/sinh(x);"+
			"coth(x):=1/tanh(x);" +
			// Giac's fPart has problems, so use this
			// http://wiki.geogebra.org/en/FractionalPart_Function
			"fractionalPart(x):=sign(x)*(abs(x)-floor(abs(x)));"+
			"xcoord(x):=x[0];"+
			"ycoord(x):=x[1];"+
			"zcoord(x):=x[2];"+
			// unicode0176u passes unaltered through Giac
			// then gets decoded to degree sign in GeoGebra
			// needed for "return angle from inverse trig function"
			// see ExpressionNode.degFix()
			"degasin(x):=normal(asin(x)/pi*180)*unicode0176u;"+
			"degacos(x):=normal(acos(x)/pi*180)*unicode0176u;"+
			"degatan(x):=normal(atan(x)/pi*180)*unicode0176u;"+
			"degatan2(y,x):=normal(arg(x+i*y)/pi*180)*unicode0176u;";

	/**
	 * whether extra functions needed in Giac have been initialized yet
	 */
	protected boolean specialFunctionsInitialized;
	
	/** CAS parser */
	public CASparser casParser;

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

					
		if (result.trim().startsWith("\"")) {
			// eg "Index outside range : 5, vector size is 3, syntax compatibility mode xcas Error: Invalid dimension"
			// assume error
			App.debug("message from giac (assuming error) "+result);
			result = "?";
		}
				
		App.debug("CASgiac.evaluateRaw: result: " + result);
		return result;
	}

	/**
	 * @param exp expression string
	 * @param timeoutMilliseconds timeout in milliseconds
	 * @return result in Giac syntax
	 * @throws Throwable for CAS error
	 */
	protected abstract String evaluate(String exp, long timeoutMilliseconds) throws Throwable;

	final public synchronized String evaluateGeoGebraCAS(
			final ValidExpression inputExpression, MyArbitraryConstant arbconst,
			StringTemplate tpl) throws CASException {
		ValidExpression casInput = inputExpression;
		Command cmd = casInput.getTopLevelCommand();
		boolean keepInput = casInput.isKeepInputUsed() || (cmd!=null && cmd.getName().equals("KeepInput"));
		String plainResult = getPlainResult(casInput);
			
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
			// delayed function definition f(x)::= Derivative[x^2] should return Derivative[x^2]
			if (casInput.getAssignmentType() == AssignmentType.DELAYED){
				return casInput.toString(StringTemplate.numericNoLocal);
			}
			// function definition f(x) := x^2 should return x^2
			// f(x):=Derivative[x^2] should return 2x
			// do not return directly, must check keepinput
			/*plainResult = evaluateMPReduce(plainResult
							+ "("
							+ ((FunctionNVar) casInput).getVarString(StringTemplate.casTemplate)
							+ ")");*/
		}
		
		String result = plainResult;
		
		if (keepInput) {
			// assume keepinput was not treated in CAS
			return casParser.toGeoGebraString(casInput, tpl);
		}
		
		// standard case
		if ("".equals(result)) {
			return null;
		}
		return toGeoGebraString(result, arbconst, tpl);

	}
	
	final public synchronized ExpressionValue evaluateToExpression(
			final ValidExpression inputExpression, MyArbitraryConstant arbconst) throws CASException {
		String result = getPlainResult(inputExpression);
		// standard case
		if ("".equals(result)) {
			return null;
		}
		return replaceRoots(casParser.parseGiac(result),arbconst);

	}

	private String getPlainResult(ValidExpression casInput) {
		// KeepInput[] command should set flag keepinput!!:=1
		// so that commands like Substitute can work accordingly
		Command cmd = casInput.getTopLevelCommand();
		
		if (cmd != null && "Delete".equals(cmd.getName())
				) {
			String label = 
					cmd.getArgument(0).toString(
							StringTemplate.defaultTemplate);
			GeoElement geo = casInput
					.getKernel()
					.lookupLabel(label);
			if(geo==null)
				geo = casInput
				.getKernel().lookupCasCellLabel(label);
			if (geo != null) {
				geo.remove();
			}
			return "true";
		}
		

		// convert parsed input to Giac string
		String giacInput = casParser.translateToCAS(casInput,
				StringTemplate.giacTemplate, this);
		
		//App.error(casInput+"\n\n"+giacInput );

		/*
		// tell MPReduce whether it should use the keep input flag,
		// e.g. important for Substitute
		StringBuilder sb = new StringBuilder();
	
		sb.append("<<resetsettings(");
		sb.append(keepInput ? 1 : 0);
		sb.append(",");
		sb.append(taylorToStd ? 1 : 0);
		sb.append(",");
		// sb.append("$ numeric!!:=0$ precision 30$ print\\_precision 16$ on pri, rationalize  $ off complex, rounded, numval, factor, exp, allfac, div, combinelogs, expandlogs, revpri $ currentx!!:= ");
		sb.append("ggbtmpvarx,ggbtmpvary);");
		

		sb.append(mpreduceInput);
		sb.append(">>");*/

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
			MyArbitraryConstant arbconst, StringTemplate tpl)
			throws CASException {
		ExpressionValue ve = replaceRoots(casParser.parseGiac(giacString), arbconst);
		//replace rational exponents by roots or vice versa

		

		return casParser.toGeoGebraString(ve, tpl);
	}
	
	private static ExpressionValue replaceRoots(ExpressionValue ve,MyArbitraryConstant arbconst){
		if (ve != null) {
			boolean toRoot = ve.getKernel().getApplication().getSettings()
					.getCasSettings().getShowExpAsRoots();
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
	 * Timeout for CAS in milliseconds.
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
			exp.getKernel();
			// get names of escaped global variables right
			// e.g. "ggbcasvar1a" needs to be changed to "a"
			// e.g. "ggbtmpvara" needs to be changed to "a"
			result = Kernel.removeCASVariablePrefix(result, " ");
		}

		c.handleCASoutput(result, input.hashCode());
		if (c.useCacheing())
			exp.getKernel().putToCasCache(input, result);
	}
	
	public void appendListStart(StringBuilder sbCASCommand){
		sbCASCommand.append("list(");
	}

	public void appendListEnd(StringBuilder sbCASCommand){
		sbCASCommand.append(")");
	}

	public String createLocusEquationScript(
			String constructRestrictions,
			String vars, String varsToEliminate) {
		StringBuilder script = new StringBuilder();
		
		return script.append("[[aa:=eliminate([").
				append(constructRestrictions).
				append("],[").
				append(varsToEliminate).
				append("])],").
				// Creating a matrix from the output to satisfy Sergio:
				append("[bb:=coeffs(aa[0],x)], [sx:=size(bb)], [sy:=size(coeffs(aa[0],y))],").
				append("[cc:=[sx,sy]], [for ii from sx-1 to 0 by -1 do dd:=coeff(bb[ii],y);").
				append("sd:=size(dd); for jj from sd-1 to 0 by -1 do ee:=dd[jj];").
				append("cc:=append(cc,ee); od; for kk from sd to sy-1 do ee:=0;").
				append("cc:=append(cc,ee); od; od], cc][5][0]")
				// See CASTranslator.createSingularScript for more details.
				
				.toString();
		
		}

	public double[][] getBivarPolyCoefficients(String rawResult, GeoGebraCAS cas) {
		String numbers = rawResult.substring(1, rawResult.length()-1);
		String[] flatData = numbers.split(",");
		int xLength = Integer.parseInt(flatData[0]);
		int yLength = Integer.parseInt(flatData[1]);
		double[][] result = new double[xLength][yLength];

		int counter = 2;
		for (int x = 0; x < xLength; x++) {
			for (int y = 0; y < yLength; y++) {
				result[x][y] = Double.parseDouble(flatData[counter]);
				App.debug("[LocusEqu] result[" + x + "," + y + "]=" + result[x][y]);
				++counter;
			}
		}
		
		return result;
	}
	
	// eg {(ggbtmpvarx>(-sqrt(110)/5)) && ((sqrt(110)/5)>ggbtmpvarx)}
	// eg {(ggbtmpvarx>=(-sqrt(110)/5)) && ((sqrt(110)/5)>=ggbtmpvarx)}
	// eg (ggbtmpvarx>3) && (4>ggbtmpvarx)
	private final static RegExp inequality = RegExp.compile("(.*)\\((ggbtmpvar.+)>(=*)(.+)\\) && \\((.+)>(=*)(ggbtmpvar.+)\\)(.*)");

	// eg 3.7 > ggbtmpvarx
	// eg (37/10) > ggbtmpvarx
	// eg 333 > ggbtmpvarx
	// eg (-33) > ggbtmpvarx
	private final static RegExp inequalitySimple = RegExp.compile("([-0-9.E/\\(\\)]+)>(=*)(ggbtmpvar.+)");
	// eg {3, 3>ggbtmpvarx, x^2}
	// eg {3, 3>ggbtmpvarx}
	// eg {3>ggbtmpvarx, x^2}
	// eg {3>ggbtmpvarx}
	private final static RegExp inequalitySimpleInList = RegExp.compile("(.*)([,{])([-0-9.E/\\(\\)]+)>(=*)(ggbtmpvar[^},]+)([,}])(.*)");

	/**
	 * convert x>3 && x<7 into 3<x<7
	 * convert 3>x into x<3
	 * convert {3>x} into {x<3}
	 * eg output from Solve[x (x-1)(x-2)(x-3)(x-4)(x-5) < 0]
	 * 
	 * Giac's normal command converts inequalities to > or >= so we don't need to check <, <=
	 * 
	 * @param exp expression
	 * @return converted expression if changed
	 */
	protected String checkInequalityInterval(String exp) {

		String ret = exp;

		MatchResult matcher = inequalitySimple.exec(ret);
		
		// swap 3>x into x<3
		if (matcher != null && exp.startsWith(matcher.getGroup(1))) {
			//App.debug(matcher.getGroup(1));
			//App.debug(matcher.getGroup(2));
			//App.debug(matcher.getGroup(3));
			//App.debug(matcher.getGroup(4));
			return matcher.getGroup(3) + "<" + matcher.getGroup(2)+ matcher.getGroup(1);
		}
		
		// swap {3>x, 6>y} into {x<3, y<6}
		while ((matcher = inequalitySimpleInList.exec(ret)) != null ) {	
			
			ret = matcher.getGroup(1) + matcher.getGroup(2) + matcher.getGroup(5) + "<" + matcher.getGroup(4) + matcher.getGroup(3) + matcher.getGroup(6) + matcher.getGroup(7);
			App.debug(ret);
		}
		
		
		// swap 5 > x && x > 3 into 3<x<5
		while ((matcher = inequality.exec(ret)) != null &&

				// check variable the same
				// ie not x>5 && y<4
				matcher.getGroup(2).equals(matcher.getGroup(7))) {	
			
			ret = matcher.getGroup(1) + matcher.getGroup(4) + "<" + matcher.getGroup(3) + matcher.getGroup(2) + "<" + matcher.getGroup(6) + matcher.getGroup(5) + matcher.getGroup(8);
		
		}

		if (!exp.equals(ret)) {
			App.debug("giac output (with inequality converted): " + ret);		
		}

		return ret;

	}

	
}
