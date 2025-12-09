/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 *
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 *
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.web.full.euclidian.quickstylebar.components;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.common.properties.impl.facade.RangePropertyListFacade;
import org.geogebra.common.properties.impl.objects.BorderThicknessProperty;
import org.geogebra.web.full.javax.swing.LineThicknessCheckMarkItem;
import org.geogebra.web.html5.gui.BaseWidgetFactory;
import org.geogebra.web.html5.gui.util.ClickStartHandler;
import org.geogebra.web.html5.main.AppW;
import org.gwtproject.user.client.ui.FlowPanel;

public class BorderThicknessPanel extends FlowPanel {
	private final RangePropertyListFacade<?> property;
	private final AppW appW;
	private List<LineThicknessCheckMarkItem> checkMarkItems;

	/**
	 * UI presenting cell border thickness
	 * @param property - cell border thickness property
	 * @param appW - application
	 */
	public BorderThicknessPanel(RangePropertyListFacade<?> property, AppW appW) {
		this.property = property;
		this.appW = appW;
		buildGui();
	}

	private void buildGui() {
		add(BaseWidgetFactory.INSTANCE.newDivider(false));
		checkMarkItems = new ArrayList<>();

		if (property.getFirstProperty() instanceof BorderThicknessProperty) {
			addNoBorderItem();
		}
		addThicknessCheckMarkItem(property, "thin", 1);
		addThicknessCheckMarkItem(property, "thick", 3);
	}

	private void addThicknessCheckMarkItem(RangePropertyListFacade<?> property,
			String style, int value) {
		LineThicknessCheckMarkItem checkMarkItem = new LineThicknessCheckMarkItem(style, value);
		add(checkMarkItem);
		checkMarkItem.setSelected(property.getValue() == value);
		checkMarkItems.add(checkMarkItem);

		addClickHandler(value, checkMarkItem, property);
	}

	private void addNoBorderItem() {
		LineThicknessCheckMarkItem noBorder = new LineThicknessCheckMarkItem(
				appW.getLocalization().getMenu("stylebar.NoBorder"), "textItem", 0);
		add(noBorder);
		noBorder.setSelected(property.getValue() == 0);
		checkMarkItems.add(noBorder);

		addClickHandler(0, noBorder, property);
	}

	private void addClickHandler(int value, LineThicknessCheckMarkItem checkMarkItem,
			RangePropertyListFacade<?> property) {
		ClickStartHandler.init(checkMarkItem,
				new ClickStartHandler(true, true) {
					@Override
					public void onClickStart(int x, int y, PointerEventType type) {
						checkMarkItems.forEach(item -> item.setSelected(false));
						checkMarkItem.setSelected(true);
						property.setValue(value);
						appW.closePopups();
					}
				});
	}
}
