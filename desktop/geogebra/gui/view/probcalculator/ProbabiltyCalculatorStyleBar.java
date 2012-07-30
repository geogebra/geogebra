package geogebra.gui.view.probcalculator;

import geogebra.common.main.App;
import geogebra.gui.view.spreadsheet.statdialog.PlotSettings;
import geogebra.main.AppD;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.MenuElement;
import javax.swing.SwingConstants;

/**
 * StyleBar for the ProbabilityCalculator view
 * @author G. Sturr
 *
 */
public class ProbabiltyCalculatorStyleBar extends JToolBar implements ActionListener{

	private static final long serialVersionUID = 1L;
	
	private AppD app;
	/** probabililty calculator*/
	ProbabilityCalculator probCalc;
	/** icon height in pixels */
	protected int iconHeight = 18;
	/** rounding button*/
	JButton btnRounding;
	private JToggleButton btnCumulative, btnLineGraph, btnGrid;
	/** rounding popup menu*/
	JPopupMenu roundingPopup;

	/**
	 * @param app application
	 * @param probCalc probability calculator
	 */
	public ProbabiltyCalculatorStyleBar(AppD app, ProbabilityCalculator probCalc){

		this.probCalc = probCalc;
		this.app = app;
		this.setFloatable(false);
		createGUI();
		updateGUI();
		setLabels();

	}

	private void createGUI(){
		this.removeAll();	
		buildOptionsButton();
		
		
		btnCumulative = new JToggleButton(app.getImageIcon("cumulative_distribution.png"));
		btnCumulative.setSelected(probCalc.isCumulative());
		btnCumulative.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				probCalc.setCumulative(!probCalc.isCumulative());
			}
		});
		
		
		btnLineGraph = new JToggleButton(app.getImageIcon("line_graph.png"));
		btnLineGraph.setSelected(probCalc.isLineGraph());
		btnLineGraph.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				probCalc.setLineGraph(!probCalc.isLineGraph());
				probCalc.updateAll();
			}
		});
		
		
		btnGrid = new JToggleButton(app.getImageIcon("grid.gif"));
		btnGrid.setSelected(probCalc.getPlotSettings().showGrid);
		btnGrid.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				PlotSettings ps = probCalc.getPlotSettings();
				ps.showGrid = !ps.showGrid;
				probCalc.setPlotSettings(ps);
				probCalc.updatePlotSettings();
			}
		});
			
		
		add(btnRounding); 
		add(btnCumulative); 
		add(btnLineGraph); 
		//add(btnGrid);  (grid doesn't work well with discrete graphs and point capturing)
		
	}
	
	/**
	 * Updates the GUI
	 */
	public void updateGUI(){
		if(probCalc.getProbManager().isDiscrete(probCalc.getSelectedDist()))
			btnLineGraph.setVisible(true);
		else
			btnLineGraph.setVisible(false);
	}
	
	/**
	 * Updates localized labels
	 */
	public void setLabels(){
		btnRounding.setText(app.getMenu("Rounding"));
		btnLineGraph.setToolTipText(app.getPlain("Lines"));	
		btnCumulative.setToolTipText(app.getMenu("Cumulative"));
	}


	/** 
	 * Builds popup button with options menu items 
	 */
	private void buildOptionsButton(){

		btnRounding = new JButton(app.getImageIcon("triangle-down.png"));	
		btnRounding.setHorizontalTextPosition(SwingConstants.LEFT); 
		btnRounding.setHorizontalAlignment(SwingConstants.LEFT);
		roundingPopup = createRoundingPopup();
		
		btnRounding.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				// popup appears below the button
				roundingPopup.show(getParent(), btnRounding.getLocation().x,btnRounding.getLocation().y + btnRounding.getHeight());
			}
		});
	
		updateMenuDecimalPlaces(roundingPopup);
	
	}


	/**
	 * Update the menu with the current number format.
	 */
	private void updateMenuDecimalPlaces(JPopupMenu menu) {
		int printFigures = probCalc.getPrintFigures();
		int printDecimals = probCalc.getPrintDecimals();

		if (menu == null)
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
			MenuElement[] m = menu.getSubElements();
			((JRadioButtonMenuItem)m[pos]).setSelected(true);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

		
	private JPopupMenu createRoundingPopup(){
		JPopupMenu menu = new JPopupMenu();
		String[] strDecimalSpaces = app.getRoundingMenu();

		addRadioButtonMenuItems(menu, this,
				strDecimalSpaces, App.strDecimalSpacesAC, 0);

		return menu;
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
	private void addRadioButtonMenuItems(JPopupMenu menu, ActionListener al,
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
	
	public void actionPerformed(ActionEvent e) {

		String cmd = e.getActionCommand();

		// decimal places
		if (cmd.endsWith("decimals")) {
			try {
				String decStr = cmd.substring(0, 2).trim();
				int decimals = Integer.parseInt(decStr);
				// Application.debug("decimals " + decimals);
				probCalc.updatePrintFormat(decimals, -1);

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
				probCalc.updatePrintFormat(-1, figures);

			} catch (Exception ex) {
				app.showError(e.toString());
			}
		}
	}

}
