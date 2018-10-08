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

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

import com.himamis.retex.renderer.share.platform.graphics.BasicStroke;
import com.himamis.retex.renderer.share.platform.graphics.Color;
import com.himamis.retex.renderer.share.platform.graphics.GraphicsFactory;
import com.himamis.retex.renderer.share.platform.graphics.Image;
import com.himamis.retex.renderer.share.platform.graphics.Stroke;
import com.himamis.retex.renderer.share.platform.graphics.Transform;

public class GraphicsFactoryDesktop extends GraphicsFactory {

	@Override
	public BasicStroke createBasicStroke(double width, int cap, int join,
			double miterLimit) {
		return new BasicStrokeD(width, cap, join, miterLimit);
	}

	@Override
	public Color createColor(int red, int green, int blue) {
		return new ColorD(red, green, blue);
	}

	@Override
	public Image createImage(int width, int height, int type) {
		return new ImageD(width, height, type);
	}

	@Override
	public Transform createTransform() {
		return new TransformD();
	}

	@Override
	public Image createImage(String path) {

		BufferedImage bimage;

		if (path.startsWith("https://")) {
			try {
				URL url = new URL(path);
				bimage = ImageIO.read(url);
				return new ImageD(bimage);
			} catch (Exception e) {
				// MalformedURLException
				// IOException
				e.printStackTrace();
				return null;
			}

		}

		final File f = new File(path);
		try {
			bimage = ImageIO.read(f);
			return new ImageD(bimage);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

	}

	@Override
	public Stroke createBasicStroke(double width, float[] dashes) {
		return new BasicStrokeD(width, dashes);
	}

}
