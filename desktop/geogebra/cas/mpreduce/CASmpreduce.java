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

	@Override
	public void evaluateGeoGebraCASAsync(final String input,
			final boolean useCaching, final AsynchronousCommand command, final int id, 
			final boolean oldDigits) {
		
		Thread casThread = new Thread(){
			@Override
			public void run(){
				String result;
				ValidExpression inVE = null;
				AbstractApplication.debug("thread is running");
				try{
					inVE = casParser.parseGeoGebraCASInput(input);
					result = evaluateGeoGebraCAS(inVE);
				}catch(Throwable exception){
					result ="";
					CASAsyncFinished(inVE, result,useCaching, exception, command, id, oldDigits,input);
				}
				CASAsyncFinished(inVE, result,useCaching, null, command, id, oldDigits,input);
			}
		};
		if(AsynchronousCommand.USE_ASYNCHRONOUS)
			casThread.start();
		else
			casThread.run();
	}

	public void initCAS() {
		// TODO Auto-generated method stub
		
	}
	
}
