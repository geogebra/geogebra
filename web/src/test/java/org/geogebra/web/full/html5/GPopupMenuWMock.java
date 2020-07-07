package org.geogebra.web.full.html5;

import java.util.List;

import org.geogebra.web.full.gui.menubar.GMenuBar;
import org.geogebra.web.full.javax.swing.GCheckmarkMenuItem;
import org.geogebra.web.full.javax.swing.GPopupMenuW;
import org.geogebra.web.html5.gui.util.AriaMenuItem;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.core.client.Scheduler;

public class GPopupMenuWMock extends GPopupMenuW {
	private GMenuBarMock menuBarMock;

	public GPopupMenuWMock(AppW app) {
		super(app);
		menuBarMock = new GMenuBarMock("menuMock", app);
	}

	@Override
	public void addSeparator() {
		menuBarMock.addSeparator();
	}

	@Override
	public GMenuBar getPopupMenu() {
		return menuBarMock;
	}

	@Override
	public void addItem(AriaMenuItem item) {
		menuBarMock.add(item.getHTML());
	}

	@Override
	public void addItem(String s, Scheduler.ScheduledCommand c) {
		menuBarMock.add(s);
	}

	@Override
	public void addItem(GCheckmarkMenuItem item) {
		menuBarMock.addItem(item);
	}

	@Override
	public void addItem(AriaMenuItem item, boolean autoHide) {
		addItem(item);
	}

	@Override
	public void clearItems() {
		menuBarMock.clearItems();
	}

	private List<String> getItems() {
		return menuBarMock.getTitles();
	}
}
