package org.geogebra.desktop.export;

import java.awt.Dimension;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

import org.geogebra.common.awt.GGraphics2D;
import org.geogebra.common.euclidian.DrawableList;
import org.geogebra.common.euclidian.DrawableList.Link;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.util.DoubleUtil;
import org.geogebra.common.util.StringUtil;

/**
 * Adds support for grouping objects in SVG files.
 * 
 * Also now adds support for exporting size in cm from <a href=
 * "https://help.geogebra.org/topic/incorrect-sizing-when-exporting-to-svg">
 * suggestion here</a> and returning null from getTransform() (ie Identity
 * matrix).
 * 
 * Needs this line changed in SVGGraphics2D.java (was private) protected
 * PrintWriter os;
 * 
 * @author Michael Borcherds
 */

public class SVGExtensions extends org.freehep.graphicsio.svg.SVGGraphics2D {

	private double cmWidth;
	private double cmHeight;

	protected String title;
	protected String desc;

	public SVGExtensions(OutputStream file, Dimension size, double cmWidth,
			double cmHeight) throws IOException {
		super(file, size);
		this.cmWidth = DoubleUtil.checkDecimalFraction(cmWidth);
		this.cmHeight = DoubleUtil.checkDecimalFraction(cmHeight);
	}

	public void startGroup(String s) {
		os.println("<g id=\"" + s + "\">");
	}

	public void endGroup(String s) {
		os.println("</g><!-- " + s + " -->");
	}

	@Override
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
			sb.append(StringUtil.encodeXML(title));
			sb.append("</title>");
		}

		if (desc != null) {
			sb.append("\n<desc>");
			sb.append(StringUtil.encodeXML(desc));
			sb.append("</desc>\n");
		}

	}

	/**
	 * 
	 * Adapted from DrawableList (to handle SVG &lt;title&gt; and &lt;desc&gt;)
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