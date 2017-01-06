package org.geogebra.web.web.gui.view.algebra;

import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoElement;

import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;

public class CheckboxTreeItem extends LatexTreeItem {
	/**
	 * checkbox displaying boolean variables
	 */
	CheckBox checkBox = null;

	public CheckboxTreeItem(GeoElement geo0) {
		super(geo0);
	}

	@Override
	protected RadioTreeItemController createController() {
		return new CheckBoxTreeItemController(this);
	}

	/**
	 * 
	 * @return The controller as CheckBoxTreeItemController.
	 */
	public CheckBoxTreeItemController getCheckBoxController() {
		return (CheckBoxTreeItemController) getController();
	}

	@Override
	protected void createAvexWidget() {
		checkBox = new CheckBox();
		checkBox.setValue(((GeoBoolean) geo).getBoolean());
		content.addStyleName("noPadding");
	}

	@Override
	protected void addControls() {
		createControls();
		// no add this time
	}

	@Override
	protected void doUpdate() {
		setNeedsUpdate(false);
		if (hasMarblePanel()) {
			marblePanel.update();
		}

		if (hasAnimPanel()) {
			controls.updateAnimPanel();

		}
		content.clear();
		createAvexWidget();
		addAVEXWidget(content);

		geo.getAlgebraDescriptionTextOrHTMLDefault(
				getBuilder(getPlainTextItem()));
		content.add(getPlainTextItem());
		checkBox.setValue(((GeoBoolean) geo).getBoolean());

		updateColor(getPlainTextItem());

	}

	@Override
	void addAVEXWidget(Widget w) {
		main.clear();
		main.add(marblePanel);
		if (checkBox != null) {
			main.add(checkBox);
		}
		// main.add(buttonPanel);
		main.add(content);
	}
	
	/**
	 * 
	 * @param geo
	 * @return if geo matches to CheckboxTreeItem.
	 */
	public static boolean match(GeoElement geo) {
		return geo instanceof GeoBoolean && geo.isSimple();
	}

	public static CheckboxTreeItem as(TreeItem ti) {
		return (CheckboxTreeItem) ti;
	}

	@Override
	public boolean isCheckBoxItem() {
		return true;
	}

	@Override
	public boolean isInputTreeItem() {
		return false;
	}
}
