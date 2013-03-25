package geogebra.common.cas;

import geogebra.common.kernel.AsynchronousCommand;
import geogebra.common.kernel.CASException;
import geogebra.common.kernel.CASGenericInterface;
import geogebra.common.kernel.GeoGebraCasInterface;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.arithmetic.ExpressionNode;
import geogebra.common.kernel.arithmetic.ExpressionNodeConstants.StringType;
import geogebra.common.kernel.arithmetic.ExpressionValue;
import geogebra.common.kernel.arithmetic.MyArbitraryConstant;
import geogebra.common.kernel.arithmetic.ValidExpression;
import geogebra.common.kernel.commands.Commands;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.main.App;
import geogebra.common.main.CasType;
import geogebra.common.util.MaxSizeHashMap;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * This class provides an interface for GeoGebra to use an underlying computer
 * algebra system like MPReduce, Maxima or MathPiper.
 * 
 * @author Markus Hohenwarter
 */
public class GeoGebraCAS implements GeoGebraCasInterface {

	private App app;
	private CASparser casParser;
	private CASGenericInterface cas;

	/**
	 * Creates new CAS interface
	 * 
	 * @param kernel
	 *            kernel
	 */
	public GeoGebraCAS(Kernel kernel) {
		app = kernel.getApplication();
		casParser = new CASparser(kernel.getParser(), kernel.getApplication()
				.getParserFunctions());

		// DO NOT init underlying CAS here to avoid hanging animation,
		// see http://www.geogebra.org/trac/ticket/1565
		// getCurrentCAS();
	}

	public CASparser getCASparser() {
		return casParser;
	}

	public CASGenericInterface getCurrentCAS() {
		if (cas == null) {
			app.setWaitCursor();
			initCurrentCAS();
			app.setDefaultCursor();
		}

		return cas;
	}

	/**
	 * Initializes underlying CAS
	 */
	public synchronized void initCurrentCAS() {
		if (cas == null)
			setCurrentCAS(Kernel.DEFAULT_CAS);
	}

	
	public synchronized void setCurrentCAS(final CasType CAS) {
		try {
			switch (CAS) {
			case GIAC:
				
				cas = getGiac();
				app.getSettings().getCasSettings().addListener(cas);				
				break;

			default:
				cas = getMPReduce();
				app.getSettings().getCasSettings().addListener(cas);
				break;
			/*
			 * case MATHPIPER: cas = getMathPiper(); currentCAS = CAS; break;
			 */
			}
			/*
			 * }catch (MaximaVersionUnsupportedExecption e){
			 * app.showError("CAS.MaximaVersionUnsupported");
			 * setCurrentCAS(CasType.MPREDUCE);
			 */
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Resets the cas and unbinds all variable and function definitions.
	 */
	public void reset() {
		getCurrentCAS().reset();
	}

	/**
	 * @return MPReduce
	 */
	private synchronized CASGenericInterface getMPReduce() {
		if (cas == null) {
			cas = app.getCASFactory()
					.newMPReduce(casParser, new CasParserToolsImpl('e'),
							app.getKernel());
		}
		return cas;
	}

	/**
	 * @return Giac
	 */
	private synchronized CASGenericInterface getGiac() {
		if (cas == null) {
			cas = app.getCASFactory()
					.newGiac(casParser, new CasParserToolsImpl('e'),
							app.getKernel());
		}
		return cas;
	}

	public String evaluateGeoGebraCAS(ValidExpression casInput,
			MyArbitraryConstant arbconst, StringTemplate tpl)
			throws CASException {

		String result = null;
		CASException exception = null;
		try {
			result = getCurrentCAS().evaluateGeoGebraCAS(casInput, arbconst,
					tpl);
		} catch (CASException ce) {
			exception = ce;
		} finally {
			// do nothing
		}

		// check if keep input command was successful
		// e.g. for KeepInput[Substitute[...]]
		// otherwise return input
		if (casInput.isKeepInputUsed()
				&& (exception != null || "?".equals(result))) {
			// return original input
			return casInput.toString(tpl);
		}

		// pass on exception
		if (exception != null)
			throw exception;

		// success
		if (result != null) {
			// get names of escaped global variables right
			// e.g. "ggbcasvar1a" needs to be changed to "a"
			// e.g. "ggbtmpvara" needs to be changed to "a"
			result = app.getKernel().removeCASVariablePrefix(result, " ");
		}

		return result;
	}

	
	final public String evaluateGeoGebraCAS(String exp,
			MyArbitraryConstant arbconst) throws CASException {
		try {
			ValidExpression inVE = casParser.parseGeoGebraCASInput(exp);
			String ret = evaluateGeoGebraCAS(inVE, arbconst);
			if (ret == null)
				throw new CASException(new Exception(
						app.getLocalization().getError("CAS.GeneralErrorMessage")));
			return ret;
		} catch (Throwable t) {
			t.printStackTrace();
			throw new CASException(t);
		}
	}
	final public String evaluateRaw(String exp) throws Throwable {
		return getCurrentCAS().evaluateRaw(exp);
	}

	/**
	 * Evaluates an expression given in MPReduce syntax.
	 * 
	 * @return result string (null possible)
	 * @param exp
	 *            the expression
	 * @throws CASException
	 *             if there is a timeout or the expression cannot be evaluated
	 * */
	final public String evaluateMPReduce(String exp) throws CASException {
		return getMPReduce().evaluateCAS(exp);
	}

	// these variables are cached to gain some speed in getPolynomialCoeffs
	private Map<String, String[]> getPolynomialCoeffsCache = new MaxSizeHashMap<String, String[]>(
			Kernel.GEOGEBRA_CAS_CACHE_SIZE);
	private StringBuilder getPolynomialCoeffsSB = new StringBuilder();
	private StringBuilder sbPolyCoeffs = new StringBuilder();

	final public String[] getPolynomialCoeffs(final String polyExpr,
			final String variable) {
		getPolynomialCoeffsSB.setLength(0);
		getPolynomialCoeffsSB.append(polyExpr);
		getPolynomialCoeffsSB.append(',');
		getPolynomialCoeffsSB.append(variable);

		String[] result = getPolynomialCoeffsCache.get(getPolynomialCoeffsSB
				.toString());
		if (result != null)
			return result;

		sbPolyCoeffs.setLength(0);
		sbPolyCoeffs.append("coeff(");
		sbPolyCoeffs.append(getPolynomialCoeffsSB.toString());
		sbPolyCoeffs.append(')');

		try {
			// expand expression and get coefficients of
			// "3*a*x^2 + b" in form "{ b, 0, 3*a }"
			String tmp = evaluateMPReduce(sbPolyCoeffs.toString());

			// no result
			if ("{}".equals(tmp) || "".equals(tmp) || tmp == null)
				return null;

			// not a polynomial: result still includes the variable, e.g. "x"
			if (tmp.indexOf(variable) >= 0)
				return null;

			// get names of escaped global variables right
			// e.g. "ggbcasvara" needs to be changed to "a"
			tmp = app.getKernel().removeCASVariablePrefix(tmp);

			tmp = tmp.substring(1, tmp.length() - 1); // strip '{' and '}'
			result = tmp.split(",");

			getPolynomialCoeffsCache.put(getPolynomialCoeffsSB.toString(),
					result);
			return result;
		} catch (Throwable e) {
			App.debug("GeoGebraCAS.getPolynomialCoeffs(): " + e.getMessage());
			// e.printStackTrace();
		}

		return null;
	}

	final private static String toString(final ExpressionValue ev,
			final boolean symbolic, StringTemplate tpl) {
		/* previously this method also replaced f by f(x), but FunctionExpander takes care of that now */
		if (symbolic) {
			return ev.wrap().toString(tpl);
		}
		return ev.toValueString(tpl);
	}

	final synchronized public String getCASCommand(final String name,
			final ArrayList<ExpressionNode> args, boolean symbolic,
			StringTemplate tpl) {
		StringBuilder sbCASCommand = new StringBuilder(80);

		// build command key as name + ".N"
		sbCASCommand.setLength(0);
		sbCASCommand.append(name);
		sbCASCommand.append(".N");
		cas.loadPackagesFor(sbCASCommand.toString());
		String translation = casParser.getTranslatedCASCommand(sbCASCommand
				.toString());

		// check for eg Sum.N=sum(%)
		if (translation != null) {
			sbCASCommand.setLength(0);
			for (int i = 0; i < translation.length(); i++) {
				char ch = translation.charAt(i);
				if (ch == '%') {
					if (args.size() == 1) { // might be a list as the argument
						ExpressionValue ev = args.get(0).unwrap();
						String str = toString(ev, symbolic, tpl);
						sbCASCommand.append(str);											
					} else {
						getCurrentCAS().appendListStart(sbCASCommand);
						for (int j = 0; j < args.size(); j++) {
							ExpressionValue ev = args.get(j);
							sbCASCommand.append(toString(ev, symbolic, tpl));
							sbCASCommand.append(',');
						}
						// remove last comma
						sbCASCommand.setLength(sbCASCommand.length() - 1);
						getCurrentCAS().appendListEnd(sbCASCommand);
					}
				} else {
					sbCASCommand.append(ch);
				}

			}

			return sbCASCommand.toString();
		}

		// build command key as name + "." + args.size()
		// remove 'N'
		sbCASCommand.setLength(sbCASCommand.length() - 1);
		// add eg '3'
		sbCASCommand.append(args.size());

		// get translation ggb -> MathPiper/Maxima
		cas.loadPackagesFor(sbCASCommand.toString());
		translation = casParser
				.getTranslatedCASCommand(sbCASCommand.toString());
		sbCASCommand.setLength(0);

		// no translation found:
		// use key as function name
		if (translation == null) {
			Kernel kern = app.getKernel();
			boolean silent = kern.isSilentMode();
			// convert command names x, y, z to xcoord, ycoord, ycoord to
			// protect it in CAS
			// see http://www.geogebra.org/trac/ticket/1440
			boolean handled = false;			
			if (name.length() == 1) {
				char ch = name.charAt(0);
				if (ch == 'x' || ch == 'y' || ch == 'z') {
					if (args.get(0).isListValue()) {
						sbCASCommand.append("applyfunction(");
						sbCASCommand.append(ch);
						sbCASCommand.append("coord,");
					} else if (args.get(0).hasCoords()) {
						sbCASCommand.append(ch);
						sbCASCommand.append("coord(");
					} else {
						sbCASCommand.append("multiplication(");
						sbCASCommand.append(tpl.printVariableName(ch+""));
						sbCASCommand.append(",");
					}
					handled = true;
				}
			}else try{
				Commands c =Commands.valueOf(name);				
				if(c!=null){
					
					kern.setSilentMode(true);
					StringBuilder sb = new StringBuilder(name);
					sb.append('[');
					for(int i=0;i<args.size();i++){
						if(i>0)
							sb.append(',');
						sb.append(args.get(i).toOutputValueString(StringTemplate.defaultTemplate));
					}
					sb.append(']');
					App.debug(sb.toString());
					GeoElement[] ggbResult = kern.getAlgebraProcessor().processAlgebraCommandNoExceptionHandling(sb.toString(), 
							false,false,false);
					kern.setSilentMode(silent);
					if(ggbResult!=null && ggbResult.length>0 && ggbResult[0]!=null)
						return ggbResult[0].toValueString(tpl);
				}
			}catch(Exception e){
				kern.setSilentMode(silent);
				App.info(name+" not known command or function");
			}

			// standard case: add ggbcasvar prefix to name for CAS
			if (!handled) {
				sbCASCommand.append(tpl
						.printVariableName(name));
				sbCASCommand.append('(');
			}
			for (int i = 0; i < args.size(); i++) {
				ExpressionValue ev = args.get(i);
				sbCASCommand.append(toString(ev, symbolic, tpl));
				sbCASCommand.append(',');
			}
			sbCASCommand.setCharAt(sbCASCommand.length() - 1, ')');
		}

		// translation found:
		// replace %0, %1, etc. in translation by command arguments
		else {
			for (int i = 0; i < translation.length(); i++) {
				char ch = translation.charAt(i);
				if (ch == '%') {
					// get number after %
					i++;
					int pos = translation.charAt(i) - '0';
					if (pos >= 0 && pos < args.size()) {
						// success: insert argument(pos)
						ExpressionValue ev = args.get(pos);
						sbCASCommand.append(toString(ev, symbolic, tpl));
					} else {
						// failed
						sbCASCommand.append(ch);
						sbCASCommand.append(translation.charAt(i));
					}
					//@ is a hack: only use the value if it does not contain () to avoid (1,2)' in CAS 
				}else if (ch == '@') {
					// get number after %
					i++;
					int pos = translation.charAt(i) - '0';
					if (pos >= 0 && pos < args.size()) {
						// success: insert argument(pos)
						ExpressionValue ev = args.get(pos);
						if(toString(ev, symbolic, tpl).matches("[^(),]*"))
							sbCASCommand.append(toString(ev, symbolic, tpl));
						else sbCASCommand.append("x");
					} else {
						// failed
						sbCASCommand.append(ch);
						sbCASCommand.append(translation.charAt(i));
					}
				} else {
					sbCASCommand.append(ch);
				}
			}
		}

		return sbCASCommand.toString();
	}

	final public boolean isCommandAvailable(final Command cmd) {
		StringBuilder sbCASCommand = new StringBuilder();
		sbCASCommand.append(cmd.getName());
		sbCASCommand.append(".");
		sbCASCommand.append(cmd.getArgumentNumber());
		if (casParser.isCommandAvailable(sbCASCommand.toString()))
			return true;

		sbCASCommand.setLength(0);
		sbCASCommand.append(cmd.getName());
		sbCASCommand.append(".N");
		if (casParser.isCommandAvailable(sbCASCommand.toString())) {
			return true;
		}
		return false;
	}

	public boolean isStructurallyEqual(final ValidExpression inputVE,
			final String localizedInput) {
		try {
			// current input
			String input1normalized = casParser.toString(inputVE,
					StringTemplate.get(StringType.GEOGEBRA_XML));

			// new input
			ValidExpression ve2 = casParser
					.parseGeoGebraCASInputAndResolveDummyVars(localizedInput);
			String input2normalized = casParser.toString(ve2,
					StringTemplate.get(StringType.GEOGEBRA_XML));

			// compare if the parsed expressions are equal
			return input1normalized.equals(input2normalized);
		} catch (Throwable th) {
			App.debug("Invalid selection: "
					+ localizedInput);
			return false;
		}
	}


	public void evaluateGeoGebraCASAsync(final AsynchronousCommand c) {
		getCurrentCAS().evaluateGeoGebraCASAsync(c);
	}

	public String evaluateGeoGebraCAS(ValidExpression evalVE,
			MyArbitraryConstant arbconst) {
		return evaluateGeoGebraCAS(evalVE, arbconst,
				StringTemplate.numericNoLocal);
	}

	public Set<String> getAvailableCommandNames() {
		Set<String> cmdSet = new HashSet<String>();
		for (String signature : casParser.getTranslationRessourceBundle()
				.keySet()) {
			String cmd = signature.substring(0, signature.indexOf('.'));
			cmdSet.add(cmd);
		}
		return cmdSet;
	}

	public void clearCache() {
		getPolynomialCoeffsCache.clear();		
	}

}