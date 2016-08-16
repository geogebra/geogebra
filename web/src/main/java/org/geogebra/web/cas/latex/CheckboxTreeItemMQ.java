package org.geogebra.web.cas.latex;

import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoElement;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;

public class CheckboxTreeItemMQ extends MathQuillTreeItem {
	/**
	 * checkbox displaying boolean variables
	 */
	private CheckBox checkBox = null;

	public CheckboxTreeItemMQ(GeoElement geo0) {
		super(geo0);
		content.removeStyleName("mathQuillEditor");
	}

	@Override
	protected void createAvexWidget() {
		checkBox = new CheckBox();
		checkBox.setValue(((GeoBoolean) geo).getBoolean());
		content.addStyleName("noPadding");
		checkBox.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				((GeoBoolean) geo).setValue(event.getValue());
				geo.updateCascade();
				kernel.notifyRepaint();

			}
		});
		addAVEXWidget(content);
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
		createAvexWidget();
		addAVEXWidget(content);

		geo.getAlgebraDescriptionTextOrHTMLDefault(
				getBuilder(getPlainTextItem()));
		checkBox.setValue(((GeoBoolean) geo).getBoolean());

		updateColor(getPlainTextItem());

	}

	void addAVEXWidget(Widget w) {
		main.clear();
		main.add(marblePanel);
		if (checkBox != null) {
			main.add(checkBox);
		}
		main.add(controls);
		main.add(content);
	}
	
	/**
	 * 
	 * @param geo
	 * @return if geo matches to CheckboxTreeItemMQ.
	 */
	public static boolean match(GeoElement geo) {
		return geo instanceof GeoBoolean && geo.isSimple();
	}
	public static CheckboxTreeItemMQ as(TreeItem ti) {
		return (CheckboxTreeItemMQ) ti;
	}
}
