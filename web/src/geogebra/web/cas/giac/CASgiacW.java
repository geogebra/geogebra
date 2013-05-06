package geogebra.web.cas.giac;

import geogebra.common.cas.CASparser;
import geogebra.common.cas.CasParserTools;
import geogebra.common.cas.giac.CASgiac;
import geogebra.common.kernel.AsynchronousCommand;
import geogebra.common.kernel.Kernel;
import geogebra.common.main.App;

import org.mathpiper.mpreduce.Interpretable;

/**
 * Web implementation of Giac CAS
 * @author Michael Borcherds, based on Reduce version
 *
 */
public class CASgiacW extends CASgiac implements geogebra.common.cas.Evaluate {

	
	
	private static boolean asyncstarted = false;
	private Kernel kernel;
	private Interpretable giac;
	
	/**
	 * Creates new CAS
	 * @param casParser parser
	 * @param parserTools scientific notation convertor
	 * @param kernel kernel
	 */
	public CASgiacW(CASparser casParser, CasParserTools parserTools, Kernel kernel) {
		super(casParser);
		this.parserTools = parserTools;
		this.kernel = kernel;
    }
	
	@Override
	public String evaluateCAS(String exp) {
		try {
			String processedExp = casParser.replaceIndices(exp);
			String ret = evaluateRaw(processedExp);
			ret = casParser.insertSpecialChars(ret); // undo special character
														// handling

			// convert MPReduce's scientific notation from e.g. 3.24e-4 to
			// 3.2E-4
			ret = parserTools.convertScientificFloatNotation(ret);

			return ret;
		//} catch (TimeoutException toe) {
		//	throw new Error(toe.getMessage());
		} catch (Throwable e) {
			App.debug("evaluateGiac: " + e.getMessage());
			return "?";
		}
	}

	/*
	 * called from JavaScript when the CAS is loaded
	 * (non-Javadoc)
	 * @see geogebra.common.kernel.cas.CASGenericInterface#initCAS()
	 */
	public void initCAS() {
		// not called?
	}
	
	public synchronized String evaluate(String s) {

		if (!specialFunctionsInitialized) {
			nativeEvaluateRaw(initString);
			nativeEvaluateRaw(specialFunctions);
			specialFunctionsInitialized = true;
		}
		
		App.debug("giac  input:"+s);
		String ret = nativeEvaluateRaw(s);
		App.debug("giac output:"+ret);
		
		return ret;
	}

	private native String nativeEvaluateRaw(String s) /*-{
		$wnd.console.log("js giac  input:"+s);
		caseval = $wnd.Module.cwrap('_ZN4giac7casevalEPKc', 'string', ['string']);  
		var ret = caseval(s);
		$wnd.console.log("js giac output:"+ret);
		return ret
	}-*/;

	public void initialize() {
		// not called?
	}

	public void evaluateGeoGebraCASAsync(AsynchronousCommand c) {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public String evaluate(String exp, long timeoutMilliseconds) {
	    return evaluate(exp);
    }
	

}
