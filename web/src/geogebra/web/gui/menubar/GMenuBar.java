package geogebra.web.gui.menubar;

import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItemSeparator;

public class GMenuBar extends MenuBar{
	private int separators = 0;

	public GMenuBar(boolean vertical) {
	    super(vertical);
    }

	public GMenuBar(boolean vertical, MenuResources menuResources) {
	    super(vertical, menuResources);
    }

	public boolean isFirstItemSelected(){
		return this.getItemIndex(this.getSelectedItem()) == 0;
	}

	public boolean isLastItemSelected() {
		return this.getItemIndex(this.getSelectedItem()) == this.getItems().size() + separators - 1;
		
    }
	
	public MenuItemSeparator addSeparator(){
		this.separators  ++;
		return super.addSeparator();
	}
}
