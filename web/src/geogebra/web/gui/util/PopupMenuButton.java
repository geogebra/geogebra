package geogebra.web.gui.util;

import com.google.gwt.canvas.dom.client.ImageData;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.PopupPanel;

import geogebra.web.awt.Color;
import geogebra.web.awt.Dimension;
import geogebra.web.awt.Font;
import geogebra.web.awt.Point;
import geogebra.web.euclidian.EuclidianStyleBar;
import geogebra.web.gui.images.AppResources;
import geogebra.web.gui.images.AppResourcesConverter;
import geogebra.web.main.Application;


public class PopupMenuButton extends MyCJButton implements ChangeHandler {
	
	private int mode;
	private Object[] data;	
	private Application app;
	private PopupMenuButton thisButton;
	private ButtonPopupMenu myPopup;
	
	public PopupPanel getMyPopup() {
		return myPopup;
	}
	
	private Slider mySlider;
	
	private Color fgColor;
	private int fontStyle = 0;
	

	public void setFontStyle(int fontStyle) {
		this.fontStyle = fontStyle;
	}

	public void setFgColor(Color fgColor) {
		this.fgColor = fgColor;
		if(myTable != null)
			myTable.setFgColor(fgColor);
		updateGUI();

	}
	
	private SelectionTable myTable;
	public SelectionTable getMyTable() {
		return myTable;
	}

	private Dimension iconSize;

	public void setIconSize(Dimension iconSize) {
		this.iconSize = iconSize;
	}

	private boolean hasTable;
	
	// flag to determine if the popup should persist after a mouse click
	private boolean keepVisible = true;

	private boolean isDownwardPopup = true;

	public void setDownwardPopup(boolean isDownwardPopup) {
		this.isDownwardPopup = isDownwardPopup;
	}


	private boolean isStandardButton = false;
	public void setStandardButton(boolean isStandardButton) {
		this.isStandardButton = isStandardButton;
	}
	
	private boolean isFixedIcon = false;


	private boolean isIniting = true;	
	protected boolean popupIsVisible;
	
	/*#***********************************
	/** Button constructors */

	/**
	 * @param app
	 */
	public PopupMenuButton(Application app){
		this( app, null, -1, -1, null, -1,  false,  false);
	}
	
	/**
	 * @param app
	 * @param data
	 * @param rows
	 * @param columns
	 * @param iconSize
	 * @param mode
	 */
	public PopupMenuButton(Application app, Object[] data, Integer rows, Integer columns, Dimension iconSize, Integer mode){
		this( app, data, rows, columns, iconSize, mode,  true,  false);	
	}
	
	/**
	 * @param app
	 * @param data
	 * @param rows
	 * @param columns
	 * @param iconSize
	 * @param mode
	 * @param hasTable
	 * @param hasSlider
	 */
	public PopupMenuButton(Application app, Object[] data, Integer rows, Integer columns, Dimension iconSize, 
			Integer mode, final boolean hasTable, boolean hasSlider){
		super(); 
		this.app = app;
		this.hasTable = hasTable;		
		this.mode = mode;
		this.iconSize = iconSize;
		this.thisButton = this;

		//this.setFocusable(false);


		// create the popup
		myPopup = new ButtonPopupMenu();
		//myPopup.setFocusable(false);
		//myPopup.setBackground(Color.WHITE);
		//myPopup.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.GRAY),
		//		BorderFactory.createEmptyBorder(3,3,3,3)));



		// add a mouse listener to our button that triggers the popup		
		addClickHandler(new ClickHandler() {
			
			public void onClick(ClickEvent event) {
				if(!thisButton.isEnabled()) {
					return;
				}
				if(popupIsVisible == true && !myPopup.isVisible()){
					popupIsVisible = false;
					return;
				}
				
				if(prepareToShowPopup() == false) {
					return;
				}
				
				Point locButton = new Point(event.getX(),event.getY());
				
				// trigger popup 
				// default: trigger only when the mouse is over the right side of the button
				// if isStandardButton: pressing anywhere triggers the popup
				if( isStandardButton || event.getX() >= getWidth()-16 &&  event.getX() <= getWidth()) { 
					if(hasTable) {
						myTable.updateFonts();
					}
					if (EuclidianStyleBar.CURRENT_POP_UP != myPopup) {
						if (EuclidianStyleBar.CURRENT_POP_UP != null) {
							EuclidianStyleBar.CURRENT_POP_UP.hide();
						}
						EuclidianStyleBar.CURRENT_POP_UP = myPopup;
					}
					if(isDownwardPopup) {
						// popup appears below the button
						myPopup.showRelativeTo(getWidget());
					} else {
						// popup appears above the button
						myPopup.showRelativeTo(getWidget());
					}
				}

				popupIsVisible = myPopup.isShowing();
			}
		});
		
		addMouseEntered(new MouseOverHandler() {
			
			public void onMouseOver(MouseOverEvent event) {
				popupIsVisible = myPopup.isShowing();
			}
		});
		
		
		
		// create selection table
			if(hasTable){			
				this.data = data;

				myTable = new SelectionTable(app,data,rows,columns,iconSize,mode);
				setSelectedIndex(0);
				
				myTable.addClickHandler(new ClickHandler() {
					
					public void onClick(ClickEvent event) {
						handlePopupActionEvent();
					}
				});		
				
				myPopup.getPanel().add(myTable);
			}
			
			// create slider
			if(hasSlider)
				getMySlider();

			isIniting = false;


			if(mode == geogebra.common.gui.util.SelectionTable.MODE_TEXT && iconSize.getWidth() == -1){
				iconSize.setWidth(myTable.getColumnWidth()-4);
				iconSize.setHeight(myTable.getRowHeight()-4);	
			}
		
		
	}
	
	/**
	 * Pass a popup action event up to the button invoker. If the first button
	 * click triggered our popup (the click was in the triangle region), then we
	 * must pass action events from the popup to the invoker
	 */
	public void handlePopupActionEvent(){
		/*button.fireEvent(new ClickEvent(){
			
				public int getClientX() {
					return 0;
				}
				
				public int getClientY() {
					return 0;
				}
			
		})*/
		((EuclidianStyleBar)app.getEuclidianView1().getStyleBar()).fireActionPerformed(this);
		updateGUI();
		if(!keepVisible) {
			myPopup.hide();
		}
	}
	
	
	
	
	
	private void updateGUI(){

		if(isIniting) return;

		setIcon(getButtonIcon());

		if(hasTable){
			myTable.repaint();
		}

		//repaint();
	}
	
	//==============================================
		//    Icon Handling
		//==============================================



		public ImageData getButtonIcon(){

			ImageData icon = (ImageData) this.getIcon();
			if(isFixedIcon) return icon;


			// draw the icon for the current table selection
			if(hasTable){
				switch (mode){

				case geogebra.common.gui.util.SelectionTable.MODE_TEXT:
					// Strings are converted to icons. We don't use setText so that the button size can be controlled
					// regardless of the layout manager.

					icon = GeoGebraIcon.createStringIcon((String)data[getSelectedIndex()], (Font) app.getPlainFontCommon(), 
							false, false, true, iconSize, Color.BLACK, null);

					break;

				case geogebra.common.gui.util.SelectionTable.MODE_ICON:
				case geogebra.common.gui.util.SelectionTable.MODE_LATEX:
					icon  = myTable.getSelectedValue();
					break;

				default:
					icon = myTable.getDataIcon(data[getSelectedIndex()]);
				}
			}
			return icon;
	}
		
	/**
	 * Append a downward triangle image to the right hand side of an input icon.
	 */
	@Override
	public void setIcon(ImageData icon) {

		if(isFixedIcon) {			
			super.setIcon(icon);
			return;
		}

		if(iconSize == null) 
			if(icon != null)
				iconSize = new Dimension(icon.getWidth(), icon.getHeight());
			else
				iconSize = new Dimension(1,1);

		if(icon == null){
			//icon = GeoGebraIcon.createEmptyIcon(1, iconSize.height);
		}else{
			icon = GeoGebraIcon.ensureIconSize((ImageData) icon, iconSize);
		}

		// add a down_triangle image to the left of the icon
		if(icon != null) {
			super.setIcon(GeoGebraIcon.joinIcons((ImageData)icon, AppResources.INSTANCE.triangle_down()));
		} else {
			AppResourcesConverter.setIcon(AppResources.INSTANCE.triangle_down(), this);
			//must be done in callback super.setIcon(AppResources.INSTANCE.triangle_down());
		}
	}


	public void setFixedIcon(ImageData icon){
		isFixedIcon = true;
		setIcon(icon);
	}

	public void setIndex(int mode) {
		myTable.setSelectedIndex(mode);
	}

	public boolean prepareToShowPopup(){
		return true;
	}
	
	public void setSelectedIndex(Integer selectedIndex) {

		if(selectedIndex == null)
			selectedIndex = -1;

		myTable.setSelectedIndex(selectedIndex);
		updateGUI();
	}
	
	public void onChange(ChangeEvent event) {
		if(mySlider != null) {
			   setSliderValue(mySlider.getValue());
		}
		((EuclidianStyleBar)app.getEuclidianView1().getStyleBar()).fireActionPerformed(this);
		updateGUI();
	}
	
	public Slider getMySlider() {
		if(mySlider == null)
			initSlider();
		return mySlider;
	}
	
	private void initSlider() {
		mySlider = new Slider(0,100);
		mySlider.setMajorTickSpacing(25);
		mySlider.setMinorTickSpacing(5);
		mySlider.setPaintTicks(false);
		mySlider.setPaintLabels(false);
		//      mySlider.setSnapToTicks(true);
		               
		mySlider.addChangeListener(this);
		
		// set slider dimensions from css
		  
		myPopup.getPanel().add(mySlider);
	}
	
	public void setSliderValue(int value) {

		mySlider.removeChangeListener(this);
		mySlider.setValue(value);
		mySlider.addChangeListener(this);

		if(hasTable)
			myTable.setSliderValue(value);
		updateGUI();
	}
	public int getSelectedIndex() {
		return myTable.getSelectedIndex();
	}

	public void update(Object[] array) {
	    // will be overwritten from instances
    }
	
	public Object getSelectedValue() {
		return myTable.getSelectedValue();
	}
	
	public int getSliderValue() {
		return mySlider.getValue();
	}
	
	private HandlerRegistration actionListener;
	
	public void removeActionListener(EuclidianStyleBar euclidianStyleBar) {
		if (actionListener != null) {
			actionListener.removeHandler();
		}
	}

	public void setKeepVisible(boolean keepVisible) {
		this.keepVisible = keepVisible;
	}
	
	/**
	 * sets the tooTip strings for the menu selection table; 
	 * the toolTipArray should have a 1-1 correspondence with the data array 
	 * @param toolTipArray
	 */
	public void setToolTipArray(String[] toolTipArray){
		myTable.setToolTipArray(toolTipArray);
	}
	
	
	
}
