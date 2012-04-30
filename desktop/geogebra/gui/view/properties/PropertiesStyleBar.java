package geogebra.gui.view.properties;

import geogebra.gui.util.PopupMenuButton;
import geogebra.gui.view.properties.PropertiesView.OptionType;
import geogebra.main.Application;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;

public class PropertiesStyleBar extends JToolBar {

	PropertiesView propertiesView;
	private Application app;

	protected PopupMenuButton btnOption;
	private JPopupMenu menu;

	private static final String downTriangle = "  \u25BE  ";

	public PropertiesStyleBar(PropertiesView propertiesView, Application app) {
		this.propertiesView = propertiesView;
		this.app = app;

		this.setFloatable(false);

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

		updateGUI();

		add(btnOption);
		add(Box.createVerticalStrut(28));
	}

	public void updateGUI() {
		btnOption.setFixedIcon(getTypeIcon(propertiesView
				.getSelectedOptionType()));
		btnOption.setText(getTypeLabel(propertiesView.getSelectedOptionType())
				+ downTriangle);
	}

	void buildMenu() {

		if (menu == null) {
			menu = new JPopupMenu();
		}
		menu.removeAll();

		for (final OptionType type : OptionType.values()) {
			final JCheckBoxMenuItem mi = new JCheckBoxMenuItem();
			mi.setFont(app.getPlainFont());
			mi.setBackground(Color.white);
			mi.setText(getTypeLabel(type));
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
			mi.setSelected(type == propertiesView.getSelectedOptionType());
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

	private String getTypeLabel(OptionType type) {
		switch (type) {
		case DEFAULTS:
			return app.getPlain("Defaults");
		case SPREADSHEET:
			return app.getPlain("Spreadsheet");
		case EUCLIDIAN:
			return app.getPlain("DrawingPad");
		case EUCLIDIAN2:
			return app.getPlain("DrawingPad2");
		case CAS:
			return app.getMenu("CAS");
		case ADVANCED:
			return app.getMenu("Advanced");
		case OBJECTS:
			return app.getMenu("Objects");
		}
		return null;
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
		}
		return null;
	}

}
