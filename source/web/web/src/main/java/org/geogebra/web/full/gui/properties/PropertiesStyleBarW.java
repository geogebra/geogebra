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

import java.util.HashMap;

import org.geogebra.common.main.App;
import org.geogebra.common.main.OptionType;
import org.geogebra.web.full.css.GuiResources;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.images.SvgPerspectiveResources;
import org.geogebra.web.html5.css.GuiResourcesSimple;
import org.geogebra.web.html5.gui.menu.AriaMenuBar;
import org.geogebra.web.html5.gui.menu.AriaMenuItem;
import org.geogebra.web.html5.gui.util.NoDragImage;
import org.geogebra.web.html5.util.TestHarness;
import org.gwtproject.core.client.Scheduler.ScheduledCommand;
import org.gwtproject.resources.client.ResourcePrototype;
import org.gwtproject.user.client.DOM;
import org.gwtproject.user.client.Event;
import org.gwtproject.user.client.ui.FlowPanel;

/**
 * @author gabor
 * Creates PropertiesStyleBar for Web
 *
 */
public class PropertiesStyleBarW {

	private static final OptionType[] OPTION_TYPE_IMPL = {
		// Implemented types of the web
			OptionType.GLOBAL, OptionType.OBJECTS, OptionType.EUCLIDIAN,
			OptionType.EUCLIDIAN2,
			OptionType.EUCLIDIAN_FOR_PLANE, OptionType.EUCLIDIAN3D,
			OptionType.SPREADSHEET, OptionType.CAS, OptionType.ALGEBRA
	};
	
	/**
	 * view
	 */
	protected PropertiesViewW propertiesView;
	/** app */
	protected App app;
	private final FlowPanel wrappedPanel;
	//private PopupMenuButton btnOption;
	/** maps options to buttons */
	private HashMap<OptionType, AriaMenuItem> buttonMap;

	private AriaMenuItem currentButton;

	/**
	 * @param propertiesView
	 *            properties view
	 * @param app
	 *            application
	 */
	public PropertiesStyleBarW(PropertiesViewW propertiesView, App app) {
		this.propertiesView = propertiesView;
		this.app = app;
		
		this.wrappedPanel = new FlowPanel();
		wrappedPanel.setStyleName("propertiesStyleBar");
		/*AGbtnOption.setHorizontalTextPosition(SwingConstants.RIGHT);
		Dimension d = btnOption.getPreferredSize();
		d.width = menu.getPreferredSize().width;
		btnOption.setPreferredSize(d);*/
		
		buildGUI();
		updateGUI();
	}
	
	/**
	 * @param type
	 *            option type
	 * @param visible
	 *            whether to show it
	 */
	protected void setButtonVisible(OptionType type, boolean visible) {
		buttonMap.get(type).setVisible(visible);
	}

	/**
	 * Show/hide the right buttons
	 */
	public void updateGUI() {

		setButtonVisible(OptionType.GLOBAL, true);

		setButtonVisible(OptionType.OBJECTS,
				app.getSelectionManager().selectedGeosSize() > 0);
		
		setButtonVisible(OptionType.EUCLIDIAN,
				app.getGuiManager()
						.showView(App.VIEW_EUCLIDIAN));
		
		setButtonVisible(OptionType.EUCLIDIAN2,
				app.getGuiManager()
						.showView(App.VIEW_EUCLIDIAN2));

		setButtonVisible(OptionType.SPREADSHEET,
				app.getGuiManager().showView(App.VIEW_SPREADSHEET));

		setButtonVisible(OptionType.CAS,
				app.getGuiManager().showView(App.VIEW_CAS));
		setButtonVisible(OptionType.ALGEBRA,
				app.getConfig().hasAlgebraView());
	}

	private void buildGUI() {
		final AriaMenuBar toolbar = new AriaMenuBar() {
			@Override
			public void onBrowserEvent(Event event) {
				super.onBrowserEvent(event);
				// by default first click gives focus, second click executes: we
				// want execute on first click
				if (DOM.eventGetType(event) == Event.ONMOUSEDOWN
						|| DOM.eventGetType(event) == Event.ONTOUCHSTART) {
					AriaMenuItem item = this.getSelectedItem();
					runCommand(item);
				}
			}

			private void runCommand(AriaMenuItem item) {
				if (item != null) {
					ScheduledCommand cmd = item.getScheduledCommand();
					if (cmd != null) {
						cmd.execute();
					}
				}
			}
		};
		
		toolbar.setStyleName("menuProperties");
		TestHarness.setAttr(toolbar, "menuProperties");
		toolbar.sinkEvents(Event.ONMOUSEDOWN | Event.ONTOUCHSTART);
		NoDragImage closeImage = new NoDragImage(
				GuiResourcesSimple.INSTANCE.close(), 24, 24);
		closeImage.addStyleName("closeButton");
		TestHarness.setAttr(closeImage, "closeButton");
		toolbar.addItem(new AriaMenuItem(closeImage, propertiesView::close));
		buttonMap = new HashMap<>();
		
		for (final OptionType type : OPTION_TYPE_IMPL) {
			if (typeAvailable(type)) {
				final PropertiesButton btn = new PropertiesButton(app,
						getTypeIcon(type), () -> {
							propertiesView.setOptionPanel(type, 0);
							selectButton(type);
						});
				btn.setTitle(propertiesView.getTypeString(type));
				toolbar.addItem(btn);
				buttonMap.put(type, btn);
			}
		}
		this.getWrappedPanel().add(toolbar);
    }
	
	/**
	 * @param type type
	 * @return true if the type is really available
	 */
	protected boolean typeAvailable(OptionType type) {
		return type != OptionType.EUCLIDIAN3D
				&& type != OptionType.EUCLIDIAN_FOR_PLANE;
	}
	
	/**
	 * @param type
	 *            option type
	 */
	protected void selectButton(OptionType type) {
		if (currentButton != null) {
			this.currentButton.removeStyleName("selected");
		}
		currentButton = buttonMap.get(type);
		currentButton.addStyleName("selected");
	}

	/**
	 * @param type
	 *            option type
	 * @return icon URL
	 */
	protected ResourcePrototype getTypeIcon(OptionType type) {
		SvgPerspectiveResources pr = SvgPerspectiveResources.INSTANCE;
		switch (type) {
		case GLOBAL:
		case LAYOUT:
		case DEFAULTS: // layout,defaults not implemented in Web => no special icon
			return MaterialDesignResources.INSTANCE.gear();
		case SPREADSHEET:
			return pr.menu_icon_spreadsheet_transparent();
		case EUCLIDIAN:
			return pr.menu_icon_graphics();
		case EUCLIDIAN2:
			return pr.menu_icon_graphics2_transparent();
		case CAS:
			return pr.menu_icon_cas_transparent();
		case ALGEBRA:
			return pr.menu_icon_algebra_transparent();
		case OBJECTS:
			return GuiResources.INSTANCE.properties_object();
		}
		return null;
    }

	/**
	 * @return stylebar panel
	 */
	public FlowPanel getWrappedPanel() {
		return wrappedPanel;
	}
}
