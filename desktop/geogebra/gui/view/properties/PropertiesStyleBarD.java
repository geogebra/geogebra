package geogebra.gui.view.properties;

import geogebra.common.gui.view.properties.PropertiesView;
import geogebra.common.gui.view.properties.PropertiesView.OptionType;
import geogebra.common.main.App;
import geogebra.gui.util.PopupMenuButton;
import geogebra.main.AppD;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.JToolTip;
import javax.swing.SwingConstants;
import javax.swing.ToolTipManager;

public class PropertiesStyleBarD extends geogebra.common.gui.view.properties.PropertiesStyleBar {

	PropertiesView propertiesView;
	private AppD app;

	protected PopupMenuButton btnOption;
	private JPopupMenu menu;
	
	private JToolBar toolbar;
	private JPanel wrappedPanel;
	
	private HashMap<OptionType,AbstractButton> buttonMap;
	
	private AbstractButton objectButton;

	public PropertiesStyleBarD(PropertiesView propertiesView, AppD app) {
		this.propertiesView = propertiesView;
		this.app = app;
		
		this.wrappedPanel = new JPanel();

		

		btnOption = new PopupMenuButton(app);
		buildMenu();
		menu.getSelectionModel().setSelectedIndex(0);
		btnOption.setPopupMenu(menu);
		btnOption.setKeepVisible(true);
		btnOption.setStandardButton(true); // mouse clicks over total
		// button region
		btnOption.setHorizontalTextPosition(SwingConstants.RIGHT);
		Dimension d = btnOption.getPreferredSize();
		d.width = menu.getPreferredSize().width;
		btnOption.setPreferredSize(d);

		buildGUI();
		updateGUI();

		//add(btnOption);
		//add(Box.createVerticalStrut(28));
	}

	
	void buildGUI() {
		
		toolbar = new JToolBar();
		toolbar.setFloatable(false);

		
		buttonMap = new HashMap<OptionType,AbstractButton>(); 
		
		ButtonGroup btnGroup = new ButtonGroup();
		for (final OptionType type : OptionType.values()) {
			final PropertiesButton btn = new PropertiesButton();
			btn.setFont(app.getPlainFont());
			btn.setToolTipText(propertiesView.getTypeString(type));
			btn.setIcon(getTypeIcon(type));
			btn.setPreferredSize(new Dimension(24,30));
			btn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					propertiesView.setOptionPanel(type);
				}
			});
			btnGroup.add(btn);
			toolbar.add(btn);
			buttonMap.put(type, btn);
			//mi.setSelected(type == propertiesView.getSelectedOptionType());
			if (type == OptionType.OBJECTS || type == OptionType.SPREADSHEET) {
				toolbar.addSeparator();
			}
		}
		objectButton = buttonMap.get(OptionType.OBJECTS);
		
		//disable object button if no object
		if (app.getKernel().isEmpty())
			setObjectButtonEnable(false);
		
		this.wrappedPanel.setLayout(new BorderLayout());
		this.wrappedPanel.add(toolbar, BorderLayout.NORTH);
	//	this.add(titlePanel, BorderLayout.SOUTH);
		this.wrappedPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(0, 0, 0, 0, SystemColor.controlShadow),
				BorderFactory.createMatteBorder(0, 0, 1, 0, SystemColor.controlLtHighlight)));
		//this.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
	}
	
	
	
	public PopupMenuButton getBtnOption() {
		return btnOption;
	}

	public void updateGUI() {
		OptionType seltype = propertiesView.getSelectedOptionType();
		btnOption.setFixedIcon(getTypeIcon(propertiesView
				.getSelectedOptionType()));
		btnOption.setText(propertiesView.getTypeString(propertiesView.getSelectedOptionType())
				+ downTriangle);
		
		buttonMap.get(seltype).setSelected(true);
		
		buttonMap.get(OptionType.EUCLIDIAN).setVisible(
				app.getGuiManager()
						.showView(App.VIEW_EUCLIDIAN));
		
		buttonMap.get(OptionType.EUCLIDIAN2).setVisible(
				app.getGuiManager()
						.showView(App.VIEW_EUCLIDIAN2));
		
		buttonMap.get(OptionType.SPREADSHEET).setVisible(
				app.getGuiManager()
						.showView(App.VIEW_SPREADSHEET));
		
		buttonMap.get(OptionType.CAS).setVisible(
				app.getGuiManager()
						.showView(App.VIEW_CAS));
	}

	
	void buildMenu() {

		if (menu == null) {
			menu = new JPopupMenu();
		}
		menu.removeAll();

		for (final OptionType type : OptionType.values()) {
			final JMenuItem mi = new JMenuItem();
			mi.setFont(app.getPlainFont());
			mi.setBackground(Color.white);
			mi.setText(propertiesView.getTypeString(type));
			mi.setIcon(getTypeIcon(type));
			mi.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					propertiesView.setOptionPanel(type);
					buildMenu();
					btnOption.setFixedIcon(mi.getIcon());
					btnOption.setText(mi.getText() + downTriangle);
				}
			});
			menu.add(mi);
			//mi.setSelected(type == propertiesView.getSelectedOptionType());
			if (type == OptionType.OBJECTS || type == OptionType.SPREADSHEET) {
				menu.addSeparator();
			}
		}
	}

	
	
	
	/**
	 * Update the labels of the components (e.g. if the language changed).
	 */
	public void setLabels() {
		
		for (final OptionType type : OptionType.values()) 
			buttonMap.get(type).setToolTipText(propertiesView.getTypeString(type));
		

	}
	
	/**
	 * 
	 */
	public void setObjectsToolTip() {
		
		objectButton.setToolTipText(propertiesView.getTypeString(OptionType.OBJECTS));
		
	
	}
	
	/**
	 * sets if object button is enabled
	 * @param flag flag
	 */
	public void setObjectButtonEnable(boolean flag){
		objectButton.setEnabled(flag);
	}

	
	private ImageIcon getTypeIcon(OptionType type) {
		switch (type) {
		case DEFAULTS:
			return app.getImageIcon("options-defaults224.png");
		case SPREADSHEET:
			return app.getImageIcon("view-spreadsheet24.png");
		case EUCLIDIAN:
			return app.getImageIcon("view-graphics24.png");
		case EUCLIDIAN2:
			return app.getImageIcon("view-graphics224.png");
		case CAS:
			return app.getImageIcon("view-cas24.png");
		case ADVANCED:
			return app.getImageIcon("options-advanced24.png");
		case OBJECTS:
			return app.getImageIcon("options-objects24.png");
		case LAYOUT:
			return app.getImageIcon("options-layout24.png");
		}
		return null;
	}
	
	
	private class PropertiesButton extends JToggleButton {

		private static final long serialVersionUID = 1L;
		
		private JToolTip tip;

		public PropertiesButton(){
			super();
			this.addMouseListener(new ToolTipMouseAdapter());
		}


		@Override
		public JToolTip createToolTip() {
			tip = super.createToolTip();
			tip.setBorder(BorderFactory.createCompoundBorder(tip.getBorder(),
					BorderFactory.createEmptyBorder(5, 5, 5, 5)));

			return tip;
		}

		@Override
		public Point getToolTipLocation(MouseEvent event){
			Point p = new Point();
			p.y = this.getY()+this.getHeight();
			p.x = 0; //this.getX();
			return  p;
		}

	}


	/**
	 * Listeners that give the tool tip a custom initial delay = 0
	 */
	public class ToolTipMouseAdapter extends MouseAdapter {
		private int defaultInitialDelay;
		private boolean preventToolTipDelay = true;

		@Override
		public void mouseEntered(MouseEvent e) {
			defaultInitialDelay = ToolTipManager.sharedInstance()
					.getInitialDelay();
			if (preventToolTipDelay) {
				ToolTipManager.sharedInstance().setInitialDelay(0);
			}

		}

		@Override
		public void mouseExited(MouseEvent e) {
			ToolTipManager.sharedInstance()
			.setInitialDelay(defaultInitialDelay);

		}
	}
	
	public JPanel getWrappedPanel() {
		return wrappedPanel; 
	}
	
}
