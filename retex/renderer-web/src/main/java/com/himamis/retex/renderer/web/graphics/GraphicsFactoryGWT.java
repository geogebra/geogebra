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

import com.himamis.retex.renderer.share.platform.graphics.BasicStroke;
import com.himamis.retex.renderer.share.platform.graphics.Color;
import com.himamis.retex.renderer.share.platform.graphics.GraphicsFactory;
import com.himamis.retex.renderer.share.platform.graphics.Image;
import com.himamis.retex.renderer.share.platform.graphics.Stroke;
import com.himamis.retex.renderer.share.platform.graphics.Transform;
import com.himamis.retex.renderer.share.platform.graphics.stubs.AffineTransform;

import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLCanvasElement;
import elemental2.dom.HTMLImageElement;
import jsinterop.base.Js;

public class GraphicsFactoryGWT extends GraphicsFactory {

	@Override
	public BasicStroke createBasicStroke(double width, int cap, int join,
			double miterLimit) {
		return new BasicStrokeW(width, cap, join, miterLimit);
	}

	@Override
	public Color createColor(int red, int green, int blue, int alpha) {
		return new ColorW(red, green, blue, alpha);
	}

	@Override
	public Image createImage(int width, int height, int type) {
		HTMLCanvasElement canvas = Js.uncheckedCast(DomGlobal.document.createElement("canvas"));
		return new ImageW(canvas, width, height, type);
	}

	public Image createImage(String base64, int width, int height) {
		HTMLImageElement img = (HTMLImageElement) DomGlobal.document.createElement("img");
		img.src = base64;
		return new ImageWImg(img, width, height);
	}

	@Override
	public Transform createTransform() {
		return new AffineTransform();
	}

	@Override
	public Stroke createBasicStroke(double width, float[] dashes) {
		return new BasicStrokeW(width, dashes);
	}

}
