package geogebra.kernel.parser.cashandlers;

import geogebra.common.kernel.arithmetic.ExpressionNode;
import geogebra.common.kernel.arithmetic.ExpressionValue;
import geogebra.common.kernel.arithmetic.MyDouble;
import geogebra.common.kernel.arithmetic.MyList;
import geogebra.common.kernel.arithmetic.Operation;
import geogebra.common.kernel.arithmetic.Variable;
import geogebra.kernel.Kernel;
import geogebra.kernel.arithmetic.MyArbitraryConstant;

/**
 * Handles special MPReduce commands to distinguish them from user defined functions
 * in the Parser.
 * 
 * @author Markus Hohenwarter
 */
public class CommandDispatcherMPReduce {
	
	/**
	 * Enum for special commands that may be returned by MPReduce.
	 */
	public enum commands { arbcomplex, arbconst, arbint, df };
	
	/**
	 * @return The result of a special MPReduce command for the given argument list as needed
	 * by the Parser. Returns null when nothing was done.
	 * 
	 * @param cmdName name of the MPReduce command to process, see CommandDispatcherMPReduce.commands
	 * @param args list of command arguments
	 */
	public static ExpressionNode processCommand(String cmdName, MyList args) {
		
		try {
			ExpressionValue ret = null;
			Kernel kernel = (Kernel) args.getKernel();
			
			switch (commands.valueOf(cmdName)) {
				case arbcomplex:
					// e.g. arbcomplex(2) from MPreduce becomes next free index label, e.g. z_5
					ret = new MyArbitraryConstant(kernel, 
							MyArbitraryConstant.ARB_COMPLEX, 
							args.getListElement(0).toString());
					break;
		
				case arbconst:
			  		// e.g. arbconst(3) from MPreduce becomes next free index label, e.g. c_4
			        ret = new MyArbitraryConstant(kernel, 
			        		MyArbitraryConstant.ARB_CONST, 
			        		args.getListElement(0).toString());
			        break;
	
				case arbint:
					// e.g. arbint(2) from MPreduce becomes next free index label, e.g. k_3
					ret = new MyArbitraryConstant(kernel, 
							MyArbitraryConstant.ARB_INT, 
							args.getListElement(0).toString());  
					break;
					
				case df:
					// e.g. df(f(var),var) from MPReduce becomes f'(var)
					// see http://www.geogebra.org/trac/ticket/1420
					String expStr = args.getListElement(0).toString();
					int nameEnd = expStr.indexOf('(');
					String funLabel = nameEnd > 0 ? expStr.substring(0, nameEnd) : expStr;
					
					// derivative of f gives f'
					ExpressionNode derivative = new ExpressionNode(kernel,
								new Variable(kernel, funLabel),	// function label "f"
								Operation.DERIVATIVE, 
								new MyDouble(kernel, 1));
					// function of given variable gives f'(t)
					ret = new ExpressionNode(kernel, 
							derivative, 
							Operation.FUNCTION, 
							args.getListElement(1)); // Variable "t"
					break;
			}
			
			// no match or ExpressionNode
			if (ret == null || ret instanceof ExpressionNode)
				return (ExpressionNode) ret;
			else
				// create ExpressionNode
				return new ExpressionNode(kernel, ret);
		}
		catch (IllegalArgumentException e) {
			// No enum const for cmdName
		}
		catch (Exception e) {
			System.err.println("CommandDispatcherMPReduce: error when processing command: " + cmdName + ", " + args);
		}
		
		// exception
		return null;
	}

}
