package org.geogebra.desktop.gui.dialog;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.gui.UpdateFonts;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.plugin.EventType;
import org.geogebra.desktop.gui.properties.UpdateablePropertiesPanel;

/**
 * panel for text editingA
 */
public class TextEditPanel extends JPanel implements ActionListener,
		UpdateablePropertiesPanel, SetLabels, UpdateFonts {
	/**
	 * 
	 */
	private final PropertiesPanelD propertiesPanelD;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/** text dialog */
	TextInputDialogD td = null;
	private JPanel editPanel;

	/**
	 * New text edit panel
	 * @param propertiesPanelD TODO
	 */
	public TextEditPanel(PropertiesPanelD propertiesPanelD) {
		this.propertiesPanelD = propertiesPanelD;
		initGUI();

	}

	private void initGUI() {
		if (td != null) {
			return;
		}
		td = new TextInputDialogD(this.propertiesPanelD.app, this.propertiesPanelD.loc.getMenu("Text"), null, null,
				true, 30, 5, false);
		setLayout(new BorderLayout());

		editPanel = new JPanel(new BorderLayout(0, 0));
		editPanel.add(td.getInputPanel(), BorderLayout.CENTER);
		editPanel.add(td.getToolBar(), BorderLayout.SOUTH);
		editPanel.setBorder(BorderFactory.createEtchedBorder());

		JSplitPane sp = new JSplitPane(JSplitPane.VERTICAL_SPLIT, editPanel,
				td.getPreviewPanel());
		sp.setResizeWeight(0.5);
		sp.setBorder(BorderFactory.createEmptyBorder());

		add(sp, BorderLayout.CENTER);
		// add(td.getPreviewPanel(), BorderLayout.NORTH);
		add(td.getButtonPanel(), BorderLayout.SOUTH);

	}

	/**
	 * apply edit modifications
	 */
	public void applyModifications() {
		td.applyModifications();
	}

	@Override
	public void setLabels() {
		// editPanel.setBorder(BorderFactory.createTitledBorder(loc.getMenu("Edit")));
		// td.getPreviewPanel().setBorder(BorderFactory.createTitledBorder(loc.getMenu("Preview")));
		td.setLabels(this.propertiesPanelD.loc.getMenu("Text"));
	}

	@Override
	public JPanel updatePanel(Object[] geos) {
		if (geos.length != 1 || !checkGeos(geos)) {
			td.reset();
			return null;
		}

		GeoText text = (GeoText) geos[0];
		td.setGeoText(text);
		td.updateRecentSymbolTable();

		return this;
	}

	private static boolean checkGeos(Object[] geos) {
		return geos.length == 1 && geos[0] instanceof GeoText
				&& !((GeoText) geos[0]).isTextCommand()
				&& !((GeoText) geos[0]).isProtected(EventType.UPDATE);
	}

	/**
	 * handle textfield changes
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		// if (e.getSource() == btEdit)
		// app.showTextDialog((GeoText) geos[0]);
	}

	@Override
	public void updateFonts() {
		Font font = this.propertiesPanelD.app.getPlainFont();

		editPanel.setFont(font);
		td.updateFonts();
	}

	@Override
	public void updateVisualStyle(GeoElement geo) {
		// TODO Auto-generated method stub

	}
}