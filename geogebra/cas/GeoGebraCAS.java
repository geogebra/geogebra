package geogebra.cas;

import geogebra.cas.error.CASException;
import geogebra.cas.mpreduce.CASmpreduce;
import geogebra.kernel.GeoGebraCASInterface;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.Command;
import geogebra.kernel.arithmetic.ExpressionNode;
import geogebra.kernel.arithmetic.ExpressionValue;
import geogebra.kernel.arithmetic.ValidExpression;
import geogebra.main.Application;
import geogebra.util.MaxSizeHashMap;

import java.util.ArrayList;
import java.util.Map;

/**
 * This class provides an interface for GeoGebra to use the computer algebra
 * systems Maxima and MathPiper.
 * 
 * @author Markus Hohenwarter
 */
public class GeoGebraCAS implements GeoGebraCASInterface {

	private Application app;
	private CASparser casParser;
	private CASgeneric cas;
	public int currentCAS = -1;

	public GeoGebraCAS(Kernel kernel) {
		app = kernel.getApplication();
		casParser = new CASparser(kernel);
		setCurrentCAS(Kernel.DEFAULT_CAS);
	}
	
	public CASparser getCASparser() {
		return casParser;
	}
	
	public CASgeneric getCurrentCAS() {
		return cas;
	}
	
	public int getCurrentCASstringType() {
		switch (currentCAS) {
			case Application.CAS_MAXIMA:
				return ExpressionNode.STRING_TYPE_MAXIMA;
				
			case Application.CAS_MPREDUCE:
				return ExpressionNode.STRING_TYPE_MPREDUCE;
				
			default:
			case Application.CAS_MATHPIPER:
				return ExpressionNode.STRING_TYPE_MATH_PIPER;	
		}
	}
	
	/**
	 * Sets the currently used CAS for evaluateGeoGebraCAS().
	 * @param CAS use CAS_MATHPIPER or CAS_MAXIMA
	 */
	public void setCurrentCAS(int CAS) {
		try {
			switch (CAS) {
			/*
				case Application.CAS_MAXIMA:
					cas = getMaxima();
					((CASmaxima) cas).initialize();
					currentCAS = CAS;
					break;*/
					
				default:
					cas = getMPReduce();
					app.getSettings().getCasSettings().addListener(cas);
					currentCAS = CAS;
					break;	
				/*
				case Application.CAS_MATHPIPER:
					cas = getMathPiper();
					currentCAS = CAS;
					break;
					*/
			}
			/*
		}catch (MaximaVersionUnsupportedExecption e){
			app.showError("CAS.MaximaVersionUnsupported");
			setCurrentCAS(Application.CAS_MPREDUCE);*/
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Resets the cas and unbinds all variable and function definitions.
	 */
	public void reset() {
		cas.reset();
	}
	
	/**
	 * Sets the number of signficiant figures (digits) that should be used as print precision for the
	 * output of Numeric[] commands.
	 * 
	 * @param significantNumbers
	 */
	public void setSignificantFiguresForNumeric(int significantNumbers) {
		cas.setSignificantFiguresForNumeric(significantNumbers);
	}
	
	/*
	private CASmathpiper getMathPiper() {
		if (currentCAS == Application.CAS_MATHPIPER)
			return (CASmathpiper) cas;
		else
			return new CASmathpiper(casParser, new CasParserToolsImpl('e'));
	}*/
	
	/*
	private CASmaxima getMaxima() {
		if (currentCAS == Application.CAS_MAXIMA)
			return (CASmaxima) cas;
		else
			return new CASmaxima(casParser, new CasParserToolsImpl('b'));
	}*/
	
	private CASmpreduce getMPReduce() {
		if (currentCAS == Application.CAS_MPREDUCE)
			return (CASmpreduce) cas;
		else
			return new CASmpreduce(casParser, new CasParserToolsImpl('e'));
	}
	
//	/**
//	 * Returns whether var is a defined variable.
//	 */
//	public boolean isVariableBound(String var) {
//		return cas.isVariableBound(var);
//	}
	
	/**
	 * Unbinds (deletes) variable.
	 * @param var
	 */
	public void unbindVariable(String var) {
		cas.unbindVariable(var);
	}
	
	/**
	 * Evaluates a valid expression and returns the resulting String in GeoGebra notation.
	 * @param casInput Input in GeoGebraCAS syntax
	 * @return evaluation result
	 * @throws CASException
	 */
	public String evaluateGeoGebraCAS(ValidExpression casInput) throws CASException {
		Kernel kernel = app.getKernel();
		boolean oldDigits = Kernel.internationalizeDigits;
		Kernel.internationalizeDigits = false;			
		
		String result = null;
		CASException exception = null;
		try {		
			result = cas.evaluateGeoGebraCAS(casInput);					
		} 	
		catch (CASException ce) {
			exception = ce;			
		}
		finally  {
			Kernel.internationalizeDigits = oldDigits;
		}
		
		// check if keep input command was successful
		// e.g. for KeepInput[Substitute[...]]
		// otherwise return input
		if (casInput.isKeepInputUsed() && 
				(exception != null || "?".equals(result))) 
		{			
			// return original input
			return casInput.toString();
		}
		
		// pass on exception
		if (exception != null)
			throw exception;
					
		// success
		if (result != null) {
			// get names of escaped global variables right
			// e.g. "ggbcasvar1a" needs to be changed to "a"
			// e.g. "ggbtmpvara" needs to be changed to "a"
			result = app.getKernel().removeCASVariablePrefix(result);
		}
		
		return result;
	}
	
	/** 
	 * Evaluates an expression in GeoGebraCAS syntax.
	 * @param exp 
     * @return result string in GeoGebra syntax (null possible)
	 * @throws CASException 
     */
	final public String evaluateGeoGebraCAS(String exp) throws CASException {
		try
		{
			ValidExpression inVE = casParser.parseGeoGebraCASInput(exp);
			return evaluateGeoGebraCAS(inVE);
		} catch (Throwable t)
		{
			throw new CASException(t);
		}
	}
	
	/** 
	 * Evaluates an expression in the syntax of the currently active CAS
	 * (MathPiper or Maxima).
     * @return result string (null possible)
	 * @throws Throwable 
     */
	final public String evaluateRaw(String exp) throws Throwable {
		return cas.evaluateRaw(exp);
	}
	
	/**
	 * Returns the error message of the last call of evaluateGeoGebraCAS().
	 * @return null if last evaluation was successful.
	 */
	final synchronized public String getGeoGebraCASError() {
		return cas.getEvaluateGeoGebraCASerror();
	}

	
	/** 
	 * Evaluates an expression given in MPReduce syntax.
     * @return result string (null possible)
     * @param exp the expression
	 * @throws CASException
	 * */
	final public String evaluateMPReduce(String exp) throws CASException
	{
		return getMPReduce().evaluateMPReduce(exp);
	}


	// these variables are cached to gain some speed in getPolynomialCoeffs
	private Map<String, String[]> getPolynomialCoeffsCache = new MaxSizeHashMap<String, String[]>(Kernel.GEOGEBRA_CAS_CACHE_SIZE);
	private StringBuilder getPolynomialCoeffsSB = new StringBuilder();
	private StringBuilder sbPolyCoeffs = new StringBuilder();
	
	/**
	 * Expands the given MPreduce expression and tries to get its polynomial
	 * coefficients. The coefficients are returned in ascending order. If exp is
	 * not a polynomial, null is returned.
	 * 
	 * example: getPolynomialCoeffs("3*a*x^2 + b"); returns ["b", "0", "3*a"]
	 */
	final public String[] getPolynomialCoeffs(String polyExpr, String variable) {
		getPolynomialCoeffsSB.setLength(0);
		getPolynomialCoeffsSB.append(polyExpr);
		getPolynomialCoeffsSB.append(',');
		getPolynomialCoeffsSB.append(variable);
		
		String[] result = getPolynomialCoeffsCache.get(getPolynomialCoeffsSB.toString());
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

			tmp = tmp.substring(1, tmp.length()-1); // strip '{' and '}'
			result = tmp.split(",");
			
			getPolynomialCoeffsCache.put(getPolynomialCoeffsSB.toString(), result);
            return result;						
		} 
		catch(Throwable e) {
			Application.debug("GeoGebraCAS.getPolynomialCoeffs(): " + e.getMessage());
			//e.printStackTrace();
		}
		
		return null;
	}
	
	final private String toString(ExpressionValue ev, boolean symbolic) {
		if (symbolic)
			return ev.toString();
		else
			return ev.toValueString();
		
	}
	
	/**
	 * Returns the CAS command for the currently set CAS using the given key and command arguments. 
	 * For example, getCASCommand("Expand.1", {"3*(a+b)"}) returns "ExpandBrackets( 3*(a+b) )" when
	 * MathPiper is the currently used CAS.
	 */
	final synchronized public String getCASCommand(String name, ArrayList args, boolean symbolic) {
		StringBuilder sbCASCommand = new StringBuilder(80);
				
		// build command key as name + ".N"
		sbCASCommand.setLength(0);
		sbCASCommand.append(name);
		sbCASCommand.append(".N");
		
		String translation = cas.getTranslatedCASCommand(sbCASCommand.toString());

		// check for eg Sum.N=sum(%)
		if (translation != null) {
			sbCASCommand.setLength(0);
			for (int i = 0; i < translation.length(); i++) {
				char ch = translation.charAt(i);
				if (ch == '%') {
					if (args.size() == 1) { // might be a list as the argument
						ExpressionValue ev = (ExpressionValue) args.get(0);
						String str = toString(ev, symbolic);
						if (ev.isListValue()) {
							// is a list, remove { and }
							// resp. list( ... ) in mpreduce
							if (currentCAS==Application.CAS_MPREDUCE)
								sbCASCommand.append(str.substring(22, str.length() - 2));
							else
								sbCASCommand.append(str.substring(1, str.length() - 1));
						} else {
							// not a list, just append
							sbCASCommand.append(str);
						}
					}
					else {
						for (int j=0; j < args.size(); j++) {
						ExpressionValue ev = (ExpressionValue) args.get(j);				
						sbCASCommand.append(toString(ev, symbolic));
						sbCASCommand.append(',');
						}
						// remove last comma
						sbCASCommand.setLength(sbCASCommand.length() - 1);
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
		translation = cas.getTranslatedCASCommand(sbCASCommand.toString());
		sbCASCommand.setLength(0);		
		
		// no translation found: 
		// use key as function name
		if (translation == null) {		
			
			// convert command names x, y, z to xcoord, ycoord, ycoord to protect it in CAS
			// see http://www.geogebra.org/trac/ticket/1440
			boolean handled = false;
			if (name.length() == 1) {
				char ch = name.charAt(0);
				if (ch == 'x' || ch == 'y' || ch == 'z') {
					sbCASCommand.append(ch);
					sbCASCommand.append("coord");
					handled = true;
				}
			}
				
			// standard case: add ggbcasvar prefix to name for CAS
			if (!handled)
				sbCASCommand.append(app.getKernel().printVariableName(name));
			
			sbCASCommand.append('(');
			for (int i=0; i < args.size(); i++) {
				ExpressionValue ev = (ExpressionValue) args.get(i);				
				sbCASCommand.append(toString(ev,symbolic));
				sbCASCommand.append(',');
			}
			sbCASCommand.setCharAt(sbCASCommand.length()-1, ')');
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
						ExpressionValue ev = (ExpressionValue) args.get(pos);				
						if (symbolic)
							sbCASCommand.append(ev.toString());
						else
							sbCASCommand.append(ev.toValueString());
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
	
	
	/**
	 * Returns whether the given command is available in the underlying CAS.
	 * @param cmd command with name and number of arguments
	 * @return whether command is available
	 */
	final public boolean isCommandAvailable(Command cmd) {
		StringBuilder sbCASCommand = new StringBuilder();
		sbCASCommand.append(cmd.getName());
		sbCASCommand.append(".");
		sbCASCommand.append(cmd.getArgumentNumber());
		if (cas.isCommandAvailable(sbCASCommand.toString()))
			return true;
		
		sbCASCommand.setLength(0);
		sbCASCommand.append(cmd.getName());
		sbCASCommand.append(".N");
		if (cas.isCommandAvailable(sbCASCommand.toString()))
			return true;
		else
			return false;
	}
	
	/**
	 * Returns true if the two input expressions are structurally equal. 
	 * For example "2 + 2/3" is structurally equal to "2 + (2/3)"
	 * but unequal to "(2 + 2)/3"
	 * @param inputVE includes internal command names
	 * @param localizedInput includes localized command names
	 */
	public boolean isStructurallyEqual(ValidExpression inputVE, String localizedInput) {
		try {
			// current input
			String input1normalized = casParser.toString(inputVE, ExpressionNode.STRING_TYPE_GEOGEBRA_XML);			
			
			// new input
			ValidExpression ve2 = casParser.parseGeoGebraCASInputAndResolveDummyVars(localizedInput);
			String input2normalized = casParser.toString(ve2, ExpressionNode.STRING_TYPE_GEOGEBRA_XML);
			
			// compare if the parsed expressions are equal
			return input1normalized.equals(input2normalized);
		} 
		catch (Throwable th) {
			System.err.println("GeoGebraCAS.isStructurallyEqual: " + th.getMessage() );
			return false;
		}
	}
	
}