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
package com.himamis.retex.renderer.share.platform.graphics;

import com.himamis.retex.renderer.share.platform.font.Font;
import com.himamis.retex.renderer.share.platform.font.FontRenderContext;
import com.himamis.retex.renderer.share.platform.geom.Line2D;
import com.himamis.retex.renderer.share.platform.geom.Rectangle2D;
import com.himamis.retex.renderer.share.platform.geom.RoundRectangle2D;
import com.himamis.retex.renderer.share.platform.geom.Shape;

public interface Graphics2DInterface {

	public void setStroke(Stroke stroke);

	public Stroke getStroke();

	public void setColor(Color color);

	public Color getColor();

	public Transform getTransform();

	public void saveTransformation();

	public void restoreTransformation();

	public Font getFont();

	public void setFont(Font font);

	public void fillRect(int x, int y, int width, int height);

	public void fill(Shape rectangle);

	void startDrawing();

	void moveTo(double x, double y);

	void lineTo(double x, double y);

	void quadraticCurveTo(double x, double y, double x1, double y1);

	void bezierCurveTo(double x, double y, double x1, double y1, double x2,
			double y2);

	void finishDrawing();

	public void draw(Rectangle2D rectangle);

	public void draw(RoundRectangle2D rectangle);

	public void draw(Line2D line);

	public void drawChars(char[] data, int offset, int length, int x, int y);

	public void drawArc(int x, int y, int width, int height, int startAngle,
			int arcAngle);

	public void fillArc(int x, int y, int width, int height, int startAngle,
			int arcAngle);

	public void translate(double x, double y);

	public void scale(double x, double y);

	public void rotate(double theta, double x, double y);

	public void rotate(double theta);

	public void drawImage(Image image, int x, int y);

	public void drawImage(Image image, Transform transform);

	public FontRenderContext getFontRenderContext();

	public void setRenderingHint(int key, int value);

	public int getRenderingHint(int key);

	public void dispose();
}
