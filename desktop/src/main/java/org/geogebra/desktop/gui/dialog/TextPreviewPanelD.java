package org.geogebra.desktop.gui.dialog;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import org.geogebra.common.euclidian.EuclidianController;
import org.geogebra.common.euclidian.EuclidianView;
import org.geogebra.common.gui.dialog.TextPreviewer;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.main.App;
import org.geogebra.common.main.settings.EuclidianSettings;
import org.geogebra.desktop.euclidian.EuclidianControllerD;
import org.geogebra.desktop.euclidian.EuclidianControllerListeners;
import org.geogebra.desktop.euclidian.EuclidianViewD;
import org.geogebra.desktop.gui.util.GeoGebraIconD;
import org.geogebra.desktop.main.AppD;

/**
 * 
 * Desktop implementation of TextPreviewPanel
 * 
 * @author G. Sturr
 * 
 */
public class TextPreviewPanelD extends TextPreviewer {

	/**
	 * @param kernel
	 */
	public TextPreviewPanelD(Kernel kernel) {

		super(kernel);
	}

	/**
	 * @return JPanel that encloses the EuclidianView
	 */
	public JPanel getJPanel() {
		return ((EuclidianViewD) ev).getJPanel();
	}

	/**
	 * Updates the preferred size of this panel to match the estimated size of
	 * the given preview geo. This forces the enclosing scrollpane to show
	 * scrollbars when the size of the preview geo grows larger than the
	 * scrollpane viewport.
	 * 
	 * Note: The preview geo uses absolute screen coords, so we can't easily get
	 * the bounding box dimensions and must use dummy containers to estimate
	 * these dimensions.
	 * 
	 * @param previewGeo
	 */
	@Override
	protected void updateViewportSize(GeoText previewGeo) {

		Dimension d = new Dimension();
		ImageIcon testIcon = new ImageIcon();
		JTextPane dummyText = new JTextPane();
		int padding = 5; // account for inset

		if (previewGeo == null) {
			return;
		}

		if (previewGeo.isLaTeX()) {
			// LaTex geo, use dummy ImageIcon

			GeoGebraIconD.drawLatexImageIcon((AppD) getApp(), testIcon,
					previewGeo.getTextString(), ((AppD) getApp()).getPlainFont(),
					true, Color.black, null);
			// System.out.println("=============> " + testIcon.getIconHeight() +
			// " : " + testIcon.getIconWidth());

			// get the dimensions from the icon and add some padding
			d.height = testIcon.getIconHeight() + padding;
			d.width = testIcon.getIconWidth() + padding;

		} else {
			// Plain text geo, use dummy JTextArea

			// set font and line spacing (guessing at this value)
			dummyText.setFont(((AppD) getApp()).getPlainFont());
			MutableAttributeSet set = new SimpleAttributeSet();
			StyleConstants.setLineSpacing(set, 1);
			// StyleConstants.setSpaceBelow(set, (float) 0.5);
			dummyText.setParagraphAttributes(set, true);

			dummyText.setText(previewGeo.getTextString());
			d = dummyText.getPreferredSize();

			// add some padding
			d.height += padding;
			d.width += padding;
		}

		// update this panel
		((EuclidianViewD) ev).setPreferredSize(d);
		((EuclidianViewD) ev).revalidate();

	}

	@Override
	protected void removeEVMouseListeners() {
		((EuclidianViewD) ev).removeMouseListener(
				(EuclidianControllerListeners) ev.getEuclidianController());
		((EuclidianViewD) ev).removeMouseMotionListener(
				(EuclidianControllerListeners) ev.getEuclidianController());
		((EuclidianViewD) ev).removeMouseWheelListener(
				(EuclidianControllerListeners) ev.getEuclidianController());
	}

	@Override
	protected EuclidianViewD getEuclidianView() {
		boolean[] showAxes = { false, false };
		boolean showGrid = false;
		if (ev == null) {
			ev = new EuclidianViewTextPreview(new EuclidianControllerD(kernel),
					showAxes, showGrid, EuclidianView.EVNO_GENERAL, null);
		}
		return (EuclidianViewD) ev;
	}

	/****************************************************************************
	 * Extension of EuclidianViewD for displaying preview text strings in the
	 * text editor.
	 * 
	 */
	private static class EuclidianViewTextPreview extends EuclidianViewD {

		public EuclidianViewTextPreview(EuclidianController ec,
				boolean[] showAxes, boolean showGrid, int evno,
				EuclidianSettings settings) {
			super(ec, showAxes, showGrid, evno, settings);
		}

		/**
		 * Overrides attachView with an empty method to prevent this panel from
		 * attaching to the kernel
		 */
		@Override
		public void attachView() {
			// do nothing
		}

		@Override
		public int getViewID() {
			return App.VIEW_TEXT_PREVIEW;
		}

		@Override
		public boolean isPlotPanel() {
			return true;
		}
	}

}
