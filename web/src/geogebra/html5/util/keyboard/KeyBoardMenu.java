package geogebra.html5.util.keyboard;

import com.google.gwt.user.client.ui.FlexTable;

public class KeyBoardMenu extends FlexTable {

	public KeyBoardMenu() {
		setText(0, 0, "ABC");
		setText(0, 1, "123");
		setText(0, 2, "functions");

		addStyleName("SymbolTable");
	}

}
