package org.geogebra.web.web.move.googledrive.operations;

import org.geogebra.common.main.App;
import org.geogebra.common.move.events.BaseEvent;
import org.geogebra.common.move.ggtapi.events.LogOutEvent;
import org.geogebra.common.move.ggtapi.events.LoginEvent;
import org.geogebra.common.move.operations.BaseOperation;
import org.geogebra.common.move.views.BaseEventView;
import org.geogebra.common.move.views.EventRenderable;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.euclidian.EuclidianViewWInterface;
import org.geogebra.web.html5.main.AppW;
import org.geogebra.web.html5.move.googledrive.GoogleDriveOperation;
import org.geogebra.web.html5.util.DynamicScriptElement;
import org.geogebra.web.html5.util.JSON;
import org.geogebra.web.html5.util.URL;
import org.geogebra.web.web.gui.dialog.DialogManagerW;
import org.geogebra.web.web.move.googledrive.events.GoogleDriveLoadedEvent;
import org.geogebra.web.web.move.googledrive.events.GoogleLogOutEvent;
import org.geogebra.web.web.move.googledrive.events.GoogleLoginEvent;
import org.geogebra.web.web.move.googledrive.models.GoogleDriveModelW;
import org.geogebra.web.web.util.SaveCallback;
import org.geogebra.web.web.util.SaveCallback.SaveState;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.Document;

/**
 * @author gabor
 *
 *         Operational class for Google Drive Api
 */
public class GoogleDriveOperationW extends BaseOperation<EventRenderable>
        implements EventRenderable, GoogleDriveOperation {

	private static final String GoogleApiJavaScriptSrc = "https://apis.google.com/js/client.js?onload=GGW_loadGoogleDrive";
	private boolean isDriveLoaded;
	private AppW app;
	private boolean loggedIn;
	private JavaScriptObject googleDriveURL;
	private String authToken;
	private boolean needsPicker;

	private String driveBase64description = null;
	private String driveBase64FileName = null;
	private Runnable waitingHandler;

	/**
	 * creates new google drive operation instance
	 * 
	 * @param app
	 *            Application
	 */
	public GoogleDriveOperationW(AppW app) {

		this.app = app;
		setCurrentFileId();
		setView(new BaseEventView());
		setModel(new GoogleDriveModelW());

		app.getLoginOperation().getView().add(this);
		getView().add(this);

	}

	public String getFileName() {
		return driveBase64FileName;
	}

	public String getFileDescription() {
		return driveBase64description;
	}

	@Override
	public GoogleDriveModelW getModel() {
		return (GoogleDriveModelW) super.getModel();
	}

	/**
	 * @return the logged in user name
	 */
	public String getUserName() {
		return getModel().getUserName();
	}

	private boolean inited = false;
	/**
	 * Go for the google drive url, and fetch the script
	 */
	public void initGoogleDriveApi() {
		if (!inited) {
			createGoogleApiCallbackFunction();
			fetchScript();
			inited = true;
		}
	}

	private static void fetchScript() {
		DynamicScriptElement script = (DynamicScriptElement) Document.get()
		        .createScriptElement();
		script.setSrc(GoogleApiJavaScriptSrc);
		Document.get().getBody().appendChild(script);
	}

	private native void createGoogleApiCallbackFunction() /*-{
		var _this = this;
		$wnd.GGW_loadGoogleDrive = function() {
			_this.@org.geogebra.web.web.move.googledrive.operations.GoogleDriveOperationW::loadGoogleDrive()();

		}
	}-*/;

	private native void loadGoogleDrive() /*-{
		var _this = this;
		if ($wnd.gapi) {
			$wnd.gapi.load('auth', {
				'callback' : function() {

				}
			});
			$wnd.gapi
					.load(
							'picker',
							{
								'callback' : function() {
									@org.geogebra.common.main.App::debug(Ljava/lang/String;)("picker loaded");
								}
							});

			if ($wnd.gapi.client) {
				$wnd.gapi.client
						.load(
								'drive',
								'v2',
								function() {
									_this.@org.geogebra.web.web.move.googledrive.operations.GoogleDriveOperationW::googleDriveLoaded()();
								});
			}
		}
	}-*/;

	private void googleDriveLoaded() {
		this.isDriveLoaded = true;
		onEvent(new GoogleDriveLoadedEvent());
	}

	/**
	 * @return if google drive loaded or not
	 */
	public boolean isDriveLoaded() {
		return isDriveLoaded;
	}

	/**
	 * logs in the user to Google
	 * 
	 * @param immediate
	 *            wheter to force login popup open
	 */
	public native void login(boolean immediate) /*-{
		var _this = this, config = {
			'client_id' : @org.geogebra.common.GeoGebraConstants::GOOGLE_CLIENT_ID,
			'scope' : @org.geogebra.common.GeoGebraConstants::DRIVE_SCOPE
					+ " "
					+ @org.geogebra.common.GeoGebraConstants::USERINFO_EMAIL_SCOPE
					+ " "
					+ @org.geogebra.common.GeoGebraConstants::USERINFO_PROFILE_SCOPE
					+ " "
					+ @org.geogebra.common.GeoGebraConstants::PLUS_ME_SCOPE,
			'immediate' : immediate
		};
		//config.max_auth_age = 0;
		$wnd.gapi.auth
				.authorize(
						config,
						function(resp) {
							var token = resp ? resp.access_token : {};
							var error = resp ? resp.error : "";
							_this.@org.geogebra.web.web.move.googledrive.operations.GoogleDriveOperationW::authorizeCallback(Ljava/lang/String;Ljava/lang/String;)(token,error);
						}

				);
	}-*/;

	private void authorizeCallback(String token, String error) {
		if (error != null && error.length() > 0) {
			App.debug("GOOGLE LOGIN" + error);
			this.loggedIn = false;
			onEvent(new GoogleLoginEvent(false));
		} else {
			this.loggedIn = true;
			this.authToken = token;
			if (this.needsPicker) {
				this.needsPicker = false;
				createPicker(authToken);
			} else if (this.waitingHandler != null) {
				waitingHandler.run();
			}
			onEvent(new GoogleLoginEvent(true));

		}
	}

	private native void createPicker(String token2) /*-{
	                                                var _this = this;
	                                                var picker = new $wnd.google.picker.PickerBuilder().
	                                                addView($wnd.google.picker.​ViewId.DOCS).
	                                                addView($wnd.google.picker.ViewId.FOLDERS).
	                                                setOAuthToken(token2).
	                                                setDeveloperKey("AIzaSyBZlOTdZmzNrXZy2QIrDEz8uXJ9lOUFGE0").
	                                                setCallback(function(data){
	                                                if(data.action != "picked" || data.docs.length <1){
	                                                return;
	                                                }
	                                                var request = $wnd.gapi.client.drive.files.get({
	                                                fileId : data.docs[0].id
	                                                });
	                                                request.execute(function(resp) {
	                                                _this.@org.geogebra.web.web.move.googledrive.operations.GoogleDriveOperationW::loadFromGoogleFile(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)(resp.downloadUrl, resp.description, resp.title, resp.id);
	                                                });
	                                                }).
	                                                build();
	                                                picker.setVisible(true);
	                                                }-*/;

	public void renderEvent(BaseEvent event) {
		App.debug("event: " + event.toString());
		if (event instanceof GoogleDriveLoadedEvent) {
			checkIfOpenedFromGoogleDrive();
			return;
		}
		if (event instanceof GoogleLoginEvent) {
			if (((GoogleLoginEvent) event).isSuccessFull()) {
				checkIfFileMustbeOpenedFromGoogleDrive();
			} else {
				if ("open".equals(getAction())) {
					login(false);
				} else if (getModel().lastLoggedInFromGoogleDrive()) {
					login(false);
				}
			}
			return;
		}
		if (event instanceof LoginEvent) {
			if (((LoginEvent) event).isSuccessful()) {
				if (app.getLoginOperation().getModel().getLoggedInUser()
				        .hasGoogleDrive()) {
					// do nothing, see requestDriveLogin
				} else {
					GoogleDriveOperationW.this.getModel()
					        .setLoggedInFromGoogleDrive(false);
				}
			} else {
				logOut();
			}
			return;
		}
		if (event instanceof LogOutEvent) {
			logOut();
			return;
		}

	}

	public void requestDriveLogin() {
		if (this.isDriveLoaded) {
			this.login(true);
			this.getModel().setLoggedInFromGoogleDrive(true);
		} else {
			getView().add(new EventRenderable() {

				public void renderEvent(BaseEvent loadevent) {
					if (loadevent instanceof GoogleDriveLoadedEvent) {
						login(true);
						GoogleDriveOperationW.this.getModel()
						        .setLoggedInFromGoogleDrive(true);
					}

				}
			});
		}
	}

	public native void initFileNameItems(GoogleDriveFileHandler fh) /*-{
	                                                                var fileChooser = this;
	                                                                fh.@org.geogebra.web.web.move.googledrive.operations.GoogleDriveFileHandler::clearMaterials()();
	                                                                function retrieveAllFiles(callback) {
	                                                                var retrievePageOfFiles = function(request, result) {
	                                                                request.execute(function(resp) {
	                                                                result = result.concat(resp.items);
	                                                                var nextPageToken = resp.nextPageToken;
	                                                                if (nextPageToken) {
	                                                                request = $wnd.gapi.client.drive.files.list({
	                                                                'pageToken': nextPageToken
	                                                                });
	                                                                retrievePageOfFiles(request, result);
	                                                                } else {
	                                                                callback(result);
	                                                                }
	                                                                });
	                                                                }
	                                                                var initialRequest = $wnd.gapi.client.drive.files.list();
	                                                                retrievePageOfFiles(initialRequest, []);
	                                                                }
	                                                                retrieveAllFiles(function(resp) {
	                                                                resp.forEach(function(value, index, array) {
	                                                                if (value.mimeType === "application/vnd.geogebra.file" ||
	                                                                value.fileExtension === "ggb" ||
	                                                                (value.title.lastIndexOf(".ggb") > -1)) {
	                                                                fh.@org.geogebra.web.web.move.googledrive.operations.GoogleDriveFileHandler::show(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)
	                                                                (value.title,value.lastModifyingUserName,new Date(value.modifiedDate).getTime()+"", value.downloadUrl, value.description, value.id, value.thumbnailLink);
	                                                                }
	                                                                
	                                                                });
	                                                                fh.@org.geogebra.web.web.move.googledrive.operations.GoogleDriveFileHandler::done()();
	                                                                });
	                                                                }-*/;

	private void checkIfFileMustbeOpenedFromGoogleDrive() {
		if ("open".equals(getAction())) {
			openFileFromGoogleDrive(googleDriveURL);
		}
	}

	private native void openFileFromGoogleDrive(JavaScriptObject descriptors) /*-{
		var id = descriptors["ids"] ? descriptors["ids"][0] : undefined, _this = this;
		request;
		if (id !== undefined) {
			request = $wnd.gapi.client.drive.files.get({
				fileId : id
			});
			request
					.execute(function(resp) {
						_this.@org.geogebra.web.web.move.googledrive.operations.GoogleDriveOperationW::loadFromGoogleFile(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)(resp.downloadUrl, resp.description, resp.title, resp.id);
					});
		}
	}-*/;

	/**
	 * @return if the user is logged into google
	 */
	public boolean isLoggedIntoGoogle() {
		return loggedIn;
	}

	/**
	 * @param currentFileName
	 *            name of the file
	 * @param description
	 *            description of the file
	 * @param title
	 *            title
	 * @param id
	 *            id of the file
	 */
	public native void loadFromGoogleFile(String currentFileName,
	        String description, String title, String id) /*-{
		var _this = this;

		function downloadFile(downloadUrl, callback) {
			if (downloadUrl) {
				var accessToken = $wnd.gapi.auth.getToken().access_token;
				var xhr = new $wnd.XMLHttpRequest();
				xhr.open('GET', downloadUrl);
				xhr.responseType = "blob";
				xhr.setRequestHeader('Authorization', 'Bearer ' + accessToken);
				xhr.onload = function() {
					callback(xhr.response);
				};
				xhr.onerror = function() {
					callback(null);
				};
				xhr.send();
			} else {
				callback(null);
			}
		}

		downloadFile(
				currentFileName,
				function(content) {
					var reader = new FileReader();
					reader
							.addEventListener(
									"loadend",
									function(e) {
										if (e.target.result.indexOf("UEsDBBQ") === 0) {
											_this.@org.geogebra.web.web.move.googledrive.operations.GoogleDriveOperationW::processGoogleDriveFileContentAsBase64(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)(e.target.result, description, title, id);
										} else {
											_this.@org.geogebra.web.web.move.googledrive.operations.GoogleDriveOperationW::processGoogleDriveFileContentAsBinary(Lcom/google/gwt/core/client/JavaScriptObject;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)(content, description, title, id);
										}
									});
					reader.readAsText(content);
				});
	}-*/;

	private void processGoogleDriveFileContentAsBase64(String base64,
	        String description, String title, String id) {
		app.loadGgbFileAsBase64Again(base64);
		postprocessFileLoading(description, title, id);
	}

	private void postprocessFileLoading(String description, String title,
	        String id) {
		refreshCurrentFileDescriptors(title, description);
		setCurrentFileId(id);
		app.setUnsaved();
	}

	private void processGoogleDriveFileContentAsBinary(JavaScriptObject binary,
	        String description, String title, String id) {
		app.loadGgbFileAsBinaryAgain(binary);
		postprocessFileLoading(description, title, id);
	}

	public void refreshCurrentFileDescriptors(String fName, String desc) {
		if (desc.equals("null") || desc.equals("undefined")) {
			driveBase64description = "";
		} else {
			driveBase64description = desc;
		}
		driveBase64FileName = fName;
		((DialogManagerW) app.getDialogManager())
		        .refreshAndShowCurrentFileDescriptors(driveBase64FileName,
		                driveBase64description);

	}

	/**
	 * logs out from Google Drive (this means, removes the possibilities to
	 * interact with Google Drive)
	 */
	public void logOut() {
		this.onEvent(new GoogleLogOutEvent());
		this.getModel().setLoggedInFromGoogleDrive(false);
	}

	/**
	 * @param fileName
	 *            name of the File
	 * @param description
	 *            Description of the file
	 * @return javascript function to called back;
	 */
	public native JavaScriptObject getPutFileCallback(String fileName,
	        String description) /*-{
		var _this = this;
		return function(base64) {
			var fName = fileName, ds = description;
			_this.@org.geogebra.web.web.move.googledrive.operations.GoogleDriveOperationW::saveFileToGoogleDrive(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)(fName,ds,base64);
		};
	}-*/;

	private void saveFileToGoogleDrive(final String fileName,
	        final String description, final String fileContent) {
		JavaScriptObject metaData = JavaScriptObject.createObject();
		JSON.put(metaData, "title", fileName);
		JSON.put(metaData, "description", description);
		if (!fileName.equals(getFileName())) {
			setCurrentFileId(null);
		}
		if ((getFolderId() != null) && !"".equals(getFolderId())) {
			JavaScriptObject folderId = JavaScriptObject.createObject();
			JSON.put(folderId, "id", getFolderId());
			JsArray<JavaScriptObject> parents = (JsArray<JavaScriptObject>) JavaScriptObject
			        .createArray();
			parents.push(folderId);
			JSON.put(metaData, "parents", parents);
		}
		JavaScriptObject thumbnail = JavaScriptObject.createObject();
		JSON.put(
		        thumbnail,
		        "image",
		        ((EuclidianViewWInterface) app.getActiveEuclidianView())
		                .getCanvasBase64WithTypeString()
		                .substring("data:image/png;base64,".length())
		                .replace("+", "-").replace("/", "_"));
		JSON.put(thumbnail, "mimeType", "image/png");
		JSON.putObject(metaData, "thumbnail", thumbnail);
		Log.debug(metaData);
		handleFileUploadToGoogleDrive(getCurrentFileId(), metaData, fileContent);
	}

	private native void handleFileUploadToGoogleDrive(String id,
	        JavaScriptObject metaData, String base64) /*-{
		var _this = this, fId = id ? id : "";
		function updateFile(fileId, fileMetadata, fileData) {
			var boundary = '-------314159265358979323846';
			var delimiter = "\r\n--" + boundary + "\r\n";
			var close_delim = "\r\n--" + boundary + "--";
			var contentType = @org.geogebra.common.GeoGebraConstants::GGW_MIME_TYPE;
			var base64Data = fileData;
			var multipartRequestBody = delimiter
					+ 'Content-Type: application/json\r\n\r\n'
					+ JSON.stringify(fileMetadata) + delimiter
					+ 'Content-Type: ' + contentType + '\r\n'
					+ 'Content-Transfer-Encoding: base64\r\n' + '\r\n'
					+ base64Data + close_delim;
			var method = (fileId ? 'PUT' : 'POST');
			var request = $wnd.gapi.client.request({
				'path' : '/upload/drive/v2/files/' + fileId,
				'method' : method,
				'params' : {
					'uploadType' : 'multipart',
					'alt' : 'json'
				},
				'headers' : {
					'Content-Type' : 'multipart/mixed; boundary="' + boundary
							+ '"'
				},
				'body' : multipartRequestBody
			});

			request
					.execute(function(resp) {
						if (!resp.error) {
							_this.@org.geogebra.web.web.move.googledrive.operations.GoogleDriveOperationW::updateAfterGoogleDriveSave(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)(resp.id, resp.title, resp.description)
						} else {
							@org.geogebra.common.main.App::debug(Ljava/lang/String;)("Error saving to Google Drive: " + resp.error);
							_this.@org.geogebra.web.web.move.googledrive.operations.GoogleDriveOperationW::showUploadError()();
						}
					});
		}
		updateFile(fId, metaData, base64);
	}-*/;

	private void showUploadError() {
		((DialogManagerW) app.getDialogManager()).getSaveDialog().hide();
		((DialogManagerW) app.getDialogManager()).showAlertDialog(app
		        .getLocalization().getMenu("GoogleDriveSaveProblem"));
	}

	private void updateAfterGoogleDriveSave(String id, String fileName,
	        String description) {
		((DialogManagerW) app.getDialogManager()).getSaveDialog()
		        .runAfterSaveCallback();
		((DialogManagerW) app.getDialogManager()).getSaveDialog().hide();
		SaveCallback.onSaved(app, SaveState.OK);
		refreshCurrentFileDescriptors(fileName, description);
		setCurrentFileId(id);
	}

	private void checkIfOpenedFromGoogleDrive() {
		String state = URL.getQueryParameterAsString("state");
		App.debug(state);
		if (state != null && !"".equals(state)) {
			googleDriveURL = JSON.parse(state);
			Log.debug(googleDriveURL);
			if (!this.loggedIn) {
				login(true);
			}

		}
	}

	private String getFolderId() {
		String folderId = null;
		if (googleDriveURL != null) {
			folderId = JSON.get(googleDriveURL, "folderId");
		}
		return folderId;
	}

	private String getAction() {
		String action = null;
		if (googleDriveURL != null) {
			action = JSON.get(googleDriveURL, "action");
		}
		return action;
	}

	public void requestPicker() {
		if (this.authToken != null) {
			createPicker(this.authToken);
		} else {
			this.needsPicker = true;
			login(false);
		}

	}

	public void resetStorageInfo() {
		driveBase64FileName = null;
		driveBase64description = null;
		currentFileId = null;
		((DialogManagerW) app.getDialogManager())
		        .refreshAndShowCurrentFileDescriptors(driveBase64FileName,
		                driveBase64description);
	}

	private String currentFileId = null;

	public String getCurrentFileId() {
		return currentFileId;
	}

	public void setCurrentFileId(String currentFileId) {
		this.currentFileId = currentFileId;
	}

	protected native void setCurrentFileId() /*-{
		if ($wnd.GGW_appengine) {
			this.@org.geogebra.web.web.move.googledrive.operations.GoogleDriveOperationW::currentFileId = $wnd.GGW_appengine.FILE_IDS[0];
		}
	}-*/;

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
