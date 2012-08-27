package geogebra.cas.mpreduce;

import geogebra.common.cas.CASparser;
import geogebra.common.cas.CasParserTools;
import geogebra.common.cas.Evaluate;
import geogebra.common.cas.mpreduce.CASmpreduce;
import geogebra.common.kernel.CASException;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.algos.ConstructionElement;
import geogebra.common.kernel.arithmetic.MyArbitraryConstant;
import geogebra.common.kernel.arithmetic.ValidExpression;
import geogebra.common.kernel.cas.AsynchronousCommand;
import geogebra.common.main.App;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.mathpiper.mpreduce.Interpreter2;

/**
 * Desktop implementation of Reduce CAS
 */
public class CASmpreduceD extends CASmpreduce {

	// using static CAS instance as a workaround for the MPReduce deadlock with
	// multiple application windows
	// see http://www.geogebra.org/trac/ticket/1415
	public static Interpreter2 mpreduce_static;
	private Interpreter2 mpreduce;

	/**
	 * @param casParser CAS parser
	 * @param parserTools parser helper tools
	 * @param casPrefix prefix for CAS variables
	 */
	public CASmpreduceD(CASparser casParser, CasParserTools parserTools,String casPrefix) {
		super(casParser,casPrefix);
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
			/*try {
				initStaticMyMPReduceFunctions(mpreduce_static);
			} catch (Throwable e) {
			}*/

			App.setCASVersionString(getVersionString(mpreduce_static));
		}

		return mpreduce_static;
	}

	/**
	 * @return MPReduce interpreter using static interpreter with local kernel
	 *         initialization.
	 */
	@Override
	protected synchronized Evaluate getMPReduce() {
		if (mpreduce == null) {
			// create mpreduce as a private reference to mpreduce_static
			mpreduce = getStaticInterpreter();

			try {
				// make sure to call initMyMPReduceFunctions() for each
				// CASmpreduce instance
				// because it depends on the current kernel's ggbcasvar prefix,
				// see #1443
				initDependentMyMPReduceFunctions(mpreduce);
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}

		return mpreduce;
	}

	/**
	 * Evaluates an expression and returns the result as a string in MPReduce
	 * syntax, e.g. evaluateMathPiper("D(x) (x^2)") returns "2*x".
	 * 
	 * @param input
	 *            expression (with command names already translated to MPReduce
	 *            syntax).
	 * @return result string (null possible)
	 * @throws CASException if CAS fails
	 */
	@Override
	public final String evaluateMPReduce(String input) throws CASException {
		try {
			String exp = casParser.replaceIndices(input);
			String ret = evaluateRaw(exp);
			ret = casParser.insertSpecialChars(ret); // undo special character
														// handling

			// convert MPReduce's scientific notation from e.g. 3.24e-4 to
			// 3.2E-4
			ret = parserTools.convertScientificFloatNotation(ret);

			return ret;
		} catch (TimeoutException toe) {
			throw new geogebra.common.cas.error.TimeoutException(toe.getMessage());
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
	/**
	 * Queue of asynchronous commands that are waiting for update
	 */
	List<AsynchronousCommand> queue =new LinkedList<AsynchronousCommand>();
	
	private Thread casThread;
	@SuppressWarnings("unused")
	public void evaluateGeoGebraCASAsync(
			 final AsynchronousCommand cmd 
			) {
		App.debug("about to start some thread");
		if(!queue.contains(cmd))
			queue.add(cmd);
		
		if(casThread == null || !casThread.isAlive()){
		casThread = new Thread(){
			@Override
			public void run(){
				App.debug("thread is starting");
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
						//TODO: arbconst()
						result = evaluateGeoGebraCAS(inVE,new MyArbitraryConstant((ConstructionElement)command),
								StringTemplate.defaultTemplate);
						CASAsyncFinished(inVE, result, null, command,  input);
					}catch(Throwable exception){
						App.debug("exception handling ...");
						exception.printStackTrace();
						result ="";
						CASAsyncFinished(inVE, result,exception, command, input);
					}
					
				}
				App.debug("thread is quiting");
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
