package geogebra.web.gui.menubar;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
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
		final Object[] ait = new Object[2];
		ait[1] = null;
		ait[0] = addItem(itemtext, textishtml, new ScheduledCommand() {
			public void execute() {
				if (ait[0] != null) {
					if (ait[1] == null) {
						// popuppanel still not present
						final PopupPanel pp = new PopupPanel(true, false);
						submenupopup.addStyleName("subMenuLeftSide2");
						pp.addStyleName("subMenuLeftSidePopup");
						pp.add(submenupopup);
						int left = ((MenuItem) ait[0]).getElement()
						        .getAbsoluteLeft();
						int top = ((MenuItem) ait[0]).getElement()
						        .getAbsoluteTop();
						pp.setPopupPosition(left, top);

						// TODO: maybe this solution is imperfect?
						// for cases other than RadioButtonMenuBarW submenus
						// but somehow the popup shall be hidden if
						// clicked on it, or on its triggering element
						pp.addDomHandler(new ClickHandler() {
							public void onClick(ClickEvent ce) {
								// on presuming that clicks will always trigger
								// something

								// TODO: better if
								// if ((submenupopup instanceof
								// RadioButtonMenuBarW)
								// && ((RadioButtonMenuBarW) submenupopup)
								// .getSelectedItemPublic() != null) {
									ait[1] = null;
									pp.hide();
								// }
							}
						}, ClickEvent.getType());

						// TODO: more difficult to solve autoOpen
						// sadly, the following thing can only work together
						// with submenus created by the other method, in theory
						// parentMenu.setAutoOpen(true);

						ait[1] = pp;
						pp.show();
					} else {
						// if popuppanel is present, it will be hidden
						// due to autoHide, and no new one is created instead
						// let's forget about it
						ait[1] = null;
					}
				}
			}
		});
		return (MenuItem) ait[0];
	}
}
