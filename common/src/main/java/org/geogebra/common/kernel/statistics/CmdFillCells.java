package org.geogebra.common.kernel.statistics;

import java.util.ArrayList;

import org.geogebra.common.awt.GPoint;
import org.geogebra.common.gui.view.spreadsheet.RelativeCopy;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.MyPoint;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.commands.CommandProcessor;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoElementSpreadsheet;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoLocus;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.main.MyError;
import org.geogebra.common.util.debug.Log;
import org.gwtproject.regexp.shared.MatchResult;

/**
 * FillCells
 */
public class CmdFillCells extends CommandProcessor {

	/**
	 * Create new command processor
	 * 
	 * @param kernel
	 *            kernel
	 */
	public CmdFillCells(Kernel kernel) {
		super(kernel);
	}

	@Override
	final public GeoElement[] process(Command c, EvalInfo info) throws MyError {
		if (!info.isScripting()) {
			return new GeoElement[0];
		}
		int n = c.getArgumentNumber();
		GeoElement[] arg;

		switch (n) {
		case 2:
			app.setScrollToShow(false);
			arg = resArgs(c);
			if (arg[0].isGeoList()) {

				GeoList cellRange = (GeoList) arg[0];

				if (!(cellRange
						.getParentAlgorithm() instanceof AlgoCellRange)) {
					Log.debug("not cell range");
					throw argErr(c, arg[0]);

				}

				AlgoCellRange algo = (AlgoCellRange) cellRange
						.getParentAlgorithm();

				GPoint[] points = algo.getRectangle();

				GPoint startCoords = points[0];
				GPoint endCoords = points[1];

				int minCol = Math.min(startCoords.x, endCoords.x);
				int maxCol = Math.max(startCoords.x, endCoords.x);
				int minRow = Math.min(startCoords.y, endCoords.y);
				int maxRow = Math.max(startCoords.y, endCoords.y);

				// Application.debug(minCol+" "+maxCol+" "+minRow+" "+maxRow);

				GeoElement geo = arg[1];
				GeoElement[] ret = {};

				if (geo.isGeoLocus()) {

					if (!geo.isDefined()) {
						throw argErr(c, arg[1]);
					}

					if (minCol + 1 != maxCol) {
						throw argErr(c, arg[0]);
					}

					GeoLocus locus = (GeoLocus) geo;

					ArrayList<MyPoint> al = locus.getPoints();

					int length = Math.min(al.size(), maxRow - minRow);

					for (int i = 0; i < length; i++) {
						int row = i + minRow;

						try {
							// cell will have been autocreated by eg A1:A3 in
							// command, so delete
							removePossibleGeo(GeoElementSpreadsheet
									.getSpreadsheetCellName(minCol, row));
							removePossibleGeo(GeoElementSpreadsheet
									.getSpreadsheetCellName(minCol + 1, row));

							MyPoint p = al.get(i);

							kernel.getGeoElementSpreadsheet()
									.setSpreadsheetCell(app, row, minCol,
											new GeoNumeric(cons, p.x));
							kernel.getGeoElementSpreadsheet()
									.setSpreadsheetCell(app, row, minCol + 1,
											new GeoNumeric(cons, p.y));
						} catch (Exception e) {
							e.printStackTrace();
							app.setScrollToShow(true);
							throw argErr(c, arg[1]);
						}

					}
					app.setScrollToShow(true);

					return ret;

				}
				if (!geo.isGeoList()) {

					for (int row = minRow; row <= maxRow; row++) {
						for (int col = minCol; col <= maxCol; col++) {
							try {
								// cell will have been autocreated by eg A1:A3
								// in command, so delete
								// in case it's being filled by eg GeoText
								removePossibleGeo(GeoElementSpreadsheet
										.getSpreadsheetCellName(col, row));

								// eg FillCells[B1:B7,A1+1]
								// change to eg A2+1, A3+1, A4+1 etc
								// FillCells[B1:B7,A1] doesn't change A1
								// use FillCells[B1:B7,A1+0] for that
								RelativeCopy.doCopyNoStoringUndoInfo0(kernel,
										app, geo, null, col - minCol,
										row - minRow, minRow, minCol);

								// old code
								// kernelA.getGeoElementSpreadsheet()
								// .setSpreadsheetCell(app, row, col, geo);
							} catch (Exception e) {
								app.setScrollToShow(true);
								e.printStackTrace();
								throw argErr(c, arg[1]);
							}
						}
					}
					app.setScrollToShow(true);
					return ret;
				}

				GeoList list = (GeoList) geo;
				if (list.isMatrix()) {

					int countX = 0;
					for (int row = minRow; row <= maxRow; row++) {
						GeoList rowList = (GeoList) list
								.get(countX % list.size());
						countX++;
						int countY = 0;
						for (int col = minCol; col <= maxCol; col++) {
							try {
								// cell will have been autocreated by eg A1:A3
								// in command, so delete
								// in case it's being filled by eg GeoText
								removePossibleGeo(GeoElementSpreadsheet
										.getSpreadsheetCellName(col, row));

								kernel.getGeoElementSpreadsheet()
										.setSpreadsheetCell(app, row, col,
												rowList.get(countY
														% rowList.size()));
								countY++;

							} catch (Exception e) {
								app.setScrollToShow(true);
								e.printStackTrace();
								throw argErr(c, arg[1]);
							}
						}
					}

				} else {
					// not matrix, just use each list value in turn
					int count = 0;
					for (int row = minRow; row <= maxRow; row++) {
						for (int col = minCol; col <= maxCol; col++) {
							try {
								// cell will have been autocreated by eg A1:A3
								// in command, so delete
								// in case it's being filled by eg GeoText
								removePossibleGeo(GeoElementSpreadsheet
										.getSpreadsheetCellName(col, row));

								kernel.getGeoElementSpreadsheet()
										.setSpreadsheetCell(app, row, col,
												list.get(count % list.size()));
								count++;

							} catch (Exception e) {
								app.setScrollToShow(true);
								e.printStackTrace();
								throw argErr(c, arg[1]);
							}
						}
					}

				}

				app.storeUndoInfo();
				app.setScrollToShow(true);
				return ret;

			}

		// arg[0] not list
		{

			if (GeoElementSpreadsheet.hasSpreadsheetLabel(arg[0])) {

				if (!arg[1].isGeoList()) {
					app.setScrollToShow(true);
					throw argErr(c, arg[1]);
				}

				GeoList list = (GeoList) arg[1];

				MatchResult matcher = GeoElementSpreadsheet.spreadsheetPattern
						.exec(arg[0].getLabel(StringTemplate.defaultTemplate));
				int column = GeoElementSpreadsheet
						.getSpreadsheetColumn(matcher);
				int row = GeoElementSpreadsheet.getSpreadsheetRow(matcher);

				if (row == -1 || column == -1) {
					app.setScrollToShow(true);
					throw argErr(c, arg[0]);
				}

				if (list.isMatrix()) {
					// 2D fill
					// FillCells[B3,{"a","b"}] will autocreate B3=0 so we
					// need to remove B3
					if (arg[0] != null) {
						arg[0].remove();
					}

					try {
						int rows = list.size();
						int cols = ((GeoList) list.get(0)).size();
						for (int r = 0; r < rows; r++) {
							GeoList rowList = (GeoList) list.get(r);
							for (int c1 = 0; c1 < cols; c1++) {
								kernel.getGeoElementSpreadsheet()
										.setSpreadsheetCell(app, row + r,
												column + c1,
												rowList.get(c1).copy());
							}
						}
					} catch (Exception e) {
						app.setScrollToShow(true);
						throw argErr(c, list);
					}

				} else {
					// 1D fill
					// FillCells[B3,{"a","b"}] will autocreate B3=0 so we
					// need to remove B3
					if (arg[0] != null) {
						arg[0].remove();
					}

					for (int i = list.size() - 1; i >= 0; i--) {
						try {
							// Application.debug("setting "+row+" "+(column+i)+"
							// to "+list.get(i).toString());
							kernel.getGeoElementSpreadsheet()
									.setSpreadsheetCell(app, row, column + i,
											list.get(i).copy());
						} catch (Exception e) {
							e.printStackTrace();
							app.setScrollToShow(true);
							throw argErr(c, arg[1]);
						}
					}
				}

			} else {
				app.setScrollToShow(true);
				throw argErr(c, arg[0]);
			}
		}

			GeoElement[] ret = {};
			app.storeUndoInfo();
			app.setScrollToShow(true);
			return ret;

		default:
			throw argNumErr(c);
		}
	}

	private void removePossibleGeo(String label) {
		GeoElement geo = kernel.lookupLabel(label);
		if (geo != null) {
			geo.remove();
		}
	}

}
