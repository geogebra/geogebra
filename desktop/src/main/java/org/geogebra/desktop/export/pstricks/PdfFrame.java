package org.geogebra.desktop.export.pstricks;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import org.geogebra.common.export.pstricks.GeoGebraToPdf;
import org.geogebra.common.util.FileExtensions;

/**
 * @author Hoszu Henrietta (from PgfFrame)
 *
 */
public class PdfFrame extends ExportFrame {
	private static final long serialVersionUID = 1L;
	final String[] format = { "LaTeX (article class)" };

	public PdfFrame(final GeoGebraToPdf ggb2pdf) {
		super(ggb2pdf, "Generate code");
		fileExtension = FileExtensions.TEX;
		fileExtensionMsg = "TeX ";
		initGui();
	}

	@SuppressWarnings("unchecked")
	protected void initGui() {
		comboFormat = new JComboBox(format);
		labelFormat = new JLabel(loc.getMenu("Format"));
		js.getViewport().add(textarea);
		setTitle(loc.getMenu("Export to animated PDF"));
		panel.setLayout(new GridBagLayout());

		// disable controls if there are no sliders
		if (comboModel.getSize() == 0) {
			button.setEnabled(false);
			buttonSave.setEnabled(false);
			button_copy.setEnabled(false);
			JOptionPane.showMessageDialog(panel,
					"You need sliders to create an animation!");
		}

		panel.add(labelXUnit,
				new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0,
						GridBagConstraints.CENTER, GridBagConstraints.NONE,
						new Insets(5, 5, 5, 5), 0, 0));
		panel.add(textXUnit, new GridBagConstraints(1, 0, 1, 1, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(5, 5, 5, 5), 0, 0));
		panel.add(labelwidth,
				new GridBagConstraints(2, 0, 1, 1, 1.0, 1.0,
						GridBagConstraints.CENTER, GridBagConstraints.NONE,
						new Insets(5, 5, 5, 5), 0, 0));
		panel.add(textwidth, new GridBagConstraints(3, 0, 1, 1, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(5, 5, 5, 5), 0, 0));
		panel.add(labelYUnit,
				new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0,
						GridBagConstraints.CENTER, GridBagConstraints.NONE,
						new Insets(5, 5, 5, 5), 0, 0));
		panel.add(textYUnit, new GridBagConstraints(1, 1, 1, 1, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(5, 5, 5, 5), 0, 0));
		panel.add(labelheight,
				new GridBagConstraints(2, 1, 1, 1, 1.0, 1.0,
						GridBagConstraints.CENTER, GridBagConstraints.NONE,
						new Insets(5, 5, 5, 5), 0, 0));
		panel.add(textheight, new GridBagConstraints(3, 1, 1, 1, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(5, 5, 5, 5), 0, 0));

		// ////////////////////////////////
		panel.add(labelXmin,
				new GridBagConstraints(0, 2, 1, 1, 1.0, 1.0,
						GridBagConstraints.CENTER, GridBagConstraints.NONE,
						new Insets(5, 5, 5, 5), 0, 0));
		panel.add(textXmin, new GridBagConstraints(1, 2, 1, 1, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(5, 5, 5, 5), 0, 0));
		panel.add(labelXmax,
				new GridBagConstraints(2, 2, 1, 1, 1.0, 1.0,
						GridBagConstraints.CENTER, GridBagConstraints.NONE,
						new Insets(5, 5, 5, 5), 0, 0));
		panel.add(textXmax, new GridBagConstraints(3, 2, 1, 1, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(5, 5, 5, 5), 0, 0));
		panel.add(labelYmin,
				new GridBagConstraints(0, 3, 1, 1, 1.0, 1.0,
						GridBagConstraints.CENTER, GridBagConstraints.NONE,
						new Insets(5, 5, 5, 5), 0, 0));
		panel.add(textYmin, new GridBagConstraints(1, 3, 1, 1, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(5, 5, 5, 5), 0, 0));
		panel.add(labelYmax,
				new GridBagConstraints(2, 3, 1, 1, 1.0, 1.0,
						GridBagConstraints.CENTER, GridBagConstraints.NONE,
						new Insets(5, 5, 5, 5), 0, 0));
		panel.add(textYmax, new GridBagConstraints(3, 3, 1, 1, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(5, 5, 5, 5), 0, 0));

		panel.add(labelFontSize, new GridBagConstraints(0, 4, 1, 1, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(5, 5, 5, 5), 0, 0));
		panel.add(comboFontSize,
				new GridBagConstraints(1, 4, 1, 1, 1.0, 1.0,
						GridBagConstraints.CENTER, GridBagConstraints.NONE,
						new Insets(5, 5, 5, 5), 0, 0));

		panel.add(labelFormat, new GridBagConstraints(2, 4, 1, 1, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(5, 5, 5, 5), 0, 0));
		panel.add(comboFormat, new GridBagConstraints(3, 4, 1, 1, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(5, 5, 5, 5), 0, 0));
		panel.add(new JLabel(loc.getMenu("Slider") + ":"),
				new GridBagConstraints(0, 5, 2, 1, 1.0, 1.0,
						GridBagConstraints.CENTER,
						GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5),
						0, 0));
		panel.add(cbSliders, new GridBagConstraints(2, 5, 2, 1, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(5, 5, 5, 5), 0, 0));
		panel.add(button, new GridBagConstraints(0, 7, 2, 1, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(5, 5, 5, 5), 0, 0));
		panel.add(buttonSave, new GridBagConstraints(2, 7, 1, 1, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(5, 5, 5, 5), 0, 0));
		panel.add(button_copy, new GridBagConstraints(3, 7, 1, 1, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
				new Insets(5, 5, 5, 5), 0, 0));

		// ///////////////////////////////
		panel.add(js,
				new GridBagConstraints(0, 8, 4, 5, 1.0, 15.0,
						GridBagConstraints.CENTER, GridBagConstraints.BOTH,
						new Insets(5, 5, 5, 5), 0, 0));

		textXUnit.setPreferredSize(
				new Dimension(110, textXUnit.getFont().getSize() + 6));
		textYUnit.setPreferredSize(
				new Dimension(110, textYUnit.getFont().getSize() + 6));
		js.setPreferredSize(new Dimension(400, 400));
		getContentPane().add(panel);
		centerOnScreen();
		setVisible(true);
	}



	@Override
	protected boolean isBeamer() {
		return false;
	}

	@Override
	protected boolean isLaTeX() {
		int id = comboFormat.getSelectedIndex();
		if (id == 0 || id == 3)
			return true;
		return false;
	}

	@Override
	protected boolean isPlainTeX() {
		return false;
	}

	@Override
	protected boolean isConTeXt() {
		return false;
	}
}
