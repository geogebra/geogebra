package geogebra.gui.util;

import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.arithmetic.ExpressionNodeConstants.StringType;
import geogebra.common.main.App;
import geogebra.main.AppD;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JRadioButtonMenuItem;

/**
 * Utility class to support non-default number formatting in a component.
 * Includes methods to store a user-selected number format, to apply the format and
 * to create a "Rounding" menu.
 * 
 * Code is adapted from gui.menubar.OptionsMenu
 *  
 * @author G. Sturr
 * 
 */
public class SpecialNumberFormat implements ActionListener {

	
	private AppD app;
	private SpecialNumberFormatInterface invoker;
	
	private JMenu menuDecimalPlaces;
	
	/**
	 * Default number format  
	 */
	private int printFigures = -1;
	private int printDecimals = 4;
	
	
	
	
	/**
	 * Constructor
	 * 
	 * @param app
	 * @param invoker
	 *            : the component utilizing this number format class
	 */
	public SpecialNumberFormat(AppD app, SpecialNumberFormatInterface invoker){
		
		this.app = app;
		this.invoker = invoker;
	}
	
	
	
	
	public int getPrintFigures() {
		return printFigures;
	}
	public int getPrintDecimals() {
		return printDecimals;
	}
	
	
	/**
	 * Converts number to string using the currently selected format 
	 * @param x number
	 * @return formated string
	 */
	public String format(double x){
		StringTemplate highPrecision;
		// override the default decimal place setting
		if(printDecimals >= 0)
			highPrecision = StringTemplate.printDecimals(StringType.GEOGEBRA, printDecimals,false);
		else
			highPrecision = StringTemplate.printFigures(StringType.GEOGEBRA, printFigures,false);

		// get the formatted string
		String result = app.getKernel().format(x,highPrecision);

		return result;
	}

	
	
	
	/**
	 * Creates a menu with number format options
	 * Note: this menu is derived from 
	 * @return
	 */
	public JMenu createMenuDecimalPlaces(){
		menuDecimalPlaces = new JMenu(app.getMenu("Rounding"));
		String[] strDecimalSpaces = app.getRoundingMenu();

		addRadioButtonMenuItems(menuDecimalPlaces, this,
				strDecimalSpaces, App.strDecimalSpacesAC, 0);

		updateMenuDecimalPlaces(); 
		
		return menuDecimalPlaces;
	}

	/**
	 * Update the menu to select the current format.
	 */
	private void updateMenuDecimalPlaces() {
		if (menuDecimalPlaces == null)
			return;
		int pos = -1;

		if (printFigures >= 0) {
			if (printFigures > 0 && printFigures < App.figuresLookup.length)
				pos = App.figuresLookup[printFigures];
		} else {
			if (printDecimals > 0 && printDecimals < App.decimalsLookup.length)
				pos = App.decimalsLookup[printDecimals];
		}

		try {
			((JRadioButtonMenuItem) menuDecimalPlaces.getMenuComponent(pos))
			.setSelected(true);
		} catch (Exception e) {
		}

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
				mi.setFont(app.getFontCanDisplayAwt(text));
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
	 * Listener for the Rounding menu. Notifies the invoking component of a
	 * format change with invoker.changedNumberFormat().
	 */
	public void actionPerformed(ActionEvent e) {

		String cmd = e.getActionCommand();

		// decimal places
		if (cmd.endsWith("decimals")) {
			try {
				String decStr = cmd.substring(0, 2).trim();
				int decimals = Integer.parseInt(decStr);
				// Application.debug("decimals " + decimals);

				printDecimals = decimals;
				printFigures = -1;
				
				invoker.changedNumberFormat();
				
			} catch (Exception ex) {
				app.showError(e.toString());
			}
		}

		// significant figures
		else if (cmd.endsWith("figures")) {
			try {
				String decStr = cmd.substring(0, 2).trim();
				int figures = Integer.parseInt(decStr);
				//	 Application.debug("figures " + figures);

				printFigures = figures;
				printDecimals = -1;
				
				invoker.changedNumberFormat();
				
			} catch (Exception ex) {
				app.showError(e.toString());
			}
		}
	}


	
	
}
