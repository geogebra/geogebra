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
import java.util.LinkedList;

import com.himamis.retex.renderer.desktop.font.FontD;
import com.himamis.retex.renderer.desktop.font.FontRenderContextD;
import com.himamis.retex.renderer.share.platform.font.Font;
import com.himamis.retex.renderer.share.platform.font.FontRenderContext;
import com.himamis.retex.renderer.share.platform.geom.Line2D;
import com.himamis.retex.renderer.share.platform.geom.Rectangle2D;
import com.himamis.retex.renderer.share.platform.geom.RoundRectangle2D;
import com.himamis.retex.renderer.share.platform.graphics.Color;
import com.himamis.retex.renderer.share.platform.graphics.Graphics2DInterface;
import com.himamis.retex.renderer.share.platform.graphics.Image;
import com.himamis.retex.renderer.share.platform.graphics.RenderingHints;
import com.himamis.retex.renderer.share.platform.graphics.Stroke;
import com.himamis.retex.renderer.share.platform.graphics.Transform;

public class Graphics2DD implements Graphics2DInterface {

	private Graphics2D impl;
	private LinkedList<TransformD> transformationStack = new LinkedList<TransformD>();

	public Graphics2DD(Graphics2D impl) {
		this.impl = impl;
	}

	public Graphics2D getImpl() {
		return impl;
	}

	public void setStroke(Stroke stroke) {
		impl.setStroke((java.awt.Stroke) stroke);
	}

	public Stroke getStroke() {
		return new StrokeD(impl.getStroke());
	}

	public void setColor(Color color) {
		impl.setColor((java.awt.Color) color);
	}

	public Color getColor() {
		return new ColorD(impl.getColor());
	}

	public Transform getTransform() {
		return new TransformD(impl.getTransform());
	}

	public Font getFont() {
		return new FontD(impl.getFont());
	}

	public void setFont(Font font) {
		impl.setFont(((FontD) font).impl);
	}

	public void fillRect(int x, int y, int width, int height) {
		impl.fillRect(x, y, width, height);
	}

	public void fill(Rectangle2D rectangle) {
		impl.fill((Shape) rectangle);
	}

	public void draw(Rectangle2D rectangle) {
		impl.draw((Shape) rectangle);
	}

	public void draw(RoundRectangle2D rectangle) {
		impl.draw((Shape) rectangle);
	}

	public void draw(Line2D line) {
		impl.draw((Shape) line);
	}

	public void drawChars(char[] data, int offset, int length, int x, int y) {
		impl.drawChars(data, offset, length, x, y);

	}

	public void drawArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
		impl.drawArc(x, y, width, height, startAngle, arcAngle);
	}

	public void fillArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
		impl.fillArc(x, y, width, height, startAngle, arcAngle);
	}

	public void translate(double x, double y) {
		impl.translate(x, y);
	}

	public void scale(double x, double y) {
		impl.scale(x, y);
	}

	public void rotate(double theta, double x, double y) {
		impl.rotate(theta, x, y);
	}

	public void rotate(double theta) {
		impl.rotate(theta);
	}

	public void drawImage(Image image, int x, int y) {
		impl.drawImage((java.awt.Image) image, x, y, null);
	}

	public void drawImage(Image image, Transform transform) {
		impl.drawImage((java.awt.Image) image, (AffineTransform) transform, null);
	}

	public FontRenderContext getFontRenderContext() {
		return new FontRenderContextD(impl.getFontRenderContext());
	}

	public void dispose() {
		impl.dispose();
	}

	public void setRenderingHint(int key, int value) {
		impl.setRenderingHint(getNativeRenderingKey(key), getNativeRenderingValue(value));
	}

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


	public void saveTransformation() {
		transformationStack.add(new TransformD(impl.getTransform()));
	}

	public void restoreTransformation() {
		TransformD last = transformationStack.removeLast();
		impl.setTransform(last);
	}
}
