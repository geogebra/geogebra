package geogebra.touch.gui.elements;

import geogebra.touch.TouchEntryPoint;
import geogebra.touch.gui.laf.LookAndFeel;

import org.vectomatic.dom.svg.ui.SVGResource;

public class StandardButton extends FastButton {

	protected static LookAndFeel laf = TouchEntryPoint.getLookAndFeel();

	private SVGResource icon;
	private String label;

	public StandardButton(SVGResource icon) {
		this.setIconAndLabel(icon, null);
	}

	public StandardButton(String label) {
		this.setIconAndLabel(null, label);
	}

	public StandardButton(SVGResource icon, String label) {
		this.setIconAndLabel(icon, label);
	}

	private void setIconAndLabel(SVGResource icon, String label) {

		this.icon = icon;
		this.label = label;

		String html = "";

		if (icon != null) {
			html = "<div class=\"image\"> <img src=\""
					+ icon.getSafeUri().asString() + "\" /></div>";
		}

		if (label != null) {
			html = html + "<div class=\"gwt-Label\">" + label + "</div>";
		}

		this.getElement().setInnerHTML(html);
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

	public String getLabel() {
		return this.label;
	}

	public void setLabel(String label) {
		setIconAndLabel(this.icon, label);
	}

	public SVGResource getIcon() {
		return this.icon;
	}

	public void setIcon(SVGResource icon) {
		setIconAndLabel(icon, this.label);

	}
}