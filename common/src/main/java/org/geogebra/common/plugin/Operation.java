package org.geogebra.common.plugin;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.Path;
import org.geogebra.common.kernel.Region;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.BooleanValue;
import org.geogebra.common.kernel.arithmetic.ExpressionNodeConstants;
import org.geogebra.common.kernel.arithmetic.ExpressionNodeEvaluator;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.Functional;
import org.geogebra.common.kernel.arithmetic.ListValue;
import org.geogebra.common.kernel.arithmetic.MyBoolean;
import org.geogebra.common.kernel.arithmetic.MyDouble;
import org.geogebra.common.kernel.arithmetic.MyList;
import org.geogebra.common.kernel.arithmetic.MyNumberPair;
import org.geogebra.common.kernel.arithmetic.MyVecNode;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.arithmetic.TextValue;
import org.geogebra.common.kernel.arithmetic.VectorNDValue;
import org.geogebra.common.kernel.arithmetic.VectorValue;
import org.geogebra.common.kernel.arithmetic3D.MyVec3DNode;
import org.geogebra.common.kernel.arithmetic3D.Vector3DValue;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoVec2D;
import org.geogebra.common.kernel.geos.ParametricCurve;
import org.geogebra.common.kernel.kernelND.Geo3DVec;
import org.geogebra.common.kernel.kernelND.GeoCurveCartesianND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.GeoVecInterface;
import org.geogebra.common.util.MyMath;
import org.geogebra.common.util.debug.Log;

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
			MyBoolean b = ExpressionNodeEvaluator.evalEquals(ev.getKernel(),
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
			if (lt instanceof BooleanValue) {

				BooleanValue a = ((BooleanValue) lt);
				boolean defined = a.isDefined();

				MyBoolean bool = a.getMyBoolean();

				bool.setValue(!a.getBoolean());
				bool.setDefined(defined);

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
			if (lt instanceof BooleanValue && rt instanceof BooleanValue) {

				BooleanValue a = ((BooleanValue) lt);
				BooleanValue b = ((BooleanValue) rt);
				boolean defined = a.isDefined() && b.isDefined();

				MyBoolean bool = a.getMyBoolean();

				bool.setValue(a.getBoolean() || b.getBoolean());
				bool.setDefined(defined);

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
			if (lt instanceof BooleanValue && rt instanceof BooleanValue) {

				BooleanValue a = ((BooleanValue) lt);
				BooleanValue b = ((BooleanValue) rt);
				boolean defined = a.isDefined() && b.isDefined();

				MyBoolean bool = a.getMyBoolean();

				bool.setValue(a.getBoolean() && b.getBoolean());
				bool.setDefined(defined);

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
			if (lt instanceof BooleanValue && rt instanceof BooleanValue) {

				BooleanValue a = ((BooleanValue) lt);
				BooleanValue b = ((BooleanValue) rt);
				boolean defined = a.isDefined() && b.isDefined();

				MyBoolean bool = a.getMyBoolean();

				bool.setValue(!a.getBoolean() || b.getBoolean());
				bool.setDefined(defined);

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
			MyBoolean b = ExpressionNodeEvaluator.evalEquals(ev.getKernel(),
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
			if (lt instanceof NumberValue && rt instanceof NumberValue) {
				
				double a = ((NumberValue) lt).getDouble();
				double b = ((NumberValue) rt).getDouble();
				boolean defined = MyDouble.isFinite(a) && MyDouble.isFinite(b);
				
				return new MyBoolean(ev.getKernel(), Kernel.isGreater(b, a),
						defined);
			}
			if (lt instanceof TextValue && rt instanceof TextValue) {
				int comp = ((TextValue) lt).toValueString(tpl).compareTo(
						((TextValue) rt).toValueString(tpl));
				return new MyBoolean(ev.getKernel(), comp < 0);
			}
			return ev.illegalComparison(lt, rt, "<");
		}
	},
	GREATER {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {
			if (lt instanceof NumberValue && rt instanceof NumberValue) {

				double a = ((NumberValue) lt).getDouble();
				double b = ((NumberValue) rt).getDouble();
				boolean defined = MyDouble.isFinite(a) && MyDouble.isFinite(b);

				return new MyBoolean(ev.getKernel(), Kernel.isGreater(a, b),
						defined);
			}
			if (lt instanceof TextValue && rt instanceof TextValue) {
				int comp = ((TextValue) lt).toValueString(tpl).compareTo(
						((TextValue) rt).toValueString(tpl));
				return new MyBoolean(ev.getKernel(), comp > 0);
			}
			return ev.illegalComparison(lt, rt, ">");
		}
	},
	LESS_EQUAL {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {
			if (lt instanceof NumberValue && rt instanceof NumberValue) {

				double a = ((NumberValue) lt).getDouble();
				double b = ((NumberValue) rt).getDouble();
				boolean defined = MyDouble.isFinite(a) && MyDouble.isFinite(b);

				return new MyBoolean(ev.getKernel(),
						Kernel.isGreaterEqual(b, a), defined);
			}
			if (lt instanceof TextValue && rt instanceof TextValue) {
				int comp = ((TextValue) lt).toValueString(tpl).compareTo(
						((TextValue) rt).toValueString(tpl));
				return new MyBoolean(ev.getKernel(), comp <= 0);
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
			if (lt instanceof NumberValue && rt instanceof NumberValue) {

				double a = ((NumberValue) lt).getDouble();
				double b = ((NumberValue) rt).getDouble();
				boolean defined = MyDouble.isFinite(a) && MyDouble.isFinite(b);

				return new MyBoolean(ev.getKernel(),
						Kernel.isGreaterEqual(a, b), defined);
			}
			if (lt instanceof TextValue && rt instanceof TextValue) {
				int comp = ((TextValue) lt).toValueString(tpl).compareTo(
						((TextValue) rt).toValueString(tpl));
				return new MyBoolean(ev.getKernel(), comp >= 0);
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
				return new MyBoolean(ev.getKernel(),
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
				return new MyBoolean(ev.getKernel(),
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
			if (rt instanceof ListValue) {
				return new MyBoolean(ev.getKernel(), MyList.isElementOf(lt,
						((ListValue) rt).getMyList()));
			}

			// checks for 2D or 3D point
			if (lt.isGeoElement() && ((GeoElement) lt).isGeoPoint()) {

				// check Region before Path (eg Polygon)
				if (rt instanceof Region) {
					return new MyBoolean(ev.getKernel(),
							((Region) rt).isInRegion(((GeoPointND) lt)));
				}

				if (rt instanceof Path) {
					return new MyBoolean(ev.getKernel(), ((Path) rt).isOnPath(
							((GeoPointND) lt), Kernel.STANDARD_PRECISION));
				}
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
			if (lt instanceof ListValue && rt instanceof ListValue) {
				return new MyBoolean(ev.getKernel(), MyList.listContains(
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
			if (lt instanceof ListValue && rt instanceof ListValue) {
				return new MyBoolean(ev.getKernel(), MyList.listContainsStrict(
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
			if (lt instanceof ListValue && rt instanceof ListValue) {
				return MyList.setDifference(ev.getKernel(),
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

			return ev.handleVectorProduct(lt, rt, tpl, holdsLaTeX);
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
			if (lt instanceof NumberValue && rt instanceof ListValue) {
				double x = ((NumberValue) lt).getDouble();
				double ret = Double.NaN;
				
				ListValue list = (ListValue) rt;
				if(list instanceof GeoList && ((GeoList)list).getElementType() != GeoClass.NUMERIC ){
					return new MyDouble(ev.getKernel(), Double.NaN);
				}
				int n = list.size() - 3;
				if (n >= 1) {
					double min = ((NumberValue) (list.getListElement(0))).getDouble();
					double max = ((NumberValue) (list.getListElement(1))).getDouble();

					if ((min > max) || (x > max) || (x < min)) {
						return new MyDouble(ev.getKernel(), Double.NaN);
					}

					double step = (max - min) / n;

					int index = (int) Math.floor((x - min) / step);

					if (index > (n - 1)) {
						ret = ((NumberValue) (list.getListElement(n + 2))).getDouble();
					} else {

						double y1 = ((NumberValue) (list.getListElement(index + 2)))
								.getDouble();
						double y2 = ((NumberValue) (list.getListElement(index + 3)))
								.getDouble();
						double x1 = min + (index * step);

						// linear interpolation between (x1,y1) and
						// (x2,y2+step) to give (x,ret)
						ret = y1 + (((x - x1) * (y2 - y1)) / step);
					}
				}
				

				return new MyDouble(ev.getKernel(), ret);

			}
			return ev.illegalArgument(lt, rt, "freehand(");
		}
	},
	DATA {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {
			if (lt instanceof NumberValue && rt instanceof MyNumberPair) {
				double x = ((NumberValue) lt).getDouble();
				MyList keyList = (MyList) ((MyNumberPair)rt).getX();
				MyList valueList = (MyList) ((MyNumberPair) rt).getY();
				if (keyList.size() < 1) {
					return new MyDouble(ev.getKernel(), Double.NaN);
				}
				double max = keyList.getListElement(keyList.size() - 1)
						.evaluateDouble();
				double min = keyList.getListElement(0).evaluateDouble();
				if (max < x || min > x) {
					return new MyDouble(ev.getKernel(), Double.NaN);
				}
				int index = (int) (keyList.size() * (x - min) / (max - min));
				index = Math.max(Math.min(index, keyList.size() - 1), 0);
				while (index > 0
						&& keyList.getListElement(index).evaluateDouble() >= x) {
					index--;
				}
				while (index < keyList.size() - 1
						&& keyList.getListElement(index + 1).evaluateDouble() < x) {
					index++;
				}
				double x1 = keyList.getListElement(index).evaluateDouble();
				double x2 = keyList.getListElement(index + 1).evaluateDouble();
				double y1 = valueList.getListElement(index).evaluateDouble();
				double y2 = valueList.getListElement(index + 1)
						.evaluateDouble();
				return new MyDouble(ev.getKernel(), ((x - x1) * y2 + y1
						* (x2 - x))
						/ (x2 - x1));
			}
			return ev.illegalArgument(lt, rt, "freehand(");
		}
	},
	COS {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {
			if (lt instanceof NumberValue) {
				return ((NumberValue) lt).getNumber().cos();
			} else if (lt instanceof VectorValue) {
				GeoVec2D vec = ((VectorValue) lt).getVector();

				// complex sin
				GeoVec2D.complexCos(vec, vec);
				return vec;

			}
			return ev.polynomialOrDie(lt, this, "cos(");

		}
	},
	SIN {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {
			if (lt instanceof NumberValue) {
				return ((NumberValue) lt).getNumber().sin();
			} else if (lt instanceof VectorValue) {
				GeoVec2D vec = ((VectorValue) lt).getVector();

				// complex sin
				GeoVec2D.complexSin(vec, vec);
				return vec;

			}
			return ev.polynomialOrDie(lt, this, "sin(");

		}
	},
	TAN {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {
			if (lt instanceof NumberValue) {
				return ((NumberValue) lt).getNumber().tan();
			} else if (lt instanceof VectorValue) {
				GeoVec2D vec = ((VectorValue) lt).getVector();

				// complex sin
				GeoVec2D.complexTan(vec, vec);
				return vec;

			}
			return ev.polynomialOrDie(lt, this, "tan(");

		}
	},
	EXP {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {
			if (lt instanceof NumberValue) {
				return ((NumberValue) lt).getNumber().exp();
			} else if (lt instanceof VectorValue) {
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
			if (lt instanceof NumberValue) {
				return ((NumberValue) lt).getNumber().log();
			} else if (lt instanceof VectorValue) {
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
			if (lt instanceof NumberValue) {
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
			if (lt instanceof NumberValue) {
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
			if (lt instanceof NumberValue) {
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
			if (lt instanceof NumberValue && rt instanceof NumberValue) {
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
			Kernel kernel = ev.getKernel();
			if (rt instanceof NumberValue) {
				double n = ((NumberValue) rt).getDouble();
				MyDouble exp = new MyDouble(kernel, 1 / n);

				if (lt instanceof NumberValue) {
					MyDouble root = ((NumberValue) lt).getNumber();
					if (Kernel.isGreater(0, root.getDouble())
							&& Kernel.isInteger(n) && Math.round(n) % 2 == 1) {
						MyDouble.powDoubleSgnChange(root, exp, root);
					} else {
						MyDouble.pow(root, exp, root);
					}
					return root;
				} else if (lt instanceof VectorValue) {
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
			if (lt instanceof NumberValue) {
				return ((NumberValue) lt).getNumber().sqrt();
			} else if (lt instanceof VectorValue) {
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
			Kernel kernel = ev.getKernel();
			GeoVec2D vec;
			if (lt instanceof NumberValue) {
				return ((NumberValue) lt).getNumber().abs();
			} else if (lt instanceof VectorValue) {
				vec = ((VectorValue) lt).getVector();

				// complex Abs(z)
				// or magnitude of point

				return new MyDouble(kernel, GeoVec2D.complexAbs(vec));

			} else if (lt instanceof Vector3DValue) {
				Geo3DVec vec3d = ((Vector3DValue) lt).getVector();

				// complex Abs(z)
				// or magnitude of point

				return new MyDouble(kernel, vec3d.length());

			} else
				return ev.polynomialOrDie(lt, Operation.ABS, "abs(");
		}
	},
	SGN {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {
			if (lt instanceof NumberValue) {
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
			return ev.handleXcoord(lt, this);

		}
	},
	YCOORD {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {
			return ev.handleYcoord(lt, this);

		}
	},
	ZCOORD {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {
			Kernel kernel = ev.getKernel();

			// z(vector)
			if (lt instanceof VectorValue) {
				return new MyDouble(kernel, 0);
			} else if (lt instanceof Vector3DValue) {
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
			return ev.handleYcoord(lt, this);

		}
	},
	REAL {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {
			return ev.handleXcoord(lt, this);

		}
	},
	FRACTIONAL_PART {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {
			if (lt instanceof NumberValue) {
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
			if (lt instanceof NumberValue) {
				return ((NumberValue) lt).getNumber().cosh();
			} else if (lt instanceof VectorValue) {
				GeoVec2D vec = ((VectorValue) lt).getVector();

				// complex sin
				GeoVec2D.complexCosh(vec, vec);
				return vec;

			}
			return ev.polynomialOrDie(lt, this, "cosh(");

		}
	},
	SINH {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {
			if (lt instanceof NumberValue) {
				return ((NumberValue) lt).getNumber().sinh();
			} else if (lt instanceof VectorValue) {
				GeoVec2D vec = ((VectorValue) lt).getVector();

				// complex sin
				GeoVec2D.complexSinh(vec, vec);
				return vec;

			}
			return ev.polynomialOrDie(lt, this, "sinh(");

		}
	},
	TANH {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {
			if (lt instanceof NumberValue) {
				return ((NumberValue) lt).getNumber().tanh();
			} else if (lt instanceof VectorValue) {
				GeoVec2D vec = ((VectorValue) lt).getVector();

				// complex sin
				GeoVec2D.complexTanh(vec, vec);
				return vec;

			}
			return ev.polynomialOrDie(lt, this, "tanh(");

		}
	},
	ACOSH {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {
			if (lt instanceof NumberValue) {
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
			if (lt instanceof NumberValue) {
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
			if (lt instanceof NumberValue) {
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
			if (lt instanceof NumberValue) {
				return ((NumberValue) lt).getNumber().csc();
			} else if (lt instanceof VectorValue) {
				GeoVec2D vec = ((VectorValue) lt).getVector();

				// complex sin
				GeoVec2D.complexCsc(vec, vec);
				return vec;

			}
			return ev.polynomialOrDie(lt, this, "csc(");

		}
	},
	SEC {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {
			if (lt instanceof NumberValue) {
				return ((NumberValue) lt).getNumber().sec();
			} else if (lt instanceof VectorValue) {
				GeoVec2D vec = ((VectorValue) lt).getVector();

				// complex sin
				GeoVec2D.complexSec(vec, vec);
				return vec;

			}
			return ev.polynomialOrDie(lt, this, "sec(");

		}
	},
	COT {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {
			if (lt instanceof NumberValue) {
				return ((NumberValue) lt).getNumber().cot();
			} else if (lt instanceof VectorValue) {
				GeoVec2D vec = ((VectorValue) lt).getVector();

				// complex sin
				GeoVec2D.complexCot(vec, vec);
				return vec;

			}
			return ev.polynomialOrDie(lt, this, "cot(");

		}
	},
	CSCH {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {
			if (lt instanceof NumberValue) {
				return ((NumberValue) lt).getNumber().csch();
			} else if (lt instanceof VectorValue) {
				GeoVec2D vec = ((VectorValue) lt).getVector();

				// complex sin
				GeoVec2D.complexCsch(vec, vec);
				return vec;

			}
			return ev.polynomialOrDie(lt, this, "csch(");

		}
	},
	SECH {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {
			if (lt instanceof NumberValue) {
				return ((NumberValue) lt).getNumber().sech();
			} else if (lt instanceof VectorValue) {
				GeoVec2D vec = ((VectorValue) lt).getVector();

				// complex sin
				GeoVec2D.complexSech(vec, vec);
				return vec;

			}
			return ev.polynomialOrDie(lt, this, "sech(");

		}
	},
	COTH {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {
			if (lt instanceof NumberValue) {
				return ((NumberValue) lt).getNumber().coth();
			} else if (lt instanceof VectorValue) {
				GeoVec2D vec = ((VectorValue) lt).getVector();

				// complex sin
				GeoVec2D.complexCoth(vec, vec);
				return vec;

			}
			return ev.polynomialOrDie(lt, this, "coth(");

		}
	},
	FLOOR {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {
			if (lt instanceof NumberValue) {
				return ((NumberValue) lt).getNumber().floor();
			}
			if (lt instanceof VectorValue) {
				GeoVec2D ret = ((VectorValue) lt).getVector().floor();
				// eg complex
				ret.setMode(((VectorValue) lt).getMode());
				return ret;
			}
			if (lt instanceof Vector3DValue) {
				Geo3DVec ret = ((Vector3DValue) lt).getVector().floor();
				return ret;
			}
			return ev.polynomialOrDie(lt, this, "floor(");

		}
	},
	CEIL {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {
			if (lt instanceof NumberValue) {
				return ((NumberValue) lt).getNumber().ceil();
			}
			if (lt instanceof VectorValue) {
				GeoVec2D ret = ((VectorValue) lt).getVector().ceil();
				// eg complex
				ret.setMode(((VectorValue) lt).getMode());
				return ret;
			}
			if (lt instanceof Vector3DValue) {
				Geo3DVec ret = ((Vector3DValue) lt).getVector().ceil();
				return ret;
			}
			return ev.polynomialOrDie(lt, this, "ceil(");

		}
	},
	FACTORIAL {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {
			if (lt instanceof NumberValue) {
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
			if (lt instanceof NumberValue) {
				if (rt instanceof NumberValue
						&& (!Double.isNaN(((NumberValue) rt).getDouble()) || rt
								.isGeoElement())) {
					return ((NumberValue) lt).getNumber().round(
							((NumberValue) rt).getDouble());
				}
				return ((NumberValue) lt).getNumber().round();
			}
			if (lt instanceof VectorValue) {
				GeoVec2D ret = ((VectorValue) lt).getVector().round();
				// eg complex
				ret.setMode(((VectorValue) lt).getMode());
				return ret;
			}
			if (lt instanceof Vector3DValue) {
				Geo3DVec ret = ((Vector3DValue) lt).getVector().round();
				return ret;
			}
			return ev.polynomialOrDie(lt, this, "round(");

		}
	},
	GAMMA {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {
			if (lt instanceof NumberValue) {
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
			if (lt instanceof NumberValue && rt instanceof NumberValue) {
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
			if (lt instanceof NumberValue && rt instanceof NumberValue) {
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
			if (lt instanceof NumberValue && rt instanceof NumberValue) {
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
			if (lt instanceof VectorValue && rt instanceof NumberValue) {
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
			if (lt instanceof VectorValue && rt instanceof NumberValue) {
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
			if (lt instanceof NumberValue) {
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
			if (lt instanceof NumberValue) {
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
			if (lt instanceof NumberValue && rt instanceof NumberValue) {
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
			if (lt instanceof NumberValue) {
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
			if (lt instanceof NumberValue) {
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
			if (lt instanceof NumberValue && rt instanceof NumberValue) {
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
			if (lt instanceof NumberValue) {
				return ((NumberValue) lt).getNumber().cosineIntegral();
			}
			return ev.polynomialOrDie(lt, this, "cosIntegral(");

		}
	},
	SI {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {
			if (lt instanceof NumberValue) {
				return ((NumberValue) lt).getNumber().sineIntegral();
			}
			return ev.polynomialOrDie(lt, this, "sinIntegral(");

		}
	},
	EI {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {
			if (lt instanceof NumberValue) {
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
			if (lt instanceof NumberValue) {
				return ((NumberValue) lt).getNumber().cbrt();
			} else if (lt instanceof VectorValue) {
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
			if (lt instanceof NumberValue) {
				return ((NumberValue) lt).getNumber();
			} else if (lt instanceof VectorValue) {
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
			Kernel kernel = ev.getKernel();
			if (lt instanceof VectorNDValue) {
				GeoVecInterface vec = ((VectorNDValue) lt).getVector();

				MyDouble ret = new MyDouble(kernel, Math.atan2(vec.getY(),
						vec.getX()));
				ret.setAngle();
				return ret;
			} else if (lt instanceof NumberValue) {
				return new MyDouble(kernel,
						((NumberValue) lt).getDouble() < 0 ? Math.PI : 0);
			}
			return ev.polynomialOrDie(lt, this, "arg(");
		}
	},
	ALT {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {
			Kernel kernel = ev.getKernel();
			if (lt instanceof VectorValue || lt instanceof NumberValue) {
				MyDouble ret = new MyDouble(kernel, 0);
				ret.setAngle();
				return ret;
			} else if (lt instanceof Vector3DValue) {
				Geo3DVec vec = ((Vector3DValue) lt).getVector();
				double l = MyMath.length(vec.getX(), vec.getY());
				MyDouble ret = new MyDouble(kernel, Math.atan2(vec.getZ(), l));
				ret.setAngle();
				return ret;
			}
			return ev.polynomialOrDie(lt, this, "alt(");
		}
	},
	FUNCTION {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {
			return ev.handleFunction(lt, rt, left);

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
			if (rt instanceof NumberValue) {
				NumberValue arg = (NumberValue) rt;

				if (lt instanceof GeoCurveCartesianND) {
					return ((GeoCurveCartesianND) lt).evaluateCurve(arg
							.getDouble());
				}

				return ((ParametricCurve) lt).evaluateCurve(arg.getDouble());
			}
			return ev.illegalArgument(rt);
		}
	},
	DERIVATIVE {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {
			if (rt instanceof NumberValue) {
				if (lt instanceof Functional) { // derivative of GeoFunction
					return ((Functional) lt).getGeoDerivative((int) Math
							.round(((NumberValue) rt).getDouble()));
				} else if (lt instanceof GeoCurveCartesianND) { // derivative of
																// GeoCurveCartesian
					return ((GeoCurveCartesianND) lt)
							.getGeoDerivative((int) Math
									.round(((NumberValue) rt).getDouble()));
				}
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
			return new MyDouble(ev.getKernel(), Double.NaN);
		}
	},
	SUBSTITUTION {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {
			return new MyDouble(ev.getKernel(), Double.NaN);
		}
	},
	INTEGRAL {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {
			return new MyDouble(ev.getKernel(), Double.NaN);
		}
	},
	IF {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {
			if (lt instanceof BooleanValue) {
				if (((BooleanValue) lt).getBoolean()) {
					return rt;
				}
				return new MyDouble(ev.getKernel(), Double.NaN);
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
				if (cond instanceof BooleanValue) {

					if (!((BooleanValue) cond).isDefined()) {
						return new MyDouble(ev.getKernel(), Double.NaN);
					}

					if (((BooleanValue) cond).getBoolean()) {
						return ((MyNumberPair) lt).getY().evaluate(tpl);
					}
					return rt;
				}
			}
			return ev.illegalArgument(lt, rt, "if(");
		}
	},
	IF_LIST {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {
			if (lt instanceof MyList && rt instanceof MyList) {
				MyList cond = (MyList) lt;
				for (int i = 0; i < cond.size(); i++) {
					ExpressionValue curr = cond.getListElement(i).evaluate(tpl);

					if (curr instanceof BooleanValue) {
						if (((BooleanValue) curr).getBoolean()) {
							return ((MyList) rt).getListElement(i)
									.evaluate(tpl);
						}

					}
				}
				return cond.size() == ((MyList) rt).size() ? new MyDouble(
						ev.getKernel(), Double.NaN) : ((MyList) rt)
						.getListElement(cond.size()).evaluate(tpl);

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
			return new MyDouble(ev.getKernel(), 0.0);
		}
	},
	ARBINT {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {
			return new MyDouble(ev.getKernel(), 0.0);
		}
	},
	ARBCOMPLEX {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {
			return new MyDouble(ev.getKernel(), 0.0);
		}
	},
	SUM {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {
			return new MyDouble(ev.getKernel(), Double.NaN);
		}
	},
	ZETA {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {
			if (lt instanceof NumberValue) {
				return ((NumberValue) lt).getNumber().zeta();
			} else if (lt instanceof VectorValue) {
				GeoVec2D vec = ((VectorValue) lt).getVector();

				GeoVec2D.complexZeta(vec, vec);

				return vec;

			} else
				return ev.polynomialOrDie(lt, this, "zeta(");
		}
	},
	DIFF {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {
			return new MyDouble(ev.getKernel(), Double.NaN);
		}
	},
	MATRIXTOVECTOR {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {

			if (!(lt.unwrap() instanceof MyList)) {
				return lt;
			}

			MyList list = (MyList) lt.unwrap();
			Log.debug(list);
			if (list.size() == 3) {
				return new MyVec3DNode(ev.getKernel(), MyList.getCell(list, 0,
						0),
 MyList.getCell(list, 0, 1), MyList.getCell(list, 0,
						2));
			}
			return new MyVecNode(ev.getKernel(), MyList.getCell(list, 0, 0),
					MyList.getCell(list, 0, 1));
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

	public boolean isPlusorMinus() {
		return this.equals(PLUS) || this.equals(MINUS);
	}

	public boolean isInequality() {
		return this.equals(GREATER_EQUAL) || this.equals(GREATER)
				|| this.equals(LESS) || this.equals(LESS_EQUAL);
	}

	/**
	 * @return negation of this expression (optimizes negation of >,<,=>,<=)
	 */
	public Operation negate() {
		switch (this) {
		case GREATER:
			return Operation.LESS_EQUAL;
		case GREATER_EQUAL:
			return Operation.LESS;
		case LESS:
			return Operation.GREATER_EQUAL;
		case LESS_EQUAL:
			return Operation.GREATER;
		case EQUAL_BOOLEAN:
			return Operation.NOT_EQUAL;
		case NOT_EQUAL:
			return Operation.EQUAL_BOOLEAN;
		default:
			return Operation.NOT;
		}
	}

	/**
	 * @return operation swapped left to right
	 */
	public Operation reverseLeftToRight() {
		switch (this) {
		case GREATER:
			return Operation.LESS;
		case GREATER_EQUAL:
			return Operation.LESS_EQUAL;
		case LESS:
			return Operation.GREATER;
		case LESS_EQUAL:
			return Operation.GREATER_EQUAL;
		case EQUAL_BOOLEAN:
			return Operation.EQUAL_BOOLEAN;
		case NOT_EQUAL:
			return Operation.NOT_EQUAL;
		}

		return Operation.NO_OPERATION;
	}

	public static boolean integralIsNonContinuous(Operation op) {

		switch (op) {
		case ABS:
		case SGN:
		case FLOOR:
		case CEIL:
		case ROUND:
		case TAN:
		case COT:
		case SEC:
		case CSC:
		case FRACTIONAL_PART:
		case ZETA:
		case GAMMA:
		case GAMMA_INCOMPLETE:
		case GAMMA_INCOMPLETE_REGULARIZED:
		case BETA:
		case BETA_INCOMPLETE:
		case BETA_INCOMPLETE_REGULARIZED:
		case POLYGAMMA:
		case PSI:
		case IF:
		case IF_ELSE:

			return true;
		}

		return false;
	}
}
