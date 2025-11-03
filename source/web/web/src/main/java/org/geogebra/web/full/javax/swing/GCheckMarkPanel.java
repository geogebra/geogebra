package org.geogebra.web.full.javax.swing;

import org.geogebra.common.main.GeoGebraColorConstants;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.html5.gui.util.AriaHelper;
import org.geogebra.web.html5.gui.util.NoDragImage;
import org.geogebra.web.resources.SVGResource;
import org.gwtproject.resources.client.ResourcePrototype;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.Image;
import org.gwtproject.user.client.ui.Label;

/**
 * Adds a panel with a optional image, text and checkmark
 */

public class GCheckMarkPanel extends FlowPanel {
	private Label label;
	private String text;
	private boolean checked;
	private Image checkImg;

	/**
	 * @param text of panel
	 * @param icon of panel (optional)
	 * @param checked initial value
	 */
	public GCheckMarkPanel(String text, ResourcePrototype icon, boolean checked) {
		addStyleName("checkMarkMenuItem");
		this.text = text;
		this.checked = checked;
		buildGui(icon, text);
		updateCheckImg();
	}

	/**
	 * @param value whether checked or not
	 */
	public void setChecked(boolean value) {
		checked = value;
		updateCheckImg();
	}

	private void updateCheckImg() {
		SVGResource svgResource = checked
				? MaterialDesignResources.INSTANCE.checkbox_checked()
				.withFill(GeoGebraColorConstants.PURPLE_600.toString())
				: MaterialDesignResources.INSTANCE.checkbox_unchecked()
				.withFill(GeoGebraColorConstants.NEUTRAL_700.toString());
		checkImg.setUrl(svgResource.getSafeUri());
	}

	private void buildGui(ResourcePrototype icon, String text) {
		if (icon != null) {
			add(new NoDragImage(icon, 24));
		}

		label = new Label(text);
		label.setStyleName("gwt-HTML");
		add(label);

		checkImg = new NoDragImage(MaterialDesignResources.INSTANCE.checkbox_checked()
				.withFill(GeoGebraColorConstants.PURPLE_600.toString()), 24, 24);
		checkImg.addStyleName("checkImg");
		add(checkImg);
		AriaHelper.setAlt(checkImg, "");
	}

	/**
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
	 * @param text checkbox label
	 */
	public void setText(String text) {
		this.text = text;
		label.setText(text);
	}
}
