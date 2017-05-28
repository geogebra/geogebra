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
package com.himamis.retex.renderer.web.font;

import com.himamis.retex.renderer.share.platform.font.Font;
import com.himamis.retex.renderer.share.platform.font.FontRenderContext;
import com.himamis.retex.renderer.share.platform.font.TextLayout;
import com.himamis.retex.renderer.share.platform.geom.Rectangle2D;
import com.himamis.retex.renderer.share.platform.graphics.Graphics2DInterface;
import com.himamis.retex.renderer.web.geom.Rectangle2DW;
import com.himamis.retex.renderer.web.graphics.FontRenderContextW;
import com.himamis.retex.renderer.web.graphics.Graphics2DW;

public class TextLayoutW implements TextLayout {

	private String string;
	private FontW font;
	private FontRenderContextW fontRenderContext;

	public TextLayoutW(String string, Font font,
			FontRenderContext fontRenderContext) {
		this.string = string;
		this.font = (FontW) font;
		this.fontRenderContext = ((FontRenderContextW) fontRenderContext);
	}

	@Override
	public Rectangle2D getBounds() {
		// improve this part with opentype.js
		double width = fontRenderContext.measureTextWith(string, font);
		double height = font.getSize();
		// y=-height is not exact, but for most characters is y in the range
		// (-0.72*height,-1*height), so let's try
		return new Rectangle2DW(0, -height, width, height);
	}

	@Override
	public void draw(Graphics2DInterface graphics, int x, int y) {
		if (graphics instanceof Graphics2DW) {
			Graphics2DW g2d = (Graphics2DW) graphics;
			g2d.setFont(font);
			g2d.drawText(string, x, y);
		}
	}

}
