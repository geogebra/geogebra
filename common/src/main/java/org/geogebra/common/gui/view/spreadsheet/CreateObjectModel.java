package org.geogebra.common.gui.view.spreadsheet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.algos.AlgoPolyLine;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.main.App;
import org.geogebra.common.util.IndexHTMLBuilder;

/**
 * Dialog to create GeoElements (lists, matrices, tabletext, etc.) from
 * spreadsheet cell selections
 * 
 * @author G. Sturr
 * 
 */
@SuppressWarnings({ "javadoc", "rawtypes" })
public class CreateObjectModel {
	public interface  ICreateObjectListener {
		void setName(String name);
		boolean isVisible();
		void setVisible(boolean value);
		void setSortVisible(boolean isVisible);
		boolean isCopiedByValue();
		boolean isScannedByColumn();
		boolean isLeftToRight();
		boolean isTranspose();
		void updatePreview(String latexStr, boolean isLatexDrawable);
	}
	private CellRangeProcessor cp;
	private ArrayList<CellRange> selectedCellRanges;

	public static final int TYPE_LIST = 0;
	public static final int TYPE_MATRIX = 2;
	public static final int TYPE_LISTOFPOINTS = 1;
	public static final int TYPE_TABLETEXT = 3;
	public static final int TYPE_POLYLINE = 4;
	private int objectType = TYPE_LIST;

	private GeoElement newGeo;
	private boolean keepNewGeo = false;
	private MyTable table;
	private App app;
	private ICreateObjectListener listener;

	public CreateObjectModel(App app, SpreadsheetViewInterface view, int objectType,
			ICreateObjectListener listener) {
		this.app = app;
		this.objectType = objectType;
		this.listener = listener;
		this.table = (MyTable )view.getSpreadsheetTable();



	}



	public List<String> getObjectTypeNames() {
		return Arrays.asList( 
				app.getMenu("List"),
				app.getMenu("Matrix"),
				app.getMenu("ListOfPoints"),
				app.getMenu("Table"),
				app.getMenu("PolyLine")
				);
	}

	public String getTitle() {
		String titleText = "";
		switch (getObjectType()) {
		case TYPE_LIST:
			titleText = app.getMenu("CreateList");
			break;

		case TYPE_LISTOFPOINTS:
			titleText = app.getMenu("CreateListOfPoints");
			break;

		case TYPE_TABLETEXT:
			titleText = app.getMenu("CreateTable");
			break;

		case TYPE_POLYLINE:
			titleText = app.getMenu("CreatePolyLine");
			break;

		case TYPE_MATRIX:
			titleText = app.getMenu("CreateMatrix");
			break;
		}
		App.debug("[CO] title is " + titleText);
		return titleText;

	}

	public void update() {

		if (newGeo == null) {
			listener.setName("");
		} else
			listener.setName(newGeo.getLabel(StringTemplate.defaultTemplate));

		listener.setSortVisible(getObjectType() == TYPE_POLYLINE);

	}


	public void cancel() {
		keepNewGeo = true;
		listener.setVisible(false);
	}

	public void apply() {
		// processInput();
	} 

	public void ok() {
		if (newGeo != null) {
			newGeo.remove();
		}
		listener.setVisible(false);

	} 

	public void close() {
		// either remove our geo or keep it and make it visible
		if (keepNewGeo && newGeo != null) {
			addNewGeoToConstruction();
		} else {
			newGeo.remove();
		}	
	}

	private void addNewGeoToConstruction() {

		if (getObjectType() == TYPE_LISTOFPOINTS || getObjectType() == TYPE_POLYLINE) {
			app.getKernel().getConstruction()
			.addToConstructionList(newGeo.getParentAlgorithm(), true);
		}

		newGeo.setEuclidianVisible(true);
		if (!newGeo.isGeoText())
			newGeo.setAuxiliaryObject(false);

		if (getObjectType() == TYPE_LISTOFPOINTS) {
			GeoList gl = (GeoList) newGeo;
			for (int i = 0; i < gl.size(); i++) {
				gl.get(i).setEuclidianVisible(true);
				gl.get(i).setAuxiliaryObject(false);
			}
		}

		if (getObjectType() == TYPE_POLYLINE) {
			GeoPoint[] pts = ((AlgoPolyLine) newGeo.getParentAlgorithm())
					.getPoints();
			for (int i = 0; i < pts.length; i++) {
				pts[i].setEuclidianVisible(true);
				pts[i].setAuxiliaryObject(false);
				pts[i].updateRepaint();
			}
		}

		newGeo.update();
		app.storeUndoInfo();
	}

	public void createNewGeo(String name) {

		boolean nullGeo = newGeo == null;

		if (!nullGeo) {
			if (getObjectType() == TYPE_LISTOFPOINTS) {
				GeoList gl = (GeoList) newGeo;
				for (int i = 0; i < gl.size(); i++)
					gl.get(i).remove();
			}

			if (getObjectType() == TYPE_POLYLINE) {
				GeoPoint[] pts = ((AlgoPolyLine) newGeo.getParentAlgorithm())
						.getPoints();
				for (int i = 0; i < pts.length; i++)
					pts[i].remove();
			}
			newGeo.remove();
		}

		int column1 = selectedCellRanges.get(0).getMinColumn();
		int column2 = selectedCellRanges.get(0).getMaxColumn();
		int row1 = selectedCellRanges.get(0).getMinRow();
		int row2 = selectedCellRanges.get(0).getMaxRow();

		boolean copyByValue = listener.isCopiedByValue();
		boolean scanByColumn = listener.isScannedByColumn();
		boolean leftToRight = listener.isLeftToRight();
		boolean transpose = listener.isTranspose();
		boolean doCreateFreePoints = true;
		boolean doStoreUndo = true;
		boolean isSorted = false;

		try {
			switch (getObjectType()) {

			case TYPE_LIST:
				newGeo = cp.createList(getSelectedCellRanges(), scanByColumn,
						copyByValue);
				break;

			case TYPE_LISTOFPOINTS:
				newGeo = cp.createPointGeoList(getSelectedCellRanges(), copyByValue,
						leftToRight, isSorted, doStoreUndo, doCreateFreePoints);
				newGeo.setLabel(null);
				for (int i = 0; i < ((GeoList) newGeo).size(); i++) {
					((GeoList) newGeo).get(i).setAuxiliaryObject(true);
					((GeoList) newGeo).get(i).setEuclidianVisible(false);
				}
				newGeo.updateRepaint();
				break;

			case TYPE_MATRIX:
				newGeo = cp.createMatrix(column1, column2, row1, row2,
						copyByValue, transpose);
				break;

			case TYPE_TABLETEXT:
				newGeo = cp.createTableText(column1, column2, row1, row2,
						copyByValue, transpose);
				newGeo.setEuclidianVisible(false);
				newGeo.updateRepaint();
				break;

			case TYPE_POLYLINE:
				newGeo = cp.createPolyLine(getSelectedCellRanges(), copyByValue,
						leftToRight);
				newGeo.setLabel(null);
				GeoPoint[] pts = ((AlgoPolyLine) newGeo.getParentAlgorithm())
						.getPoints();
				for (int i = 0; i < pts.length; i++) {
					pts[i].setAuxiliaryObject(true);
					pts[i].setEuclidianVisible(false);
					pts[i].updateRepaint();
				}
				newGeo.updateRepaint();
				break;

			}

			// String latexStr = newGeo.getLaTeXAlgebraDescription(true);

			listener.updatePreview(newGeo.getFormulaString(
					StringTemplate.latexTemplate, true), newGeo.isLaTeXDrawableGeo());
					;

			// System.out.println(latexStr);


			if (!nullGeo) {
				newGeo.setLabel(name);
				newGeo.setAuxiliaryObject(true);
				newGeo.setEuclidianVisible(false);
			}


		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void cleanUp() {
		if (newGeo == null) {
			return;
		}
		if (keepNewGeo) {
			addNewGeoToConstruction();
		} else {
			newGeo.remove();
		}
	}
	
	public String getNonLatexText() {
		return newGeo.getAlgebraDescriptionTextOrHTMLDefault(new IndexHTMLBuilder(
				true));
	}

	public CellRangeProcessor getCellRangeProcessor() {
		return cp;
	}



	public void setCellRangeProcessor(CellRangeProcessor cp) {
		this.cp = cp;
	}



	public ArrayList<CellRange> getSelectedCellRanges() {
		return selectedCellRanges;
	}



	public void setSelectedCellRanges(ArrayList<CellRange> selectedCellRanges) {
		this.selectedCellRanges = selectedCellRanges;
	}



	public int getObjectType() {
		return objectType;
	}



	public void setObjectType(int objectType) {
		this.objectType = objectType;

	}



	public void setListType() {
		objectType = TYPE_LIST;
	}

	public static final int OPTION_ORDER = 0;
	public static final int OPTION_XY = 1;
	public static final int OPTION_TRANSPOSE = 2;

	public int getOptionType() {
		int idx = 0;

		switch (getObjectType()) {
		case CreateObjectModel.TYPE_LIST:
			idx = OPTION_ORDER;
			break;
		case CreateObjectModel.TYPE_LISTOFPOINTS:
			idx = OPTION_XY;
			break;
		case CreateObjectModel.TYPE_MATRIX:
			idx = OPTION_TRANSPOSE;
			break;
		case CreateObjectModel.TYPE_TABLETEXT:
			idx = OPTION_TRANSPOSE;
			break;
		case CreateObjectModel.TYPE_POLYLINE:
			idx = OPTION_XY;
		}

		return idx;
	}


}
