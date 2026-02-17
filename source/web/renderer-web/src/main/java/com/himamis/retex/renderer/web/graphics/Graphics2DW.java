/*
 * This file is part of the ReTeX library - https://github.com/himamis/ReTeX
 *
 * Copyright (C) 2015 Balazs Bencze
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or (at
 * your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * A copy of the GNU General Public License can be found in the file
 * LICENSE.txt provided with the source distribution of this program (see
 * the META-INF directory in the source jar). This license can also be
 * found on the GNU website at http://www.gnu.org/licenses/gpl.html.
 *
 * If you did not receive a copy of the GNU General Public License along
 * with this program, contact the lead developer, or write to the Free
 * Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301, USA.
 *
 * Linking this library statically or dynamically with other modules 
 * is making a combined work based on this library. Thus, the terms 
 * and conditions of the GNU General Public License cover the whole 
 * combination.
 * 
 * As a special exception, the copyright holders of this library give you 
 * permission to link this library with independent modules to produce 
 * an executable, regardless of the license terms of these independent 
 * modules, and to copy and distribute the resulting executable under terms 
 * of your choice, provided that you also meet, for each linked independent 
 * module, the terms and conditions of the license of that module. 
 * An independent module is a module which is not derived from or based 
 * on this library. If you modify this library, you may extend this exception 
 * to your version of the library, but you are not obliged to do so. 
 * If you do not wish to do so, delete this exception statement from your 
 * version.
 * 
 */

package com.himamis.retex.renderer.web.graphics;

import static org.geogebra.web.awt.GGraphics2DW.doDrawShape;

import java.util.ArrayList;

import org.geogebra.common.awt.AwtFactory;
import org.geogebra.common.awt.GAffineTransform;
import org.geogebra.common.awt.GBasicStroke;
import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GGeneralPath;
import org.geogebra.common.awt.GShape;
import org.geogebra.ggbjdk.java.awt.geom.AffineTransform;
import org.geogebra.ggbjdk.java.awt.geom.Path2D;
import org.geogebra.ggbjdk.java.awt.geom.Shape;
import org.geogebra.web.awt.JLMContext2D;
import org.geogebra.web.awt.JLMContextHelper;
import org.geogebra.web.awt.Log;

import com.himamis.retex.renderer.share.platform.FactoryProvider;
import com.himamis.retex.renderer.share.platform.font.Font;
import com.himamis.retex.renderer.share.platform.font.FontLoader;
import com.himamis.retex.renderer.share.platform.font.FontRenderContext;
import com.himamis.retex.renderer.share.platform.graphics.Graphics2DInterface;
import com.himamis.retex.renderer.share.platform.graphics.Image;
import com.himamis.retex.renderer.web.DrawingFinishedCallback;
import com.himamis.retex.renderer.web.font.AsyncLoadedFont;
import com.himamis.retex.renderer.web.font.AsyncLoadedFont.FontLoadCallback;
import com.himamis.retex.renderer.web.font.DefaultFont;
import com.himamis.retex.renderer.web.font.FontW;
import com.himamis.retex.renderer.web.font.FontWrapper;

import elemental2.core.JsArray;
import elemental2.dom.CSSStyleDeclaration;
import elemental2.dom.CanvasRenderingContext2D;
import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLCanvasElement;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLImageElement;
import jsinterop.base.Js;

public class Graphics2DW implements Graphics2DInterface {

	JLMContext2D context;

	private GBasicStroke basicStroke;
	private GColor color;
	private FontW font;

	private DrawingFinishedCallback drawingFinishedCallback;
	private static final CSSStyleDeclaration FONT_PARSER = initFontParser();
	private double[] coords = new double[6];

	/**
	 * Creates a DIV for parsing CSS font definitions.
	 * @return font parsing DIV
	 */
	public static CSSStyleDeclaration initFontParser() {
		return ((HTMLElement) DomGlobal.document.createElement("div")).style;
	}

	/**
	 * @param context 2D context
	 */
	public Graphics2DW(CanvasRenderingContext2D context) {
		this.context = JLMContextHelper.as(context);
		this.context.initTransform();
		initBasicStroke();
		initColor();
		initFont();
	}

	public Graphics2DW(HTMLCanvasElement canvas) {
		this(Js.<CanvasRenderingContext2D>uncheckedCast(canvas.getContext("2d")));
	}

	private void initBasicStroke() {
		basicStroke = AwtFactory.getPrototype().newBasicStroke(context.getLineWidth(),
				StrokeUtil.getLineCap(context.getLineCap()),
				StrokeUtil.getLineJoin(context.getLineJoin()),
				context.getMiterLimit(), null);
	}

	private void initColor() {
		color = GColor.BLACK;
		context.setStrokeStyle(color.toString());
	}

	private void initFont() {
		FONT_PARSER.font = context.getFont();
		String fontFamily = FONT_PARSER.fontFamily;
		font = new DefaultFont(fontFamily, Font.PLAIN,
				(int) Math.round(FontLoader.PIXELS_PER_POINT));
	}

	public CanvasRenderingContext2D getContext() {
		return context;
	}

	@Override
	public void setStroke(GBasicStroke stroke) {
		basicStroke = stroke;
		context.setLineCap(StrokeUtil.getJSLineCap(basicStroke.getEndCap()));
		context.setLineJoin(StrokeUtil.getJSLineJoin(basicStroke.getLineJoin()));
		context.setLineWidth(basicStroke.getLineWidth());
		context.setMiterLimit(basicStroke.getMiterLimit());

		double[] dashArray = basicStroke.getDashArray();
		if (dashArray != null) {
			context.setLineDash(dashArray);
		} else {
			context.setLineDash(JsArray.of());
		}

	}

	@Override
	public GBasicStroke getStroke() {
		return basicStroke;
	}

	@Override
	public void setColor(GColor color) {
		this.color = color;
		context.setStrokeStyle(this.color.toString());
		context.setFillStyle(this.color.toString());
	}

	@Override
	public GColor getColor() {
		return color;
	}

	@Override
	public AffineTransform getTransform() {
		double[] mat = context.getTransformMatrix();
		if (mat.length == 6) {
			return new AffineTransform(mat[0], mat[1], mat[2], mat[3], mat[4], mat[5]);
		} else {
			return null;
		}
	}

	@Override
	public void saveTransform() {
		context.saveTransform();
	}

	@Override
	public void restoreTransform() {
		context.restoreTransform();

		// these values are also restored on context.restore()
		// so we have to re-set them
		setFont(font);
		setColor(color);
		setStroke(basicStroke);
	}

	@Override
	public FontW getFont() {
		return font;
	}

	@Override
	public void setFont(Font font) {
		this.font = (FontW) font;
		try {
			context.setFont(this.font.getCssFontString());
		} catch (Exception e) {
			FactoryProvider.debugS("Problem setting font");
		}
	}

	// Consider http://jsfiddle.net/9bMPD/357/ for rectangles!!

	@Override
	public void fillRect(double x, double y, double width, double height) {
		context.fillRect(x, y, width, height);
	}

	@Override
	public void fill(GShape gshape) {
		if (gshape == null) {
			Log.debug("Error in EuclidianView.draw");
			return;
		}
		Shape shape = (Shape) gshape;

		doDrawShape(shape, context, coords);

		/*
		 * App.debug((shape instanceof GeneralPath)+""); App.debug((shape
		 * instanceof GeneralPathClipped)+"");
		 * App.debug((shape.getClass().toString())+"");
		 */

		// default winding rule changed for ggb50 (for Polygons) #3983
		if (shape instanceof GGeneralPath) {
			GGeneralPath gp = (GGeneralPath) shape;
			int rule = gp.getWindingRule();
			if (rule == Path2D.WIND_EVEN_ODD) {
				context.fill("evenodd");
			} else {
				// context.fill("") differs between browsers
				context.fill();
			}
		} else {
			context.fill();
		}
	}

	@Override
	public void draw(GShape shape) {
		if (shape == null) {
			Log.debug("Error in draw");
			return;
		}
		doDrawShape((Shape) shape, context, coords);
		context.stroke();
	}

	@Override
	public void drawChars(char[] data, int offset, int length, int x, int y) {
		if (length > 1) {
			throw new UnsupportedOperationException(
					"Cannot draw multiple chars");
		}
		String string = String.valueOf(data, offset, length);
		drawText(string, x, y);
	}

	public void cancelCallbacks() {
		charDrawingRequests.forEach(FontLoadCallback::cancel);
	}

	private static class FontDrawContext {
		private Graphics2DW graphics;
		private String text;
		private int x;
		private int y;

		private AffineTransform transformCopy;
		private FontW font;
		private GColor color;

		public FontDrawContext(Graphics2DW graphics, String text, int x,
				int y) {
			this.graphics = graphics;
			this.text = text;
			this.x = x;
			this.y = y;

			transformCopy = graphics.getTransform();
			font = graphics.getFont();
			color = graphics.getColor();
		}

		public void doDraw() {
			FontW oldFont = graphics.getFont();
			GColor oldColor = graphics.getColor();

			graphics.saveTransform();
			graphics.setFont(font);
			graphics.setColor(color);
			// TRAC-5353
			// bad
			// graphics.transform(transformCopy);
			// good
			graphics.setTransform(transformCopy);
			graphics.fillTextInternal(text, x, y);
			graphics.restoreTransform();

			graphics.setFont(oldFont);
			graphics.setColor(oldColor);
		}

	}

	private ArrayList<FontLoadCallback> charDrawingRequests = new ArrayList<>();

	public void drawText(String text, int x, int y) {
		if (!font.isLoaded()) {
			final FontDrawContext fdc = new FontDrawContext(this, text, x, y);

			// Javascript callback is needed after the drawing has finished.
			// This would be straightforward in a synchronous library, but that
			// is not the case here.
			// This is the only asynchronous part in the JLaTeXMath API. (for
			// now)
			// A drawing request is created everytime a font is needed and is
			// not loaded.
			// The drawing request is executed after the font has been loaded.
			// Count all the drawing requests which will be processed after the
			// main drawing process has finished. After that in all the font
			// callbacks (error or loaded) check if there are any requests
			// waiting to be executed. If there aren't any, drawing has
			// finished.

			FontLoadCallback callback = new FontLoadCallback() {
				private boolean canceled;

				@Override
				public void onFontLoaded(AsyncLoadedFont font) {
					if (!canceled) {
						fdc.doDraw();
					}
					charDrawingRequests.remove(this);
					maybeNotifyDrawingFinishedCallback(true);
				}

				@Override
				public void onFontError(AsyncLoadedFont font) {
					FactoryProvider.debugS("Error loading font " + font);
					charDrawingRequests.remove(this);
					maybeNotifyDrawingFinishedCallback(true);
				}

				@Override
				public void cancel() {
					canceled = true;
				}
			};
			charDrawingRequests.add(callback);
			font.addFontLoadedCallback(callback);

		} else {
			fillTextInternal(text, x, y);
		}
	}

	public void setDrawingFinishedCallback(
			DrawingFinishedCallback drawingFinishedCallback) {
		this.drawingFinishedCallback = drawingFinishedCallback;
	}

	public void maybeNotifyDrawingFinishedCallback(boolean async) {
		if (!hasUnprocessedCharDrawingRequests()) {
			notifyDrawingFinishedCallback(async);
		}
	}

	private boolean hasUnprocessedCharDrawingRequests() {
		return !charDrawingRequests.isEmpty();
	}

	private void notifyDrawingFinishedCallback(boolean async) {
		if (drawingFinishedCallback != null) {
			drawingFinishedCallback.onDrawingFinished(async);
			drawingFinishedCallback = null;
		}
	}

	protected void fillTextInternal(String text, int x, int y) {
		FontWrapper fontWrapper = font.getFontWrapper();
		fontWrapper.drawGlyph(text, x, y, font.getSize(), context);
	}

	@Override
	public void drawArc(int x, int y, int width, int height, int startAngle,
			int arcAngle) {
		doArcPath(x, y, width, height, startAngle, arcAngle);
		context.stroke();
	}

	@Override
	public void fillArc(int x, int y, int width, int height, int startAngle,
			int arcAngle) {
		doArcPath(x, y, width, height, startAngle, arcAngle);
		context.fill();
	}

	private void doArcPath(int x, int y, int width, int height, int startAngle,
			int arcAngle) {
		context.saveTransform();
		context.beginPath();

		context.translate2(x, y);
		context.scale2(width / 2.0, height / 2.0);

		context.arc(1, 1, 1, startAngle, arcAngle);
		context.restoreTransform();
	}

	@Override
	public void translate(double x, double y) {
		context.translate2(x, y);
	}

	@Override
	public void scale(double x, double y) {
		context.scale2(x, y);
	}

	@Override
	public void rotate(double theta, double x, double y) {
		translate(x, y);
		rotate(theta);
		translate(-x, -y);
	}

	@Override
	public void rotate(double theta) {
		context.rotate2(theta);
	}

	@Override
	public void drawImage(Image image, final int x, final int y) {

		if (image instanceof ImageWImg) {
			ImageWImg impl = (ImageWImg) image;
			HTMLImageElement canvasElement = impl.getImage();
			context.drawImage(canvasElement, x, y);
		} else {
			ImageW impl = (ImageW) image;
			HTMLCanvasElement canvasElement = impl.getCanvas();
			context.drawImage(canvasElement, x, y);
		}
	}

	@Override
	public void drawImage(Image image, GAffineTransform transform) {
		context.saveTransform(transform.getScaleX(), transform.getShearY(),
				transform.getShearX(), transform.getScaleY(),
				transform.getTranslateX(), transform.getTranslateY());

		transform(transform);
		drawImage(image, 0, 0);

		context.restoreTransform();
	}

	/**
	 * Applies the transformation matrix to the context. Please ensure one call
	 * to graphics.save() before this method and graphics.restore() after this
	 * method.
	 * 
	 * @param transform
	 *            transformation matrix
	 */
	protected void transform(GAffineTransform transform) {
		context.transform(transform.getScaleX(), transform.getShearY(),
				transform.getShearX(), transform.getScaleY(),
				transform.getTranslateX(), transform.getTranslateY());
	}

	/**
	 * Sets the transformation matrix. Please ensure one call to graphics.save()
	 * before this method and graphics.restore() after this method.
	 * 
	 * @param transform
	 *            transformation matrix
	 */
	protected void setTransform(AffineTransform transform) {

		double dp = context.getDevicePixelRatio();

		context.setTransform2(transform.getScaleX() * dp,
				transform.getShearY() * dp, transform.getShearX() * dp,
				transform.getScaleY() * dp, transform.getTranslateX() * dp,
				transform.getTranslateY() * dp);
	}

	@Override
	public FontRenderContext getFontRenderContext() {
		return new FontRenderContextW(this);
	}

	@Override
	public void setRenderingHint(int key, int value) {
		// TODO Auto-generated method stub
	}

	@Override
	public int getRenderingHint(int key) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void dispose() {
		// NO-OP
	}

}
