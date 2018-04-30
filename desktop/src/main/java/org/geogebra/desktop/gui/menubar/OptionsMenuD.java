package org.geogebra.desktop.gui.menubar;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.TreeSet;

import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.SwingConstants;

import org.geogebra.common.euclidian.EuclidianConstants;
import org.geogebra.common.gui.menubar.MenuFactory;
import org.geogebra.common.gui.menubar.MenuInterface;
import org.geogebra.common.gui.menubar.MyActionListener;
import org.geogebra.common.gui.menubar.RadioButtonMenuBar;
import org.geogebra.common.kernel.ConstructionDefaults;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.main.App.InputPosition;
import org.geogebra.common.main.OptionType;
import org.geogebra.common.util.lang.Language;
import org.geogebra.desktop.main.AppD;
import org.geogebra.desktop.main.GeoGebraPreferencesD;
import org.geogebra.desktop.util.GuiResourcesD;
import org.geogebra.desktop.util.ImageResourceD;

import com.himamis.retex.editor.share.util.Unicode;

/**
 * The "Options" menu.
 */
public class OptionsMenuD extends BaseMenu
		implements ActionListener, MyActionListener, MenuInterface {
	private static final long serialVersionUID = -8032696074032177289L;

	private AbstractAction
	// drawingPadPropAction,
	showOptionsAction, saveSettings, restoreDefaultSettings;

	private OptionsMenuController optionsMenu;

	/**
	 * @param app
	 *            application
	 */
	public OptionsMenuD(AppD app) {
		super(app, "Options");

		// items are added to the menu when it's opened, see BaseMenu:
		// addMenuListener(this);
	}

	@Override
	protected void initItems() {

		// G.Sturr 2009-10-18
		// Algebra description: show value or definition of objects
		// getOptionsMenu().addAlgebraDescriptionMenu(this);
		getOptionsMenu().addDecimalPlacesMenu(this);
		addSeparator();
		// Labeling
		getOptionsMenu().addLabelingMenu(this);

		// add(drawingPadPropAction);

		addSeparator();

		getOptionsMenu().addFontSizeMenu(this);

		/*
		 * // FontName menuFontName = new JMenu(getMenu("PointCapturing"));
		 * String[] strFontName = { "Sans Serif", "Serif" }; String[]
		 * strFontNameAC = { "SansSerif", "Serif" };
		 * addRadioButtonMenuItems(menuFontName, al, strFontName, strFontNameAC,
		 * 0); add(menuFontName); updateMenuFontName();
		 */

		// addSeparator();
		// Language
		if (app.getLocalization().propertiesFilesPresent()) {



			LanguageActionListener langListener = new LanguageActionListener(
					app);
			final JMenu submenuLang = new JMenu(loc.getMenu("Language"));
			submenuLang.setIcon(app.getMenuIcon(GuiResourcesD.LANGUAGE));
			addLanguageMenuItems(app, submenuLang, langListener);
			add(submenuLang);



		}

		addSeparator();

		// advanced properties
		add(showOptionsAction);

		// doesn't work in applets
		if (!app.isApplet()) {
			addSeparator();

			// save settings
			add(saveSettings);

			// restore default settings
			add(restoreDefaultSettings);
		}

		// support for right-to-left languages
		app.setComponentOrientation(this);

	}

	/**
	 * Create a list with all languages which can be selected.
	 * 
	 * @param app
	 *            application
	 * 
	 * @param menu
	 *            menu component
	 * @param al
	 *            language change listener
	 */
	public static void addLanguageMenuItems(AppD app, JComponent menu,
			ActionListener al) {
		JRadioButtonMenuItem mi;
		ButtonGroup bg = new ButtonGroup();
		boolean rtl = app.getLocalization().isRightToLeftReadingOrder();
		JMenu submenu1 = new JMenu(rtl ? "D - A" : "A - D");
		JMenu submenu2 = new JMenu(rtl ? "I - E" : "E - I");
		JMenu submenu3 = new JMenu(rtl ? "Q - J" : "J - Q");
		JMenu submenu4 = new JMenu(rtl ? "Z - R" : "R - Z");
		menu.add(submenu1);
		menu.add(submenu2);
		menu.add(submenu3);
		menu.add(submenu4);

		String currentLocale = app.getLocale().toString();

		// change en_GB into enGB
		currentLocale = currentLocale.replaceAll("_", "");
		StringBuilder sb = new StringBuilder(20);

		for (Language loc : Language.values()) {

			// enforce to show specialLanguageNames first
			// because here getDisplayLanguage doesn't return a good result
			String text = loc.name;

			char ch = text.charAt(0);

			if (ch == Unicode.LEFT_TO_RIGHT_MARK
					|| ch == Unicode.RIGHT_TO_LEFT_MARK) {
				ch = text.charAt(1);
			} else {
				// make sure brackets are correct in Arabic, ie not )US)
				sb.setLength(0);
				sb.append(Unicode.LEFT_TO_RIGHT_MARK);
				sb.append(text);
				sb.append(Unicode.LEFT_TO_RIGHT_MARK);
				text = sb.toString();
			}

			mi = new LanguageRadioButtonMenuItem(text);

			// make sure eg Malayalam, Georgian drawn OK (not in standard Java
			// font)
			mi.setFont(app.getFontCanDisplayAwt(text, false, Font.PLAIN,
					app.getGUIFontSize()));

			if (loc.locale.equals(currentLocale)) {
				mi.setSelected(true);
			}
			mi.setActionCommand(loc.locale);
			mi.addActionListener(al);
			bg.add(mi);

			if (ch <= 'D') {
				submenu1.add(mi);
			} else if (ch <= 'I') {
				submenu2.add(mi);
			} else if (ch <= 'Q') {
				submenu3.add(mi);
			} else {
				submenu4.add(mi);
			}
		}
	}

	/**
	 * Initialize the actions.
	 */
	@Override
	protected void initActions() {
		// display the options dialog
		showOptionsAction = new AbstractAction(loc.getMenu("Advanced") + " ...",
				app.getMenuIcon(GuiResourcesD.VIEW_PROPERTIES_16)) {
			@SuppressWarnings("hiding")
			public static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				app.getDialogManager().showPropertiesDialog(OptionType.GLOBAL,
						null);
			}
		};

		if (!app.isApplet()) {
			// save settings
			saveSettings = new AbstractAction(loc.getMenu("Settings.Save"),
					app.getMenuIcon(GuiResourcesD.DOCUMENT_SAVE)) {
				@SuppressWarnings("hiding")
				public static final long serialVersionUID = 1L;

				@Override
				public void actionPerformed(ActionEvent e) {
					GeoGebraPreferencesD.getPref().saveXMLPreferences(app);
				}
			};

			// restore default settings
			restoreDefaultSettings = new AbstractAction(
					loc.getMenu("Settings.ResetDefault"), app.getEmptyIcon()) {
				@SuppressWarnings("hiding")
				public static final long serialVersionUID = 1L;

				@Override
				public void actionPerformed(ActionEvent e) {

					// set checkbox size to new default
					app.getEuclidianView1().setBooleanSize(
							EuclidianConstants.DEFAULT_CHECKBOX_SIZE);
					if (app.hasEuclidianView2(1)) {
						app.getEuclidianView2(1).setBooleanSize(
								EuclidianConstants.DEFAULT_CHECKBOX_SIZE);
					}

					// set sliders to new styling
					TreeSet<GeoElement> geos = app.getKernel().getConstruction()
							.getGeoSetConstructionOrder();
					Iterator<GeoElement> it = geos.iterator();
					while (it.hasNext()) {
						GeoElement geo = it.next();
						if (geo instanceof GeoNumeric
								&& ((GeoNumeric) geo).isSlider()) {
							GeoNumeric slider = (GeoNumeric) geo;
							slider.setAlphaValue(
									ConstructionDefaults.DEFAULT_NUMBER_ALPHA);
							slider.setLineThickness(
									GeoNumeric.DEFAULT_SLIDER_THICKNESS);
							slider.setSliderWidth(
									GeoNumeric.DEFAULT_SLIDER_WIDTH_PIXEL,
									true);
							slider.setSliderBlobSize(
									GeoNumeric.DEFAULT_SLIDER_BLOB_SIZE);
							slider.setLineThickness(
									GeoNumeric.DEFAULT_SLIDER_THICKNESS);
							slider.updateRepaint();
						}
					}

					GeoGebraPreferencesD.getPref().clearPreferences(app);
					boolean oldAxisX = app.getSettings().getEuclidian(1)
							.getShowAxis(0);
					boolean oldAxisY = app.getSettings().getEuclidian(1)
							.getShowAxis(1);
					// reset defaults for GUI, views etc
					// this has to be called before load XML preferences,
					// in order to avoid overwrite
					app.getSettings().resetSettings(app);

					// for geoelement defaults, this will do nothing, so it is
					// OK here
					GeoGebraPreferencesD.getPref().loadXMLPreferences(app);
					app.getSettings().getEuclidian(1).setShowAxes(oldAxisX,
							oldAxisY);
					// reset default line thickness etc
					app.getKernel().getConstruction().getConstructionDefaults()
							.resetDefaults();

					// reset defaults for geoelements; this will create brand
					// new objects
					// so the options defaults dialog should be reset later
					app.getKernel().getConstruction().getConstructionDefaults()
							.createDefaultGeoElements();
					app.setInputPosition(InputPosition.algebraView, false);
					// reset the stylebar defaultGeo
					if (app.getEuclidianView1().hasStyleBar()) {
						app.getEuclidianView1().getStyleBar()
								.restoreDefaultGeo();
					}
					if (app.hasEuclidianView2EitherShowingOrNot(1)) {
						if (app.getEuclidianView2(1).hasStyleBar()) {
							app.getEuclidianView2(1).getStyleBar()
									.restoreDefaultGeo();
						}
					}
					app.getKernel().updateConstruction(false);
					// set default layout options
					app.setToolbarPosition(SwingConstants.NORTH, false);
					app.setShowToolBarNoUpdate(true);
					app.setShowToolBarHelpNoUpdate(false);
					app.setShowDockBar(true, false);
					app.setDockBarEast(true);
					app.updateContentPane();

				}
			};
		}
	}

	@Override
	public void update() {
		getOptionsMenu().update();
	}

	/**
	 * Execute a performed action.
	 */
	@Override
	public void actionPerformed(ActionEvent event) {
		String cmd = event.getActionCommand();
		getOptionsMenu().processActionPerformed(cmd);
	}


	@Override
	public void actionPerformed(String command) {
		getOptionsMenu().processActionPerformed(command);
	}

	private OptionsMenuController getOptionsMenu() {

		if (optionsMenu == null) {
			optionsMenu = new OptionsMenuController(app, new MenuFactory() {

				@Override
				public RadioButtonMenuBar newSubmenu() {
					return new RadioButtonMenuBarD(app);
				}

				@Override
				public void addMenuItem(MenuInterface parentMenu, String key,
						boolean asHtml, MenuInterface subMenu) {
					ImageResourceD res = null;
					if ("Labeling".equals(key)) {
						res = GuiResourcesD.MODE_SHOWHIDELABEL;
					}
					if ("FontSize".equals(key)) {
						res = GuiResourcesD.FONT;
					}
					if (res != null) {
						((JMenuItem) subMenu).setIcon(app.getMenuIcon(res));
					}
					((JMenuItem) subMenu).setText(loc.getMenu(key));
					((JMenu) parentMenu).add((JMenuItem) subMenu);

				}
			});
		}
		return optionsMenu;

	}

}
