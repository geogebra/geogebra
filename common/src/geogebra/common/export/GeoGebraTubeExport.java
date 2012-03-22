package geogebra.common.export;

import geogebra.common.GeoGebraConstants;
import geogebra.common.kernel.Construction;
import geogebra.common.main.AbstractApplication;

import java.io.IOException;

/**
 * Export GeoGebra worksheet to GeoGebraTube.
 * 
 * @author Florian Sonner
 */
public abstract class GeoGebraTubeExport {
	/**
	 * URL of the webpage to call if a file should be uploaded.
	 */
	protected static final String uploadURL = "http://www.geogebratube.org/upload";
	
	/**
	 * Application instance.
	 */
	public AbstractApplication app;
	
	/**
	 * Constructs a new instance of the GeoGebraTube exporter.
	 * 
	 * @param app
	 */
	public GeoGebraTubeExport(AbstractApplication app) {
		this.app = app;
	}
	
	/**
	 * Upload the current worksheet to GeoGebraTube.
	 */
	public abstract void uploadWorksheet();
	

	

	protected abstract void statusLabelSetText(String plain);

	protected abstract void pack();

	/**
	 * Hides progress dialog.
	 */
	public abstract void hideDialog();
	


	/**
	 * Append a base64 encoded .ggb file to the passed string buffer. 
	 * 
	 * @throws IOException
	 */
	protected String getBase64String() throws IOException {
		return app.getGgbApi().getBase64(true);
	}
	
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
		
		// build post query
		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append("data=");
		stringBuffer.append(encode(getBase64String()));

		stringBuffer.append("&title=");
		stringBuffer.append(encode(cons.getTitle()));
		
		stringBuffer.append("&pretext=");
		stringBuffer.append(encode(cons.getWorksheetText(0)));
		
		stringBuffer.append("&posttext=");
		stringBuffer.append(encode(cons.getWorksheetText(1)));
		
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
