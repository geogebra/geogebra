package geogebra.html5.gui;

import geogebra.web.gui.NoDragImage;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Label;

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
		this.getElement().removeAllChildren();

		if (image != null) {
			NoDragImage im = new NoDragImage(image.getSafeUri().asString());
			this.getElement().appendChild(im.getElement());
		}

		if (label != null) {
			Label l = new Label(label);
			this.getElement().appendChild(l.getElement());
		}
	}
	
	@Override
    public void setText(String text){
		this.label = text;
		setIconAndLabel(this.icon, text);
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
	
	@Override
    @Deprecated
    /**
     * Use addFastClickHandler instead
     */
	public HandlerRegistration addClickHandler(ClickHandler c){
		return null;
	}
}