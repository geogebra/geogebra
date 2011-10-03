package geogebra.euclidian;

import geogebra.kernel.GeoElement;
import geogebra.kernel.GeoFunction;
import geogebra.kernel.arithmetic.ExpressionNode;
import geogebra.kernel.arithmetic.FunctionNVar;
import geogebra.kernel.arithmetic.FunctionNVar.IneqTree;
import geogebra.kernel.arithmetic.FunctionalNVar;
import geogebra.kernel.arithmetic.Inequality;
import geogebra.main.Application;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Area;
import java.util.TreeSet;

/**
 * Graphical representation of inequality
 * 
 * @author Zbynek Konecny
 * 
 */
public class DrawInequality extends Drawable {

	private boolean isVisible;
	private boolean labelVisible;

	private Drawable drawable;
	private int operation = 0;
	private DrawInequality left, right;
	private Inequality ineq;
	private FunctionalNVar function;

	/**
	 * Creates new drawable linear inequality
	 * 
	 * @param view
	 * @param function
	 *            boolean 2-var function
	 */
	public DrawInequality(EuclidianView view, FunctionalNVar function) {
		this.view = view;
		hitThreshold = view.getCapturingThreshold();
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

	private DrawInequality(FunctionNVar.IneqTree tree, EuclidianView view,
			GeoElement geo) {
		this.view = view;
		this.geo = geo;
		setForceNoFill(true);
		updateRecursive(tree);
	}

	private GeneralPathClipped[] gpAxis;

	final public void update() {
		// take line g here, not geo this object may be used for conics too
		isVisible = geo.isEuclidianVisible();
		if (!isVisible)
			return;
		labelVisible = geo.isLabelVisible();
		
		// init gp
		updateRecursive(function.getIneqs());
		labelDesc = geo.getLabel();
		if ((geo instanceof GeoFunction) && ((GeoFunction) geo).showOnAxis()
				&& !"y".equals(((GeoFunction) geo).getVarString())) {
			TreeSet<Double> zeros = new TreeSet<Double>();
			((GeoFunction) geo).getIneqs().getZeros(zeros);
			// radius of the dots
			double radius = geo.getLineThickness()
					* DrawInequality1Var.DOT_RADIUS;
			// we add poits 2*radius to the left and right of the screen
			zeros.add(view.xmin - 2 * radius * view.xscale);
			zeros.add(view.xmax + 2 * radius * view.xscale);
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
		} else
			gpAxis = null;
		if(labelVisible)
			addLabelOffset();
	}

	private void updateRecursive(IneqTree it) {
		updateTrees(it);
		operation = it.getOperation();
		updateShape();
		if(left!=null){
			yLabel = left.yLabel;
			xLabel = left.xLabel;
		}

		if (ineq != it.getIneq())
			ineq = it.getIneq();

		if (ineq != null) {

			if (drawable == null || !matchBorder(ineq.getBorder(), drawable)) {
				createDrawable();
			} else if (ineq.getType() == Inequality.INEQUALITY_CONIC) {
				ineq.getConicBorder().setInverseFill(ineq.isAboveBorder());
			}
			drawable.update();
			setShape(drawable.getShape());
			xLabel = drawable.xLabel;
			yLabel = drawable.yLabel;
		}
		if (geo.isInverseFill() && !isForceNoFill()) {
			Area b = new Area(view.getBoundingPath());
			b.subtract(getShape());
			setShape(b);
		}

	}

	private void createDrawable() {
		switch (ineq.getType()) {
		case Inequality.INEQUALITY_PARAMETRIC_Y:
			drawable = new DrawParametricInequality(ineq, view, geo);
			break;
		case Inequality.INEQUALITY_PARAMETRIC_X:
			drawable = new DrawParametricInequality(ineq, view, geo);
			break;
		case Inequality.INEQUALITY_1VAR_X:
			drawable = new DrawInequality1Var(ineq, view, geo, false);
			break;
		case Inequality.INEQUALITY_1VAR_Y:
			drawable = new DrawInequality1Var(ineq, view, geo, true);
			break;
		case Inequality.INEQUALITY_CONIC:
			drawable = new DrawConic(view, ineq.getConicBorder());
			ineq.getConicBorder().setInverseFill(ineq.isAboveBorder());
			break;
		case Inequality.INEQUALITY_IMPLICIT:
			drawable = new DrawImplicitPoly(view, ineq.getImpBorder());
			break;
		default:
			Application.debug("Unhandled inequality type");
			return;
		}
		drawable.setGeoElement(geo);
		drawable.setForceNoFill(true);
	}

	private void updateShape() {
		if (operation == ExpressionNode.AND) {			
			setShape(left.getShape());
			getShape().intersect(right.getShape());
		} else if (operation == ExpressionNode.OR) {
			setShape(left.getShape());
			getShape().add(right.getShape());
		} else if (operation == ExpressionNode.EQUAL_BOOLEAN) {
			setShape(new Area(view.getBoundingPath()));
			left.getShape().exclusiveOr(right.getShape());
			getShape().subtract(left.getShape());
		} else if (operation == ExpressionNode.NOT_EQUAL) {
			setShape(left.getShape());
			getShape().exclusiveOr(right.getShape());
		} else if (operation == ExpressionNode.NOT) {
			setShape(new Area(view.getBoundingPath()));
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
			right = new DrawInequality(it.getLeft(), view, geo);
		}
		if (it.getRight() != null)
			right.updateRecursive(it.getRight());
		else
			right = null;

	}

	private boolean matchBorder(GeoElement border, Drawable d) {
		if (d instanceof DrawConic && ((DrawConic) d).getConic().equals(border))
			return true;
		if (d instanceof DrawImplicitPoly
				&& ((DrawImplicitPoly) d).getPoly().equals(border))
			return true;
		if (d instanceof DrawParametricInequality
				&& ((DrawParametricInequality) d).getBorder().equals(border))
			return ((DrawParametricInequality) d).isXparametric();

		return false;
	}

	public void draw(Graphics2D g2) {
		if (!isForceNoFill() && !isVisible)
			return;
		if (operation == ExpressionNode.NO_OPERATION) {
			if (drawable != null) {
				drawable.draw(g2);
			}
		} else {
			if (left != null)
				left.draw(g2);
			if (right != null)
				right.draw(g2);
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
				g2.setPaint(geo.getObjectColor());
				g2.setStroke(objStroke);
				for (int i = 0; gpAxis[i] != null; i++) {
					g2.draw(gpAxis[i]);
				}

			} else
				fill(g2, getShape(), true);
		}
		
		if (labelVisible) {
			g2.setFont(view.fontConic);
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
				&& ((GeoFunction) geo).getVarString().equals("y"))
			return ((GeoFunction) geo).getFunction().evaluateBoolean(coords[1]);
		return ((FunctionalNVar) geo).getFunction().evaluateBoolean(coords);

	}

	@Override
	public boolean hit(int x, int y) {
		if(!geo.isEuclidianVisible())
			return false;		
		if (geo instanceof GeoFunction && ((GeoFunction) geo).showOnAxis()
				&& Math.abs(y - view.toScreenCoordY(0)) > 3)
			return false;
		return hit2(x, y) || hit2(x - 4, y) || hit2(x + 4, y) || hit2(x, y - 4)
				|| hit2(x, y + 4);

	}

	@Override
	public boolean isInside(Rectangle rect) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setGeoElement(GeoElement geo) {
		this.geo = geo;
	}

	private class DrawParametricInequality extends Drawable {

		private Inequality ineq;
		private GeneralPathClipped gp;

		protected DrawParametricInequality(Inequality ineq, EuclidianView view,
				GeoElement geo) {
			this.view = view;
			this.ineq = ineq;
			this.geo = geo;
		}

		public Area getShape() {
			return new Area(gp);
		}

		private Object getBorder() {
			return ineq.getBorder();
		}

		@Override
		public void draw(Graphics2D g2) {			
			if (geo.doHighlighting()) {
				g2.setPaint(geo.getSelColor());
				g2.setStroke(selStroke);
				Drawable.drawWithValueStrokePure(gp, g2);
			}

			if (!isForceNoFill())
				fill(g2, gp, true); // fill using default/hatching/image as
			// appropriate

			if (geo.lineThickness > 0) {
				g2.setPaint(geo.getObjectColor());
				g2.setStroke(objStroke);
				Drawable.drawWithValueStrokePure(gp, g2);
			}


		}

		@Override
		public GeoElement getGeoElement() {
			return geo;
		}

		@Override
		public boolean hit(int x, int y) {
			return gp.contains(x, y)
					|| gp.intersects(x - hitThreshold, y - hitThreshold,
							2 * hitThreshold, 2 * hitThreshold);
		}

		@Override
		public boolean isInside(Rectangle rect) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void setGeoElement(GeoElement geo) {
			this.geo = geo;

		}

		@Override
		public void update() {
			if (gp == null)
				gp = new GeneralPathClipped(view);
			else
				gp.reset();
			GeoFunction border = ineq.getFunBorder();
			border.setLineThickness(geo.lineThickness);
			updateStrokes(border);
			Point labelPos;
			if (ineq.getType() == Inequality.INEQUALITY_PARAMETRIC_X) {
				double bx = view.toRealWorldCoordY(-10);
				double ax = view.toRealWorldCoordY(view.height + 10);				
				double axEv = view.toScreenCoordYd(ax);				
				if (ineq.isAboveBorder()) {					
					gp.moveTo(view.width + 10, axEv);
					labelPos = DrawParametricCurve.plotCurve(border, ax, bx, view, gp,
							true, DrawParametricCurve.GAP_RESET_XMAX);
					gp.lineTo(view.width + 10, gp.getCurrentPoint().getY());
					gp.lineTo(view.width + 10, axEv);
					gp.closePath();
				} else {					
					gp.moveTo(-10, axEv);
					labelPos = DrawParametricCurve.plotCurve(border, ax, bx, view, gp,
							true, DrawParametricCurve.GAP_RESET_XMIN);
					gp.lineTo(-10, gp.getCurrentPoint().getY());
					gp.lineTo(-10, axEv);
					gp.closePath();
				}				
			} else {
				double ax = view.toRealWorldCoordX(-10);
				double bx = view.toRealWorldCoordX(view.width + 10);				
				double axEv = view.toScreenCoordXd(ax);				
				if (ineq.isAboveBorder()) {
					gp.moveTo(axEv, -10);
					labelPos = DrawParametricCurve.plotCurve(border, ax, bx, view, gp,
							true, DrawParametricCurve.GAP_RESET_YMIN);
					gp.lineTo(gp.getCurrentPoint().getX(), -10);
					gp.lineTo(axEv, -10);
					gp.closePath();
				} else {
					gp.moveTo(axEv, view.height + 10);
					labelPos = DrawParametricCurve.plotCurve(border, ax, bx, view, gp,
							true, DrawParametricCurve.GAP_RESET_YMAX);					
					gp.lineTo(gp.getCurrentPoint().getX(), view.height + 10);
					gp.lineTo(axEv, view.height + 10);
					gp.closePath();
				}
				border.evaluateCurve(ax);
				
			}
			if (labelVisible) {
				xLabel = (int)labelPos.getX();
				yLabel = (int)labelPos.getY();				
				addLabelOffset();
			}

		}

		private boolean isXparametric() {
			return ineq.getType() == Inequality.INEQUALITY_PARAMETRIC_X;
		}

	}
}
