package org.geogebra.web.full.gui.view.algebra;

import java.util.ArrayList;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.gui.view.algebra.SuggestionRootExtremum;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.main.Feature;
import org.geogebra.common.main.Localization;
import org.geogebra.common.main.OptionType;
import org.geogebra.common.scientific.LabelController;
import org.geogebra.web.full.css.MaterialDesignResources;
import org.geogebra.web.full.gui.menubar.MainMenu;
import org.geogebra.web.full.javax.swing.GPopupMenuW;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.html5.gui.util.AriaMenuItem;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.resources.SVGResource;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;

/**
 * The ... menu for AV items
 *
 */
public class ContextMenuAVItemMore implements SetLabels {
	/** visible component */
	protected GPopupMenuW wrappedPopup;
	/** localization */
	protected Localization loc;
	private AppWFull app;
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

	/**
	 * Rebuild the UI
	 */
	public void buildGUI() {
		wrappedPopup.clearItems();
		if (!app.getConfig().hasAutomaticLabels()) {
			if (item.geo.isAlgebraLabelVisible()) {
				addHideLabelItem();
			} else {
				addShowLabelItem();
			}
		}
		if (item.geo.hasTableOfValues() && app.getConfig().hasTableView(app)) {
			addTableOfValuesItem();
		}
		if (SuggestionRootExtremum.get(item.geo) != null
				&& app.has(Feature.SPECIAL_POINTS_IN_CONTEXT_MENU)) {
			addSpecialPointsItem();
		}
		addDuplicateItem();
		addDeleteItem();
		// wrappedPopup.addSeparator();
		if (app.getActivity().showObjectSettingsFromAV()) {
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
		addAction(new MenuAction(loc.getMenu("TableOfValues"),
				MaterialDesignResources.INSTANCE.toolbar_table_view_black()) {

					@Override
					public void execute() {
						app.getGuiManager().showTableValuesView(item.geo);
					}
				});
	}

	private void addShowLabelItem() {
		addAction(new MenuAction(loc.getMenu("ShowLabel"),
				MaterialDesignResources.INSTANCE.label()) {

					@Override
					public void execute() {
						new LabelController().showLabel(item.geo);
					}
				});
	}

	private void addHideLabelItem() {
		addAction(new MenuAction(loc.getMenu("HideLabel"),
				MaterialDesignResources.INSTANCE.label_off()) {

					@Override
					public void execute() {
						new LabelController().hideLabel(item.geo);
					}
				});
	}

	private void addSpecialPointsItem() {
		addAction(new MenuAction(loc.getMenu("Suggestion.SpecialPoints"),
				MaterialDesignResources.INSTANCE.special_points()) {

					@Override
					public void execute() {
						SuggestionRootExtremum.get(item.geo).execute(item.geo);
					}
				});
	}

	private void addDuplicateItem() {
		addAction(new MenuAction(loc.getMenu("Duplicate"),
				MaterialDesignResources.INSTANCE.duplicate_black()) {

			@Override
			public void execute() {
				RadioTreeItem input = item.getAV().getInputTreeItem();

				String dup = "";
				if ("".equals(item.geo
						.getDefinition(StringTemplate.defaultTemplate))) {
					dup = item.geo.getValueForInputBar();
				} else {
					dup = item.geo.getDefinitionNoLabel(
							StringTemplate.editorTemplate);
				}
				item.selectItem(false);
				input.setText(dup);
				input.setFocus(true, true);
			}
						
			@Override
			public boolean isAvailable() {
				return item.geo.isAlgebraDuplicateable();
			}
		});

	}
		
	private void addDeleteItem() {
		addAction(
				new MenuAction(loc.getMenu("Delete"),
						MaterialDesignResources.INSTANCE.delete_black()) {
					
					@Override
					public void execute() {
						item.geo.remove();
						app.storeUndoInfo();
					}
				});
	}

	private void addPropertiesItem() {
		addAction(
				new MenuAction(loc.getMenu("Settings"), MaterialDesignResources.INSTANCE.gear()) {
					
					@Override
					public void execute() {
						openSettings();
					}
				});
	}

	private void addAction(MenuAction menuAction) {
		SVGResource img = menuAction.getImage();
		AriaMenuItem mi = new AriaMenuItem(
				MainMenu.getMenuBarHtml(img, menuAction.getTitle()), true,
				menuAction);
		mi.setEnabled(menuAction.isAvailable());
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

