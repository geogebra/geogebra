package geogebra.gui.properties;

import geogebra.common.gui.SetLabels;
import geogebra.common.gui.UpdateFonts;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.arithmetic.ExpressionNodeConstants.StringType;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.gui.AngleTextField;
import geogebra.gui.dialog.PropertiesDialog;
import geogebra.main.AppD;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * panel for animation step
 * @author Markus Hohenwarter
 */
public class AnimationStepPanel
	extends JPanel
	implements ActionListener, FocusListener, UpdateablePropertiesPanel, SetLabels, UpdateFonts {
	
	private static final long serialVersionUID = 1L;
	
	private Object[] geos; // currently selected geos
	private JLabel label;	
	private AngleTextField tfAnimStep;
	private boolean partOfSliderPanel = false;
	
	private Kernel kernel;

	public AnimationStepPanel(AppD app) {
		kernel = app.getKernel();
		
		// text field for animation step
		label = new JLabel();
		tfAnimStep = new AngleTextField(6, app);
		label.setLabelFor(tfAnimStep);
		tfAnimStep.addActionListener(this);
		tfAnimStep.addFocusListener(this);

		// put it all together
		JPanel animPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		animPanel.add(label);
		animPanel.add(tfAnimStep);

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		animPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		add(animPanel);
				
		setLabels();
	}
	
	public void setLabels() {
		label.setText(kernel.getApplication().getPlain("AnimationStep") + ": ");
	}	
	
	public void setPartOfSliderPanel() {
		partOfSliderPanel = true;
	}

	public JPanel update(Object[] geos) {		
		this.geos = geos;
		if (!checkGeos(geos))
			return null;

		tfAnimStep.removeActionListener(this);

		// check if properties have same values
		GeoElement temp, geo0 = (GeoElement) geos[0];
		boolean equalStep = true;
		boolean onlyAngles = true;

		for (int i = 0; i < geos.length; i++) {
			temp = (GeoElement) geos[i];
			// same object visible value
			if (!Kernel.isEqual(geo0.getAnimationStep(), temp.getAnimationStep()))
				equalStep = false;
			if (!(temp.isGeoAngle()))
				onlyAngles = false;
		}

		// set trace visible checkbox
		//int oldDigits = kernel.getMaximumFractionDigits();
		//kernel.setMaximumFractionDigits(PropertiesDialog.TEXT_FIELD_FRACTION_DIGITS);
		StringTemplate highPrecision = StringTemplate.printDecimals(StringType.GEOGEBRA, PropertiesDialog.TEXT_FIELD_FRACTION_DIGITS,false);

        if (equalStep){
        	GeoElement stepGeo = geo0.getAnimationStepObject();
			if (onlyAngles && (stepGeo == null ||(!stepGeo.isLabelSet() && stepGeo.isIndependent())))
				tfAnimStep.setText(
					kernel.formatAngle(geo0.getAnimationStep(),highPrecision).toString());
			else
				tfAnimStep.setText(stepGeo.getLabel(highPrecision));
        }
		else
			tfAnimStep.setText("");
        
		

		tfAnimStep.addActionListener(this);
		return this;
	}

	private boolean checkGeos(Object[] geos) {
		boolean geosOK = true;
		for (int i = 0; i < geos.length; i++) {
			GeoElement geo = (GeoElement) geos[i];
			if (!geo.isChangeable() 
					|| geo.isGeoText() 
					|| geo.isGeoImage()
					|| geo.isGeoList()
					|| geo.isGeoBoolean()
					|| geo.isGeoButton()
					|| (!partOfSliderPanel && geo.isGeoNumeric() && geo.isIndependent()) // slider						
			)  
			{				
				geosOK = false;
				break;
			}
		}
		
		
		return geosOK;
	}
	
	/**
	 * handle textfield changes
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == tfAnimStep)
			doActionPerformed();
	}

	private void doActionPerformed() {
		NumberValue newVal =
			kernel.getAlgebraProcessor().evaluateToNumeric(
				tfAnimStep.getText(),true);
		if (newVal != null && !Double.isNaN(newVal.getDouble())) {
			for (int i = 0; i < geos.length; i++) {
				GeoElement geo = (GeoElement) geos[i];
				geo.setAnimationStep(newVal);
				geo.updateRepaint();
			}
		}
		update(geos);
	}

	public void focusGained(FocusEvent arg0) {
	}

	public void focusLost(FocusEvent e) {
		doActionPerformed();
	}

	public void updateFonts() {
		Font font = ((AppD) kernel.getApplication()).getPlainFont();
		
		label.setFont(font);
		tfAnimStep.setFont(font);
	}

	public void updateVisualStyle(GeoElement geo) {
		// TODO Auto-generated method stub
		
	}
}