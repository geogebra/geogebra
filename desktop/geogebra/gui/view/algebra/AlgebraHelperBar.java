package geogebra.gui.view.algebra;

import geogebra.common.gui.view.algebra.AlgebraView.SortMode;
import geogebra.gui.util.PopupMenuButton;
import geogebra.main.AppD;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import javax.swing.JToolBar;

/**
 * Helper tool bar for the algebra view which displays some useful buttons to
 * change the functionality (e.g. show auxiliary objects).
 */
public class AlgebraHelperBar extends JToolBar implements ActionListener {
	/** */
	private static final long serialVersionUID = 1L;

	/**
	 * The algebra view which uses this tool bar.
	 */
	protected AlgebraViewD algebraView;

	/**
	 * Instance of the application.
	 */
	protected AppD app;

	/**
	 * Button to show/hide auxiliary objects in the algebra view.
	 */
	private JButton toggleAuxiliary;

	/**
	 * Button to toggle between the two tree modes of the algebra view: -
	 * Categorize objects by free / independent / auxiliary - Categorize objects
	 * by their type
	 */
	protected PopupMenuButton toggleTypeTreeMode;

	private PopupMenuButton btnTextSize;

	private JPopupMenu menu;

	/**
	 * Button to toggle LaTeX rendering
	 */
	// private JButton toggleLaTeX;

	/**
	 * Helper bar.
	 * 
	 * @param algebraView
	 * @param app
	 */
	public AlgebraHelperBar(AlgebraViewD algebraView, AppD app) {
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
	protected void addButtons() {

		toggleAuxiliary = new JButton(app.getImageIcon("auxiliary.png"));
		toggleAuxiliary.setFocusPainted(false);
		toggleAuxiliary.addActionListener(this);
		add(toggleAuxiliary);

		addSeparator();

		toggleTypeTreeMode = new PopupMenuButton(app);
		buildMenu();
		toggleTypeTreeMode.setPopupMenu(menu);
		toggleTypeTreeMode.setKeepVisible(true);
		toggleTypeTreeMode.setStandardButton(true); // mouse clicks over total
													// button region
		toggleTypeTreeMode.setIcon(app.getImageIcon("tree.png"));

		add(toggleTypeTreeMode);

	}

	/**
	 * Update the states of the tool bar buttons.
	 */
	public void updateStates() {
		toggleAuxiliary.setSelected(app.showAuxiliaryObjects());
		//toggleTypeTreeMode.setSelected(algebraView.getTreeMode().equals(SortMode.TYPE));
		// toggleLaTeX.setSelected(!algebraView.isRenderLaTeX());
	}

	/**
	 * Update the tool tip texts (used for language change).
	 */
	public void updateLabels() {
		toggleAuxiliary.setToolTipText(app.getPlainTooltip("AuxiliaryObjects"));

		toggleTypeTreeMode.setToolTipText(app.getPlainTooltip("SortObjectsBy"));
		buildMenu();

	}

	/**
	 * React to button presses.
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == toggleAuxiliary) {
			app.setShowAuxiliaryObjects(!app.showAuxiliaryObjects());
			toggleAuxiliary.setSelected(app.showAuxiliaryObjects());

		}
		// else if(e.getSource() == toggleLaTeX) {
		// algebraView.setRenderLaTeX(!algebraView.isRenderLaTeX());
		// toggleLaTeX.setSelected(!algebraView.isRenderLaTeX());
		// }
	}

	void buildMenu() {

		if (menu == null) {
			menu = new JPopupMenu();
		}
		menu.removeAll();

		JLabel title = new JLabel(app.getPlain("SortBy") + ":");
		title.setFont(app.getBoldFont());
		title.setIcon(app.getEmptyIcon());
		title.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
		add(title);

		menu.add(title);

		JCheckBoxMenuItem mi = new JCheckBoxMenuItem();
		mi.setFont(app.getPlainFont());
		mi.setBackground(Color.white);
		mi.setText(app.getPlain("Dependency"));
		mi.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				algebraView.setTreeMode(SortMode.DEPENDENCY);
				buildMenu();
			}
		});
		mi.setSelected(algebraView.getTreeMode() == SortMode.DEPENDENCY);
		menu.add(mi);

		mi = new JCheckBoxMenuItem();
		mi.setFont(app.getPlainFont());
		mi.setBackground(Color.white);
		mi.setText(app.getPlain("ObjectType"));
		mi.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				algebraView.setTreeMode(SortMode.TYPE);
				buildMenu();
			}
		});
		mi.setSelected(algebraView.getTreeMode() == SortMode.TYPE);
		menu.add(mi);

		mi = new JCheckBoxMenuItem();
		mi.setFont(app.getPlainFont());
		mi.setBackground(Color.white);
		mi.setText(app.getPlain("Layer"));
		mi.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				algebraView.setTreeMode(SortMode.LAYER);
				buildMenu();
			}
		});
		mi.setSelected(algebraView.getTreeMode() == SortMode.LAYER);
		menu.add(mi);

		mi = new JCheckBoxMenuItem();
		mi.setFont(app.getPlainFont());
		mi.setBackground(Color.white);
		mi.setText(app.getPlain("ConstructionOrder"));
		mi.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				algebraView.setTreeMode(SortMode.ORDER);
				buildMenu();
			}
		});
		mi.setSelected(algebraView.getTreeMode() == SortMode.ORDER);
		menu.add(mi);
		
		app.setComponentOrientation(menu);


	}
}
