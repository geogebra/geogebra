package com.himamis.retex.renderer.web.graphics;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.CanvasPattern;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.core.client.JavaScriptObject;
import com.himamis.retex.renderer.share.platform.geom.Rectangle2D;
import com.himamis.retex.renderer.share.platform.geom.Shape;
import com.himamis.retex.renderer.web.geom.AreaW;
import com.himamis.retex.renderer.web.geom.ShapeW;

/**
 * @author michael
 *         <p>
 *         Shared class between JLaTeXMath and GeoGebra so that the transform
 *         can be shared https://jira.geogebra.org/browse/TRAC-5353
 *         <p>
 *         Also originally: Added to allow ctx.fill("evenodd") (new winding rule
 *         from ggb50) ignored in IE9, IE10
 */
public class JLMContext2d extends Context2d {

	protected JLMContext2d() {
		// constructor extending Context2d must be empty
	}

	/**
	 * Gets 2D context
	 * 
	 * @param canvas
	 *            canvas
	 * @return context
	 */
	public static JLMContext2d forCanvas(Canvas canvas) {
		return (JLMContext2d) canvas.getContext2d();
	}

	/**
	 * Fills the current path.
	 * 
	 * @param windingRule
	 *            winding rule, either "nonzero" or "evenodd"
	 */
	public final native void fill(String windingRule) /*-{
		this.fill(windingRule);
	}-*/;

	/**
	 * Fills the current path.
	 */
	public final native CanvasPattern createPatternSVG(String path,
			String style, double width, double height, double angle,
			String fill) /*-{
		var svg = {
			"path" : path,
			"width" : width,
			"height" : height,
			"style" : style,
			"angle" : angle,
			"fill" : fill,
		};
		//console.log("createPatternSVG", svg);
		return this.createPatternSVG(svg, "repeating");
	}-*/;

	public final native void initTransform() /*-{
		if (!this) {
			// 3D View, ignore
			return;
		}

		if (!this.ggbDevicePixelRatio) {
			this.ggbDevicePixelRatio = 1;
		}
		this.m00_ = 1;
		this.m10_ = 0;
		this.m01_ = 0;
		this.m11_ = 1;
		this.m02_ = 0;
		this.m12_ = 0;

		this.ggbTransformCache = [];
	}-*/;

	public final native void setDevicePixelRatio(double devicePixelRatio) /*-{
		this.ggbDevicePixelRatio = devicePixelRatio;
	}-*/;

	public final native double getDevicePixelRatio() /*-{
		return this.ggbDevicePixelRatio || 1;
	}-*/;

	public final native void saveTransform(double m00, double m10, double m01,
			double m11, double m02, double m12) /*-{
		this.ggbTransformCache.push([ m00, m10, m01, m11, m02, m12 ]);
	}-*/;

	public final native void saveTransform() /*-{
		if (!this.ggbTransformCache) {
			this.ggbTransformCache = [];
		}

		var t = [ this.m00_, this.m10_, this.m01_, this.m11_, this.m02_,
				this.m12_ ];

		this.ggbTransformCache.push([ t[0], t[1], t[2], t[3], t[4], t[5] ]);

		this.save();
	}-*/;

	public final native void restoreTransform() /*-{

		var dp = this.ggbDevicePixelRatio;

		var t = this.ggbTransformCache.pop();

		if (!t) {
			// correct behaviour is do nothing and return
			this.restore();
			return;
		}

		this.m00_ = t[0];
		this.m10_ = t[1];
		this.m01_ = t[2];
		this.m11_ = t[3];
		this.m02_ = t[4];
		this.m12_ = t[5];

		this.restore();
	}-*/;

	public final native double getScaleX() /*-{
		return this.m00_;
	}-*/;

	public final native double getShearY() /*-{
		return this.m10_;
	}-*/;

	public final native double getShearX() /*-{
		return this.m01_;
	}-*/;

	public final native double getScaleY() /*-{
		return this.m11_;
	}-*/;

	public final native double getTranslateX() /*-{
		return this.m02_;
	}-*/;

	public final native double getTranslateY() /*-{
		return this.m12_;
	}-*/;

	public final native void scale2(double sx, double sy) /*-{
		this.m00_ *= sx;
		this.m10_ *= sx;
		this.m01_ *= sy;
		this.m11_ *= sy;

		this.scale(sx, sy);
	}-*/;

	public final native void translate2(double dx, double dy) /*-{

		this.m02_ += dx * this.m00_ + dy * this.m01_;
		this.m12_ += dx * this.m10_ + dy * this.m11_;

		this.translate(dx, dy);
	}-*/;

	// adapted from TransformW.java
	public final native void rotate2(double theta) /*-{

		var sin = Math.sin(theta);
		if (sin == 1.0) {
			//rotate90();
			var M0 = this.m00_;
			this.m00_ = this.m01_;
			this.m01_ = -M0;
			M0 = this.m10_;
			this.m10_ = this.m11_;
			this.m11_ = -M0;
		} else if (sin == -1.0) {
			//rotate270();
			var M0 = this.m00_;
			this.m00_ = -this.m01_;
			this.m01_ = M0;
			M0 = this.m10_;
			this.m10_ = -this.m11_;
			this.m11_ = M0;

		} else {
			var cos = Math.cos(theta);
			if (cos == -1.0) {
				//rotate180();
				this.m00_ = -this.m00_;
				this.m11_ = -this.m11_;
			} else if (cos != 1.0) {
				var M0, M1;
				M0 = this.m00_;
				M1 = this.m01_;
				this.m00_ = cos * M0 + sin * M1;
				this.m01_ = -sin * M0 + cos * M1;
				M0 = this.m10_;
				M1 = this.m11_;
				this.m10_ = cos * M0 + sin * M1;
				this.m11_ = -sin * M0 + cos * M1;
			}
		}

		this.rotate(theta);
	}-*/;

	public final native void setTransform2(double m00, double m10, double m01,
			double m11, double m02, double m12) /*-{
		this.m00_ = m00;
		this.m10_ = m10;
		this.m01_ = m01;
		this.m11_ = m11;
		this.m02_ = m02;
		this.m12_ = m12;

		this.setTransform(m00, m10, m01, m11, m02, m12);
	}-*/;

	public final native void resetTransform(double dp) /*-{

		if (!this) {
			// 3D View, ignore
			return;
		}

		this.ggbDevicePixelRatio = dp;

		this.setTransform(dp * this.m00_, dp * this.m10_, dp * this.m01_, dp
				* this.m11_, dp * this.m02_, dp * this.m12_);
	}-*/;

	// adapted from goog.graphics.AffineTransform.prototype.concatenate
	public final native void transform2(double m00, double m10, double m01,
			double m11, double m02, double m12) /*-{

		var m0 = this.m00_;
		var m1 = this.m01_;
		this.m00_ = m00 * m0 + m10 * m1;
		this.m01_ = m01 * m0 + m11 * m1;
		this.m02_ += m02 * m0 + m12 * m1;

		m0 = this.m10_;
		m1 = this.m11_;
		this.m10_ = m00 * m0 + m10 * m1;
		this.m11_ = m01 * m0 + m11 * m1;
		this.m12_ += m02 * m0 + m12 * m1;

		// XXX should this be setTransform()?
		this.transform(m00, m10, m01, m11, m02, m12);
	}-*/;

	/**
	 * @param shape
	 *            shape
	 */
	final public void fill(Shape shape) {
		if (shape instanceof Rectangle2D) {
			Rectangle2D rect = (Rectangle2D) shape;
			fillRect(rect.getX(), rect.getY(), rect.getWidth(),
					rect.getHeight());
		} else if (shape instanceof AreaW) {
			AreaW area = (AreaW) shape;
			area.fill(this);
		} else if (shape instanceof ShapeW) {
			((ShapeW) shape).fill(this);
		}
	}

	final public native void fillOpentype(JavaScriptObject path) /*-{
		path.fill = this.fillStyle;
		@com.himamis.retex.renderer.web.font.opentype.OpentypeFontWrapper::drawPath(Lcom/google/gwt/core/client/JavaScriptObject;IILcom/google/gwt/canvas/client/Canvas;)(path, 0, 0, this);
	}-*/;

}
