package geogebra.common.plugin;

import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.arithmetic.BooleanValue;
import geogebra.common.kernel.arithmetic.ExpressionNode;
import geogebra.common.kernel.arithmetic.ExpressionNodeConstants;
import geogebra.common.kernel.arithmetic.ExpressionNodeEvaluator;
import geogebra.common.kernel.arithmetic.ExpressionValue;
import geogebra.common.kernel.arithmetic.Functional;
import geogebra.common.kernel.arithmetic.ListValue;
import geogebra.common.kernel.arithmetic.MyBoolean;
import geogebra.common.kernel.arithmetic.MyDouble;
import geogebra.common.kernel.arithmetic.MyList;
import geogebra.common.kernel.arithmetic.MyNumberPair;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.arithmetic.Polynomial;
import geogebra.common.kernel.arithmetic.Term;
import geogebra.common.kernel.arithmetic.TextValue;
import geogebra.common.kernel.arithmetic.VectorValue;
import geogebra.common.kernel.arithmetic3D.Vector3DValue;
import geogebra.common.kernel.geos.GeoCurveCartesian;
import geogebra.common.kernel.geos.GeoLine;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoVec2D;
import geogebra.common.kernel.geos.ParametricCurve;

@SuppressWarnings("javadoc")
public enum Operation {
	NO_OPERATION {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {
			return null;
		}
	},
	NOT_EQUAL {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {
			MyBoolean b = ExpressionNodeEvaluator.evalEquals(lt.getKernel(),
					lt, rt);
			if (b == null) {
				return ev.illegalComparison(lt, rt,
						ExpressionNodeConstants.strNOT_EQUAL);
			}
			b.setValue(!b.getBoolean());
			return b;
		}
	},
	NOT {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {
			if (lt.isBooleanValue()) {
				MyBoolean bool = ((BooleanValue) lt).getMyBoolean();
				bool.setValue(!bool.getBoolean());
				return bool;
			}
			return ev.illegalBoolean(lt, ExpressionNodeConstants.strNOT);
		}
	},
	OR {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {
			if (lt.isBooleanValue() && rt.isBooleanValue()) {
				MyBoolean bool = ((BooleanValue) lt).getMyBoolean();
				bool.setValue(bool.getBoolean()
						|| ((BooleanValue) rt).getBoolean());
				return bool;
			}
			return ev.illegalBinary(lt, rt, "IllegalBoolean",
					ExpressionNodeConstants.strNOT);
		}
	},
	AND {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {
			if (lt.isBooleanValue() && rt.isBooleanValue()) {
				MyBoolean bool = ((BooleanValue) lt).getMyBoolean();
				bool.setValue(bool.getBoolean()
						&& ((BooleanValue) rt).getBoolean());
				return bool;
			}
			return ev.illegalBinary(lt, rt, "IllegalBoolean",
					ExpressionNodeConstants.strNOT);
		}
	},
	AND_INTERVAL {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {
			return AND.handle(ev, lt, rt, left, right, tpl, holdsLaTeX);
		}
	},
	IMPLICATION {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {
			if (lt.isBooleanValue() && rt.isBooleanValue()) {
				MyBoolean bool = ((BooleanValue) lt).getMyBoolean();
				bool.setValue(!bool.getBoolean()
						|| ((BooleanValue) rt).getBoolean());
				return bool;
			}
			return ev.illegalBinary(lt, rt, "IllegalBoolean",
					ExpressionNodeConstants.strNOT);
		}
	},
	EQUAL_BOOLEAN {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {
			MyBoolean b = ExpressionNodeEvaluator.evalEquals(lt.getKernel(),
					lt, rt);
			if (b == null) {
				return ev.illegalComparison(lt, rt,
						ExpressionNodeConstants.strNOT_EQUAL);
			}
			return b;
		}
	},
	LESS {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {
			if (lt.isNumberValue() && rt.isNumberValue()) {
				return new MyBoolean(lt.getKernel(), Kernel.isGreater(
						((NumberValue) rt).getDouble(),
						((NumberValue) lt).getDouble()));
			}
			if (lt.isTextValue() && rt.isTextValue()) {
				int comp = ((TextValue) lt).toValueString(tpl).compareTo(
						((TextValue) rt).toValueString(tpl));
				return new MyBoolean(lt.getKernel(), comp < 0);
			}
			return ev.illegalComparison(lt, rt, "<");
		}
	},
	GREATER {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {
			if (lt.isNumberValue() && rt.isNumberValue()) {
				return new MyBoolean(lt.getKernel(), Kernel.isGreater(
						((NumberValue) lt).getDouble(),
						((NumberValue) rt).getDouble()));
			}
			if (lt.isTextValue() && rt.isTextValue()) {
				int comp = ((TextValue) lt).toValueString(tpl).compareTo(
						((TextValue) rt).toValueString(tpl));
				return new MyBoolean(lt.getKernel(), comp > 0);
			}
			return ev.illegalComparison(lt, rt, ">");
		}
	},
	LESS_EQUAL {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {
			if (lt.isNumberValue() && rt.isNumberValue()) {
				return new MyBoolean(lt.getKernel(), Kernel.isGreaterEqual(
						((NumberValue) rt).getDouble(),
						((NumberValue) lt).getDouble()));
			}
			if (lt.isTextValue() && rt.isTextValue()) {
				int comp = ((TextValue) lt).toValueString(tpl).compareTo(
						((TextValue) rt).toValueString(tpl));
				return new MyBoolean(lt.getKernel(), comp <= 0);
			}
			return ev.illegalComparison(lt, rt,
					ExpressionNodeConstants.strLESS_EQUAL);
		}
	},
	GREATER_EQUAL {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {
			if (lt.isNumberValue() && rt.isNumberValue()) {
				return new MyBoolean(lt.getKernel(), Kernel.isGreaterEqual(
						((NumberValue) lt).getDouble(),
						((NumberValue) rt).getDouble()));
			}
			if (lt.isTextValue() && rt.isTextValue()) {
				int comp = ((TextValue) lt).toValueString(tpl).compareTo(
						((TextValue) rt).toValueString(tpl));
				return new MyBoolean(lt.getKernel(), comp >= 0);
			}
			return ev.illegalComparison(lt, rt,
					ExpressionNodeConstants.strGREATER_EQUAL);
		}
	},
	PARALLEL {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {
			if ((lt instanceof GeoLine) && (rt instanceof GeoLine)) {
				return new MyBoolean(lt.getKernel(),
						((GeoLine) lt).isParallel((GeoLine) rt));
			}
			return ev.illegalComparison(lt, rt,
					ExpressionNodeConstants.strPARALLEL);
		}
	},
	PERPENDICULAR {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {
			if ((lt instanceof GeoLine) && (rt instanceof GeoLine)) {
				return new MyBoolean(lt.getKernel(),
						((GeoLine) lt).isPerpendicular((GeoLine) rt));
			}
			return ev.illegalComparison(lt, rt,
					ExpressionNodeConstants.strPERPENDICULAR);
		}
	},
	IS_ELEMENT_OF {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {
			if (rt.isListValue()) {
				return new MyBoolean(lt.getKernel(), MyList.isElementOf(lt,
						((ListValue) rt).getMyList()));
			}
			return ev.illegalListOp(lt, rt,
					ExpressionNodeConstants.strIS_ELEMENT_OF);
		}
	},
	IS_SUBSET_OF {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {
			if (lt.isListValue() && rt.isListValue()) {
				return new MyBoolean(lt.getKernel(), MyList.listContains(
						((ListValue) rt).getMyList(),
						((ListValue) lt).getMyList(), tpl));
			}
			return ev.illegalListOp(lt, rt,
					ExpressionNodeConstants.strIS_SUBSET_OF);
		}
	},
	IS_SUBSET_OF_STRICT {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {
			if (lt.isListValue() && rt.isListValue()) {
				return new MyBoolean(lt.getKernel(), MyList.listContainsStrict(
						((ListValue) rt).getMyList(),
						((ListValue) lt).getMyList(), tpl));
			}
			return ev.illegalListOp(lt, rt,
					ExpressionNodeConstants.strIS_SUBSET_OF_STRICT);
		}
	},
	SET_DIFFERENCE {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {
			if (lt.isListValue() && rt.isListValue()) {
				return MyList.setDifference(lt.getKernel(),
						((ListValue) lt).getMyList(),
						((ListValue) rt).getMyList());
			}
			return ev.illegalListOp(lt, rt,
					ExpressionNodeConstants.strSET_DIFFERENCE);
		}
	},
	PLUS {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {
			return ev.handlePlus(lt, rt, tpl, holdsLaTeX);

		}
	},
	MINUS {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {
			return ev.handleMinus(lt, rt);

		}
	},
	VECTORPRODUCT {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {
			if (lt.isVectorValue() && rt.isVectorValue()) {
				GeoVec2D vec = ((VectorValue) lt).getVector();
				GeoVec2D vec2 = ((VectorValue) rt).getVector();
				MyDouble num = new MyDouble(lt.getKernel());
				GeoVec2D.vectorProduct(vec, vec2, num);
				return num;
			}
			return ev.illegalBinary(lt, rt, "IllegalMultiplication",
					ExpressionNodeConstants.strVECTORPRODUCT);
		}
	},

	// these next three must be adjacent
	// so that brackets work for eg a/(b/c)
	// and are removed in (a/b)/c
	// see case DIVIDE in ExpressionNode
	MULTIPLY {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {
			return ev.handleMult(lt, rt, tpl, holdsLaTeX);

		}
	},
	MULTIPLY_OR_FUNCTION {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {
			return ev.handleMult(lt, rt, tpl, holdsLaTeX);

		}
	},
	DIVIDE {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {
			return ev.handleDivide(lt, rt, left, right);

		}
	},
	POWER {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {
			return ev.handlePower(lt, rt, right);

		}
	},

	FREEHAND {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {
			if (lt.isNumberValue() && rt.isListValue()) {
				double x = ((NumberValue) lt).getDouble();
				double ret = Double.NaN;
				if (rt.isGeoElement()) {
					GeoList list = (GeoList) rt;
					int n = list.size() - 3;
					if ((n >= 1)
							&& list.getElementType().equals(GeoClass.NUMERIC)) {
						double min = ((NumberValue) (list.get(0))).getDouble();
						double max = ((NumberValue) (list.get(1))).getDouble();

						if ((min > max) || (x > max) || (x < min)) {
							return new MyDouble(lt.getKernel(), Double.NaN);
						}

						double step = (max - min) / n;

						int index = (int) Math.floor((x - min) / step);

						if (index > (n - 1)) {
							ret = ((NumberValue) (list.get(n + 2))).getDouble();
						} else {

							double y1 = ((NumberValue) (list.get(index + 2)))
									.getDouble();
							double y2 = ((NumberValue) (list.get(index + 3)))
									.getDouble();
							double x1 = min + (index * step);

							// linear interpolation between (x1,y1) and
							// (x2,y2+step) to give (x,ret)
							ret = y1 + (((x - x1) * (y2 - y1)) / step);
						}
					}
				}

				return new MyDouble(lt.getKernel(), ret);

			}
			return ev.illegalArgument(lt, rt, "freehand(");
		}
	},
	COS {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {
			if (lt.isNumberValue()) {
				return ((NumberValue) lt).getNumber().cos();
			}
			return ev.polynomialOrDie(lt, this, "cos(");

		}
	},
	SIN {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {
			if (lt.isNumberValue()) {
				return ((NumberValue) lt).getNumber().sin();
			}
			return ev.polynomialOrDie(lt, this, "sin(");

		}
	},
	TAN {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {
			if (lt.isNumberValue()) {
				return ((NumberValue) lt).getNumber().tan();
			}
			return ev.polynomialOrDie(lt, this, "tan(");

		}
	},
	EXP {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {
			if (lt.isNumberValue()) {
				return ((NumberValue) lt).getNumber().exp();
			} else if (lt.isVectorValue()) {
				GeoVec2D vec = ((VectorValue) lt).getVector();

				// complex e^z

				GeoVec2D.complexExp(vec, vec);
				return vec;

			} else
				return ev.polynomialOrDie(lt, this, "exp(");

		}
	},
	LOG {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {
			if (lt.isNumberValue()) {
				return ((NumberValue) lt).getNumber().log();
			} else if (lt.isVectorValue()) {
				GeoVec2D vec = ((VectorValue) lt).getVector();

				// complex natural log(z)

				GeoVec2D.complexLog(vec, vec);
				return vec;

			} else
				return ev.polynomialOrDie(lt, this, "log(");
		}
	},
	ARCCOS {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {
			if (lt.isNumberValue()) {
				return ((NumberValue) lt).getNumber().acos();
			}
			return ev.polynomialOrDie(lt, this, "acos(");

		}
	},
	ARCSIN {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {
			if (lt.isNumberValue()) {
				return ((NumberValue) lt).getNumber().asin();
			}
			return ev.polynomialOrDie(lt, this, "asin(");

		}
	},
	ARCTAN {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {
			if (lt.isNumberValue()) {
				return ((NumberValue) lt).getNumber().atan();
			}
			return ev.polynomialOrDie(lt, this, "atan(");

		}
	},
	ARCTAN2 {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {
			if (lt.isNumberValue() && rt.isNumberValue()) {
				return ((NumberValue) lt).getNumber().atan2((NumberValue) rt)
						.getNumber();
			}
			return ev.illegalArgument(lt, rt, "arctan2(");
		}
	},
	NROOT {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {
			Kernel kernel = lt.getKernel();
			if (rt.isNumberValue()) {
				double n = ((NumberValue) rt).getDouble();
				MyDouble exp = new MyDouble(kernel, 1 / n);

				if (lt.isNumberValue()) {
					MyDouble root = ((NumberValue) lt).getNumber();
					if (Kernel.isGreater(0, root.getDouble())
							&& Kernel.isInteger(n) && Math.round(n) % 2 == 1) {
						MyDouble.powDoubleSgnChange(root, exp, root);
					} else {
						MyDouble.pow(root, exp, root);
					}
					return root;
				} else if (lt.isPolynomialInstance()
						&& (((Polynomial) lt).degree() == 0)) {
					ExpressionValue coeff = ((Polynomial) lt)
							.getConstantCoefficient();
					return new Polynomial(kernel, new Term(new ExpressionNode(
							kernel, coeff, Operation.NROOT, rt), ""));
				} else if (lt.isVectorValue()) {
					GeoVec2D vec = ((VectorValue) lt).getVector();

					// complex sqrt
					GeoVec2D.complexPower(vec, exp, vec);
					return vec;

				}
			}
			return ev.illegalArgument(lt, rt, "nroot(");
		}
	},
	SQRT {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {
			if (lt.isNumberValue()) {
				return ((NumberValue) lt).getNumber().sqrt();
			} else if (lt.isVectorValue()) {
				GeoVec2D vec = ((VectorValue) lt).getVector();

				// complex sqrt
				GeoVec2D.complexSqrt(vec, vec);
				return vec;

			} else
				return ev.polynomialOrDie(lt, this, "sqrt(");
		}
	},
	SQRT_SHORT {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {
			return SQRT.handle(ev, lt, rt, left, right, tpl, holdsLaTeX);
		}
	},
	ABS {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {
			Kernel kernel = lt.getKernel();
			GeoVec2D vec;
			if (lt.isNumberValue()) {
				return ((NumberValue) lt).getNumber().abs();
			} else if (lt.isVectorValue()) {
				vec = ((VectorValue) lt).getVector();

				// complex Abs(z)
				// or magnitude of point

				return new MyDouble(kernel, GeoVec2D.complexAbs(vec));

			} else
				return ev.polynomialOrDie(lt, Operation.ABS, "abs(");
		}
	},
	SGN {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {
			if (lt.isNumberValue()) {
				return ((NumberValue) lt).getNumber().sgn();
			}
			return ev.polynomialOrDie(lt, this, "sgn(");
		}
	},
	XCOORD {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {
			return ev.handleXcoord(lt,this);

		}
	},
	YCOORD {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {
			return ev.handleYcoord(lt,this);

		}
	},
	ZCOORD {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {
			Kernel kernel = lt.getKernel();

			// y(vector)
			if (lt.isVectorValue()) {
				return new MyDouble(kernel, 0);
			} else if (lt.isVector3DValue()) {
				return new MyDouble(kernel,
						((Vector3DValue) lt).getPointAsDouble()[2]);
			} else if (lt instanceof GeoLine) {
				return new MyDouble(kernel, ((GeoLine) lt).z);
			}
			return ev.polynomialOrDie(lt, Operation.YCOORD, "z(");
		}
	},
	IMAGINARY {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {
			return ev.handleYcoord(lt,this);

		}
	},
	REAL {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {
			return ev.handleXcoord(lt,this);

		}
	},
	FRACTIONAL_PART {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {
			if (lt.isNumberValue()) {
				return ((NumberValue) lt).getNumber().fractionalPart();
			}
			return ev.polynomialOrDie(lt, this, "fractionalPart(");
		}
	},
	COSH {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {
			if (lt.isNumberValue()) {
				return ((NumberValue) lt).getNumber().cosh();
			}
			return ev.polynomialOrDie(lt, this, "cosh(");

		}
	},
	SINH {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {
			if (lt.isNumberValue()) {
				return ((NumberValue) lt).getNumber().sinh();
			}
			return ev.polynomialOrDie(lt, this, "sinh(");

		}
	},
	TANH {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {
			if (lt.isNumberValue()) {
				return ((NumberValue) lt).getNumber().tanh();
			}
			return ev.polynomialOrDie(lt, this, "tanh(");

		}
	},
	ACOSH {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {
			if (lt.isNumberValue()) {
				return ((NumberValue) lt).getNumber().acosh();
			}
			return ev.polynomialOrDie(lt, this, "acosh(");

		}
	},
	ASINH {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {
			if (lt.isNumberValue()) {
				return ((NumberValue) lt).getNumber().asinh();
			}
			return ev.polynomialOrDie(lt, this, "asinh(");

		}
	},
	ATANH {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {
			if (lt.isNumberValue()) {
				return ((NumberValue) lt).getNumber().atanh();
			}
			return ev.polynomialOrDie(lt, this, "atanh(");

		}
	},
	CSC {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {
			if (lt.isNumberValue()) {
				return ((NumberValue) lt).getNumber().csc();
			}
			return ev.polynomialOrDie(lt, this, "csc(");

		}
	},
	SEC {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {
			if (lt.isNumberValue()) {
				return ((NumberValue) lt).getNumber().sec();
			}
			return ev.polynomialOrDie(lt, this, "sec(");

		}
	},
	COT {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {
			if (lt.isNumberValue()) {
				return ((NumberValue) lt).getNumber().cot();
			}
			return ev.polynomialOrDie(lt, this, "cot(");

		}
	},
	CSCH {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {
			if (lt.isNumberValue()) {
				return ((NumberValue) lt).getNumber().csch();
			}
			return ev.polynomialOrDie(lt, this, "csch(");

		}
	},
	SECH {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {
			if (lt.isNumberValue()) {
				return ((NumberValue) lt).getNumber().sech();
			}
			return ev.polynomialOrDie(lt, this, "sech(");

		}
	},
	COTH {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {
			if (lt.isNumberValue()) {
				return ((NumberValue) lt).getNumber().coth();
			}
			return ev.polynomialOrDie(lt, this, "coth(");

		}
	},
	FLOOR {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {
			if (lt.isNumberValue()) {
				return ((NumberValue) lt).getNumber().floor();
			}
			return ev.polynomialOrDie(lt, this, "floor(");

		}
	},
	CEIL {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {
			if (lt.isNumberValue()) {
				return ((NumberValue) lt).getNumber().ceil();
			}
			return ev.polynomialOrDie(lt, this, "ceil(");

		}
	},
	FACTORIAL {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {
			if (lt.isNumberValue()) {
				return ((NumberValue) lt).getNumber().factorial();
			}
			return ev.polynomialOrDie(lt, this, "", "!");

		}
	},
	ROUND {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {
			if (lt.isNumberValue()) {
				return ((NumberValue) lt).getNumber().round();
			}
			return ev.polynomialOrDie(lt, this, "round(");

		}
	},
	GAMMA {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {
			if (lt.isNumberValue()) {
				return ((NumberValue) lt).getNumber().gamma();
			}
			return ev.polynomialOrDie(lt, this, "gamma(");
		}
	},
	GAMMA_INCOMPLETE {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {
			if (lt.isNumberValue() && rt.isNumberValue()) {
				return ((NumberValue) rt).getNumber().gammaIncomplete(
						(NumberValue) lt);
			}
			return ev.illegalArgument(lt, rt, "gammaIncomplete");
		}
	},
	GAMMA_INCOMPLETE_REGULARIZED {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {
			if (lt.isNumberValue() && rt.isNumberValue()) {
				return ((NumberValue) rt).getNumber()
						.gammaIncompleteRegularized((NumberValue) lt);
			}
			return ev.illegalArgument(lt, rt, "gammaIncompleteRegularized");
		}
	},
	BETA {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {
			if (lt.isNumberValue() && rt.isNumberValue()) {
				return ((NumberValue) rt).getNumber().beta((NumberValue) lt);
			}
			return ev.illegalArgument(lt, rt, "beta(");
		}
	},
	BETA_INCOMPLETE {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {
			if (lt.isVectorValue() && rt.isNumberValue()) {
				return ((NumberValue) rt).getNumber().betaIncomplete(
						(VectorValue) lt);
			}
			return ev.illegalArgument(lt, rt, "betaIncomplete(");
		}
	},
	BETA_INCOMPLETE_REGULARIZED {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {
			if (lt.isVectorValue() && rt.isNumberValue()) {
				return ((NumberValue) rt).getNumber()
						.betaIncompleteRegularized((VectorValue) lt);
			}
			return ev.illegalArgument(lt, rt, "betaIncompleteRegularized(");
		}
	},
	ERF {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {
			if (lt.isNumberValue()) {
				return ((NumberValue) lt).getNumber().erf();
			}
			return ev.polynomialOrDie(lt, this, "erf(");
		}
	},
	PSI {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {
			if (lt.isNumberValue()) {
				return ((NumberValue) lt).getNumber().psi();
			}
			return ev.polynomialOrDie(lt, this, "psi(");
		}
	},
	POLYGAMMA {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {
			if (lt.isNumberValue() && rt.isNumberValue()) {
				return ((NumberValue) rt).getNumber().polygamma(
						(NumberValue) lt);
			}
			return ev.polynomialOrDie(lt, this, "polygamma(");
		}
	},
	LOG10 {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {
			if (lt.isNumberValue()) {
				return ((NumberValue) lt).getNumber().log10();
			}
			return ev.polynomialOrDie(lt, this, "log10(");

		}
	},
	LOG2 {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {
			if (lt.isNumberValue()) {
				return ((NumberValue) lt).getNumber().log2();
			}
			return ev.polynomialOrDie(lt, this, "log2(");

		}
	},
	LOGB {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {
			if (lt.isNumberValue() && rt.isNumberValue()) {
				return ((NumberValue) rt).getNumber().log((NumberValue) lt);
			}
			return ev.illegalArgument(lt, rt, "log(");
		}
	},
	CI {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {
			if (lt.isNumberValue()) {
				return ((NumberValue) lt).getNumber().cosineIntegral();
			}
			return ev.polynomialOrDie(lt, this, "cosineIntegral(");

		}
	},
	SI {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {
			if (lt.isNumberValue()) {
				return ((NumberValue) lt).getNumber().sineIntegral();
			}
			return ev.polynomialOrDie(lt, this, "sineIntegral(");

		}
	},
	EI {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {
			if (lt.isNumberValue()) {
				return ((NumberValue) lt).getNumber().expIntegral();
			}
			return ev.polynomialOrDie(lt, this, "expIntegral(");

		}
	},
	CBRT {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {
			if (lt.isNumberValue()) {
				return ((NumberValue) lt).getNumber().cbrt();
			} else if (lt.isVectorValue()) {
				GeoVec2D vec = ((VectorValue) lt).getVector();
				// complex cbrt
				GeoVec2D.complexCbrt(vec, vec);
				return vec;
			}
			return ev.polynomialOrDie(lt, this, "cbrt(");
		}
	},
	RANDOM {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {
			return ((NumberValue) lt).getNumber();
		}
	},
	CONJUGATE {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {
			if (lt.isNumberValue()) {
				return ((NumberValue) lt).getNumber();
			} else if (lt.isVectorValue()) {
				GeoVec2D vec = ((VectorValue) lt).getVector();

				// complex cbrt
				GeoVec2D.complexConjugate(vec, vec);
				return vec;

			}
			return ev.polynomialOrDie(lt, this, "conjugate(");
		}
	},
	ARG {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {
			Kernel kernel = lt.getKernel();
			if (lt.isVectorValue()) {
				GeoVec2D vec = ((VectorValue) lt).getVector();

				MyDouble ret = new MyDouble(kernel, GeoVec2D.arg(vec));
				ret.setAngle();
				return ret;
			} else if (lt.isNumberValue()) {
				return new MyDouble(kernel,
						((NumberValue) lt).getDouble() < 0 ? Math.PI : 0);
			}
			return ev.polynomialOrDie(lt, this, "arg(");
		}
	},
	FUNCTION {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {
			return ev.handleFunction(lt, rt);

		}
	},
	FUNCTION_NVAR {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {
			return ev.handleFunctionNVar(lt, rt);
		}
	},
	VEC_FUNCTION {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {
			if (rt.isNumberValue()) {
				NumberValue arg = (NumberValue) rt;
				return ((ParametricCurve) lt).evaluateCurve(arg.getDouble());
			} else if (lt.isPolynomialInstance() && rt.isPolynomialInstance()
					&& (((Polynomial) rt).degree() == 0)) {
				ExpressionValue c1 = ((Polynomial) lt).getConstantCoefficient();
				ExpressionValue c2 = ((Polynomial) rt).getConstantCoefficient();
				return new Polynomial(lt.getKernel(), new Term(
						new ExpressionNode(lt.getKernel(), c1,
								Operation.VEC_FUNCTION, c2), ""));
			}
			return ev.illegalArgument(rt);
		}
	},
	DERIVATIVE {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {
			if (rt.isNumberValue()) {
				if (lt instanceof Functional) { // derivative of GeoFunction
					return ((Functional) lt).getGeoDerivative((int) Math
							.round(((NumberValue) rt).getDouble()));
				} else if (lt instanceof GeoCurveCartesian) { // derivative of
																// GeoCurveCartesian
					return ((GeoCurveCartesian) lt).getGeoDerivative((int) Math
							.round(((NumberValue) rt).getDouble()));
				}
			} else if (lt.isPolynomialInstance() && rt.isPolynomialInstance()
					&& (((Polynomial) rt).degree() == 0)) {
				ExpressionValue c1 = ((Polynomial) lt).getConstantCoefficient();
				ExpressionValue c2 = ((Polynomial) rt).getConstantCoefficient();
				return new Polynomial(lt.getKernel(), new Term(
						new ExpressionNode(lt.getKernel(), c1,
								Operation.DERIVATIVE, c2), ""));
			}
			return ev.illegalArgument(rt);
		}
	},
	ELEMENT_OF {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {
			// TODO not implemented #1115
			return new MyDouble(lt.getKernel(), Double.NaN);
		}
	},
	SUBSTITUTION {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {
			return new MyDouble(lt.getKernel(), Double.NaN);
		}
	},
	INTEGRAL {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {
			return new MyDouble(lt.getKernel(), Double.NaN);
		}
	},
	IF {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {
			if (lt.isBooleanValue()) {
				if (((BooleanValue) lt).getBoolean()) {
					return rt;
				}
				return new MyDouble(lt.getKernel(), Double.NaN);
			}
			return ev.illegalArgument(lt, rt, "if(");
		}
	},
	IF_ELSE {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {
			if (lt instanceof MyNumberPair) {
				ExpressionValue cond = ((MyNumberPair) lt).getX().evaluate(tpl);
				if (cond.isBooleanValue()) {
					if (((BooleanValue) cond).getBoolean()) {
						return ((MyNumberPair) lt).getY().evaluate(tpl);
					}
					return rt;
				}
			}
			return ev.illegalArgument(lt, rt, "if(");
		}
	},

	// spreadsheet absolute reference using $ signs
	$VAR_ROW {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {
			return lt;
		}
	},
	$VAR_COL {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {
			return lt;
		}
	},
	$VAR_ROW_COL {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {
			return lt;
		}
	},

	ARBCONST {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {
			return new MyDouble(lt.getKernel(), 0.0);
		}
	},
	ARBINT {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {
			return new MyDouble(lt.getKernel(), 0.0);
		}
	},
	ARBCOMPLEX {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {
			return new MyDouble(lt.getKernel(), 0.0);
		}
	},
	SUM {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {
			return new MyDouble(lt.getKernel(), Double.NaN);
		}
	},
	ZETA {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {
			if (lt.isNumberValue()) {
				return ((NumberValue) lt).getNumber().zeta();
			} else if (lt.isVectorValue()) {
				GeoVec2D vec = ((VectorValue) lt).getVector();

				GeoVec2D.complexZeta(vec, vec);

				return vec;

			} else
				return ev.polynomialOrDie(lt, this, "zeta(");
		}
	};

	public static boolean isSimpleFunction(Operation op) {
		switch (op) {
		case SIN:
		case COS:
		case TAN:
		case ARCSIN:
		case ARCCOS:
		case ARCTAN:
		case SINH:
		case COSH:
		case TANH:
		case ASINH:
		case ACOSH:
		case ATANH:
		case CSC:
		case SEC:
		case COT:
		case CSCH:
		case SECH:
		case COTH:

		case EXP:
		case ZETA:
		case LOG:
		case LOG10:
		case LOG2:
		case SQRT:
		case CBRT:
		case ERF:
		case ABS:
		case CI:
		case SI:
		case EI:
		case PSI:
		case GAMMA:

			return true;
		}
		return false;
	}

	public abstract ExpressionValue handle(ExpressionNodeEvaluator ev,
			ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
			ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX);
}