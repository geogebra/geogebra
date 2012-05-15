package geogebra.web.gui.util;

import javax.swing.JSlider;

import geogebra.common.awt.Color;
import geogebra.common.main.AbstractApplication;
import geogebra.web.gui.util.SelectionTable;
import geogebra.web.main.Application;
import geogebra.web.awt.Dimension;
import geogebra.web.euclidian.EuclidianStyleBar;

import com.gargoylesoftware.htmlunit.javascript.host.EventHandler;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.CanvasElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ToggleButton;

public class PopupMenuButton extends Composite implements ChangeHandler {
	
	private MyToggleButton tb;
	private HorizontalPanel hp;
	private MyCanvasButton b;
	private int mode;
	private Object[] data;	
	private Application app;
	private ImageResource[] mArray;
	private Dimension dimension;
	private int modeIcon;
	private boolean keepVisible;
	private Color fgColor;
	private SelectionTable myTable;
	private boolean hasTable;
	private Slider mySlider;
	private int fontStyle = 0;
	
	public void setFontStyle(int fontStyle) {
		this.fontStyle = fontStyle;
	}
	
	private boolean isStandardButton = false;
	public void setStandardButton(boolean isStandardButton) {
		this.isStandardButton = isStandardButton;
	}

	private boolean isFixedIcon = false;


	private boolean isIniting = true;	
	protected boolean popupIsVisible;
	private Dimension iconSize;
	private ButtonPopupMenu myPopup;
	
	/*#***********************************
	/** Button constructors */

	/**
	 * @param app
	 */
	public PopupMenuButton(Application app){
		this( app, null, 0, 0, null, -1,  false,  false);
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
		this.tb = new MyToggleButton(GeoGebraIcon.createDownTriangleIcon());
		tb.setDimension(10,20);
		this.hp = new HorizontalPanel();
		this.b = new MyCanvasButton();
		hp.add(b);
		hp.add(tb);
		initWidget(hp);
		setStyleName("PopupMenuButton");
		
		// create the popup
		myPopup = new ButtonPopupMenu();
		//myPopup.setFocusable(false);
		//myPopup.setBackground(Color.WHITE);
		//myPopup.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.GRAY),
				//BorderFactory.createEmptyBorder(3,3,3,3)));



		// add a mouse listener to our button that triggers the popup		
		tb.addClickHandler(new ClickHandler() {
			
			public void onClick(ClickEvent event) {
				if (tb.isDown()) {
					myPopup.showRelativeTo(tb);
				} else {
					myPopup.hide();
				}
			}
		});
				
			


		// place text to the left of drop down icon
		//this.setHorizontalTextPosition(SwingConstants.LEFT); 
		//this.setHorizontalAlignment(SwingConstants.LEFT);


		// create selection table
		if(hasTable){			
			this.data = data;

			myTable = new SelectionTable(app,data,rows,columns,iconSize,mode);
			setSelectedIndex(0);	

			// add a mouse listener to handle table selection
			myTable.addClickHandler(new ClickHandler() {
				
				public void onClick(ClickEvent event) {
					handlePopupActionEvent();
				}
				
			});

			/*		
			// if displaying text only, then adjust the width 
			if(mode == SelectionTable.MODE_TEXT){
				 Dimension d = this.getPreferredSize();
				 d.width = myTable.getColumnWidth();
				 setMinimumSize(d); 
				 setMaximumSize(d); 
			 }
			 */	

			//myTable.setBackground(myPopup.getBackground());
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

	protected void handlePopupActionEvent() {
	   ((Canvas)b.getButton()).fireEvent(new ClickEvent(){});
	   myPopup.hide();
    }
	
	public void setKeepVisible(boolean keepVisible) {
		this.keepVisible = keepVisible;
	}

	public void update(Object[] geos) {
	    // TODO Auto-generated method stub
	    
    }
	
	public void setFgColor(Color fgColor) {
		this.fgColor = fgColor;
		if(myTable != null)
			myTable.setFgColor(fgColor);
		updateGUI();

	}

	private void updateGUI() {
	    // TODO Auto-generated method stub
	    
    }
	
	public void setSelectedIndex(Integer selectedIndex) {

		if(selectedIndex == null)
			selectedIndex = -1;

		myTable.setSelectedIndex(selectedIndex);
		updateGUI();
	}
	
	public int getSliderValue() {
		return mySlider.getValue();
	}
	
	public void setSliderValue(int value) {

		mySlider.removeChangeListener(this);
		mySlider.setValue(value);
		mySlider.addChangeListener(this);

		if(hasTable)
			myTable.setSliderValue(value);
		updateGUI();
	}
	
	public SelectionTable getMyTable() {
		return myTable;
	}

	public CanvasElement getButtonIcon() {
	    return tb.getIcon();
    }
	
	public int getSelectedIndex() {
		return myTable.getSelectedIndex();
	}

	public Slider getMySlider() {
		if(mySlider == null) {
			initSlider();
		}
	    return mySlider;
    }

	private void initSlider() {
	   mySlider = new Slider(0,100);
	   mySlider.setMajorTickSpacing(25);
	   mySlider.setMinorTickSpacing(5);
	   mySlider.setPaintTicks(false);
	   mySlider.setPaintLabels(false);
		//	mySlider.setSnapToTicks(true);
		
	   mySlider.addChangeListener(this);
		
		// set slider dimensions from css
		
		myPopup.getPanel().add(mySlider);
    }


	public void setIconSize(Dimension d) {
	    // TODO Auto-generated method stub
	    
    }

	public void setIcon(ImageResource ptCaptureIcon) {
		b.setIcon(ptCaptureIcon);
    }
	
	/**
	 * sets the tooTip strings for the menu selection table; 
	 * the toolTipArray should have a 1-1 correspondence with the data array 
	 * @param toolTipArray
	 */
	public void setToolTipArray(String[] toolTipArray){
		myTable.setToolTipArray(toolTipArray);
	}
	
	public CanvasElement getIcon() {
		return tb.getIcon();
	}


	public void onChange(ChangeEvent event) {
	  AbstractApplication.debug("slider changed");
    }


	public void addClickHandler(EuclidianStyleBar euclidianStyleBar) {
		b.addClickHandler(euclidianStyleBar);
    }


	protected void setIcon(CanvasElement ic) {
	    b.setIcon(ic);
    }
	
	public Object getPopupButton() {
		return tb.getButton();
	}


	public Object getActionButton() {
	    return b.getButton();
    }
	
	public Object getSelectedValue() {
		return myTable.getSelectedValue();
	}

}
