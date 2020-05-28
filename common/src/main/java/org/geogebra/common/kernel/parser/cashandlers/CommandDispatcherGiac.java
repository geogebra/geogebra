package org.geogebra.common.kernel.parser.cashandlers;

import org.geogebra.common.kernel.CASException;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.arithmetic.Equation;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.arithmetic.GetItem;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.arithmetic.MyList;
import org.geogebra.common.kernel.arithmetic.MyNumberPair;
import org.geogebra.common.kernel.arithmetic.MyVecNode;
import org.geogebra.common.kernel.arithmetic.ValidExpression;
import org.geogebra.common.kernel.arithmetic3D.MyVec3DNode;
import org.geogebra.common.kernel.commands.CmdIf;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.matrix.Coords;
import org.geogebra.common.plugin.Operation;
import org.geogebra.common.util.debug.Log;

/**
 * Handles special Giac commands to distinguish them from user defined functions
 * in the Parser.
 * 
 * Adapted from CommandDispatcherMPReduce
 * 
 * @author Michael
 */
public class CommandDispatcherGiac {

	/**
	 * Enum for special commands that may be returned by Giac.
	 */
	public enum GiacCommands {
		/** when aka If[] */
		when(Operation.NO_OPERATION),
		/** gamma regularized */
		igamma(Operation.NO_OPERATION),
		/** derivative */
		diff(Operation.DERIVATIVE),
		/** bounded_function */
		bounded_function(Operation.NO_OPERATION),
		/** integral */
		integrate(Operation.INTEGRAL),
		/** rootof */
		rootof(Operation.NO_OPERATION),
		/** irem */
		irem(Operation.NO_OPERATION),
		/** rem */
		rem(Operation.NO_OPERATION),
		/** irem */
		iquo(Operation.NO_OPERATION),
		/** irem */
		quo(Operation.NO_OPERATION),
		/** exact (convert to fraction) */
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
		/** eg alt(x) returns as ggbalt(x) */
		ggbalt(Operation.ALT),
		/** sqrt */
		sqrt(Operation.SQRT),
		/** sign */
		sign(Operation.SGN),

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
		/** abs */
		abs(Operation.ABS),
		/** erf */
		erf(Operation.ERF),
		/** symbolic x coord */
		xcoord(Operation.XCOORD),
		/** symbolic y coord */
		ycoord(Operation.YCOORD),
		/** symbolic z coord */
		zcoord(Operation.ZCOORD),
		/** symbolic x coord */
		xcoordsymb(Operation.XCOORD),
		/** symbolic y coord */
		ycoordsymb(Operation.YCOORD),
		/** symbolic z coord */
		zcoordsymb(Operation.ZCOORD),
		/** alt(x) */
		altsymb(Operation.ALT),
		/** LambertW */
		LambertW(Operation.LAMBERTW),

		/** GeoGebra vector */
		ggbvect(Operation.NO_OPERATION),

		/** symbolic sum */
		sum(Operation.SUM),

		/** inverse for Normal(0,1,x) */
		normal_icdf(Operation.INVERSE_NORMAL),

		/** If[] */
		piecewise(Operation.IF_ELSE),

		/** to return text from Giac eg from ToBase() "1010011_{2}" */
		ggbText(Operation.NO_OPERATION),

		/**
		 * eg hyperplan({3,5,-1},point[0,0,-37/10])
		 */
		point(Operation.NO_OPERATION),
		/**
		 * returned from plane(4*x + 3*y + z = 1)
		 * 
		 * eg hyperplan({3,5,-1},point[0,0,-37/10]) hyperplan({3,5,-1},{0,0,1})
		 */
		hyperplan(Operation.NO_OPERATION),

		/** if returned from Giac -> error */
		laplace(Operation.NO_OPERATION),

		/** if returned from Giac -> error */
		det(Operation.NO_OPERATION),

		/** if returned from Giac -> error */
		det_minor(Operation.NO_OPERATION),

		/** if returned from Giac -> error */
		ilaplace(Operation.NO_OPERATION),

		/** if returned from Giac -> error */
		invlaplace(Operation.NO_OPERATION),

		/** returned by eg BinomialDist[72,1/7,n,true] -> error */
		binomial_cdf(Operation.NO_OPERATION),

		/** eg binomial_icdf(23,0.9285714285714,-9.999988812822E-013) */
		binomial_icdf(Operation.NO_OPERATION),

		/** if returned from Giac -> error */
		fisher_cdf(Operation.NO_OPERATION),

		/** if returned from Giac -> error */
		normald_cdf(Operation.NO_OPERATION),

		/** if returned from Giac -> error */
		student_cdf(Operation.NO_OPERATION),

		/** if returned from Giac -> error */
		chisquare_cdf(Operation.NO_OPERATION),

		/** polar coordinate */
		ggb_ang(Operation.NO_OPERATION),

		/** if returned from Giac -> error */
		poly1(Operation.NO_OPERATION),
		/** if returned from Giac -> error */
		tran(Operation.NO_OPERATION),
		/** if returned from Giac -> error */
		jordan(Operation.NO_OPERATION),

		/** fsolve, shouldn't get returned */
		fsolve(Operation.NO_OPERATION),
		/** solve, shouldn't get returned */
		solve(Operation.NO_OPERATION),

		/** arbitrary constant */
		arbconst(Operation.ARBCONST),
		/** arbitrary integer (comes from trig equations) */
		arbint(Operation.ARBINT),
		/** floor */
		floor(Operation.FLOOR),
		/** ceiling */
		ceiling(Operation.CEIL),
		/** rand(n) gives random integer from [0,n-1] */
		rand(Operation.NO_OPERATION),

		;
		private Operation op;

		private GiacCommands(Operation op) {
			this.op = op;
		}

		/**
		 * @return single variable operation
		 */
		public Operation getOperation() {
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
	 * @param kernel
	 *            kernel
	 */
	public static ExpressionNode processCommand(String cmdName, GetItem args,
			Kernel kernel) {
		GiacCommands cmd = null;
		if (cmdName == null || cmdName.startsWith(Kernel.TMP_VARIABLE_PREFIX)) {
			return null;
		}
		try {
			cmd = GiacCommands.valueOf(cmdName);
		} catch (Exception Ex) {
			Ex.printStackTrace();
			Log.error(
					"Unknown CAS command " + cmdName + ", arguments: " + args);
			return null;
		}
		try {
			ExpressionValue ret = null;
			switch (cmd) {

			case sum:
				ret = new ExpressionNode(kernel,
						new MyNumberPair(kernel, args.getItem(0),
								args.getItem(1)),
						Operation.SUM, new MyNumberPair(kernel, args.getItem(2),
								args.getItem(3)));

				break;
			case piecewise:

				if (args.getLength() < 3) {
					// eg Integral[Function[x^2,-2,3]]
					return new ExpressionNode(kernel, Double.NaN);
				}

				return CmdIf.expandIf(kernel, args);

			case exact:
				// just return argument
				ret = new ExpressionNode(kernel, args.getItem(0));
				break;
			case LambertW:
				return null; //fallback to GGB parser
			case Psi:
				if (args.getLength() == 1) {
					// Psi(x) -> psi(x)
					ret = new ExpressionNode(kernel, args.getItem(0),
							Operation.PSI, null);

				} else {
					// swap arguments
					// e.g. Psi(x,3) -> polyGamma(3,x)
					ret = new ExpressionNode(kernel, args.getItem(1),
							Operation.POLYGAMMA, args.getItem(0));
				}
				break;

			case ggbText:
				if (args.getLength() == 1) {
					return args.getItem(0).wrap();
				}

				throw new CASException("Giac: bad number of args for text(): "
						+ args.getLength());

			case point:
				switch (args.getLength()) {
				case 2:
					double a = args.getItem(0).evaluateDouble();
					double b = args.getItem(1).evaluateDouble();
					return new ExpressionNode(kernel,
							new GeoPoint(kernel.getConstruction(), a, b, 1));
				case 3:
					a = args.getItem(0).evaluateDouble();
					b = args.getItem(1).evaluateDouble();
					double c = args.getItem(2).evaluateDouble();
					GeoElementND point = kernel.getManager3D().point3D(a, b, c,
							false);
					return new ExpressionNode(kernel, point);
				default:
					throw new CASException(
							"Giac: bad number of args for point(): "
									+ args.getLength());
				}

			case hyperplan:

				switch (args.getLength()) {
				case 2:

					ExpressionValue item0 = args.getItem(0).unwrap();
					ExpressionValue item1 = args.getItem(1).unwrap();

					if (!(item0 instanceof MyList)) {
						Log.error("wrong class: " + item0.getClass());
						return new ExpressionNode(kernel, Double.NaN);
					}

					MyList list1 = (MyList) item0;
					double a = list1.getListElement(0).evaluateDouble();
					double b = list1.getListElement(1).evaluateDouble();
					double c = list1.getListElement(2).evaluateDouble();
					double constant;

					if (item1.isGeoElement()
							&& ((GeoElement) item1).isGeoPoint()) {
						// hyperplan({3,5,-1},point[0,0,-37/10])

						GeoPointND point = (GeoPointND) item1;
						Coords coords = point.getInhomCoordsInD3();

						constant = a * coords.get(1) + b * coords.get(2)
								+ c * coords.get(3);

					} else if (item1 instanceof MyList) {

						MyList list2 = (MyList) item1;

						double d = list2.getListElement(0).evaluateDouble();
						double e = list2.getListElement(1).evaluateDouble();
						double f = list2.getListElement(2).evaluateDouble();

						if (f != 0) {
							constant = f * c;
						} else if (e != 0) {
							// 1000x+100y+0z=3
							// hyperplan({1000,100,0},{0,3/100,0})
							constant = e * b;
						} else {
							constant = d * a;
						}

					} else {
						Log.error("wrong class: " + item0.getClass());
						return new ExpressionNode(kernel, Double.NaN);
					}

					ExpressionNode expX = new ExpressionNode(kernel,
							new FunctionVariable(kernel, "x")).multiply(a);
					ExpressionNode expY = new ExpressionNode(kernel,
							new FunctionVariable(kernel, "y")).multiply(b);
					ExpressionNode expZ = new ExpressionNode(kernel,
							new FunctionVariable(kernel, "z")).multiply(c);

					ExpressionNode rhs = new ExpressionNode(kernel,
							new MyDouble(kernel, constant));

					ExpressionNode sum = expX.plus(expY).plus(expZ);

					Equation eq = new Equation(kernel, sum, rhs);

					return new ExpressionNode(kernel, eq);

				default:
					throw new CASException(
							"Giac: bad number of args for hyperplan(): "
									+ args.getLength());
				}

			case ggbvect:

				ValidExpression vec;

				switch (args.getLength()) {
				case 2:

					vec = new MyVecNode(kernel, args.getItem(0),
							args.getItem(1));
					((MyVecNode) vec).setupCASVector();
					break;
				case 3:
					vec = new MyVec3DNode(kernel, args.getItem(0),
							args.getItem(1), args.getItem(2));
					((MyVec3DNode) vec).setupCASVector();
					break;

				default:
					throw new CASException(
							"Giac: bad number of args for ggbvect(): "
									+ args.getLength());

				}

				ret = new ExpressionNode(kernel, vec, Operation.NO_OPERATION,
						null);

				break;

			case arbint:
			case arbconst:
			case Ci:
			case Si:
			case Ei:
			case Zeta:
			case fPart:
			case Gamma:
			case conj:
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
			case abs:
			case xcoord:
			case ycoord:
			case zcoord:
			case xcoordsymb:
			case ycoordsymb:
			case zcoordsymb:
			case altsymb:
			case sqrt:
			case ggbalt:
			case sign:
			case floor:
			case ceiling:
			case normal_icdf:
				if (args.getLength() != 1) {

					// eg Derivative[zeta(x)] -> Zeta(1,x) which GeoGebra
					// doesn't support
					ret = new ExpressionNode(kernel, Double.NaN);
				} else {

					ret = new ExpressionNode(kernel, args.getItem(0),
							GiacCommands.valueOf(cmdName).getOperation(), null);
				}
				break;
			case im:

				if (args.getItem(0).unwrap() instanceof Command) {
					String cmdname = ((Command) args.getItem(0).unwrap())
							.getName();
					if (cmdname.startsWith(Kernel.TMP_VARIABLE_PREFIX) && kernel
							.lookupLabel(Kernel.removeCASVariablePrefix(
									cmdname)) == null) {
						ret = new MyDouble(kernel).wrap();
						break;
					}
				}

				ret = new ExpressionNode(kernel, args.getItem(0),
						Operation.IMAGINARY, null);
				break;
			case re:

				if (args.getItem(0).unwrap() instanceof Command) {
					String cmdname = ((Command) args.getItem(0).unwrap())
							.getName();
					if (cmdname.startsWith(Kernel.TMP_VARIABLE_PREFIX) && kernel
							.lookupLabel(Kernel.removeCASVariablePrefix(
									cmdname)) == null) {
						ret = args.getItem(0);
						break;
					}
				}

				ret = new ExpressionNode(kernel, args.getItem(0),
						Operation.REAL, null);
				break;
			case ggb_ang:
				ret = new MyVecNode(kernel);
				((MyVecNode) ret).setPolarCoords(args.getItem(0),
						args.getItem(1));
				break;

			case igamma:
				if (args.getLength() == 2) {
					ret = new ExpressionNode(kernel, args.getItem(0),
							Operation.GAMMA_INCOMPLETE, args.getItem(1));
				} else { // must be 3

					// discard 3rd arg (dummy to flag regularized)
					ret = new ExpressionNode(kernel, args.getItem(0),
							Operation.GAMMA_INCOMPLETE_REGULARIZED,
							args.getItem(1));
				}
				break;
			case when:
				if (args.getLength() == 2) {
					// shouldn't get 2 arguments, but just in case
					ret = new ExpressionNode(kernel, args.getItem(0),
							Operation.IF, args.getItem(1));
				} else if (args.getLength() == 3) {

					ExpressionValue Else = args.getItem(2);

					if ("?".equals(
							Else.toString(StringTemplate.defaultTemplate))) {
						ret = new ExpressionNode(kernel, args.getItem(0),
								Operation.IF, args.getItem(1));
					} else {

						ret = new ExpressionNode(kernel, args.getItem(0),
								Operation.IF_ELSE, new MyNumberPair(kernel,
										args.getItem(1), Else));
					}
				} else {
					throw new CASException("Giac: bad number of args for when:"
							+ args.getLength());
				}
				break;
			case surd:
				if (args.getLength() == 2) {

					ExpressionValue arg1 = args.getItem(1);
					double arg1Num = arg1.evaluateDouble();

					if (arg1Num == 3) {
						ret = new ExpressionNode(kernel, args.getItem(0),
								Operation.CBRT, null);
					} else if (arg1Num == 2) {
						ret = new ExpressionNode(kernel, args.getItem(0),
								Operation.SQRT, null);
					} else {

						ret = new ExpressionNode(kernel, args.getItem(0),
								Operation.NROOT, arg1);
					}
				} else {
					throw new CASException("Giac: bad number of args for surd:"
							+ args.getLength());
				}
				break;

			case Beta:
				switch (args.getLength()) {

				default:
					throw new CASException("Giac: bad number of args for beta:"
							+ args.getLength());
				case 2:
					ret = new ExpressionNode(kernel, args.getItem(0),
							Operation.BETA, args.getItem(1));

					break;

				case 3:
					MyNumberPair np = new MyNumberPair(kernel, args.getItem(1),
							args.getItem(2));
					ret = new ExpressionNode(kernel, args.getItem(0),
							Operation.BETA_INCOMPLETE, np);

					break;

				case 4:
					// 4th argument is dummy to flag "regularized"
					np = new MyNumberPair(kernel, args.getItem(1),
							args.getItem(2));
					ret = new ExpressionNode(kernel, args.getItem(0),
							Operation.BETA_INCOMPLETE_REGULARIZED, np);
					break;

				}
				break;

			case rootof: // rootof should get removed by evalfa()
				Log.warn("'rootof()' returned from giac");
				ret = new ExpressionNode(kernel, Double.NaN);
				break;
			case quo:
			case iquo:
			case rem:
			case irem:
			case binomial_cdf:
			case binomial_icdf:
			case fisher_cdf:
			case normald_cdf:
			case student_cdf:
			case chisquare_cdf:
			case det:
			case det_minor:
			case laplace:
			case ilaplace:
			case invlaplace:
			case fsolve:
			case solve:
			case jordan:
			case tran:
			case poly1: // eg ggbtmpvarp = (ggbtmpvarz)+(((1,2))*(ggbtmpvarz))
			case integrate: // eg Integral[exp(x^3)]
			case bounded_function: // eg Limit[cos(x),infinity]
			case rand: // eg RandomBetween[0, undefined variable]
				ret = new ExpressionNode(kernel, Double.NaN);
				break;

			case diff:

				if (args.getLength() == 3 && !"1".equals(args.getItem(2)
						.toString(StringTemplate.giacTemplate))) {
					return new ExpressionNode(kernel,
							new MyNumberPair(kernel, args.getItem(0),
									args.getItem(1)),
							Operation.DIFF, args.getItem(2));
				}
				if (ExpressionNode.isConstantDouble(args.getItem(0), 0)) {
					return new ExpressionNode(kernel, 0);
				}
				ret = new ExpressionNode(kernel, args.getItem(0),
						Operation.DIFF, args.getItem(1));
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
			Log.error("CommandDispatcherGiac: error when processing command: "
								+ cmdName + ", " + args);
		}

		// exception, eg Derivative[f(x)+g(x)]
		return null;
	}

}
