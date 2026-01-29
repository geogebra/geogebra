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

package org.geogebra.web.full.javax.swing;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.euclidian.draw.HasTextFormat;
import org.geogebra.common.kernel.geos.GeoInline;
import org.geogebra.common.main.App;
import org.geogebra.common.main.undo.UpdateContentActionStore;
import org.geogebra.web.html5.gui.util.Dom;
import org.geogebra.web.html5.gui.util.FastClickHandler;
import org.geogebra.web.html5.gui.view.IconSpec;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.main.general.GeneralIcon;
import org.geogebra.web.html5.main.general.GeneralIconResource;
import org.gwtproject.user.client.ui.FlowPanel;
import org.gwtproject.user.client.ui.Widget;

/**
 * Menu item that acts like a toolbar.
 */
public class InlineTextToolbar implements FastClickHandler {
	private final App app;
	private List<HasTextFormat> formatters;
	private FlowPanel panel;
	private StandardButton subScriptBtn;
	private StandardButton superScriptBtn;
	private StandardButton bulletListBtn;
	private StandardButton numberedListBtn;
	private final GeneralIconResource generalIconResource;

	/**
	 * Constructor of special context menu item holding the
	 * list and sub/superscript toggle buttons
	 * @param formatters the formatters.
	 *
	 */
	public InlineTextToolbar(List<HasTextFormat> formatters, App app) {
		this.formatters = formatters;
		this.app = app;
		this.generalIconResource = ((AppW) app).getGeneralIconResource();

		createGui();
		setTooltips();
	}

	/**
	 * Creates the toolbar gui
	 */
	protected void createGui() {
		panel = new FlowPanel();
		createSubscriptBtn();
		createSuperscriptBtn();
		createBulletListBtn();
		createNumberedListBtn();
		updateState();
	}

	private void createSubscriptBtn() {
		subScriptBtn = createButton(generalIconResource.getImageResource(GeneralIcon.X_2));
		add(subScriptBtn);
	}

	private void createSuperscriptBtn() {
		superScriptBtn = createButton(generalIconResource.getImageResource(GeneralIcon.X_SQUARE));
		add(superScriptBtn);
	}

	private void createBulletListBtn() {
		bulletListBtn = createButton(generalIconResource
				.getImageResource(GeneralIcon.BULLET_LIST));
		add(bulletListBtn);
	}

	private void createNumberedListBtn() {
		numberedListBtn = createButton(generalIconResource
				.getImageResource(GeneralIcon.NUMBERED_LIST));
		add(numberedListBtn);
	}

	private void updateState() {
		Dom.toggleClass(subScriptBtn, "selected", "sub".equals(getScriptFormat()));
		Dom.toggleClass(superScriptBtn, "selected", "super".equals(getScriptFormat()));
		Dom.toggleClass(bulletListBtn, "selected", "bullet".equals(getListStyle()));
		Dom.toggleClass(numberedListBtn, "selected", "number".equals(getListStyle()));
	}

	private StandardButton createButton(IconSpec icon) {
		StandardButton button = new StandardButton(icon, "", 24, 24);
		button.addStyleName("ToggleButton");
		button.addFastClickHandler(this);
		return button;
	}

	protected String getScriptFormat() {
		if (formatters.isEmpty()) {
			return "";
		}

		String format = formatters.get(0).getFormat("script", "normal");
		if (formatters.size() == 1) {
			return format;
		}

		for (HasTextFormat formatter : formatters) {
			if (!format.equals(formatter.getFormat("script", "normal"))) {
				return "";
			}
		}

		return format;
	}

	protected String getListStyle() {
		if (formatters.isEmpty()) {
			return "";
		}

		String listStyle = getListStyle(formatters.get(0));
		if (formatters.size() == 1) {
			return listStyle;
		}

		for (HasTextFormat formatter : formatters) {
			if (!listStyle.equals(getListStyle(formatter))) {
				return "";
			}
		}
		return listStyle;
	}

	private String getListStyle(HasTextFormat formatter) {
		return formatter.getListStyle() != null
				? formatter.getListStyle()
				: "";
	}

	/**
	 * Adds a widget.
	 * @param widget widget to add
	 */
	public void add(Widget widget) {
		panel.add(widget);
	}

	@Override
	public void onClick(Widget source) {

		if (subScriptBtn == source) {
			setSubscript(!subScriptBtn.getStyleName().contains("selected"));
		} else if (superScriptBtn == source) {
			setSuperscript(!superScriptBtn.getStyleName().contains("selected"));
		} else if (bulletListBtn == source) {
			switchListTo("bullet");
		} else if (numberedListBtn == source) {
			switchListTo("number");
		}

		updateState();
	}

	private void setSubscript(Boolean value) {
		formatScript("sub", value);
	}

	private void setSuperscript(Boolean value) {
		formatScript("super", value);
	}

	private void formatScript(String type, Boolean value) {
		ArrayList<GeoInline> geosToStore = new ArrayList<>();
		for (HasTextFormat formatter : formatters) {
			geosToStore.add(formatter.getInline());
		}

		UpdateContentActionStore store = new UpdateContentActionStore(geosToStore);
		for (HasTextFormat formatter : formatters) {
			formatter.format("script", value ? type : "none");
		}
		if (store.needUndo()) {
			store.storeUndo();
		}
	}

	private void switchListTo(String listType) {
		for (HasTextFormat formatter : formatters) {
			formatter.switchListTo(listType);
		}
	}

	/**
	 * Sets the tooltips
	 */
	protected void setTooltips() {
		subScriptBtn.setTitle(app.getLocalization().getMenu("Subscript"));
		superScriptBtn.setTitle(app.getLocalization().getMenu("Superscript"));
		bulletListBtn.setTitle(app.getLocalization().getMenu("bulletList"));
		numberedListBtn.setTitle(app.getLocalization().getMenu("numberedList"));
	}

	/**
	 *
	 * @return the toolbar as a menu item
	 */
	public FlowPanel getItem() {
		return panel;
	}
}