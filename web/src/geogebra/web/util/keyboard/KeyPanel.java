package geogebra.web.util.keyboard;

import java.util.ArrayList;

import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 *
 */
public class KeyPanel extends VerticalPanel {

	private ArrayList<HorizontalPanel> rows;

	/**
	 */
	public KeyPanel() {
		rows = new ArrayList<HorizontalPanel>();
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
	}

	/**
	 * @return a list of all rows
	 */
	public ArrayList<HorizontalPanel> getRows() {
		return this.rows;
	}
}
