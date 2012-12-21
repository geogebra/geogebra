package geogebra.gui.menubar;

import geogebra.common.GeoGebraConstants;
import geogebra.common.main.App;
import geogebra.gui.util.HelpAction;
import geogebra.main.AppD;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

/**
 * The "Help" menu.
 */
class HelpMenu extends BaseMenu {
	private static final long serialVersionUID = 1125756553396593316L;

	private AbstractAction
		helpAction,
		tutorialAction,
		geogebratubeAction,
		infoAction,
		reportBugAction
	;
	/**
	 * Creates new help menu
	 * @param app
	 */
	public HelpMenu(AppD app) {
		super(app, app.getMenu("Help"));
		
		// items are added to the menu when it's opened, see BaseMenu: addMenuListener(this);
	}
	
	/**
	 * Initialize the menu items.
	 */
	@Override
	protected void initItems()
	{

		removeAll();

		// doesn't work in unsigned applets 
		if (!AppD.hasFullPermissions()) {		
			JMenuItem mi = add(helpAction);
			KeyStroke ks = KeyStroke.getKeyStroke(KeyEvent.VK_F1,
					0);
			mi.setAccelerator(ks);

			add(tutorialAction);
			add(geogebratubeAction);

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
	protected void initActions()
	{
		if (helpAction == null) {
			helpAction = new HelpAction(app, app
					.getImageIcon("help.png"),app.getMenu("Help"),App.WIKI_MANUAL);
						
			
			tutorialAction = new HelpAction(app,null,app.getMenu("Tutorials"),App.WIKI_TUTORIAL);
	
			reportBugAction = new AbstractAction(app.getMenu("ReportBug"), app.getEmptyIcon()) {
				private static final long serialVersionUID = 1L;
	
				public void actionPerformed(ActionEvent e) {
					GeoGebraMenuBar.copyDebugInfoToClipboard(app);
					app.getGuiManager().showURLinBrowser(
							GeoGebraConstants.GEOGEBRA_REPORT_BUG_DESKTOP);
				}
			};
	
			geogebratubeAction = new AbstractAction("GeoGebraTube", app.getImageIcon("GeoGebraTube.png")) {
				private static final long serialVersionUID = 1L;
	
				public void actionPerformed(ActionEvent e) {
					app.getGuiManager().showURLinBrowser(
							GeoGebraConstants.GEOGEBRATUBE_WEBSITE);
				}
			};
			
			infoAction = new AbstractAction(app.getMenu("AboutLicense"), app.getImageIcon("info.gif")) {
				private static final long serialVersionUID = 1L;
	
				public void actionPerformed(ActionEvent e) {
					GeoGebraMenuBar.showAboutDialog(app);
				}
			};
		
		}
	}

	@Override
	public void update() {
		//
	}



}
