package geogebra.touch.gui.elements;

import org.vectomatic.dom.svg.ui.SVGResource;

public class StandardImageButton extends FastButton {
	private SVGResource icon;

	public StandardImageButton(SVGResource icon) {
		this.setIcon(icon);
	}

	public void setIcon(SVGResource icon) {
		this.icon = icon;
		final String html = "<img src=\"" + this.icon.getSafeUri().asString()
				+ "\" />";
		this.getElement().setInnerHTML(html);
	}

	public SVGResource getIcon() {
		return this.icon;
	}
}