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
package com.himamis.retex.renderer.desktop.graphics;

import java.awt.Graphics2D;
import java.awt.RenderingHints.Key;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.LinkedList;

import javax.imageio.ImageIO;

import com.himamis.retex.renderer.desktop.font.FontD;
import com.himamis.retex.renderer.desktop.font.FontRenderContextD;
import com.himamis.retex.renderer.share.platform.FactoryProvider;
import com.himamis.retex.renderer.share.platform.font.Font;
import com.himamis.retex.renderer.share.platform.font.FontRenderContext;
import com.himamis.retex.renderer.share.platform.geom.Line2D;
import com.himamis.retex.renderer.share.platform.geom.Rectangle2D;
import com.himamis.retex.renderer.share.platform.geom.RoundRectangle2D;
import com.himamis.retex.renderer.share.platform.graphics.Color;
import com.himamis.retex.renderer.share.platform.graphics.Graphics2DInterface;
import com.himamis.retex.renderer.share.platform.graphics.Image;
import com.himamis.retex.renderer.share.platform.graphics.ImageBase64;
import com.himamis.retex.renderer.share.platform.graphics.RenderingHints;
import com.himamis.retex.renderer.share.platform.graphics.Stroke;
import com.himamis.retex.renderer.share.platform.graphics.Transform;

public class Graphics2DD implements Graphics2DInterface {

	private Graphics2D impl;
	private LinkedList<TransformD> transformationStack = new LinkedList<>();
	private GeneralPath path;

	public Graphics2DD(Graphics2D impl) {
		this.impl = impl;
	}

	public Graphics2D getImpl() {
		return impl;
	}

	@Override
	public void setStroke(Stroke stroke) {
		impl.setStroke((java.awt.Stroke) stroke);
	}

	@Override
	public Stroke getStroke() {
		return new StrokeD(impl.getStroke());
	}

	@Override
	public void setColor(Color color) {
		impl.setColor((java.awt.Color) color);
	}

	@Override
	public Color getColor() {
		return new ColorD(impl.getColor());
	}

	@Override
	public Transform getTransform() {
		return new TransformD(impl.getTransform());
	}

	@Override
	public Font getFont() {
		return new FontD(impl.getFont());
	}

	@Override
	public void setFont(Font font) {
		impl.setFont(((FontD) font).impl);
	}

	@Override
	public void fillRect(int x, int y, int width, int height) {
		impl.fillRect(x, y, width, height);
	}

	@Override
	public void fill(com.himamis.retex.renderer.share.platform.geom.Shape s) {
		impl.fill((Shape) s);
	}

	@Override
	public void startDrawing() {
		path = new GeneralPath();
	}

	@Override
	public void moveTo(double x, double y) {
		path.moveTo(x, y);
	}

	@Override
	public void lineTo(double x, double y) {
		path.lineTo(x, y);
	}

	@Override
	public void quadraticCurveTo(double x, double y, double x1, double y1) {
		path.quadTo(x, y, x1, y1);
	}

	@Override
	public void bezierCurveTo(double x, double y, double x1, double y1,
			double x2, double y2) {
		path.curveTo(x, y, x1, y1, x2, y2);
	}

	@Override
	public void finishDrawing() {
		impl.fill(path);
	}

	@Override
	public void draw(Rectangle2D rectangle) {
		impl.draw((Shape) rectangle);
	}

	@Override
	public void draw(RoundRectangle2D rectangle) {
		impl.draw((Shape) rectangle);
	}

	@Override
	public void draw(Line2D line) {
		impl.draw((Shape) line);
	}

	@Override
	public void drawChars(char[] data, int offset, int length, int x, int y) {
		impl.drawChars(data, offset, length, x, y);

	}

	@Override
	public void drawArc(int x, int y, int width, int height, int startAngle,
			int arcAngle) {
		impl.drawArc(x, y, width, height, startAngle, arcAngle);
	}

	@Override
	public void fillArc(int x, int y, int width, int height, int startAngle,
			int arcAngle) {
		impl.fillArc(x, y, width, height, startAngle, arcAngle);
	}

	@Override
	public void translate(double x, double y) {
		impl.translate(x, y);
	}

	@Override
	public void scale(double x, double y) {
		impl.scale(x, y);
	}

	@Override
	public void rotate(double theta, double x, double y) {
		impl.rotate(theta, x, y);
	}

	@Override
	public void rotate(double theta) {
		impl.rotate(theta);
	}

	@Override
	public void drawImage(Image image, int x, int y) {

		if (image instanceof ImageBase64) {
			impl.drawImage(base64ToBufferedImage((ImageBase64) image), x, y,
					null);
		} else {
			impl.drawImage((java.awt.Image) image, x, y, null);
		}

	}

	@Override
	public void drawImage(Image image, Transform transform) {
		if (image instanceof ImageBase64) {
			impl.drawImage(base64ToBufferedImage((ImageBase64) image),
					(AffineTransform) transform, null);
		} else {
			impl.drawImage((java.awt.Image) image, (AffineTransform) transform,
					null);
		}
	}

	private static BufferedImage base64ToBufferedImage(ImageBase64 image) {
		String pngBase64 = image.getBase64();

		final String pngMarker = "data:image/png;base64,";

		if (pngBase64.startsWith(pngMarker)) {
			pngBase64 = pngBase64.substring(pngMarker.length());
		} else {
			FactoryProvider.debugS("invalid base64 image");
			return null;
		}

		byte[] imageData = Base64.decode(pngBase64);

		try {
			return ImageIO.read(new ByteArrayInputStream(imageData));
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public FontRenderContext getFontRenderContext() {
		return new FontRenderContextD(impl.getFontRenderContext());
	}

	@Override
	public void dispose() {
		impl.dispose();
	}

	@Override
	public void setRenderingHint(int key, int value) {
		impl.setRenderingHint(getNativeRenderingKey(key),
				getNativeRenderingValue(value));
	}

	@Override
	public int getRenderingHint(int key) {
		Key nKey = getNativeRenderingKey(key);
		Object val = impl.getRenderingHint(nKey);
		return getRenderingValue(val);
	}

	private static Key getNativeRenderingKey(int key) {
		switch (key) {
		case RenderingHints.KEY_ANTIALIASING:
			return java.awt.RenderingHints.KEY_ANTIALIASING;
		case RenderingHints.KEY_RENDERING:
			return java.awt.RenderingHints.KEY_RENDERING;
		case RenderingHints.KEY_TEXT_ANTIALIASING:
			return java.awt.RenderingHints.KEY_TEXT_ANTIALIASING;
		default:
			return null;
		}
	}

	private static Object getNativeRenderingValue(int value) {
		switch (value) {
		case RenderingHints.VALUE_ANTIALIAS_ON:
			return java.awt.RenderingHints.VALUE_ANTIALIAS_ON;
		case RenderingHints.VALUE_RENDER_QUALITY:
			return java.awt.RenderingHints.VALUE_RENDER_QUALITY;
		case RenderingHints.VALUE_TEXT_ANTIALIAS_ON:
			return java.awt.RenderingHints.VALUE_TEXT_ANTIALIAS_ON;
		default:
			return null;
		}
	}

	private static int getRenderingValue(Object value) {
		if (value == java.awt.RenderingHints.VALUE_INTERPOLATION_BICUBIC) {
			return RenderingHints.VALUE_INTERPOLATION_BICUBIC;
		} else if (value == java.awt.RenderingHints.VALUE_INTERPOLATION_BILINEAR) {
			return RenderingHints.VALUE_INTERPOLATION_BILINEAR;
		} else if (value == java.awt.RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR) {
			return RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR;
		} else {
			return -1;
		}
	}

	@Override
	public void saveTransformation() {
		transformationStack.add(new TransformD(impl.getTransform()));
	}

	@Override
	public void restoreTransformation() {
		TransformD last = transformationStack.removeLast();
		impl.setTransform(last);
	}
}
