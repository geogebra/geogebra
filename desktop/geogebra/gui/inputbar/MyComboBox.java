package geogebra.gui.inputbar;

import javax.swing.JComboBox;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

/**
 * JComboBox that allows using setPrototypeDisplayValue() while
 * keeping the correct (usually larger) width of its popup menu.
 * 
 * @author Markus Hohenwarter
 * @see "http://forums.sun.com/thread.jspa?threadID=570675"
 */
public class MyComboBox extends JComboBox implements PopupMenuListener {
	
	private static final long serialVersionUID = 1L;
	
	private int popupWidth = 400;

	public MyComboBox() {
		addPopupMenuListener(this);
	}
	
	/**
	 * Call this method after you have filled this combo box
	 * in order to keep the correct width of the popup menu.
	 */
	public void setPrototypeDisplayValue(String str) {
		popupWidth = getPreferredSize().width + 10;
		super.setPrototypeDisplayValue(str);
	}

	//Popup state to prevent loop
	boolean stateCmb = false;
	 
	//Extend JComboBox's length and reset it
	public void popupMenuWillBecomeVisible(PopupMenuEvent e)
	{
	  JComboBox cmb = (JComboBox)e.getSource();
	  int oldWidth = cmb.getPreferredSize().width;
	  
	  //Extend JComboBox
	  cmb.setSize(popupWidth, cmb.getHeight());
	
	  //If it pops up now JPopupMenu will still be short
	  //Fire popupMenuCanceled...
	  if(!stateCmb)
	    cmb.firePopupMenuCanceled();
	  
	  //Reset JComboBox and state
	  stateCmb = false;
	  cmb.setSize(oldWidth, cmb.getHeight());
	}
			
	//Show extended JPopupMenu
	public void popupMenuCanceled(PopupMenuEvent e)
	{
	  JComboBox cmb = (JComboBox)e.getSource();
	  stateCmb = true;
	  //JPopupMenu is long now, so repop
	  cmb.showPopup();
	}

	public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
		stateCmb = false;
	}
}
