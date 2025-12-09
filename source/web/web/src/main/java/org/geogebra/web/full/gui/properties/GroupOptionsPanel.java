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

package org.geogebra.web.full.gui.properties;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.gui.dialog.options.model.GroupModel;
import org.geogebra.common.gui.dialog.options.model.OptionsModel;
import org.geogebra.common.main.Localization;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.Label;
import org.gwtproject.user.client.ui.Widget;

public class GroupOptionsPanel implements IOptionPanel {
	private List<IOptionPanel> panels;
	private String titleId;
	private FlowPanel mainWidget;
	private FlowPanel group;
	private Label titleLabel;
	private Localization loc;
	private GroupModel model;

	/**
	 * @param title
	 *            group title
	 * @param loc
	 *            localization
	 * @param model
	 *            model
	 */
	public GroupOptionsPanel(final String title, Localization loc,
			GroupModel model) {
		super();
		this.titleId = title;
		this.loc = loc;
		titleLabel = new Label("misc");
		mainWidget = new FlowPanel();
		group = new FlowPanel();
		mainWidget.add(titleLabel);
		mainWidget.add(group);
		this.model = model;
		titleLabel.setStyleName("panelTitle");
		group.setStyleName("optionsPanelIndent");
		
		panels = new ArrayList<>();
		setTitleLabel();
	}

	/**
	 * Add panel to the group.
	 * 
	 * @param panel
	 *            panel
	 */
	public void add(IOptionPanel panel) {
		group.add(panel.getWidget());
		panels.add(panel);
	}

	@Override
	public void setLabels() {
		setTitleLabel();
		for (IOptionPanel panel: panels) {
			panel.setLabels();
		}

	}

	/**
	 * Update title
	 */
	protected void setTitleLabel() {
		titleLabel.setText(getTitle());
	}

	private String getTitle() {
		String ret = loc.getMenu(titleId);
		if (ret.equals(titleId)) {
			// needed for eg Miscellaneous
			ret = loc.getMenu(titleId);
		}
		return ret;
    }

	@Override
	public Object updatePanel(Object[] geos) {
		boolean result = false;
		for (IOptionPanel panel: panels) {
			result = panel.updatePanel(geos) != null || result;
		}
		return result ? this : null;
	}

	@Override
	public Widget getWidget() {
		return mainWidget;
	}

	@Override
	public OptionsModel getModel() {
		return model;
	}

}
