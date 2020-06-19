/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.kernel.geos;

import org.geogebra.common.awt.GBasicStroke;
import org.geogebra.common.awt.GBufferedImage;
import org.geogebra.common.awt.GColor;
import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.awt.MyImage;
import org.geogebra.common.euclidian.EuclidianStatic;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.plugin.GeoClass;

/**
 * A GeoImage that can be used for drawing by AlgoElements.
 * 
 * @author G. Sturr
 * 
 */
public class GeoCanvasImage extends GeoImage {

	private MyImage bufferedImage = null;
	private GGraphics2D g;

	/**
	 * Creates new image
	 * 
	 * @param c
	 *            construction
	 */
	public GeoCanvasImage(Construction c) {
		this(c, 200, 100);
	}

	/**
	 * Creates new labeled image
	 * 
	 * @param c
	 *            construction
	 * @param label
	 *            label
	 */
	public GeoCanvasImage(Construction c, String label) {
		this(c);
		setLabel(label);
	}

	/**
	 * Create new image with indicated size
	 * 
	 * @param c
	 *            construction
	 * @param width
	 *            width of image
	 * @param height
	 *            height of image
	 */
	public GeoCanvasImage(Construction c, int width, int height) {
		super(c);

		pixelWidth = width;
		pixelHeight = height;

		this.setBackgroundColor(GColor.BLUE);
		GBasicStroke objStroke = EuclidianStatic.getDefaultStroke();
		createImage(objStroke, getAlgebraColor(), this.getBackgroundColor(),
				alphaValue, pixelWidth, pixelHeight);

	}

	@Override
	public GeoImage copy() {
		GeoCanvasImage copy = new GeoCanvasImage(cons);
		copy.set(this);
		return copy;
	}

	@Override
	protected boolean showInEuclidianView() {
		return isDefined();
	}

	/**
	 * @return fill image
	 */
	@Override
	public MyImage getFillImage() {
		return bufferedImage;
	}

	/**
	 * @return graphics
	 */
	public GGraphics2D getGraphics() {
		return g;
	}

	@Override
	public double getWidth() {
		return pixelWidth;
	}

	@Override
	public double getHeight() {
		return pixelHeight;
	}

	private GGraphics2D createImage(GBasicStroke objStroke, GColor color,
			GColor bgColor1, double backgroundTransparency, int xInt,
			int yInt) {

		bufferedImage = AwtFactory.getPrototype().newMyImage(xInt, yInt,
				GBufferedImage.TYPE_INT_ARGB);

		g = bufferedImage.createGraphics();

		// enable anti-aliasing
		// g.setAntialiasing();

		// enable transparency
		g.setTransparent();

		// paint background transparent
		if (bgColor1 == null) {
			g.setColor(GColor.newColor(255, 255, 255,
					(int) (backgroundTransparency * 255f)));
		} else {
			g.setColor(bgColor1);
		}

		g.fillRect(0, 0, xInt, yInt);
		g.setColor(color);

		g.setStroke(objStroke);

		return g;
	}

	@Override
	public GeoClass getGeoClassType() {
		return GeoClass.IMAGE;
	}

	@Override
	public boolean isGeoImage() {
		return true;
	}

	@Override
	public boolean isIndependent() {
		return true;
	}

	@Override
	public void getXML(boolean getListenersToo, StringBuilder sb) {
		/*
		 * Since objects of this class are always created by commands you must
		 * override the method to prevent the XML result duplicates the object
		 * definition.
		 */
	}

	/**
	 * returns all class-specific xml tags for getXML
	 */

	/*
	 * @Override protected void
	 * 
	 * // name of image file sb.append("\t<file name=\""); // Michael Borcherds
	 * 2007-12-10 this line restored (not needed now MD5 // code put in the
	 * correct place)
	 * sb.append(StringUtil.encodeXML(this.getGraphicsAdapter().getImageFileName
	 * ())); sb.append("\"/>\n");
	 * 
	 * // name of image file sb.append("\t<inBackground val=\"");
	 * sb.append(inBackground); sb.append("\"/>\n");
	 * 
	 * // image has to be interpolated if (!isInterpolate()) sb.append(
	 * "\t<interpolate val=\"false\"/>\n");
	 * 
	 * // locateion of image if (hasAbsoluteScreenLocation) {
	 * sb.append(getXMLabsScreenLoc()); } else { // store location of corners
	 * for (int i = 0; i < corners.length; i++) { if (corners[i] != null) {
	 * sb.append(getCornerPointXML(i)); } } }
	 * 
	 * getAuxiliaryXML(sb);
	 * 
	 * // sb.append(getXMLvisualTags()); // sb.append(getBreakpointXML());
	 * super.getXMLtags(sb);
	 * 
	 * }
	 */

}
