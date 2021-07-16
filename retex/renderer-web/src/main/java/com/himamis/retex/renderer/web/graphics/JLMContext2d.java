package com.himamis.retex.renderer.web.graphics;

import com.himamis.retex.renderer.share.platform.geom.Rectangle2D;
import com.himamis.retex.renderer.share.platform.geom.Shape;
import com.himamis.retex.renderer.share.platform.graphics.stubs.AffineTransform;
import com.himamis.retex.renderer.web.geom.AreaW;
import com.himamis.retex.renderer.web.geom.ShapeW;

import elemental2.core.JsArray;
import elemental2.dom.CanvasPattern;
import elemental2.dom.CanvasRenderingContext2D;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;

/**
 * @author michael
 *         <p>
 *         Shared class between JLaTeXMath and GeoGebra so that the transform
 *         can be shared https://jira.geogebra.org/browse/TRAC-5353
 *         <p>
 *         Also originally: Added to allow ctx.fill("evenodd") (new winding rule
 *         from ggb50) ignored in IE9, IE10
 */

@JsType(isNative = true, namespace = JsPackage.GLOBAL)
public class JLMContext2d extends CanvasRenderingContext2D {
	private double ggbDevicePixelRatio;
	private double m00_;
	private double m01_;
	private double m10_;
	private double m11_;
	private double m02_;
	private double m12_;
	private JsArray<JsArray<Double>> ggbTransformCache;

	protected JLMContext2d() {
		// constructor extending Context2d must be empty
	}

	/**
	 * Fills the current path.
	 */
	@JsOverlay
	public final CanvasPattern createPatternSVG(String path,
			String style, double width, double height, double angle,
			String fill) {
		JsPropertyMap svg = JsPropertyMap.of("path", path);
		svg.set("width", width);
		svg.set("height", height);
		svg.set("style", style);
		svg.set("angle", angle);
		svg.set("fill", fill);
		return createPatternSVG(svg, "repeating");
	}

	public native CanvasPattern createPatternSVG(JsPropertyMap svg, String repetition);

	@JsOverlay
	public final void initTransform() {
		if (this.ggbDevicePixelRatio == 0) {
			this.ggbDevicePixelRatio = 1;
		}
		this.m00_ = 1;
		this.m10_ = 0;
		this.m01_ = 0;
		this.m11_ = 1;
		this.m02_ = 0;
		this.m12_ = 0;

		this.ggbTransformCache = JsArray.of();
	}

	@JsOverlay
	public final void setDevicePixelRatio(double devicePixelRatio) {
		this.ggbDevicePixelRatio = devicePixelRatio;
	}

	@JsOverlay
	public final double getDevicePixelRatio() {
		return Js.isFalsy(this.ggbDevicePixelRatio) ? 1 : ggbDevicePixelRatio;
	}

	@JsOverlay
	public final void saveTransform(double m00, double m10, double m01,
			double m11, double m02, double m12) {
		this.ggbTransformCache.push(JsArray.of( m00, m10, m01, m11, m02, m12 ));
	}

	@JsOverlay
	public final void saveTransform() {
		if (this.ggbTransformCache == null) {
			this.ggbTransformCache = JsArray.of();
		}

		JsArray t = JsArray.of(this.m00_, this.m10_, this.m01_, this.m11_, this.m02_,
				this.m12_);

		this.ggbTransformCache.push(t);

		this.save();
	}

	@JsOverlay
	public final void restoreTransform() {
		JsArray<Double> t = this.ggbTransformCache.pop();
		if (Js.isTruthy(t)) {
			this.m00_ = t.getAt(0);
			this.m10_ = t.getAt(1);
			this.m01_ = t.getAt(2);
			this.m11_ = t.getAt(3);
			this.m02_ = t.getAt(4);
			this.m12_ = t.getAt(5);
		}
		this.restore();
	}

	@JsOverlay
	public final void scale2(double sx, double sy) {
		this.m00_ *= sx;
		this.m10_ *= sx;
		this.m01_ *= sy;
		this.m11_ *= sy;

		this.scale(sx, sy);
	}

	@JsOverlay
	public final void translate2(double dx, double dy) {

		this.m02_ += dx * this.m00_ + dy * this.m01_;
		this.m12_ += dx * this.m10_ + dy * this.m11_;

		this.translate(dx, dy);
	}

	// adapted from TransformW.java
	@JsOverlay
	public final void rotate2(double theta) {

		double sin = Math.sin(theta);
		if (sin == 1.0) {
			//rotate90();
			double M0 = this.m00_;
			this.m00_ = this.m01_;
			this.m01_ = -M0;
			M0 = this.m10_;
			this.m10_ = this.m11_;
			this.m11_ = -M0;
		} else if (sin == -1.0) {
			//rotate270();
			double M0 = this.m00_;
			this.m00_ = -this.m01_;
			this.m01_ = M0;
			M0 = this.m10_;
			this.m10_ = -this.m11_;
			this.m11_ = M0;

		} else {
			double cos = Math.cos(theta);
			if (cos == -1.0) {
				//rotate180();
				this.m00_ = -this.m00_;
				this.m11_ = -this.m11_;
			} else if (cos != 1.0) {
				double M0, M1;
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
	}

	@JsOverlay
	public final void setTransform2(double m00, double m10, double m01,
			double m11, double m02, double m12) {
		this.m00_ = m00;
		this.m10_ = m10;
		this.m01_ = m01;
		this.m11_ = m11;
		this.m02_ = m02;
		this.m12_ = m12;

		this.setTransform(m00, m10, m01, m11, m02, m12);
	}

	@JsOverlay
	public final void resetTransform(double dp) {
		this.ggbDevicePixelRatio = dp;
		this.setTransform(dp * this.m00_, dp * this.m10_, dp * this.m01_, dp
				* this.m11_, dp * this.m02_, dp * this.m12_);
	}

	// adapted from goog.graphics.AffineTransform.prototype.concatenate
	@JsOverlay
	public final void transform2(double m00, double m10, double m01,
			double m11, double m02, double m12) {

		double m0 = this.m00_;
		double m1 = this.m01_;
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
	}

	/**
	 * @param shape
	 *            shape
	 */
	@JsOverlay
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

	@JsOverlay
	public final void setFillStyle(Object colorStr) {
		fillStyle = FillStyleUnionType.of(colorStr);
	}

	@JsOverlay
	public final void setStrokeStyle(String colorStr) {
		strokeStyle = StrokeStyleUnionType.of(colorStr);
	}

	@JsOverlay
	public final AffineTransform getTransform() {
		return new AffineTransform(this.m00_, this.m10_,
				this.m01_, this.m11_, this.m02_, this.m12_);
	}
}
