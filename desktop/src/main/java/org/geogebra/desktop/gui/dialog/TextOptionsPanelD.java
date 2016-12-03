package org.geogebra.desktop.gui.dialog;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ArrayList;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JToggleButton;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.gui.UpdateFonts;
import org.geogebra.common.gui.dialog.options.model.TextOptionsModel;
import org.geogebra.common.gui.dialog.options.model.TextOptionsModel.ITextOptionsListener;
import org.geogebra.common.gui.inputfield.DynamicTextElement;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.main.Localization;
import org.geogebra.desktop.gui.dialog.PropertiesPanelD.TextEditPanel;
import org.geogebra.desktop.gui.properties.UpdateablePropertiesPanel;

/**
 * panel to select the size of a GeoText
 * 
 * @author Markus Hohenwarter
 */
class TextOptionsPanelD extends JPanel implements ActionListener,
		SetLabels, UpdateFonts, UpdateablePropertiesPanel, FocusListener,
		ITextOptionsListener {
	/**
	 * 
	 */
	private final PropertiesPanelD propertiesPanelD;
	private static final long serialVersionUID = 1L;
	private TextOptionsModel model;
	private JLabel decimalLabel;
	private JComboBox cbFont, cbSize, cbDecimalPlaces;
	private JToggleButton btBold, btItalic;

	private JPanel secondLine;
	private boolean secondLineVisible = false;
	private TextEditPanel editPanel;

	public TextOptionsPanelD(PropertiesPanelD propertiesPanelD) {

		this.propertiesPanelD = propertiesPanelD;
		model = new TextOptionsModel(this.propertiesPanelD.app);
		model.setListener(this);

		cbFont = new JComboBox(model.getFonts());
		cbFont.addActionListener(this);

		// font size
		// TODO require font phrases F.S.
		cbSize = new JComboBox(model.getFontSizes());
		cbSize.addActionListener(this);
		cbFont.addFocusListener(this);
		// toggle buttons for bold and italic
		btBold = new JToggleButton();
		btBold.setFont(this.propertiesPanelD.app.getBoldFont());
		btBold.addActionListener(this);
		btItalic = new JToggleButton();
		btItalic.setFont(this.propertiesPanelD.app.getPlainFont().deriveFont(Font.ITALIC));
		btItalic.addActionListener(this);

		// decimal places
		ComboBoxRenderer renderer = new ComboBoxRenderer();
		cbDecimalPlaces = new JComboBox(this.propertiesPanelD.loc.getRoundingMenu());
		cbDecimalPlaces.setRenderer(renderer);
		cbDecimalPlaces.addActionListener(this);

		// font, size
		JPanel firstLine = new JPanel();
		firstLine.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 0));

		firstLine.add(cbFont);
		firstLine.add(cbSize);
		firstLine.add(btBold);
		firstLine.add(btItalic);

		// bold, italic
		secondLine = new JPanel();
		secondLine.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 0));
		decimalLabel = new JLabel();
		secondLine.add(decimalLabel);
		secondLine.add(cbDecimalPlaces);

		setLayout(new BorderLayout(5, 5));
		add(firstLine, BorderLayout.NORTH);
		add(secondLine, BorderLayout.SOUTH);
		secondLineVisible = true;
	}

	public void setEditPanel(TextEditPanel tep) {
		this.editPanel = tep;
	}

	public void setLabels() {
		Localization loc = this.propertiesPanelD.app.getLocalization();
		String[] fontSizes = loc.getFontSizeStrings();

		int selectedIndex = cbSize.getSelectedIndex();
		cbSize.removeActionListener(this);
		cbSize.removeAllItems();

		for (int i = 0; i < fontSizes.length; ++i) {
			cbSize.addItem(fontSizes[i]);
		}

		cbSize.addItem(loc.getMenu("Custom") + "...");

		cbSize.setSelectedIndex(selectedIndex);
		cbSize.addActionListener(this);

		btItalic.setText(loc.getMenu("Italic").substring(0, 1));
		btBold.setText(loc.getMenu("Bold").substring(0, 1));

		decimalLabel.setText(loc.getMenu("Rounding") + ":");
	}

	public void setFontSizeVisibleOnly() {
		cbSize.setVisible(true);
		cbFont.setVisible(false);
		btBold.setVisible(false);
		btItalic.setVisible(false);
		secondLine.setVisible(false);
	}

	static class ComboBoxRenderer extends JLabel implements ListCellRenderer {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		JSeparator separator;

		public ComboBoxRenderer() {
			setOpaque(true);
			setBorder(new EmptyBorder(1, 1, 1, 1));
			separator = new JSeparator(SwingConstants.HORIZONTAL);
		}

		public Component getListCellRendererComponent(JList list,
				Object value, int index, boolean isSelected,
				boolean cellHasFocus) {
			String str = (value == null) ? "" : value.toString();
			if ("---".equals(str)) {
				return separator;
			}
			if (isSelected) {
				setBackground(list.getSelectionBackground());
				setForeground(list.getSelectionForeground());
			} else {
				setBackground(list.getBackground());
				setForeground(list.getForeground());
			}
			setFont(list.getFont());
			setText(str);
			return this;
		}
	}

	public JPanel updatePanel(Object[] geos) {
		model.setGeos(geos);
		return update();
	}

	public void updateVisualStyle(GeoElement geo) {
		if (!model.hasGeos()) {
			return;
		}
		update();
	}

	public JPanel update() {
		// check geos
		if (!model.checkGeos()) {
			model.cancelEditGeo();
			return null;
		}

		cbSize.removeActionListener(this);
		cbFont.removeActionListener(this);
		cbDecimalPlaces.removeActionListener(this);

		model.updateProperties();

		cbSize.addActionListener(this);
		cbFont.addActionListener(this);
		cbDecimalPlaces.addActionListener(this);
		return this;
	}

	/**
	 * change listener implementation for slider
	 */
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();

		if (source == cbSize) {
			boolean isCustom = (cbSize.getSelectedIndex() == 7);
			if (isCustom) {
				final String percentStr = JOptionPane.showInputDialog(
						this.propertiesPanelD.app.getFrame(),
						this.propertiesPanelD.app.getLocalization()
								.getMenu("EnterPercentage"),
						Math.round(model.getTextPropertiesAt(0)
								.getFontSizeMultiplier() * 100) + "%");

				model.applyFontSizeFromString(percentStr);
			} else {
				model.applyFontSizeFromIndex(cbSize.getSelectedIndex());
			}
		} else if (source == cbFont) {
			model.applyFont(cbFont.getSelectedIndex() == 1);
		} else if (source == cbDecimalPlaces) {
			model.applyDecimalPlaces(cbDecimalPlaces.getSelectedIndex());
		} else if (source == btBold || source == btItalic) {
			model.applyFontStyle(btBold.isSelected(), btItalic.isSelected());
		}
	}

	public void updateFonts() {
		Font font = this.propertiesPanelD.app.getPlainFont();

		cbFont.setFont(font);
		cbSize.setFont(font);
		cbDecimalPlaces.setFont(font);

		btItalic.setFont(font);
		btBold.setFont(font);

		decimalLabel.setFont(font);

		editPanel.setFont(font);
	}

	public void focusGained(FocusEvent arg0) {
		cbSize.setSelectedIndex(GeoText.getFontSizeIndex(model
				.getTextPropertiesAt(0).getFontSizeMultiplier()));
	}

	public void focusLost(FocusEvent arg0) {
		model.cancelEditGeo();
	}

	public void setWidgetsVisible(boolean showFontDetails, boolean isButton) {
		// hide most options for Textfields
		cbFont.setVisible(showFontDetails);
		btBold.setVisible(showFontDetails);
		btItalic.setVisible(showFontDetails);
		secondLine.setVisible(showFontDetails);
		secondLineVisible = showFontDetails;

		if (isButton) {
			secondLine.setVisible(!showFontDetails);
			secondLineVisible = !showFontDetails;
		}

		// removed, stops lists being set to Serif / Sans Serif
		// cbFont.setVisible(model.isTextEditable());

	}

	public void selectSize(int index) {
		cbSize.setSelectedIndex(index);

	}

	public void selectFont(int index) {
		cbFont.setSelectedIndex(index);

	}

	public void selectDecimalPlaces(int index) {
		cbDecimalPlaces.setSelectedIndex(index);
	}

	public void setSecondLineVisible(boolean noDecimals) {
		if (noDecimals) {

			if (secondLineVisible) {
				secondLineVisible = false;
			}
		} else {
			if (!secondLineVisible) {
				secondLineVisible = true;
			}

			secondLine.setVisible(secondLineVisible);
		}

	}

	public void updatePreview() {
		if (this.propertiesPanelD.getTextPanel() != null) {
			this.propertiesPanelD.getTextPanel().td.handleDocumentEvent();
		}
	}

	public void selectFontStyle(int style) {
		btBold.setSelected(style == Font.BOLD
				|| style == (Font.BOLD + Font.ITALIC));
		btItalic.setSelected(style == Font.ITALIC
				|| style == (Font.BOLD + Font.ITALIC));

	}

	public void setEditorText(ArrayList<DynamicTextElement> list) {
		// TODO Auto-generated method stub

	}

	public void setEditorText(String text) {
		// TODO Auto-generated method stub

	}

	public void reinitEditor() {
		// only called in Web

	}
}