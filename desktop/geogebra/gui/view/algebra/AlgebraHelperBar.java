package geogebra.gui.view.algebra;

import geogebra.gui.util.PopupMenuButton;
import geogebra.gui.view.algebra.AlgebraView.SortMode;
import geogebra.main.Application;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JToolBar;

/**
 * Helper tool bar for the algebra view which displays some useful
 * buttons to change the functionality (e.g. show auxiliary objects).
 */
public class AlgebraHelperBar extends JToolBar implements ActionListener {
	/** */
	private static final long serialVersionUID = 1L;

	/**
	 * The algebra view which uses this tool bar.
	 */
	protected AlgebraView algebraView;
	
	/**
	 * Instance of the application.
	 */
	protected Application app;
	
	/**
	 * Button to show/hide auxiliary objects in the algebra view.
	 */
	private JButton toggleAuxiliary;
	
	/**
	 * Button to toggle between the two tree modes of the algebra view:
	 *  - Categorize objects by free / independent / auxiliary
	 *  - Categorize objects by their type 
	 */
	protected JButton toggleTypeTreeMode;

	private PopupMenuButton btnTextSize;

	private JPopupMenu menu;

	/**
	 * Button to toggle LaTeX rendering
	 */
//	private JButton toggleLaTeX;
	
	/**
	 * Helper bar.
	 * 
	 * @param algebraView
	 * @param app
	 */
	public AlgebraHelperBar(AlgebraView algebraView, Application app) {
		this.algebraView = algebraView;
		this.app = app;
		
		setFloatable(false);
		
		addButtons();
		
		updateStates();
		updateLabels();
	}
	
	/**
	 * add the buttons
	 */
	protected void addButtons(){
		
		toggleAuxiliary = new JButton(app.getImageIcon("auxiliary.png"));
		toggleAuxiliary.addActionListener(this);
		add(toggleAuxiliary);
		
		addSeparator();
		
		toggleTypeTreeMode = new JButton(app.getImageIcon("tree.png"));
		toggleTypeTreeMode.addActionListener(this);
		add(toggleTypeTreeMode);

	}
	
	/**
	 * Update the states of the tool bar buttons.
	 */
	public void updateStates() {
		toggleAuxiliary.setSelected(app.showAuxiliaryObjects());
		toggleTypeTreeMode.setSelected(algebraView.getTreeMode().equals(SortMode.TYPE));
//		toggleLaTeX.setSelected(!algebraView.isRenderLaTeX());
	}
	
	/**
	 * Update the tool tip texts (used for language change).
	 */
	public void updateLabels() {
		toggleAuxiliary.setToolTipText(app.getPlainTooltip("AuxiliaryObjects"));
		
		if(algebraView.getTreeMode().equals(SortMode.TYPE)) {
			toggleTypeTreeMode.setToolTipText(app.getPlainTooltip("TreeModeDependency"));
		} else {
			toggleTypeTreeMode.setToolTipText(app.getPlainTooltip("TreeModeType"));
		}
		if (menu != null) {
			buildMenu();
		}
	}

	/**
	 * React to button presses.
	 */
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == toggleAuxiliary) {
			app.setShowAuxiliaryObjects(!app.showAuxiliaryObjects());
			toggleAuxiliary.setSelected(app.showAuxiliaryObjects());
			
		} else if(e.getSource() == toggleTypeTreeMode) {
			
			if (menu == null) {
				buildMenu();
			}
			
			if (menu.isVisible()) menu.setVisible(false);
			else menu.show(toggleTypeTreeMode, toggleTypeTreeMode.getX(), toggleTypeTreeMode.getY());


		}
//		else if(e.getSource() == toggleLaTeX) {
//			algebraView.setRenderLaTeX(!algebraView.isRenderLaTeX());
//			toggleLaTeX.setSelected(!algebraView.isRenderLaTeX());
//		}
	}
	
	private void buildMenu() {
		menu = new JPopupMenu();
		
		menu.add(new JLabel(app.getPlain("SortBy")+":"));
		
		JMenuItem mi = new JMenuItem();		
		mi.setFont(app.getPlainFont());
		mi.setBackground(Color.white);
		mi.setText(app.getPlain("Dependency"));				
		mi.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				algebraView.setTreeMode(SortMode.DEPENDENCY);
			}
		});
		menu.add(mi);
		
		mi = new JMenuItem();		
		mi.setFont(app.getPlainFont());
		mi.setBackground(Color.white);
		mi.setText(app.getPlain("ObjectType"));				
		mi.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				algebraView.setTreeMode(SortMode.TYPE);
			}
		});
		menu.add(mi);
		
		mi = new JMenuItem();		
		mi.setFont(app.getPlainFont());
		mi.setBackground(Color.white);
		mi.setText(app.getPlain("Layer"));				
		mi.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				algebraView.setTreeMode(SortMode.LAYER);
			}
		});
		menu.add(mi);
		
		mi = new JMenuItem();		
		mi.setFont(app.getPlainFont());
		mi.setBackground(Color.white);
		mi.setText(app.getPlain("ConstructionOrder"));				
		mi.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				algebraView.setTreeMode(SortMode.ORDER);
			}
		});
		menu.add(mi);
		
	}
}
