package geogebra.web.util.keyboard;

import java.util.ArrayList;

import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 *
 */
public class KeyPanel extends VerticalPanel {

	private ArrayList<HorizontalPanel> rows;
	private ArrayList<KeyBoardButton> buttons;

	/**
	 */
	public KeyPanel() {
		rows = new ArrayList<HorizontalPanel>();
		buttons = new ArrayList<KeyBoardButton>();
	}

	/**
	 * adds the given button to the row with given index.
	 * 
	 * @param index
	 *            int
	 * @param button
	 *            {@link KeyBoardButton}
	 */
	public void addToRow(int index, KeyBoardButton button) {
		if (rows.size() <= index) {
			HorizontalPanel newRow = new HorizontalPanel();
			newRow.addStyleName("KeyPanelRow");
			rows.add(newRow);
			this.add(newRow);
		}
		rows.get(index).add(button);
		buttons.add(button);
	}

	/**
	 * @return all {@link KeyBoardButton buttons} of this keyPanel
	 */
	public ArrayList<KeyBoardButton> getButtons() {
		return this.buttons;
	}
}
