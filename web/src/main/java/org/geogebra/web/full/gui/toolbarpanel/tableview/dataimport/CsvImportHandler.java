package org.geogebra.web.full.gui.toolbarpanel.tableview.dataimport;

import org.geogebra.common.gui.view.table.TableValuesPoints;
import org.geogebra.common.gui.view.table.TableValuesView;
import org.geogebra.common.gui.view.table.importer.DataImporter;
import org.geogebra.web.full.gui.dialog.OverwriteDataDialog;
import org.geogebra.web.full.main.AppWFull;
import org.geogebra.web.html5.gui.util.Dom;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.shared.components.dialog.DialogData;
import org.gwtproject.dom.client.Element;
import org.gwtproject.user.client.Command;
import org.gwtproject.user.client.ui.FileUpload;

import elemental2.dom.DomGlobal;
import elemental2.dom.File;
import elemental2.dom.FileReader;
import elemental2.dom.HTMLInputElement;
import jsinterop.base.Js;

public class CsvImportHandler {
	protected AppW appW;
	private FileUpload csvChooser;
	private DataImportSnackbar progressSnackbar;
	private Command csvHandler = () -> {
		csvChooser = getCSVChooser();
		if (getTable().isEmpty()) {
			csvChooser.click();
		} else {
			DialogData data = new DialogData(null, "Cancel", "Overwrite");
			OverwriteDataDialog overwriteDataDialog = new OverwriteDataDialog(
					appW, data);
			overwriteDataDialog.setOnPositiveAction(() -> csvChooser.click());
			overwriteDataDialog.show();
		}
	};

	public CsvImportHandler(AppW appW) {
		this.appW = appW;
	}

	private FileUpload getCSVChooser() {
		FileUpload csvChooser = new FileUpload();
		Element el = csvChooser.getElement();
		el.setAttribute("accept", ".csv");
		Dom.addEventListener(el, "change", event -> {
			HTMLInputElement input = Js.uncheckedCast(el);
			File fileToHandle = input.files.getAt(0);

			progressSnackbar = new DataImportSnackbar(appW, fileToHandle.name);
			getTable().getTableValuesModel().removeAllColumns();
			getTable().clearView();
			getTable().getTableValuesModel().setOnDataImportedRunnable(() -> {
				progressSnackbar.hide();
				final TableValuesPoints tvPoints = appW.getGuiManager()
						.getTableValuesPoints();
				if (tvPoints != null) {
					tvPoints.createAndAddPoints();
				}
			});
			openCSV(fileToHandle);
		});

		return csvChooser;
	}

	/**
	 * open csv file
	 * @param fileToHandle - selected file
	 */
	public final void openCSV(File fileToHandle) {
		FileReader reader = new FileReader();
		String fileName = fileToHandle.name;
		reader.addEventListener("load", (event) -> {
			if (reader.readyState == FileReader.DONE) {
				String fileStr = reader.result.asString();
				importData(DomGlobal.atob(fileStr.substring(fileStr.indexOf(",") + 1)), fileName);
			}
		});
		reader.readAsDataURL(fileToHandle);
	}

	private void importData(String csv, String fileName) {
		DataImportHandler handler = new DataImportHandler((AppWFull) appW, fileName,
				progressSnackbar);
		DataImporter importer = new DataImporter(getTable(), handler);
		importer.importCSV(csv, appW.getLocalization().getDecimalPoint());
	}

	public Command getCsvHandler() {
		return csvHandler;
	}

	private TableValuesView getTable() {
		return (TableValuesView) appW.getGuiManager().getTableValuesView();
	}
}
