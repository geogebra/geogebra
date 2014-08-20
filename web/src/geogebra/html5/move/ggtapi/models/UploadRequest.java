package geogebra.html5.move.ggtapi.models;

import geogebra.common.move.ggtapi.models.Request;
import geogebra.common.move.ggtapi.models.json.JSONObject;
import geogebra.common.move.ggtapi.models.json.JSONString;
import geogebra.web.main.AppW;


public class UploadRequest implements Request {

	private final String API = "1.0.0";
	private final String GGB = "geogebra";
	private final String TASK = "upload";
	private final String TYPE = "applet";
	private AppW app;
	private String consTitle = "GeoGebra";
	private String uniqueID;
	
	
	/**
	 * @param app AppW
	 * @param consTitle title of construction
	 */
	UploadRequest(AppW app, String consTitle) {
		this.app = app;
		this.consTitle = consTitle;
		this.uniqueID = this.app.getUniqueId();
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
				
//			//description
//			task.put("description", new JSONString("This is just an example"));
				
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
			
//			//tags
//			JSONObject tags = new JSONObject();
//				//tag
//				JSONObject tag = new JSONObject();
//					//name
//					tag.put("-name", new JSONString("example"));
//				tags.put("tag", tag);
//			task.put("tags", tags);
			
			//file
			JSONObject file = new JSONObject();
					file.put("-base64", new JSONString(app.getGgbApi().getBase64(true)));
			task.put("file", file);
			
			api.put("task", task);
		request.put("request", api);
		
		return request.toString();
    }
	
	/**
	 * @param app AppW
	 * @param filename title of construction
	 * @return the upload XML as JSON String
	 */
	public static UploadRequest getRequestElement(AppW app, String filename) {
		return new UploadRequest(app, filename);
	}

}
