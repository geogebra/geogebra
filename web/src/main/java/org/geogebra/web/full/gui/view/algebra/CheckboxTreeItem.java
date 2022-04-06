package org.geogebra.web.full.gui.view.algebra;

import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoElement;

import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;

/**
 * ReTeX based implementation of AV checkbox
 *
 */
public class CheckboxTreeItem extends RadioTreeItem {
	/**
	 * checkbox displaying boolean variables
	 */
	CheckBox checkBox = null;

	/**
	 * @param geo0
	 *            boolean geo
	 */
	public CheckboxTreeItem(GeoElement geo0) {
		super(geo0);
	}

	@Override
	protected LatexTreeItemController createController() {
		return new CheckBoxTreeItemController(this);
	}

	@Override
	protected void createAvexWidget() {
		checkBox = new CheckBox();
		checkBox.setValue(((GeoBoolean) geo).getBoolean());
		content.addStyleName("noPadding");
		main.addStyleName("checkboxElem");
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

		if (controls.hasAnimPanel()) {
			controls.updateAnimPanel();

		}
		content.clear();
		createAvexWidget();
		addAVEXWidget(content);

		geo.getAlgebraDescriptionTextOrHTMLDefault(
				new DOMIndexHTMLBuilder(getDefinitionValuePanel(), app));
		content.add(getDefinitionValuePanel());
		checkBox.setValue(((GeoBoolean) geo).getBoolean());
	}

	@Override
	protected void addAVEXWidget(Widget w) {
		main.clear();
		main.add(marblePanel.asWidget());
		if (checkBox != null) {
			main.add(checkBox);
		}
		// main.add(buttonPanel);
		main.add(content);
		main.add(controls);
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
