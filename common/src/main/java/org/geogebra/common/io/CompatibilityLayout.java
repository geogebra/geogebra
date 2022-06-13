package org.geogebra.common.io;

import java.util.LinkedHashMap;

import org.geogebra.common.awt.GDimension;
import org.geogebra.common.factories.AwtFactory;
import org.geogebra.common.io.layout.DockPanelData;
import org.geogebra.common.io.layout.DockSplitPaneData;
import org.geogebra.common.io.layout.Perspective;
import org.geogebra.common.javax.swing.SwingConstants;
import org.geogebra.common.main.App;
import org.geogebra.common.main.settings.EuclidianSettings;

public class CompatibilityLayout {
	/**
	 * Backward compatibility for version < 3.03 where no layout component was
	 * used. Temporary storage for the split divider location of the split panes
	 * #1/#2.
	 */
	protected int sp1;
	protected int sp2;

	/**
	 * If the split divider is horizontal. (version < 3.03)
	 */
	protected boolean spHorizontal = true;

	/**
	 * If the algebra view is visible. (version < 3.03)
	 */
	protected boolean showAlgebra;
	/**
	 * If the spreadsheet view is visible. (version < 3.03)
	 */
	protected boolean showSpreadsheet;

	private GDimension dimension;

	/**
	 * @param tmp_perspective
	 *            perspective
	 * @param app
	 *            app
	 */
	public void update(Perspective tmp_perspective, App app) {
		int splitOrientation = spHorizontal
				? SwingConstants.HORIZONTAL_SPLIT
				: SwingConstants.VERTICAL_SPLIT;
		String defEV, defSV, defAV;
		// we have to create the definitions for the single views manually to
		// prevent nullpointers
		if (splitOrientation == SwingConstants.HORIZONTAL_SPLIT) {
			if (showSpreadsheet && showAlgebra) {
				defEV = "1,3";
				defSV = "1,1";
				defAV = "3";
			} else {
				if (showSpreadsheet) {
					defEV = "3";
					defSV = "1";
					defAV = "3,3"; // not used directly
				} else {
					defEV = "1";
					defAV = "3";
					defSV = "1,1"; // not used directly
				}
			}
		} else {
			if (showSpreadsheet && showAlgebra) {
				defEV = "0";
				defAV = "2,0";
				defSV = "2,2";
			} else {
				if (showSpreadsheet) {
					defEV = "0";
					defSV = "2";
					defAV = "0,0"; // not used directly
				} else {
					defEV = "2";
					defAV = "0";
					defSV = "2,2"; // not used directly
				}
			}
		}

		EuclidianSettings euclidian = app.getSettings().getEuclidian(1);

		// calculate graphics dimensions
		int width = euclidian.getFileWidth();
		int height = euclidian.getFileHeight();

		// minimal size for documents, necessary for GeoGebra < 3
		if (width <= 100 || height <= 100) {
			width = 600;
			height = 440;
		}

		int ssize = 200;
		if (showSpreadsheet) {
			if (splitOrientation == SwingConstants.HORIZONTAL_SPLIT) {
				ssize = app.getSettings().getSpreadsheet().preferredSize()
						.getWidth();
			} else {
				ssize = app.getSettings().getSpreadsheet().preferredSize()
						.getHeight();
			}
		}

		// construct default xml data in case we're using an old version which
		// didn't
		// store the layout xml.
		DockPanelData[] dpXml = new DockPanelData[] {
				new DockPanelData(App.VIEW_EUCLIDIAN, null, true, false, false,
						AwtFactory.getPrototype().newRectangle(400, 400), defEV,
						width),
				new DockPanelData(App.VIEW_ALGEBRA, null, showAlgebra,
						false, false,
						AwtFactory.getPrototype().newRectangle(200, 400), defAV,
						(showAlgebra && sp2 > 0) ? sp2 : 200),
				new DockPanelData(App.VIEW_SPREADSHEET, null,
						showSpreadsheet, false, false,
						AwtFactory.getPrototype().newRectangle(400, 400), defSV,
						ssize) };
		tmp_perspective.setDockPanelData(dpXml);
		tmp_perspective.setShowToolBar(true);

		if (splitOrientation == SwingConstants.HORIZONTAL_SPLIT) {
			if (showSpreadsheet) {
				width += 5 + ssize;
			}

			if (showAlgebra) {
				width += 5 + sp2;
			}
		} else {
			if (showSpreadsheet) {
				height += 5 + ssize;
			}
			if (showAlgebra) {
				height += 5 + sp2;
			}
		}

		DockSplitPaneData[] spXml;

		// use two split panes in case all three views are visible
		if (showSpreadsheet && showAlgebra) {
			int total = splitOrientation == SwingConstants.HORIZONTAL_SPLIT
					? width : height;
			double relative1 = (double) sp2 / total;
			double relative2 = (double) sp1 / (total - sp2);
			spXml = new DockSplitPaneData[] {
					new DockSplitPaneData("", relative1, splitOrientation),
					new DockSplitPaneData(
							splitOrientation == SwingConstants.HORIZONTAL_SPLIT
									? "1" : "2",
							relative2, splitOrientation) };
		} else {
			int total = splitOrientation == SwingConstants.HORIZONTAL_SPLIT
					? width : height;
			double relative;
			if (showSpreadsheet) {
				relative = sp1 / (double) total;
			} else {
				relative = sp2 / (double) total;
			}
			spXml = new DockSplitPaneData[] {
					new DockSplitPaneData("", relative, splitOrientation) };
		}

		// additional space for toolbar and others, we add this here
		// as it shouldn't influence the relative positions of the
		// split pane dividers above
		width += 15;
		height += 90;

		if (tmp_perspective.getShowInputPanel()) {
			height += 50;
		}

		tmp_perspective.setSplitPaneData(spXml);
		this.dimension = AwtFactory.getPrototype().newDimension(width, height);
	}

	/**
	 * Kept for backward compatibility with version < 3.3
	 * 
	 * @param attrs
	 *            split attributes
	 * @return success
	 */
	boolean handleSplitDivider(LinkedHashMap<String, String> attrs) {
		try {
			sp1 = 0;
			sp2 = 0;
			spHorizontal = !"false".equals(attrs.get("horizontal"));

			// There were just two panels in GeoGebra < 3.2, therefore just one
			// split divider position
			// may be given. 'loc' in < 3.2 corresponds to 'loc2' in 3.2+.
			if (attrs.get("loc2") == null) {
				attrs.put("loc2", attrs.get("loc"));
				attrs.put("loc", "0"); // prevent NP exception in
										// Integer.parseInt()
			}

			if (spHorizontal) {
				sp1 = Integer.parseInt(attrs.get("loc"));
				sp2 = Integer.parseInt(attrs.get("loc2"));
			} else {
				String strLocVert = attrs.get("locVertical");
				if (strLocVert != null) {
					sp1 = Integer.parseInt(strLocVert);
				} else {
					sp1 = Integer.parseInt(attrs.get("loc"));
				}

				String strLocVert2 = attrs.get("locVertical2");
				if (strLocVert2 != null) {
					sp2 = Integer.parseInt(strLocVert2);
				} else {
					sp2 = Integer.parseInt(attrs.get("loc2"));
				}
			}
			return true;
		} catch (RuntimeException e) {
			sp1 = 0;
			sp2 = 0;
			return false;
		}
	}

	public GDimension getDimension() {
		return dimension;
	}
}
