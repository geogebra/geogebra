package org.geogebra.common.kernel.arithmetic;

import org.geogebra.common.kernel.CASGenericInterface;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.variable.Variable;
import org.geogebra.common.kernel.arithmetic3D.MyVec3DNode;
import org.geogebra.common.kernel.geos.GeoCasCell;
import org.geogebra.common.kernel.geos.GeoDummyVariable;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoSymbolic;
import org.geogebra.common.kernel.kernelND.GeoCurveCartesianND;
import org.geogebra.common.kernel.kernelND.GeoSurfaceCartesianND;
import org.geogebra.common.plugin.Operation;

/**
 * Expands f as f(x) or f(x,y) in CAS
 *
 * @author Zbynek Konecny
 */
public class FunctionExpander implements Traversing {
	// store function variables if needed
	private FunctionVariable[] variables = null;
	private int constructionIndex;

	public FunctionExpander() {
		this(Integer.MAX_VALUE);
	}

	public FunctionExpander(int constructionIndex) {
		this.constructionIndex = constructionIndex;
	}

	private FunctionExpander(GeoElement element) {
		this(element == null ? Integer.MAX_VALUE : element.getConstructionIndex());
	}

	private ExpressionValue expand(GeoElement geo) {
		if (geo instanceof FunctionalNVar) {
			return ((FunctionalNVar) geo).getFunctionExpression()
					.deepCopy(geo.getKernel()).traverse(this);
		}
		if (geo instanceof GeoCasCell) {
			return ((GeoCasCell) geo).getValue()
					.deepCopy(geo.getKernel()).traverse(this).unwrap();
		}
		return geo;
	}

	private boolean contains(GeoDummyVariable gdv) {
		if (variables == null) {
			return false;
		}
		for (FunctionVariable funvar : variables) {
			if (funvar.toString(StringTemplate.defaultTemplate).equals(
					gdv.toString(StringTemplate.defaultTemplate))) {
				return true;
			}
		}
		return false;
	}

	@Override
	public ExpressionValue process(ExpressionValue ev) {
		if (ev instanceof ExpressionNode) {
			boolean surfaceNoComplex = false;
			final ExpressionNode en = (ExpressionNode) ev;
			if (en.getOperation() == Operation.FUNCTION
					|| en.getOperation() == Operation.FUNCTION_NVAR
					|| en.getOperation() == Operation.VEC_FUNCTION) {
				ExpressionValue geo = en.getLeft().unwrap();
				ExpressionValue deriv = null;
				if (geo.isOperation(Operation.DERIVATIVE)) {
					// template not important, right it is a constant
					// MyDouble anyway
					deriv = ((ExpressionNode) geo).getRight().evaluate(
							StringTemplate.defaultTemplate);
					geo = ((ExpressionNode) geo).getLeft().unwrap();
				}
				if (geo instanceof GeoDummyVariable) {
					geo = ((GeoDummyVariable) geo).getElementWithSameName();
				}
				ExpressionNode en2 = null;
				FunctionVariable[] fv = null;
				if (geo instanceof GeoCurveCartesianND) {
					Kernel kernel = ((GeoCurveCartesianND) geo).getKernel();
					ExpressionValue en2x = ((GeoCurveCartesianND) geo)
							.getFun(0).getFunctionExpression().getCopy(kernel)
							.traverse(this);
					ExpressionValue en2y = ((GeoCurveCartesianND) geo)
							.getFun(1).getFunctionExpression()
									.getCopy(kernel).traverse(this);
					if (((GeoCurveCartesianND) geo).getDimension() > 2) {
						ExpressionValue en2z = ((GeoCurveCartesianND) geo)
								.getFun(2).getFunctionExpression()
								.getCopy(kernel).traverse(this);
						en2 = new MyVec3DNode(kernel, en2x, en2y, en2z).wrap();
					} else {
						en2 = new MyVecNode(kernel, en2x, en2y).wrap();
					}

					fv = ((GeoCurveCartesianND) geo).getFunctionVariables();
				}
				if (geo instanceof FunctionalNVar) {
					en2 = (ExpressionNode) ((FunctionalNVar) geo)
							.getFunctionExpression()
							.getCopy(((FunctionalNVar) geo).getKernel())
							.traverse(this);
					fv = ((FunctionalNVar) geo).getFunction()
							.getFunctionVariables();
				} else if (geo instanceof GeoSymbolic) {
					GeoSymbolic symbolic = (GeoSymbolic) geo;
					FunctionExpander expander = newFunctionExpander(symbolic);
					en2 = (ExpressionNode) symbolic.getValue().wrap()
							.getCopy(symbolic.getKernel()).traverse(expander);
					fv = ((GeoSymbolic) geo).getFunctionVariables();
				}
				if (geo instanceof GeoCasCell) {
					ValidExpression ve = ((GeoCasCell) geo)
							.getValue();
					// related to #4126 -- maybe not needed though
					if (((GeoCasCell) geo).isKeepInputUsed()) {
						ve = expand((GeoCasCell) geo).wrap();
					}
					en2 = ve.unwrap() instanceof FunctionNVar ? ((FunctionNVar) ve
							.unwrap()).getExpression() : ve.wrap();

					en2 = en2.traverse(this).wrap();
					if (en2.getLeft() instanceof GeoSurfaceCartesianND) {
						FunctionNVar[] fun = ((GeoSurfaceCartesianND) en2
								.getLeft()).getFunctions();
						MyVecNDNode vector;
						if (fun.length > 2) {
							vector = new MyVec3DNode(
									((ExpressionNode) ev).getKernel(),
									fun[0].getExpression(),
									fun[1].getExpression(),
									fun[2].getExpression());
						} else {
							vector = new MyVecNode(
									((ExpressionNode) ev).getKernel(),
									fun[0].getExpression(),
									fun[1].getExpression());
						}
						en2 = new ExpressionNode(en.getKernel(), vector);
						if (en.getRight() instanceof MyList
								&& ((MyList) en.getRight()).get(
										0) instanceof ExpressionNode
								&& ((ExpressionNode) ((MyList) en.getRight())
										.get(0)).getLeft() instanceof MyList) {
							en.setRight(((ExpressionNode) ((MyList) en
									.getRight()).get(0)).getLeft());
						}
					} else {
						en2 = en2.getCopy(((GeoCasCell) geo).getKernel());
					}
					fv = ((GeoCasCell) geo).getFunctionVariables();
				}
				if (geo instanceof GeoSurfaceCartesianND) {
					if (en.getRight() instanceof MyList
							&& ((MyList) en.getRight()).get(
							0) instanceof ExpressionNode
							&& ((ExpressionNode) ((MyList) en.getRight())
							.get(0))
							.getLeft() instanceof MyList) {
						en.setRight(((ExpressionNode) ((MyList) en.getRight())
								.get(0)).getLeft());
					}
					GeoSurfaceCartesianND geoSurface = (GeoSurfaceCartesianND) geo;
					Kernel kernel = geoSurface.kernel;
					fv = geoSurface.getFunctionVariables();
					if (geoSurface.getComplexVariable() != null) {
						en2 = geoSurface.getDefinition().deepCopy(kernel);
					} else {
						surfaceNoComplex = true;
						FunctionNVar[] fun = ((GeoSurfaceCartesianND) geo)
								.getFunctions();
						MyVecNDNode vect;
						if (fun.length > 2) {
							vect = new MyVec3DNode(
									((ExpressionNode) ev).getKernel(),
									fun[0].getExpression().deepCopy(kernel),
									fun[1].getExpression().deepCopy(kernel),
									fun[2].getExpression().deepCopy(kernel));
						} else {
							vect = new MyVecNode(
									((ExpressionNode) ev).getKernel(),
									fun[0].getExpression().deepCopy(kernel),
									fun[1].getExpression().deepCopy(kernel));
						}
						en2 = new ExpressionNode(en.getKernel(), vect);
					}
				}
				if (deriv != null) {
					CASGenericInterface cas = en.getKernel().getGeoGebraCAS()
							.getCurrentCAS();
					Command derivCommand = new Command(en.getKernel(),
							"Derivative", false);
					derivCommand.addArgument(en2);
					if (fv != null && fv.length > 0) {
						derivCommand.addArgument(fv[0].wrap());
					}
					derivCommand.addArgument(deriv.wrap());
					en2 = cas.evaluateToExpression(derivCommand, null,
							en.getKernel()).wrap();

				}
				if (fv != null) {
					return replaceFunctionVariables(en, en2, fv, surfaceNoComplex);
				}
			} else if (en.getOperation() == Operation.DERIVATIVE) {
				// should not get there

			} else {
				GeoElement geo = null;
				if (en.getLeft() instanceof GeoDummyVariable
						&& !contains((GeoDummyVariable) en.getLeft())) {
					geo = ((GeoDummyVariable) en.getLeft())
							.getElementWithSameName();
					if (geo != null && hasLowerConstructionIndex(geo)) {
						en.setLeft(expand(geo));
					}
				}
				if (en.getLeft() instanceof Variable) {
					geo = ((Variable) en.getLeft())
							.getKernel()
							.getConstruction()
							.lookupLabel(
									en.getLeft().toString(
											StringTemplate.defaultTemplate));
					if (geo != null) {
						ExpressionNode en2 = (ExpressionNode) ((FunctionalNVar) geo)
								.getFunctionExpression()
								.getCopy(((FunctionalNVar) geo).getKernel())
								.traverse(this);
						return en2;
					}
				}

			}
			if (en.getRight() != null) {
				GeoElement geo = null;
				if (en.getRight() instanceof GeoDummyVariable
						&& !contains((GeoDummyVariable) en.getRight())) {
					geo = ((GeoDummyVariable) en.getRight())
							.getElementWithSameName();
					if (geo != null && hasLowerConstructionIndex(geo)) {
						en.setRight(expand(geo));
					}
				}
			}
		} else if (ev instanceof GeoDummyVariable
				&& !contains((GeoDummyVariable) ev)) {
			GeoElement geo = ((GeoDummyVariable) ev).getElementWithSameName();
			if (geo != null && hasLowerConstructionIndex(geo)) {
				return expand(geo);
			}
		} else if (ev instanceof GeoCasCell) {
			// expanding the cell here is necessary #4126
			if (((GeoCasCell) ev).isKeepInputUsed()) {
				return expand((GeoCasCell) ev);
			}
			if (((GeoCasCell) ev).getValue() != null) {
				return ((GeoCasCell) ev).getValue().wrap()
					.getCopy(((GeoCasCell) ev).getKernel());
			}
		} else if (ev instanceof FunctionNVar) {
			variables = ((FunctionNVar) ev).fVars;
		}

		return ev;
	}

	private ExpressionValue replaceFunctionVariables(ExpressionNode en, ExpressionNode en2,
			FunctionVariable[] fv, boolean surfaceNoComplex) {
		ExpressionValue argument = en.getRight().wrap()
				.getCopy(en.getKernel()).traverse(this).unwrap();
		ExpressionValue ithArg = argument;
		VariableReplacer vr = en
				.getKernel().getVariableReplacer();

		// some heuristic to apply f(list) piecewise for simple functions, see APPS-4510
		if (en.isOperation(Operation.FUNCTION) && isListNotMatrix(argument)
				&& !en2.containsCommands()) {
			return new ExpressionNode(en.getKernel(), new Function(en2, fv[0]),
					Operation.FUNCTION, argument);
		}
		// variables have to be replaced with one traversing
		// or else replacing f(x,y) with f(y,x)
		// will result in f(x, x)
		for (int i = 0; i < fv.length; i++) {
			if (en.getOperation() == Operation.FUNCTION_NVAR || surfaceNoComplex) {
				ithArg = getElement(argument, i, fv.length);
			}
			vr.addVars(fv[i].getSetVarString(), ithArg);
		}
		return en2.traverse(vr).wrap();
	}

	private boolean isListNotMatrix(ExpressionValue argument) {
		return (argument instanceof MyList) && !((MyList) argument).isMatrix();
	}

	private ExpressionValue getElement(ExpressionValue argument, int i, int argLength) {
		if (argument instanceof MyList
				&& ((MyList) argument).size() == argLength) {
			return ((MyList) argument).get(i);
		} else {
			return VectorArithmetic.computeCoord(argument.wrap(), i);
		}
	}

	private boolean hasLowerConstructionIndex(GeoElement element) {
		int elementConstructionIndex = element.getConstructionIndex();
		return elementConstructionIndex > -1 && elementConstructionIndex < constructionIndex;
	}

	/**
	 * Creates a new Function Expander.
	 * @return function expander
	 */
	public static FunctionExpander newFunctionExpander() {
		return new FunctionExpander();
	}

	/**
	 * Creates a new function expander
	 * @param element geo element to expand
	 * @return function expander
	 */
	public static FunctionExpander newFunctionExpander(GeoElement element) {
		return new FunctionExpander(element);
	}
}