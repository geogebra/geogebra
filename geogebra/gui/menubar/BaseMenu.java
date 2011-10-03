package geogebra.gui.menubar;

import geogebra.main.Application;

import java.awt.Event;
import java.awt.Toolkit;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

/**
 * The base class for the sub-menus.
 * 
 * @author Florian Sonner
 */
abstract class BaseMenu extends JMenu {
	private static final long serialVersionUID = 2394839950861976156L;
	
	/**
	 * An instance of the application.
	 */
	protected Application app;
	
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
}
