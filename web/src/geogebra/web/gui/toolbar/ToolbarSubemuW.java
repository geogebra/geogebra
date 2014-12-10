package geogebra.web.gui.toolbar;

import geogebra.html5.gui.util.ListItem;
import geogebra.html5.gui.util.UnorderedList;
import geogebra.html5.main.AppW;
import geogebra.web.css.GuiResources;
import geogebra.web.gui.NoDragImage;
import geogebra.web.gui.app.GGWToolBar;
import geogebra.web.gui.laf.GLookAndFeel;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
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

	private int order;
	
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
		this.addDomHandler(new MouseDownHandler(){

			@Override
            public void onMouseDown(MouseDownEvent event) {
	            event.stopPropagation();
	            
            }}, MouseDownEvent.getType());
		this.addDomHandler(new TouchStartHandler(){

			@Override
            public void onTouchStart(TouchStartEvent event) {
	            event.stopPropagation();
	            
            }}, TouchStartEvent.getType());
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
