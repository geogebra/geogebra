package geogebra.web.helper;

import geogebra.web.jso.JsFileList;



public interface FileReader {
	
	void readSingleGgbFile(JsFileList fileList, FileLoadCallback callback);
	
}
