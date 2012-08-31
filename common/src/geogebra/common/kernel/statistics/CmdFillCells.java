package geogebra.common.kernel.statistics;

import geogebra.common.awt.GPoint;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.MyPoint;
import geogebra.common.kernel.StringTemplate;
import geogebra.common.kernel.arithmetic.Command;
import geogebra.common.kernel.commands.CommandProcessor;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoElementSpreadsheet;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoLocus;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.main.App;
import geogebra.common.main.MyError;

import java.util.ArrayList;

import com.google.gwt.regexp.shared.MatchResult;

/**
 *FillCells
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
	final public GeoElement[] process(Command c) throws MyError {
		int n = c.getArgumentNumber();
		GeoElement[] arg;

		switch (n) {
		case 2:
			app.setScrollToShow(false);
			arg = resArgs(c);
			if (arg[0].isGeoList()) {

				GeoList cellRange = (GeoList) arg[0];

				if (!(cellRange.getParentAlgorithm() instanceof AlgoCellRange)) {
					App.debug("not cell range");
					throw argErr(app, c.getName(), arg[0]);

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

					if (!geo.isDefined())
						throw argErr(app, c.getName(), arg[1]);

					if (minCol + 1 != maxCol)
						throw argErr(app, c.getName(), arg[0]);

					GeoLocus locus = (GeoLocus) geo;

					ArrayList<MyPoint> al = locus.getPoints();

					int length = Math.min(al.size(), maxRow - minRow);

					for (int i = 0; i < length; i++) {
						int row = i + minRow;

						try {
							// cell will have been autocreated by eg A1:A3 in
							// command, so delete
							kernelA.lookupLabel(
									GeoElementSpreadsheet.getSpreadsheetCellName(minCol,
											row)).remove();
							kernelA.lookupLabel(
									GeoElementSpreadsheet.getSpreadsheetCellName(
											minCol + 1, row)).remove();

							MyPoint p = al.get(i);														

							kernelA.getGeoElementSpreadsheet().setSpreadsheetCell(app, row, minCol,
									new GeoNumeric(cons, p.x));
							kernelA.getGeoElementSpreadsheet().setSpreadsheetCell(app, row, minCol + 1,
									new GeoNumeric(cons, p.y));
						} catch (Exception e) {
							e.printStackTrace();
							app.setScrollToShow(true);
							throw argErr(app, c.getName(), arg[1]);
						}

					}
					app.setScrollToShow(true);

					return ret;

				}
				if (!geo.isGeoList()) {
					for (int row = minRow; row <= maxRow; row++)
						for (int col = minCol; col <= maxCol; col++) {
							try {
								// cell will have been autocreated by eg A1:A3
								// in command, so delete
								// in case it's being filled by eg GeoText
								kernelA.lookupLabel(
										GeoElementSpreadsheet.getSpreadsheetCellName(col,
												row)).remove();

								kernelA.getGeoElementSpreadsheet().setSpreadsheetCell(app, row, col,
										geo);
							} catch (Exception e) {
								app.setScrollToShow(true);
								e.printStackTrace();
								throw argErr(app, c.getName(), arg[1]);
							}
						}
					app.setScrollToShow(true);
					return ret;
				}

				// TODO finish
				// GeoList list = (GeoList)geo;
				// if (list.isMatrix())

				app.storeUndoInfo();
				app.setScrollToShow(true);
				return ret;

			} 
			{

				app.getKernel().getGeoElementSpreadsheet();
				if (GeoElementSpreadsheet.hasSpreadsheetLabel(arg[0])) {

					if (!arg[1].isGeoList()) {
						app.setScrollToShow(true);
						throw argErr(app, c.getName(), arg[1]);
					}

					GeoList list = (GeoList) arg[1];

					MatchResult matcher = GeoElementSpreadsheet.spreadsheetPattern
							.exec(arg[0].getLabel(StringTemplate.defaultTemplate));
					int column = GeoElementSpreadsheet.getSpreadsheetColumn(matcher);
					int row = GeoElementSpreadsheet.getSpreadsheetRow(matcher);

					if (row == -1 || column == -1) {
						app.setScrollToShow(true);
						throw argErr(app, c.getName(), arg[0]);
					}

					if (list.isMatrix()) {
						// 2D fill
						// FillCells[B3,{"a","b"}] will autocreate B3=0 so we
						// need to remove B3
						arg[0].remove();

						try {
							int rows = list.size();
							int cols = ((GeoList) list.get(0)).size();
							for (int r = 0; r < rows; r++) {
								GeoList rowList = (GeoList) list.get(r);
								for (int c1 = 0; c1 < cols; c1++) {
									kernelA.getGeoElementSpreadsheet()
											.setSpreadsheetCell(app, row + r,
													column + c1, rowList
															.get(c1).copy());
								}
							}
						} catch (Exception e) {
							app.setScrollToShow(true);
							throw argErr(app, c.getName(), list);
						}

					} else {
						// 1D fill
						// FillCells[B3,{"a","b"}] will autocreate B3=0 so we
						// need to remove B3
						arg[0].remove();

						for (int i = list.size() - 1; i >= 0; i--)
							try {
								// Application.debug("setting "+row+" "+(column+i)+" to "+list.get(i).toString());
								kernelA.getGeoElementSpreadsheet().setSpreadsheetCell(app, row, column
										+ i, list.get(i).copy());
							} catch (Exception e) {
								e.printStackTrace();
								app.setScrollToShow(true);
								throw argErr(app, c.getName(), arg[1]);
							}
					}

				} else {
					app.setScrollToShow(true);
					throw argErr(app, c.getName(), arg[0]);
				}
			}

			GeoElement[] ret = {};
			app.storeUndoInfo();
			app.setScrollToShow(true);
			return ret;

		default:
			throw argNumErr(app, c.getName(), n);
		}
	}

}
