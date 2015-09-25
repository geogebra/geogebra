package org.geogebra.desktop.export;

import java.awt.Dimension;
import java.awt.geom.AffineTransform;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.euclidian.DrawableList;
import org.geogebra.common.euclidian.DrawableList.Link;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoText;

/**
 * adds support for grouping objects in SVG files
 * 
 * also now adds support for exporting size in cm from suggestion here
 * http://forum.geogebra.org/viewtopic.php?f=8&t=38068
 * 
 * and returning null from getTransform() (ie Identity matrix)
 * 
 * needs this line changed in SVGGraphics2D.java (was private) protected
 * PrintWriter os;
 * 
 * @author Michael Borcherds
 */

public class SVGExtensions extends org.freehep.graphicsio.svg.SVGGraphics2D {

	private double cmWidth;
	private double cmHeight;

	private AffineTransform identity = new AffineTransform();
	protected String title;
	protected String desc;

	public SVGExtensions(File file, Dimension size, double cmWidth,
			double cmHeight) throws IOException {
		super(file, size);
		this.cmWidth = Kernel.checkDecimalFraction(cmWidth);
		this.cmHeight = Kernel.checkDecimalFraction(cmHeight);
	}

	public void startGroup(String s) {
		os.println("<g id=\"" + s + "\">");
	}

	public void endGroup(String s) {
		os.println("</g><!-- " + s + " -->");
	}

	protected void writeSize(PrintWriter os) {

		if (cmWidth > 0 && cmHeight > 0) {
			// cm
			os.println("     width=\"" + cmWidth + "cm\"");
			os.println("     height=\"" + cmHeight + "cm\"");

		} else {
			super.writeSize(os);

		}

	}

	public void setElementTitle(String title) {
		this.title = title;

	}

	public String getElementTitle(String title) {
		return this.title;

	}

	public void setElementDesc(String desc) {
		this.desc = desc;
	}

	public String getElementDesc(String desc) {
		return this.desc;
	}

	@Override
	protected void appendElementTitleAndDescription(StringBuffer sb) {

		if (title != null) {
			sb.append("\n<title>");
			sb.append(title);
			sb.append("</title>");
		}

		if (desc != null) {
			sb.append("\n<desc>");
			sb.append(desc);
			sb.append("</desc>\n");
		}

	}

	/**
	 * 
	 * Adapted from DrawableList (to handle SVG <title> and <desc>)
	 * 
	 * @param list
	 * @param g2
	 */
	public final void drawAll(DrawableList list, GGraphics2D g2) {
		Link cur = list.head;
		while (cur != null) {
			GeoElement geo = cur.d.getGeoElement();
			// defined check needed in case the GeoList changed its size
			if (geo.isDefined()) {
				if (cur.d.needsUpdate()) {
					cur.d.setNeedsUpdate(false);
					cur.d.update();
				}

				if (geo.isGeoText()) {
					setElementTitle(((GeoText) geo).getTextString());
				} else {
					setElementTitle(geo.getNameDescription());
				}

				if (geo.isIndependent()) {
					// eg a:y = 4x + 3
					// eg A =(3, 4)
					setElementDesc(geo.getAlgebraDescriptionDefault());
				} else {
					setElementDesc(geo.getLongDescription());
				}

				cur.d.draw(g2);
			}
			cur = cur.next;
		}
	}
}