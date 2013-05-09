package geogebra.common.export;

import geogebra.common.GeoGebraConstants;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Macro;
import geogebra.common.main.App;
import geogebra.common.main.Localization;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Export GeoGebra worksheet to GeoGebraTube.
 * 
 * @author Florian Sonner
 */
public abstract class GeoGebraTubeExport {
	/**
	 * URL of the webpage to call if a file should be uploaded.
	 * If you want to test GeoGebraTube uploads on a test server,
	 * use a test IP URL instead, e.g.: "http://140.78.116.131:8082/upload"
	 */
	//protected static final String uploadURL = "http://www.geogebratube.org/upload";
	protected static final String uploadURL = "http://test.geogebratube.org:8085/upload";
	
	/**
	 * Application instance.
	 */
	public App app;
	public Localization loc;

	protected ArrayList<Macro> macros;
	
	/**
	 * Constructs a new instance of the GeoGebraTube exporter.
	 * 
	 * @param app
	 */
	public GeoGebraTubeExport(App app) {
		this.app = app;
		this.loc = app.getLocalization();
	}
	
	/**
	 * Upload the current worksheet to GeoGebraTube.
	 */
	public abstract void uploadWorksheet(ArrayList<Macro> macros);
	

	

	protected abstract void statusLabelSetText(String plain);

	protected abstract void pack();

	/**
	 * Hides progress dialog.
	 */
	public abstract void hideDialog();
	


	/**
	 * returns a base64 encoded .ggb file 
	 * 
	 * @throws IOException
	 */
	protected String getBase64String() throws IOException {
		return app.getGgbApi().getBase64(true);
	}
	
	/**
	 * returns a base64 encoded .ggt file 
	 * 
	 * @throws IOException
	 */
	protected abstract String getBase64Tools(ArrayList<Macro> macros) throws IOException;
	
	/**
	 * Shows a small dialog with a progress bar. 
	 */
	protected abstract void showDialog();
	
	/**
	 * Storage container for uploading results.
	 * 
	 * @author Florian Sonner
	 */
	public class UploadResults {
		private String status;
		private String uid;
		private String errorMessage;
		
		/**
		 * Parse upload result string.
		 *  
		 * @param string
		 */		
		public UploadResults(String string) {
			status = uid = errorMessage = "";
			
			for(String line : string.split(",")) {
				int delimiterPos = line.indexOf(':');
				String key = line.substring(0, delimiterPos).toLowerCase();
				String value = line.substring(delimiterPos+1).toLowerCase();
				
				if(key.equals("status")) {
					status = value;
				} else if(key.equals("uid")) {
					uid = value;
				} else if(key.equals("error")) {
					errorMessage = value;
				}
			}
		}
		
		public boolean HasError() {
			return !status.equals("ok");
		}
		
		public String getStatus() { return status; }
		public String getUID() { return uid; }
		public String getErrorMessage() { return errorMessage; }
	}
	
	protected StringBuffer getPostData() throws IOException {
		Construction cons = app.getKernel().getConstruction();
		
		boolean isConstruction = (macros == null);
		
		// build post query
		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append("data=");
		stringBuffer.append(encode(isConstruction ? getBase64String() : getBase64Tools(macros)));

		stringBuffer.append("&type=");
		stringBuffer.append(isConstruction ? "ggb" : "ggt");
		
		if(isConstruction) {
			stringBuffer.append("&title=");
			stringBuffer.append(encode(cons.getTitle()));
			
			stringBuffer.append("&pretext=");
			stringBuffer.append(encode(cons.getWorksheetText(0)));
			
			stringBuffer.append("&posttext=");
			stringBuffer.append(encode(cons.getWorksheetText(1)));
		}
		
		stringBuffer.append("&version=");
		stringBuffer.append(encode(GeoGebraConstants.VERSION_STRING));
		
		return stringBuffer;
	}

	protected abstract String encode(String str);

	protected abstract void setMaximum(int i);

	protected abstract void setMinimum(int i);

	protected abstract void setIndeterminate(boolean b);

	protected abstract void setValue(int end);

	protected abstract void setEnabled(boolean b);
}
