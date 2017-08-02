package org.geogebra.desktop.gui.view.properties;

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

import org.geogebra.common.gui.view.properties.PropertiesStyleBar;
import org.geogebra.common.gui.view.properties.PropertiesView;
import org.geogebra.common.main.App;
import org.geogebra.common.main.OptionType;
import org.geogebra.common.util.debug.Log;
import org.geogebra.desktop.gui.util.PopupMenuButtonD;
import org.geogebra.desktop.main.AppD;

public class PropertiesStyleBarD extends PropertiesStyleBar {

	private static final int MARGIN_Y = 5;
	PropertiesView propertiesView;
	protected AppD app;

	protected PopupMenuButtonD btnOption;
	private JPopupMenu menu;

	private JToolBar toolbar;
	private JPanel wrappedPanel;

	protected HashMap<OptionType, AbstractButton> buttonMap;

	private AbstractButton objectButton;

	public PropertiesStyleBarD(PropertiesView propertiesView, AppD app) {
		this.propertiesView = propertiesView;
		this.app = app;

		this.wrappedPanel = new JPanel();

		btnOption = new PopupMenuButtonD(app);
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

		// add(btnOption);
		// add(Box.createVerticalStrut(28));
	}

	void buildGUI() {

		toolbar = new JToolBar();
		toolbar.setFloatable(false);

		buttonMap = new HashMap<OptionType, AbstractButton>();

		ButtonGroup btnGroup = new ButtonGroup();
		for (final OptionType type : OptionType.values()) {
			final PropertiesButton btn = newPropertiesButton(type);
			if (btn != null) {
				btn.setFont(app.getPlainFont());
				btn.setToolTipText(propertiesView.getTypeString(type));
				ImageIcon icon = PropertiesViewD.getTypeIcon(app, type);
				if (icon == null) {
					Log.error("No icon for" + type);
				} else {
					btn.setIcon(icon);
					btn.setPreferredSize(new Dimension(icon.getIconWidth(),
							icon.getIconHeight()));
					btn.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							propertiesView.setOptionPanel(type);
						}
					});
					btnGroup.add(btn);
					toolbar.add(btn);
					buttonMap.put(type, btn);
					// mi.setSelected(type ==
					// propertiesView.getSelectedOptionType());
					if (type == OptionType.OBJECTS
							|| type == OptionType.SPREADSHEET) {
						toolbar.addSeparator();
					}
				}
			}
		}
		objectButton = buttonMap.get(OptionType.OBJECTS);

		// disable object button if no object
		if (app.getKernel().isEmpty()) {
			setObjectButtonEnable(false);
		}

		this.wrappedPanel.setLayout(new BorderLayout());
		this.wrappedPanel.add(toolbar, BorderLayout.NORTH);
		// this.add(titlePanel, BorderLayout.SOUTH);
		this.wrappedPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(0, 0, 0, 0,
						SystemColor.controlShadow),
				BorderFactory.createMatteBorder(0, 0, 1, 0,
						SystemColor.controlLtHighlight)));
		// this.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
	}

	/**
	 * create a new properties button if type is compatible
	 * 
	 * @param type
	 *            type
	 * @return new properties button
	 */
	protected PropertiesButton newPropertiesButton(OptionType type) {

		if (type == OptionType.EUCLIDIAN3D) { // used only for 3D
			return null;
		}

		if (type == OptionType.EUCLIDIAN_FOR_PLANE) { // used only for 3D
			return null;
		}

		return new PropertiesButton();
	}

	public PopupMenuButtonD getBtnOption() {
		return btnOption;
	}

	public void updateGUI() {

		OptionType seltype = propertiesView.getSelectedOptionType();
		btnOption.setFixedIcon(PropertiesViewD.getTypeIcon(app,
				propertiesView.getSelectedOptionType()));
		btnOption.setText(propertiesView.getTypeString(
				propertiesView.getSelectedOptionType()) + downTriangle);

		buttonMap.get(seltype).setSelected(true);

		buttonMap.get(OptionType.EUCLIDIAN)
				.setVisible(app.getGuiManager().showView(App.VIEW_EUCLIDIAN));

		buttonMap.get(OptionType.EUCLIDIAN2)
				.setVisible(app.getGuiManager().showView(App.VIEW_EUCLIDIAN2));

		buttonMap.get(OptionType.SPREADSHEET)
				.setVisible(app.getGuiManager().showView(App.VIEW_SPREADSHEET));

		buttonMap.get(OptionType.CAS)
				.setVisible(app.getGuiManager().showView(App.VIEW_CAS));
	}

	/**
	 * create a new menu item if type is compatible
	 * 
	 * @param type
	 *            type
	 * @return new menu item
	 */
	protected JMenuItem newJMenuItem(OptionType type) {

		if (type == OptionType.EUCLIDIAN3D) { // used only for 3D
			return null;
		}

		if (type == OptionType.EUCLIDIAN_FOR_PLANE) { // used only for 3D
			return null;
		}

		return new JMenuItem();

	}

	void buildMenu() {

		if (menu == null) {
			menu = new JPopupMenu();
		}
		menu.removeAll();

		for (final OptionType type : OptionType.values()) {
			final JMenuItem mi = newJMenuItem(type);
			if (mi != null) {
				mi.setFont(app.getPlainFont());
				mi.setBackground(Color.white);
				mi.setText(propertiesView.getTypeString(type));
				mi.setIcon(PropertiesViewD.getTypeIcon(app, type));
				mi.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						propertiesView.setOptionPanel(type);
						buildMenu();
						btnOption.setFixedIcon(mi.getIcon());
						btnOption.setText(mi.getText() + downTriangle);
					}
				});
				menu.add(mi);
				// mi.setSelected(type ==
				// propertiesView.getSelectedOptionType());
				if (type == OptionType.OBJECTS
						|| type == OptionType.SPREADSHEET) {
					menu.addSeparator();
				}
			}
		}

		app.setComponentOrientation(menu);

	}

	/**
	 * Update the labels of the components (e.g. if the language changed).
	 */
	public void setLabels() {

		for (final OptionType type : OptionType.values()) {
			AbstractButton button = buttonMap.get(type);
			if (button != null) {
				button.setToolTipText(propertiesView.getTypeString(type));
			}
		}

	}

	/**
	 * 
	 */
	public void setObjectsToolTip() {

		objectButton.setToolTipText(
				propertiesView.getTypeString(OptionType.OBJECTS));

	}

	/**
	 * sets if object button is enabled
	 * 
	 * @param flag
	 *            flag
	 */
	public void setObjectButtonEnable(boolean flag) {
		objectButton.setEnabled(flag);
	}

	protected static class PropertiesButton extends JToggleButton {

		// add(Box.createVerticalStrut(2
		private static final long serialVersionUID = 1L;

		private JToolTip tip;

		public PropertiesButton() {
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
		public Point getToolTipLocation(MouseEvent event) {
			Point p = new Point();
			p.y = this.getY();
			p.x = 0;// this.getX();
			if (tip != null) {
				p.y -= tip.getHeight();
			} else {
				p.y -= this.getHeight() + 5;
			}
			return p;
		}

	}

	/**
	 * Listeners that give the tool tip a custom initial delay = 0
	 */
	public static class ToolTipMouseAdapter extends MouseAdapter {
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

	public void reinit() {
		wrappedPanel.removeAll();
		buildGUI();
		updateGUI();
		Dimension d = wrappedPanel.getPreferredSize();
		d.height = app.getScaledIconSize() + 2 * MARGIN_Y;
		wrappedPanel.setPreferredSize(d);
	}

}
