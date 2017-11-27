package org.geogebra.web.web.gui.properties;

import java.util.HashMap;

import org.geogebra.common.main.App;
import org.geogebra.common.main.OptionType;
import org.geogebra.web.html5.gui.util.ImgResourceHelper;
import org.geogebra.web.web.css.GuiResources;
import org.geogebra.web.web.css.MaterialDesignResources;
import org.geogebra.web.web.gui.ImageFactory;
import org.geogebra.web.web.gui.images.AppResources;
import org.geogebra.web.web.gui.images.SvgPerspectiveResources;
import org.geogebra.web.web.gui.menubar.AriaMenuBar;
import org.geogebra.web.web.gui.menubar.AriaMenuItem;
import org.geogebra.web.web.gui.menubar.MainMenu;
import org.geogebra.web.web.gui.util.PopupMenuButtonW;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.resources.client.impl.ImageResourcePrototype;
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

	private static OptionType OptionTypesImpl[] = {
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
		};
		
		toolbar.setStyleName("menuProperties");	
		toolbar.sinkEvents(Event.ONMOUSEDOWN | Event.ONTOUCHSTART);
		toolbar.setFocusOnHoverEnabled(false);
		
		buttonMap = new HashMap<OptionType, AriaMenuItem>();
		
		for (final OptionType type : OptionTypesImpl) {
			if (typeAvailable(type)){
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
	protected boolean typeAvailable(OptionType type){
		return type != OptionType.EUCLIDIAN3D
				&& type != OptionType.EUCLIDIAN_FOR_PLANE;
	}
	
	/**
	 * @param type
	 *            option type
	 */
	protected void selectButton(OptionType type) {
		if(currentButton != null){
			this.currentButton.removeStyleName("selected");
		}
		currentButton = buttonMap.get(type);
		currentButton.addStyleName("selected");
	    
    }




	private String getMenuHtml(OptionType type) {
		String typeString = "";//propertiesView.getTypeString(type);
		return MainMenu.getMenuBarHtml(getTypeIcon(type), typeString);
    }
	
	/**
	 * @param type
	 *            option type
	 * @param btn
	 *            button
	 */
	protected void setIcon(OptionType type, PopupMenuButtonW btn) {
		SvgPerspectiveResources pr = ImageFactory.getPerspectiveResources();
		switch (type) {
		case GLOBAL:
			ImgResourceHelper.setIcon(
					MaterialDesignResources.INSTANCE.gear(), btn);
		case DEFAULTS:
			ImgResourceHelper.setIcon(AppResources.INSTANCE.options_defaults224(), btn) ;
		case SPREADSHEET:
			ImgResourceHelper.setIcon(pr.menu_icon_spreadsheet24(), btn);
		case EUCLIDIAN:
			ImgResourceHelper
					.setIcon(app.isUnbundledOrWhiteboard()
							? new ImageResourcePrototype(null,
					MaterialDesignResources.INSTANCE
									.geometry().getSafeUri(),
							0, 0, 24, 24, false, false)
							: pr.menu_icon_graphics24(),
					btn);
		case EUCLIDIAN2:
			ImgResourceHelper.setIcon(pr.menu_icon_graphics224(), btn);
		case EUCLIDIAN_FOR_PLANE:
			ImgResourceHelper.setIcon(pr.menu_icon_graphics_extra24(),
					btn);
		case EUCLIDIAN3D:
			ImgResourceHelper.setIcon(pr.menu_icon_graphics3D24(), btn);
		case CAS:
			ImgResourceHelper.setIcon(pr.menu_icon_cas24(), btn);
		case ALGEBRA:
			ImgResourceHelper
					.setIcon(
							app.isUnbundledOrWhiteboard()
									? new ImageResourcePrototype(null,
											MaterialDesignResources.INSTANCE
													.graphing().getSafeUri(),
											0, 0, 24, 24, false, false)
									: AppResources.INSTANCE.options_algebra24(),
							btn);
		case OBJECTS:
			//AppResourcesConverter.setIcon(AppResources.INSTANCE.options_objects24(), btn);
			ImgResourceHelper.setIcon(GuiResources.INSTANCE.properties_object(), btn);
		case LAYOUT:
			ImgResourceHelper.setIcon(AppResources.INSTANCE.options_layout24(), btn);
		}
	}

	/**
	 * @param type
	 *            option type
	 * @return icon URL
	 */
	protected String getTypeIcon(OptionType type) {
		SvgPerspectiveResources pr = ImageFactory.getPerspectiveResources();
		switch (type) {
		case GLOBAL:
			return MaterialDesignResources.INSTANCE.gear().getSafeUri()
					.asString();
		case DEFAULTS:
			return AppResources.INSTANCE.options_defaults224().getSafeUri().asString();
		case SPREADSHEET:
			return ImgResourceHelper.safeURI(pr.menu_icon_spreadsheet24());
		case EUCLIDIAN:
			return app.isUnbundledOrWhiteboard()
					? new ImageResourcePrototype(null,
							MaterialDesignResources.INSTANCE.geometry()
									.getSafeUri(),
							0, 0, 24, 24, false, false).getURL()
					: ImgResourceHelper.safeURI(pr.menu_icon_graphics24());
			//return GuiResources.INSTANCE.properties_graphics().getSafeUri().asString();
		case EUCLIDIAN2:
			return ImgResourceHelper.safeURI(pr.menu_icon_graphics224());
			//return GuiResources.INSTANCE.properties_graphics2().getSafeUri().asString();
		case CAS:
			return ImgResourceHelper.safeURI(pr.menu_icon_cas24());
		case ALGEBRA:
			return app.isUnbundledOrWhiteboard()
					? new ImageResourcePrototype(null,
							MaterialDesignResources.INSTANCE.graphing()
									.getSafeUri(),
							0, 0, 24, 24, false, false).getURL()
					: AppResources.INSTANCE.options_algebra24().getSafeUri()
					.asString();
		case OBJECTS:
			//return AppResources.INSTANCE.options_objects24().getSafeUri().asString();
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
