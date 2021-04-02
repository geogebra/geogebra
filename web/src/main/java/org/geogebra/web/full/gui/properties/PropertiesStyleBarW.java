package org.geogebra.web.full.gui.properties;

import java.util.HashMap;

import org.geogebra.common.main.App;
import org.geogebra.common.main.OptionType;
import org.geogebra.web.full.css.GuiResources;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.images.AppResources;
import org.geogebra.web.full.gui.images.SvgPerspectiveResources;
import org.geogebra.web.full.gui.menubar.MainMenu;
import org.geogebra.web.html5.css.GuiResourcesSimple;
import org.geogebra.web.html5.gui.util.AriaMenuBar;
import org.geogebra.web.html5.gui.util.AriaMenuItem;
import org.geogebra.web.html5.gui.util.NoDragImage;
import org.geogebra.web.html5.util.TestHarness;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.FlowPanel;

/**
 * @author gabor
 * Creates PropertiesStyleBar for Web
 *
 */
public class PropertiesStyleBarW extends
        org.geogebra.common.gui.view.properties.PropertiesStyleBar {

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
	private FlowPanel wrappedPanel;
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

			@Override
			protected App getApp() {
				return app;
			}
		};
		
		toolbar.setStyleName("menuProperties");
		TestHarness.setAttr(toolbar, "menuProperties");
		toolbar.sinkEvents(Event.ONMOUSEDOWN | Event.ONTOUCHSTART);
		NoDragImage closeImage = new NoDragImage(
				GuiResourcesSimple.INSTANCE.close(), 24, 24);
		closeImage.addStyleName("closeButton");
		TestHarness.setAttr(closeImage, "closeButton");
		toolbar.addItem(new AriaMenuItem(closeImage.getElement().getString(),
				true, new ScheduledCommand() {

					@Override
					public void execute() {
						propertiesView.close();
					}
		}));
		buttonMap = new HashMap<>();
		
		for (final OptionType type : OPTION_TYPE_IMPL) {
			if (typeAvailable(type)) {
				final PropertiesButton btn = new PropertiesButton(app,
						getMenuHtml(type), new Command() {

					@Override
					public void execute() {
						propertiesView.setOptionPanel(type, 0);
						selectButton(type);
					}
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

	private String getMenuHtml(OptionType type) {
		String typeString = ""; // propertiesView.getTypeString(type);
		return MainMenu.getMenuBarHtmlClassic(getTypeIcon(type), typeString);
    }

	/**
	 * @param type
	 *            option type
	 * @return icon URL
	 */
	protected String getTypeIcon(OptionType type) {
		SvgPerspectiveResources pr = SvgPerspectiveResources.INSTANCE;
		switch (type) {
		case GLOBAL:
			return MaterialDesignResources.INSTANCE.gear().getSafeUri()
					.asString();
		case DEFAULTS:
			return AppResources.INSTANCE.options_defaults224().getSafeUri().asString();
		case SPREADSHEET:
			return pr.menu_icon_spreadsheet_transparent().getSafeUri().asString();
		case EUCLIDIAN:
			return pr.menu_icon_graphics().getSafeUri().asString();
		case EUCLIDIAN2:
			return pr.menu_icon_graphics2_transparent().getSafeUri().asString();
		case CAS:
			return pr.menu_icon_cas_transparent().getSafeUri().asString();
		case ALGEBRA:
			return pr.menu_icon_algebra_transparent().getSafeUri().asString();
		case OBJECTS:
			return GuiResources.INSTANCE.properties_object().getSafeUri().asString();
		case LAYOUT:
			return AppResources.INSTANCE.options_layout24().getSafeUri().asString();
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
