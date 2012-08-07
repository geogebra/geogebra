package geogebra.gui.properties;

import geogebra.common.gui.SetLabels;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.arithmetic.ExpressionNodeConstants.StringType;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.geos.GeoButton;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.gui.dialog.PropertiesDialog;
import geogebra.main.AppD;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * panel for animation speed
 * @author adapted from AnimationStepPanel
 */
public class AnimationSpeedPanel
	extends JPanel
	implements ActionListener, FocusListener, UpdateablePropertiesPanel, SetLabels {
	
	private static final long serialVersionUID = 1L;
	
	private Object[] geos; // currently selected geos
	private JTextField tfAnimSpeed;
	private boolean partOfSliderPanel = false;
	private JComboBox animationModeCB;	
	private JLabel modeLabel, speedLabel;
	private AppD app;	
	private Kernel kernel;

	public AnimationSpeedPanel(AppD app) {
		this.app = app;
		this.kernel = app.getKernel();
		
			// combo box for 
		animationModeCB = new JComboBox();
		modeLabel = new JLabel();
		
		// text field for animation step
		speedLabel = new JLabel();
		tfAnimSpeed = new JTextField(5);
		speedLabel.setLabelFor(tfAnimSpeed);
		tfAnimSpeed.addActionListener(this);
		tfAnimSpeed.addFocusListener(this);

		// put it all together
		JPanel animPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		animPanel.add(speedLabel);
		animPanel.add(tfAnimSpeed);
		animPanel.add(modeLabel);
		animPanel.add(animationModeCB);

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		animPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		add(animPanel);
		
		setLabels();
	}
	
	public void setLabels() {
		modeLabel.setText(app.getPlain("Repeat") + ": ");
		speedLabel.setText(app.getPlain("AnimationSpeed") + ": ");
		
		int selectedIndex = animationModeCB.getSelectedIndex();
		animationModeCB.removeActionListener(this);
		
		animationModeCB.removeAllItems();
		animationModeCB.addItem("\u21d4 "+app.getPlain("Oscillating")); // index 0
		animationModeCB.addItem("\u21d2 "+app.getPlain("Increasing")); // index 1
		animationModeCB.addItem("\u21d0 "+app.getPlain("Decreasing")); // index 2
		animationModeCB.addItem("\u21d2 "+app.getPlain("IncreasingOnce")); // index 3
		
		animationModeCB.setSelectedIndex(selectedIndex);
		animationModeCB.addActionListener(this);
	}
	
	public void setPartOfSliderPanel() {
		partOfSliderPanel = true;
	}

	public JPanel update(Object[] geos) {		
		this.geos = geos;
		if (!checkGeos(geos))
			return null;

		tfAnimSpeed.removeActionListener(this);
		animationModeCB.removeActionListener(this);

		// check if properties have same values
		GeoElement temp, geo0 = (GeoElement) geos[0];
		boolean equalSpeed = true;
		boolean equalAnimationType = true;

		for (int i = 0; i < geos.length; i++) {
			temp = (GeoElement) geos[i];
			// same object visible value
			if (geo0.getAnimationSpeedObject() != temp.getAnimationSpeedObject())
				equalSpeed = false;
			if (geo0.getAnimationType() != temp.getAnimationType())
				equalAnimationType = false;
		}

		if (equalAnimationType)
			animationModeCB.setSelectedIndex(geo0.getAnimationType());
		else
			animationModeCB.setSelectedItem(null);

		// set trace visible checkbox
		
		StringTemplate highPrecision = StringTemplate.printDecimals(StringType.GEOGEBRA, PropertiesDialog.TEXT_FIELD_FRACTION_DIGITS,false);
        
        if (equalSpeed) {
        	GeoElement speedObj = geo0.getAnimationSpeedObject();
        	GeoNumeric num = kernel.getDefaultNumber(geo0.isAngle());
			tfAnimSpeed.setText(speedObj == null ? num.getAnimationSpeedObject().getLabel(highPrecision) : speedObj.getLabel(highPrecision));
        } else
			tfAnimSpeed.setText("");
        
		

		tfAnimSpeed.addActionListener(this);
		animationModeCB.addActionListener(this);
		return this;
	}

	private boolean checkGeos(Object[] geos) {
		boolean geosOK = true;
		for (int i = 0; i < geos.length; i++) {
			GeoElement geo = (GeoElement) geos[i];
			if (!geo.isChangeable() 
					|| geo.isGeoText() 
					|| geo.isGeoImage()
					|| (geo instanceof GeoButton)
					|| geo.isGeoList()
					|| geo.isGeoBoolean()
					|| (geo.isGeoPoint() && !geo.isPointOnPath())
					|| !partOfSliderPanel && geo.isGeoNumeric() && geo.isIndependent() // slider						
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
		if (e.getSource() == tfAnimSpeed)
			doActionPerformed();
		else if (e.getSource() == animationModeCB) 
			setType(animationModeCB.getSelectedIndex());
	}

	private void doActionPerformed() {
		NumberValue animSpeed = 
			kernel.getAlgebraProcessor().evaluateToNumeric(tfAnimSpeed.getText(), false);
		if (animSpeed != null) {
			for (int i = 0; i < geos.length; i++) {
				GeoElement geo = (GeoElement) geos[i];
				geo.setAnimationSpeedObject(animSpeed);
				geo.updateCascade();
			}
			kernel.udpateNeedToShowAnimationButton();
			kernel.notifyRepaint();
			
			
		}
		update(geos);
	}

	private void setType(int type) {
		
		if (geos == null) return;
		
			for (int i = 0; i < geos.length; i++) {
				GeoElement geo = (GeoElement) geos[i];
				geo.setAnimationType(type);
				geo.updateRepaint();
			}
		
		update(geos);
	}

	public void focusGained(FocusEvent arg0) {
	}

	public void focusLost(FocusEvent e) {
		doActionPerformed();
	}
}