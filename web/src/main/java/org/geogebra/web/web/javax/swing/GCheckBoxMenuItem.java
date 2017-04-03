package org.geogebra.web.web.javax.swing;

import org.geogebra.common.main.App;
import org.geogebra.common.main.Feature;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MenuItem;

public class GCheckBoxMenuItem {

	CheckBox checkBox;
	MenuItem menuItem;
	HorizontalPanel itemPanel;

	// true if menu has no checkbox, but ON/OFF label.
	boolean toggle = false;
	boolean selected;
	private App app;
	private boolean isHtml;
	String text;
	private boolean forceCheckbox = false;
	// public GCheckBoxMenuItem(SafeHtml html, final ScheduledCommand cmd) {
	// super(html, cmd);
	// checkBox = new CheckBox(html);
	// checkBox.addValueChangeHandler(new ValueChangeHandler<Boolean>(){
	// public void onValueChange(ValueChangeEvent<Boolean> event) {
	// cmd.execute();
	// }});
	// setHTML(checkBox.toString());
	// }

	/*
	 * text should be shown when the item is selected
	 */
	private String textSelected;
	/*
	 * text should be shown when the item is NON selected
	 */
	private String textNonSelected;

	public GCheckBoxMenuItem(String text,
			boolean isHtml, App app) {

		// It's didn't work, becase when I clicked on the label of the checkbox,
		// the command of menuitem didn't run, so I added the html-string for
		// the MenuItem
		// in an another way (see below)
		// checkBox = new CheckBox(html);

		this.text = text;
		this.isHtml = isHtml;
		this.app = app;

		this.toggle = app.has(Feature.WHITEBOARD_APP)
				&& app.has(Feature.CONTEXT_MENU);


		itemPanel = new HorizontalPanel();

		checkBox = new CheckBox();
		itemPanel.add(checkBox);
		checkBox.setVisible(!isToggleMenu());
		setText(text);
	}



	public GCheckBoxMenuItem(String text, final ScheduledCommand cmd,
			boolean isHtml, App app) {
		this(text, isHtml, app);
		setCommand(cmd);
	}

	public GCheckBoxMenuItem(String text, String textSel, String textNonSel,
			final ScheduledCommand cmd, boolean isHtml, App app) {
		this(text, isHtml, app);
		textSelected = textSel;
		textNonSelected = textNonSel;
		setCommand(cmd);
	}

	public void setCommand(ScheduledCommand cmd) {
		menuItem = new MenuItem(itemPanel.toString(), true, cmd);

	}
	public void setSelected(boolean sel) {
		selected = sel;
		if (textSelected != null) {
			setText(sel ? textSelected : textNonSelected);
		} else if (isToggleMenu()) {
			itemPanel.clear();
			String txt = app.getLocalization()
					.getPlain(selected ? "On" : "Off");
			setText(text + " " + txt);
		} else {
			checkBox.setValue(sel);
		}

		menuItem.setHTML(itemPanel.toString());
	}

	private boolean isToggleMenu() {
		return toggle && !forceCheckbox;
	}

	/**
	 * 
	 * @return true if check box is checked
	 */
	public boolean isSelected() {
		return isToggleMenu() ? selected : checkBox.getValue();
	}

	public MenuItem getMenuItem() {
		return menuItem;
	}

	private void setText(String text) {
		if (isHtml) {
			itemPanel.add(new HTML(text));
		} else {
			itemPanel.add(new Label(text));
		}
	}

	public boolean isForceCheckbox() {
		return forceCheckbox;
	}

	public void setForceCheckbox(boolean forceCheckbox) {
		this.forceCheckbox = forceCheckbox;
		checkBox.setVisible(!isToggleMenu());
	}
}
