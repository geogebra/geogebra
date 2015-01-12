package geogebra.web.gui.menubar;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.MenuItemSeparator;
import com.google.gwt.user.client.ui.PopupPanel;

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

	/**
	 * As far as GWT is buggy in the implementation of this,
	 * it is necessary to have a method to correct that
	 * @return MenuItem
	 */
	@Override
	public MenuItem addItem(String itemtext, boolean textishtml,
	        final MenuBar submenupopup) {
		// this works, but it is still different from addItem in
		// not following mouse movement, only following mouse clicks, etc
		final MenuItem[] ait = new MenuItem[1];
		ait[0] = addItem(itemtext, textishtml, new ScheduledCommand() {
			public void execute() {
				if (ait[0] != null) {
					PopupPanel pp = new PopupPanel(true, false);
					submenupopup.addStyleName("subMenuLeftSide2");
					pp.addStyleName("subMenuLeftSidePopup");
					pp.add(submenupopup);
					int left = ait[0].getElement().getAbsoluteLeft();
					int top = ait[0].getElement().getAbsoluteTop();
					pp.setPopupPosition(left, top);
					pp.show();
				}
			}
		});
		return ait[0];
	}
}
