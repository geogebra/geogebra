package geogebra.web.cas.mpreduce;

import geogebra.common.cas.CASparser;
import geogebra.common.cas.CasParserTools;
import geogebra.common.cas.Evaluate;
import geogebra.common.cas.mpreduce.AbstractCASmpreduce;

public class CASmpreduce extends AbstractCASmpreduce implements geogebra.common.cas.Evaluate {


	public CASmpreduce(CASparser casParser, CasParserTools t) {
		super(casParser);
		this.parserTools = parserTools;
    }
	
	@Override
	protected synchronized Evaluate getMPReduce() {
		return this;
	}

	@Override
	public String evaluateMPReduce(String exp) {
		try {
			exp = casParser.replaceIndices(exp);
			String ret = evaluateRaw(exp);
			ret = casParser.insertSpecialChars(ret); // undo special character
														// handling

			// convert MPReduce's scientific notation from e.g. 3.24e-4 to
			// 3.2E-4
			ret = parserTools.convertScientificFloatNotation(ret);

			return ret;
		//} catch (TimeoutException toe) {
		//	throw new Error(toe.getMessage());
		} catch (Throwable e) {
			System.err.println("evaluateMPReduce: " + e.getMessage());
			return "?";
		}
	}



	@Override
	public String evaluateRaw(String exp) {
		return nativeEvaluateRaw(exp);
	}
	
	public static native String nativeEvaluateRaw(String exp) /*-{
	if (typeof $wnd.callCAS === 'function')
		return $wnd.callCAS(exp);
	}-*/;



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

	/*
	 * called from JavaScript when the CAS is loaded
	 * (non-Javadoc)
	 * @see geogebra.common.kernel.cas.CASGenericInterface#initCAS()
	 */
	public void initCAS() {
		try {
	        initMyMPReduceFunctions(this);
        } catch (Throwable e) {
	        e.printStackTrace();
        }
	}

	
	public synchronized String evaluate(String send) {
		send = send.trim();
		if (((send.endsWith(";")) || (send.endsWith("$"))) != true)
			send = send + ";\n";
		
		while (send.endsWith(";;"))
			send = send.substring(0, send.length() - 1);
	
		while (send.endsWith("$"))
			send = send.substring(0, send.length() - 1);

		send = send + "\n";
		// System.err.println("Expression for MPReduce "+send.trim());
		

		return evaluateRaw(send);
	}

	public String evaluate(String exp, long timeoutMilliseconds) {
	    return evaluate(exp);
    }

	public void initialize() {
	    // just needed for Desktop
    }




}
