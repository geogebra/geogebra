package geogebra.gui.view.properties;

import geogebra.common.main.AbstractApplication;
import geogebra.gui.dialog.options.OptionsUtil;
import geogebra.gui.util.PopupMenuButton;
import geogebra.gui.view.properties.PropertiesView.OptionType;
import geogebra.main.Application;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;

public class PropertiesStyleBar extends JPanel {

	PropertiesView propertiesView;
	private Application app;

	protected PopupMenuButton btnOption;
	private JPopupMenu menu;
	
	private JToolBar toolbar;
	private JLabel lblTitle;
	
	private HashMap<OptionType,AbstractButton> buttonMap;

	private static final String downTriangle = "  \u25BE  ";

	public PropertiesStyleBar(PropertiesView propertiesView, Application app) {
		this.propertiesView = propertiesView;
		this.app = app;

		

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
		

		lblTitle = new JLabel();
		JPanel titlePanel = OptionsUtil.flowPanel(10, lblTitle);

		toolbar = new JToolBar();
		toolbar.setFloatable(false);

		
		buttonMap = new HashMap<OptionType,AbstractButton>(); 
		
		ButtonGroup btnGroup = new ButtonGroup();
		for (final OptionType type : OptionType.values()) {
			final JToggleButton btn = new JToggleButton();
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
		
		this.setLayout(new BorderLayout());
		this.add(toolbar, BorderLayout.NORTH);
	//	this.add(titlePanel, BorderLayout.SOUTH);
		this.setBorder(BorderFactory.createCompoundBorder(
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
		
		lblTitle.setFont(app.getBoldFont());
		lblTitle.setText(propertiesView.getTypeString(seltype));
		buttonMap.get(seltype).setSelected(true);
		
		buttonMap.get(OptionType.EUCLIDIAN).setVisible(
				app.getGuiManager()
						.showView(AbstractApplication.VIEW_EUCLIDIAN));
		
		buttonMap.get(OptionType.EUCLIDIAN2).setVisible(
				app.getGuiManager()
						.showView(AbstractApplication.VIEW_EUCLIDIAN2));
		
		buttonMap.get(OptionType.SPREADSHEET).setVisible(
				app.getGuiManager()
						.showView(AbstractApplication.VIEW_SPREADSHEET));
		
		buttonMap.get(OptionType.CAS).setVisible(
				app.getGuiManager()
						.showView(AbstractApplication.VIEW_CAS));
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

		buildMenu();

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

}
