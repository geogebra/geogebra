package org.geogebra.desktop.gui.menubar;

import java.awt.Event;
import java.awt.Toolkit;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

import org.geogebra.desktop.main.AppD;
import org.geogebra.desktop.main.LocalizationD;

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
	protected AppD app;

	protected boolean initialized = false;
	/** localization */
	protected LocalizationD loc;

	/**
	 * Construct a new sub-menu and assign the application attribute.
	 * 
	 * @param app
	 * @param key
	 *            The title of this menu
	 */
	public BaseMenu(AppD app, String key) {
		super(app.getLocalization().getMenu(key));

		this.app = app;
		this.loc = app.getLocalization();

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
		KeyStroke ks = KeyStroke.getKeyStroke(acc,
				Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
		mi.setAccelerator(ks);
	}

	/**
	 * Set the shortcut for a menu item which requires SHIFT to be pressed as
	 * well.
	 * 
	 * @param mi
	 * @param acc
	 */
	protected void setMenuShortCutShiftAccelerator(JMenuItem mi, char acc) {
		KeyStroke ks = KeyStroke.getKeyStroke(acc,
				Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()
						+ Event.SHIFT_MASK);
		mi.setAccelerator(ks);
	}

	/**
	 * Set the shortcut for a menu item which requires SHIFT + ALT to be pressed
	 * as well.
	 * 
	 * @param mi
	 * @param acc
	 */
	protected void setMenuShortCutShiftAltAccelerator(JMenuItem mi, char acc) {
		KeyStroke ks = KeyStroke.getKeyStroke(acc,
				Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()
						+ Event.SHIFT_MASK + Event.ALT_MASK);
		mi.setAccelerator(ks);
	}

	@Override
	final public void menuDeselected(MenuEvent e) {
		// nothing to do here
	}

	@Override
	final public void menuCanceled(MenuEvent e) {
		// nothing to do here
	}

	@Override
	public void menuSelected(MenuEvent e) {

		if (getItemCount() == 0) {
			// UIManager.put("Menu.acceleratorFont", app.getPlainFont());
			UIManager.put("MenuItem.acceleratorFont", app.getPlainFont());
			initialized = true;
			// AbstractApplication.debug("building menu");
			initActions();
			initItems();
			update();
			GeoGebraMenuBar.setMenuFontRecursive(this, app.getPlainFont());
		}
	}

	protected abstract void initActions();

	protected abstract void initItems();

}
