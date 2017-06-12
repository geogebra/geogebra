package org.geogebra.web.web.gui.toolbarpanel;

import java.util.ArrayList;
import java.util.List;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.main.Localization;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.css.MaterialDesignResources;
import org.geogebra.web.web.javax.swing.GCheckmarkMenuItem;
import org.geogebra.web.web.javax.swing.GPopupMenuW;

import com.google.gwt.user.client.Command;

/**
 * Algebra tab 3-dot menu.
 * 
 * @author laszlo
 *
 */
public class ContextMenuTools implements SetLabels {
	protected GPopupMenuW wrappedPopup;
	protected Localization loc;
	private List<GCheckmarkMenuItem> checkmarkItems;
	AppW app;

	private enum ToolType {
		BASIC, STANDARD, ALL
	}

	private ToolType toolType = ToolType.STANDARD;
	private String checkmarkUrl;

	/**
	 * Creates new context menu
	 * 
	 * @param app
	 *            application
	 */
	ContextMenuTools(AppW app) {
		this.app = app;
		loc = app.getLocalization();
		wrappedPopup = new GPopupMenuW(app);
		wrappedPopup.getPopupPanel().addStyleName("matMenu");
		checkmarkItems = new ArrayList<GCheckmarkMenuItem>();
		buildGUI();
		}

	private void buildGUI() {
		wrappedPopup.clearItems();
		addToolItems();
		setToolType(ToolType.STANDARD);
	}

	private void addToolItems() {
		checkmarkUrl = MaterialDesignResources.INSTANCE.check_black()
				.getSafeUri().asString();

		addCheckmarkItem(loc.getPlain("Basic.Tools"), false, new Command() {

			public void execute() {
				setToolType(ToolType.BASIC);
			}
		});

		addCheckmarkItem(loc.getPlain("Standard.Tools"), false, new Command() {

			public void execute() {
				setToolType(ToolType.STANDARD);
			}
		});

		addCheckmarkItem(loc.getPlain("All.Tools"), false, new Command() {
			public void execute() {
				setToolType(ToolType.ALL);
			}
		});

	}

	private void updateToolItems() {
		for (int i = 0; i < checkmarkItems.size(); i++) {
			GCheckmarkMenuItem cm = checkmarkItems.get(i);
			cm.setSelected(ToolType.values()[i] == getToolType());
		}
	}
	/**
	 * Adds a menu item with checkmark
	 * 
	 * @param text
	 *            of the item
	 * @param selected
	 *            if checkmark should be shown or not
	 * @param command
	 *            to execute when selected.
	 */
	public void addCheckmarkItem(String text, boolean selected,
			Command command) {
		GCheckmarkMenuItem cm = new GCheckmarkMenuItem(text, checkmarkUrl,
				selected, command, app);
		wrappedPopup.addItem(cm.getMenuItem());
		checkmarkItems.add(cm);

	}

	public void show(GPoint p) {

		wrappedPopup.show(p);
	}

	public void show(int x, int y) {
		wrappedPopup.show(new GPoint(x, y));
	}

	@Override
	public void setLabels() {
		buildGUI();
	}

	public ToolType getToolType() {
		return toolType;
	}

	public void setToolType(ToolType toolType) {
		this.toolType = toolType;
		updateToolItems();
	}
	
}

