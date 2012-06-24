package geogebra.gui.menubar;

import geogebra.common.gui.view.properties.PropertiesView.OptionType;
import geogebra.common.io.MyXMLHandler;
import geogebra.common.kernel.Kernel;
import geogebra.common.main.AbstractApplication;
import geogebra.common.util.Language;
import geogebra.main.Application;
import geogebra.main.GeoGebraPreferences;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

/**
 * The "Options" menu.
 */
public class OptionsMenu extends BaseMenu implements ActionListener, MenuListener {
	private static final long serialVersionUID = -8032696074032177289L;
	
	Kernel kernel;
	private AbstractAction
		//drawingPadPropAction,
		showOptionsAction,
		saveSettings,
		restoreDefaultSettings
	;
	
	private JMenu
		menuPointCapturing,
		menuDecimalPlaces,
		menuLabeling, 
		menuAlgebraStyle
	;
	
	public OptionsMenu(Application app) {
		super(app, app.getMenu("Options"));
		
		kernel = app.getKernel();

		// items are added to the menu when it's opened, see BaseMenu: addMenuListener(this);
	}
	
	/**
	 * Initialize the menu items.
	 * @param flag 
	 */
	void initItems(ImageIcon flag)
	{
		final JMenu submenu;
		int pos;
		
		//G.Sturr 2009-10-18
		// Algebra description: show value or definition of objects
		menuAlgebraStyle = new JMenu(app.getMenu("AlgebraDescriptions"));
		menuAlgebraStyle.setIcon(app.getEmptyIcon());
		String[] strDescription = { app.getPlain("Value"), 
				app.getPlain("Definition"), 
				app.getPlain("Command")};
		String[] strDescriptionAC = { "0", "1", "2" };
		ActionListener descAL = new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				int desc = Integer.parseInt(ae.getActionCommand());
				kernel.setAlgebraStyle(desc);
				kernel.updateConstruction();
			}
		};
		addRadioButtonMenuItems(menuAlgebraStyle, descAL, strDescription, strDescriptionAC,0);
		add(menuAlgebraStyle);
		updateMenuViewDescription();
		//END G.Sturr
		
		// point capturing
		menuPointCapturing = new JMenu(app.getMenu("PointCapturing"));
		menuPointCapturing.setIcon(app.getImageIcon("magnet.gif"));
		String[] strPointCapturing = { "Labeling.automatic", "SnapToGrid",
				"FixedToGrid", "off" };
		String[] strPointCapturingAC = { "3 PointCapturing",
				"1 PointCapturing", "2 PointCapturing", "0 PointCapturing" };
		addRadioButtonMenuItems(menuPointCapturing, this,
				strPointCapturing, strPointCapturingAC, 0);
		add(menuPointCapturing);
		updateMenuPointCapturing();

		// decimal places
		menuDecimalPlaces = new JMenu(app.getMenu("Rounding"));
		menuDecimalPlaces.setIcon(app.getEmptyIcon());
		/*
		 * int max_dec = 15; String[] strDecimalSpaces = new String[max_dec +
		 * 1]; String[] strDecimalSpacesAC = new String[max_dec + 1]; for (int
		 * i=0; i <= max_dec; i++){ strDecimalSpaces[i] = Integer.toString(i);
		 * strDecimalSpacesAC[i] = i + " decimals"; }
		 */
		String[] strDecimalSpaces = app.getRoundingMenu();

		addRadioButtonMenuItems(menuDecimalPlaces, this,
				strDecimalSpaces, AbstractApplication.strDecimalSpacesAC, 0);
		add(menuDecimalPlaces);
		updateMenuDecimalPlaces();

		addSeparator();

		// Labeling
		menuLabeling = new JMenu(app.getMenu("Labeling"));
		menuLabeling.setIcon(app.getImageIcon("mode_showhidelabel_16.gif"));
		String[] lstr = { "Labeling.automatic", "Labeling.on", "Labeling.off",
				"Labeling.pointsOnly" };
		String[] lastr = { "0_labeling", "1_labeling", "2_labeling",
				"3_labeling" };
		addRadioButtonMenuItems(menuLabeling, this, lstr,
				lastr, 0);
		add(menuLabeling);
		updateMenuLabeling();
		
		//add(drawingPadPropAction);	

		/*
		 * // Graphics quality submenu = new
		 * JMenu(app.getMenu("GraphicsQuality")); String[] gqfi = {
		 * "LowQuality", "HighQuality" }; if
		 * (app.getEuclidianView().getAntialiasing()) pos = 1; else pos = 0;
		 * addRadioButtonMenuItems(submenu, this, gqfi, gqfi, pos);
		 * add(submenu);
		 */

		addSeparator();

		// Font size
		submenu = new JMenu(app.getMenu("FontSize"));
		submenu.setIcon(app.getImageIcon("font.png"));
		
		//String[] fsfi = { "12 pt", "14 pt", "16 pt", "18 pt", "20 pt", "24 pt",
		//		"28 pt", "32 pt" };
		String[] fsfi = new String[MyXMLHandler.menuFontSizes.length];
		String[] fontActionCommands = new String[MyXMLHandler.menuFontSizes.length];

		// find current pos
		int fontSize = app.getFontSize();
		pos = 0;
		for (int i = 0; i < MyXMLHandler.menuFontSizes.length; i++) {
			if (fontSize == MyXMLHandler.menuFontSizes[i]) {
				pos = i;
			}
			fsfi[i] = app.getPlain("Apt",MyXMLHandler.menuFontSizes[i]+"");
			fontActionCommands[i]=MyXMLHandler.menuFontSizes[i] + " pt";
		}

		addRadioButtonMenuItems(submenu, this, fsfi, fontActionCommands, pos);
		add(submenu);

		/*
		 * // FontName menuFontName = new JMenu(getMenu("PointCapturing"));
		 * String[] strFontName = { "Sans Serif", "Serif" }; String[]
		 * strFontNameAC = { "SansSerif", "Serif" };
		 * addRadioButtonMenuItems(menuFontName, al, strFontName, strFontNameAC,
		 * 0); add(menuFontName); updateMenuFontName();
		 */

		// addSeparator();
		// Language
		if (app.propertiesFilesPresent()) {
			
			ImageIcon flagIcon;
			final String flagName = app.getFlagName(false);
			
			if (flag != null) {
				flagIcon = flag;
			} else {
				AbstractApplication.debug("using flag: "+flagName);
				flagIcon = app.getFlagIcon(flagName);
				
			}
						
			LanguageActionListener langListener = new LanguageActionListener(app);
			final JMenu submenuLang = new JMenu(app.getMenu("Language"));
			//submenu.setIcon(app.getImageIcon("globe.png"));
			submenuLang.setIcon(flagIcon);
			addLanguageMenuItems(app, submenuLang, langListener);
			add(submenuLang);
			
			// check 
			if (flag == null) {
				new Thread(
						new Runnable() {
							public void run() {

								String geoIPflagname = app.getFlagName(true);

								// fake for testing
								// geoIPflagname = "wales.png";

								if (!geoIPflagname.equals(flagName)) {
									AbstractApplication.debug("updating flag to: "+geoIPflagname);

									// rebuild menu with new flag
									removeAll();
									initItems(app.getFlagIcon(geoIPflagname));
								}
							}
						}).start();		
			}

		}

		addSeparator();

		// advanced properties	
		add(showOptionsAction);
		
		addSeparator();

		// save settings	
		add(saveSettings);

		// restore default settings	
		add(restoreDefaultSettings);

	}

	/**
	 * Create a set of radio buttons automatically.
	 * 
	 * @param menu
	 * @param al
	 * @param items
	 * @param actionCommands
	 * @param selectedPos
	 */
	private void addRadioButtonMenuItems(JMenu menu, ActionListener al,
			String[] items, String[] actionCommands, int selectedPos) {
		JRadioButtonMenuItem mi;
		ButtonGroup bg = new ButtonGroup();
		// String label;

		for (int i = 0; i < items.length; i++) {
			if (items[i] == "---") {
				menu.addSeparator();
			} else {
				String text = app.getMenu(items[i]);
				mi = new JRadioButtonMenuItem(text);
				mi.setFont(app.getFontCanDisplayAwt(text, false, Font.PLAIN, app.getGUIFontSize()));
				if (i == selectedPos)
					mi.setSelected(true);
				mi.setActionCommand(actionCommands[i]);
				mi.addActionListener(al);
				bg.add(mi);
				menu.add(mi);
			}
		}
	}

	/**
	 * Create a list with all languages which can be selected.
	 * 
	 * @param menu
	 * @param al
	 */
	public static void addLanguageMenuItems(Application app, JComponent menu, ActionListener al) {
		JRadioButtonMenuItem mi;
		ButtonGroup bg = new ButtonGroup();
		JMenu submenu1 = new JMenu("A - D");
		JMenu submenu2 = new JMenu("E - I");
		JMenu submenu3 = new JMenu("J - Q");
		JMenu submenu4 = new JMenu("R - Z");
		menu.add(submenu1);
		menu.add(submenu2);
		menu.add(submenu3);
		menu.add(submenu4);

		Language[] languages = Language.values();
		
		String currentLocale = app.getLocale().toString();
		
		// change en_GB into enGB
		currentLocale = currentLocale.replaceAll("_", "");
		
		for (int i = 0; i < languages.length; i++) {
			Language loc = languages[i];

			// enforce to show specialLanguageNames first
			// because here getDisplayLanguage doesn't return a good result
			String text = loc.name;
			mi = new JRadioButtonMenuItem(text);
			
			// make sure eg Malayalam, Georgian drawn OK (not in standard Java font)
			mi.setFont(app.getFontCanDisplayAwt(text, false, Font.PLAIN, app.getGUIFontSize()));

			if (loc.locale.equals(currentLocale)) {
				mi.setSelected(true);
			}
			mi.setActionCommand(loc.locale);
			mi.addActionListener(al);
			bg.add(mi);

			char ch = text.charAt(0);
			if (ch <= 'D')
				submenu1.add(mi);
			else if (ch <= 'I')
				submenu2.add(mi);
			else if (ch <= 'Q')
				submenu3.add(mi);
			else
				submenu4.add(mi);
		}
	}
	
	/**
	 * Initialize the actions.
	 */
	@Override
	protected void initActions()
	{
		// display the options dialog
		showOptionsAction = new AbstractAction(app
				.getMenu("Advanced")+" ...", app.getImageIcon("view-properties16.png")) {
			@SuppressWarnings("hiding")
			public static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				app.getDialogManager().showPropertiesDialog(OptionType.ADVANCED, null);
			}
		};
		
		// save settings
		saveSettings = new AbstractAction(app
				.getMenu("Settings.Save"),app.getImageIcon("document-save.png")) {
			@SuppressWarnings("hiding")
			public static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				GeoGebraPreferences.getPref().saveXMLPreferences(app);
			}
		};

		// restore default settings
		restoreDefaultSettings = new AbstractAction(app
				.getMenu("Settings.ResetDefault"),app.getEmptyIcon()) {
			@SuppressWarnings("hiding")
			public static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				GeoGebraPreferences.getPref().clearPreferences();

				// reset defaults for GUI, views etc
				// this has to be called before load XML preferences,
				// in order to avoid overwrite
				app.getSettings().resetSettings();

				// for geoelement defaults, this will do nothing, so it is
				// OK here
				GeoGebraPreferences.getPref().loadXMLPreferences(app);

				// reset default line thickness etc
				app.getKernel().getConstruction().getConstructionDefaults()
				.resetDefaults();

				// reset defaults for geoelements; this will create brand
				// new objects
				// so the options defaults dialog should be reset later
				app.getKernel().getConstruction().getConstructionDefaults()
				.createDefaultGeoElementsFromScratch();

				// reset the stylebar defaultGeo
				if (app.getEuclidianView1().hasStyleBar())
					app.getEuclidianView1().getStyleBar()
					.restoreDefaultGeo();
				if (app.hasEuclidianView2EitherShowingOrNot())
					if (app.getEuclidianView2().hasStyleBar())
						app.getEuclidianView2().getStyleBar()
						.restoreDefaultGeo();



			}
		};
	}

	@Override
	public void update() {
		updateMenuDecimalPlaces();
		updateMenuPointCapturing();
		updateMenuViewDescription();
	}
	
	/**
	 * Update algebra style description (switch between value / definition / command).
	 */
	private void updateMenuViewDescription() {
		if (menuAlgebraStyle != null) {
			((JRadioButtonMenuItem) menuAlgebraStyle.getMenuComponent(kernel.getAlgebraStyle()))
					.setSelected(true);
		}
	}

	/**
	 * Update the point capturing menu.
	 */
	private void updateMenuPointCapturing() {
		if (menuPointCapturing == null)
			return;

		String pos = Integer.toString(app.getActiveEuclidianView()
				.getPointCapturingMode());
		for (int i = 0; i < 4; i++) {
			JRadioButtonMenuItem mi = (JRadioButtonMenuItem) menuPointCapturing
					.getMenuComponent(i);
			String ac = mi.getActionCommand();
			if (ac.substring(0, 1).equals(pos)) {
				mi.setSelected(true);
				break;
			}
		}
	}

	/**
	 * Update the selected item in the labeling capturing menu.
	 */
	private void updateMenuLabeling() {
		if (menuLabeling == null) return;
		
		int pos = app.getLabelingStyle();
		((JRadioButtonMenuItem) menuLabeling.getMenuComponent(pos))
				.setSelected(true);
	}

	/**
	 * Update the menu with all decimal places.
	 */
	private void updateMenuDecimalPlaces() {
		if (menuDecimalPlaces == null)
			return;
		int pos = -1;

		if (kernel.useSignificantFigures) {
			int figures = kernel.getPrintFigures();
			if (figures > 0 && figures < AbstractApplication.figuresLookup.length)
				pos = AbstractApplication.figuresLookup[figures];
		} else {
			int decimals = kernel.getPrintDecimals();

			if (decimals > 0 && decimals < AbstractApplication.decimalsLookup.length)
				pos = AbstractApplication.decimalsLookup[decimals];

		}

		try {
			((JRadioButtonMenuItem) menuDecimalPlaces.getMenuComponent(pos))
					.setSelected(true);
		} catch (Exception e) {
			//
		}

	}

	/**
	 * Execute a performed action.
	 */
	public void actionPerformed(ActionEvent event) {
		String cmd = event.getActionCommand();

		// change graphics quality
		if (cmd.equals("LowQuality")) {
			app.getEuclidianView1().setAntialiasing(false);
			if(app.hasEuclidianView2EitherShowingOrNot())
				app.getEuclidianView2().setAntialiasing(false);
		} else if (cmd.equals("HighQuality")) {
			app.getEuclidianView1().setAntialiasing(true);
			if(app.hasEuclidianView2EitherShowingOrNot())
				app.getEuclidianView2().setAntialiasing(true);
		}

		// font size
		else if (cmd.endsWith("pt")) {
			try {
				app.setFontSize(Integer.parseInt(cmd.substring(0, 2)));
				app.setUnsaved();
			} catch (Exception e) {
				app.showError(e.toString());
			}
		}

		// decimal places
		else if (cmd.endsWith("decimals")) {
			try {
				String decStr = cmd.substring(0, 2).trim();
				int decimals = Integer.parseInt(decStr);
				// Application.debug("decimals " + decimals);

				kernel.setPrintDecimals(decimals);
				kernel.updateConstruction();
				app.refreshViews();
				
				// see ticket 79
				kernel.updateConstruction();

				app.setUnsaved();
			} catch (Exception e) {
				app.showError(e.toString());
			}
		}

		// significant figures
		else if (cmd.endsWith("figures")) {
			try {
				String decStr = cmd.substring(0, 2).trim();
				int figures = Integer.parseInt(decStr);
				// Application.debug("figures " + figures);

				kernel.setPrintFigures(figures);
				kernel.updateConstruction();
				app.refreshViews();
				
				// see ticket 79
				kernel.updateConstruction();

				app.setUnsaved();
			} catch (Exception e) {
				app.showError(e.toString());
			}
		}

		// Point capturing
		else if (cmd.endsWith("PointCapturing")) {
			int mode = Integer.parseInt(cmd.substring(0, 1));
			app.getEuclidianView1().setPointCapturing(mode);
			if (app.hasEuclidianView2EitherShowingOrNot()) {
				app.getEuclidianView2().setPointCapturing(mode);
			}
			app.setUnsaved();
		}

		// Labeling
		else if (cmd.endsWith("labeling")) {
			int style = Integer.parseInt(cmd.substring(0, 1));
			app.setLabelingStyle(style);
			app.setUnsaved();
		}
	}

	@Override
	protected void initItems() {
		initItems(null);
	}


}
