package org.geogebra.desktop.gui.menubar;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.euclidian3D.Input3D;
import org.geogebra.common.main.App;
import org.geogebra.common.main.Feature;
import org.geogebra.desktop.gui.util.HelpAction;
import org.geogebra.desktop.main.AppD;
import org.geogebra.desktop.util.GuiResourcesD;

/**
 * The "Help" menu.
 */
class HelpMenuD extends BaseMenu {
	private static final long serialVersionUID = 1125756553396593316L;

	private AbstractAction helpAction, tutorialAction, input3DTutorialAction,
			forumAction, infoAction,
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

		add(tutorialAction);
		if (input3DTutorialAction != null) {
			add(input3DTutorialAction);
		}
		JMenuItem mi = add(helpAction);
		KeyStroke ks = KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0);
		mi.setAccelerator(ks);

		add(forumAction);

		addSeparator();

		add(reportBugAction);

		addSeparator();


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
			helpAction = new HelpAction(app,
					app.getMenuIcon(GuiResourcesD.HELP),
					app.getMenu("Manual"), App.WIKI_MANUAL);

			tutorialAction = new HelpAction(app, null,
					app.getMenu("Tutorials"), App.WIKI_TUTORIAL);

			if (app.has(Feature.REALSENSE)
					&& app.getInput3DType().equals(Input3D.PREFS_REALSENSE)) {
				input3DTutorialAction = new AbstractAction(
						app.getMenu("RealSense.Tutorial"), app.getEmptyIcon()) {
					private static final long serialVersionUID = 1L;

					public void actionPerformed(ActionEvent e) {
						app.getGuiManager().showURLinBrowser(
								App.REALSENSE_TUTORIAL);
					}
				};
			} else {
				input3DTutorialAction = null;
			}

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
					app.getEmptyIcon()) {
				private static final long serialVersionUID = 1L;

				public void actionPerformed(ActionEvent e) {
					app.getGuiManager().showURLinBrowser(
							GeoGebraConstants.FORUM_URL);
				}
			};

			infoAction = new AbstractAction(app.getMenu("AboutLicense"),
					app.getMenuIcon(GuiResourcesD.INFO)) {
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
				app.getMenuIcon(GuiResourcesD.HELP));
		infoAction.putValue(AbstractAction.SMALL_ICON,
				app.getMenuIcon(GuiResourcesD.INFO));

	}

}
