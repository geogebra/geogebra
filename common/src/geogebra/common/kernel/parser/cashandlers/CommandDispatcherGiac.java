package geogebra.common.kernel.parser.cashandlers;

import geogebra.common.kernel.CASException;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.arithmetic.ExpressionNode;
import geogebra.common.kernel.arithmetic.ExpressionValue;
import geogebra.common.kernel.arithmetic.GetItem;
import geogebra.common.kernel.arithmetic.MyDouble;
import geogebra.common.kernel.arithmetic.MyNumberPair;
import geogebra.common.kernel.arithmetic.Variable;
import geogebra.common.main.App;
import geogebra.common.plugin.Operation;

/**
 * Handles special Giac commands to distinguish them from user defined
 * functions in the Parser.
 * 
 * Adapted from CommandDispatcherMPReduce
 * 
 * @author Michael
 */
public class CommandDispatcherGiac {

	/**
	 * Enum for special commands that may be returned by MPReduce.
	 */
	public enum commands {
		/** gamma regularized */
		igamma(Operation.NO_OPERATION),
		/** derivative*/
		diff(Operation.DERIVATIVE),
		/** derivative*/
		integrate(Operation.INTEGRAL),
		/** exact (convert to fraction)*/
		exact(Operation.NO_OPERATION),
		/** psi */
		Psi(Operation.PSI),
		/** sine integral */
		Si(Operation.SI),
		/** cosine integral */
		Ci(Operation.CI),		
		/** exp integral */
		Ei(Operation.EI),
		/** Reimann-Zeta function */
		Zeta(Operation.ZETA),
		/** Beta function */
		Beta(Operation.NO_OPERATION),
		/** Gamma function */
		Gamma(Operation.GAMMA),
		/** fractional part */
		fPart(Operation.FRACTIONAL_PART),
		/** fractional part */
		conj(Operation.CONJUGATE),
		/** imaginary part */
		im(Operation.IMAGINARY),
		/** real part */
		re(Operation.REAL),
		/** surd(a,b)=bth root of a */
		surd(Operation.NROOT),
		/** sqrt */
		sqrt(Operation.SQRT),

		/** sine */
		sin(Operation.SIN),
		/** cosine */
		cos(Operation.COS),
		/** tan */
		tan(Operation.TAN),
		/** asin */
		asin(Operation.ARCSIN),
		/** acos */
		acos(Operation.ARCCOS),
		/** atan */
		atan(Operation.ARCTAN),

		/** hyperbolic sine */
		sinh(Operation.SINH),
		/** hyperbolic cos */
		cosh(Operation.COSH),
		/** hyperbolic tan */
		tanh(Operation.TANH),

		
		/** sec */
		sec(Operation.SEC),
		/** cosec */
		csc(Operation.CSC),
		/** cot */
		cot(Operation.COT),

		/** ln */
		ln(Operation.LOG),
		/** exp */
		exp(Operation.EXP),
		/** erf */
		erf(Operation.ERF),

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
	 *            name of the Giac command to process, see
	 *            CommandDispatcherGiac.commands
	 * @param args
	 *            list of command arguments
	 */
	public static ExpressionNode processCommand(String cmdName, GetItem args) {

		try {
			ExpressionValue ret = null;
			Kernel kernel = args.getKernel();
			//TODO -- template is not important for arb*, but is this correct for diff?
			StringTemplate tpl = StringTemplate.casTemplate;

			switch (commands.valueOf(cmdName)) {

			case exact:
				// just return argument
				ret = new ExpressionNode(kernel, args.getItem(0));
				break;
			case Psi:
				if (args.getLength() == 1) {
					// Psi(x) -> psi(x)
					ret = new ExpressionNode(kernel,
							args.getItem(0),Operation.PSI,
							null);

				} else {
					// swap arguments
					// e.g. Psi(x,3) -> polyGamma(3,x)
					ret = new ExpressionNode(kernel,
							args.getItem(1),Operation.POLYGAMMA,
							args.getItem(0));
				}
				break;

			case Ci:
			case Si:
			case Ei:
			case Zeta:
			case fPart:
			case Gamma:	
			case conj:
			case re:
			case im:
			case sin:
			case cos:
			case tan:
			case asin:
			case acos:
			case atan:
			case sinh:
			case cosh:
			case tanh:
			case sec:
			case csc:
			case cot:
			case ln:
			case exp:
			case erf:

				
				if (args.getLength() != 1) {
				
					// eg Derivative[zeta(x)] -> Zeta(1,x) which GeoGebra doesn't support
					ret = new ExpressionNode(kernel, Double.NaN);
				} else {
				
					ret = new ExpressionNode(kernel,
							args.getItem(0),commands.valueOf(cmdName).getOperation(), null);
				}
				break;
			case igamma:	
				if (args.getLength() == 2) {
					ret = new ExpressionNode(kernel,
							args.getItem(0),Operation.GAMMA_INCOMPLETE,
							args.getItem(1));
				} else { // must be 3

					// discard 3rd arg (dummy to flag regularized)
					ret = new ExpressionNode(kernel,
							args.getItem(0),Operation.GAMMA_INCOMPLETE_REGULARIZED,
							args.getItem(1));
				}
				break;
			case surd:	
				if (args.getLength() == 2) {
					
					ExpressionValue arg1 = args.getItem(1);
					double arg1Num = arg1.evaluateNum().getDouble();
					
					if (arg1Num == 3) {
						ret = new ExpressionNode(kernel,
								args.getItem(0),Operation.CBRT,
								null);
					} else if (arg1Num == 2) {
						ret = new ExpressionNode(kernel,
								args.getItem(0),Operation.SQRT,
								null);
					} else {
					
						ret = new ExpressionNode(kernel,
								args.getItem(0),Operation.NROOT,
								arg1);
					}
				} else { 
					throw new CASException("Giac: bad number of args for surd:"+args.getLength());
				}
				break;

			case Beta:	
				switch (args.getLength()) {

				default:
					throw new CASException("Giac: bad number of args for beta:"+args.getLength());
				case 2:
					ret = new ExpressionNode(kernel,
							args.getItem(0),Operation.BETA,
							args.getItem(1));
					
					break;

				case 3:
					MyNumberPair np = new MyNumberPair(kernel,args.getItem(1), args.getItem(2));
					ret = new ExpressionNode(kernel,
							args.getItem(0),Operation.BETA_INCOMPLETE, np);

					break;

				case 4:
					// 4th argument is dummy to flag "regularized"
					np = new MyNumberPair(kernel,args.getItem(1), args.getItem(2));
					ret = new ExpressionNode(kernel,
							args.getItem(0),Operation.BETA_INCOMPLETE_REGULARIZED, np);
					break;

				}
				break;

			case integrate:
				// eg Integral[exp(x^3)]
				ret = new ExpressionNode(kernel, Double.NaN);
				break;
			case diff:
				// e.g. diff(f(var),var) from Giac becomes f'(var)
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
		} catch (Exception e) {
			e.printStackTrace();
			App.error("CommandDispatcherGiac: error when processing command: "
					+ cmdName + ", " + args);
		}

		// exception
		return null;
	}

}
