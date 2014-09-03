package geogebra.web.gui.toolbar;

import geogebra.html5.gui.util.ListItem;
import geogebra.html5.gui.util.UnorderedList;
import geogebra.html5.main.AppW;
import geogebra.web.css.GuiResources;
import geogebra.web.gui.app.GGWToolBar;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

/**
 * Submenu for ModeToggleMenu. This extension is needed so that
 * this FlowPanel can act as a popup.
 * @author bencze
 */
public class ToolbarSubemuW extends FlowPanel {
	
	/**
	 * Application
	 */
	private AppW app;
	
	/**
	 * Panel containing the arrow.
	 */
	private FlowPanel submenuArrow;
	
	/**
	 * Item list containing the submenu items.
	 */
	private UnorderedList itemList;
	
	/**
	 * Creates the sub menu, sets the stylename, and creates the
	 * child elements.
	 */
	public ToolbarSubemuW(AppW app) {
		this.app = app;
	    setStyleName("toolbar_submenu");
	    initGui();
    }
	
	private void initGui() {
		submenuArrow = new FlowPanel();
		submenuArrow.add(new Image(GuiResources.INSTANCE.arrow_submenu_up()));
		submenuArrow.setStyleName("submenuArrow");
		add(submenuArrow);
		
		itemList = new UnorderedList();
		itemList.setStyleName("submenuContent");
		add(itemList);
	}

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		setStyleName("visible", visible);
	}
	
	@Override
	public boolean isVisible() {
	    return getElement().hasClassName("visible");
	}
	
	/**
	 * Creates a list item, adds an image and a label with the specified mode,
	 * and adds it to the submenu list.
	 * @param addMode the mode to be used with this item
	 * @return the newly created {@code ListItem}
	 */
	public ListItem addItem(int addMode) {
		ListItem listItem = new ListItem();
		
		Image image = new Image(((GGWToolBar)app.getToolbar()).getImageURL(addMode));
		Label label = new Label(app.getToolName(addMode));
		listItem.add(image);
		listItem.add(label);
		listItem.getElement().setAttribute("mode", addMode + "");
		itemList.add(listItem);
		
		return listItem;
	}
	
	/**
	 * @return Item list containing the menu items
	 */
	public UnorderedList getItemList() {
		return itemList;
	}
}
