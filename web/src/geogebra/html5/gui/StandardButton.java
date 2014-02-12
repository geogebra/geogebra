package geogebra.html5.gui;

import com.google.gwt.resources.client.ImageResource;

public class StandardButton extends FastButton {

	

	private ImageResource icon;
	private String label;

	public StandardButton(final ImageResource icon) {
		setIconAndLabel(icon, null);
	}

	public StandardButton(final String label) {
		setIconAndLabel(null, label);
	}

	public StandardButton(final ImageResource icon, final String label) {
		setIconAndLabel(icon, label);
	}

	private void setIconAndLabel(final ImageResource image, final String label) {

		this.icon = image;
		this.label = label;

		String html = "";

		if (image != null) {
			html = "<div class=\"image\"> <img src=\""
					+ image.getSafeUri().asString() + "\" /></div>";
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

	public void setLabel(final String label) {
		setIconAndLabel(this.icon, label);
	}

	public ImageResource getIcon() {
		return this.icon;
	}

	public void setIcon(final ImageResource icon) {
		setIconAndLabel(icon, this.label);

	}
}