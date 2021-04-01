package org.geogebra.common.gui.view.spreadsheet;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.kernel.CircularDefinitionException;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.Locateable;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.Traversing;
import org.geogebra.common.kernel.arithmetic.Traversing.SpreadsheetVariableRenamer;
import org.geogebra.common.kernel.arithmetic.ValidExpression;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoElementSpreadsheet;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.kernel.geos.GeoImage;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoText;
import org.geogebra.common.kernel.geos.LabelManager;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.main.App;
import org.geogebra.common.main.MyError.Errors;
import org.geogebra.common.main.SpreadsheetTableModel;
import org.geogebra.common.main.error.ErrorHandler;
import org.geogebra.common.main.error.ErrorHelper;
import org.geogebra.common.plugin.EventType;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.debug.Log;
import org.gwtproject.regexp.shared.MatchResult;
import org.gwtproject.regexp.shared.RegExp;

import com.himamis.retex.editor.share.util.Unicode;

public class RelativeCopy {

	protected Kernel kernel;

	App app;
	// protected MyTable table;
	protected static final RegExp pattern2 = RegExp
			.compile("(::|\\$)([A-Z]+)(::|\\$)([0-9]+)");
	private static GeoElementND redefinedElement;

	/**
	 * @param kernel
	 *            kernel
	 */
	public RelativeCopy(Kernel kernel) {
		this.kernel = kernel;
		app = kernel.getApplication();
	}

	/**
	 * Performs spreadsheet drag-copy operation.
	 * 
	 * @param sx1
	 *            source minimum column
	 * @param sy1
	 *            source minimum row
	 * @param sx2
	 *            source maximum column
	 * @param sy2
	 *            source maximum row
	 * @param dx1
	 *            destination minimum column
	 * @param dy1
	 *            destination minimum row
	 * @param dx2
	 *            destination maximum column
	 * @param dy2
	 *            destination maximum row
	 * @return success
	 */
	public boolean doDragCopy(int sx1, int sy1, int sx2, int sy2, int dx1,
			int dy1, int dx2, int dy2) {
		// -|1|-
		// 2|-|3
		// -|4|-
		app.setWaitCursor();
		Construction cons = kernel.getConstruction();

		try {
			boolean success = false;

			// collect all redefine operations
			cons.startCollectingRedefineCalls();

			boolean patternOK = isPatternSource(
					new CellRange(app, sx1, sy1, sx2, sy2));

			// ==============================================
			// vertical drag
			// ==============================================
			if ((sx1 == dx1) && (sx2 == dx2)) {

				if (dy2 < sy1) { // 1 ----- drag up
					if (((sy1 + 1) == sy2) && patternOK) {
						// two row source, so drag copy a linear pattern
						for (int x = sx1; x <= sx2; ++x) {
							GeoElement v1 = getValue(app, x, sy1);
							GeoElement v2 = getValue(app, x, sy2);
							if ((v1 == null) || (v2 == null)) {
								continue;
							}
							for (int y = dy2; y >= dy1; --y) {

								// quick solution: stop on fixed cell
								// this may be improved later
								GeoElement vOld = getValue(app, x, y);
								if (vOld != null
										&& vOld.isProtected(EventType.UPDATE)) {
									break;
								}

								GeoElement v3 = getValue(app, x, y + 2);
								GeoElement v4 = getValue(app, x, y + 1);
								String vs1 = v3.isGeoFunction() ? "(x)" : "";
								String vs2 = v4.isGeoFunction() ? "(x)" : "";
								String d0 = GeoElementSpreadsheet
										.getSpreadsheetCellName(x, y + 2) + vs1;
								String d1 = GeoElementSpreadsheet
										.getSpreadsheetCellName(x, y + 1) + vs2;
								String text = "=CopyFreeObject[2*" + d1 + "-"
										+ d0 + "]";
								doCopyNoStoringUndoInfo1(kernel, app, text, v4,
										x, y);
							}
						}
					} else { // not two row source, so drag-copy the first row
								// of the source
						doCopyVerticalNoStoringUndoInfo1(sx1, sx2, sy1, dy1,
								dy2);
					}
					success = true;
				}

				else if (dy1 > sy2) { // 4 ---- drag down
					if (((sy1 + 1) == sy2) && patternOK) {
						// two row source, so drag copy a linear pattern
						for (int x = sx1; x <= sx2; ++x) {
							GeoElement v1 = getValue(app, x, sy1);
							GeoElement v2 = getValue(app, x, sy2);
							if ((v1 == null) || (v2 == null)) {
								continue;
							}
							for (int y = dy1; y <= dy2; ++y) {

								// quick solution: stop on fixed cell
								// this may be improved later
								GeoElement vOld = getValue(app, x, y);
								if (vOld != null
										&& vOld.isProtected(EventType.UPDATE)) {
									break;
								}

								GeoElement v3 = getValue(app, x, y - 2);
								GeoElement v4 = getValue(app, x, y - 1);
								String vs1 = v3.isGeoFunction() ? "(x)" : "";
								String vs2 = v4.isGeoFunction() ? "(x)" : "";
								String d0 = GeoElementSpreadsheet
										.getSpreadsheetCellName(x, y - 2) + vs1;
								String d1 = GeoElementSpreadsheet
										.getSpreadsheetCellName(x, y - 1) + vs2;
								String text = "=CopyFreeObject[2*" + d1 + "-"
										+ d0 + "]";
								doCopyNoStoringUndoInfo1(kernel, app, text, v4,
										x, y);
							}
						}
					} else {
						// not two row source, so drag-copy the last row of the
						// source
						doCopyVerticalNoStoringUndoInfo1(sx1, sx2, sy2, dy1,
								dy2);
					}
					success = true;
				}
			}

			// ==============================================
			// horizontal drag
			// ==============================================
			else if ((sy1 == dy1) && (sy2 == dy2)) {
				if (dx2 < sx1) { // 2 ---- drag left
					if (((sx1 + 1) == sx2) && patternOK) {
						// two column source, so drag copy a linear pattern
						for (int y = sy1; y <= sy2; ++y) {
							GeoElement v1 = getValue(app, sx1, y);
							GeoElement v2 = getValue(app, sx2, y);
							if ((v1 == null) || (v2 == null)) {
								continue;
							}
							for (int x = dx2; x >= dx1; --x) {

								// quick solution: stop on fixed cell
								// this may be improved later
								GeoElement vOld = getValue(app, x, y);
								if (vOld != null
										&& vOld.isProtected(EventType.UPDATE)) {
									break;
								}

								GeoElement v3 = getValue(app, x + 2, y);
								GeoElement v4 = getValue(app, x + 1, y);
								String vs1 = v3.isGeoFunction() ? "(x)" : "";
								String vs2 = v4.isGeoFunction() ? "(x)" : "";
								String d0 = GeoElementSpreadsheet
										.getSpreadsheetCellName(x + 2, y) + vs1;
								String d1 = GeoElementSpreadsheet
										.getSpreadsheetCellName(x + 1, y) + vs2;
								String text = "=CopyFreeObject[2*" + d1 + "-"
										+ d0 + "]";
								doCopyNoStoringUndoInfo1(kernel, app, text, v4,
										x, y);
							}
						}
					} else {
						// not two column source, so drag-copy the first column
						// of the source
						doCopyHorizontalNoStoringUndoInfo1(sy1, sy2, sx1, dx1,
								dx2);
					}
					success = true;
				} else if (dx1 > sx2) { // 4 --- drag right
					if (((sx1 + 1) == sx2) && patternOK) {
						// two column source, so drag copy a linear pattern
						for (int y = sy1; y <= sy2; ++y) {
							GeoElement v1 = getValue(app, sx1, y);
							GeoElement v2 = getValue(app, sx2, y);
							if ((v1 == null) || (v2 == null)) {
								continue;
							}
							for (int x = dx1; x <= dx2; ++x) {

								// quick solution: stop on fixed cell
								// this may be improved later
								GeoElement vOld = getValue(app, x, y);
								if (vOld != null
										&& vOld.isProtected(EventType.UPDATE)) {
									break;
								}

								GeoElement v3 = getValue(app, x - 2, y);
								GeoElement v4 = getValue(app, x - 1, y);
								String vs1 = v3.isGeoFunction() ? "(x)" : "";
								String vs2 = v4.isGeoFunction() ? "(x)" : "";
								String d0 = GeoElementSpreadsheet
										.getSpreadsheetCellName(x - 2, y) + vs1;
								String d1 = GeoElementSpreadsheet
										.getSpreadsheetCellName(x - 1, y) + vs2;
								String text = "=CopyFreeObject[2*" + d1 + "-"
										+ d0 + "]";
								doCopyNoStoringUndoInfo1(kernel, app, text, v4,
										x, y);
							}
						}
					} else {
						// not two column source, so drag-copy the last column
						// of the source
						doCopyHorizontalNoStoringUndoInfo1(sy1, sy2, sx2, dx1,
								dx2);
					}
					success = true;
				}
			}

			// now do all redefining and build new construction
			cons.processCollectedRedefineCalls();

			if (success) {
				return true;
			}

			String msg = "sx1 = " + sx1 + "\r\n" + "sy1 = " + sy1 + "\r\n"
					+ "sx2 = " + sx2 + "\r\n" + "sy2 = " + sy2 + "\r\n"
					+ "dx1 = " + dx1 + "\r\n" + "dy1 = " + dy1 + "\r\n"
					+ "dx2 = " + dx2 + "\r\n" + "dy2 = " + dy2 + "\r\n";
			throw new RuntimeException(
					"Error from RelativeCopy.doCopy:\r\n" + msg);
		} catch (Exception ex) {
			// kernel.getApplication().showError(ex.getMessage());
			ex.printStackTrace();
			return false;
		} finally {
			cons.stopCollectingRedefineCalls();
			app.setDefaultCursor();
		}
	}

	/**
	 * Tests if a cell range can be used as the source for a pattern drag-copy.
	 * 
	 * @param cellRange
	 *            cell range
	 * @return whether all geos are acceptable
	 */
	private static boolean isPatternSource(CellRange cellRange) {
		// don't allow empty cells
		if (cellRange.hasEmptyCells()) {
			return false;
		}

		// test for any unacceptable geos in the range
		ArrayList<GeoElement> list = cellRange.toGeoList();
		for (GeoElement geo : list) {
			if (!(geo.isGeoNumeric() || geo.isGeoFunction()
					|| geo.isGeoPoint())) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Performs a vertical spreadsheet drag-copy. Cells are copied vertically
	 * row by row using a single given row as the copy source.
	 * 
	 * @param x1
	 *            minimum column of the drag-copy region
	 * @param x2
	 *            maximum column of the drag-copy region
	 * @param sy
	 *            source row
	 * @param dy1
	 *            destination minimum row
	 * @param dy2
	 *            destination maximum row
	 * @throws Exception
	 *             on parse problem, circular reference
	 */
	public void doCopyVerticalNoStoringUndoInfo1(int x1, int x2, int sy,
			int dy1, int dy2) throws Exception {

		// create a treeset, ordered by construction index
		// so that when we relative copy A1=1 B1=(A1+C1)/2 C1=3
		// B2 is done last
		TreeSet<GeoElement> tree = new TreeSet<>();
		for (int x = x1; x <= x2; ++x) {
			int ix = x - x1;
			GeoElement cell = getValue(app, x1 + ix, sy);
			if (cell != null) {
				tree.add(cell);
			}
		}

		for (int y = dy1; y <= dy2; ++y) {
			int iy = y - dy1;
			Iterator<GeoElement> iterator = tree.iterator();
			while (iterator.hasNext()) {
				GeoElement geo = (iterator.next());
				if (geo != null) {
					GPoint p = geo.getSpreadsheetCoords();

					GeoElement vOld = getValue(app, p.x, dy1 + iy);
					if (vOld != null && vOld.isProtected(EventType.UPDATE)) {
						continue;
					}

					doCopyNoStoringUndoInfo0(kernel, app, geo,
							getValue(app, p.x, dy1 + iy), 0, y - sy);
					// Application.debug(p.x+"");
				}
			}
		}
	}

	/**
	 * Performs a horizontal spreadsheet drag-copy. Cells are copied
	 * horizontally column by column using a single given column as the copy
	 * source.
	 * 
	 * @param y1
	 *            minimum row of the drag-copy region
	 * @param y2
	 *            maximum row of the drag-copy region
	 * @param sx
	 *            source column
	 * @param dx1
	 *            destination minimum column
	 * @param dx2
	 *            destination maximum column
	 * @throws Exception
	 *             on parse error, circular reference
	 */
	public void doCopyHorizontalNoStoringUndoInfo1(int y1, int y2, int sx,
			int dx1, int dx2) throws Exception {

		// create a treeset, ordered by construction index
		// so that when we relative copy A1=1 A2=(A1+A3)/2 A3=3
		// B2 is done last
		TreeSet<GeoElement> tree = new TreeSet<>();
		for (int y = y1; y <= y2; ++y) {
			int iy = y - y1;
			GeoElement cell = getValue(app, sx, y1 + iy);
			if (cell != null) {
				tree.add(cell);
			}
		}
		for (int x = dx1; x <= dx2; ++x) {
			int ix = x - dx1;

			Iterator<GeoElement> iterator = tree.iterator();
			while (iterator.hasNext()) {

				GeoElement geo = (iterator.next());

				if (geo != null) {
					GPoint p = geo.getSpreadsheetCoords();

					GeoElement vOld = getValue(app, dx1 + ix, p.y);
					if (vOld != null && vOld.isProtected(EventType.UPDATE)) {
						continue;
					}

					doCopyNoStoringUndoInfo0(kernel, app, geo,
							getValue(app, dx1 + ix, p.y), x - sx, 0);
					// Application.debug(p.y+"");
				}
			}
		}
	}

	/**
	 * @param kernel
	 *            kernel
	 * @param app
	 *            application
	 * @param value
	 *            copied value
	 * @param oldValue
	 *            overwritten value
	 * @param dx
	 *            column difference
	 * @param dy
	 *            row difference
	 * @return element copy
	 * @throws Exception
	 *             on parse problem, circular reference
	 */
	public static GeoElementND doCopyNoStoringUndoInfo0(Kernel kernel, App app,
			GeoElement value, GeoElementND oldValue, int dx, int dy)
			throws Exception {

		return doCopyNoStoringUndoInfo0(kernel, app, value, oldValue, dx, dy,
				-1, -1);
	}

	/**
	 * @param kernel
	 *            kernel
	 * @param app
	 *            application
	 * @param value
	 *            copied value
	 * @param oldValue
	 *            overwritten value
	 * @param dx
	 *            column difference
	 * @param dy
	 *            row difference
	 * @param rowStart
	 *            first row
	 * @param columnStart
	 *            first column
	 * @return element copy
	 * @throws Exception
	 *             on parse problem, circular reference
	 */
	public static GeoElementND doCopyNoStoringUndoInfo0(Kernel kernel, App app,
			GeoElement value, GeoElementND oldValue, int dx, int dy,
			int rowStart, int columnStart) throws Exception {
		if (value == null) {
			if (oldValue != null) {
				MatchResult matcher = GeoElementSpreadsheet.spreadsheetPattern
						.exec(oldValue
								.getLabel(StringTemplate.defaultTemplate));
				int column = GeoElementSpreadsheet
						.getSpreadsheetColumn(matcher);
				int row = GeoElementSpreadsheet.getSpreadsheetRow(matcher);

				prepareAddingValueToTableNoStoringUndoInfo(kernel, app, null,
						oldValue, column, row, true);
			}
			return null;
		}
		String text = null;

		// make sure a/0.001 doesn't become a/0

		StringTemplate highPrecision = StringTemplate.maxPrecision;
		if (value.isPointOnPath() || value.isPointInRegion()) {
			text = value.getDefinition(highPrecision);
		} else if (value.isChangeable()) {
			text = value.toValueString(highPrecision);
		} else {
			text = value.getDefinition(highPrecision);
		}

		// handle GeoText source value
		if (value.isGeoText() && !((GeoText) value).isTextCommand()) {
			// enclose text in quotes if we are copying an independent GeoText,
			// e.g. "2+3"
			if (value.isIndependent()) {
				text = "\"" + text + "\"";
			} else {

				// check if 'text' parses to a GeoText
				GeoText testGeoText = kernel.getAlgebraProcessor()
						.evaluateToText(text, false, false);

				// if it doesn't then force it to by adding +"" on the end
				if (testGeoText == null) {
					text = text + "+\"\"";
				}
			}
		}

		// for E1 = Polynomial[D1] we need value.getCommandDescription();
		// even though it's a GeoFunction
		if (value.isGeoFunction() && "".equals(text)) {
			// we need the definition without A1(x)= on the front
			text = ((GeoFunction) value).toSymbolicString(highPrecision);
		}

		boolean freeImage = false;

		if (value.isGeoImage()) {
			GeoImage image = (GeoImage) value;
			if (image.getParentAlgorithm() == null) {
				freeImage = true;
			}
		}
		boolean oldFlag = kernel.isUsingInternalCommandNames();
		kernel.setUseInternalCommandNames(true);
		// FIXME maybe try-catch this?
		ValidExpression exp = kernel.getParser().parseGeoGebraExpression(text);
		kernel.setUseInternalCommandNames(oldFlag);

		updateCellReferences(exp, dx, dy);

		text = exp.toString(highPrecision);

		// condition to show object
		GeoBoolean bool = value.getShowObjectCondition();
		String boolText = null, oldBoolText = null;
		if (bool != null) {
			if (bool.isChangeable()) {
				oldBoolText = bool.toValueString(highPrecision);
			} else {
				oldBoolText = bool.getDefinition(highPrecision);
			}
		}

		// dynamic color function
		GeoList dynamicColorList = value.getColorFunction();
		String colorText = null, oldColorText = null;
		if (dynamicColorList != null) {
			if (dynamicColorList.isChangeable()) {
				oldColorText = dynamicColorList.toValueString(highPrecision);
			} else {
				oldColorText = dynamicColorList.getDefinition(highPrecision);
			}
		}

		// allow pasting blank strings
		if ("".equals(text)) {
			text = "\"\"";
		}

		// make sure that non-GeoText elements are copied when the
		// equalsRequired option is set
		if (!value.isGeoText()
				&& app.getSettings().getSpreadsheet().equalsRequired()) {
			text = "=" + text;
		}

		// Application.debug("add text = " + text + ", name = " + (char)('A' +
		// column + dx) + (row + dy + 1));

		// get location of source cell
		// TODO: Why not always use getSpreadsheetCoords()?
		int row0 = rowStart;
		int column0 = columnStart;
		if (row0 > -1 && column0 > -1) {
			// nothing to do, already set
		} else if (value.isLabelSet()) {
			MatchResult matcher = GeoElementSpreadsheet.spreadsheetPattern
					.exec(value.getLabel(StringTemplate.defaultTemplate));
			column0 = GeoElementSpreadsheet.getSpreadsheetColumn(matcher);
			row0 = GeoElementSpreadsheet.getSpreadsheetRow(matcher);
		} else if (value.getSpreadsheetCoords() != null) {
			// the cell has been deleted but still exists in clipboard memory
			column0 = value.getSpreadsheetCoords().x;
			row0 = value.getSpreadsheetCoords().y;
		}

		// create the new cell geo
		GeoElementND value2;

		if (freeImage || value.isGeoButton()) {
			value2 = value.copy();
			if (oldValue != null) {
				oldValue.remove();
			}
			// value2.setLabel(table.getModel().getColumnName(column0 + dx)
			// + (row0 + dy + 1));
			value2.setLabel(GeoElementSpreadsheet
					.getSpreadsheetCellName(column0 + dx, row0 + dy));
			value2.updateRepaint();
		} else {
			value2 = prepareAddingValueToTableNoStoringUndoInfo(kernel, app,
					text, oldValue, column0 + dx, row0 + dy, true);
		}
		if (value2 == null) {
			return null;
		}
		value2.setAllVisualProperties(value, false);

		value2.setAuxiliaryObject(true);

		String[] startPoints = null;
		if (value instanceof Locateable) {
			Locateable loc = (Locateable) value;

			GeoPointND[] pts = loc.getStartPoints();

			if (pts != null) {

				startPoints = new String[pts.length];

				for (int i = 0; i < pts.length; i++) {
					startPoints[i] = ((GeoElement) pts[i])
							.getLabel(highPrecision);

					if (GeoElementSpreadsheet.spreadsheetPattern
							.test(startPoints[i])) {
						startPoints[i] = updateCellNameWithOffset(
								startPoints[i], dx, dy);
					}
				}
			}

		}

		if (oldBoolText != null) {
			exp = kernel.getParser().parseGeoGebraExpression(oldBoolText);
			updateCellReferences(exp, dx, dy);
			boolText = exp.toString(StringTemplate.maxPrecision);
		}

		// attempt to set updated condition to show object (if it's changed)
		if ((boolText != null)) {
			// removed as doesn't work for eg "random()<0.5" #388
			// && !boolText.equals(oldBoolText)) {

			GeoBoolean newConditionToShowObject = kernel.getAlgebraProcessor()
					.evaluateToBoolean(boolText, ErrorHelper.silent());
			if (newConditionToShowObject != null) {
				value2.setShowObjectCondition(newConditionToShowObject);
				value2.update(); // needed to hide/show object as
									// appropriate
			} else {
				return null;
			}

		}

		if (oldColorText != null) {
			exp = kernel.getParser().parseGeoGebraExpression(oldColorText);
			updateCellReferences(exp, dx, dy);
			colorText = exp.toString(StringTemplate.maxPrecision);
		}

		// copy the scripts from the old GeoElement
		value2.setScripting(value);

		// attempt to set updated dynamic color function (if it's changed)
		if ((colorText != null)) {
			// removed as doesn't work for eg "random()" #388
			// && !colorText.equals(oldColorText)) {
			try {
				// Application.debug("new color function: "+colorText);
				GeoList newColorFunction = kernel.getAlgebraProcessor()
						.evaluateToList(colorText);
				value2.setColorFunction(newColorFunction);
				// value2.update();
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}

		if (startPoints != null) {
			for (int i = 0; i < startPoints.length; i++) {
				((Locateable) value2).setStartPoint(kernel.getAlgebraProcessor()
						.evaluateToPoint(startPoints[i],
								app.getDefaultErrorHandler(), true),
						i);
			}

			value2.update();
		}

		return value2;
	}

	/**
	 * Updates the cell references in text according to a relative copy in the
	 * spreadsheet of offset (dx,dy) (changes only dependents of value) eg
	 * change A1 < 3 to A2 < 3 for a vertical copy
	 */
	private static void updateCellReferences(ValidExpression exp, int dx,
			int dy) {

		SpreadsheetVariableRenamer replacer = new Traversing.SpreadsheetVariableRenamer(
				dx, dy);

		exp.traverse(replacer);

	}

	/**
	 * @param name
	 *            source cell name
	 * @param dx
	 *            horizontal offset
	 * @param dy
	 *            vertical offset
	 * @return new cell name
	 */
	public static String updateCellNameWithOffset(String name, int dx, int dy) {
		MatchResult m = GeoElementSpreadsheet.spreadsheetPattern.exec(name);

		// $ or ""
		String m1 = m.getGroup(GeoElementSpreadsheet.MATCH_COLUMN_DOLLAR);
		// column eg A
		String m2 = m.getGroup(GeoElementSpreadsheet.MATCH_COLUMN);
		// $ or ""
		String m3 = m.getGroup(GeoElementSpreadsheet.MATCH_ROW_DOLLAR);
		// row eg 23
		String m4 = m.getGroup(GeoElementSpreadsheet.MATCH_ROW);

		if ("".equals(m1)) {
			int column = GeoElementSpreadsheet.getSpreadsheetColumn(m);
			if (column > -1 && dx + column > 0) {
				m2 = GeoElementSpreadsheet
						.getSpreadsheetColumnName(dx + column);
			}
		}

		if ("".equals(m3)) {
			int row = GeoElementSpreadsheet.getSpreadsheetRow(m);
			if (row > -1 && dy + row + 1 >= 1) {
				m4 = "" + (dy + row + 1);
			}
		}

		// preserve $ eg A$3 -> A$4
		StringBuilder newName = new StringBuilder();
		newName.append(m1);
		newName.append(m2);
		newName.append(m3);
		newName.append(m4);

		return newName.toString();
	}

	/**
	 * @param kernel
	 *            kernel
	 * @param app
	 *            application
	 * @param text
	 *            definition text
	 * @param geoForStyle
	 *            geo to be used for style of output
	 * @param column
	 *            column
	 * @param row
	 *            row
	 * @throws Exception
	 *             if definition of new geo fails
	 */
	public static void doCopyNoStoringUndoInfo1(Kernel kernel, App app,
			String text, GeoElement geoForStyle, int column, int row)
			throws Exception {
		GeoElement oldValue = getValue(app, column, row);

		if (text == null) {
			if (oldValue != null) {
				prepareAddingValueToTableNoStoringUndoInfo(kernel, app, null,
						oldValue, column, row, true);
			}
			return;
		}

		GeoElementND value2 = prepareAddingValueToTableNoStoringUndoInfo(kernel,
				app, text, oldValue, column, row, true);

		if (geoForStyle != null) {
			value2.setVisualStyle(geoForStyle);
		}
	}

	/**
	 * Returns array of GeoElements that depend on given GeoElement geo
	 * 
	 * @param geo
	 *            spreadsheet cell
	 * @return predecessors or empty array
	 */
	public static GeoElement[] getDependentObjects(GeoElement geo) {
		if (geo.isIndependent()) {
			return new GeoElement[0];
		}
		TreeSet<GeoElement> geoTree = geo.getAllPredecessors();
		return geoTree.toArray(new GeoElement[0]);
	}

	/**
	 * Returns 2D array, GeoElement[columns][rows], containing GeoElements found
	 * in the cell range with upper left corner (column1, row1) and lower right
	 * corner (column2, row2).
	 * 
	 * @param app
	 *            application
	 * @param column1
	 *            start column
	 * @param row1
	 *            start row
	 * @param column2
	 *            end column
	 * @param row2
	 *            end row
	 * @return array of geos in given range
	 */
	public static GeoElement[][] getValues(App app, int column1, int row1,
			int column2, int row2) {
		GeoElement[][] values = new GeoElement[(column2 - column1)
				+ 1][(row2 - row1) + 1];
		for (int r = row1; r <= row2; ++r) {
			for (int c = column1; c <= column2; ++c) {
				values[c - column1][r - row1] = getValue(app, c, r);
			}
		}
		return values;
	}

	/**
	 * Returns the GeoElement for the cell with the given column and row values.
	 * 
	 * @param app
	 *            application
	 * @param point
	 *            coordinates
	 * @return spreadsheet cell
	 */
	public static GeoElement getValue(App app, GPoint point) {
		return getValue(app, point.getX(), point.getY());
	}

	/**
	 * Returns the GeoElement for the cell with the given column and row values.
	 * 
	 * @param app
	 *            application
	 * @param column
	 *            column number
	 * @param row
	 *            row number
	 * @return spreadsheet cell
	 */
	public static GeoElement getValue(App app, int column, int row) {
		SpreadsheetTableModel tableModel = app.getSpreadsheetTableModel();
		if ((row < 0) || (row >= tableModel.getRowCount())) {
			return null;
		}
		if ((column < 0) || (column >= tableModel.getColumnCount())) {
			return null;
		}
		return (GeoElement) tableModel.getValueAt(row, column);
	}

	// =========================================================================
	// Cell Editing Methods
	// =========================================================================

	private static GeoElementND prepareNewValue(Kernel kernel, String name,
			String inputText) throws Exception {
		String text = inputText;
		if (text == null) {
			return null;
		}

		// remove leading equal sign, e.g. "= A1 + A2"
		if (text.length() > 0 && text.charAt(0) == '=') {
			text = text.substring(1);
		}
		text = text.trim();

		// no equal sign in input
		GeoElementND[] newValues = null;
		try {
			// check if input is same as name: circular definition
			if (text.equals(name)) {
				// circular definition
				throw new CircularDefinitionException();
			}

			// evaluate input text without an error dialog in case of unquoted
			// text
			newValues = kernel.getAlgebraProcessor()
					.processAlgebraCommandNoExceptionsOrErrors(text, false);

			// check if text was the label of an existing geo
			// toUpperCase() added to fix bug A1=1, enter just 'a1' or 'A1' into
			// cell B1 -> A1 disappears
			if (StringUtil.toLowerCaseUS(text)
					.equals(newValues[0]
							.getLabel(StringTemplate.defaultTemplate))
					// also need eg =a to work
					|| text.equals(newValues[0]
							.getLabel(StringTemplate.defaultTemplate))) {
				// make sure we create a copy of this existing or auto-created
				// geo
				// by providing the new cell name in the beginning
				text = name + " = " + text;
				newValues = kernel.getAlgebraProcessor()
						.processAlgebraCommandNoExceptions(text, false);
			}

			// check if name was auto-created: if yes we could have a circular
			// definition
			GeoElement autoCreateGeo = kernel.lookupLabel(name);
			if (autoCreateGeo != null) {
				// check for circular definition: if newValue depends on
				// autoCreateGeo
				boolean circularDefinition = false;
				for (int i = 0; i < newValues.length; i++) {
					if (newValues[i].isChildOf(autoCreateGeo)) {
						circularDefinition = true;
						break;
					}
				}

				if (circularDefinition) {
					// remove the auto-created object and the result
					autoCreateGeo.remove();
					newValues[0].remove();

					// circular definition
					throw new CircularDefinitionException();
				}
			}

			for (int i = 0; i < newValues.length; i++) {
				newValues[i].setAuxiliaryObject(true);
				if (newValues[i].isGeoText()) {
					newValues[i].setEuclidianVisible(false);
				}
			}

			LabelManager.setLabels(name, newValues); // set names to be D1,
													// E1,
			// F1, etc for multiple
			// objects
		} catch (CircularDefinitionException ce) {
			// circular definition
			kernel.getApplication().showError(Errors.CircularDefinition);
			return null;
		} catch (Exception e) {
			// create text if something went wrong
			if (text.startsWith("\"")) {
				text = text.substring(1, text.length() - 2);
			}
			text = "\"" + (text.replace("\"", "\"+UnicodeToLetter[34]+\""))
					+ "\"";
			newValues = kernel.getAlgebraProcessor()
					.processAlgebraCommandNoExceptions(text, false);
			newValues[0].setLabel(name);
			newValues[0].setEuclidianVisible(false);
			newValues[0].update();
		}
		return newValues[0];
	}

	private static void updateOldValue(final Kernel kernel,
			final GeoElementND oldValue, String name, String text0,
			final AsyncOperation<GeoElementND> callback) throws Exception {
		String text = text0;
		if (text.charAt(0) == '=') {
			text = text.substring(1);
		}
		// always redefine objects in spreadsheet, don't store undo info
		// here
		EvalInfo info = new EvalInfo(
				!kernel.getConstruction().isSuppressLabelsActive(), true);
		kernel.getAlgebraProcessor().changeGeoElementNoExceptionHandling(
				oldValue, text, info, false,
				new AsyncOperation<GeoElementND>() {

					@Override
					public void callback(GeoElementND newValue) {
						Log.debug("REDEFINED" + newValue);
						// newValue.setConstructionDefaults();
						newValue.setAllVisualProperties(oldValue.toGeoElement(),
								true);
						if (oldValue.isAuxiliaryObject()) {
							newValue.setAuxiliaryObject(true);
						}

						// Application.debug("GeoClassType = " +
						// newValue.getGeoClassType()+" " +
						// newValue.getGeoClassType());
						if (newValue.getGeoClassType() == oldValue
								.getGeoClassType()) {
							// newValue.setVisualStyle(oldValue);
						} else {
							kernel.getApplication().refreshViews();
						}
						callback.callback(newValue);
					}
				}, getErrorHandler(kernel, oldValue, name, text0, callback));

	}

	private static ErrorHandler getErrorHandler(final Kernel kernel,
			final GeoElementND oldValue, final String name, final String text0,
			final AsyncOperation<GeoElementND> callback) {
		return new ErrorHandler() {

			@Override
			public void showError(String msg) {
				Log.debug(msg);
				if (Errors.CircularDefinition.getError(kernel.getLocalization())
						.equals(msg)) {
					kernel.getApplication().getDefaultErrorHandler()
							.showError(msg);
				} else {
					handleThrowable();
				}

			}

			@Override
			public void resetError() {
				showError(null);
			}

			public void handleThrowable() {

				// if exception is thrown treat the input as text and try to
				// update the cell as a GeoText
				// reset the text string if old value is GeoText
				if (oldValue.isGeoText()) {
					((GeoText) oldValue).setTextString(text0);
					oldValue.updateCascade();
				}

				// if not currently a GeoText and no children, redefine the cell
				// as new GeoText
				else if (!oldValue.hasChildren()) {
					oldValue.remove();
					GeoElementND newValue;
					// add input as text
					try {
						newValue = prepareNewValue(kernel, name,
								"\"" + text0 + "\"");
					} catch (Throwable t) {
						try {
							newValue = prepareNewValue(kernel, name, "");
						} catch (Throwable tt) {
							newValue = new GeoNumeric(kernel.getConstruction(),
									Double.NaN);
						}
					}
					newValue.setEuclidianVisible(false);
					newValue.update();
					callback.callback(newValue);
				}

				// otherwise throw an exception and let the cell revert to the
				// old value
				else {
					// throw new Exception(e);
				}
			}

			@Override
			public void showCommandError(String command, String message) {
				handleThrowable();

			}

			@Override
			public boolean onUndefinedVariables(String string,
					AsyncOperation<String[]> callback1) {
				handleThrowable(); // a+b should also be text if a,b are
									// undefined
				return false;
			}

			@Override
			public String getCurrentCommand() {
				// TODO Auto-generated method stub
				return null;
			}
		};
	}

	/**
	 * Prepares a spreadsheet cell editor string for processing in the kernel
	 * and returns either (1) a new GeoElement for the cell or (2) null.
	 * 
	 * @param kernel
	 *            kernel
	 * @param app
	 *            application
	 * @param inputText
	 *            string representation of the new GeoElement
	 * @param oldValue
	 *            current cell GeoElement
	 * @param column
	 *            cell column
	 * @param row
	 *            cell row
	 * @param internal
	 *            whether to force internal command names
	 * @return either (1) a new GeoElement for the cell or (2) null
	 * @throws Exception
	 *             on parse error or circular reference
	 */
	public static GeoElementND prepareAddingValueToTableNoStoringUndoInfo(
			Kernel kernel, App app, String inputText, GeoElementND oldValue,
			int column, int row, boolean internal) throws Exception {
		String text = inputText;
		// get the cell name
		String name = GeoElementSpreadsheet.getSpreadsheetCellName(column, row);

		// trim the text
		if (text != null) {
			text = text.trim();
			if (text.length() == 0) {
				text = null;
			}
		}

		// if "=" is required before commands and text is not a number
		// or does not begin with "=" then surround it with quotes.
		// This will force the cell to become GeoText.
		if (app.getSettings().getSpreadsheet().equalsRequired()
				&& text != null) {

			boolean possibleString = text.startsWith("\"")
					&& text.endsWith("\"");

			if (!possibleString && !isNumber(text)) {
				text = "\"" + text + "\"";
			}
		}

		// if the cell is currently GeoText then prepare it for changes
		// make sure it can be changed to something else
		// eg (2,3 can be overwritten as (2,3)
		// if (oldValue != null && oldValue.isGeoText() &&
		// !oldValue.hasChildren()) {
		// oldValue.remove();
		// oldValue = null;
		// }

		// if the text is null then remove the current cell geo and return null
		if (text == null) {
			if (oldValue != null) {
				oldValue.remove();
			}
			return null;

			// else if the target cell is empty, try to create a new GeoElement
			// for this cell
		}
		boolean oldFlag = kernel.isUsingInternalCommandNames();
		try {
			// this will be a new geo
			kernel.setUseInternalCommandNames(internal);
			if (oldValue == null) {
				GeoElementND ret = prepareNewValue(kernel, name, text);
				kernel.setUseInternalCommandNames(oldFlag);
				return ret;
			}
			updateOldValue(kernel, oldValue, name, text,
					new AsyncOperation<GeoElementND>() {

						@Override
						public void callback(GeoElementND obj) {
							redefinedElement = obj;
						}
					});
			kernel.setUseInternalCommandNames(oldFlag);
			return redefinedElement;

		} catch (Throwable t) {
			kernel.setUseInternalCommandNames(oldFlag);
			return prepareNewValue(kernel, name, "");
		}
	}

	/**
	 * Tests if a string represents a number.
	 * 
	 * @param str
	 *            string
	 * @return true if the given string represents a number.
	 */
	public static boolean isNumber(String str) {
		String s = str;
		// trim and return false if empty string
		s = s.trim();
		if (s == null || s.length() == 0) {
			return false;
		}

		// remove degree/% char from end of string
		if (s.charAt(s.length() - 1) == Unicode.DEGREE_CHAR
				|| s.charAt(s.length() - 1) == '%') {
			s = s.substring(0, s.length() - 1);
		}

		// split the string using the exponentiation char
		// and test for possible number strings
		String[] s2 = s.split("E");
		if (s2.length == 1) {
			return isStandardNumber(s2[0]);
		} else if (s2.length == 2) {
			return isStandardNumber(s2[0]) && isStandardNumber(s2[1]);
		} else {
			return false;
		}
	}

	/**
	 * Returns true if a string is a standard number, i.e not in scientific
	 * notation
	 * 
	 * @param s
	 *            string to check
	 * @return whether string is a standard number
	 */
	private static boolean isStandardNumber(String s) {

		// return if empty string
		if (s == null || s.length() == 0) {
			return false;
		}

		// test the first char for a digit, sign or decimal point.
		Character c = s.charAt(0);
		if (!(StringUtil.isDigit(c) || c == '.' || c == '-' || c == '+'
				|| c == '\u2212')) {
			return false;
		}

		// test the remaining chars for digits or decimal point
		int decimalCount = 0;
		for (int i = 1; i < s.length(); i++) {
			c = s.charAt(i);
			if (StringUtil.isDigit(c)) {
				continue;
			}
			if (c == '.' && decimalCount == 0) {
				decimalCount++;
			} else {
				return false;
			}
		}
		return true;
	}

}
