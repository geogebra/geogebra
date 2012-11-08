package geogebra.common.kernel.parser.cashandlers;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.arithmetic.Equation;
import geogebra.common.kernel.arithmetic.ExpressionNode;
import geogebra.common.kernel.arithmetic.ExpressionValue;
import geogebra.common.kernel.arithmetic.FunctionVariable;
import geogebra.common.kernel.arithmetic.GetItem;
import geogebra.common.kernel.arithmetic.MyDouble;
import geogebra.common.kernel.arithmetic.MyList;
import geogebra.common.kernel.arithmetic.MyNumberPair;
import geogebra.common.kernel.arithmetic.Traversing.VariableReplacer;
import geogebra.common.kernel.arithmetic.Variable;
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
		arbcomplex(Operation.ARBCOMPLEX),
		/** arbitrary constant*/
		arbconst(Operation.ARBCONST),
		/** arbitrary integer (comes from trig equations)*/
		arbint(Operation.ARBINT),
		/** derivative*/
		df(Operation.DERIVATIVE),
		/** internal addition -- need to parse that if we used keepinput*/
		addition (Operation.PLUS),
		/** internal multiplication -- need to parse that if we used keepinput*/
		multiplication (Operation.MULTIPLY),
		/** function application (i.e. for lists)*/
		applyfunction (Operation.FUNCTION),
		/** internal subtraction -- need to parse that if we used keepinput*/
		subtraction (Operation.MINUS),
		/** logb */
		logb(Operation.LOGB),
		/** sine integral */
		si(Operation.SI),
		/** cosine integral */
		ci(Operation.CI),
		/** cosine integral */
		ei(Operation.EI),
		/** symbolic x coord*/
		xscoord(Operation.XCOORD),
		/** symbolic y coord*/
		yscoord(Operation.YCOORD),
		/** symbolic z coord*/
		zscoord(Operation.ZCOORD),
		/** taylor(sin(x),0,3) returns taylor(x-x^3/6,x,0,3)*/
		taylor(Operation.NO_OPERATION),
		//we should also have int here, but the name clashes with Java's int
		/** sub(x=y,int(x,x)) */
		sub(Operation.SUBSTITUTION),
		/** interval returned from Solve[x^2<4]*/
		ggbinterval(Operation.NO_OPERATION),
		/** division */
		mydivision(Operation.DIVIDE),
		/** power*/
		mypower(Operation.POWER),
		/** power*/
		myround(Operation.ROUND),
		/** two way conditional function */
		ifelsefun(Operation.NO_OPERATION),
		/** conditional function */
		iffun(Operation.NO_OPERATION),
		/** symbolic sum */
		sum(Operation.SUM),
		/** imaginary part */
		impart(Operation.IMAGINARY),
		/** real part */
		repart(Operation.REAL),
		/** realpart that can handle points */
		myreal(Operation.REAL),
		/** equal operator */
		sequal(Operation.EQUAL_BOOLEAN),
		/** not equal operator */
		sunequal(Operation.NOT_EQUAL),
		/** and operator */
		sand(Operation.AND),
		/** or operator */
		sor(Operation.OR),
		/** implication operator */
		simplies(Operation.IMPLICATION),
		/** not operator */
		snot(Operation.NOT),
		/** > operator */
		sgreater(Operation.GREATER),
		/** >= operator */
		sgreaterequal(Operation.GREATER_EQUAL),
		/** < operator */
		sless(Operation.LESS),
		/** <= operator */
		slessequal(Operation.LESS_EQUAL),
		
		;
		private Operation op;
		private commands(Operation op){
			this.op = op;
		}
		/**
		 * @return single variable operation
		 */
		public Operation getOperation(){
			return op;
		}
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
	public static ExpressionNode processCommand(String cmdName, GetItem args) {

		try {
			ExpressionValue ret = null;
			Kernel kernel = args.getKernel();
			//TODO -- template is not important for arb*, but is this correct for df?
			StringTemplate tpl = StringTemplate.casTemplate;
			
			if("int".equals(cmdName)){
				ret = new ExpressionNode(kernel,args.getItem(0),Operation.INTEGRAL,args.getItem(1));
			}
			else switch (commands.valueOf(cmdName)) {
			//Sum[sin(sin(a)),a,b,c]
			case sum:
				ret = new ExpressionNode(kernel,
						new MyNumberPair(kernel,args.getItem(0),args.getItem(1)),
						Operation.SUM,
						new MyNumberPair(kernel,args.getItem(2),args.getItem(3))
						);
				
				break;
			case ifelsefun:
				MyNumberPair branches = new MyNumberPair(kernel,args.getItem(1),args.getItem(2));
				ret = new ExpressionNode(kernel,
				args.getItem(0),Operation.IF_ELSE,
				branches);
						break;							
			case ggbinterval:
				int type = (int) args.getItem(3).evaluateNum().getDouble();
				boolean leftClosed = type >1;
				boolean rightClosed = type%2==1;
				ExpressionNode leftBound = new ExpressionNode(kernel,args.getItem(0),leftClosed?Operation.GREATER_EQUAL:Operation.GREATER,args.getItem(1)),
						rightBound = new ExpressionNode(kernel,args.getItem(0),rightClosed?Operation.LESS_EQUAL:Operation.LESS,args.getItem(2));
				if(args.getItem(2).evaluateNum().getDouble()==Double.POSITIVE_INFINITY){
					ret = leftBound;
				}else if(args.getItem(1).evaluateNum().getDouble()==Double.NEGATIVE_INFINITY){
					ret = rightBound;
				}else {ret = new ExpressionNode(kernel,
						leftBound,
						Operation.AND,
						rightBound
						);
				}
				break;
			case taylor:
				ret = args.getItem(0);
				break;
			case logb:
				// e.g. logb[x,3] becomes log(3,x)
				ret = new ExpressionNode(kernel,
						 args.getItem(1),Operation.LOGB,
								args.getItem(0));
				break;
			case arbcomplex:
			case arbconst:
			case arbint:
			case ci:
			case si:
			case ei:
			case impart:
			case repart:	
			case xscoord:
			case yscoord:
			case zscoord:
			case snot:	
			case myround:
			case myreal:
				// e.g. logb[x,3] becomes log(3,x)
				ret = new ExpressionNode(kernel,
						 args.getItem(0).unwrap(),commands.valueOf(cmdName).getOperation(),
								null);
				break;
			case sub:	
				if(args.getItem(1).isExpressionNode()&& ((ExpressionNode)args.getItem(1)).getOperation()==Operation.INTEGRAL){
					String var = ((ExpressionNode)args.getItem(1)).getRight().toString(StringTemplate.defaultTemplate);
					FunctionVariable fv =new FunctionVariable(kernel,"t");
					VariableReplacer rep = VariableReplacer.getReplacer(var, fv);
					args.getItem(1).traverse(rep); 
					((Equation)((ExpressionNode)args.getItem(0)).getLeft()).setLHS(new ExpressionNode(kernel,fv));
				}
				// e.g. addition[x,3] becomes x + 3
				ret = new ExpressionNode(kernel,
						 args.getItem(0),commands.valueOf(cmdName).getOperation(),
						 args.getItem(1));
				break;
			case iffun:	
			case multiplication:
			case subtraction:
			case addition:
			case applyfunction:	
			case mydivision:	
			case sand:
			case sor:
			case simplies:
			case sgreater:
			case sless:
			case sequal:
			case sunequal:
			case sgreaterequal:
			case slessequal:
			case mypower:
				// e.g. addition[x,3] becomes x + 3
				ret = new ExpressionNode(kernel,
						 args.getItem(0),commands.valueOf(cmdName).getOperation(),
						 args.getItem(1));
				break;
			case df:
				// e.g. df(f(var),var) from MPReduce becomes f'(var)
				// see http://www.geogebra.org/trac/ticket/1420
				String expStr = args.getItem(0).toString(tpl);
				int nameEnd = expStr.indexOf('(');
				String funLabel = nameEnd > 0 ? expStr.substring(0, nameEnd)
						: expStr;

				// derivative of f gives f'
				ExpressionNode derivative = new ExpressionNode(kernel,
						new Variable(kernel, funLabel), // function label "f"
						Operation.DERIVATIVE, new MyDouble(kernel, 1));
				// function of given variable gives f'(t)
				ret = new ExpressionNode(kernel, derivative,
						Operation.FUNCTION, args.getItem(1)); // Variable
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
			e.printStackTrace();
			System.err
					.println("CommandDispatcherMPReduce: error when processing command: "
							+ cmdName + ", " + args);
		}

		// exception
		return null;
	}

}
