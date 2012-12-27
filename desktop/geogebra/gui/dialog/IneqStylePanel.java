package geogebra.gui.dialog;

import geogebra.common.gui.SetLabels;
import geogebra.common.gui.UpdateFonts;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.kernel.geos.InequalityProperties;
import geogebra.gui.properties.UpdateablePropertiesPanel;

import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JCheckBox;
import javax.swing.JPanel;

class IneqStylePanel extends JPanel implements ItemListener,
		SetLabels, UpdateFonts, UpdateablePropertiesPanel {
	/**
	 * 
	 */
	private final PropertiesPanel propertiesPanel;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Object[] geos; // currently selected geos
	private JCheckBox showOnAxis;

	public IneqStylePanel(PropertiesPanel propertiesPanel) {
		super(new FlowLayout(FlowLayout.LEFT));
		this.propertiesPanel = propertiesPanel;

		// check boxes for show trace
		showOnAxis = new JCheckBox();
		showOnAxis.addItemListener(this);
		add(showOnAxis);
	}

	public void setLabels() {
		showOnAxis.setText(this.propertiesPanel.app.getPlain("ShowOnXAxis"));
	}

	public JPanel update(Object[] geos) {
		this.geos = geos;
		if (!checkGeos(geos))
			return null;

		showOnAxis.removeItemListener(this);

		// check if properties have same values
		if (!(geos[0] instanceof InequalityProperties))
			return null;
		InequalityProperties temp, geo0 = (InequalityProperties) geos[0];
		boolean equalFix = true;

		for (int i = 0; i < geos.length; i++) {
			if (!(geos[i] instanceof InequalityProperties))
				return null;
			temp = (InequalityProperties) geos[i];

			if (geo0.showOnAxis() != temp.showOnAxis())
				equalFix = false;
		}

		// set trace visible checkbox
		if (equalFix) {
			showOnAxis.setSelected(geo0.showOnAxis());
			if (geo0.showOnAxis())
				this.propertiesPanel.fillingPanel.setAllEnabled(false);
		} else
			showOnAxis.setSelected(false);

		showOnAxis.addItemListener(this);
		return this;
	}

	private static boolean checkGeos(Object[] geos) {
		for (int i = 0; i < geos.length; i++) {
			GeoElement geo = ((GeoElement)geos[i]).getGeoElementForPropertiesDialog();
			if (!(geo instanceof GeoFunction))
				return false;
			GeoFunction gfun = (GeoFunction) geo;
			if (!gfun.isBooleanFunction()
					|| gfun.getVarString(StringTemplate.defaultTemplate)
							.equals("y"))
				return false;
		}
		return true;
	}

	/**
	 * listens to checkboxes and sets trace state
	 */
	public void itemStateChanged(ItemEvent e) {
		InequalityProperties geo;
		Object source = e.getItemSelectable();

		// show trace value changed
		if (source == showOnAxis) {
			for (int i = 0; i < geos.length; i++) {
				geo = (InequalityProperties) geos[i];
				geo.setShowOnAxis(showOnAxis.isSelected());
				geo.updateRepaint();

			}
			this.propertiesPanel.fillingPanel.setAllEnabled(!showOnAxis.isSelected());
		}

		this.propertiesPanel.updateSelection(geos);
	}

	public void updateFonts() {
		Font font = this.propertiesPanel.app.getPlainFont();
		
		showOnAxis.setFont(font);
	}

	public void updateVisualStyle(GeoElement geo) {
		// TODO Auto-generated method stub
		
	}
}