package org.geogebra.desktop.gui.util;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JRadioButtonMenuItem;

import org.geogebra.common.gui.menubar.RoundingOptions;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.ExpressionNodeConstants.StringType;
import org.geogebra.common.main.App;
import org.geogebra.desktop.main.AppD;

/**
 * Utility class to support non-default number formatting in a component.
 * Includes methods to store a user-selected number format, to apply the format
 * and to create a "Rounding" menu.
 * 
 * Code is adapted from gui.menubar.OptionsMenu
 * 
 * @author G. Sturr
 * 
 */
public class SpecialNumberFormat implements ActionListener {

	private AppD app;
	private SpecialNumberFormatInterface invoker;
	private RoundingOptions roundingOptions;

	private JMenu menuDecimalPlaces;

	/**
	 * Default number format
	 */
	private int printFigures = -1;
	private int printDecimals = 4;

	/**
	 * Constructor
	 * 
	 * @param app application
	 * @param invoker
	 *            : the component utilizing this number format class
	 */
	public SpecialNumberFormat(AppD app, SpecialNumberFormatInterface invoker) {

		this.app = app;
		this.invoker = invoker;
		this.roundingOptions = new RoundingOptions(app.getLocalization());
	}

	public int getPrintFigures() {
		return printFigures;
	}

	public int getPrintDecimals() {
		return printDecimals;
	}

	/**
	 * Converts number to string using the currently selected format
	 * 
	 * @param x
	 *            number
	 * @return formatted string
	 */
	public String format(double x) {
		StringTemplate highPrecision;
		// override the default decimal place setting
		if (printDecimals >= 0) {
			highPrecision = StringTemplate.printDecimals(StringType.GEOGEBRA,
					printDecimals, false);
		} else {
			highPrecision = StringTemplate.printFigures(StringType.GEOGEBRA,
					printFigures, false);
		}

		// get the formatted string
		String result = app.getKernel().format(x, highPrecision);

		return result;
	}

	/**
	 * Creates a menu with number format options Note: this menu is derived from
	 * 
	 * @return decimal places menu
	 */
	public JMenu createMenuDecimalPlaces() {
		menuDecimalPlaces = new JMenu(
				app.getLocalization().getMenu("Rounding"));
		String[] strDecimalSpaces = app.getLocalization().getRoundingMenu();

		addRadioButtonMenuItems(menuDecimalPlaces, this, strDecimalSpaces,
				App.getStrDecimalSpacesAC(), 0);

		updateMenuDecimalPlaces();

		return menuDecimalPlaces;
	}

	/**
	 * Update the menu to select the current format.
	 */
	private void updateMenuDecimalPlaces() {
		if (menuDecimalPlaces == null) {
			return;
		}
		int pos = -1;

		if (printFigures >= 0) {
			if (printFigures > 0
					&& printFigures < roundingOptions.figuresLookupLength()) {
				pos = roundingOptions.figuresLookup(printFigures);
			}
		} else {
			if (printDecimals > 0
					&& printDecimals < roundingOptions.decimalsLookupLength()) {
				pos = roundingOptions.decimalsLookup(printDecimals);
			}
		}

		try {
			((JRadioButtonMenuItem) menuDecimalPlaces.getMenuComponent(pos))
					.setSelected(true);
		} catch (Exception e) {
		}

		app.setComponentOrientation(menuDecimalPlaces);

	}

	/**
	 * Create a set of radio buttons automatically.
	 */
	private void addRadioButtonMenuItems(JMenu menu, ActionListener al,
			String[] items, String[] actionCommands, int selectedPos) {
		JRadioButtonMenuItem mi;
		ButtonGroup bg = new ButtonGroup();
		// String label;

		for (int i = 0; i < items.length; i++) {
			if ("---".equals(items[i])) {
				menu.addSeparator();
			} else {
				String text = app.getLocalization().getMenu(items[i]);
				mi = new JRadioButtonMenuItem(text);
				mi.setFont(app.getFontCanDisplayAwt(text));
				if (i == selectedPos) {
					mi.setSelected(true);
				}
				mi.setActionCommand(actionCommands[i]);
				mi.addActionListener(al);
				bg.add(mi);
				menu.add(mi);
			}
		}
	}

	/**
	 * Listener for the Rounding menu. Notifies the invoking component of a
	 * format change with invoker.changedNumberFormat().
	 */
	@Override
	public void actionPerformed(ActionEvent e) {

		String cmd = e.getActionCommand();

		// decimal places
		if (cmd.endsWith("decimals")) {
			try {
				String decStr = cmd.substring(0, 2).trim();
				printDecimals = Integer.parseInt(decStr);
				printFigures = -1;

				invoker.changedNumberFormat();

			} catch (Exception ex) {
				app.showGenericError(ex);
			}
		}

		// significant figures
		else if (cmd.endsWith("figures")) {
			try {
				String decStr = cmd.substring(0, 2).trim();
				printFigures = Integer.parseInt(decStr);
				printDecimals = -1;

				invoker.changedNumberFormat();

			} catch (Exception ex) {
				app.showError(e.toString());
			}
		}
	}

}
