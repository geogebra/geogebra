/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.desktop.gui.virtualkeyboard;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.font.FontRenderContext;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.WindowConstants;

import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.main.settings.AbstractSettings;
import org.geogebra.common.main.settings.SettingListener;
import org.geogebra.common.util.debug.Log;
import org.geogebra.common.util.lang.Language;
import org.geogebra.desktop.gui.GuiManagerD;
import org.geogebra.desktop.main.AppD;
import org.geogebra.desktop.main.KeyboardSettings;
import org.geogebra.desktop.main.LocalizationD;
import org.geogebra.desktop.main.MyResourceBundle;
import org.geogebra.desktop.util.GuiResourcesD;

import com.himamis.retex.editor.share.util.Unicode;

/**
 * @author Michael Borcherds (based loosely on
 *         http://sourceforge.net/projects/virtualkey/ )
 * 
 */
@SuppressWarnings("javadoc")
public class VirtualKeyboardD extends JFrame
		implements ActionListener, SettingListener, SetLabels {

	private static final long serialVersionUID = 1L;

	/**
	 * List with supported languages.
	 */

	// private static Boolean Upper = false;

	// private JPanel jContentPane = null;

	// private JTextArea jTextArea = null;

	private JButton SpaceButton = null;
	private JButton DummyButton = null;
	private JToggleButton CapsLockButton = null;
	private JToggleButton AltButton = null;
	private JToggleButton AltGrButton = null;
	private JToggleButton CtrlButton = null;
	private JToggleButton MathButton = null;
	private JToggleButton NumericButton = null;
	private JToggleButton GreekButton = null;
	private JToggleButton EnglishButton = null;

	private String ctrlText = "Ctrl";
	private String altText = "Alt";
	private String altGrText = "AltG";
	private String escText = "Esc";

	AppD app;

	// max width character
	private final static char wideCharDefault = '@';
	private char wideChar = wideCharDefault;

	private int buttonRows = 5;
	private int buttonCols = 14;
	private int buttonRowsNum = 4;
	private int buttonColsNum = 11;
	private double buttonSizeX, buttonSizeY;

	private double horizontalMultiplier = 1;
	private double verticalMultiplier = 1;

	JButton[][] Buttons = new JButton[buttonRows + 1][buttonCols];

	private int windowWidth, windowHeight;

	private Font currentFont;

	private Font[] fonts = new Font[100];

	private String fontName = null;

	private boolean shrink;

	private LocalizationD loc;

	/**
	 * This is the default constructor
	 * 
	 * @param app
	 * @param windowWidth
	 * @param windowHeight
	 * @param opacity
	 */
	public VirtualKeyboardD(final AppD app, int windowWidth, int windowHeight,
			float opacity) {

		super();

		readConf(app, null, false);

		this.windowWidth = windowWidth;
		this.windowHeight = windowHeight;

		this.app = app;
		this.setFocusableWindowState(false);
		this.setAlwaysOnTop(true);

		setFonts();

		// make sure resizing the window dynamically updates the contents
		// doesn't seem to be needed on Java 5
		Toolkit kit = Toolkit.getDefaultToolkit();
		kit.setDynamicLayout(true);

		if (app != null) {
			this.loc = app.getLocalization();
			initialize();

			setLabels();
			setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		} else {
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		}

		windowResized();

		// setVisible(true);
		addWindowListener(new WindowListener() {
			@Override
			public void windowClosed(WindowEvent arg0) {
				// System.out.println("Window close event occur");
			}

			@Override
			public void windowActivated(WindowEvent arg0) {
				// System.out.println("Window Activated");
			}

			@Override
			public void windowClosing(WindowEvent arg0) {
				// System.out.println("Window Closing");
				// if closed with the X, stop it auto-opening
				AppD.setVirtualKeyboardActive(false);
				((GuiManagerD) app.getGuiManager()).updateMenubar();
			}

			@Override
			public void windowDeactivated(WindowEvent arg0) {
				// System.out.println("Window Deactivated");
			}

			@Override
			public void windowDeiconified(WindowEvent arg0) {
				// System.out.println("Window Deiconified");
			}

			@Override
			public void windowIconified(WindowEvent arg0) {
				// System.out.println("Window Iconified");
			}

			@Override
			public void windowOpened(WindowEvent arg0) {
				// System.out.println("Window Opened");
			}
		});

		// Event Handling
		getContentPane().addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				windowResized();
			}
		});

		// http://java.sun.com/developer/technicalArticles/GUI/translucent_shaped_windows/#Setting-the-Opacity-Level-of-a-Window
		// AWTUtilities.setWindowOpacity

		// TODO: fix
		// force resizing of contentPane
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				windowResized();
			}
		});

	}

	private void setFonts() {

		String fName = app
				.getFontCanDisplayAwt(""
						+ Language.getTestChar(app.getLocale().getLanguage()))
				.getFontName();

		if (fName.equals(this.fontName)) {
			return;
		}

		this.fontName = fName;

		for (int i = 0; i < 100; i++) {
			fonts[i] = new Font(fName, Font.PLAIN, i + 1);
		}
	}

	final void windowResized() {
		windowWidth = getWidth();
		windowHeight = getHeight();

		int cpWidth = getContentPane().getWidth();
		int cpHeight = getContentPane().getHeight();

		if (cpWidth == 0) {
			cpWidth = windowWidth;
		}
		if (cpHeight == 0) {
			cpHeight = windowHeight;
		}
		if (getKeyboardMode() == KEYBOARD_NUMERIC) {
			buttonSizeX = 0.15 + cpWidth / (buttonColsNum - 0.0);
			buttonSizeY = 0.25 + cpHeight / (buttonRowsNum + 1.0);
		} else {
			buttonSizeX = 0.15 + (double) cpWidth / (double) (buttonCols);
			buttonSizeY = 0.25 + (double) cpHeight / (double) (buttonRows + 1);
		}
		// if (buttonSize < 20) buttonSize = 20;

		updateButtons();
		KeyboardSettings kbs = (KeyboardSettings) app.getSettings()
				.getKeyboard();
		kbs.keyboardResized(windowWidth, windowHeight);
	}

	/**
	 * This method initializes this keyboard
	 */
	private void initialize() {
		setSize(windowWidth, windowHeight);
		setPreferredSize(new Dimension(windowWidth, windowHeight));
		populateContentPane();
	}

	public void updateButtons() {
		if (getKeyboardMode() == KEYBOARD_NUMERIC && !shrink) {
			shrink = true;
			windowResized();
			/*
			 * int baseWidth = (int)
			 * (windowWidth*buttonColsNum/(double)buttonCols);
			 * setSize(baseWidth,windowHeight); setPreferredSize(new
			 * Dimension(baseWidth,windowHeight));
			 */
		}
		if (getKeyboardMode() != KEYBOARD_NUMERIC && shrink) {
			shrink = false;
			windowResized();
			/*
			 * int baseWidth = (int)
			 * (windowWidth*buttonCols/(double)buttonColsNum);
			 * setSize(baseWidth,windowHeight); setPreferredSize(new
			 * Dimension(baseWidth,windowHeight));
			 */
		}
		for (int i = 1; i <= buttonRows; i++) {
			for (int j = 0; j < buttonCols; j++) {
				updateButton(i, j);
			}
		}

		updateSpaceButton();
		updateCapsLockButton();
		updateMathButton();
		updateNumericButton();
		updateGreekButton();
		updateEnglishButton();
		updateAltButton();
		updateAltGrButton();
		updateCtrlButton();
	}

	/**
	 * This method initializes SpaceButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getSpaceButton() {
		if (SpaceButton == null) {

			SpaceButton = new JButton();
			SpaceButton.setRequestFocusEnabled(false);
			updateSpaceButton();
			SpaceButton.setMargin(new Insets(0, 0, 0, 0));
			SpaceButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					insertText(" ");
				}
			});
		}
		return SpaceButton;
	}

	/*
	 * used to find the preferred size of buttons with certain characters or
	 * fonts
	 */
	private JButton getDummyButton() {
		if (DummyButton == null) {
			DummyButton = new JButton(wideChar + "");
			DummyButton.setRequestFocusEnabled(false);
			DummyButton.setSize(new Dimension(10, 10));
			DummyButton.setLocation(new Point(0, 0));
			DummyButton.setMargin(new Insets(0, 0, 0, 0));
		}
		return DummyButton;
	}// */

	private void updateSpaceButton() {
		SpaceButton.setSize(
				new Dimension((int) (buttonSizeX * 5d), (int) buttonSizeY));
		SpaceButton.setLocation(
				new Point((int) (buttonSizeX * 4d), (int) (buttonSizeY * 4d)));
		SpaceButton.setVisible(getKeyboardMode() != KEYBOARD_NUMERIC);

	}

	private void updateCapsLockButton() {
		CapsLockButton
				.setSize(new Dimension((int) (buttonSizeX), (int) buttonSizeY));
		CapsLockButton.setLocation(
				new Point((int) (buttonSizeX / 2d), (int) (buttonSizeY * 4d)));

		CapsLockButton.setFont(getFont((int) (minButtonSize()), false));

		setColor(CapsLockButton);
		CapsLockButton.setVisible(getKeyboardMode() != KEYBOARD_NUMERIC);
	}

	void updateCtrlButton() {
		CtrlButton
				.setSize(new Dimension((int) (buttonSizeX), (int) buttonSizeY));
		CtrlButton.setLocation(new Point((int) (buttonSizeX * 3d / 2d),
				(int) (buttonSizeY * 4d)));

		CtrlButton.setFont(getFont((int) (minButtonSize() / 2), false));
		CtrlButton.setVisible(getKeyboardMode() != KEYBOARD_NUMERIC);
		setColor(CtrlButton);
	}

	void updateAltButton() {
		AltButton
				.setSize(new Dimension((int) (buttonSizeX), (int) buttonSizeY));
		AltButton.setLocation(new Point((int) (buttonSizeX * 5d / 2d),
				(int) (buttonSizeY * 4d)));

		AltButton.setFont(getFont((int) (minButtonSize() / 2), false));

		setColor(AltButton);
		AltButton.setVisible(getKeyboardMode() != KEYBOARD_NUMERIC);
		if (sbAlt != null) {
			sbAlt.setLength(0);
		}
	}

	void updateAltGrButton() {
		AltGrButton
				.setSize(new Dimension((int) (buttonSizeX), (int) buttonSizeY));
		AltGrButton.setLocation(
				new Point((int) (buttonSizeX * 9), (int) (buttonSizeY * 4d)));

		AltGrButton.setFont(getFont((int) (minButtonSize() / 2), false));
		AltGrButton.setVisible(getKeyboardMode() != KEYBOARD_NUMERIC);
		setColor(AltGrButton);

	}

	private void updateMathButton() {
		MathButton
				.setSize(new Dimension((int) (buttonSizeX), (int) buttonSizeY));
		MathButton.setLocation(
				new Point((int) (buttonSizeX * 10), (int) (buttonSizeY * 4d)));

		MathButton.setFont(getFont((int) (minButtonSize()), false));
		MathButton.setVisible(getKeyboardMode() != KEYBOARD_NUMERIC);
		setColor(MathButton);
	}

	private void updateNumericButton() {
		NumericButton
				.setSize(new Dimension((int) (buttonSizeX), (int) buttonSizeY));
		if (getKeyboardMode() != KEYBOARD_NUMERIC) {
			NumericButton.setLocation(new Point((int) (buttonSizeX * 13),
					(int) (buttonSizeY * 4d)));
		} else {
			NumericButton.setLocation(new Point((int) (buttonSizeX * 10),
					(int) (buttonSizeY * 2d)));
		}

		NumericButton.setFont(getFont((int) (minButtonSize()), false));

		setColor(NumericButton);
	}

	private static void setColor(JToggleButton tb) {
		if (tb.isSelected()) {
			tb.setBackground(Color.cyan);
		} else {
			tb.setBackground(null);
		}
	}

	private void updateGreekButton() {
		GreekButton
				.setSize(new Dimension((int) (buttonSizeX), (int) buttonSizeY));
		if (getKeyboardMode() != KEYBOARD_NUMERIC) {
			GreekButton.setLocation(new Point((int) (buttonSizeX * 12),
					(int) (buttonSizeY * 4d)));
		} else {
			GreekButton.setLocation(new Point((int) (buttonSizeX * 10),
					(int) (buttonSizeY * 1d)));
		}
		GreekButton.setFont(getFont((int) (minButtonSize()), false));

		setColor(GreekButton);

	}

	private void updateEnglishButton() {
		EnglishButton
				.setSize(new Dimension((int) (buttonSizeX), (int) buttonSizeY));
		if (getKeyboardMode() != KEYBOARD_NUMERIC) {
			EnglishButton.setLocation(new Point((int) (buttonSizeX * 11),
					(int) (buttonSizeY * 4d)));
		} else {
			EnglishButton.setLocation(new Point((int) (buttonSizeX * 10),
					(int) (buttonSizeY * 0d)));
		}
		EnglishButton.setFont(getFont((int) (minButtonSize()), false));

		EnglishButton.setVisible(true);

		setColor(EnglishButton);

	}

	private double minButtonSize() {
		double ret = Math.min(buttonSizeX * horizontalMultiplier,
				buttonSizeY * verticalMultiplier);

		return (ret == 0) ? 1 : ret;
	}

	private JToggleButton getCapsLockButton() {
		if (CapsLockButton == null) {

			CapsLockButton = new JToggleButton("\u21e7");
			updateCapsLockButton();
			CapsLockButton.setMargin(new Insets(0, 0, 0, 0));
			CapsLockButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					updateButtons();
				}
			});
		}
		return CapsLockButton;
	}

	private JToggleButton getAltButton() {
		if (AltButton == null) {

			AltButton = new JToggleButton(altText);
			updateAltButton();
			AltButton.setMargin(new Insets(0, 0, 0, 0));

			AltButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					// altPressed = !altPressed;
					updateAltButton();
				}
			});
		}
		return AltButton;
	}

	private JToggleButton getAltGrButton() {
		if (AltGrButton == null) {

			AltGrButton = new JToggleButton(altGrText);
			updateAltGrButton();
			AltGrButton.setMargin(new Insets(0, 0, 0, 0));

			AltGrButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					updateButtons();
					updateAltGrButton();
				}
			});
		}
		return AltGrButton;
	}

	private JToggleButton getCtrlButton() {
		if (CtrlButton == null) {

			CtrlButton = new JToggleButton(ctrlText);
			updateCtrlButton();
			CtrlButton.setMargin(new Insets(0, 0, 0, 0));

			CtrlButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					updateCtrlButton();
				}
			});
		}
		return CtrlButton;
	}

	private JToggleButton getMathButton() {
		if (MathButton == null) {

			MathButton = new JToggleButton("\u222b");
			updateMathButton();
			MathButton.setMargin(new Insets(0, 0, 0, 0));
			MathButton.setToolTipText(loc.getMenu("Keyboard.Math"));
			MathButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {

					getGreekButton().setSelected(false);
					getEnglishButton().setSelected(false);

					if (getKeyboardMode() != KEYBOARD_MATH) {
						setMode(KEYBOARD_MATH, null);
					} else {
						setMode(KEYBOARD_NORMAL, null);
					}

				}
			});
		}
		return MathButton;
	}

	public void toggleNumeric(boolean numeric) {
		setMode(numeric ? KEYBOARD_NUMERIC : KEYBOARD_NORMAL, null);
	}

	private JToggleButton getNumericButton() {
		if (NumericButton == null) {

			NumericButton = new JToggleButton();
			NumericButton
					.setIcon(app.getScaledIcon(GuiResourcesD.CAS_KEYBOARD));
			NumericButton.setToolTipText(loc.getMenu("Keyboard.Numeric"));
			updateNumericButton();
			NumericButton.setMargin(new Insets(0, 0, 0, 0));
			NumericButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {

					getGreekButton().setSelected(false);
					getEnglishButton().setSelected(false);

					if (getKeyboardMode() != KEYBOARD_NUMERIC) {
						setMode(KEYBOARD_NUMERIC, null);
					} else {
						setMode(KEYBOARD_NORMAL, null);
					}

				}
			});
		}
		return NumericButton;
	}

	boolean greek() {
		return getGreekButton().isSelected();
	}

	JToggleButton getGreekButton() {
		if (GreekButton == null) {

			GreekButton = new JToggleButton(Unicode.alpha + "");
			updateGreekButton();
			GreekButton.setMargin(new Insets(0, 0, 0, 0));
			GreekButton.setToolTipText(loc.getMenu("Keyboard.Greek"));
			GreekButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					setMode(KEYBOARD_NORMAL, null);
					if (greek()) {
						readConf(app, new Locale("el"), false);
					}

					getEnglishButton().setSelected(false);

					updateButtons();

				}
			});
		}
		return GreekButton;
	}

	boolean english() {
		return getEnglishButton().isSelected();
	}

	JToggleButton getEnglishButton() {
		if (EnglishButton == null) {

			EnglishButton = new JToggleButton("a");
			updateEnglishButton();
			EnglishButton.setToolTipText(loc.getMenu("Keyboard.Standard"));
			EnglishButton.setMargin(new Insets(0, 0, 0, 0));
			EnglishButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					setMode(KEYBOARD_NORMAL, null);
					if (english()) {
						readConf(app, new Locale("en"), false);
					}

					getGreekButton().setSelected(false);

					updateButtons();

				}
			});
		}
		return EnglishButton;
	}

	/**
	 * This method initializes jContentPane
	 */
	private void populateContentPane() {

		setLayout(null);

		for (int i = 1; i <= buttonRows; i++) {
			for (int j = 0; j < buttonCols; j++) {
				add(getButton(i, j), null);
			}
		}

		add(getSpaceButton(), null);
		add(getCapsLockButton(), null);
		add(getMathButton(), null);
		add(getNumericButton(), null);
		add(getGreekButton(), null);
		add(getEnglishButton(), null);
		add(getAltButton(), null);
		add(getAltGrButton(), null);
		add(getCtrlButton(), null);

		pack();

	}

	public static final char KEYBOARD_NORMAL = ' ';
	public static final char KEYBOARD_MATH = 'M';
	public static final char KEYBOARD_NUMERIC = 'N';
	// public static final char KEYBOARD_ALTGR = 'Q';
	public static final char KEYBOARD_ACUTE = 'A';
	public static final char KEYBOARD_GRAVE = 'G';
	public static final char KEYBOARD_UMLAUT = 'U';
	public static final char KEYBOARD_CEDILLA = 'c';
	public static final char KEYBOARD_CARON = 'v';
	public static final char KEYBOARD_CIRCUMFLEX = 'C';
	public static final char KEYBOARD_BREVE = 'B';
	public static final char KEYBOARD_TILDE = 'T';
	public static final char KEYBOARD_OGONEK = 'O';
	public static final char KEYBOARD_DOT_ABOVE = 'D';
	public static final char KEYBOARD_RING_ABOVE = 'R';
	public static final char KEYBOARD_DIALYTIKA_TONOS = 'd';
	public static final char KEYBOARD_DOUBLE_ACUTE = 'a';
	public static final char KEYBOARD_SOLIDUS = '/';

	private char KEYBOARD_MODE = KEYBOARD_NORMAL;

	/**
	 * This method adds a char to the text-field
	 */
	void insertText(String str) {
		String addchar = str;
		if (addchar.length() == 1) {
			switch (addchar.charAt(0)) {
			case '\u00b4': // acute
				setMode(KEYBOARD_ACUTE, kbLocale);
				return;

			case '\u0338': // solidus (/)
				setMode(KEYBOARD_SOLIDUS, kbLocale);
				return;

			// case '\u0060': // grave
			case '\u0300': // combining grave
				setMode(KEYBOARD_GRAVE, kbLocale);
				return;

			case '\u02d8': // breve
				setMode(KEYBOARD_BREVE, kbLocale);
				return;

			case '\u0303': // tilde
				setMode(KEYBOARD_TILDE, kbLocale);
				return;

			case '\u0302': // circumflex
				setMode(KEYBOARD_CIRCUMFLEX, kbLocale);
				return;

			case '\u0385': // dialytika tonos
				setMode(KEYBOARD_DIALYTIKA_TONOS, kbLocale);
				return;

			case '\u00b8': // cedilla
				setMode(KEYBOARD_CEDILLA, kbLocale);
				return;

			case '\u00a8': // umlaut
				setMode(KEYBOARD_UMLAUT, kbLocale);
				return;

			case '\u02c7': // caron
				setMode(KEYBOARD_CARON, kbLocale);
				return;

			case '\u02d9': // dot above
				setMode(KEYBOARD_DOT_ABOVE, kbLocale);
				return;

			case '\u02db': // Ogonek
				setMode(KEYBOARD_OGONEK, kbLocale);
				return;

			case '\u02da': // ring above
				setMode(KEYBOARD_RING_ABOVE, kbLocale);
				return;

			case '\u02dd': // double acute
				setMode(KEYBOARD_DOUBLE_ACUTE, kbLocale);
				return;

			case '0':
			case '1':
			case '2':
			case '3':
			case '4':
			case '5':
			case '6':
			case '7':
			case '8':
			case '9':
				if (AltButton.isSelected()) {
					StringBuilder asb = getAltStringBuilder();

					asb.append(addchar);

					AltButton.setBackground(Color.orange);

					if (asb.length() < 4) {
						return;
					}

					// convert string to Unicode char
					char c = (char) Integer.parseInt(asb.toString());

					// release alt
					AltButton.setSelected(false);
					updateAltButton();

					// type Unicode char
					((GuiManagerD) app.getGuiManager())
							.insertStringIntoTextfield(c + "", false, false,
									false);

					asb.setLength(0);
					return;

				} // else pass on as normal

			}
		}

		if ("<enter>".equals(addchar)) {
			addchar = "\n";
		} else if ("<E>".equals(addchar)) {
			addchar = "E";
		} else if ("<multiply>".equals(addchar)) {
			addchar = "*";
		} else if ("<divide>".equals(addchar)) {
			addchar = "/";
		} else if ("<minus>".equals(addchar)) {
			addchar = "-";
		}

		if (app != null) {
			((GuiManagerD) app.getGuiManager()).insertStringIntoTextfield(
					addchar, getAltButton().isSelected(),
					getCtrlButton().isSelected(),
					getCapsLockButton().isSelected());
		} else {
			getKeyboard().doType(getAltButton().isSelected(),
					getCtrlButton().isSelected(),
					getCapsLockButton().isSelected(), addchar);
		}

		// no special keys pressed, reset to normal (except eg Greek)
		if (getKeyboardMode() != KEYBOARD_NUMERIC) {
			setMode(KEYBOARD_NORMAL, kbLocale);
		}

	}

	StringBuilder sbAlt;

	private StringBuilder getAltStringBuilder() {
		if (sbAlt == null) {
			sbAlt = new StringBuilder();
		}

		return sbAlt;
	}

	void setMode(char mode, Locale loc) {

		// loc==null -> restore language (eg if greek selected before)
		readConf(app, loc, false);

		if (getKeyboardMode() == mode) {
			setKEYBOARD_MODE(KEYBOARD_NORMAL);
		} else {
			// reset first
			setKEYBOARD_MODE(KEYBOARD_NORMAL);
			updateButtons();

			// new mode
			setKEYBOARD_MODE(mode);
		}

		if (getKeyboardMode() != KEYBOARD_MATH) {
			getMathButton().setSelected(false);
		}
		if (getKeyboardMode() == KEYBOARD_MATH) {
			getAltGrButton().setSelected(false);
		}

		updateButtons();

	}

	private boolean Upper() {
		return getCapsLockButton().isSelected();
	}

	/**
	 * This method adds a char to the text-field
	 * 
	 */
	private void insertKeyText(KeyboardKeys Keys) {
		if (Upper()) {
			insertText(Keys.getUpperCase());
		} else {
			insertText(Keys.getLowerCase());
		}
	}

	private StringBuilder sb = new StringBuilder();

	private KeyboardKeys getKey(int i, int j) {

		sb.setLength(0);
		sb.append('B');
		if (i < 10)
		 {
			sb.append('0'); // pad from "1" to "01"
		}
		sb.append(i + "");
		if (j < 10)
		 {
			sb.append('0'); // pad from "1" to "01"
		}
		sb.append(j + "");

		KeyboardKeys ret1 = myKeys.get(sb.toString());

		if (ret1 == null) {
			Log.debug("KB Error: " + sb.toString());
		}
		sb.append(getKeyboardMode()); // append 'A' for acute , ' ' for default
										// etc

		KeyboardKeys ret2 = myKeys.get(sb.toString());

		// check for AltGr (Q) code if no accent etc available
		if (ret2 == null && getAltGrButton().isSelected()) {
			sb.setLength(sb.length() - 1); // remove MODE
			sb.append("Q");
			ret2 = myKeys.get(sb.toString());
		}

		return ret2 != null ? ret2 : ret1;
	}

	private JButton getButton(final int i, final int j) {
		if (Buttons[i][j] == null) {
			KeyboardKeys thisKeys = getKey(i, j);
			Buttons[i][j] = new JButton();
			updateButton(i, j);
			Insets Inset = new Insets(0, 0, 0, 0);
			Buttons[i][j].setMargin(Inset);
			String text = Upper() ? thisKeys.getUpperCase()
					: thisKeys.getLowerCase();

			Buttons[i][j].setText(processSpecialKeys(text));

			Buttons[i][j].addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					String txt = Buttons[i][j].getText();
					if ("\u2190".equals(txt)) {
						startAutoRepeat("<left>");
					} else if ("\u2191".equals(txt)) {
						startAutoRepeat("<up>");
					} else if ("\u2192".equals(txt)) {
						startAutoRepeat("<right>");
					} else if ("\u2193".equals(txt)) {
						startAutoRepeat("<down>");
					} else if ("\u21a4".equals(txt)) {
						startAutoRepeat("<backspace>");
					}
					// Application.debug(Buttons[i][j].getText());
				}

				@Override
				public void mouseReleased(MouseEvent e) {
					stopAutoRepeat();
				}

				@Override
				public void mouseExited(MouseEvent e) {
					stopAutoRepeat();

				}
			});

			Buttons[i][j].addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					buttonPressed(i, j);
				}
			});
		}
		return Buttons[i][j];
	}

	void buttonPressed(int i, int j) {
		// don't insert if timer running
		// (done in timer on auto-repeat)
		if (timer == null || !timer.isRunning()) {
			insertKeyText(getKey(i, j));
		}

		boolean doUpdateButtons = false;

		// reset buttons on a keypress (one-shot mode)
		if (getCapsLockButton().isSelected()) {
			getCapsLockButton().setSelected(false);
			doUpdateButtons = true;
		}

		if (getAltGrButton().isSelected()) {
			getAltGrButton().setSelected(false);
			doUpdateButtons = true;
		}

		if (getCtrlButton().isSelected()) {
			getCtrlButton().setSelected(false);
			doUpdateButtons = true;
		}

		if (doUpdateButtons) {
			updateButtons();
		}

	}

	private String processSpecialKeys(String text) {

		// check first for speed
		if (!text.startsWith("<")) {
			return text;
		}

		if ("<enter>".equals(text)) {
			return unicodeString('\u21b2', "");
		}
		if ("<backspace>".equals(text)) {
			return "\u21a4";
		}
		if ("<escape>".equals(text)) {
			return (app == null) ? escText : loc.getMenu("Esc");
		}
		if ("<left>".equals(text)) {
			return "\u2190";
		}
		if ("<up>".equals(text)) {
			return "\u2191";
		}
		if ("<right>".equals(text)) {
			return "\u2192";
		}
		if ("<down>".equals(text)) {
			return "\u2193";
		}
		if ("<E>".equals(text))
		 {
			return "\u00D710\u207F"; // *10^n
		}
		if ("<multiply>".equals(text)) {
			return Unicode.MULTIPLY + "";
		}
		if ("<divide>".equals(text)) {
			return Unicode.DIVIDE + "";
		}
		if ("<minus>".equals(text)) {
			return Unicode.MINUS + "";
		}
		return text;
	}

	private String unicodeString(char c, String alternative) {
		if (getCurrentFont().canDisplay(c)) {
			return c + "";
		}
		return alternative;
	}

	HashMap<Character, Boolean> characterIsTooWide = new HashMap<>(
			200);

	private void updateButton(int i, int j) {
		KeyboardKeys k = getKey(i, j);
		if (Upper()) {
			Buttons[i][j].setText(processSpecialKeys(k.getUpperCase()));
		} else {
			Buttons[i][j].setText(processSpecialKeys(k.getLowerCase()));
		}
		if (getKeyboardMode() == KEYBOARD_NUMERIC
				&& (i > 5 || j > 6 || (i == 5 && j > 4))) {
			Buttons[i][j].setVisible(false);
		} else {
			Buttons[i][j].setVisible(true);
		}
		setTooltip(i, j);
		// skip a row (for spacebar etc)
		int ii = (i == 5 && getKeyboardMode() != KEYBOARD_NUMERIC) ? 6 : i;

		int height = (int) buttonSizeY;
		int width = (int) buttonSizeX;
		int xOffset = 0;
		if (getKeyboardMode() == KEYBOARD_NUMERIC) {
			xOffset = (j > 0 ? 1 : 0) + (j > 1 && i < 5 ? 1 : 0)
					+ (j > 2 ? 1 : 0) + (j > 2 && i < 5 ? 1 : 0)
					+ (j > 4 ? 1 : 0) + (i == 5 && j > 0 ? 7 : 0);
		}
		// enter key: double height
		if (i == 3 && j == 13) {
			Buttons[i][j].setVisible(getKeyboardMode() == KEYBOARD_MATH);
		}
		if (i == 2 && j == 13 && getKeyboardMode() != KEYBOARD_MATH) {
			height *= 2;
		}
		if (i < 5 && j < 3 && getKeyboardMode() == KEYBOARD_NUMERIC) {
			width *= 1.5;
		}
		if (i == 5 && j == 0 && getKeyboardMode() == KEYBOARD_NUMERIC) {
			width *= 4.5;
		}

		Buttons[i][j].setBounds(
				new Rectangle((int) (0.5 + buttonSizeX * (j + 0.5 * xOffset)),
						(int) (0.5 + buttonSizeY * (ii - 1)), width, height));

		String text = Buttons[i][j].getText();
		int len = text.length();

		if (len == 0) {
			len = 1;
			text = " ";

		}

		if (len == 1) {

			// make sure extra-wide characters fit (eg <=> \u21d4 )

			FontRenderContext frc = new FontRenderContext(null, true, true);
			double wideCharWidth = getCurrentFont()
					.getStringBounds(wideChar + "", frc).getWidth();
			double charWidth = getCurrentFont().getStringBounds(text, frc)
					.getWidth();
			boolean oversize = charWidth > wideCharWidth;

			if (oversize) {
				Buttons[i][j].setFont(
						getFont((int) minButtonSize() * 10 / 12, false));
			} else {
				Buttons[i][j].setFont(getFont((int) minButtonSize(), true));
			}
		} else {
			// make sure "Esc" fits
			FontMetrics fm = getFontMetrics(getCurrentFont());
			int width2 = fm.stringWidth(Buttons[i][j].getText()); // wide arrow
																	// <=>
			int w2 = fm.stringWidth(wideChar + "");
			if (i == 4 && j == 2 && getKeyboardMode() == KEYBOARD_NUMERIC) {
				Buttons[i][j].setFont(getFont(
						(int) (minButtonSize() * 1.5 * w2 / width2), false));
			} else {
				Buttons[i][j].setFont(
						getFont((int) (minButtonSize() * w2 / width2), false));
			}
		}

	}

	private void setTooltip(int i, int j) {
		String text = null;
		if (getKeyboardMode() == KEYBOARD_NUMERIC) {
			String src = Buttons[i][j].getText();
			if (":=".equals(src)) {
				text = "Assignment";
			} else if ("$".equals(src)) {
				text = "DynamicReference";
			} else if ("#".equals(src)) {
				text = "StaticReference";
			} else if ((Unicode.VECTOR_PRODUCT + "").equals(src)) {
				text = "VectorProduct";
			}

			if (text != null) {
				text = loc.getMenu("Symbol." + text);
			}
		}

		Buttons[i][j].setToolTipText(text);

	}

	private Font getCurrentFont() {
		if (currentFont != null) {
			return currentFont;
		}

		return getFont((int) (minButtonSize()), true);
	}

	private HashMap<Integer, Font> fontsHash = new HashMap<>(30);

	private Font getFont(int size, boolean setFont) {

		Integer Size = Integer.valueOf(size);

		Font ret = fontsHash.get(Size);

		// all OK, return
		if (ret != null) {
			return ret;
		}

		int maxSize = 100;
		int minSize = 1;

		// interval bisection method to find desired fontsize
		while (minSize != maxSize - 1) {
			// Application.debug(minSize+" "+maxSize);
			// better than (low+high)/2 for positive numbers
			int midSize = (minSize + maxSize) >>> 1;

			getDummyButton().setFont(fonts[midSize]);
			getDummyButton().setText(wideChar + "");
			Dimension buttonSize = DummyButton.getPreferredSize();

			int wideCharSize = buttonSize.width;

			if (wideCharSize < size) {
				minSize = midSize;
			} else {
				maxSize = midSize;
			}

		}

		/*
		 * fallback for Mac OS getPreferredSize() is returning a minimum of
		 * width = 75 height = 29 so we just choose size / 2 for the fontSize
		 */
		if (minSize < 3) {
			minSize = size / 2;
		}

		if (setFont) {
			currentFont = fonts[minSize];
		}
		fontsHash.put(Size, fonts[minSize]);
		// Application.debug("KB: storing "+size+" "+minSize);
		return fonts[minSize];

	}

	private Hashtable<String, KeyboardKeys> myKeys = new Hashtable<>();

	private Locale kbLocale = null;

	public void setKbLocale(Locale loc) {
		readConf(app, loc, false);
		doSetLabels();
	}

	void readConf(AppD appD, Locale loc0, boolean math) {

		ResourceBundle rbKeyboard;

		Locale locale;

		if (appD != null) {
			String locName = ((KeyboardSettings) appD.getSettings()
					.getKeyboard()).getKeyboardLocale();
			if (locName == null) {
				locale = appD.getLocale();
			} else {
				locale = new Locale(locName);
			}

		} else {
			locale = getLocale();
		}

		kbLocale = locale;

		if (math) {
			rbKeyboard = MyResourceBundle.createBundle(
					"/org/geogebra/desktop/gui/virtualkeyboard/keyboardMath",
					locale);
		} else {
			if (loc0 == null) {
				rbKeyboard = MyResourceBundle.createBundle(
						"/org/geogebra/desktop/gui/virtualkeyboard/keyboard",
						locale);
			} else {
				rbKeyboard = MyResourceBundle.createBundle(
						"/org/geogebra/desktop/gui/virtualkeyboard/keyboard",
						loc0);
				kbLocale = loc0;
			}
		}

		Enumeration<String> keys = rbKeyboard.getKeys();

		while (keys.hasMoreElements()) {
			String keyU = keys.nextElement();

			if (keyU.endsWith("U")) {
				KeyboardKeys keyItem = new KeyboardKeys();
				String key = keyU.substring(0, keyU.length() - 1);

				String valueU = rbKeyboard.getString(keyU);
				String valueL = rbKeyboard.getString(key + "L");

				keyItem.setLowerCase(valueL);
				keyItem.setUpperCase(valueU);

				myKeys.put(key, keyItem);
			}
		}
	}

	/*
	 * called when eg language changed
	 */
	@Override
	public void setLabels() {

		setFonts();

		setTitle((app == null) ? "Virtual Keyboard"
				: loc.getMenu("VirtualKeyboard"));

		readConf(app, null, false);
		doSetLabels();

	}

	private void doSetLabels() {
		if (kbLocale.getLanguage().equals("ml")) {
			wideChar = '\u0d4c'; // widest Malayalan char
		} else if (kbLocale.getLanguage().equals("ar")) {
			wideChar = '\u0636'; // widest Arabic char
		} else {
			wideChar = wideCharDefault;
		}

		if (fontsHash != null) {
			fontsHash.clear();
		}

		if (app != null) {
			getCtrlButton().setText(loc.getMenu("Ctrl"));
			getAltButton().setText(loc.getMenu("Alt"));
			getAltGrButton().setText(loc.getMenu("AltGr"));
			updateCtrlButton();
			updateAltButton();
			updateAltGrButton();
		}
		getAltButton().setSelected(false);
		getAltGrButton().setSelected(false);
		getMathButton().setSelected(false);
		getNumericButton().setSelected(false);
		getGreekButton().setSelected(false);
		getEnglishButton().setSelected(false);
		getCtrlButton().setSelected(false);
		getCapsLockButton().setSelected(false);
		setKEYBOARD_MODE(KEYBOARD_NORMAL);
		updateButtons();

	}


	public WindowsUnicodeKeyboard getKeyboard() {
		WindowsUnicodeKeyboard kb = null;
		try {
			kb = new WindowsUnicodeKeyboard();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return kb;
	}

	private Timer timer;
	private String timerInsertStr = "";

	final void startAutoRepeat(String str) {
		if (timer == null) {
			timer = new Timer(1000, this);
		}
		timer.stop();
		timer.setDelay(1000);
		timer.start();
		timer.setDelay(200); // long first pause then quicker repeat
		timerInsertStr = str;
		insertAutoRepeatString();

	}

	private void insertAutoRepeatString() {
		((GuiManagerD) app.getGuiManager()).insertStringIntoTextfield(
				timerInsertStr, getAltButton().isSelected(),
				getCtrlButton().isSelected(), getCapsLockButton().isSelected());
	}

	final void stopAutoRepeat() {
		if (timer != null) {
			timer.stop();
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// timer event
		insertAutoRepeatString();
	}

	public void setWindowWidth(int windowWidth) {
		this.windowWidth = windowWidth;
		setSize(windowWidth, windowHeight);
	}

	public void setWindowHeight(int windowHeight) {
		this.windowHeight = windowHeight;
		setSize(windowWidth, windowHeight);
	}

	@Override
	public void settingsChanged(AbstractSettings settings) {
		KeyboardSettings kbs = (KeyboardSettings) settings;
		setWindowHeight(kbs.getKeyboardHeight());
		setWindowWidth(kbs.getKeyboardWidth());
		Locale newLocale = kbs.getKeyboardLocale() == null ? app.getLocale()
				: new Locale(kbs.getKeyboardLocale());
		if (!newLocale.equals(kbLocale)) {
			setKbLocale(newLocale);
		}
	}

	public char getKeyboardMode() {
		return KEYBOARD_MODE;
	}

	public void setKEYBOARD_MODE(char kEYBOARD_MODE) {
		KEYBOARD_MODE = kEYBOARD_MODE;
	}

}
