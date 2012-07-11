package geogebra.gui.menubar;

import geogebra.common.io.MyXMLHandler;
import geogebra.common.kernel.Kernel;
import geogebra.common.main.App;
import geogebra.common.util.Language;
import geogebra.common.util.StringUtil;
import geogebra.common.util.Unicode;
import geogebra.main.AppD;
import geogebra.main.GeoGebraPreferences;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Locale;

import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JRadioButtonMenuItem;

/**
 * The "Options" menu.
 */
public class LanguageMenu extends BaseMenu implements ActionListener {
	private static final long serialVersionUID = -8032696074032177289L;
	
	private Kernel kernel;
	private AbstractAction
		//drawingPadPropAction,
		showOptionsAction
	;
	
	private JMenu
		menuPointCapturing,
		menuDecimalPlaces,
		menuLabeling, 
		menuAlgebraStyle
	;
	
	public LanguageMenu(AppD app) {
		super(app, "hello");
		
		kernel = app.getKernel();

		// items are added to the menu when it's opened, see BaseMenu: addMenuListener(this);
	}
	
	/**
	 * Initialize the menu items.
	 */
	@Override
	protected void initItems()
	{
		JMenu submenu;
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
				strDecimalSpaces, App.strDecimalSpacesAC, 0);
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
			LanguageActionListener langListener = new LanguageActionListener();
			submenu = new JMenu(app.getMenu("Language"));
			submenu.setIcon(app.getFlagIcon("ko.png"));
			addLanguageMenuItems(submenu, langListener);
			add(submenu);
		}

		addSeparator();

		// drawing pad properties	
		add(showOptionsAction);
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
	private void addLanguageMenuItems(JMenu menu, ActionListener al) {
		JRadioButtonMenuItem mi;
		ButtonGroup bg = new ButtonGroup();
		// String label;
		JMenu submenu1 = new JMenu(app.isRightToLeftReadingOrder() ? "D - A" : "A - D");
		JMenu submenu2 = new JMenu(app.isRightToLeftReadingOrder() ? "I - E" : "E - I");
		JMenu submenu3 = new JMenu(app.isRightToLeftReadingOrder() ? "Q - J" : "J - Q");
		JMenu submenu4 = new JMenu(app.isRightToLeftReadingOrder() ? "Z - R" : "R - Z");
		menu.add(submenu1);
		menu.add(submenu2);
		menu.add(submenu3);
		menu.add(submenu4);
		
		Language[] languages = Language.values();

		for (int i = 0; i < languages.length; i++) {
			Language loc = languages[i];

			StringBuilder sb = new StringBuilder(20);
			
			// enforce to show specialLanguageNames first
			// because here getDisplayLanguage doesn't return a good result
			String text = loc.name;
			char ch = text.charAt(0);
			
			if (ch == Unicode.LeftToRightMark || ch == Unicode.RightToLeftMark) {
				ch = text.charAt(1);
			} else {			
				// make sure brackets are right in Arabic, ie not )US)
				sb.setLength(0);
				sb.append(Unicode.LeftToRightMark);
				sb.append(text);
				sb.append(Unicode.LeftToRightMark);
				text = sb.toString();
			}	
				
			mi = new JRadioButtonMenuItem(text);
			
			// make sure eg Malayalam, Georgian drawn OK (not in standard Java font)
			mi.setFont(app.getFontCanDisplayAwt(text, false, Font.PLAIN, app.getGUIFontSize()));

			if (loc.locale.equals(app.getLocale().toString())) {
				mi.setSelected(true);
			}
			mi.setActionCommand(loc.locale);
			mi.addActionListener(al);
			bg.add(mi);

			
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
				.getMenu("Settings")+"...", app.getImageIcon("document-properties.png")) {
			@SuppressWarnings("hiding")
			public static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				app.getDialogManager().showOptionsDialog(-1);
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
			if (figures > 0 && figures < App.figuresLookup.length)
				pos = App.figuresLookup[figures];
		} else {
			int decimals = kernel.getPrintDecimals();

			if (decimals > 0 && decimals < App.decimalsLookup.length)
				pos = App.decimalsLookup[decimals];

		}

		try {
			((JRadioButtonMenuItem) menuDecimalPlaces.getMenuComponent(pos))
					.setSelected(true);
		} catch (Exception e) {
			//
		}

	}

	/**
	 * Handle the change of the language.
	 */
	private class LanguageActionListener implements ActionListener {

		public LanguageActionListener() {
		}

		public void actionPerformed(ActionEvent e) {
			app.setLanguage(AppD.getLocale(e.getActionCommand()));
			// make sure axes labels are updated eg for Arabic 
			app.getEuclidianView1().updateBackground();
			if(app.hasEuclidianView2EitherShowingOrNot())
				app.getEuclidianView2().updateBackground();
			GeoGebraPreferences.getPref().saveDefaultLocale(app.getLocale());
		}
	}

	/**
	 * Execute a performed action.
	 */
	public void actionPerformed(ActionEvent event) {
		String cmd = event.getActionCommand();

		// font size
		if (cmd.endsWith("pt")) {
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
	
	
}
