package org.geogebra.desktop.gui.util;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import org.geogebra.common.awt.GColor;

public interface SVGModel {
	int getWidth();

	int getHeight();

	void setFill(GColor color);

	void paint(Graphics2D g);

	boolean isInvalid();

	void setTransform(AffineTransform transform);

	String getContent();
}
