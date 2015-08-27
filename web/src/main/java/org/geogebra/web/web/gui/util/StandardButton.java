package org.geogebra.web.web.gui.util;

import org.geogebra.web.html5.gui.FastButton;
import org.geogebra.web.html5.gui.NoDragImage;
import org.geogebra.web.web.gui.app.GGWToolBar;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.ResourcePrototype;
import com.google.gwt.user.client.ui.Label;

public class StandardButton extends FastButton {

	

	private ResourcePrototype icon;
	private String label;
	private int width = -1;

	public StandardButton(final ImageResource icon) {
		setIconAndLabel(icon, null, icon.getWidth());
	}

	public StandardButton(final String label) {
		setIconAndLabel(null, label, -1);
	}

	public StandardButton(final ResourcePrototype icon, final String label, int width) {
		setIconAndLabel(icon, label, width);
	}

	private void setIconAndLabel(final ResourcePrototype image, final String label, int width) {
		this.width = width;
		this.icon = image;
		this.label = label;
		this.getElement().removeAllChildren();

		if (image != null) {
			NoDragImage im = new NoDragImage(GGWToolBar.safeURI(image),width);
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
		setIconAndLabel(this.icon, text, this.width);
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
		setIconAndLabel(this.icon, label, this.width);
	}

	public ResourcePrototype getIcon() {
		return this.icon;
	}

	public void setIcon(final ImageResource icon) {
		setIconAndLabel(icon, this.label, this.width);

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