package org.geogebra.desktop.gui.menubar;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.main.App;
import org.geogebra.desktop.gui.util.HelpAction;
import org.geogebra.desktop.main.AppD;

/**
 * The "Help" menu.
 */
class HelpMenuD extends BaseMenu {
	private static final long serialVersionUID = 1125756553396593316L;

	private AbstractAction helpAction, tutorialAction, forumAction, infoAction,
			reportBugAction;

	/**
	 * Creates new help menu
	 * 
	 * @param app
	 */
	public HelpMenuD(AppD app) {
		super(app, app.getMenu("Help"));

		// items are added to the menu when it's opened, see BaseMenu:
		// addMenuListener(this);
	}

	/**
	 * Initialize the menu items.
	 */
	@Override
	protected void initItems() {

		removeAll();

		// doesn't work in unsigned applets
		if (AppD.hasFullPermissions()) {
			add(tutorialAction);
			JMenuItem mi = add(helpAction);
			KeyStroke ks = KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0);
			mi.setAccelerator(ks);

			add(forumAction);

			addSeparator();

			add(reportBugAction);

			addSeparator();
		}

		add(infoAction);

		// support for right-to-left languages
		app.setComponentOrientation(this);

	}

	/**
	 * Initialize the actions.
	 */
	@Override
	protected void initActions() {
		if (helpAction == null) {
			helpAction = new HelpAction(app, app.getMenuIcon("help.png"),
					app.getMenu("Manual"), App.WIKI_MANUAL);

			tutorialAction = new HelpAction(app, null,
					app.getMenu("Tutorials"), App.WIKI_TUTORIAL);

			reportBugAction = new AbstractAction(app.getMenu("ReportBug"),
					app.getEmptyIcon()) {
				private static final long serialVersionUID = 1L;

				public void actionPerformed(ActionEvent e) {
					GeoGebraMenuBar.copyDebugInfoToClipboard(app);
					app.getGuiManager().showURLinBrowser(
							GeoGebraConstants.GEOGEBRA_REPORT_BUG_DESKTOP);
				}
			};

			forumAction = new AbstractAction(app.getMenu("GeoGebraForum"),
					app.getMenuIcon("forum.gif")) {
				private static final long serialVersionUID = 1L;

				public void actionPerformed(ActionEvent e) {
					app.getGuiManager().showURLinBrowser(
							GeoGebraConstants.FORUM_URL);
				}
			};

			infoAction = new AbstractAction(app.getMenu("AboutLicense"),
					app.getMenuIcon("info.gif")) {
				private static final long serialVersionUID = 1L;

				public void actionPerformed(ActionEvent e) {
					GeoGebraMenuBar.showAboutDialog(app);
				}
			};

		}
	}

	@Override
	public void update() {
		if (helpAction == null) {
			return;
		}
		helpAction.putValue(AbstractAction.SMALL_ICON,
				app.getMenuIcon("help.png"));
		forumAction.putValue(AbstractAction.SMALL_ICON,
				app.getMenuIcon("forum.png"));
		infoAction.putValue(AbstractAction.SMALL_ICON,
				app.getMenuIcon("info.gif"));

	}

}
