package geogebra.common.euclidian.draw;

import geogebra.common.awt.GPoint;
import geogebra.common.euclidian.Drawable;
import geogebra.common.euclidian.EuclidianStatic;
import geogebra.common.euclidian.EuclidianView;
import geogebra.common.euclidian.GeneralPathClipped;
import geogebra.common.euclidian.draw.DrawParametricCurve.Gap;
import geogebra.common.factories.AwtFactory;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.arithmetic.FunctionalNVar;
import geogebra.common.kernel.arithmetic.IneqTree;
import geogebra.common.kernel.arithmetic.Inequality;
import geogebra.common.kernel.arithmetic.Inequality.IneqType;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.main.App;
import geogebra.common.plugin.Operation;

import java.util.TreeSet;

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

	/**
	 * Creates new drawable linear inequality
	 * 
	 * @param view view
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

	private DrawInequality(IneqTree tree, EuclidianView view,
			GeoElement geo) {
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
		if ((geo instanceof GeoFunction) && ((GeoFunction) geo).showOnAxis()
				&& !"y".equals(((GeoFunction) geo).getVarString(StringTemplate.defaultTemplate))) {
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
		} else
			gpAxis = null;
		
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
			} else if (ineq.getType() == IneqType.INEQUALITY_CONIC) {
				ineq.getConicBorder().setInverseFill(ineq.isAboveBorder());
			}
			drawable.update();
			setShape(drawable.getShape());
			xLabel = drawable.xLabel;
			yLabel = drawable.yLabel;
		}
		if (geo.isInverseFill() && !isForceNoFill()) {
			geogebra.common.awt.GArea b = AwtFactory.prototype.newArea(view.getBoundingPath());
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
			drawable = new DrawConic(view, ineq.getConicBorder());
			ineq.getConicBorder().setInverseFill(ineq.isAboveBorder());
			break;
		case INEQUALITY_LINEAR:
			drawable = new DrawLine(view, ineq.getLineBorder());
			ineq.getLineBorder().setInverseFill(ineq.isAboveBorder());
			break;	
		/*case IneqType.INEQUALITY_IMPLICIT:
			drawable = new DrawImplicitPoly(view, ineq.getImpBorder());
			break; TODO put this back when implicit polynomial can be shaded*/ 
		default:
			App.debug("Unhandled inequality type");
			return;
		}
		drawable.setGeoElement(geo);
		drawable.setForceNoFill(true);
	}

	private void updateShape() {
		if (operation.equals(Operation.AND)) {			
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
			right = new DrawInequality(it.getLeft(), view, geo);
		}
		if (it.getRight() != null)
			right.updateRecursive(it.getRight());
		else
			right = null;

	}

	private static boolean matchBorder(GeoElement border, Drawable d) {
		if (d instanceof DrawConic && ((DrawConic) d).getConic().equals(border))
			return true;
		/*if (d instanceof DrawImplicitPoly
				&& ((DrawImplicitPoly) d).getPoly().equals(border))
			return true;*/
		if (d instanceof DrawParametricInequality
				&& ((DrawParametricInequality) d).getBorder().equals(border))
			return ((DrawParametricInequality) d).isXparametric();

		return false;
	}

	@Override
	public void draw(geogebra.common.awt.GGraphics2D g2) {
		if (!isForceNoFill() && !isVisible)
			return;
		if (operation.equals(Operation.NO_OPERATION)) {
			if (drawable != null) {
				drawable.updateStrokesJustLineThickness(geo); 
				if(geo.getLineThickness()>0)
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
				g2.setPaint(geo.getObjectColor());
				g2.setStroke(objStroke);
				for (int i = 0; gpAxis[i] != null; i++) {
					g2.draw(gpAxis[i]);
				}

			} else {
				if (geo.getFillType()==GeoElement.FILL_HATCH) { 
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
				&& ((GeoFunction) geo).getVarString(StringTemplate.defaultTemplate).equals("y"))
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
	public boolean isInside(geogebra.common.awt.GRectangle rect) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setGeoElement(GeoElement geo) {
		this.geo = geo;
	}

	private class DrawParametricInequality extends Drawable {

		private Inequality paramIneq;
		private GeneralPathClipped gp;

		protected DrawParametricInequality(Inequality ineq, EuclidianView view,
				GeoElement geo) {
			this.view = view;
			this.paramIneq = ineq;
			this.geo = geo;
		}

		@Override
		public geogebra.common.awt.GArea getShape() {
			return AwtFactory.prototype.newArea(gp);
		}

		GeoElement getBorder() {
			return paramIneq.getBorder();
		}

		@Override
		public void draw(geogebra.common.awt.GGraphics2D g2) {			
			if (geo.doHighlighting()) {
				g2.setPaint(geo.getSelColor());
				g2.setStroke(selStroke);
				EuclidianStatic.drawWithValueStrokePure(gp, g2);
			}

			if (!isForceNoFill())
				fill(g2, gp, true); // fill using default/hatching/image as
			// appropriate

			if (geo.lineThickness > 0) {
				g2.setPaint(geo.getObjectColor());
				g2.setStroke(objStroke);
				EuclidianStatic.drawWithValueStrokePure(gp, g2);
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
		public boolean isInside(geogebra.common.awt.GRectangle rect) {
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
			GeoFunction border = paramIneq.getFunBorder();
			border.setLineThickness(geo.lineThickness);
			updateStrokes(border);
			GPoint labelPos;
			if (paramIneq.getType() == IneqType.INEQUALITY_PARAMETRIC_X) {
				double bx = view.toRealWorldCoordY(-10);
				double ax = view.toRealWorldCoordY(view.getHeight() + 10);				
				double axEv = view.toScreenCoordYd(ax);				
				if (paramIneq.isAboveBorder()) {					
					gp.moveTo(view.getWidth() + 10, axEv);
					labelPos = DrawParametricCurve.plotCurve(border, ax, bx, view, gp,
							true, Gap.RESET_XMAX);
					gp.lineTo(view.getWidth() + 10, gp.getCurrentPoint().getY());
					gp.lineTo(view.getWidth() + 10, axEv);
					gp.closePath();
				} else {					
					gp.moveTo(-10, axEv);
					labelPos = DrawParametricCurve.plotCurve(border, ax, bx, view, gp,
							true, Gap.RESET_XMIN);
					gp.lineTo(-10, gp.getCurrentPoint().getY());
					gp.lineTo(-10, axEv);
					gp.closePath();
				}				
			} else {
				double ax = view.toRealWorldCoordX(-10);
				double bx = view.toRealWorldCoordX(view.getWidth() + 10);				
				double axEv = view.toScreenCoordXd(ax);				
				if (paramIneq.isAboveBorder()) {
					gp.moveTo(axEv, -10);
					labelPos = DrawParametricCurve.plotCurve(border, ax, bx, view, gp,
							true, Gap.RESET_YMIN);
					gp.lineTo(gp.getCurrentPoint().getX(), -10);
					gp.lineTo(axEv, -10);
					gp.closePath();
				} else {
					gp.moveTo(axEv, view.getHeight() + 10);
					labelPos = DrawParametricCurve.plotCurve(border, ax, bx, view, gp,
							true, Gap.RESET_YMAX);					
					gp.lineTo(gp.getCurrentPoint().getX(), view.getHeight() + 10);
					gp.lineTo(axEv, view.getHeight() + 10);
					gp.closePath();
				}
				border.evaluateCurve(ax);
				
			}
			if (labelVisible) {
				xLabel = labelPos.getX();
				yLabel = labelPos.getY();				
				addLabelOffset();
			}

		}

		boolean isXparametric() {
			return paramIneq.getType() == IneqType.INEQUALITY_PARAMETRIC_X;
		}

	}
}
