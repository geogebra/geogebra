package org.geogebra.web.full.gui.view.algebra;

import java.util.ArrayList;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.Feature;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.OptionType;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.menubar.MainMenu;
import org.geogebra.web.full.javax.swing.GPopupMenuW;
import org.geogebra.web.html5.gui.util.AriaMenuItem;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.resources.SVGResource;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.Command;

/**
 * The ... menu for AV items
 *
 */
public class ContextMenuAVItemMore implements SetLabels {
	/** visible component */
	protected GPopupMenuW wrappedPopup;
	/** localization */
	protected Localization loc;
	private AppW app;
	/** parent item */
	RadioTreeItem item;

	/**
	 * Creates new context menu
	 * 
	 * @param item
	 *            application
	 */
	ContextMenuAVItemMore(RadioTreeItem item) {
		app = item.getApplication();
		loc = app.getLocalization();
		this.item = item;
		wrappedPopup = new GPopupMenuW(app);
		if (app.isUnbundled()) {
			wrappedPopup.getPopupPanel().addStyleName("matMenu");
		} else {
			wrappedPopup.getPopupPanel().addStyleName("mioMenu");
		}
		buildGUI();
	}

	/**
	 * @return see {@link AppW}
	 */
	public AppW getApp() {
		return app;
	}

	private void buildGUI() {
		wrappedPopup.clearItems();
		if (item.geo.hasTableOfValues() && app.has(Feature.TABLE_VIEW)) {
			addTableOfValuesItem();
		}
		addDuplicateItem();
		addDeleteItem();
		// wrappedPopup.addSeparator();
		if (app.getConfig().showObjectSettingsFromAV()) {
			addPropertiesItem();
		}
	}

	/**
	 * @param x
	 *            screen x-coordinate
	 * @param y
	 *            screen y-coordinate
	 */
	public void show(int x, int y) {
		wrappedPopup.show(new GPoint(x, y));
		focusDeferred();
	}

	private void addTableOfValuesItem() {
		SVGResource img = MaterialDesignResources.INSTANCE
				.toolbar_table_view_black();
		AriaMenuItem mi = new AriaMenuItem(MainMenu.getMenuBarHtml(img,
				loc.getMenu("TableOfValues")), true, new Command() {

					@Override
					public void execute() {
						app.getGuiManager().showTableValuesView(item.geo);
					}
				});
		wrappedPopup.addItem(mi);
	}

	private void addDuplicateItem() {
		SVGResource img = MaterialDesignResources.INSTANCE.duplicate_black();
		AriaMenuItem mi = new AriaMenuItem(
				MainMenu.getMenuBarHtml(img, loc.getMenu("Duplicate")),
				true,
				new Command() {
					
					@Override
					public void execute() {
						RadioTreeItem input = item.getAV().getInputTreeItem();
						
						String dup = "";
						if ("".equals(item.geo.getDefinition(StringTemplate.defaultTemplate))) {
							dup = item.geo.getValueForInputBar();
						} else {
							dup = item.geo.getDefinitionNoLabel(
									StringTemplate.editorTemplate);
						}
						item.selectItem(false);
						input.setText(dup);
						input.setFocus(true, true);
					
					}
				});
		mi.setEnabled(item.geo.isAlgebraDuplicateable());
		wrappedPopup.addItem(mi);
	}
		
	private void addDeleteItem() {
		SVGResource img = MaterialDesignResources.INSTANCE.delete_black();
		AriaMenuItem mi = new AriaMenuItem(
				MainMenu.getMenuBarHtml(img,
						loc.getMenu("Delete")),
				true,
				new Command() {
					
					@Override
					public void execute() {
						item.geo.remove();
						app.storeUndoInfo();
					}
				});
		wrappedPopup.addItem(mi);
	}

	private void addPropertiesItem() {
		SVGResource img = MaterialDesignResources.INSTANCE.gear();
		AriaMenuItem mi = new AriaMenuItem(
				MainMenu.getMenuBarHtml(img, loc.getMenu("Settings")),
				true,
				new Command() {
					
					@Override
					public void execute() {
						openSettings();
					}
				});

		wrappedPopup.addItem(mi);
	}

	/**
	 * OPen object settings
	 */
	protected void openSettings() {
		ArrayList<GeoElement> list = new ArrayList<>();
		list.add(item.geo);
		app.getDialogManager().showPropertiesDialog(OptionType.OBJECTS, list);
	}

	@Override
	public void setLabels() {
		buildGUI();
	}
	
	private void focusDeferred() {
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				wrappedPopup.getPopupMenu().getElement().focus();
			}
		});
	}
}

