package geogebra.web.cas.mpreduce;

import geogebra.common.cas.CASparser;
import geogebra.common.cas.CasParserTools;
import geogebra.common.cas.Evaluate;
import geogebra.common.cas.mpreduce.CASmpreduce;
import geogebra.common.kernel.AsynchronousCommand;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.arithmetic.ValidExpression;
import geogebra.common.main.App;

import org.mathpiper.mpreduce.Interpretable;
import org.mathpiper.mpreduce.InterpreterJs;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;

/**
 * Web implementation of MPReduce CAS
 * @author Michael Borcherds, based on desktop version
 *
 */
public class CASmpreduceW extends CASmpreduce implements geogebra.common.cas.Evaluate {

	
	
	private static Interpretable mpreduce_static = new Interpretable() {
		
		public String evaluate(String exp, long timeoutMilliseconds)
		        throws Throwable {
			// TODO Auto-generated method stub
			return "?";
		}
		
		public String version() {
			// TODO Auto-generated method stub
			return "?";
		}
		
		public void initialize() {
			// TODO Auto-generated method stub
			
		}
		
		public RepeatingCommand getInitializationExecutor() {
			// TODO Auto-generated method stub
			return null;
		}
		
		public String evaluate(String send) {
			// TODO Auto-generated method stub
			return "?";
		}
	};
	private static boolean asyncstarted = false;
	private Kernel kernel;
	private Interpretable mpreduce;
	
	/**
	 * Creates new CAS
	 * @param casParser parser
	 * @param parserTools scientific notation convertor
	 * @param kernel kernel
	 */
	public CASmpreduceW(CASparser casParser, CasParserTools parserTools,Kernel kernel) {
		super(casParser,kernel.getCasVariablePrefix());
		this.parserTools = parserTools;
		this.kernel=kernel;
    }
	
	@Override
	protected synchronized Evaluate getMPReduce() {
		//return this;
		if (mpreduce == null) {
			// create mpreduce as a private reference to mpreduce_static
			mpreduce = getStaticInterpreter(this);

			try {
				// make sure to call initMyMPReduceFunctions() for each
				// CASmpreduce instance
				// because it depends on the current kernel's ggbcasvar prefix,
				// see #1443
				//dont do this here
				//initMyMPReduceFunctions((Evaluate)mpreduce);
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}

		return mpreduce;
	}
	
	/**
	 * @param casInstance CAS instance
	 * @return Static MPReduce interpreter shared by all CASmpreduce instances.
	 */
	public static synchronized Interpretable getStaticInterpreter(final CASmpreduceW casInstance) {
		if (!asyncstarted ) {
			asyncstarted = true;
			GWT.runAsync(new RunAsyncCallback() {
				
				public void onSuccess() {
					//just let the constructor run (until callback we can't do anything wit cas anyway, let dummy cas work
					new InterpreterJs().getStartMessage();
					
					//this will be hard. 1; First async: we got CAS here. But we must load Lisp image.
					InterpreterJs.casLoadImage(casInstance);      
				}
				
				public void onFailure(Throwable reason) {
					App.debug(reason);
				}
			});
		}
		return mpreduce_static;
	}

	@Override
	public String evaluateMPReduce(String exp) {
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
			App.debug("evaluateMPReduce: " + e.getMessage());
			return "?";
		}
	}


	/*/*
	 * Evaluates the processed expression in CAS
	 * @param exp expression
	 * @return MPReduce string representation of result
	 */
 
	
	/*public static native String nativeEvaluateRaw(String exp) /*-{
	if (typeof $wnd.callCAS === 'function')
		return $wnd.callCAS(exp);
	}-*/



	@Override
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
			App.error("Failed to clear variable from MPReduce: " + var);
		}
	}

	/*
	 * called from JavaScript when the CAS is loaded
	 * (non-Javadoc)
	 * @see geogebra.common.kernel.cas.CASGenericInterface#initCAS()
	 */
	public void initCAS() {
		App.debug("initCAS() called");
		try {
	        initDependentMyMPReduceFunctions(this);
        } catch (Throwable e) {
	        e.printStackTrace();
        }
	}
	
	public synchronized String evaluate(String sendRaw) {
		String send = sendRaw.trim();
		if (((send.endsWith(";")) || (send.endsWith("$"))) != true)
			send = send + ";\n";
		
		while (send.endsWith(";;"))
			send = send.substring(0, send.length() - 1);
	
		while (send.endsWith("$"))
			send = send.substring(0, send.length() - 1);

		send = send + "\n";
		// System.err.println("Expression for MPReduce "+send.trim());
		

		return nativeEvaluateRaw(send);
	}

	private String nativeEvaluateRaw(String send) {
	    String ret = mpreduce.evaluate(send);
	    if (ret.length() > 3)
	    	ret = ret.substring(0, ret.length()-2);
	    return ret;
    }

	public String evaluate(String exp, long timeoutMilliseconds) {
	    return evaluate(exp);
    }

	public void initialize() {
	    // just needed for Desktop
    }
	
	/**
	 * TODO: Current implementation for web is actually SYNCHRONOUS
	 */
	public void evaluateGeoGebraCASAsync(
			 final AsynchronousCommand command 
			) {
		
				String input = command.getCasInput();
				String result;
				ValidExpression inVE = null;
				try{
					inVE = casParser.parseGeoGebraCASInput(input);
					//TODO: arbconst()
					result = evaluateGeoGebraCAS(inVE,null,StringTemplate.defaultTemplate);
				}catch(Throwable exception){
					result ="";
					CASAsyncFinished(inVE, result, exception, command, input);
				}
				CASAsyncFinished(inVE, result, null, command, input);
			}

	/**
	 * Callback method; used when Lisp image is loaded
	 */
	public void lispImageLoaded() {
		//now we have REAL cas
		App.debug("LISP image loaded");
		mpreduce_static = InterpreterJs.getInstance();
		mpreduce = mpreduce_static;
		//2; Second callback: when LISP image loaded :-)
		try {
	        initDependentMyMPReduceFunctions(mpreduce);
        } catch (Throwable e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
        }
        kernel.getApplication().getGgbApi().initCAS();
    }
}
