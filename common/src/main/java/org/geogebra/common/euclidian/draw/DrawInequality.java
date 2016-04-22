package org.geogebra.common.euclidian.draw;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

import org.geogebra.common.awt.GArea;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.euclidian.Drawable;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.euclidian.GeneralPathClipped;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.arithmetic.FunctionalNVar;
import org.geogebra.common.kernel.arithmetic.IneqTree;
import org.geogebra.common.kernel.arithmetic.Inequality;
import org.geogebra.common.kernel.arithmetic.Inequality.IneqType;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.main.App;
import org.geogebra.common.plugin.Operation;

import edu.uci.ics.jung.graph.util.Pair;

/**
 * Graphical representation of inequality
 * 
 * @author Zbynek Konecny
 * 
 */
public class DrawInequality extends Drawable {

	private boolean isVisible;
	/** true if label is visible */
	boolean labelVisible;

	private Drawable drawable;
	private Operation operation = Operation.NO_OPERATION;
	private DrawInequality left, right;
	private Inequality ineq;
	private FunctionalNVar function;

	private boolean wasOR = false;
	private double maxBound = 1000000;
	private Drawable max;
	private double minBound = -1000000;
	private Drawable min;
	private ArrayList<Pair<Map<Double, Drawable>>> orBounds = new ArrayList<Pair<Map<Double, Drawable>>>();

	/**
	 * Creates new drawable linear inequality
	 * 
	 * @param view
	 *            view
	 * @param function
	 *            boolean 2-var function
	 */
	public DrawInequality(EuclidianView view, FunctionalNVar function) {
		this.view = view;
		geo = (GeoElement) function;
		this.function = function;
		operation = function.getIneqs().getOperation();
		if (function.getIneqs().getLeft() != null)
			left = new DrawInequality(function.getIneqs().getLeft(), view, geo);
		if (function.getIneqs().getRight() != null)
			right = new DrawInequality(function.getIneqs().getRight(), view,
					geo);
		if (function.getIneqs().getIneq() != null)
			ineq = function.getIneqs().getIneq();
		update();
	}

	/**
	 * method to remove extra borders of inequality
	 */
	public void update2() {
		if (left == null || (operation != Operation.NOT && right == null)) {
			return;
		}

		if (this.operation.equals(Operation.AND_INTERVAL)
				&& left.drawable instanceof DrawInequality1Var) {
			if (((DrawInequality1Var) left.drawable).isMinBoundSet()) {
				double minLeft = ((DrawInequality1Var) left.drawable)
						.getMinBound();
				if (Kernel.isGreater(minLeft, minBound)) {
					minBound = minLeft;
					if (this.min != null) {
						((DrawInequality1Var) this.min).ignoreLines();
					}
					this.min = left.drawable;
				} else if (Kernel.isGreater(minBound, minLeft)
						|| ((DrawInequality1Var) left.drawable)
								.isGrtLessEqual()) {
					((DrawInequality1Var) left.drawable).ignoreLines();
				}
			} else {
				double maxLeft = ((DrawInequality1Var) left.drawable)
						.getMaxBound();
				if (Kernel.isGreater(maxBound, maxLeft)) {
					maxBound = maxLeft;
					if (this.max != null) {
						((DrawInequality1Var) this.max).ignoreLines();
					}
					this.max = left.drawable;
				} else if (Kernel.isGreater(maxLeft, maxBound)
						|| ((DrawInequality1Var) left.drawable)
								.isGrtLessEqual()) {
					((DrawInequality1Var) left.drawable).ignoreLines();
				}
			}

			if (((DrawInequality1Var) right.drawable).isMinBoundSet()) {
				double minRight = ((DrawInequality1Var) right.drawable)
						.getMinBound();
				if (Kernel.isGreater(minRight, minBound)) {
					minBound = minRight;
					if (this.min != null) {
						((DrawInequality1Var) this.min).ignoreLines();
					}
					this.min = right.drawable;
				} else if (Kernel.isGreater(minBound, minRight)
						|| ((DrawInequality1Var) right.drawable)
								.isGrtLessEqual()) {
					((DrawInequality1Var) right.drawable).ignoreLines();
				}
			} else {
				double maxRight = ((DrawInequality1Var) right.drawable)
					.getMaxBound();
				if (Kernel.isGreater(maxBound, maxRight)) {
					maxBound = maxRight;
					if (this.max != null) {
						((DrawInequality1Var) this.max).ignoreLines();
					}
					this.max = right.drawable;
				} else if (Kernel.isGreater(maxRight, maxBound)
						|| ((DrawInequality1Var) right.drawable)
								.isGrtLessEqual()) {
					((DrawInequality1Var) right.drawable).ignoreLines();
				}
			}
			return;
		}

		if ((left.drawable == null || left.operation
				.equals(Operation.AND_INTERVAL))) {
			left.min = min;
			left.max = max;
			left.minBound = minBound;
			left.maxBound = maxBound;
			left.update2();
			if (this.operation.equals(Operation.OR)) {
				if (!left.orBounds.isEmpty()) {
					orBounds.addAll(left.orBounds);
				}
				Map<Double, Drawable> minEntry = new HashMap<Double, Drawable>();
				if (left.min == null) {
					minEntry.put(left.minBound, null);
				} else {
					minEntry.put(left.minBound, left.min);
				}
				Map<Double, Drawable> maxEntry = new HashMap<Double, Drawable>();
				if (left.max == null) {
					maxEntry.put(left.maxBound, null);
				} else {
					maxEntry.put(left.maxBound, left.max);
				}
				Pair<Map<Double, Drawable>> pair = new Pair<Map<Double, Drawable>>(
						minEntry, maxEntry);
				orBounds.add(pair);
			}
			this.min = left.min;
			this.max = left.max;
			this.minBound = left.minBound;
			this.maxBound = left.maxBound;
		} else if (left.drawable instanceof DrawInequality1Var && !wasOR) {
			if (((DrawInequality1Var) left.drawable).isMinBoundSet()) {
				double minLeft = ((DrawInequality1Var) left.drawable)
						.getMinBound();
				if (Kernel.isGreater(minLeft, minBound)) {
					minBound = minLeft;
					if (this.min != null) {
						((DrawInequality1Var) this.min).ignoreLines();
					}
					this.min = left.drawable;
				} else if (Kernel.isGreater(minBound, minLeft)
						|| ((DrawInequality1Var) left.drawable)
								.isGrtLessEqual()) {
					((DrawInequality1Var) left.drawable).ignoreLines();
				}
			} else {
				double maxLeft = ((DrawInequality1Var) left.drawable)
						.getMaxBound();
				if (Kernel.isGreater(maxBound, maxLeft)) {
					maxBound = maxLeft;
					if (this.max != null) {
						((DrawInequality1Var) this.max).ignoreLines();
					}
					this.max = left.drawable;
				} else if (Kernel.isGreater(maxLeft, maxBound)
						|| ((DrawInequality1Var) left.drawable)
								.isGrtLessEqual()) {
					((DrawInequality1Var) left.drawable).ignoreLines();
				}
			}
		}

		/*
		 * if (this.operation.equals(Operation.OR)) { wasOR = true; }
		 */

		if ((right.drawable == null || right.operation
				.equals(Operation.AND_INTERVAL))) {
			if (this.operation.equals(Operation.OR)) {
				right.min = null;
				right.max = null;
				right.minBound = -1000000;
				right.maxBound = 1000000;
				right.orBounds = orBounds;
				right.update2();
				if (!right.orBounds.isEmpty()) {
					orBounds.addAll(right.orBounds);
				}
				if (!orBounds.isEmpty()) {
					isRightInOrBounds(orBounds, right);
				} else {
					if (Kernel.isGreater(right.minBound, minBound)
							&& Kernel.isGreater(maxBound, right.minBound)
							&& right.min != null) {
						((DrawInequality1Var) right.min).ignoreLines();
					}
					if (Kernel.isGreater(right.maxBound, minBound)
							&& Kernel.isGreater(maxBound, right.maxBound)
							&& right.max != null) {
						((DrawInequality1Var) right.max).ignoreLines();
					}
				}
				Map<Double, Drawable> minEntry = new HashMap<Double, Drawable>();
				if (right.min == null) {
					minEntry.put(right.minBound, null);
				} else {
					minEntry.put(right.minBound, right.min);
				}
				Map<Double, Drawable> maxEntry = new HashMap<Double, Drawable>();
				if (right.max == null) {
					maxEntry.put(right.maxBound, null);
				} else {
					maxEntry.put(right.maxBound, right.max);
				}
				Pair<Map<Double, Drawable>> pair = new Pair<Map<Double, Drawable>>(
						minEntry, maxEntry);
				orBounds.add(pair);
			} else {
				right.min = min;
				right.max = max;
				right.minBound = minBound;
				right.maxBound = maxBound;
				right.update2();
				this.min = right.min;
				this.max = right.max;
				this.minBound = right.minBound;
				this.maxBound = right.maxBound;
			}
		} else if (right.drawable instanceof DrawInequality1Var
				&& !operation.equals(Operation.OR)) {
			if (((DrawInequality1Var) right.drawable).isMinBoundSet()) {
				double minRight = ((DrawInequality1Var) right.drawable)
						.getMinBound();
				if (Kernel.isGreater(minRight, minBound)) {
					minBound = minRight;
					if (this.min != null) {
						((DrawInequality1Var) this.min).ignoreLines();
					}
					this.min = right.drawable;
				} else if (Kernel.isGreater(minBound, minRight)
						|| ((DrawInequality1Var) right.drawable)
								.isGrtLessEqual()) {
					((DrawInequality1Var) right.drawable).ignoreLines();
				}
			} else {
				double maxRight = ((DrawInequality1Var) right.drawable)
					.getMaxBound();
				if (Kernel.isGreater(maxBound, maxRight)) {
					maxBound = maxRight;
					if (this.max != null) {
						((DrawInequality1Var) this.max).ignoreLines();
					}
					this.max = right.drawable;
				} else if (Kernel.isGreater(maxRight, maxBound)
						|| ((DrawInequality1Var) right.drawable)
								.isGrtLessEqual()) {
					((DrawInequality1Var) right.drawable).ignoreLines();
				}
			}
		}
		if (this.operation.equals(Operation.OR)) {

			if (right.drawable instanceof DrawInequality1Var) {
				if (((DrawInequality1Var) right.drawable).isMinBoundSet()) {
					double minRight = ((DrawInequality1Var) right.drawable)
							.getMinBound();
					if (Kernel.isGreater(minBound, minRight)) {
						minBound = minRight;
						if (this.min != null) {
							((DrawInequality1Var) this.min).ignoreLines();
						}
						this.min = right.drawable;
					} else if (Kernel.isGreater(minRight, minBound)
							&& Kernel.isGreater(maxBound, minRight)) {
						((DrawInequality1Var) right.drawable).ignoreLines();
						if (!Kernel.isEqual(maxBound, 1000000)
								&& this.max != null) {
							((DrawInequality1Var) this.max).ignoreLines();
						}
					} else if (Kernel.isEqual(maxBound, minRight)) {
						((DrawInequality1Var) right.drawable).ignoreLines();
						if (this.max != null) {
							((DrawInequality1Var) this.max).ignoreLines();
						}
					}
				} else {
					double maxRight = ((DrawInequality1Var) right.drawable)
							.getMaxBound();
					if (Kernel.isGreater(maxRight, maxBound)) {
						maxBound = maxRight;
						if (this.max != null) {
							((DrawInequality1Var) this.max).ignoreLines();
						}
						this.max = right.drawable;
					} else if (Kernel.isGreater(maxBound, maxRight)
							&& Kernel.isGreater(maxRight, minBound)) {
						((DrawInequality1Var) right.drawable).ignoreLines();
						if (Kernel.isEqual(maxBound, 1000000)
								&& this.min != null) {
							((DrawInequality1Var) this.min).ignoreLines();
						}
					} else if (Kernel.isEqual(maxRight, minBound)) {
						((DrawInequality1Var) right.drawable).ignoreLines();
						if (this.min != null) {
							((DrawInequality1Var) this.min).ignoreLines();
						}
					}
				}
				return;
			}
		}
	}

	private void isRightInOrBounds(
			ArrayList<Pair<Map<Double, Drawable>>> orBounds2,
			DrawInequality right2) {
		for (int i = 0; i < orBounds2.size(); i++) {
			double minCurrOrBound = orBounds2.get(i).getFirst().keySet()
					.iterator().next();
			double maxCurrOrBound = orBounds2.get(i).getSecond().keySet()
					.iterator().next();
			if (Kernel.isGreater(right2.minBound, minCurrOrBound)
					&& Kernel.isGreater(maxCurrOrBound, right2.minBound)
					&& orBounds2.get(i).getFirst().get(minCurrOrBound) != null) {
				((DrawInequality1Var) orBounds2.get(i).getFirst()
						.get(minCurrOrBound)).ignoreLines();
			}
			if (Kernel.isGreater(right2.maxBound, minCurrOrBound)
					&& Kernel.isGreater(maxCurrOrBound, right2.maxBound)
					&& orBounds2.get(i).getSecond().get(maxCurrOrBound) != null) {
				((DrawInequality1Var) orBounds2.get(i).getSecond()
						.get(maxCurrOrBound)).ignoreLines();
			}
		}

	}

	private boolean isNumber(ExpressionValue left2) {
		return left2 instanceof NumberValue
				&& !(left2 instanceof FunctionVariable);
	}

	private DrawInequality(IneqTree tree, EuclidianView view, GeoElement geo) {
		this.view = view;
		this.geo = geo;
		setForceNoFill(true);
		updateRecursive(tree);
	}

	private GeneralPathClipped[] gpAxis;

	@Override
	final public void update() {
		// take line g here, not geo this object may be used for conics too
		isVisible = geo.isEuclidianVisible();
		if (!isVisible)
			return;
		labelVisible = geo.isLabelVisible();

		// init gp
		updateRecursive(function.getIneqs());
		labelDesc = geo.getLabelDescription();
		if ((geo instanceof GeoFunction)
				&& ((GeoFunction) geo).showOnAxis()
				&& !"y".equals(((GeoFunction) geo)
						.getVarString(StringTemplate.defaultTemplate))) {
			TreeSet<Double> zeros = new TreeSet<Double>();
			((GeoFunction) geo).getIneqs().getZeros(zeros);
			// radius of the dots
			double radius = geo.getLineThickness()
					* DrawInequality1Var.DOT_RADIUS;
			// we add poits 2*radius to the left and right of the screen
			zeros.add(view.getXmin() - 2 * radius * view.getXscale());
			zeros.add(view.getXmax() + 2 * radius * view.getXscale());
			gpAxis = new GeneralPathClipped[zeros.size()];
			Double last = null;
			int gpCount = 0;
			for (Double zero : zeros) {
				if (last != null) {
					boolean value = ((GeoFunction) geo)
							.evaluateBoolean(0.5 * (last + zero));
					if (value) {
						gpAxis[gpCount] = new GeneralPathClipped(view);
						gpAxis[gpCount].moveTo(view.toScreenCoordXd(last)
								+ radius, view.toScreenCoordYd(0));
						gpAxis[gpCount].lineTo(view.toScreenCoordXd(zero)
								- radius, view.toScreenCoordYd(0));
						gpCount++;
					}
				}
				last = zero;
			}
			updateStrokes(geo);
		} else {
			gpAxis = null;
		}

		if (left != null && right != null) {
			maxBound = 1000000;
			minBound = -1000000;
			max = null;
			min = null;
			orBounds = new ArrayList<Pair<Map<Double, Drawable>>>();
			update2();
			if (maxBound < minBound) {
				if (max != null) {
					((DrawInequality1Var) max).ignoreLines();
				}
				if (min != null) {
					((DrawInequality1Var) min).ignoreLines();
				}
			}
		}

	}

	private void updateRecursive(IneqTree it) {
		updateTrees(it);
		operation = it.getOperation();
		updateShape();
		if (left != null) {
			yLabel = left.yLabel;
			xLabel = left.xLabel;
		}

		if (ineq != it.getIneq())
			ineq = it.getIneq();

		if (ineq != null) {

			if (drawable == null || !matchBorder(ineq.getBorder(), drawable)) {
				createDrawable();
			} else if (ineq.getType() == IneqType.INEQUALITY_CONIC) {
				ineq.getConicBorder().setInverseFill(ineq.isAboveBorder());
				if (drawable instanceof DrawConic) {
					((DrawConic) drawable).setIgnoreSingularities(!ineq
							.isStrict() == ineq.isAboveBorder());
				}
			}
			drawable.update();
			setShape(drawable.getShape());
			xLabel = drawable.xLabel;
			yLabel = drawable.yLabel;
		}
		if (geo.isInverseFill() && !isForceNoFill()) {
			GArea b = AwtFactory.prototype.newArea(view
					.getBoundingPath());
			b.subtract(getShape());
			setShape(b);
		}

	}

	private void createDrawable() {
		switch (ineq.getType()) {
		case INEQUALITY_PARAMETRIC_Y:
			drawable = new DrawParametricInequality(ineq, view, geo);
			break;
		case INEQUALITY_PARAMETRIC_X:
			drawable = new DrawParametricInequality(ineq, view, geo);
			break;
		case INEQUALITY_1VAR_X:
			drawable = new DrawInequality1Var(ineq, view, geo, false);
			break;
		case INEQUALITY_1VAR_Y:
			drawable = new DrawInequality1Var(ineq, view, geo, true);
			break;
		case INEQUALITY_CONIC:
			drawable = new DrawConic(view, ineq.getConicBorder(),
					!ineq.isStrict() == ineq.isAboveBorder());
			ineq.getConicBorder().setInverseFill(ineq.isAboveBorder());
			break;
		case INEQUALITY_LINEAR:
			drawable = new DrawLine(view, ineq.getLineBorder());
			ineq.getLineBorder().setInverseFill(ineq.isAboveBorder());
			break;
		/*
		 * case IneqType.INEQUALITY_IMPLICIT: drawable = new
		 * DrawImplicitPoly(view, ineq.getImpBorder()); break; TODO put this
		 * back when implicit polynomial can be shaded
		 */
		default:
			App.debug("Unhandled inequality type");
			return;
		}
		drawable.setGeoElement(geo);
		drawable.setForceNoFill(true);
	}

	private void updateShape() {
		if (operation.equals(Operation.AND)
				|| operation.equals(Operation.AND_INTERVAL)) {
			setShape(left.getShape());
			getShape().intersect(right.getShape());
		} else if (operation.equals(Operation.OR)) {
			setShape(left.getShape());
			getShape().add(right.getShape());
		} else if (operation.equals(Operation.EQUAL_BOOLEAN)) {
			setShape(AwtFactory.prototype.newArea(view.getBoundingPath()));
			left.getShape().exclusiveOr(right.getShape());
			getShape().subtract(left.getShape());
		} else if (operation.equals(Operation.NOT_EQUAL)) {
			setShape(left.getShape());
			getShape().exclusiveOr(right.getShape());
		} else if (operation.equals(Operation.NOT)) {
			setShape(AwtFactory.prototype.newArea(view.getBoundingPath()));
			getShape().subtract(left.getShape());
		}
	}

	private void updateTrees(IneqTree it) {
		if (it.getLeft() != null && left == null) {
			left = new DrawInequality(it.getLeft(), view, geo);
		}
		if (it.getLeft() != null) {
			left.updateRecursive(it.getLeft());
		} else
			left = null;
		if (it.getRight() != null && right == null) {
			right = new DrawInequality(it.getRight(), view, geo);
		}
		if (it.getRight() != null)
			right.updateRecursive(it.getRight());
		else
			right = null;

	}

	private static boolean matchBorder(GeoElement border, Drawable d) {
		if (d instanceof DrawConic && ((DrawConic) d).getConic().equals(border))
			return true;
		/*
		 * if (d instanceof DrawImplicitPoly && ((DrawImplicitPoly)
		 * d).getPoly().equals(border)) return true;
		 */
		if (d instanceof DrawParametricInequality
				&& ((DrawParametricInequality) d).getBorder().equals(border))
			return ((DrawParametricInequality) d).isXparametric();

		return false;
	}

	@Override
	public void draw(GGraphics2D g2) {
		if (!isForceNoFill() && !isVisible)
			return;
		if (operation.equals(Operation.NO_OPERATION)) {
			if (drawable != null) {
				drawable.updateStrokesJustLineThickness(geo);
				if (geo.getLineThickness() > 0)
					drawable.draw(g2);
			}
		} else {
			if (left != null) {
				left.updateStrokesJustLineThickness(geo);
				left.draw(g2);
			}
			if (right != null) {
				right.updateStrokesJustLineThickness(geo);
				right.draw(g2);
			}
		}
		if (!isForceNoFill()) {
			if (gpAxis != null) {
				if (geo.doHighlighting()) {
					g2.setPaint(geo.getSelColor());
					g2.setStroke(selStroke);
					for (int i = 0; gpAxis[i] != null; i++) {
						g2.draw(gpAxis[i]);
					}
				}
				g2.setPaint(getObjectColor());
				g2.setStroke(objStroke);
				for (int i = 0; gpAxis[i] != null; i++) {
					g2.draw(gpAxis[i]);
				}

			} else {
				if (geo.getFillType() != GeoElement.FillType.IMAGE) {
					// make sure line thickness set for hatching
					updateStrokes(geo);
				}

				fill(g2, getShape(), true);
			}
		}

		if (labelVisible) {
			g2.setFont(view.getFontConic());
			g2.setPaint(geo.getLabelColor());
			drawLabel(g2);
		}
	}

	@Override
	public GeoElement getGeoElement() {
		return geo;
	}

	private boolean hit2(int x, int y) {
		double[] coords = new double[] { view.toRealWorldCoordX(x),
				view.toRealWorldCoordY(y) };
		if (geo instanceof GeoFunction
				&& ((GeoFunction) geo).getVarString(
						StringTemplate.defaultTemplate).equals("y"))
			return ((GeoFunction) geo).getFunction().evaluateBoolean(coords[1]);
		return ((FunctionalNVar) geo).getFunction().evaluateBoolean(coords);

	}

	@Override
	public boolean hit(int x, int y, int hitThreshold) {
		if (!geo.isEuclidianVisible())
			return false;
		if (geo instanceof GeoFunction && ((GeoFunction) geo).showOnAxis()
				&& Math.abs(y - view.toScreenCoordY(0)) > hitThreshold)
			return false;
		return hit2(x, y) || hit2(x - 4, y) || hit2(x + 4, y) || hit2(x, y - 4)
				|| hit2(x, y + 4);

	}

	@Override
	public boolean isInside(GRectangle rect) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setGeoElement(GeoElement geo) {
		this.geo = geo;
	}

	public DrawInequality getLeft() {
		return this.left;
	}

	public DrawInequality getRight() {
		return this.right;
	}
}
