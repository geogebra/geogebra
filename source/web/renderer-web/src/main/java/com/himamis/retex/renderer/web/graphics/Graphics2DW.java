/**
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

import java.util.ArrayList;

import com.himamis.retex.renderer.share.platform.FactoryProvider;
import com.himamis.retex.renderer.share.platform.font.Font;
import com.himamis.retex.renderer.share.platform.font.FontLoader;
import com.himamis.retex.renderer.share.platform.font.FontRenderContext;
import com.himamis.retex.renderer.share.platform.geom.Line2D;
import com.himamis.retex.renderer.share.platform.geom.Rectangle2D;
import com.himamis.retex.renderer.share.platform.geom.RoundRectangle2D;
import com.himamis.retex.renderer.share.platform.graphics.Color;
import com.himamis.retex.renderer.share.platform.graphics.Graphics2DInterface;
import com.himamis.retex.renderer.share.platform.graphics.Image;
import com.himamis.retex.renderer.share.platform.graphics.Stroke;
import com.himamis.retex.renderer.share.platform.graphics.Transform;
import com.himamis.retex.renderer.share.platform.graphics.stubs.AffineTransform;
import com.himamis.retex.renderer.web.DrawingFinishedCallback;
import com.himamis.retex.renderer.web.font.AsyncLoadedFont;
import com.himamis.retex.renderer.web.font.AsyncLoadedFont.FontLoadCallback;
import com.himamis.retex.renderer.web.font.DefaultFont;
import com.himamis.retex.renderer.web.font.FontW;
import com.himamis.retex.renderer.web.font.FontWrapper;
import com.himamis.retex.renderer.web.geom.AreaW;
import com.himamis.retex.renderer.web.geom.RoundRectangle2DW;
import com.himamis.retex.renderer.web.geom.ShapeW;

import elemental2.core.JsArray;
import elemental2.dom.CSSStyleDeclaration;
import elemental2.dom.CanvasRenderingContext2D;
import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLCanvasElement;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLImageElement;
import jsinterop.base.Js;

public class Graphics2DW implements Graphics2DInterface {

	JLMContext2d context;

	private BasicStrokeW basicStroke;
	private ColorW color;
	private FontW font;

	private DrawingFinishedCallback drawingFinishedCallback;
	private static final CSSStyleDeclaration FONT_PARSER = initFontParser();

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
		basicStroke = new BasicStrokeW(context.getLineWidth(),
				context.getLineCap(), context.getLineJoin(),
				context.getMiterLimit());
	}

	private void initColor() {
		color = new ColorW(0, 0, 0);
		context.setStrokeStyle(color.getCssColor());
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
	public void setStroke(Stroke stroke) {
		basicStroke = (BasicStrokeW) stroke;
		context.setLineCap(basicStroke.getJSLineCap());
		context.setLineJoin(basicStroke.getJSLineJoin());
		context.setLineWidth(basicStroke.getWidth());
		context.setMiterLimit(basicStroke.getMiterLimit());

		double[] dasharr = basicStroke.getDash();
		if (dasharr != null) {
			context.setLineDash(dasharr);
		} else {
			context.setLineDash(JsArray.of());
		}

	}

	@Override
	public Stroke getStroke() {
		return basicStroke;
	}

	@Override
	public void setColor(Color color) {
		this.color = (ColorW) color;
		context.setStrokeStyle(this.color.getCssColor());
		context.setFillStyle(this.color.getCssColor());
	}

	@Override
	public ColorW getColor() {
		return color;
	}

	@Override
	public AffineTransform getTransform() {
		return context.getAffineTransform();
	}

	@Override
	public void saveTransformation() {
		context.saveTransform();
	}

	@Override
	public void restoreTransformation() {
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
	public void fill(com.himamis.retex.renderer.share.platform.geom.Shape shape) {
		if (shape instanceof Rectangle2D) {
			Rectangle2D rect = (Rectangle2D) shape;
			fillRect(rect.getX(), rect.getY(), rect.getWidth(),
					rect.getHeight());
		} else if (shape instanceof AreaW) {
			AreaW area = (AreaW) shape;
			area.fill(this, context);
		} else if (shape instanceof ShapeW) {
			((ShapeW) shape).fill(context);
		} else if (shape instanceof RoundRectangle2DW) {
			drawRoundRectangle((RoundRectangle2DW) shape);
			context.fill();
		}
	}

	@Override
	public void startDrawing() {
		context.beginPath();
	}

	@Override
	public void moveTo(double x, double y) {
		context.moveTo(x, y);
	}

	@Override
	public void lineTo(double x, double y) {
		context.lineTo(x, y);
	}

	@Override
	public void quadraticCurveTo(double x, double y, double x1, double y1) {
		context.quadraticCurveTo(x, y, x1, y1);
	}

	@Override
	public void bezierCurveTo(double x, double y, double x1, double y1,
			double x2, double y2) {
		context.bezierCurveTo(x, y, x1, y1, x2, y2);
	}

	@Override
	public void finishDrawing() {
		context.closePath();
		context.fill();
	}

	@Override
	public void draw(Rectangle2D rectangle) {
		context.strokeRect(rectangle.getX(), rectangle.getY(),
				rectangle.getWidth(), rectangle.getHeight());
	}

	@Override
	public void draw(RoundRectangle2D rectangle) {
		drawRoundRectangle(rectangle);
		context.stroke();
	}

	private void drawRoundRectangle(RoundRectangle2D rectangle) {
		double x = rectangle.getX();
		double y = rectangle.getY();
		double width = rectangle.getWidth();
		double height = rectangle.getHeight();
		double arcW = rectangle.getArcW();
		double arcH = rectangle.getArcH();
		if (Math.abs(arcW - arcH) < 0.01) {
			double radius = arcW / 2.0;
			context.beginPath();
			context.moveTo(x + radius, y);
			context.lineTo(x + width - radius, y);
			context.quadraticCurveTo(x + width, y, x + width, y + radius);
			context.lineTo(x + width, y + height - radius);
			context.quadraticCurveTo(x + width, y + height, x + width - radius,
					y + height);
			context.lineTo(x + radius, y + height);
			context.quadraticCurveTo(x, y + height, x, y + height - radius);
			context.lineTo(x, y + radius);
			context.quadraticCurveTo(x, y, x + radius, y);
			context.closePath();
		} else {
			throw new UnsupportedOperationException(
					"ArcW and ArcH must be equal.");
		}
	}

	@Override
	public void draw(Line2D line) {
		context.beginPath();
		context.moveTo(line.getX1(), line.getY1());
		context.lineTo(line.getX2(), line.getY2());
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
		charDrawingRequests.forEach(f -> f.cancel());
	}

	private static class FontDrawContext {
		private Graphics2DW graphics;
		private String text;
		private int x;
		private int y;

		private AffineTransform transformCopy;
		private FontW font;
		private ColorW color;

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
			ColorW oldColor = graphics.getColor();

			graphics.saveTransformation();
			graphics.setFont(font);
			graphics.setColor(color);
			// TRAC-5353
			// bad
			// graphics.transform(transformCopy);
			// good
			graphics.setTransform(transformCopy);
			graphics.fillTextInternal(text, x, y);
			graphics.restoreTransformation();

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
	public void drawImage(Image image, Transform transform) {
		context.saveTransform(transform.getScaleX(), transform.getShearY(),
				transform.getShearX(), transform.getScaleY(),
				transform.getTranslateX(), transform.getTranslateY());

		transform((AffineTransform) transform);
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
	protected void transform(AffineTransform transform) {
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
