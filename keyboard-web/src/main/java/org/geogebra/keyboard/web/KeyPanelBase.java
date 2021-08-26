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
	 * @param keyBoardLayout
	 *            {@link Keyboard}
	 */
	public KeyPanelBase(Keyboard keyBoardLayout) {
		rows = new ArrayList<>();
		buttons = new ArrayList<>();
		this.layout = keyBoardLayout;
	}

	/**
	 * empty constructor
	 */
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
			add(newRow);
		}
		rows.get(index).add(button);
		buttons.add(button);
	}

	/**
	 * @param keyboardLayout
	 *            {@link Keyboard}
	 */
	public void reset(Keyboard keyboardLayout) {
		clear();
		this.layout = keyboardLayout;
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

	/**
	 * @return keyboard layout
	 */
	public Keyboard getLayout() {
		return layout;
	}
}
