package org.geogebra.keyboard.web;

import java.util.ArrayList;

import org.geogebra.keyboard.base.Keyboard;

import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 *
 */
public class KeyPanelBase extends VerticalPanel {

	private ArrayList<HorizontalPanel> rows;
	private ArrayList<KeyBoardButtonBase> buttons;
	private Keyboard layout;

	/**
	 */
	public KeyPanelBase(Keyboard layout) {
		rows = new ArrayList<HorizontalPanel>();
		buttons = new ArrayList<KeyBoardButtonBase>();
		this.layout = layout;
	}


	public KeyPanelBase() {
		this(null);
	}

	/**
	 * adds the given button to the row with given index.
	 * 
	 * @param index
	 *            int
	 * @param button
	 *            {@link KeyBoardButtonBase}
	 */
	public void addToRow(int index, KeyBoardButtonBase button) {
		if (rows.size() <= index) {
			HorizontalPanel newRow = new HorizontalPanel();
			newRow.addStyleName("KeyPanelRow");
			rows.add(newRow);
			this.add(newRow);
		}
		rows.get(index).add(button);
		buttons.add(button);
	}

	public void reset(Keyboard layout) {
		clear();
		this.layout = layout;
		rows.clear();
		buttons.clear();
	}

	/**
	 * @return all {@link KeyBoardButtonBase buttons} of this keyPanel
	 */
	public ArrayList<KeyBoardButtonBase> getButtons() {
		return this.buttons;
	}

	/**
	 * @return all {@link HorizontalPanel rows} of this keyPanel
	 */
	public ArrayList<HorizontalPanel> getRows() {
		return this.rows;
	}

	public Keyboard getLayout() {
		return layout;
	}
}
