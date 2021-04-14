package org.geogebra.web.full.move.googledrive.operations;

import org.geogebra.common.GeoGebraConstants;
import org.geogebra.common.util.StringUtil;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.full.gui.dialog.DialogManagerW;
import org.geogebra.web.full.move.googledrive.api.GoogleApi;
import org.geogebra.web.full.move.googledrive.api.GoogleAuthorization;
import org.geogebra.web.full.move.googledrive.api.GoogleDriveDocument;
import org.geogebra.web.full.move.googledrive.api.GooglePicker;
import org.geogebra.web.full.move.googledrive.api.GooglePickerBuilder;
import org.geogebra.web.full.move.googledrive.api.GoogleUploadRequest;
import org.geogebra.web.full.move.googledrive.api.GoogleViewId;
import org.geogebra.web.full.util.SaveCallback;
import org.geogebra.web.full.util.SaveCallback.SaveState;
import org.geogebra.web.html5.Browser;
import org.geogebra.web.html5.euclidian.EuclidianViewWInterface;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.move.googledrive.GoogleDriveOperation;
import org.geogebra.web.html5.util.JsRunnable;
import org.geogebra.web.html5.util.StringConsumer;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.ScriptElement;
import com.google.gwt.user.client.Window.Location;

import elemental2.core.ArrayBuffer;
import elemental2.core.Global;
import elemental2.core.JsArray;
import elemental2.core.JsObject;
import elemental2.dom.FileReader;
import elemental2.dom.XMLHttpRequest;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;

/**
 * Operational class for Google Drive Api
 */
public class GoogleDriveOperationW implements GoogleDriveOperation {

	private static final String GoogleApiJavaScriptSrc = "https://apis.google.com/js/client.js?onload=GGW_loadGoogleDrive";
	private final AppW app;
	private boolean loggedIn;
	private JsPropertyMap<Object> googleDriveURL;
	private String authToken;
	private boolean needsPicker;

	private String driveBase64FileName = null;
	private Runnable waitingHandler;
	private boolean inited = false;
	private String currentFileId = null;

	/**
	 * creates new google drive operation instance
	 * 
	 * @param app
	 *            Application
	 */
	public GoogleDriveOperationW(AppW app) {
		this.app = app;
	}

	/**
	 * @return filename
	 */
	public String getFileName() {
		return driveBase64FileName;
	}

	/**
	 * Go for the google drive url, and fetch the script
	 */
	@Override
	public void initGoogleDriveApi() {
		if (!inited) {
			GoogleApi.setOnloadCallback(this::loadGoogleDrive);
			fetchScript();
			inited = true;
		}
	}

	private static void fetchScript() {
		ScriptElement script = Document.get().createScriptElement();
		script.setSrc(GoogleApiJavaScriptSrc);
		Document.get().getBody().appendChild(script);
	}

	private void loadGoogleDrive() {
		if (GoogleApi.get() != null) {
			GoogleApi.get().load("auth",
					JsPropertyMap.of("callback", (JsRunnable) () -> {}));
			GoogleApi.get().load("picker",
					JsPropertyMap.of("callback", (JsRunnable) () -> Log.debug("picker loaded")));

			if (GoogleApi.get().getClient() != null) {
				GoogleApi.get().getClient()
						.load("drive", "v3", this::checkIfOpenedFromGoogleDrive);
			}
		}
	}

	/**
	 * logs in the user to Google
	 * 
	 * @param immediate
	 *            wheter to force login popup open
	 */
	public void login(boolean immediate) {
		JsPropertyMap<Object> config = JsPropertyMap.of(
			"client_id", app.getLAF().getClientId(),
			"scope", GeoGebraConstants.DRIVE_SCOPE + " "
						+ GeoGebraConstants.USERINFO_EMAIL_SCOPE + " "
						+ GeoGebraConstants.USERINFO_PROFILE_SCOPE + " "
						+ GeoGebraConstants.PLUS_ME_SCOPE,
			"immediate", immediate
		);

		GoogleApi.get().getAuthorization().authorize(config, this::authorizeCallback);
	}

	private void authorizeCallback(GoogleAuthorization.Response response) {
		if (response.error != null) {
			Log.error("Error loading from GoogleDrive: "
					+ response.error + " " + response.details);
			this.loggedIn = false;
			if ("open".equals(getAction())) {
				login(false);
			}
		} else {
			this.loggedIn = true;
			this.authToken = response.access_token;
			if (this.needsPicker) {
				this.needsPicker = false;
				createPicker(authToken);
			} else if (this.waitingHandler != null) {
				waitingHandler.run();
			}

			checkIfFileMustbeOpenedFromGoogleDrive();
		}
	}

	private void createPicker(String token) {
		GooglePicker picker = new GooglePickerBuilder()
				.addView(GoogleViewId.DOCS)
				.addView(GoogleViewId.FOLDERS)
				.setOAuthToken(token)
				.setCallback((data) -> {
					if (!"picked".equals(data.action) || data.docs.length < 1) {
						return;
					}

					GoogleDriveDocument file = data.docs.getAt(0);
					loadFromGoogleFile(file.name, file.id);
				}).build();
		picker.setVisible(true);
	}

	private void checkIfFileMustbeOpenedFromGoogleDrive() {
		if ("open".equals(getAction())) {
			app.resetPerspectiveParam();
			app.getAppletParameters().setAttribute("appName", "auto");
			openFileFromGoogleDrive(googleDriveURL);
		}
	}

	private void openFileFromGoogleDrive(JsPropertyMap<Object> descriptors) {
		String id = (String) descriptors.nestedGet("ids.0");

		if (id != null) {
			loadFromGoogleFile(null, id);
		}
	}

	/**
	 * @return if the user is logged into google
	 */
	public boolean isLoggedIntoGoogle() {
		return loggedIn;
	}

	/**
	 * @param name
	 *            name of the file
	 * @param id
	 *            id of the file
	 */
	public void loadFromGoogleFile(String name, String id) {
		String accessToken = GoogleApi.get().getAuthorization().getToken().access_token;
		XMLHttpRequest xhr = new XMLHttpRequest();
		xhr.open("GET", "https://www.googleapis.com/drive/v3/files/" + id + "?alt=media");
		xhr.responseType = "blob";
		xhr.setRequestHeader("Authorization", "Bearer " + accessToken);

		xhr.onload = (e) -> {
			JsObject content = xhr.response.asJsObject();
			FileReader reader = new FileReader();
			reader.onloadend = (e2) -> {
				if (e2.target.result.asString().startsWith("UEsDBBQ")) {
					processGoogleDriveFileContentAsBase64(e2.target.result.asString(),
							name, id);
				} else {
					processGoogleDriveFileContentAsBinary(Js.uncheckedCast(content),
							name, id);
				}
				return null;
			};
			reader.readAsText(Js.uncheckedCast(content));
		};

		xhr.send();
	}

	private void processGoogleDriveFileContentAsBase64(String base64,
			final String title, String id) {
		// true = reload the whole doc
		app.loadGgbFileAsBase64Again(base64, true);
		postprocessFileLoading(title, id);
	}

	private void postprocessFileLoading(String title, String id) {
		refreshCurrentFileDescriptors(title);
		setCurrentFileId(id);
		app.setUnsaved();
	}

	private void processGoogleDriveFileContentAsBinary(ArrayBuffer binary,
	        String title, String id) {
		app.loadGgbFileAsBinaryAgain(binary);
		postprocessFileLoading(title, id);
	}

	@Override
	public void refreshCurrentFileDescriptors(String fName) {
		if (app.getAppletParameters().getDataParamFitToScreen()
				&& !StringUtil.empty(fName)) {
			Browser.changeMetaTitle(fName.replace(".ggb", ""));
		}

		driveBase64FileName = fName;
	}

	/**
	 * @param fileName
	 *            name of the File
	 * @param description
	 *            Description of the file
	 * @param isggb
	 *            whether this is GGB
	 * @return javascript function to called back;
	 */
	public StringConsumer getPutFileCallback(String fileName, String description, boolean isggb) {
		return (base64) -> saveFileToGoogleDrive(fileName, description, base64, isggb);
	}

	private void saveFileToGoogleDrive(final String fileName,
			final String description, final String fileContent,
			boolean isggb) {
		if (!fileName.equals(getFileName())) {
			setCurrentFileId(null);
		}

		JsPropertyMap<Object> metaData = JsPropertyMap.of(
				"title", fileName,
				"description", description
		);

		if ((getFolderId() != null) && !"".equals(getFolderId())) {
			metaData.set("parents", new JsArray<>(JsPropertyMap.of("id", getFolderId())));
		}

		metaData.set("thumbnail", JsPropertyMap.of(
				"image", getThumbnail(),
				"mimeType", "image/png"
		));

		handleFileUploadToGoogleDrive(getCurrentFileId(), metaData, fileContent, isggb);
	}

	private String getThumbnail() {
		return ((EuclidianViewWInterface) app.getActiveEuclidianView())
				.getCanvasBase64WithTypeString()
				.substring(StringUtil.pngMarker.length()).replace("+", "-")
				.replace("/", "_");
	}

	private void showUploadError() {
		((DialogManagerW) app.getDialogManager()).getSaveDialog(false, true).hide();
		((DialogManagerW) app.getDialogManager()).showAlertDialog(app
				.getLocalization().getMenu("GoogleDriveSaveProblem"));
	}

	private void handleFileUploadToGoogleDrive(String fileId,
			JsPropertyMap<Object> fileMetadata, String fileData, boolean isggb) {
		String boundary = "-------314159265358979323846";
		String delimiter = "\r\n--" + boundary + "\r\n";
		String close_delim = "\r\n--" + boundary + "--";
		String contentType = GeoGebraConstants.GGW_MIME_TYPE;

		String multipartRequestBody = delimiter
				+ "Content-Type: application/json\r\n\r\n"
				+ Global.JSON.stringify(fileMetadata) + delimiter
				+ "Content-Type: " + contentType + "\r\n"
				+ "Content-Transfer-Encoding: base64\r\n" + "\r\n"
				+ fileData + close_delim;

		JsPropertyMap<Object> requestBody = JsPropertyMap.of();
		requestBody.set("path", "/upload/drive/v2/files/" + (fileId == null ? "" : fileId));
		requestBody.set("method", fileId != null ? "PUT" : "POST");
		requestBody.set("params", JsPropertyMap.of(
				"uploadType", "multipart",
				"alt", "json"
		));
		requestBody.set("headers", JsPropertyMap.of(
				"Content-Type",
				"multipart/mixed; boundary=\"" + boundary + "\"")
		);
		requestBody.set("body", multipartRequestBody);

		GoogleUploadRequest request = GoogleApi.get().getClient().request(requestBody);

		request.execute((resp) -> {
				if (resp.error == null) {
					updateAfterGoogleDriveSave(resp.id, resp.title, isggb);
				} else {
					Log.error("Error saving to Google Drive: " + resp.error);
					showUploadError();
				}
		});
	}

	private void updateAfterGoogleDriveSave(String id, String fileName, boolean isggb) {
		app.getSaveController().runAfterSaveCallback(true);
		((DialogManagerW) app.getDialogManager()).getSaveDialog(false, true).hide();
		SaveCallback.onSaved(app, SaveState.OK, !isggb);
		if (isggb) {
			refreshCurrentFileDescriptors(fileName);
			setCurrentFileId(id);
		}
	}

	private void checkIfOpenedFromGoogleDrive() {
		String state = Location.getParameter("state");
		if (state != null && !"".equals(state)) {
			googleDriveURL = Js.uncheckedCast(Global.JSON.parse(state));
			if (!this.loggedIn) {
				login(true);
			}
		}
	}

	private String getFolderId() {
		String folderId = null;
		if (googleDriveURL != null) {
			folderId = (String) googleDriveURL.get("folderId");
		}
		return folderId;
	}

	private String getAction() {
		String action = null;
		if (googleDriveURL != null) {
			action = (String) googleDriveURL.get("action");
		}
		return action;
	}

	@Override
	public void requestPicker() {
		if (this.authToken != null) {
			createPicker(this.authToken);
		} else {
			this.needsPicker = true;
			login(false);
		}
	}

	@Override
	public void resetStorageInfo() {
		driveBase64FileName = null;
		currentFileId = null;
	}

	/**
	 * @return current file ID
	 */
	public String getCurrentFileId() {
		return currentFileId;
	}

	/**
	 * @param currentFileId
	 *            current file ID
	 */
	public void setCurrentFileId(String currentFileId) {
		this.currentFileId = currentFileId;
	}

	@Override
	public void afterLogin(Runnable todo) {
		if (this.isLoggedIntoGoogle()) {
			todo.run();
		} else {
			this.waitingHandler = todo;
			login(false);
		}
	}
}
