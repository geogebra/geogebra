package geogebra.web.helper;

import geogebra.web.jso.JsFile;
import geogebra.web.jso.JsFileList;



public class FileReaderImpl implements FileReader {

	@Override
	public void readSingleGgbFile(JsFileList fileList, FileLoadCallback callback) {
		if (fileList.getLength() != 1) {
			callback.onError("Please drop exactly one file");
			return;
		}
		JsFile file = fileList.get(0);
		if (! file.getName().endsWith(".ggb")) {
			callback.onError("The provided file is not a GeoGebra file (*.ggb)");
			return;
		}
		
		nativeReadSingleGgbFile(file, callback);
	}
	
	private native void nativeReadSingleGgbFile(JsFile file, FileLoadCallback callback) /*-{
		var reader = new FileReader();
		reader.onerror = function(evt) {
			callback.@geogebra.web.helper.FileLoadCallback::onError(Ljava/lang/String;)('Could not read the provided file');
		};
		reader.onload = function(evt) {
			var bytes = new Uint8Array(evt.target.result);		// wrap ArrayBuffer
			callback.@geogebra.web.helper.FileLoadCallback::onSuccess(Lgeogebra/web/jso/JsUint8Array;)(bytes);
		};
		reader.readAsArrayBuffer(file);
	}-*/;

}
