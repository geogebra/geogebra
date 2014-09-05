package geogebra.web.move.ggtapi.models;

import geogebra.common.move.ggtapi.models.Material;
import geogebra.common.move.ggtapi.models.Request;
import geogebra.common.move.ggtapi.models.json.JSONObject;
import geogebra.common.move.ggtapi.models.json.JSONString;
import geogebra.html5.main.AppW;


public class UploadRequest implements Request {

	private final String API = "1.0.0";
	private final String GGB = "geogebra";
	private final String TASK = "upload";
	private final String TYPE = "applet";
	private AppW app;
	private String consTitle = "GeoGebra";
	private String uniqueID;
	private String base64;
	
	
	/**
	 * @param app AppW
	 * @param consTitle title of construction
	 */
	UploadRequest(AppW app, String consTitle, String base64) {
		this.app = app;
		this.consTitle = consTitle;
		this.uniqueID = this.app.getUniqueId();
		this.base64 = base64;
	}
	
	/**
	 * 
	 * @param app AppW
	 * @param mat Material
	 */
	public UploadRequest(AppW app, Material mat) {
	    this.app = app;
	    this.consTitle = mat.getTitle();
	    this.uniqueID = Integer.toString(mat.getId());
	    this.base64 = mat.getBase64();
    }
	
	
	@Override
    public String toJSONString() {
		//TODO for save we only need title
		//request
		JSONObject request = new JSONObject();
		
		JSONObject api = new JSONObject();
		api.put("-api", new JSONString(this.API));
		
			//login
			JSONObject login = new JSONObject();
				login.put("-type", new JSONString(this.GGB));
				login.put("-token", new JSONString(app.getLoginOperation().getModel().getLoggedInUser().getLoginToken()));
			api.put("login", login);
		
			//task
			JSONObject task = new JSONObject();
			task.put("-type", new JSONString(this.TASK));
			
			if (this.uniqueID != null) {
				//ID
				task.put("id", new JSONString(this.uniqueID));
			}
			
			//type
			task.put("type", new JSONString(this.TYPE));
			
			//title
			task.put("title", new JSONString(this.consTitle));
					
			//language
			task.put("language", new JSONString(app.getLocalization().getLanguage()));
				
			//settings
			JSONObject settings = new JSONObject();
				settings.put("-toolbar", new JSONString("false"));
				settings.put("-menubar", new JSONString("false"));
				settings.put("-inputbar", new JSONString("false"));
			task.put("settings", settings);
		
			//age
			JSONObject age = new JSONObject();
				age.put("-min", new JSONString("0"));
				age.put("-max", new JSONString("19"));
			task.put("age", age);
			
			//file
			JSONObject file = new JSONObject();
					file.put("-base64", new JSONString(this.base64));
			task.put("file", file);
			
			api.put("task", task);
		request.put("request", api);
		
		return request.toString();
    }
	
	/**
	 * @param app AppW
	 * @param filename title of construction
	 * @param base64 String
	 * @return the upload XML as JSON String
	 */
	public static UploadRequest getRequestElement(AppW app, String filename, String base64) {
		return new UploadRequest(app, filename, base64);
	}


	/**
	 * @param app {@link AppW}
	 * @param mat {@link Material}
	 * @return the upload XML as JSON String
	 */
	public static UploadRequest getRequestElement(AppW app, Material mat) {
	    return new UploadRequest(app, mat);
    }
}
