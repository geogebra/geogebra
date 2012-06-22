package geogebra.gui.menubar;

import geogebra.main.Application;

import java.awt.Event;
import java.awt.Toolkit;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

/**
 * The base class for the sub-menus.
 * 
 * @author Florian Sonner
 */
abstract class BaseMenu extends JMenu implements MenuListener {
	private static final long serialVersionUID = 2394839950861976156L;
	
	/**
	 * An instance of the application.
	 */
	protected Application app;
	
	protected boolean initialized = false;
	
	/**
	 * Construct a new sub-menu and assign the application attribute.
	 * 
	 * @param app
	 * @param text		The title of this menu
	 */
	public BaseMenu(Application app, String text)
	{
		super(text);
		
		this.app = app;
		
		// don't add any menu items until menu is opened
		// makes GeoGebra load faster
		addMenuListener(this);
	}
	
	/**
	 * Update this menu.
	 */
	public abstract void update();
	
	
	/**
	 * Set the shortcut for a menu item.
	 * 
	 * @param mi
	 * @param acc
	 */
	protected void setMenuShortCutAccelerator(JMenuItem mi, char acc) {
		KeyStroke ks = KeyStroke.getKeyStroke(acc, Toolkit.getDefaultToolkit()
				.getMenuShortcutKeyMask());
		mi.setAccelerator(ks);
	}

	/**
	 * Set the shortcut for a menu item which requires SHIFT to be pressed as well. 
	 * 
	 * @param mi
	 * @param acc
	 */
	protected void setMenuShortCutShiftAccelerator(JMenuItem mi, char acc) {
		KeyStroke ks = KeyStroke.getKeyStroke(acc, Toolkit.getDefaultToolkit()
				.getMenuShortcutKeyMask()
				+ Event.SHIFT_MASK);
		mi.setAccelerator(ks);
	}
	
	/**
	 * Set the shortcut for a menu item which requires SHIFT + ALT to be pressed as well. 
	 * 
	 * @param mi
	 * @param acc
	 */
	protected void setMenuShortCutShiftAltAccelerator(JMenuItem mi, char acc) {
		KeyStroke ks = KeyStroke.getKeyStroke(acc, Toolkit.getDefaultToolkit()
				.getMenuShortcutKeyMask()
				+ Event.SHIFT_MASK + Event.ALT_MASK);
		mi.setAccelerator(ks);
	}
	
	final public void menuDeselected(MenuEvent e) {
		// nothing to do here		
	}

	final public void menuCanceled(MenuEvent e) {
		// nothing to do here		
	}

	public void menuSelected(MenuEvent e) {
		//AbstractApplication.debug("Menu opening: "+getClass());
		if (getItemCount() == 0) {
			initialized = true;
			//AbstractApplication.debug("building menu");
			initActions();
			initItems();
			update();
		}
		
	}

	protected abstract void initActions();
	protected abstract void initItems();

}
