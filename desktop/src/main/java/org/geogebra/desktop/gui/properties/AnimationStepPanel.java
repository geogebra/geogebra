package org.geogebra.desktop.gui.properties;

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

import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.gui.UpdateFonts;
import org.geogebra.common.gui.dialog.options.model.AnimationStepModel;
import org.geogebra.common.gui.dialog.options.model.ITextFieldListener;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.desktop.gui.AngleTextField;
import org.geogebra.desktop.main.AppD;

/**
 * panel for animation step
 * 
 * @author Markus Hohenwarter
 */
public class AnimationStepPanel extends JPanel
		implements ActionListener, FocusListener, UpdateablePropertiesPanel,
		SetLabels, UpdateFonts, ITextFieldListener {

	private static final long serialVersionUID = 1L;

	private AnimationStepModel model;
	private JLabel label;
	private AngleTextField tfAnimStep;

	private Kernel kernel;

	/**
	 * @param app
	 *            application
	 */
	public AnimationStepPanel(AppD app) {
		this(new AnimationStepModel(app), app);
	}

	/**
	 *
	 * @param m model
	 * @param app application
	 */
	public AnimationStepPanel(AnimationStepModel m, AppD app) {
		kernel = app.getKernel();
		model = m;
		model.setListener(this);
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

	@Override
	public void setLabels() {
		label.setText(kernel.getLocalization().getMenu("AnimationStep") + ": ");
	}

	public void setPartOfSliderPanel() {
		model.setPartOfSlider(true);
	}

	@Override
	public JPanel updatePanel(Object[] geos) {
		model.setGeos(geos);

		if (!model.checkGeos()) {
			return null;
		}

		tfAnimStep.removeActionListener(this);
		model.updateProperties();
		tfAnimStep.addActionListener(this);
		return this;
	}

	/**
	 * handle textfield changes
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == tfAnimStep) {
			doActionPerformed();
		}
	}

	private void doActionPerformed() {

		model.applyChanges(tfAnimStep.getText());
		updatePanel(model.getGeos());
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
		Font font = ((AppD) kernel.getApplication()).getPlainFont();

		label.setFont(font);
		tfAnimStep.setFont(font);
	}

	@Override
	public void updateVisualStyle(GeoElement geo) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setText(String text) {
		tfAnimStep.setText(text);
	}
}