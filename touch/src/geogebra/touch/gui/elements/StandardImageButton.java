package geogebra.touch.gui.elements;

import org.vectomatic.dom.svg.ui.SVGResource;

public class StandardImageButton extends FastButton {
	private SVGResource icon;

	public StandardImageButton(SVGResource icon) {
		super();
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

	@Override
	public void onHoldPressDownStyle() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onHoldPressOffStyle() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDisablePressStyle() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onEnablePressStyle() {
		// TODO Auto-generated method stub
		
	}
}