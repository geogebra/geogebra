package org.geogebra.web.full.javax.swing;

import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.html5.gui.util.NoDragImage;
import org.geogebra.web.html5.util.Dom;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;

/**
 * item with checkmark and line thickness preview
 */
public class LineThicknessCheckMarkItem extends FlowPanel {
	private NoDragImage checkImg;
	private boolean selected;
	private Label text;
	private int value;

	/**
	 * constructor
	 * @param thicknessStyle - style class name of item
	 * @param value - thickness
	 */
	public LineThicknessCheckMarkItem(String thicknessStyle, int value) {
		addStyleName("lineThicknessItem");
		this.value = value;
		buildGUI(thicknessStyle);
	}

	/**
	 * constructor
	 * @param itemText - text of menu item
	 * @param thicknessStyle - style class name of item
	 * @param value - thickness
	 */
	public LineThicknessCheckMarkItem(String itemText, String thicknessStyle, int value) {
		addStyleName("lineThicknessItem");
		addStyleName(thicknessStyle);
		this.value = value;

		checkImg = new NoDragImage(MaterialDesignResources.INSTANCE.check_black(), 24, 24);
		checkImg.addStyleName("checkImg");
		add(checkImg);

		Label text = new Label(itemText);
		this.text = text;
		add(text);
	}

	private void buildGUI(String thicknessStyle) {
		checkImg = new NoDragImage(MaterialDesignResources.INSTANCE.check_black(), 24, 24);
		checkImg.addStyleName("checkImg");
		add(checkImg);

		SimplePanel linePreview = new SimplePanel();
		linePreview.addStyleName("linePreview");
		linePreview.addStyleName(thicknessStyle);
		add(linePreview);
	}

	/**
	 * @return true if checkmark is selected
	 */
	public boolean isSelected() {
		return selected;
	}

	/**
	 * @param selected
	 *            true if checkmark visible
	 */
	public void setSelected(boolean selected) {
		this.selected = selected;
		updateCheckMarkStyle();
	}

	/**
	 * @return thickness value
	 */
	public int getValue() {
		return value;
	}

	/**
	 * show or hide checkmark depending on its status (selected or not selected)
	 */
	public void updateCheckMarkStyle() {
		Dom.toggleClass(checkImg, "selected", isSelected());
	}

	/**
	 * update translation of text check mark item
	 * @param translation - new translation
	 */
	public void setLabel(String translation) {
		if (text != null) {
			text.setText(translation);
		}
	}
}