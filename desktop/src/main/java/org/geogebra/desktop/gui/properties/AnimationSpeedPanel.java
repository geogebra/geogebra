package org.geogebra.desktop.gui.properties;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.gui.UpdateFonts;
import org.geogebra.common.gui.dialog.options.model.AnimationSpeedModel;
import org.geogebra.common.gui.dialog.options.model.AnimationSpeedModel.IAnimationSpeedListener;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.arithmetic.NumberValue;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.desktop.main.AppD;

/**
 * panel for animation speed
 * 
 * @author adapted from AnimationStepPanel
 */
public class AnimationSpeedPanel extends JPanel implements ActionListener,
		FocusListener, UpdateablePropertiesPanel, SetLabels, UpdateFonts,
		IAnimationSpeedListener {

	private static final long serialVersionUID = 1L;

	private AnimationSpeedModel model;
	private JTextField tfAnimSpeed;
	private JComboBox animationModeCB;
	private JLabel modeLabel, speedLabel;
	private AppD app;
	private Kernel kernel;

	public AnimationSpeedPanel(AppD app) {
		this.app = app;
		this.kernel = app.getKernel();

		model = new AnimationSpeedModel(app);
		model.setListener(this);

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
		model.fillModes(app.getLocalization());
		animationModeCB.setSelectedIndex(selectedIndex);
		animationModeCB.addActionListener(this);
	}

	public void setPartOfSliderPanel() {
		model.setShowSliders(true);
	}

	public JPanel updatePanel(Object[] geos) {
		return update(geos);
	}
	public JPanel update(Object[] geos) {
		model.setGeos(geos);
		if (!model.checkGeos()) { // geos,partOfSliderPanel))
			return null;
		}

		tfAnimSpeed.removeActionListener(this);
		animationModeCB.removeActionListener(this);
		model.updateProperties();
		tfAnimSpeed.addActionListener(this);
		animationModeCB.addActionListener(this);
		return this;
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
		NumberValue animSpeed = kernel.getAlgebraProcessor().evaluateToNumeric(
				tfAnimSpeed.getText(), false);
		if (animSpeed != null) {
			model.applySpeedChanges(animSpeed);
		}
		update(model.getGeos());
	}

	private void setType(int type) {

		if (!model.hasGeos()) {
			return;
		}
		model.applyTypeChanges(type);

		update(model.getGeos());

	}

	public void focusGained(FocusEvent arg0) {
	}

	public void focusLost(FocusEvent e) {
		doActionPerformed();
	}

	public void updateFonts() {
		Font font = app.getPlainFont();

		modeLabel.setFont(font);
		speedLabel.setFont(font);
		animationModeCB.setFont(font);

		tfAnimSpeed.setFont(font);
	}

	public void updateVisualStyle(GeoElement geo) {
		// TODO Auto-generated method stub

	}

	public void setSelectedIndex(int index) {
		animationModeCB.setSelectedIndex(index);

	}

	public void addItem(String item) {
		animationModeCB.addItem(item);
	}

	public void setText(String text) {
		tfAnimSpeed.setText(text);
	}

	public void setSelectedItem(String item) {
		// TODO Auto-generated method stub

	}
}