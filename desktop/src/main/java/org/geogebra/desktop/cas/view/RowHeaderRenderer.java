package org.geogebra.desktop.cas.view;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;

import org.geogebra.common.cas.view.CASInputHandler;
import org.geogebra.common.cas.view.MarbleRenderer;
import org.geogebra.common.kernel.geos.GeoCasCell;
import org.geogebra.common.main.GeoGebraColorConstants;
import org.geogebra.common.util.debug.Log;
import org.geogebra.desktop.awt.GColorD;
import org.geogebra.desktop.main.AppD;
import org.geogebra.desktop.util.GuiResourcesD;

/**
 * Renders row headers
 */
public class RowHeaderRenderer extends JPanel
		implements ListCellRenderer, MarbleRenderer {

	private static final long serialVersionUID = 1L;
	private CASTableD casTable;
	private JLabel numLabel;
	/** show hide option (also called plot tool) for this cell content */
	protected JLabel showHideControl;
	private ImageIcon iconShown, iconHidden;
	/** constraints */
	protected GridBagConstraints c;
	private AppD app;
	private boolean marbleValue;

	/**
	 * Creates new renderer
	 * 
	 * @param casTable
	 *            CAS table
	 */
	public RowHeaderRenderer(CASTableD casTable) {
		super(new GridBagLayout());
		c = new GridBagConstraints();
		app = (AppD) casTable.getApplication();
		iconShown = app.getScaledIcon(GuiResourcesD.ALGEBRA_SHOWN);
		iconHidden = app.getScaledIcon(GuiResourcesD.ALGEBRA_HIDDEN);
		numLabel = new JLabel("", SwingConstants.CENTER);
		this.casTable = casTable;
		// setBorder(BorderFactory.createEmptyBorder(5, 5, 10, 5));
		showHideControl = new JLabel(iconHidden);
		showHideControl.setVisible(false);
		add(numLabel, c);

		// set constraint to place the marble 8 pixels below numLabel
		c.insets = new Insets(8, 0, 0, 0);
		c.gridy = 1;
		add(showHideControl, c);

		setOpaque(true);
		setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0,
				GColorD.getAwtColor(GeoGebraColorConstants.TABLE_GRID_COLOR)));
	}

	public void updateIcons() {
		iconShown = app.getScaledIcon(GuiResourcesD.ALGEBRA_SHOWN);
		iconHidden = app.getScaledIcon(GuiResourcesD.ALGEBRA_HIDDEN);
		setMarbleValue(marbleValue);
	}

	@Override
	public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {
		numLabel.setText((value == null) ? "" : value.toString());
		numLabel.setFont(casTable.getFont());
		GeoCasCell ctr = casTable.getGeoCasCell(index);
		if (ctr == null) {
			Log.warn("No cas cell " + index);
			return this;
		}
		CASInputHandler.handleMarble(ctr, this);

		if (isSelected) {
			setBackground(GColorD.getAwtColor(
					GeoGebraColorConstants.TABLE_SELECTED_BACKGROUND_COLOR_HEADER));
		} else {
			setBackground(GColorD.getAwtColor(
					GeoGebraColorConstants.TABLE_BACKGROUND_COLOR_HEADER));
		}

		// update height
		Dimension prefSize = getPreferredSize();
		// go through all columns of this row to get the max height
		int height = casTable.getPreferredRowHeight(index);
		if (height != prefSize.height) {
			prefSize.height = height;
			setSize(prefSize);
			setPreferredSize(prefSize);
		}

		return this;
	}

	@Override
	public void setMarbleValue(boolean value) {
		showHideControl.setIcon(value ? iconShown : iconHidden);
		marbleValue = value;
	}

	@Override
	public void setMarbleVisible(boolean visible) {
		showHideControl.setVisible(visible);
	}

}
