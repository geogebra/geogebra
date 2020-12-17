package org.geogebra.desktop.gui.view.algebra;

import javax.swing.tree.DefaultMutableTreeNode;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.util.IndexHTMLBuilder;

public class GeoMutableTreeNode extends DefaultMutableTreeNode {
	private final GeoElement geo;
	private String algebraDescription;

	/**
	 * @param geo construction element
	 */
	public GeoMutableTreeNode(GeoElement geo) {
		super(geo);
		this.geo = geo;
	}

	/**
	 * @return algebra description (label [: or =] value)
	 */
	public String getAlgebraDescription() {
		if (algebraDescription == null) {
			algebraDescription = ((GeoElement) getUserObject()).getAlgebraDescriptionTextOrHTMLDefault(
					new IndexHTMLBuilder(true));
		}
		return algebraDescription;
	}

	public void reset() {
		algebraDescription = null;
	}

	public GeoElement getGeo() {
		return geo;
	}
}
