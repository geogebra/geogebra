/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package geogebra.common.euclidian.draw;

import geogebra.common.awt.GPoint;
import geogebra.common.awt.GRectangle;
import geogebra.common.awt.GShape;
import geogebra.common.euclidian.Drawable;
import geogebra.common.euclidian.EuclidianStatic;
import geogebra.common.euclidian.EuclidianView;
import geogebra.common.euclidian.plot.CurvePlotter;
import geogebra.common.euclidian.plot.GeneralPathClippedForCurvePlotter;
import geogebra.common.factories.AwtFactory;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.VarString;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.kernel.geos.ParametricCurve;


/**
 * Draws graphs of parametric curves and functions
 * 
 * @author Markus Hohenwarter, with ideas from John Gillam (see below)
 */
public class DrawParametricCurve extends Drawable {

	private ParametricCurve curve;
	private GeneralPathClippedForCurvePlotter gp;
	private boolean isVisible, labelVisible, fillCurve;


	/**
	 * Creates graphical representation of the curve
	 * 
	 * @param view
	 *            Euclidian view in which it should be drawn
	 * @param curve
	 *            Curve to be drawn
	 */
	public DrawParametricCurve(EuclidianView view, ParametricCurve curve) {
		this.view = view;
		hitThreshold = view.getCapturingThreshold();
		this.curve = curve;
		geo = curve.toGeoElement();
		update();
	}

	private StringBuilder labelSB = new StringBuilder();

	@Override
	final public void update() {
		isVisible = geo.isEuclidianVisible();
		if (!isVisible)
			return;
		labelVisible = geo.isLabelVisible();
		updateStrokes(geo);
		if (gp == null)
			gp = new GeneralPathClippedForCurvePlotter(view);
		gp.reset();

		fillCurve = filling(curve);

		double min = curve.getMinParameter();
		double max = curve.getMaxParameter();
		if (curve instanceof GeoFunction) {
			double minView = view.getXmin();
			double maxView = view.getXmax();
			if (min < minView)
				min = minView;
			if (max > maxView)
				max = maxView;
		}
		GPoint labelPoint;
		if (Kernel.isEqual(min, max)) {
			double[] eval = new double[2];
			curve.evaluateCurve(min, eval);
			view.toScreenCoords(eval);
			labelPoint = new GPoint((int) eval[0], (int) eval[1]);
		} else {
			labelPoint = CurvePlotter.plotCurve(curve, min, max, view, gp, labelVisible, fillCurve ? CurvePlotter.Gap.CORNER : CurvePlotter.Gap.MOVE_TO);
		}

		// gp on screen?
		if (!gp.intersects(0, 0, view.getWidth(), view.getHeight())) {
			isVisible = false;
			// don't return here to make sure that getBounds() works for
			// offscreen points too
		}
		
		if (labelPoint != null) {
			xLabel = labelPoint.x;
			yLabel = labelPoint.y;
			switch (geo.labelMode) {
			case GeoElement.LABEL_NAME_VALUE:
				StringTemplate tpl = StringTemplate.latexTemplate;
				labelSB.setLength(0);
				labelSB.append('$');
				labelSB.append(geo.getLabel(tpl));
				labelSB.append('(');
				labelSB.append(((VarString) geo).getVarString(tpl));
				labelSB.append(")\\;=\\;");
				labelSB.append(geo.getLaTeXdescription());
				labelSB.append('$');

				labelDesc = labelSB.toString();
				break;

			case GeoElement.LABEL_VALUE:
				labelSB.setLength(0);
				labelSB.append('$');
				labelSB.append(geo.getLaTeXdescription());
				labelSB.append('$');

				labelDesc = labelSB.toString();
				break;

			case GeoElement.LABEL_CAPTION:
			default: // case LABEL_NAME:
				labelDesc = geo.getLabelDescription();
			}
			addLabelOffsetEnsureOnScreen();
		}
		// shape for filling

		if (geo.isInverseFill()) {
			setShape(AwtFactory.prototype.newArea(view.getBoundingPath()));
			getShape().subtract(AwtFactory.prototype.newArea(gp));
		}
		// draw trace
		if (curve.getTrace()) {
			isTracing = true;
			geogebra.common.awt.GGraphics2D g2 = view.getBackgroundGraphics();
			if (g2 != null)
				drawTrace(g2);
		} else {
			if (isTracing) {
				isTracing = false;
				//view.updateBackground();
			}
		}
	}

	/**
	 * Returns true when x is either NaN or infinite.
	 */
	private static boolean isUndefined(double x) {
		return Double.isNaN(x) || Double.isInfinite(x);
	}

	/**
	 * Returns true when at least one element of eval is either NaN or infinite.
	 */
	private static boolean isUndefined(double[] eval) {
		for (int i = 0; i < eval.length; i++) {
			if (isUndefined(eval[i]))
				return true;
		}
		return false;
	}



	/*
	 * The algorithm in plotInterval() is based on an algorithm by John Gillam.
	 * Below you find his explanation.
	 * 
	 * 
	 * Let X(t)=(x(t),y(t)) be a function defined on [a,b]. Make a uniform
	 * partition t0=a<t1<t2...<tn=b of [a,b]. Assume we've already plotted
	 * X(ti). Choose j>i and see if it's acceptable for the next point to be
	 * X(tj). If not, "back up" and try another j. Unfortunately, this probably
	 * leads to multiple evaluations of the function X at the same point. The
	 * following discussion presents an algorithm which basically does this
	 * "back up" but multiple function evaluations at the same point are
	 * avoided.
	 * 
	 * The actual algorithm always has n a power of 2, namely 2 to the maxDepth.
	 * In Math Okay, the user sets maxDepth in the drawing quality under Max
	 * lines.
	 * 
	 * The following first creates a complete binary tree of height h(3) Then
	 * the following algorithm visits all the leaves of the tree (2^h leaves):
	 * (easy induction on height h). Furthermore, the maximum stack size is
	 * clearly h+1:
	 * 
	 * Node root=create(1,3),p; p=root; Node[] stack=new Node[20]; int top=0;
	 * stack[top++]=p; do { while (p.left!=null) { stack[top++]=p;
	 * p=(Node)p.left; } System.out.print(" "+p.n); // visit p p=stack[--top];
	 * p=(Node)p.right; } while (top!=0);
	 * 
	 * Now the following code "clearly" generates as t values all the dyadic
	 * rationals with denominator 2^n, with n==maxDepth
	 * 
	 * int dyadicStack[]=new int[20]; int depthStack[]=new int[20]; double
	 * divisors[]=new double[20]; divisors[0]=1; for (int
	 * i=1;i<20;divisors[i]=divisors[i-1]/2,i++) ; int top=0,maxDepth=5; // of
	 * course 5 is just a test double t; int i=1; dyadicStack[0]=1;
	 * depthStack[0]=0; top=1; int depth=0; do { while (depth<maxDepth) {
	 * dyadicStack[top]=i; depthStack[top++]=depth; i<<=1; i--; depth++; }
	 * t=i*divisors[maxDepth]; // a visit of dyadic rational t
	 * depth=depthStack[--top]+1; // pop stack and go to right
	 * i=dyadicStack[top]<<1; } while (top !=0);
	 * 
	 * Finally, here is code which draws a curve (continuous) x(t),y(t) for
	 * t1<=t<=t2; xEval and yEval evaluate x and y at t; maxXDistance and
	 * maxYDistance are set somewhere. Let P0=(x0,y0) be "previous point" and
	 * P=(x,y) the "current point". The while loop that goes down the tree has
	 * condition of form: depth<maxDepth && !acceptable(P0,P); i.e., go on down
	 * the tree when it is unacceptable to draw the line from P0 to P.
	 * 
	 * double x0,y0,x,y,t; int dyadicStack[]=new int[20]; int depthStack[]=new
	 * int[20]; double xStack[]=new double[20]; double yStack[]=new double[20];
	 * double divisors[]=new double[20]; divisors[0]=t2-t1; for (int
	 * i=1;i<20;divisors[i]=divisors[i-1]/2,i++) ; int i=1; dyadicStack[0]=1;
	 * depthStack[0]=0; x0=xEval(t1); y0=yEval(t1); xStack[0]=x=xEval(t2);
	 * yStack[0]=y=yEval(t2); top=1; int depth=0; // with a GeneralPathClipped
	 * moveTo(sx0,sy0) , the "screen" point do { while (depth<maxDepth &&
	 * (abs(x-x0)>=maxXDistance || abs(y-y0)>=maxYDistance)) {
	 * dyadicStack[top]=i; depthStack[top]=depth; xStack[top]=x;
	 * yStack[top++]=y; i<<=1; i--; depth++; t=t1+i*divisors[depth]; //
	 * t=t1+(t2-t1)*(i/2^depth) x=xEval(t); y=yEval(t); } drawLine(x0,y0,x,y);
	 * // or with a GeneralPathClipped lineTo(sx,sy) // above is call to user
	 * written function x0=x; y0=y; // Here's the real utility of the algorithm:
	 * //Now pop stack and go to right; notice the corresponding dyadic value
	 * when we go to right is 2*i/(2^(d+1) = i/2^d !! So we've already
	 * //calculated the corresponding x and y values when we pushed.
	 * y=yStack[--top]; x=xStack[top] depth=depthStack[top]+1; // pop stack and
	 * go to right i=dyadicStack[top]<<1; } while (top !=0);
	 * 
	 * Notice the lines drawn from (x0,y0) to (x,y) always satisfy
	 * |x-x0|<maxXDistance and |y-y0|<maxYDistance or the minimum mesh
	 * 1/2^maxDepth has been reached. Also the maximum number of evaluations of
	 * functions x and y is 2^maxDepth. All this pushing and popping looks
	 * expensive, but compared to function evaluation and rendering, it's
	 * trivial.
	 * 
	 * For the special case of y=f(x), of course x(t)==t and y(t)=f(t). In this
	 * special case, you still have to worry about discontinuities of f, and in
	 * particular vertical asymptopes. So you need to adjust the Boolean
	 * acceptable.
	 */

	@Override
	final public void draw(geogebra.common.awt.GGraphics2D g2) {
		if (isVisible) {
			if (geo.doHighlighting()) {
				g2.setPaint(geo.getSelColor());
				g2.setStroke(selStroke);
				EuclidianStatic.drawWithValueStrokePure(gp, g2);
			}

			g2.setPaint(geo.getObjectColor());
			g2.setStroke(objStroke);
			EuclidianStatic.drawWithValueStrokePure(gp, g2);

			if (fillCurve) {
				try {

					fill(g2, (geo.isInverseFill() ? getShape() : gp), false); // fill
																			// using
																			// default/hatching/image
																			// as
																			// appropriate

				} catch (Exception e) {
					System.err.println(e.getMessage());
				}
			}

			if (labelVisible) {
				g2.setFont(view.getFontConic());
				g2.setPaint(geo.getLabelColor());
				drawLabel(g2);
			}
		}
	}


	@Override
	protected
	final void drawTrace(geogebra.common.awt.GGraphics2D g2) {
		g2.setPaint(geo
				.getObjectColor());
		g2.setStroke(objStroke);
		EuclidianStatic.drawWithValueStrokePure(gp, g2);
	}

	@Override
	final public boolean hit(int x, int y) {
		if (isVisible) {
			GShape t = geo.isInverseFill() ? getShape() : gp;
			if (strokedShape == null) {
				//strokedShape = new geogebra.awt.GenericShape(geogebra.awt.BasicStroke.getAwtStroke(objStroke).createStrokedShape(geogebra.awt.GenericShape.getAwtShape(gp)));
				strokedShape = objStroke.createStrokedShape(gp);
			}
			if (geo.getAlphaValue() > 0.0f || geo.isHatchingEnabled()) {
				return t.intersects(x - hitThreshold, y - hitThreshold,
						2 * hitThreshold, 2 * hitThreshold);
			}
			
			// workaround for #2364
			if (geo.isGeoFunction()) {
				return gp.intersects(x - hitThreshold, y - hitThreshold,
					2 * hitThreshold, 2 * hitThreshold) && !gp.contains(x - hitThreshold, y - hitThreshold,
							2 * hitThreshold, 2 * hitThreshold);
			}
			
			// not GeoFunction, eg parametric
			return strokedShape.intersects(x - hitThreshold, y - hitThreshold,
						2 * hitThreshold, 2 * hitThreshold);			
			
		}
		return false;
		/*
		 * return gp.intersects(x-3,y-3,6,6) && !gp.contains(x-3,y-3,6,6);
		 */
	}

	@Override
	public boolean intersectsRectangle(GRectangle rect) {
		if (isVisible) {
			GShape t = geo.isInverseFill() ? getShape() : gp;
			if (strokedShape == null) {
				//strokedShape = new geogebra.awt.GenericShape(geogebra.awt.BasicStroke.getAwtStroke(objStroke).createStrokedShape(geogebra.awt.GenericShape.getAwtShape(gp)));
				strokedShape = objStroke.createStrokedShape(gp);
			}
			if (geo.getAlphaValue() > 0.0f || geo.isHatchingEnabled()) {
				return t.intersects(rect);
			}
			
			return strokedShape.intersects(rect);			
			
		}
		return false;
	}

	@Override
	final public boolean isInside(geogebra.common.awt.GRectangle rect) {
		return gp != null && rect.contains(gp.getBounds());
	}

	@Override
	public GeoElement getGeoElement() {
		return geo;
	}

	@Override
	public void setGeoElement(GeoElement geo) {
		this.geo = geo;
	}

	/**
	 * Returns the bounding box of this DrawPoint in screen coordinates.
	 */
	@Override
	final public geogebra.common.awt.GRectangle getBounds() {
		if (!geo.isDefined() || !curve.isClosedPath()
				|| !geo.isEuclidianVisible()) {
			return null;
		}
		return AwtFactory.prototype.newRectangle(gp.getBounds());
	}

	final private static boolean filling(ParametricCurve curve) {
		return !curve.isFunctionInX()
				&& (curve.toGeoElement().getAlphaValue() > 0 || curve
						.toGeoElement().isHatchingEnabled());
	}

}
