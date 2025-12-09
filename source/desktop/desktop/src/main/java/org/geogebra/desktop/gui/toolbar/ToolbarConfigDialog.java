/*
 * GeoGebra - Dynamic Mathematics for Everyone
 * Copyright (c) GeoGebra GmbH, Altenbergerstr. 69, 4040 Linz, Austria
 * https://www.geogebra.org
 * 
 * This file is licensed by GeoGebra GmbH under the EUPL 1.2 licence and
 * may be used under the EUPL 1.2 in compatible projects (see Article 5
 * and the Appendix of EUPL 1.2 for details).
 * You may obtain a copy of the licence at:
 * https://interoperable-europe.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 * 
 * Note: The overall GeoGebra software package is free to use for
 * non-commercial purposes only.
 * See https://www.geogebra.org/license for full licensing details
 */

package org.geogebra.desktop.gui.toolbar;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JPanel;

import org.geogebra.desktop.gui.GuiManagerD;
import org.geogebra.desktop.gui.layout.DockPanelD;
import org.geogebra.desktop.main.AppD;
import org.geogebra.desktop.main.LocalizationD;

/**
 * Toolbar configuration dialog.
 * 
 * @author Markus Hohenwarter
 *
 */
public class ToolbarConfigDialog extends JDialog implements ActionListener {

	private static final long serialVersionUID = 1L;

	private AppD app;
	private ToolbarConfigPanel confPanel;

	private LocalizationD loc;

	/**
	 * Creates new dialog
	 * 
	 * @param app
	 *            application
	 */
	public ToolbarConfigDialog(AppD app) {
		super(app.getFrame(), false);
		this.app = app;
		this.loc = app.getLocalization();
		setTitle(loc.getMenu("Toolbar.Customize"));

		// list with panels
		JComboBox switcher = new JComboBox();
		switcher.addItem(new KeyValue(-1, loc.getMenu("General")));

		DockPanelD[] panels = ((GuiManagerD) app.getGuiManager())
				.getLayout().getDockManager().getPanels();

		int toolbarId = app.getGuiManager().getActiveToolbarId();
		int selIdx = 0;
		for (DockPanelD panel : panels) {
			if (panel.canCustomizeToolbar()) {
				int viewId = panel.getViewId();
				switcher.addItem(new KeyValue(viewId,
						loc.getMenu(panel.getViewTitle())));
				if (viewId == toolbarId) {
					selIdx = switcher.getItemCount() - 1;
				}
			}
		}

		switcher.addActionListener(this); // add at the end to not be notified
											// about items being added

		JPanel switcherPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

		getContentPane().setLayout(new BorderLayout(5, 5));
		getContentPane().add(switcherPanel, BorderLayout.NORTH);
		confPanel = new ToolbarConfigPanel(app);
		getContentPane().add(confPanel, BorderLayout.CENTER);
		getContentPane().add(createButtonPanel(), BorderLayout.SOUTH);

		pack();
		setLocationRelativeTo(app.getFrame());

		switcherPanel.add(switcher);
		switcher.setSelectedIndex(selIdx);

	}

	/**
	 * Applies changes to the toolbar
	 */
	void apply() {
		confPanel.apply();
		app.updateToolBar();
		app.setUnsaved();
	}

	private JPanel createButtonPanel() {
		JPanel btPanel = new JPanel();
		btPanel.setLayout(new BoxLayout(btPanel, BoxLayout.X_AXIS));
		btPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

		/*
		 * DefaultComboBoxModel model = new DefaultComboBoxModel();
		 * model.addElement(loc.getMenu("Toolbar.Default"));
		 * //model.addElement(loc.getMenu("Basic"));
		 * model.addElement(loc.getMenu("Toolbar.UserDefined")); JComboBox
		 * cbToolbar = new JComboBox(model);
		 */

		final JButton btDefaultToolbar = new JButton();
		btPanel.add(btDefaultToolbar);
		btDefaultToolbar.setText(loc.getMenu("Toolbar.ResetDefault"));

		btPanel.add(Box.createHorizontalGlue());
		final JButton btApply = new JButton();
		btPanel.add(btApply);
		btApply.setText(loc.getMenu("Apply"));

		final JButton btCancel = new JButton();
		btPanel.add(Box.createRigidArea(new Dimension(5, 0)));
		btPanel.add(btCancel);
		btCancel.setText(loc.getMenu("Close"));

		ActionListener ac = e -> {
			Object src = e.getSource();
			if (src == btApply) {
				apply();
			} else if (src == btCancel) {
				setVisible(false);
				dispose();
			} else if (src == btDefaultToolbar) {
				confPanel.resetDefaultToolbar();
			}
		};
		btCancel.addActionListener(ac);
		btApply.addActionListener(ac);
		btDefaultToolbar.addActionListener(ac);

		return btPanel;
	}

	/**
	 * Key value pairs.
	 */
	private static class KeyValue {
		int key;
		String value;

		public KeyValue(int key, String value) {
			this.key = key;
			this.value = value;
		}

		public int getKey() {
			return key;
		}

		@Override
		public String toString() {
			return value;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof KeyValue) {
				KeyValue kv = (KeyValue) obj;
				return (kv.value.equals(this.value)) && (kv.key == this.key);
			}
			return false;
		}

		@Override
		public int hashCode() {
			// TODO Auto-generated method stub
			return super.hashCode();
		}
	}

	/**
	 * Switch panel for which we want to change the toolbar.
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		int id = ((KeyValue) ((JComboBox) e.getSource()).getSelectedItem())
				.getKey();

		if (id == -1) {
			confPanel.setToolbar(null,
					((GuiManagerD) app.getGuiManager()).getToolbarDefinition());
		} else {
			DockPanelD panel = ((GuiManagerD) app.getGuiManager()).getLayout()
					.getDockManager().getPanel(id);
			confPanel.setToolbar(panel, panel.getToolbarString());
		}
	}
}