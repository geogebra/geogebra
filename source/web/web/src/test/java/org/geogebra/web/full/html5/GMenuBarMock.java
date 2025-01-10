package org.geogebra.web.full.html5;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.geogebra.web.full.gui.menubar.GMenuBar;
import org.geogebra.web.full.javax.swing.GCheckmarkMenuItem;
import org.geogebra.web.html5.gui.menu.AriaMenuItem;
import org.geogebra.web.html5.main.AppW;

public class GMenuBarMock extends GMenuBar {
	public static final String SEPARATOR = "SEPARATOR";
	private ArrayList<String> items;
	private Map<String, GCheckmarkMenuItem> checkMarks = new HashMap<>();

	/**
	 * @param title title
	 * @param app app
	 */
	public GMenuBarMock(String title, AppW app) {
		super(title, app);
		this.items = new ArrayList<>();
	}

	public void add(String title) {
		items.add(strip(title));
	}

	private String strip(String title) {
		return title.replaceAll("<.*>", "");
	}

	public ArrayList<String> getTitles() {
		return items;
	}

	@Override
	public void addSeparator() {
		items.add(SEPARATOR);
		super.addSeparator();
	}

	@Override
	public void clearItems() {
		items.clear();
		super.clearItems();
	}

	/**
	 * @param item new item
	 */
	public void addItem(GCheckmarkMenuItem item) {
		AriaMenuItem menuItem = item.getMenuItem();
		String text = menuItem.getText();
		add(text);
		checkMarks.put(strip(text), item);
	}

	/**
	 * @param title item title
	 * @return whether given item is checked
	 */
	public boolean isChecked(String title) {
		GCheckmarkMenuItem item = checkMarks.get(title);
		if (item instanceof GCheckmarkMenuItemMock) {
			return ((GCheckmarkMenuItemMock) item).isChecked();
		}
		return false;
	}
}
