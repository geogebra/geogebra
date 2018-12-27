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
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoNumberValue;
import org.geogebra.desktop.main.AppD;
import org.geogebra.desktop.main.LocalizationD;

/**
 * panel for animation speed
 * 
 * @author adapted from AnimationStepPanel
 */
public class AnimationSpeedPanel extends JPanel
		implements ActionListener, FocusListener, UpdateablePropertiesPanel,
		SetLabels, UpdateFonts, IAnimationSpeedListener {

	private static final long serialVersionUID = 1L;

	private AnimationSpeedModel model;
	private JTextField tfAnimSpeed;
	private JComboBox animationModeCB;
	private JLabel modeLabel, speedLabel;
	private AppD app;
	private Kernel kernel;
	private LocalizationD loc;

	public AnimationSpeedPanel(AppD app) {

		this.app = app;
		this.loc = app.getLocalization();
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

	@Override
	public void setLabels() {
		modeLabel.setText(loc.getMenu("Repeat") + ": ");
		speedLabel.setText(loc.getMenu("AnimationSpeed") + ": ");

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

	@Override
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
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == tfAnimSpeed) {
			doActionPerformed();
		} else if (e.getSource() == animationModeCB) {
			setType(animationModeCB.getSelectedIndex());
		}
	}

	private void doActionPerformed() {

		GeoNumberValue animSpeed;

		// if a label is entered, no need to parse
		// hopefully avoids bug where label is replaced with definition
		// sometimes
		GeoElement geo = kernel.lookupLabel(tfAnimSpeed.getText());

		if (geo instanceof GeoNumberValue) {
			animSpeed = (GeoNumberValue) geo;
		} else {
			animSpeed = kernel.getAlgebraProcessor()
					.evaluateToNumeric(tfAnimSpeed.getText(), false);
		}

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

	@Override
	public void focusGained(FocusEvent arg0) {
		// only handle focus lost
	}

	@Override
	public void focusLost(FocusEvent e) {
		doActionPerformed();
	}

	@Override
	public void updateFonts() {
		Font font = app.getPlainFont();

		modeLabel.setFont(font);
		speedLabel.setFont(font);
		animationModeCB.setFont(font);

		tfAnimSpeed.setFont(font);
	}

	@Override
	public void updateVisualStyle(GeoElement geo) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setSelectedIndex(int index) {
		animationModeCB.setSelectedIndex(index);

	}

	@Override
	public void addItem(String item) {
		animationModeCB.addItem(item);
	}

	@Override
	public void setText(String text) {
		tfAnimSpeed.setText(text);
	}

	@Override
	public void clearItems() {
		// TODO Auto-generated method stub
	}

	public void addItem(GeoElement item) {
		// TODO Auto-generated method stub
	}
}