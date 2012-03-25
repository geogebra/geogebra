package geogebra.common.kernel.parser.cashandlers;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.arithmetic.ExpressionNode;
import geogebra.common.kernel.arithmetic.ExpressionValue;
import geogebra.common.kernel.arithmetic.MyArbitraryConstant;
import geogebra.common.kernel.arithmetic.MyDouble;
import geogebra.common.kernel.arithmetic.MyList;
import geogebra.common.kernel.arithmetic.Variable;
import geogebra.common.main.AbstractApplication;
import geogebra.common.plugin.Operation;

/**
 * Handles special MPReduce commands to distinguish them from user defined
 * functions in the Parser.
 * 
 * @author Markus Hohenwarter
 */
public class CommandDispatcherMPReduce {

	/**
	 * Enum for special commands that may be returned by MPReduce.
	 */
	public enum commands {
		/** arbitrary complex number*/
		arbcomplex,
		/** arbitrary constant*/
		arbconst,
		/** arbitrary integer (comes from trig equations)*/
		arbint,
		/** derivative*/
		df,
		/** logb */
		logb,
		/** sine integral */
		si,
		/** cosine integral */
		ci,
		/** cosine integral */
		ei
	}

	/**
	 * @return The result of a special MPReduce command for the given argument
	 *         list as needed by the Parser. Returns null when nothing was done.
	 * 
	 * @param cmdName
	 *            name of the MPReduce command to process, see
	 *            CommandDispatcherMPReduce.commands
	 * @param args
	 *            list of command arguments
	 */
	public static ExpressionNode processCommand(String cmdName, MyList args) {

		try {
			ExpressionValue ret = null;
			Kernel kernel = args.getKernel();
			AbstractApplication.debug(cmdName);
			//TODO -- template is not important for arb*, but is this correct for df?
			StringTemplate tpl = StringTemplate.casTemplate;
			switch (commands.valueOf(cmdName)) {
			case arbcomplex:
				// e.g. arbcomplex(2) from MPreduce becomes next free index
				// label, e.g. z_5
				ret = new MyArbitraryConstant(kernel,
						MyArbitraryConstant.ARB_COMPLEX, args.getListElement(0)
								.toString(tpl));
				break;

			case arbconst:
				// e.g. arbconst(3) from MPreduce becomes next free index label,
				// e.g. c_4
				ret = new MyArbitraryConstant(kernel,
						MyArbitraryConstant.ARB_CONST, args.getListElement(0)
								.toString(tpl));
				break;

			case arbint:
				// e.g. arbint(2) from MPreduce becomes next free index label,
				// e.g. k_3
				ret = new MyArbitraryConstant(kernel,
						MyArbitraryConstant.ARB_INT, args.getListElement(0)
								.toString(tpl));
				break;
				
			case logb:
				// e.g. logb[x,3] becomes log(3,x)
				ret = new ExpressionNode(kernel,
						 args.getListElement(1),Operation.LOGB,
								args.getListElement(0));
				break;
				
			case ci:
				// e.g. logb[x,3] becomes log(3,x)
				ret = new ExpressionNode(kernel,
						 args.getListElement(0),Operation.CI,null);
				break;
			case si:
				// e.g. logb[x,3] becomes log(3,x)
				ret = new ExpressionNode(kernel,
						 args.getListElement(0),Operation.SI,
								null);
				break;
			case ei:
				// e.g. logb[x,3] becomes log(3,x)
				ret = new ExpressionNode(kernel,
						 args.getListElement(0),Operation.EI,
								null);
				break;

			case df:
				// e.g. df(f(var),var) from MPReduce becomes f'(var)
				// see http://www.geogebra.org/trac/ticket/1420
				String expStr = args.getListElement(0).toString(tpl);
				int nameEnd = expStr.indexOf('(');
				String funLabel = nameEnd > 0 ? expStr.substring(0, nameEnd)
						: expStr;

				// derivative of f gives f'
				ExpressionNode derivative = new ExpressionNode(kernel,
						new Variable(kernel, funLabel), // function label "f"
						Operation.DERIVATIVE, new MyDouble(kernel, 1));
				// function of given variable gives f'(t)
				ret = new ExpressionNode(kernel, derivative,
						Operation.FUNCTION, args.getListElement(1)); // Variable
																		// "t"
				break;
			}

			// no match or ExpressionNode
			if (ret == null || ret instanceof ExpressionNode) {
				return (ExpressionNode) ret;
			}
			// create ExpressionNode
			return new ExpressionNode(kernel, ret);
		} catch (IllegalArgumentException e) {
			// No enum const for cmdName
		} catch (Exception e) {
			System.err
					.println("CommandDispatcherMPReduce: error when processing command: "
							+ cmdName + ", " + args);
		}

		// exception
		return null;
	}

}
