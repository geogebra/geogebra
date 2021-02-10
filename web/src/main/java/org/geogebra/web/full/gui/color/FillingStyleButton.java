package org.geogebra.web.full.gui.color;

import org.geogebra.common.gui.util.SelectionTable;
import org.geogebra.common.kernel.geos.properties.FillType;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.util.ButtonPopupMenu;
import org.geogebra.web.full.gui.util.GeoGebraIconW;
import org.geogebra.web.full.gui.util.PopupMenuButtonW;
import org.geogebra.web.html5.gui.util.ImageOrText;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

/**
 * Button to get popup with filling options.
 */
public class FillingStyleButton extends PopupMenuButtonW {

	private FillType[] fillTypes;

	/**
	 * Filling style for fillable Geos
	 * 
	 * @param app
	 *            application
	 */
	public FillingStyleButton(AppW app, FillType[] fillTypes) {
		super(app, createDummyIcons(fillTypes.length), -1, 5, SelectionTable.MODE_ICON);
		this.fillTypes = fillTypes;

		ButtonPopupMenu pp = (ButtonPopupMenu) getMyPopup();
		pp.addStyleName("mowPopup");

		createFillTable();
	}
	
	private void createFillTable() {
		ImageOrText[] icons = new ImageOrText[fillTypes.length];
		for (int i = 0; i < fillTypes.length; i++) {
			icons[i] = GeoGebraIconW.createFillStyleIcon(fillTypes[i]);
		}
		getMyTable().populateModel(icons);
		getMyTable().addClickHandler(new ClickHandler() {

			@Override

			public void onClick(ClickEvent event) {
				handlePopupActionEvent();
			}

		});
	}

	private static ImageOrText[] createDummyIcons(int count) {
		ImageOrText[] a = new ImageOrText[count];
		for (int i = 0; i < count; i++) {
			a[i] = new ImageOrText();
		}
		return a;
	}

	/**
	 * Shows/hides fill table.
	 * 
	 * @param b
	 *            true if filling is enabled.
	 */
	public void setFillEnabled(boolean b) {
		getMyTable().setVisible(b);
	}

	/**
	 * @return selected fill type
	 */
	public FillType getSelectedFillType() {
		int idx = getMyTable().getSelectedIndex();
		return idx != -1 ? fillTypes[idx] : FillType.STANDARD;
	}

	/**
	 * Sets the original fill type of the geo.
	 * 
	 * @param fillType
	 *            the type to select initially.
	 */
	public void setFillType(FillType fillType) {
		for (int i = 0; i < fillTypes.length; i++) {
			if (fillTypes[i] == fillType) {
				getMyTable().setSelectedIndex(i);
			}
		}
	}

	@Override
	public ImageOrText getButtonIcon() {
		return new ImageOrText(MaterialDesignResources.INSTANCE.filling_black(), 24);
	}
}
