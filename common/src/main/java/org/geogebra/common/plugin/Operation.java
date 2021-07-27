package org.geogebra.common.plugin;

import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.Path;
import org.geogebra.common.kernel.Region;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.BooleanValue;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
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
import org.geogebra.common.kernel.arithmetic.Traversing;
import org.geogebra.common.kernel.arithmetic.VectorNDValue;
import org.geogebra.common.kernel.arithmetic.VectorValue;
import org.geogebra.common.kernel.arithmetic3D.MyVec3DNode;
import org.geogebra.common.kernel.arithmetic3D.Vector3DValue;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoVec2D;
import org.geogebra.common.kernel.geos.ParametricCurve;
import org.geogebra.common.kernel.kernelND.Geo3DVecInterface;
import org.geogebra.common.kernel.kernelND.GeoCurveCartesianND;
import org.geogebra.common.kernel.kernelND.GeoLineND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.kernel.kernelND.GeoSurfaceCartesianND;
import org.geogebra.common.kernel.kernelND.GeoVecInterface;
import org.geogebra.common.kernel.statistics.AlgoNpR;
import org.geogebra.common.main.MyError.Errors;
import org.geogebra.common.util.DoubleUtil;
import org.geogebra.common.util.MyMath;

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
	SEQUENCE {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {
			if ((lt.unwrap() instanceof NumberValue)
					&& (rt.unwrap() instanceof NumberValue)) {
				MyList list = new MyList(ev.getKernel());

				double from = Math.round(lt.evaluateDouble());
				double to = Math.round(rt.evaluateDouble());

				if (from > MyMath.LARGEST_INTEGER
						|| from < -MyMath.LARGEST_INTEGER) {
					throw ev.illegalArgument(lt);
				}

				if (to > MyMath.LARGEST_INTEGER
						|| to < -MyMath.LARGEST_INTEGER) {
					throw ev.illegalArgument(rt);
				}

				// also see AlgoSequence.computeRange()
				if (from < to) {

					// increasing list
					for (double k = from; k <= to; k++) {
						list.addListElement(new MyDouble(ev.getKernel(), k));
					}

				} else {

					// decreasing list
					for (double k = from; k >= to; k--) {
						list.addListElement(new MyDouble(ev.getKernel(), k));
					}

				}

				return list;
			}
			if (!(lt.unwrap() instanceof NumberValue)) {
				throw ev.illegalArgument(lt);
			}

			throw ev.illegalArgument(rt);
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
			throw ev.illegalBinary(lt, rt, Errors.IllegalBoolean,
					ExpressionNodeConstants.strNOT);
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
			throw ev.illegalBinary(lt, rt, Errors.IllegalBoolean,
					ExpressionNodeConstants.strOR);
		}
	},
	XOR {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {
			if (lt instanceof BooleanValue && rt instanceof BooleanValue) {

				BooleanValue a = ((BooleanValue) lt);
				BooleanValue b = ((BooleanValue) rt);
				boolean defined = a.isDefined() && b.isDefined();

				MyBoolean bool = a.getMyBoolean();

				bool.setValue(a.getBoolean() ^ b.getBoolean());
				bool.setDefined(defined);

				return bool;
			}
			throw ev.illegalBinary(lt, rt, Errors.IllegalBoolean,
					ExpressionNodeConstants.strOR);
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
			throw ev.illegalBinary(lt, rt, Errors.IllegalBoolean,
					ExpressionNodeConstants.strAND);
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
	NOT_EQUAL {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {
			MyBoolean b = ExpressionNodeEvaluator.evalEquals(ev.getKernel(), lt,
					rt);
			// b can't be null here (findbugs)
			// if (b == null) {
			// return ev.illegalComparison(lt, rt,
			// ExpressionNodeConstants.strNOT_EQUAL);
			// }
			b.setValue(!b.getBoolean());
			return b;
		}
	},
	EQUAL_BOOLEAN {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {
			// b can't be null here (findbugs)
			// if (b == null) {
			// return ev.illegalComparison(lt, rt,
			// ExpressionNodeConstants.strNOT_EQUAL);
			// }
			return ExpressionNodeEvaluator.evalEquals(ev.getKernel(), lt,
					rt);
		}
	},
	LESS {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {
			if (lt instanceof NumberValue && rt instanceof NumberValue) {

				double a = lt.evaluateDouble();
				double b = rt.evaluateDouble();
				boolean defined = MyDouble.isFinite(a) && MyDouble.isFinite(b);

				return new MyBoolean(ev.getKernel(), DoubleUtil.isGreater(b, a),
						defined);
			}
			if (lt instanceof TextValue && rt instanceof TextValue) {
				int comp = lt.toValueString(tpl)
						.compareTo(rt.toValueString(tpl));
				return new MyBoolean(ev.getKernel(), comp < 0);
			}
			throw ev.illegalComparison(lt, rt, "<");
		}
	},
	GREATER {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {
			if (lt instanceof NumberValue && rt instanceof NumberValue) {

				double a = lt.evaluateDouble();
				double b = rt.evaluateDouble();
				boolean defined = MyDouble.isFinite(a) && MyDouble.isFinite(b);

				return new MyBoolean(ev.getKernel(), DoubleUtil.isGreater(a, b),
						defined);
			}
			if (lt instanceof TextValue && rt instanceof TextValue) {
				int comp = lt.toValueString(tpl)
						.compareTo(rt.toValueString(tpl));
				return new MyBoolean(ev.getKernel(), comp > 0);
			}
			throw ev.illegalComparison(lt, rt, ">");
		}
	},
	LESS_EQUAL {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {
			if (lt instanceof NumberValue && rt instanceof NumberValue) {

				double a = lt.evaluateDouble();
				double b = rt.evaluateDouble();
				boolean defined = MyDouble.isFinite(a) && MyDouble.isFinite(b);

				return new MyBoolean(ev.getKernel(),
						DoubleUtil.isGreaterEqual(b, a), defined);
			}
			if (lt instanceof TextValue && rt instanceof TextValue) {
				int comp = lt.toValueString(tpl)
						.compareTo(rt.toValueString(tpl));
				return new MyBoolean(ev.getKernel(), comp <= 0);
			}
			throw ev.illegalComparison(lt, rt,
					ExpressionNodeConstants.strLESS_EQUAL);
		}
	},
	GREATER_EQUAL {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {
			if (lt instanceof NumberValue && rt instanceof NumberValue) {

				double a = lt.evaluateDouble();
				double b = rt.evaluateDouble();
				boolean defined = MyDouble.isFinite(a) && MyDouble.isFinite(b);

				return new MyBoolean(ev.getKernel(),
						DoubleUtil.isGreaterEqual(a, b), defined);
			}
			if (lt instanceof TextValue && rt instanceof TextValue) {
				int comp = lt.toValueString(tpl)
						.compareTo(rt.toValueString(tpl));
				return new MyBoolean(ev.getKernel(), comp >= 0);
			}
			throw ev.illegalComparison(lt, rt,
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
			throw ev.illegalComparison(lt, rt,
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
			throw ev.illegalComparison(lt, rt,
					ExpressionNodeConstants.strPERPENDICULAR);
		}
	},
	IS_SUBSET_OF {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {
			if (lt instanceof ListValue && rt instanceof ListValue) {
				return new MyBoolean(ev.getKernel(),
						MyList.listContains(((ListValue) rt).getMyList(),
								((ListValue) lt).getMyList(), tpl));
			}
			throw ev.illegalListOp(lt, rt,
					ExpressionNodeConstants.strIS_SUBSET_OF);
		}
	},
	IS_SUBSET_OF_STRICT {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {
			if (lt instanceof ListValue && rt instanceof ListValue) {
				return new MyBoolean(ev.getKernel(),
						MyList.listContainsStrict(((ListValue) rt).getMyList(),
								((ListValue) lt).getMyList(), tpl));
			}
			throw ev.illegalListOp(lt, rt,
					ExpressionNodeConstants.strIS_SUBSET_OF_STRICT);
		}
	},
	IS_ELEMENT_OF {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {
			if (rt instanceof ListValue) {
				return new MyBoolean(ev.getKernel(),
						MyList.isElementOf(lt, ((ListValue) rt).getMyList()));
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

			throw ev.illegalListOp(lt, rt,
					ExpressionNodeConstants.strIS_ELEMENT_OF);
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
			throw ev.illegalListOp(lt, rt,
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
	PLUSMINUS {

		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {
			MyList ret = new MyList(ev.getKernel(), true);
			if (left.wrap().containsFreeFunctionVariable(null)
					|| right.wrap().containsFreeFunctionVariable(null)) {
				ExpressionValue[] leftParts = expandPlusMinus(new ExpressionNode(ev.getKernel(),
						left, Operation.PLUSMINUS, right), ev.getKernel());
				for (ExpressionValue part: leftParts) {
					ret.addListElement(ev.getKernel().getAlgebraProcessor()
							.makeFunctionNVar(part.wrap()));
				}
			} else if (rt instanceof MyNumberPair) {
					ret.addListElement(MyList.get(lt, 0));
					ret.addListElement(ExpressionNode
							.unaryMinus(ev.getKernel(), MyList.get(lt, 1))
							.evaluate(tpl));
			} else {
				ret.addListElement(
						ev.handlePlus(MyList.get(lt, 0), MyList.get(rt, 0),
								StringTemplate.defaultTemplate, false));

				ret.addListElement(ev.handleMinus(MyList.get(lt, 1),
						MyList.get(rt, 1)));
			}
			return ret;
		}

		private void add(MyList ret, ExpressionValue lt,
				ExpressionValue rt, final Operation op) {
			Traversing pmSimplifier = new Traversing() {

				@Override
				public ExpressionValue process(ExpressionValue ev) {
					if (ev.isExpressionNode()) {
						ExpressionNode en = (ExpressionNode) ev;
						if (en.getOperation() == Operation.PLUSMINUS) {
							en.setOperation(op);
						}
					}
					return ev;
				}

			};
			ret.addListElement(
					ret.getKernel().getAlgebraProcessor()
							.makeFunctionNVar(lt.wrap().apply(op, rt)
									.deepCopy(ret.getKernel())
									.traverse(pmSimplifier).wrap()));

		}
	},
	VECTORPRODUCT {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {

			return ev.handleVectorProduct(lt, rt);
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
				double x = lt.evaluateDouble();
				double ret = Double.NaN;

				ListValue list = (ListValue) rt;
				if (list instanceof GeoList && ((GeoList) list)
						.getElementType() != GeoClass.NUMERIC) {
					return new MyDouble(ev.getKernel(), Double.NaN);
				}
				int n = list.size() - 3;
				if (n >= 1) {
					double min = (list.getListElement(0)).evaluateDouble();
					double max = (list.getListElement(1)).evaluateDouble();

					if ((min > max) || (x > max) || (x < min)) {
						return new MyDouble(ev.getKernel(), Double.NaN);
					}

					double step = (max - min) / n;

					int index = (int) Math.floor((x - min) / step);

					if (index > (n - 1)) {
						ret = (list.getListElement(n + 2)).evaluateDouble();
					} else {

						double y1 = (list.getListElement(index + 2))
								.evaluateDouble();
						double y2 = (list.getListElement(index + 3))
								.evaluateDouble();
						double x1 = min + (index * step);

						// linear interpolation between (x1,y1) and
						// (x2,y2+step) to give (x,ret)
						ret = y1 + (((x - x1) * (y2 - y1)) / step);
					}
				}

				return new MyDouble(ev.getKernel(), ret);

			}
			throw ev.illegalArgument(lt, rt, "freehand(");
		}
	},
	DATA {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {
			if (lt instanceof NumberValue && rt instanceof MyNumberPair) {
				double x = lt.evaluateDouble();
				ListValue keyList = (ListValue) ((MyNumberPair) rt).getX();
				ListValue valueList = (ListValue) ((MyNumberPair) rt).getY();
				if (keyList.size() < 1) {
					return new MyDouble(ev.getKernel(), Double.NaN);
				}
				double max = keyList.getListElement(keyList.size() - 1)
						.evaluateDouble();
				if (keyList.size() == 1) {
					double ret = Double.NaN;
					if (DoubleUtil.isEqual(max, x)) {
						ret = valueList.getListElement(0).evaluateDouble();
					}
					return new MyDouble(ev.getKernel(), ret);
				}
				double min = keyList.getListElement(0).evaluateDouble();
				if (max < x || min > x) {
					return new MyDouble(ev.getKernel(), Double.NaN);
				}
				int index = (int) (keyList.size() * (x - min) / (max - min));
				index = Math.max(Math.min(index, keyList.size() - 1), 0);
				while (index > 0 && keyList.getListElement(index)
						.evaluateDouble() >= x) {
					index--;
				}
				while (index < keyList.size() - 1 && keyList
						.getListElement(index + 1).evaluateDouble() < x) {
					index++;
				}
				double x1 = keyList.getListElement(index).evaluateDouble();
				double x2 = keyList.getListElement(index + 1).evaluateDouble();
				double y1 = valueList.getListElement(index).evaluateDouble();
				double y2 = valueList.getListElement(index + 1)
						.evaluateDouble();
				return new MyDouble(ev.getKernel(),
						((x - x1) * y2 + y1 * (x2 - x)) / (x2 - x1));
			}
			throw ev.illegalArgument(lt, rt, "dataFunction(");
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
			throw ev.polynomialOrDie(lt, "cos(");

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
			throw ev.polynomialOrDie(lt, "sin(");

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
			throw ev.polynomialOrDie(lt, "tan(");

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

			} else {
				throw ev.polynomialOrDie(lt, "exp(");
			}

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

			} else {
				throw ev.polynomialOrDie(lt, "ln(");
			}
		}
	},
	ARCCOS {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {
			if (lt instanceof NumberValue) {
				return ((NumberValue) lt).getNumber().acos(false);
			}
			throw ev.polynomialOrDie(lt, "acos(");

		}
	},
	/*
	 * always returns angle in degrees
	 */
	ARCCOSD {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {
			if (lt instanceof NumberValue) {
				return ((NumberValue) lt).getNumber().acos(true);
			}
			throw ev.polynomialOrDie(lt, "acosd(");

		}
	},
	ARCSIN {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {
			if (lt instanceof NumberValue) {
				return ((NumberValue) lt).getNumber().asin(false);
			}
			throw ev.polynomialOrDie(lt, "asin(");

		}
	},
	/*
	 * always returns angle in degrees
	 */
	ARCSIND {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {
			if (lt instanceof NumberValue) {
				return ((NumberValue) lt).getNumber().asin(true);
			}
			throw ev.polynomialOrDie(lt, "asind(");

		}
	},
	ARCTAN {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {
			if (lt instanceof NumberValue) {
				return ((NumberValue) lt).getNumber().atan(false);
			}
			throw ev.polynomialOrDie(lt, "atan(");

		}
	},
	/*
	 * always returns angle in degrees
	 */
	ARCTAND {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {
			if (lt instanceof NumberValue) {
				return ((NumberValue) lt).getNumber().atan(true);
			}
			throw ev.polynomialOrDie(lt, "atand(");

		}
	},
	ARCTAN2 {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {
			if (lt instanceof NumberValue && rt instanceof NumberValue) {
				return ((NumberValue) lt).getNumber()
						.atan2((NumberValue) rt, false).getNumber();
			}
			throw ev.illegalArgument(lt, rt, "arctan2(");
		}
	},
	ARCTAN2D {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {
			if (lt instanceof NumberValue && rt instanceof NumberValue) {
				return ((NumberValue) lt).getNumber()
						.atan2((NumberValue) rt, true).getNumber();
			}
			throw ev.illegalArgument(lt, rt, "arctan2d(");
		}
	},
	NROOT {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {
			Kernel kernel = ev.getKernel();
			if (rt instanceof NumberValue) {
				double n = rt.evaluateDouble();
				MyDouble exp = new MyDouble(kernel, 1 / n);

				if (lt instanceof NumberValue) {
					MyDouble root = ((NumberValue) lt).getNumber();
					if (0 > root.getDouble()
							&& DoubleUtil.isInteger(n) && Math.round(n) % 2 == 1) {
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
			throw ev.illegalArgument(lt, rt, "nroot(");
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

			} else {
				throw ev.polynomialOrDie(lt, "sqrt(");
			}
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
				Geo3DVecInterface vec3d = ((Vector3DValue) lt).getVector();

				// complex Abs(z)
				// or magnitude of point

				return new MyDouble(kernel, vec3d.length());

			} else {
				throw ev.polynomialOrDie(lt, "abs(");
			}
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
			throw ev.polynomialOrDie(lt, "sgn(");
		}
	},
	XCOORD {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {
			return new MyDouble(ev.getKernel(), ev.handleXcoord(lt, this));
		}
	},
	YCOORD {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {
			return new MyDouble(ev.getKernel(), ev.handleYcoord(lt, this));
		}
	},
	ZCOORD {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {
			return new MyDouble(ev.getKernel(), ev.handleZcoord(lt));
		}
	},
	IMAGINARY {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {
			return new MyDouble(ev.getKernel(), ev.handleYcoord(lt, this));

		}
	},
	REAL {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {
			return new MyDouble(ev.getKernel(), ev.handleXcoord(lt, this));

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
			throw ev.polynomialOrDie(lt, "fractionalPart(");
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
			throw ev.polynomialOrDie(lt, "cosh(");

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
			throw ev.polynomialOrDie(lt, "sinh(");

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
			throw ev.polynomialOrDie(lt, "tanh(");

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
			throw ev.polynomialOrDie(lt, "acosh(");

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
			throw ev.polynomialOrDie(lt, "asinh(");

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
			throw ev.polynomialOrDie(lt, "atanh(");
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
			throw ev.polynomialOrDie(lt, "csc(");
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
			throw ev.polynomialOrDie(lt, "sec(");
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
			throw ev.polynomialOrDie(lt, "cot(");
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
			throw ev.polynomialOrDie(lt, "csch(");
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
			throw ev.polynomialOrDie(lt, "sech(");
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
			throw ev.polynomialOrDie(lt, "coth(");
		}
	},
	FLOOR {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {
			if (lt instanceof NumberValue) {
				return ((NumberValue) lt).getNumber()
						.floor(ev.getKernel().getAngleUnit());
			}
			if (lt instanceof VectorValue) {
				GeoVec2D ret = ((VectorValue) lt).getVector().floor();
				// eg complex
				ret.setMode(((VectorValue) lt).getToStringMode());
				return ret;
			}
			if (lt instanceof Vector3DValue) {
				return ((Vector3DValue) lt).getVector()
						.floor();
			}
			throw ev.polynomialOrDie(lt, "floor(");
		}
	},
	CEIL {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {
			if (lt instanceof NumberValue) {
				return ((NumberValue) lt).getNumber()
						.ceil(ev.getKernel().getAngleUnit());
			}
			if (lt instanceof VectorValue) {
				GeoVec2D ret = ((VectorValue) lt).getVector().ceil();
				// eg complex
				ret.setMode(((VectorValue) lt).getToStringMode());
				return ret;
			}
			if (lt instanceof Vector3DValue) {
				return ((Vector3DValue) lt).getVector().ceil();
			}
			throw ev.polynomialOrDie(lt, "ceil(");
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
			throw ev.polynomialOrDie(lt, "", "!");
		}
	},
	ROUND {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {
			if (lt instanceof NumberValue) {
				return ((NumberValue) lt).getNumber()
						.round(ev.getKernel().getAngleUnit());
			}
			if (lt instanceof VectorValue) {
				GeoVec2D ret = ((VectorValue) lt).getVector().round();
				// eg complex
				ret.setMode(((VectorValue) lt).getToStringMode());
				return ret;
			}
			if (lt instanceof Vector3DValue) {
				return ((Vector3DValue) lt).getVector()
						.round();
			}
			throw ev.polynomialOrDie(lt, "round(");
		}
	},
	ROUND2 {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {
			if (lt instanceof NumberValue) {
				if (rt instanceof NumberValue
						&& (!Double.isNaN(rt.evaluateDouble())
								|| rt.isGeoElement())) {
					return ((NumberValue) lt).getNumber().round(
							rt.evaluateDouble(),
							ev.getKernel().getAngleUnit());
				}
				return ((NumberValue) lt).getNumber()
						.round(ev.getKernel().getAngleUnit());
			}
			if (lt instanceof VectorValue) {
				GeoVec2D ret = ((VectorValue) lt).getVector().round();
				// eg complex
				ret.setMode(((VectorValue) lt).getToStringMode());
				return ret;
			}
			if (lt instanceof Vector3DValue) {
				return ((Vector3DValue) lt).getVector()
						.round();
			}
			throw ev.polynomialOrDie(lt, "round(");
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
			throw ev.polynomialOrDie(lt, "gamma(");
		}
	},
	GAMMA_INCOMPLETE {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {
			if (lt instanceof NumberValue && rt instanceof NumberValue) {
				return ((NumberValue) rt).getNumber()
						.gammaIncomplete((NumberValue) lt);
			}
			throw ev.illegalArgument(lt, rt, "gammaIncomplete");
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
			throw ev.illegalArgument(lt, rt, "gammaIncompleteRegularized");
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
			throw ev.illegalArgument(lt, rt, "beta(");
		}
	},
	BETA_INCOMPLETE {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {
			if (lt instanceof VectorValue && rt instanceof NumberValue) {
				return ((NumberValue) rt).getNumber()
						.betaIncomplete((VectorValue) lt);
			}
			throw ev.illegalArgument(lt, rt, "betaIncomplete(");
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
			throw ev.illegalArgument(lt, rt, "betaIncompleteRegularized(");
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
			throw ev.polynomialOrDie(lt, "erf(");
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
			throw ev.polynomialOrDie(lt, "psi(");
		}
	},
	POLYGAMMA {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {
			if (lt instanceof NumberValue && rt instanceof NumberValue) {
				return ((NumberValue) rt).getNumber()
						.polygamma((NumberValue) lt);
			}
			throw ev.polynomialOrDie(lt, "polygamma(");
		}
	},
	LAMBERTW {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {
			double branch = 0;
			if (rt instanceof NumberValue) {
				branch = rt.evaluateDouble();
				if (Double.isNaN(branch)) {
					branch = 0;
				}
			}
			if (lt instanceof NumberValue) {
				return ((NumberValue) lt).getNumber().lambertW(branch);
			}
			throw ev.polynomialOrDie(lt, "LambertW(");
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
			throw ev.polynomialOrDie(lt, "log10(");
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
			throw ev.polynomialOrDie(lt, "log2(");
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
			throw ev.illegalArgument(lt, rt, "log(");
		}
	},
	NPR {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev, ExpressionValue lt,
				ExpressionValue rt, ExpressionValue left, ExpressionValue right, StringTemplate tpl,
				boolean holdsLaTeX) {
			if (lt instanceof NumberValue && rt instanceof NumberValue) {
				return new MyDouble(ev.getKernel(),
						AlgoNpR.nPr(lt.evaluateDouble(), rt.evaluateDouble()));
			}
			throw ev.illegalArgument(lt, rt, "nPr(");
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
			throw ev.polynomialOrDie(lt, "cosIntegral(");
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
			throw ev.polynomialOrDie(lt, "sinIntegral(");
		}
	},
	EI {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {
			if (lt instanceof NumberValue) {
				return ((NumberValue) lt).getNumber().expIntegral();
			} else if (lt instanceof VectorValue) {
				GeoVec2D vec = ((VectorValue) lt).getVector();
				return vec.ei();
			}
			throw ev.polynomialOrDie(lt, "expIntegral(");
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
			throw ev.polynomialOrDie(lt, "cbrt(");
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
			throw ev.polynomialOrDie(lt, "conjugate(");
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

				MyDouble ret = new MyDouble(kernel,
						Math.atan2(vec.getY(), vec.getX()));
				ret.setAngle();
				return ret;
			} else if (lt instanceof NumberValue) {

				double num = lt.evaluateDouble();

				if (MyDouble.isFinite(num)) {

					return new MyDouble(kernel, num < 0 ? Math.PI : 0);
				}
				return new MyDouble(kernel, Double.NaN);
			}
			throw ev.polynomialOrDie(lt, "arg(");
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
				Geo3DVecInterface vec = ((Vector3DValue) lt).getVector();
				double l = MyMath.length(vec.getX(), vec.getY());
				MyDouble ret = new MyDouble(kernel, Math.atan2(vec.getZ(), l));
				ret.setAngle();
				return ret;
			}
			throw ev.polynomialOrDie(lt, "alt(");
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

				if (lt instanceof GeoCurveCartesianND) {
					return ((GeoCurveCartesianND) lt)
							.evaluateCurve(rt.evaluateDouble());
				}
				if (lt instanceof GeoLineND) {
					return ((GeoLineND) lt).evaluateCurve(rt.evaluateDouble());
				}
				if (lt instanceof GeoSurfaceCartesianND) {
					return ((GeoSurfaceCartesianND) lt).evaluateSurface(
							rt.evaluateDouble(), 0);
				}
				return ((ParametricCurve) lt)
						.evaluateCurve(rt.evaluateDouble());
			}
			if (rt instanceof ListValue) {
				ListValue arg = (ListValue) rt;

				if (lt instanceof GeoSurfaceCartesianND) {
					return ((GeoSurfaceCartesianND) lt).evaluateSurface(
							arg.getListElement(0).evaluateDouble(),
							arg.getListElement(1).evaluateDouble());
				}
				throw ev.illegalArgument(lt);
			}
			if (rt instanceof VectorValue) {
				GeoVec2D arg = ((VectorValue) rt).getVector();

				if (lt instanceof GeoSurfaceCartesianND) {
					return ((GeoSurfaceCartesianND) lt).evaluateSurface(
							arg.getX(), arg.getY());
				}
				throw ev.illegalArgument(lt);
			}

			throw ev.illegalArgument(rt);
		}
	},

	DERIVATIVE {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {
			if (rt instanceof NumberValue) {
				if (lt instanceof Functional) { // derivative of GeoFunction
					return ((Functional) lt).getGeoDerivative(
							(int) Math.round(rt.evaluateDouble()),
							true);
				} else if (lt instanceof GeoCurveCartesianND) { // derivative of
																// GeoCurveCartesian
					return ((GeoCurveCartesianND) lt).getGeoDerivative(
							(int) Math.round(rt.evaluateDouble()));
				}
			}
			throw ev.illegalArgument(rt);
		}
	},
	ELEMENT_OF {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {
			return ev.handleElementOf(lt, rt, 0);

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
				return ev.handleIf(lt, rt);
			}
			throw ev.illegalArgument(lt, rt, "if(");
		}
	},
	IF_SHORT {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev, ExpressionValue lt,
				ExpressionValue rt, ExpressionValue left, ExpressionValue right, StringTemplate tpl,
				boolean holdsLaTeX) {
			if (lt instanceof BooleanValue) {
				return ev.handleIf(lt, rt);
			}
			throw ev.illegalCondition();
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
			throw ev.illegalArgument(lt, rt, "if(");
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
				return cond.size() == ((MyList) rt).size()
						? new MyDouble(ev.getKernel(), Double.NaN)
						: ((MyList) rt).getListElement(cond.size())
								.evaluate(tpl);
			}

			throw ev.illegalArgument(lt, rt, "if(");
		}
	},

	// spreadsheet absolute reference using $ signs
	DOLLAR_VAR_ROW {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {
			return lt;
		}
	},
	DOLLAR_VAR_COL {
		@Override
		public ExpressionValue handle(ExpressionNodeEvaluator ev,
				ExpressionValue lt, ExpressionValue rt, ExpressionValue left,
				ExpressionValue right, StringTemplate tpl, boolean holdsLaTeX) {
			return lt;
		}
	},
	DOLLAR_VAR_ROW_COL {
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
	INVERSE_NORMAL {
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

			} else {
				throw ev.polynomialOrDie(lt, "zeta(");
			}
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
			if (list.size() == 3) {
				return new MyVec3DNode(ev.getKernel(),
						MyList.getCell(list, 0, 0), MyList.getCell(list, 0, 1),
						MyList.getCell(list, 0, 2));
			}
			return new MyVecNode(ev.getKernel(), MyList.getCell(list, 0, 0),
					MyList.getCell(list, 0, 1));
		}
	};

	protected ExpressionValue[] expandPlusMinus(ExpressionNode exp, Kernel kernel) {
		ExpressionValue[] expand = new ExpressionValue[2];
		if (exp == null || exp.isLeaf()) {
			expand[0] = expand[1] = exp;
		} else {
			Operation operation = exp.getOperation();
			if (operation == Operation.PLUSMINUS) {
				if (exp.getRight() instanceof MyNumberPair) {
					ExpressionValue[] expandLeft = expandPlusMinus(exp.getLeftTree(), kernel);
					expand[0] = expandLeft[0];
					expand[1] = ExpressionNode.unaryMinus(kernel, expandLeft[0]);
				} else {
					expandPlusMinusOperationNode(exp, kernel,
							Operation.PLUS, Operation.MINUS, expand);
				}
			} else {
				expandPlusMinusOperationNode(exp, kernel, operation, operation, expand);
			}
		}
		return expand;
	}

	protected void expandPlusMinusOperationNode(ExpressionNode wrap, Kernel kernel,
			Operation plusOp, Operation minusOp, ExpressionValue[] expand) {
		ExpressionValue[] expandLeft = expandPlusMinus(wrap.getLeftTree(), kernel);
		ExpressionValue[] expandRight = expandPlusMinus(wrap.getRightTree(), kernel);
		expand[0] = new ExpressionNode(kernel, expandLeft[0], plusOp, expandRight[0]);
		expand[1] = new ExpressionNode(kernel, expandLeft[1], minusOp, expandRight[1]);
	}

	public boolean isUnary() {
		return this == NO_OPERATION || isSimpleFunction(this);
	}

	/**
	 * @param op
	 *            operation
	 * @return whether operation is a real->real function
	 */
	public static boolean isSimpleFunction(Operation op) {
		switch (op) {
		case SIN:
		case COS:
		case TAN:
		case ARCSIN:
		case ARCSIND:
		case ARCCOS:
		case ARCCOSD:
		case ARCTAN:
		case ARCTAND:
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

	/**
	 * @return true if it's a trig function that takes degrees as input ie not
	 *         inverse, not hyperbolic
	 */
	public boolean hasDegreeInput() {
		switch (this) {
		case SIN:
		case COS:
		case TAN:
		case CSC:
		case SEC:
		case COT:
			return true;
		}
		return false;
	}

	/**
	 * @return true if it's a trig function that returns degrees
	 */
	public boolean doesReturnDegrees() {
		switch (this) {
		case ARCSIND:
		case ARCCOSD:
		case ARCTAND:
		case ARCTAN2D:
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

	/**
	 * @return whether this is an inequality sign
	 */
	public boolean isInequality() {
		return this.equals(GREATER_EQUAL) || this.equals(GREATER)
				|| this.equals(LESS) || this.equals(LESS_EQUAL);
	}

	public boolean isInequalityLess() {
		return this.equals(LESS) || this.equals(LESS_EQUAL);
	}

	/**
	 * 
	 * @param op
	 *            operation eg Operation.SIN
	 * @return inverse of op eg Operation.ARCSIN
	 */
	public static Operation inverse(Operation op) {
		switch (op) {
		case PLUS:
			return Operation.MINUS;
		case MINUS:
			return Operation.PLUS;
		case MULTIPLY:
			return Operation.DIVIDE;
		case DIVIDE:
			return Operation.MULTIPLY;
		case SIN:
			return Operation.ARCSIN;
		case COS:
			return Operation.ARCCOS;
		case TAN:
			return Operation.ARCTAN;
		case ARCSIN:
		case ARCSIND:
			return Operation.SIN;
		case ARCCOS:
			return Operation.COS;
		case ARCTAN:
			return Operation.TAN;
		case SINH:
			return Operation.ASINH;
		case COSH:
			return Operation.ACOSH;
		case TANH:
			return Operation.ATANH;
		case ASINH:
			return Operation.SINH;
		case ACOSH:
			return Operation.COSH;
		case ATANH:
			return Operation.TANH;
		case EXP:
			return Operation.LOG;
		case LOG:
			return Operation.EXP;
		default:
			return null;
		}
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

	/**
	 * @param op
	 *            operation
	 * @return whether operation is one of (freehand, data)
	 */
	public static boolean includesFreehandOrData(Operation op) {
		switch (op) {
		case DATA:
		case FREEHAND:

			return true;
		}

		return false;
	}

	/**
	 * @param op
	 *            operation
	 * @return whether integral of this function is not continuous
	 */
	public static boolean integralIsNonContinuous(Operation op) {

		switch (op) {
		case ABS:
		case SGN:
		case FLOOR:
		case CEIL:
		case ROUND:
		case ROUND2:
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
		case IF_SHORT:
		case IF_ELSE:
		case IF_LIST:
		case DATA:
		case FREEHAND:

			return true;
		}

		return false;
	}

	public boolean isIf() {
		return this == IF || this == IF_SHORT;
	}

}
