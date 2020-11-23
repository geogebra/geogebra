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
import org.geogebra.common.util.debug.Log;

/**
 * Expands f as f(x) or f(x,y) in CAS
 * 
 * @author Zbynek Konecny
 */
public class FunctionExpander implements Traversing {
	private static FunctionExpander collector = new FunctionExpander();
	// store function variables if needed
	private FunctionVariable[] variables = null;

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
			boolean surface = false;
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
					en2 = (ExpressionNode) ((GeoSymbolic) geo).getValue().wrap()
							.getCopy(((GeoSymbolic) geo).getKernel())
							.traverse(this);
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
						MyVec3DNode vect = new MyVec3DNode(
								((ExpressionNode) ev).getKernel(),
								fun[0].getExpression(), fun[1].getExpression(),
								fun[2].getExpression());
						en2 = new ExpressionNode(en.getKernel(), vect);
						if (en.getRight() instanceof MyList
								&& ((MyList) en.getRight()).getListElement(
										0) instanceof ExpressionNode
								&& ((ExpressionNode) ((MyList) en.getRight())
										.getListElement(0)).getLeft() instanceof MyList) {
							en.setRight(((ExpressionNode) ((MyList) en
									.getRight()).getListElement(0)).getLeft());
						}
					} else {
						en2 = en2.getCopy(((GeoCasCell) geo).getKernel());
					}
					fv = ((GeoCasCell) geo).getFunctionVariables();
				}
				if (geo instanceof GeoSurfaceCartesianND) {
					surface = true;
							if (en.getRight() instanceof MyList
									&& ((MyList) en.getRight()).getListElement(
											0) instanceof ExpressionNode
							&& ((ExpressionNode) ((MyList) en.getRight())
									.getListElement(0))
													.getLeft() instanceof MyList) {
						en.setRight(((ExpressionNode) ((MyList) en.getRight())
								.getListElement(0)).getLeft());
					}
					FunctionNVar[] fun = ((GeoSurfaceCartesianND) geo)
							.getFunctions();
					fv = fun[0].getFunctionVariables();
					Kernel kernel = fun[0].getKernel();
					MyVec3DNode vect = new MyVec3DNode(
							((ExpressionNode) ev).getKernel(), fun[0]
									.getExpression().deepCopy(kernel), fun[1]
									.getExpression().deepCopy(kernel), fun[2]
									.getExpression().deepCopy(kernel));
					en2 = new ExpressionNode(en.getKernel(), vect);
				}
				if (deriv != null) {
					CASGenericInterface cas = en.getKernel().getGeoGebraCAS()
							.getCurrentCAS();
					Command derivCommand = new Command(en.getKernel(),
							"Derivative", false);
					derivCommand.addArgument(en2);
					derivCommand.addArgument(fv[0].wrap());
					derivCommand.addArgument(deriv.wrap());
					en2 = cas.evaluateToExpression(derivCommand, null,
							en.getKernel()).wrap();

				}
				if (fv != null) {
					ExpressionValue argument = en.getRight().wrap()
							.getCopy(en.getKernel()).traverse(this).unwrap();
					ExpressionValue ithArg = argument;
					VariableReplacer vr = VariableReplacer.getReplacer(en
							.getKernel());

					// variables have to be replaced with one traversing
					// or else replacing f(x,y) with f(y,x)
					// will result in f(x, x)
					for (int i = 0; i < fv.length; i++) {
						if (en.getOperation() == Operation.FUNCTION_NVAR || surface) {
							if (argument instanceof MyList) {
								ithArg = ((MyList) argument).getListElement(i);
							} else {
								MyVecNDNode vec = (MyVecNDNode) argument;
								switch (i) {
								default:
									ithArg = null;
									Log.debug("problem in FunctionExpander " + i);
									break;
								case 0:
									ithArg = vec.getX();
									break;
								case 1:
									ithArg = vec.getY();
									break;
								case 2:
									ithArg = vec.getZ();
									break;
								}
							}
						}
						VariableReplacer.addVars(fv[i].getSetVarString(), ithArg);
					}
					en2 = en2.traverse(vr).wrap();
					return en2;
				}
			} else if (en.getOperation() == Operation.DERIVATIVE) {
				// should not get there

			} else {
				GeoElement geo = null;
				if (en.getLeft() instanceof GeoDummyVariable
						&& !contains((GeoDummyVariable) en.getLeft())) {
					geo = ((GeoDummyVariable) en.getLeft())
							.getElementWithSameName();
					if (geo != null) {
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
					if (geo != null) {
						en.setRight(expand(geo));
					}
						}
					}
		} else if (ev instanceof GeoDummyVariable
				&& !contains((GeoDummyVariable) ev)) {
			GeoElement geo = ((GeoDummyVariable) ev).getElementWithSameName();
			if (geo != null) {
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

	/**
	 * Resets and returns the collector
	 *
	 * @return function expander
	 */
	public static FunctionExpander getCollector() {
		collector.variables = null;
		return collector;
	}
}