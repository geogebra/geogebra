package geogebra.web.gui.toolbar;

import geogebra.common.awt.GColor;
import geogebra.web.gui.app.GGWToolBar;
import geogebra.web.javax.swing.GPopupMenuW;
import geogebra.web.main.AppW;

import java.util.ArrayList;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;

public class ModeToggleMenu extends MenuBar{

	private static final long serialVersionUID = 1L;
	ModeToggleButtonGroup bg;
	//private MyJToggleButton tbutton, mouseOverButton;
	private MyJToggleButton tbutton;
	private GPopupMenuW popMenu;
	private ArrayList<MenuItem> menuItemList;

//	private ActionListener popupMenuItemListener;
	private AppW app;
	int size;
	ToolBarW toolbar;
	private AbsolutePanel imagePanel;

	final static GColor bgColor = GColor.white;

	public ModeToggleMenu(AppW app, ToolBarW toolbar,
			ModeToggleButtonGroup bg) {
		super(true);
		setFocusOnHoverEnabled(false);
		this.app = app;
		this.bg = bg;
		this.toolbar = toolbar;
		tbutton = new MyJToggleButton(this);
		tbutton.getElement().setAttribute("isSelected", "false");
		tbutton.addStyleName("toolbar_item");

		
		
//		tbutton.setAlignmentY(BOTTOM_ALIGNMENT);
//		addItem(tbutton);

//		popMenu = new GPopupMenuW();
//		popMenu.setBackground(bgColor);
		menuItemList = new ArrayList<MenuItem>();
		//popupMenuItemListener = new MenuItemListener();
		size = 0;
	}

	public int getToolsCount() {
		return size;
	}

	public boolean selectMode(int mode) {
		String modeText = mode + "";

		for (int i = 0; i < size; i++) {
			MenuItem mi = menuItemList.get(i);
			// found item for mode?
			if (mi.getElement().getAttribute("mode").equals(modeText)) {
				selectMenuItem(mi);
				return true;
			}
		}
		tbutton.getElement().setAttribute("isSelected", "false");
		return false;
	}

	public int getFirstMode() {
		if (menuItemList == null || menuItemList.size() == 0) {
			return -1;
		}
		MenuItem mi = menuItemList.get(0);
		return Integer.parseInt(mi.getElement().getAttribute("mode"));
	}

	/*
	 * This method has the same functionality as the 
	 * geogebra.gui.toolbar.ModeToggleMenu.selectItem(JMenuItem mi)
	 */
	void selectMenuItem(MenuItem mi) {
		String miMode = mi.getElement().getAttribute("mode");
		// check if the menu item is already selected
		if (tbutton.getElement().getAttribute("isSelected").equals(true)
				&& tbutton.getElement().getAttribute("mode").equals(miMode)) {
			return;
		}
		

		
//		tbutton.setIcon(mi.getIcon());
//		tbutton.setText(app.getToolName(Integer.parseInt(miMode)));
		tbutton.getElement().setAttribute("mode",miMode);
		//tbutton.setHTML(GGWToolBar.getImageHtml(Integer.parseInt(miMode)));
		tbutton.updateCanvas(Integer.parseInt(miMode));
//		tbutton.setText(miMode);
		
		ArrayList<ModeToggleMenu> modeToggleMenus = toolbar.getModeToggleMenus();
		for (int i = 0; i < modeToggleMenus.size(); i++) {
			ModeToggleMenu mtm = modeToggleMenus.get(i);
			if (mtm != this) {
				mtm.tbutton.getElement().setAttribute("isSelected","false");
			}
		}
		tbutton.getElement().setAttribute("isSelected","true");
		tbutton.setToolTipText(app.getToolTooltipHTML(Integer.parseInt(miMode)));
		
		// tbutton.requestFocus();*/
		
		//temporary - until we have only one toolbar
		setMode(Integer.parseInt(miMode));
	}

	public void addMode(int mode) {
		// add menu item to popup menu
		Command tempCommand = null;
		MenuItem mi = new MenuItem(app.getToolName(mode), tempCommand);
		//mi.setFont(app.getPlainFont());
		//mi.setBackground(bgColor);

		// tool name as text
		//mi.setText(app.getToolName(mode));

		//Icon icon = app.getModeIcon(mode);
		String actionText = Integer.toString(mode);
		//mi.setIcon(icon);
		mi.getElement().setAttribute("mode", actionText);
		//mi.setStyleName("toolbar_menuitem");
		//mi.addActionListener(popupMenuItemListener);

		//popMenu.add(mi);
		menuItemList.add(mi);
		size++;

		if (size == 1) {
			// init tbutton
			tbutton.getElement().setAttribute("mode", actionText);
			//tbutton.setHTML(getImagePanelHtml(mode));
			tbutton.getElement().appendChild(tbutton.getCanvas(mode).getElement());
			
			this.getElement().setAttribute("mode", actionText);
			//this.setTitle(app.getToolName(mode));
			// tooltip: tool name and tool help
			tbutton.setToolTipText(app.getToolTooltipHTML(mode));
			//tbutton.setText(app.getToolName(mode));
		} else if (size == 2){
			tbutton.getElement().appendChild(tbutton.getCanvasForRedTriangle().getElement());
		}
	}
	


	
	/**
	 * Sets tbutton field.
	 * @param button the new value of tbutton
	 */
//	public void setButton(MenuItem button){
//		tbutton = button;
//	}
	
	/**
	 * Gets tbutton field.
	 * @return with tbutton
	 */
	public MyJToggleButton getButton(){
		return tbutton;		
	}
	

	public void setMode(int mode) {
		app.setMode(mode);
	}

	class MyJToggleButton extends MenuItem{

		private static final long serialVersionUID = 1L;
		
		Canvas canvas;
		Canvas canvasForRedTriangle;
		boolean popupTriangleHighlighting = false;
		CssColor arrowColor = CssColor.make("rgba(0, 0, 0, 130)");

		private ImageElement imgElement;

		MyJToggleButton(ModeToggleMenu menu) {
			super("", true, menu);
			initCanvasForRedTriangle();
			this.addNativeToolTipHandler(this.getElement(), this);
		}

		String toolTipText;
		public void setToolTipText(String string) {
			toolTipText = string;
        }
		
		public void showToolTip(){
			if (toolbar.hasPopupOpen()) return;
			
			app.getToolTipManager().setEnableDelay(false);
			app.getToolTipManager().showToolTip(this.getElement(), toolTipText);
			app.getToolTipManager().setEnableDelay(true);
		}
		
		public void hideToolTip(){
			app.getToolTipManager().hideToolTip();
		}
		
		private void initCanvasForRedTriangle(){
			canvasForRedTriangle = Canvas.createIfSupported();
			canvasForRedTriangle.setWidth("40px");
			canvasForRedTriangle.setHeight("8px");
			canvasForRedTriangle.setCoordinateSpaceWidth(40);
			canvasForRedTriangle.setCoordinateSpaceHeight(8);
			canvasForRedTriangle.setVisible(true);
			canvasForRedTriangle.addStyleName("red_triangle");
			attachNativeHandler(canvasForRedTriangle.getElement());
			
		}
		
		public void attachNativeHandler(Element element) {
			addNativeHandler(element, this);
		}

		private native void addNativeToolTipHandler(Element element, ModeToggleMenu.MyJToggleButton bt) /*-{
		element.addEventListener("mouseout",function() {
			bt.@geogebra.web.gui.toolbar.ModeToggleMenu.MyJToggleButton::hideToolTip()();
		});
		element.addEventListener("mouseover",function() {
			bt.@geogebra.web.gui.toolbar.ModeToggleMenu.MyJToggleButton::showToolTip()();
		});
		
		
		}-*/;
		
		private native void addNativeHandler(Element element, ModeToggleMenu.MyJToggleButton bt) /*-{
			element.addEventListener("mouseout",function() {
				bt.@geogebra.web.gui.toolbar.ModeToggleMenu.MyJToggleButton::triangleMouseOut()();
			});
			element.addEventListener("mouseover",function() {
				bt.@geogebra.web.gui.toolbar.ModeToggleMenu.MyJToggleButton::triangleMouseOver()();
			});
			
			
		}-*/;
		
		public void triangleMouseOver(){
			popupTriangleHighlighting = true;
			drawRedTriangle(true);
		}
		
		public void triangleMouseOut(){
			popupTriangleHighlighting = false;
			drawRedTriangle(false);
		}
		
		Canvas getCanvasForRedTriangle(){
			drawRedTriangle(false);
			return canvasForRedTriangle;
		}
	
		private void drawRedTriangle(boolean selected){
			
			Context2d context = canvasForRedTriangle.getContext2d();
			context.clearRect(0, 0, canvasForRedTriangle.getOffsetWidth(), canvasForRedTriangle.getOffsetHeight());

			//red triangle for popup menu
			//color-settings for selected and unselected arrow
			if (popupTriangleHighlighting) {
				context.setStrokeStyle(CssColor.make("black"));
				context.setFillStyle(CssColor.make("red"));
			} else {
				context.setStrokeStyle(arrowColor);
				if(selected){
					context.setFillStyle(CssColor.make("white"));
				}else{
					context.setFillStyle(CssColor.make("#e7e7e7"));
				}
			}
			
			context.setLineWidth(1);	
			context.beginPath();
			context.moveTo(32.5,1.5);
			context.lineTo(39.5,1.5);
			context.lineTo(36,6.5);
			context.closePath();
			context.stroke();
			context.fill();	
			
		}
		
		public boolean isTriangleHighlighted(){
			return popupTriangleHighlighting;
		}
		
		
		Canvas getCanvas(int mode){
			
			canvas = Canvas.createIfSupported();

			// canvas init.
			canvas.setWidth("32px");
			canvas.setHeight("32px");
			canvas.setCoordinateSpaceWidth(32);
			canvas.setCoordinateSpaceHeight(32);
			canvas.addStyleName("toolbar_icon");
		
			updateCanvas(mode);
			
			canvas.setVisible(true);
			return canvas;
			
		}
		
		private void updateCanvas(int mode) {
			final Image image = new Image(GGWToolBar.getImageURL(mode));
			ImageElement imageElement = (ImageElement) image.getElement().cast();
			imgElement = imageElement;
			attachNativeLoadHandler(imageElement);
		}
		

		/**
		 * @param img
		 */
		public void attachNativeLoadHandler(ImageElement img) {
			addNativeLoadHandler(img,this);
		}

		private native void addNativeLoadHandler(ImageElement img, ModeToggleMenu.MyJToggleButton bt) /*-{
			img.addEventListener("load",function() {
				bt.@geogebra.web.gui.toolbar.ModeToggleMenu.MyJToggleButton::drawOnCanvas(Lcom/google/gwt/dom/client/ImageElement;Z)(img, false);
			});
		}-*/;
		
		/**
		 * @param imageElement - TODO: remove
		 * @param selected
		 */
		public void drawOnCanvas(ImageElement imageElement, boolean selected) {

			Context2d context = canvas.getContext2d();
			//context.clearRect(0, 0, canvas.getOffsetWidth(), canvas.getOffsetHeight());
			context.drawImage(imageElement, 0, 0);

		}
		
		public void drawOnCanvas(boolean selected){
			drawOnCanvas(imgElement, selected);
		}

	}

}
