/*
 * @(#)PaintUtils.java
 *
 * $Date: 2010-01-03 07:20:54 -0600 (Sun, 03 Jan 2010) $
 *
 * Copyright (c) 2009 by Jeremy Wood.
 * All rights reserved.
 *
 * The copyright of this software is owned by Jeremy Wood. 
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * Jeremy Wood. For details see accompanying license terms.
 * 
 * This software is probably, but not necessarily, discussed here:
 * http://javagraphics.blogspot.com/
 * 
 * And the latest version should be available here:
 * https://javagraphics.dev.java.net/
 * 
 * ---
 * 
 * Modifications by Florian Sonner for the usage in GeoGebra:
 *  - stripped out unused methods
 *  - uses just one bevel color now
 */
package com.bric.plaf;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.TexturePaint;
import java.awt.image.BufferedImage;
import java.util.Hashtable;

import javax.swing.SwingConstants;
import javax.swing.UIManager;

import com.bric.util.JVM;

/** Some static methods for some common painting functions.
 *
 * @author Jeremy Wood
 **/
public class PlafPaintUtils {

	/** Four shades of white, each with increasing opacity. */
	final static Color[] whites = new Color[] {
			new Color(255,255,255,50)
	};
	
	/** Four shades of black, each with increasing opacity. */
	final static Color[] blacks = new Color[] {
			new Color(0,0,0,50)
	};
	
	/** @return the color used to indicate when a component has
	 * focus.  By default this uses the color (64,113,167), but you can
	 * override this by calling:
	 * <BR><code>UIManager.put("focusRing",customColor);</code>
	 */
	public static Color getFocusRingColor() {
		Object obj = UIManager.getColor("Focus.color");
		if(obj instanceof Color)
			return (Color)obj;
		obj = UIManager.getColor("focusRing");
		if(obj instanceof Color)
			return (Color)obj;
		return new Color(64,113,167);
	}
	
	/** Paints 3 different strokes around a shape to indicate focus.
	 * The widest stroke is the most transparent, so this achieves a nice
	 * "glow" effect.
	 * <P>The catch is that you have to render this underneath the shape,
	 * and the shape should be filled completely.
	 * 
	 * @param g the graphics to paint to
	 * @param shape the shape to outline
	 * @param pixelSize the number of pixels the outline should cover.
	 */
	public static void paintFocus(Graphics2D g,Shape shape,int pixelSize) {
		Color focusColor = getFocusRingColor();
		Color[] focusArray = new Color[] {
			new Color(focusColor.getRed(), focusColor.getGreen(), focusColor.getBlue(),235),
			new Color(focusColor.getRed(), focusColor.getGreen(), focusColor.getBlue(),130),
			new Color(focusColor.getRed(), focusColor.getGreen(), focusColor.getBlue(),80)	
		};
		if(JVM.usingQuartz) {
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
		} else {
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
		}
		
		g.setStroke(new BasicStroke(2*pixelSize+1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
		g.setColor(focusArray[2]);
		g.draw(shape);
		if(2*pixelSize+1>0) {
			g.setStroke(new BasicStroke(2*pixelSize-2+1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
			g.setColor(focusArray[1]);
			g.draw(shape);
		}
		if(2*pixelSize-4+1>0) {
			g.setStroke(new BasicStroke(2*pixelSize-4+1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
			g.setColor(focusArray[0]);
			g.draw(shape);
		}
	}
	
	/** Uses translucent shades of white and black to draw highlights
	 * and shadows around a rectangle, and then frames the rectangle
	 * with a shade of gray (120).
	 * <P>This should be called to add a finishing touch on top of
	 * existing graphics.
	 * @param g the graphics to paint to.
	 * @param r the rectangle to paint.
	 */
	public static void drawBevel(Graphics2D g,Rectangle r) {
		g.setStroke(new BasicStroke(1));
		drawColors(blacks,g, r.x, r.y+r.height, r.x+r.width, r.y+r.height, SwingConstants.SOUTH);
		drawColors(blacks,g, r.x+r.width, r.y, r.x+r.width, r.y+r.height, SwingConstants.EAST);

		drawColors(whites,g, r.x, r.y, r.x+r.width, r.y, SwingConstants.NORTH);
		drawColors(whites,g, r.x, r.y, r.x, r.y+r.height, SwingConstants.WEST);
		
		g.setColor(new Color(120, 120, 120));
		g.drawRect(r.x, r.y, r.width, r.height);
	}
	
	private static void drawColors(Color[] colors,Graphics g,int x1,int y1,int x2,int y2,int direction) {
		for(int a = 0; a<colors.length; a++) {
			g.setColor(colors[colors.length-a-1]);
			if(direction==SwingConstants.SOUTH) {
				g.drawLine(x1, y1-a, x2, y2-a);
			} else if(direction==SwingConstants.NORTH) {
				g.drawLine(x1, y1+a, x2, y2+a);
			} else if(direction==SwingConstants.EAST) {
				g.drawLine(x1-a, y1, x2-a, y2);
			} else if(direction==SwingConstants.WEST) {
				g.drawLine(x1+a, y1, x2+a, y2);
			}
		}
	}
}
