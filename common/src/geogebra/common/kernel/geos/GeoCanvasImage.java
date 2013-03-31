/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package geogebra.common.kernel.geos;

import geogebra.common.awt.GBasicStroke;
import geogebra.common.awt.GBufferedImage;
import geogebra.common.awt.GColor;
import geogebra.common.awt.GGraphics2D;
import geogebra.common.euclidian.EuclidianStatic;
import geogebra.common.factories.AwtFactory;
import geogebra.common.kernel.Construction;
import geogebra.common.plugin.GeoClass;

/**
 * A GeoImage that can be used for drawing by AlgoElements.
 * 
 * @author G. Sturr
 * 
 */
public class GeoCanvasImage extends GeoImage {

	private GBufferedImage bufferedImage = null;
	protected GBasicStroke objStroke = EuclidianStatic.getDefaultStroke();
	private GGraphics2D g;

	/**
	 * Creates new image
	 * 
	 * @param c
	 *            construction
	 */
	public GeoCanvasImage(Construction c) {
		super(c);

		pixelWidth = 200;
		pixelHeight = 100;

		this.setBackgroundColor(GColor.blue);
		createImage(objStroke, getAlgebraColor(), this.getBackgroundColor(),
				alphaValue, pixelWidth, pixelHeight);

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
	 * Copy constructor
	 * 
	 * @param img
	 *            source image
	 */
	public GeoCanvasImage(GeoCanvasImage img) {
		this(img.cons);
		set(img);
	}

	@Override
	public GeoElement copy() {
		return new GeoCanvasImage(this);
	}

	@Override
	protected boolean showInEuclidianView() {
		return isDefined();
	}

	/**
	 * @return fill image
	 */
	@Override
	public GBufferedImage getFillImage() {
		return bufferedImage;
	}

	public GGraphics2D getGraphics() {
		return g;
	}

	public int getWidth() {
		return pixelWidth;
	}

	public int getHeight() {
		return pixelHeight;
	}

	private GGraphics2D createImage(GBasicStroke objStroke, GColor color,
			GColor bgColor, float backgroundTransparency, int xInt, int yInt) {

		bufferedImage = AwtFactory.prototype.newBufferedImage(xInt, yInt,
				GBufferedImage.TYPE_INT_ARGB);

		g = bufferedImage.createGraphics();

		// enable anti-aliasing
		// g.setAntialiasing();

		// enable transparency
		g.setTransparent();

		// paint background transparent
		if (bgColor == null) {
			g.setColor(AwtFactory.prototype.newColor(255, 255, 255,
					(int) (backgroundTransparency * 255f)));
		} else {
			g.setColor(bgColor);
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

	public void setDefined(boolean b) {
		// TODO Auto-generated method stub

	}

	/**
	 * returns all class-specific xml tags for getXML
	 */

	/*
	 * @Override protected void getXMLtags(StringBuilder sb) {
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
	 * // image has to be interpolated if (!isInterpolate())
	 * sb.append("\t<interpolate val=\"false\"/>\n");
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
