package org.geogebra.web.web.gui.toolbar;

import org.geogebra.common.euclidian.event.PointerEventType;
import org.geogebra.web.html5.gui.NoDragImage;
import org.geogebra.web.html5.gui.util.ClickStartHandler;
import org.geogebra.web.html5.gui.util.ListItem;
import org.geogebra.web.html5.gui.util.UnorderedList;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.web.css.GuiResources;
import org.geogebra.web.web.gui.app.GGWToolBar;
import org.geogebra.web.web.gui.laf.GLookAndFeel;

import com.google.gwt.dom.client.Style.Unit;
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
	protected AppW app;
	
	/**
	 * Panel containing the arrow.
	 */
	private FlowPanel submenuArrow;
	
	/**
	 * Item list containing the submenu items.
	 */
	protected UnorderedList itemList;

	private double maxHeight;

	int order;
	
	/**
	 * Creates the sub menu, sets the stylename, and creates the
	 * child elements.
	 */
	public ToolbarSubemuW(AppW app, int order) {
		this.app = app;
		this.order = order;
		this.maxHeight = app.getHeight() - 40;
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
		setMaxHeight((int)app.getHeight() - GLookAndFeel.TOOLBAR_OFFSET);
		add(itemList);
		//catch the events to make sure scrollbar is usable when present
		ClickStartHandler.init(this, new ClickStartHandler(false, true) {
			@Override
			public void onClickStart(int x, int y, PointerEventType type) {
				// nothing to do here
			}
		});
	}

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		setStyleName("visible", visible);
		if(visible){
			app.getGuiManager().invokeLater(new Runnable(){

				@Override
                public void run() {
					if(itemList.getOffsetWidth() + ToolbarSubemuW.this.order * 45 > app.getWidth()){
						itemList.getElement().getStyle().setLeft(app.getWidth() - 45 * ToolbarSubemuW.this.order - itemList.getOffsetWidth() -10, Unit.PX);
					}
                }});
		}
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
		ListItem listItem = createListItem(addMode);
		listItem.getElement().setAttribute("mode", addMode + "");
		itemList.add(listItem);
		
		return listItem;
	}

	protected ListItem createListItem(int mode) {
		ListItem listItem = new ListItem();
		
		Image image = createImage(mode);
		Label label = createLabel(mode);
		listItem.add(image);
		listItem.add(label);
		return listItem;
	}

	protected Image createImage(int mode) {
		return new NoDragImage(((GGWToolBar)app.getToolbar()).getImageURL(mode), 32);
	}

	protected Label createLabel(int mode) {
		return new Label(app.getToolName(mode));
	}
	
	/**
	 * @return Item list containing the menu items
	 */
	public UnorderedList getItemList() {
		return itemList;
	}

	public void setMaxHeight(int d) {
		itemList.getElement().getStyle().setProperty("maxHeight", d+"px");
    }
}
