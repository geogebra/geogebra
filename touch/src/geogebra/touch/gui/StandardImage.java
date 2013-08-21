package geogebra.touch.gui;

import org.vectomatic.dom.svg.ui.SVGResource;

import com.google.gwt.user.client.ui.FlowPanel;

public class StandardImage extends FlowPanel {
	private SVGResource icon;

	public StandardImage(SVGResource icon) {
		this.setIcon(icon);
		this.setStyleName("image");
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
