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

package org.geogebra.web.full.gui.components.dropdown.grid;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.euclidian.background.BackgroundType;
import org.geogebra.web.html5.main.AppW;
import org.gwtproject.event.dom.client.ClickEvent;
import org.gwtproject.user.client.ui.FlowPanel;

public class GridCardPanel extends FlowPanel {
	private final AppW appW;
	private final List<GridCard> cardList = new ArrayList<>();
	private GridDropdownListener listener;

	/**
	 * Panel holding grid cards
	 * @param appW - application
	 * @param selectedBackgroundType - background type
	 */
	public GridCardPanel(AppW appW, BackgroundType selectedBackgroundType) {
		this.appW = appW;

		buildCardPanel(selectedBackgroundType);
	}

	private void buildCardPanel(BackgroundType selectedBackgroundType) {
		addStyleName("gridCardPanel");

		for (BackgroundType type : BackgroundType.rulingOptions) {
			GridCard gridCard = buildCard(type, selectedBackgroundType);
			cardList.add(gridCard);
			add(gridCard);
		}
	}

	private GridCard buildCard(BackgroundType type, BackgroundType selectedBackgroundType) {
		GridCard gridCard = new GridCard(appW, GridDataProvider.getResourceForBackgroundType(
				type), GridDataProvider.getTransKeyForRulingType(type));
		if (type == selectedBackgroundType) {
			gridCard.setSelected(true);
		}

		addClickHandler(gridCard, type);
		return gridCard;
	}

	private void addClickHandler(GridCard gridCard, BackgroundType type) {
		gridCard.addDomHandler(event -> {
			deselectAll();
			gridCard.setSelected(true);
			listener.itemSelected(BackgroundType.rulingOptions.indexOf(type));
		}, ClickEvent.getType());
	}

	/**
	 * remove card selection
	 */
	private void deselectAll() {
		for (GridCard gridCard : cardList) {
			if (gridCard.isSelected()) {
				gridCard.setSelected(false);
			}
		}
	}

	/**
	 * Select card
	 * @param index - index
	 */
	public void setSelectedIndex(int index) {
		deselectAll();
		cardList.get(index).setSelected(true);
	}

	public void setListener(GridDropdownListener listener) {
		this.listener = listener;
	}
}
