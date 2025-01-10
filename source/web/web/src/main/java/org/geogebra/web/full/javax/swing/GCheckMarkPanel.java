package org.geogebra.web.full.javax.swing;

import org.geogebra.web.html5.gui.util.NoDragImage;
import org.geogebra.web.resources.SVGResource;
import org.gwtproject.resources.client.ResourcePrototype;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.Image;
import org.gwtproject.user.client.ui.Label;

/**
 * Adds a panel with a checkmark on its end.
 * 
 * @author laszlo
 * 
 */
public class GCheckMarkPanel extends FlowPanel {

	private final Label label;
	private boolean checked;
	private String text;
	private final Image checkImg;

	/**
	 * @param text
	 *            Title
	 * @param checkUrl
	 *            image of check mark
	 * @param checked
	 *            initial value.
	 */
	public GCheckMarkPanel(String text, ResourcePrototype icon,
			SVGResource checkUrl, boolean checked) {
		checkImg = new NoDragImage(checkUrl, 24, 24);
		checkImg.addStyleName("checkImg");
		addStyleName("checkMarkMenuItem");
		this.text = text;
		label = new Label(text);
		label.setStyleName("gwt-HTML");
		this.checked = checked;
		buildGui(icon);
	}

	/**
	 * Sets the item checked/unchecked.
	 * 
	 * @param value
	 *            to set.
	 */
	public void setChecked(boolean value) {
		checked = value;
		if (checked) {
			add(checkImg);
		} else {
			checkImg.removeFromParent();
		}
	}

	private void buildGui(ResourcePrototype icon) {
		if (icon != null) {
			add(new NoDragImage(icon, 24));
		}
		add(label);
		if (checked) {
			add(checkImg);
		}
	}

	/**
	 * 
	 * @return true if item is checked
	 */
	public boolean isChecked() {
		return checked;
	}

	/**
	 * @return checkbox label
	 */
	public String getText() {
		return text;
	}

	/**
	 * @param text
	 *            checkbox label
	 */
	public void setText(String text) {
		this.text = text;
		label.setText(text);
	}
}
