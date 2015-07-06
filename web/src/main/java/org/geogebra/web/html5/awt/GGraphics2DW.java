package org.geogebra.web.html5.awt;

import java.util.Map;

import org.geogebra.common.awt.GAffineTransform;
import org.geogebra.common.awt.GBasicStroke;
import org.geogebra.common.awt.GBufferedImage;
import org.geogebra.common.awt.GBufferedImageOp;
import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GComposite;
import org.geogebra.common.awt.GDimension;
import org.geogebra.common.awt.GFontRenderContext;
import org.geogebra.common.awt.GImage;
import org.geogebra.common.awt.GKey;
import org.geogebra.common.awt.GPaint;
import org.geogebra.common.awt.GShape;
import org.geogebra.common.awt.MyImage;
import org.geogebra.common.euclidian.GeneralPathClipped;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.kernel.View;
import org.geogebra.common.main.App;
import org.geogebra.common.util.StringUtil;
import org.geogebra.ggbjdk.java.awt.geom.AffineTransform;
import org.geogebra.ggbjdk.java.awt.geom.PathIterator;
import org.geogebra.ggbjdk.java.awt.geom.Shape;
import org.geogebra.web.html5.gawt.GBufferedImageW;
import org.geogebra.web.html5.main.MyImageW;
import org.geogebra.web.html5.util.ImageLoadCallback;
import org.geogebra.web.html5.util.ImageWrapper;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.CanvasPattern;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.Context2d.Repetition;
import com.google.gwt.canvas.dom.client.ImageData;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayNumber;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.ImageElement;

public class GGraphics2DW implements org.geogebra.common.awt.GGraphics2D {

	protected final Canvas canvas;
	private final MyContext2d context;
	protected org.geogebra.common.awt.GShape clipShape = null;

	private GFontW currentFont = new GFontW("normal");
	private GColor color = new GColorW(255, 255, 255, 255);
	private GAffineTransform currentTransform;
	private float[] dash_array = null;

	GPaint currentPaint = new GColorW(255, 255, 255, 255);
	private JsArrayNumber jsarrn;

	private View view;

	/**
	 * the pixel ratio of the canvas.
	 */
	public double devicePixelRatio = 1;
	private int id;

	private static int counter = 1;

	/**
	 * @param canvas
	 */
	public GGraphics2DW(Canvas canvas) {
		this.id = counter++;
		this.canvas = canvas;
		setDirection();
		this.context = (MyContext2d) canvas.getContext2d();
		currentTransform = new AffineTransform();
		preventContextMenu(canvas.getElement());
	}

	/**
	 * @param view
	 *            The view associated with this instance of GGraphics2DW
	 */
	public void setView(View view) {
		this.view = view;
	}

	/**
	 * @return The view associated with this instance of GGraphics2DW, null if
	 *         no view has been set
	 */
	public View getView() {
		return view;
	}

	public void setImageInterpolation(boolean b) {
		setImageInterpolationNative(canvas.getContext2d(), b);
	}

	private native void setImageInterpolationNative(Context2d ctx, boolean b) /*-{

		ctx['imageSmoothingEnabled'] = b;
		ctx['mozImageSmoothingEnabled'] = b;
		// IE11+ only
		ctx['msImageSmoothingEnabled'] = b;
		ctx['oImageSmoothingEnabled'] = b;
		// deprecated
		// https://groups.google.com/a/chromium.org/forum/#!msg/blink-dev/Ud3cV1mj35s/Ssat21OeRqYJ
		// ctx['webkitImageSmoothingEnabled'] = b;

	}-*/;

	/**
	 * If we allow right-to left direction * checkboxes have their labels to the
	 * right * labels are drawn to the right, hence the check to fit in screen
	 * will probably fail * labels are malformed, eg )A=(1,2
	 */
	private void setDirection() {
		this.canvas.getElement().setDir("ltr");
	}

	public GGraphics2DW(Canvas canvas, boolean resetColor) {
		this(canvas);
		if (resetColor) {
			updateCanvasColor();
		}
	}

	private native void preventContextMenu(Element canvas) /*-{
		canvas.addEventListener("contextmenu", function(e) {
			e.preventDefault();
			e.stopPropagation();
			return false;
		});
	}-*/;

	public void draw3DRect(int x, int y, int width, int height, boolean raised) {
		App.debug("draw3DRect: implementation needed");

	}

	public void fill3DRect(int x, int y, int width, int height, boolean raised) {
		App.debug("fill3DRect: implementation needed");
	}

	private double[] coords = new double[6];

	public void drawStraightLine(double x1, double y1, double x2, double y2) {
		int width = (int) context.getLineWidth();
		context.beginPath();
		if (dash_array == null || nativeDashUsed) {
			if (width % 2 == 1) {
				context.moveTo(Math.floor(x1) + 0.5, Math.floor(y1) + 0.5);
				context.lineTo(Math.floor(x2) + 0.5, Math.floor(y2) + 0.5);
			} else {
				context.moveTo(Math.round(x1), Math.round(y1));
				context.lineTo(Math.round(x2), Math.round(y2));
			}
		} else {
			drawStraightDashedLine(x1, y1, x2, y2, jsarrn, context,
			        width % 2 == 1);
		}
		context.stroke();
	}

	protected void doDrawShape(Shape shape, boolean enableDashEmulation) {
		context.beginPath();
		PathIterator it = shape.getPathIterator(null);

		// see #1718
		// boolean enableDashEmulation = true;//nativeDashUsed ||
		// App.isFullAppGui();

		while (!it.isDone()) {
			int cu = it.currentSegment(coords);
			switch (cu) {
			case PathIterator.SEG_MOVETO:
				context.moveTo(coords[0], coords[1]);
				if (enableDashEmulation)
					setLastCoords(coords[0], coords[1]);
				break;
			case PathIterator.SEG_LINETO:
				if (dash_array == null || !enableDashEmulation) {
					context.lineTo(coords[0], coords[1]);
				} else {
					if (nativeDashUsed) {
						context.lineTo(coords[0], coords[1]);
					} else {
						drawDashedLine(pathLastX, pathLastY, coords[0],
						        coords[1], jsarrn, context);
					}
				}
				setLastCoords(coords[0], coords[1]);
				break;
			case PathIterator.SEG_CUBICTO:
				context.bezierCurveTo(coords[0], coords[1], coords[2],
				        coords[3], coords[4], coords[5]);
				if (enableDashEmulation)
					setLastCoords(coords[4], coords[5]);
				break;
			case PathIterator.SEG_QUADTO:
				context.quadraticCurveTo(coords[0], coords[1], coords[2],
				        coords[3]);
				if (enableDashEmulation)
					setLastCoords(coords[2], coords[3]);
				break;
			case PathIterator.SEG_CLOSE:
				context.closePath();
			default:
				break;
			}
			it.next();
		}
		// this.closePath();
	}

	private double pathLastX;
	private double pathLastY;

	private void setLastCoords(double x, double y) {
		pathLastX = x;
		pathLastY = y;
	}

	//

	public void drawString(String str, int x, int y) {
		context.fillText(str, x, y);
	}

	public void drawString(String str, float x, float y) {
		context.fillText(str, x, y);
	}

	public void setComposite(GComposite comp) {
		context.setGlobalAlpha(((GAlphaCompositeW) comp).getAlpha());

		// if (comp != null) {
		// float alpha= ((AlphaComposite) comp).getAlpha();
		// if (alpha >= 0f && alpha < 1f) {
		// context.setGlobalAlpha(alpha);
		// }
		// context.setGlobalAlpha(0.5d);
		// context.restore();
		// }
	}

	public void setPaint(final GPaint paint) {
		if (paint instanceof GColor) {
			setColor((GColor) paint);
		} else if (paint instanceof GGradientPaintW) {
			context.setFillStyle(((GGradientPaintW) paint).getGradient(context));
			currentPaint = new GGradientPaintW((GGradientPaintW) paint);
			color = null;
		} else if (paint instanceof GTexturePaintW) {
			try {// bug in Firefox
				 // https://groups.google.com/forum/#!msg/craftyjs/3qRwn_cW1gs/DdPTaCD81ikJ
				 // NS_ERROR_NOT_AVAILABLE: Component is not available
				 // https://bugzilla.mozilla.org/show_bug.cgi?id=574330
				final GBufferedImageW bi = ((GTexturePaintW) paint).getImg();
				CanvasPattern ptr;
				if (bi.hasCanvas()) {
					currentPaint = new GTexturePaintW((GTexturePaintW) paint);
					ptr = context.createPattern(bi.getCanvas()
					        .getCanvasElement(), Repetition.REPEAT);
					context.setFillStyle(ptr);
					color = null;
				} else if (bi.isLoaded()) {
					currentPaint = new GTexturePaintW((GTexturePaintW) paint);
					ptr = context.createPattern(bi.getImageElement(),
					        Repetition.REPEAT);
					context.setFillStyle(ptr);
					color = null;
				} else {
					ImageWrapper.nativeon(bi.getImageElement(), "load",
					        new ImageLoadCallback() {
						        public void onLoad() {
							        currentPaint = new GTexturePaintW(
							                (GTexturePaintW) paint);
							        CanvasPattern ptr = context.createPattern(
							                bi.getImageElement(),
							                Repetition.REPEAT);
							        context.setFillStyle(ptr);
							        color = null;
						        }
					        });
				}
			} catch (Throwable e) {
				App.error(e.getMessage());
			}
		} else {
			App.error("unknown paint type");
		}
	}

	public void setStroke(GBasicStroke stroke) {
		if (stroke != null) {
			context.setLineWidth(((GBasicStrokeW) stroke).getLineWidth());
			context.setLineCap(((GBasicStrokeW) stroke).getEndCapString());
			context.setLineJoin(((GBasicStrokeW) stroke).getLineJoinString());

			float[] dasharr = ((GBasicStrokeW) stroke).getDashArray();
			if (dasharr != null) {
				jsarrn = JavaScriptObject.createArray().cast();
				jsarrn.setLength(dasharr.length);
				for (int i = 0; i < dasharr.length; i++)
					jsarrn.set(i, dasharr[i]);
				setStrokeDash(context, jsarrn);
			} else {
				setStrokeDash(context, null);
			}
			dash_array = dasharr;
		}
	}

	private boolean nativeDashUsed = false;
	private int canvasWidth;
	private int canvasHeight;

	public native void setStrokeDash(Context2d ctx, JsArrayNumber dasharray) /*-{
		if (dasharray === undefined || dasharray === null) {
			dasharray = [];
		}

		if (typeof ctx.setLineDash === 'function') {
			ctx.setLineDash(dasharray);
			this.@org.geogebra.web.html5.awt.GGraphics2DW::nativeDashUsed = true;
		} else if (typeof ctx.mozDash !== 'undefined') {
			ctx.mozDash = dasharray;
			this.@org.geogebra.web.html5.awt.GGraphics2DW::nativeDashUsed = true;
		} else if (typeof ctx.webkitLineDash !== 'undefined') {
			ctx.webkitLineDash = dasharray;
			this.@org.geogebra.web.html5.awt.GGraphics2DW::nativeDashUsed = true;
		}

	}-*/;

	public void setRenderingHint(GKey hintKey, Object hintValue) {
		//

	}

	public Object getRenderingHint(GKey hintKey) {
		//
		return null;
	}

	public void setRenderingHints(Map<?, ?> hints) {
		//

	}

	public void translate(double tx, double ty) {
		context.translate(tx, ty);
		currentTransform.translate(tx, ty);

	}

	public void scale(double sx, double sy) {
		context.scale(sx, sy);
		currentTransform.scale(sx, sy);
	}

	public void transform(GAffineTransform Tx) {
		context.transform(Tx.getScaleX(), Tx.getShearY(), Tx.getShearX(),
		        Tx.getScaleY(), ((AffineTransform) Tx).getTranslateX(),
		        ((AffineTransform) Tx).getTranslateY());
		currentTransform.concatenate(Tx);
	}

	public void setTransform(GAffineTransform Tx) {
		context.setTransform(devicePixelRatio * Tx.getScaleX(),
				devicePixelRatio * Tx.getShearY(),
				devicePixelRatio * Tx.getShearX(),
				devicePixelRatio * Tx.getScaleY(), devicePixelRatio
						* ((AffineTransform) Tx).getTranslateX(),
				devicePixelRatio * ((AffineTransform) Tx).getTranslateY());
		currentTransform = Tx;

	}

	public GAffineTransform getTransform() {
		GAffineTransform ret = new AffineTransform();
		ret.setTransform(currentTransform);
		return ret;
	}

	public GComposite getComposite() {
		return new GAlphaCompositeW(3, (float) context.getGlobalAlpha());

		// context.save();
		// //just to not return null;
		// return new AlphaComposite(0, 0) {
		// };
	}

	public GColor getBackground() {
		return GColor.WHITE;
	}

	public GBasicStroke getStroke() {

		return new GBasicStrokeW((float) context.getLineWidth(),
		        GBasicStrokeW.getCap(context.getLineCap()),
		        GBasicStrokeW.getJoin(context.getLineJoin()), 0, dash_array, 0);
	}

	public GFontRenderContext getFontRenderContext() {
		return new GFontRenderContextW(context);
	}

	public GColor getColor() {
		return color;
	}

	public GFontW getFont() {
		return currentFont;
	}

	private int physicalPX(int logicalPX) {
		return (int) (logicalPX * devicePixelRatio);
	}
	public void setCoordinateSpaceSize(int width, int height) {
		App.debug("Coordinates ratio" + devicePixelRatio);
		canvas.setCoordinateSpaceWidth(physicalPX(width));
		canvas.setCoordinateSpaceHeight(physicalPX(height));
		setTransform(currentTransform);
		setWidth(width);
		setHeight(height);
		this.updateCanvasColor();
	}

	public int getOffsetWidth() {
		int width = canvas.getOffsetWidth();
		return width == 0 ? canvasWidth : width;
	}

	public int getOffsetHeight() {
		int height = canvas.getOffsetHeight();
		return height == 0 ? canvasHeight : height;
	}

	public int getCoordinateSpaceWidth() {
		return canvas.getCoordinateSpaceWidth();
	}

	public int getCoordinateSpaceHeight() {
		return canvas.getCoordinateSpaceHeight();
	}

	public int getAbsoluteTop() {
		return canvas.getAbsoluteTop();
	}

	public int getAbsoluteLeft() {
		return canvas.getAbsoluteLeft();
	}

	public void setFont(org.geogebra.common.awt.GFont font) {
		if (font instanceof GFontW) {
			currentFont = (GFontW) font;
			// TODO: pass other parameters here as well
			try {
				context.setFont(currentFont.getFullFontString());
			} catch (Throwable t) {
				App.error("problem setting font: "
				        + currentFont.getFullFontString());
			}
		}

	}

	public void setColor(GColor fillColor) {
		// checking for the same color here speeds up axis drawing by 25%
		if (fillColor != null && fillColor.equals(color)) {
			return;
		}
		// but it seems that setColor is not only for setting "color",
		// but also for setFillStyle and setStrokeStyle,
		// and it seems that this is necessary to run,
		// until a better solution is found - see ticket #4291

		this.color = fillColor;
		updateCanvasColor();
		this.currentPaint = new GColorW((GColorW) fillColor);
	}

	public void updateCanvasColor() {
		if (color == null) {
			return;
		}
		String colorStr = "rgba(" + color.getRed() + "," + color.getGreen()
		        + "," + color.getBlue() + "," + (color.getAlpha() / 255d) + ")";
		context.setStrokeStyle(colorStr);
		context.setFillStyle(colorStr);

	}

	public void clip(org.geogebra.common.awt.GShape shape) {
		if (shape == null) {
			App.error("Error in Graphics2D.clip");
			return;
		}
		clipShape = shape;

		Shape shape2 = (Shape) shape;

		doDrawShape(shape2, false);
		context.save();
		context.clip();
	}

	public void fillRect(int x, int y, int w, int h) {
		context.fillRect(x, y, w, h);
	}

	public void clearRect(int x, int y, int w, int h) {
		context.save();
		context.setTransform(1, 0, 0, 1, 0, 0);
		context.clearRect(x, y, w, h);
		context.restore();
	}

	public void drawLine(int x1, int y1, int x2, int y2) {

		/*
		 * TODO: there is some differences between the result of
		 * geogebra.awt.Graphics.drawLine(...) function. Here is an attempt to
		 * make longer the vertical and horizontal lines:
		 * 
		 * int x_1 = Math.min(x1,x2); int y_1 = Math.min(y1,y2); int x_2 =
		 * Math.max(x1,x2); int y_2 = Math.max(y1,y2);
		 * 
		 * if(x1==x2){ y_1--; y_2++; } else if(y1==y2){ x_1--; x_2++; }
		 * 
		 * context.beginPath(); context.moveTo(x_1, y_1); context.lineTo(x_2,
		 * y_2); context.closePath(); context.stroke();
		 */
		context.beginPath();
		context.moveTo(x1, y1);
		context.lineTo(x2, y2);
		context.closePath();
		context.stroke();

	}

	public void setClip(org.geogebra.common.awt.GShape shape) {
		clipShape = shape;
		if (shape == null) {
			// this may be an intentional call to restore the context
			context.restore();
			return;
		}
		Shape shape2 = (Shape) shape;

		doDrawShape(shape2, false);
		if (clipShape != null) {
			// we should call this only if no clip was set or just after another
			// clip to overwrite
			// in this case we don't want to double-clip something so let's
			// restore the context
			context.restore();
		}
		context.save();
		context.clip();
	}

	public void draw(org.geogebra.common.awt.GShape shape) {
		if (shape == null) {
			App.error("Error in EuclidianView.draw");
			return;
		}
		if (shape instanceof GeneralPathClipped) {
			doDrawShape((Shape) ((GeneralPathClipped) shape).getGeneralPath(),
			        true);
		} else {
			doDrawShape((Shape) shape, true);
		}
		context.stroke();
	}

	public void fill(org.geogebra.common.awt.GShape gshape) {
		if (gshape == null) {
			App.error("Error in EuclidianView.draw");
			return;
		}
		Shape shape;
		if (gshape instanceof GeneralPathClipped) {
			shape = (Shape) ((GeneralPathClipped) gshape).getGeneralPath();
		} else {
			shape = (Shape) gshape;
		}

		doDrawShape(shape, false);

		/*
		 * App.debug((shape instanceof GeneralPath)+""); App.debug((shape
		 * instanceof GeneralPathClipped)+"");
		 * App.debug((shape.getClass().toString())+"");
		 */

		// default winding rule changed for ggb50 (for Polygons) #3983
		if (shape instanceof org.geogebra.ggbjdk.java.awt.geom.GeneralPath) {
			org.geogebra.ggbjdk.java.awt.geom.GeneralPath gp = (org.geogebra.ggbjdk.java.awt.geom.GeneralPath) shape;
			int rule = gp.getWindingRule();
			if (rule == org.geogebra.ggbjdk.java.awt.geom.GeneralPath.WIND_EVEN_ODD) {
				context.fill("evenodd");
			} else {
				// context.fill("") differs between browsers
				context.fill();
			}
		} else {
			context.fill();
		}
	}

	public org.geogebra.common.awt.GShape getClip() {
		return clipShape;
	}

	public void drawRect(int x, int y, int width, int height) {
		context.beginPath();
		context.rect(x, y, width, height);
		context.stroke();

	}

	public void setClip(int x, int y, int width, int height) {

		float[] dash_array_save = dash_array;
		dash_array = null;
		org.geogebra.common.awt.GShape sh = AwtFactory.prototype.newRectangle(x, y,
		        width, height);
		setClip(sh);
		dash_array = dash_array_save;

		/*
		 * alternative: makes clipping bad, see #3212
		 * 
		 * //context.save(); context.beginPath(); context.moveTo(x, y);
		 * context.lineTo(x + width, y); context.lineTo(x + width, y + height);
		 * context.lineTo(x , y + height); context.lineTo(x , y);
		 * //context.closePath(); context.clip();
		 */
	}

	public void setWidth(int w) {
		this.canvasWidth = w;
		canvas.setWidth(w + "px");
	}

	public void setHeight(int h) {
		this.canvasHeight = h;
		canvas.setHeight(h + "px");
	}

	public void setPreferredSize(GDimension preferredSize) {
		setWidth(Math.max(0, preferredSize.getWidth()));
		setHeight(Math.max(0, preferredSize.getHeight()));

		// do not use getOffsetWidth here,
		// as it is prepared by the browser and not yet ready...
		// if preferredSize can be negative, have a check for it instead
		setCoordinateSpaceSize(
				(preferredSize.getWidth() >= 0) ? preferredSize.getWidth() : 0,
				(preferredSize.getHeight() >= 0) ? preferredSize.getHeight()
						: 0);
	}

	public Canvas getCanvas() {
		return this.canvas;
	}

	public void drawRoundRect(int x, int y, int width, int height,
	        int arcWidth, int arcHeight) {
		roundRect(x, y, width, height, arcHeight - arcHeight / 2);
		context.stroke();

	}

	/**
	 * Using arc, because arc to has buggy implementation in some browsers
	 * 
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 * @param r
	 */
	private void roundRect(int x, int y, int w, int h, int r) {
		context.beginPath();
		int ey = y + h;
		int ex = x + w;
		float r2d = (float) Math.PI / 180;
		context.moveTo(x + r, y);
		context.lineTo(ex - r, y);
		context.arc(ex - r, y + r, r, r2d * 270, r2d * 360, false);
		context.lineTo(ex, ey - r);
		context.arc(ex - r, ey - r, r, r2d * 0, r2d * 90, false);
		context.lineTo(x + r, ey);
		context.arc(x + r, ey - r, r, r2d * 90, r2d * 180, false);
		context.lineTo(x, y + r);
		context.arc(x + r, y + r, r, r2d * 180, r2d * 270, false);

		context.closePath();
	}

	public void fillRoundRect(int x, int y, int width, int height,
	        int arcWidth, int arcHeight) {
		roundRect(x, y, width, height, arcHeight - arcHeight / 2);
		context.fill("evenodd");

	}

	private native void drawStraightDashedLine(double fromX, double fromY,
	        double toX, double toY, JsArrayNumber pattern, Context2d ctx,
	        boolean odd) /*-{
		// Our growth rate for our line can be one of the following:
		// (+,+), (+,-), (-,+), (-,-)
		// Because of this, our algorithm needs to understand if the x-coord and
		// y-coord should be getting smaller or larger and properly cap the values
		// based on (x,y).

		// make sure we don't get an infinite loop drawing eg
		//y = -7.85046229341888E-17x
		var EPSILON = 0.00000001;

		var lt = function(a, b) {
			return a <= b + EPSILON;
		};
		var gt = function(a, b) {
			return a >= b - EPSILON;
		};
		var capmin = function(a, b) {
			return $wnd.Math.min(a, b);
		};
		var capmax = function(a, b) {
			return $wnd.Math.max(a, b);
		};

		var checkX = {
			thereYet : gt,
			cap : capmin
		};
		var checkY = {
			thereYet : gt,
			cap : capmin
		};

		if (fromY - toY > 0) {
			checkY.thereYet = lt;
			checkY.cap = capmax;
		}
		if (fromX - toX > 0) {
			checkX.thereYet = lt;
			checkX.cap = capmax;
		}

		//ctx.moveTo(fromX, fromY);
		var offsetX = fromX;
		var offsetY = fromY;
		var idx = 0, dash = true;
		var ang = $wnd.Math.atan2(toY - fromY, toX - fromX);
		var cos = $wnd.Math.cos(ang);
		var sin = $wnd.Math.sin(ang);

		while (!(checkX.thereYet(offsetX, toX) && checkY.thereYet(offsetY, toY))) {

			var len = pattern[idx];

			offsetX = checkX.cap(toX, offsetX + (cos * len));
			offsetY = checkY.cap(toY, offsetY + (sin * len));

			if (dash)
				ctx.lineTo(odd ? $wnd.Math.floor(offsetX) + 0.5 : $wnd.Math
						.round(offsetX), odd ? $wnd.Math.floor(offsetY) + 0.5
						: Math.round(offsetY));
			else
				ctx.moveTo(odd ? $wnd.Math.floor(offsetX) + 0.5 : $wnd.Math
						.round(offsetX), odd ? $wnd.Math.floor(offsetY) + 0.5
						: Math.round(offsetY));

			idx = (idx + 1) % pattern.length;
			dash = !dash;
		}

	}-*/;

	private native void drawDashedLine(double fromX, double fromY, double toX,
	        double toY, JsArrayNumber pattern, Context2d ctx) /*-{
		// Our growth rate for our line can be one of the following:
		// (+,+), (+,-), (-,+), (-,-)
		// Because of this, our algorithm needs to understand if the x-coord and
		// y-coord should be getting smaller or larger and properly cap the values
		// based on (x,y).

		// make sure we don't get an infinite loop drawing eg
		//y = -7.85046229341888E-17x
		var EPSILON = 0.00000001;

		var lt = function(a, b) {
			return a <= b + EPSILON;
		};
		var gt = function(a, b) {
			return a >= b - EPSILON;
		};
		var capmin = function(a, b) {
			return $wnd.Math.min(a, b);
		};
		var capmax = function(a, b) {
			return $wnd.Math.max(a, b);
		};

		var checkX = {
			thereYet : gt,
			cap : capmin
		};
		var checkY = {
			thereYet : gt,
			cap : capmin
		};

		if (fromY - toY > 0) {
			checkY.thereYet = lt;
			checkY.cap = capmax;
		}
		if (fromX - toX > 0) {
			checkX.thereYet = lt;
			checkX.cap = capmax;
		}

		//ctx.moveTo(fromX, fromY);
		var offsetX = fromX;
		var offsetY = fromY;
		var idx = 0, dash = true;
		var ang = $wnd.Math.atan2(toY - fromY, toX - fromX);
		var cos = $wnd.Math.cos(ang);
		var sin = $wnd.Math.sin(ang);

		while (!(checkX.thereYet(offsetX, toX) && checkY.thereYet(offsetY, toY))) {

			var len = pattern[idx];

			offsetX = checkX.cap(toX, offsetX + (cos * len));
			offsetY = checkY.cap(toY, offsetY + (sin * len));

			if (dash)
				ctx.lineTo(offsetX, offsetY);
			else
				ctx.moveTo(offsetX, offsetY);

			idx = (idx + 1) % pattern.length;
			dash = !dash;
		}

	}-*/;

	public ImageData getImageData(int x, int y, int width, int height) {
		return context.getImageData(x, y, width, height);
	}

	/**
	 * @param data
	 *            Imagedata to put on the canvas
	 */
	public void putImageData(ImageData data, double x, double y) {
		context.putImageData(data, x, y);
	}

	public void drawImage(GBufferedImage img, int x, int y) {
		drawImage(img, null, x, y);
	}

	public void setAntialiasing() {
		// not needed
	}

	public void setTransparent() {
		setComposite(GAlphaCompositeW.Src);
	}

	public void fillWith(GColor color) {
		this.setColor(color);
		this.fillRect(0, 0, canvas.getCoordinateSpaceWidth(),
		        canvas.getCoordinateSpaceHeight());
	}

	private boolean lastDebugOk = false;
	private boolean lastDebugException = false;

	public void debug() {
		// TODO Auto-generated method stub
		String physical = context.getFillStyle().toString().toUpperCase();
		String logical = "null";
		if (color != null) {
			logical = color.getAlpha() < 255 ? "RGBA(" + color.getRed() + ", "
			        + color.getGreen() + ", " + color.getBlue() + ", 0."
			        + (int) (1000000 * color.getAlpha() / 255d) + ")" : "#"
			        + StringUtil.toHexString(color).toUpperCase();
		}
		if (color == null && physical.contains("OBJ")) {
			System.out.println(hashCode() + ": not colors");
			lastDebugOk = false;
			lastDebugException = false;
		} else if (!logical.equals(physical)) {
			if (!lastDebugException) {
				App.printStacktrace(hashCode() + ": "
				        + logical.replace(".0", "") + " / "
				        + physical.replace(".0", ""));
			}
			lastDebugOk = false;
			lastDebugException = true;
		} else if (!lastDebugOk) {
			System.out.println(hashCode() + ": ok");
			lastDebugOk = true;
			lastDebugException = false;
		}
	}

	public double getScale() {
		return devicePixelRatio;
	}

	public MyContext2d getContext() {
		return context;
	}

	public void drawWithValueStrokePure(GShape shape) {
		draw(shape);
	}

	public void fillWithValueStrokePure(GShape shape) {
		fill(shape);

	}

	public Object setInterpolationHint(boolean needsInterpolationRenderingHint) {
		this.setImageInterpolation(needsInterpolationRenderingHint);
		return null;
	}

	public void resetInterpolationHint(Object oldInterpolationHint) {
		this.setImageInterpolation(true);

	}

	public void drawImage(MyImage img, GBufferedImageOp op, int x, int y) {
		context.drawImage(((MyImageW) img).getImage(), x, y);

	}

	public void drawImage(org.geogebra.common.awt.GBufferedImage img,
	        GBufferedImageOp op, int x, int y) {
		GBufferedImageW bi = (GBufferedImageW) img;
		if (bi == null)
			return;
		try {
			if (bi.hasCanvas()) {
				context.drawImage(
						bi.getCanvas().getCanvasElement(),
						0,
						0,
						bi
						.getCanvas().getCoordinateSpaceWidth(), bi.getCanvas()
.getCoordinateSpaceHeight(),
						x,
						y,
 this
						.getOffsetWidth(), this.getOffsetHeight());

			} else {
				context.drawImage(bi.getImageElement(), x, y);
			}
		} catch (Exception e) {
			App.error("error in context.drawImage method");
		}
	}

	public void drawImage(ImageElement img, int x, int y) {
		try {
			context.drawImage(img, x, y);
		} catch (Exception e) {
			App.error("error in context.drawImage method");
		}
	}

	public void drawImage(MyImage img, int x, int y) {
		context.drawImage(((MyImageW) img).getImage(), x, y);

	}

	// TODO: remove?
	public void drawImage(GImage img, int x, int y) {
		App.debug("drawImage: implementation needed");
	}

	public Integer getID() {
		return this.id;
	}

	private GAffineTransform savedTransform;

	@Override
	public void saveTransform() {
		savedTransform = getTransform();
	}

	@Override
	public void restoreTransform() {
		if (savedTransform == null) {
			throw new RuntimeException("The method saveTransform() was not called");
		}
		setTransform(savedTransform);
		savedTransform = null;
	}

}
