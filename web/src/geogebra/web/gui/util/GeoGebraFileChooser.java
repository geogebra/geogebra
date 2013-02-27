package geogebra.web.gui.util;

import geogebra.common.main.App;
import geogebra.web.helper.MyGoogleApis;
import geogebra.web.main.AppW;
import geogebra.web.main.GgbAPI;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class GeoGebraFileChooser extends PopupPanel {
	
	App app;
	VerticalPanel p;
	TextBox fileName;
	TextArea description;
	Button save;
	Button cancel;
	Anchor download;
	Button uploadToGGT;
	GeoGebraFileChooser _this = this;
	private int type;

	public GeoGebraFileChooser(final App app) {
	    super();
	    this.app = app;
	    add(p = new VerticalPanel());
	    
	    HorizontalPanel fileNamePanel = new HorizontalPanel();
	    fileNamePanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
	    fileNamePanel.add(new Label("Filename: "));
	    fileNamePanel.add(fileName = new TextBox());
	    fileNamePanel.addStyleName("fileNamePanel");
	    p.add(fileNamePanel);
	    
	    HorizontalPanel descriptionPanel = new HorizontalPanel();
	    descriptionPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
	    descriptionPanel.add(new Label("Description: "));
	    descriptionPanel.add(description = new TextArea());
	    descriptionPanel.addStyleName("descriptionPanel");
	    p.add(descriptionPanel);

		download = new Anchor();
		download.setText(app.getMenu("DownloadAsGgbFile"));	
		download.setStyleName("gwt-Button");
		download.addStyleName("linkDownload");
		download.getElement().setAttribute(
				"style", "text-decoration: none; color: black");
	    
		
		
	    HorizontalPanel buttonPanel = new HorizontalPanel();
	    buttonPanel.addStyleName("buttonPanel");
	    buttonPanel.add(cancel = new Button(app.getMenu("Cancel")));
	    buttonPanel.add(save = new Button(app.getMenu("SaveToGoogleDrive")));
	    buttonPanel.add(download);
	    buttonPanel.add(uploadToGGT = new Button(app.getMenu("UploadGeoGebraTube")));
	    p.add(buttonPanel);
	    addStyleName("GeoGebraFileChooser");
	    
	    cancel.addClickHandler(new ClickHandler() {
			
			public void onClick(ClickEvent event) {
				app.setDefaultCursor();
				hide();
			}
		});
	    
	    save.addClickHandler(new ClickHandler() {
			
			public void onClick(ClickEvent event) {
				if (fileName.getText() != "") {
					save.setEnabled(false);
					cancel.setEnabled(false);
					fileName.setEnabled(false);
					description.setEnabled(false);
					download.setEnabled(false);
					uploadToGGT.setEnabled(false);
					String saveName = fileName.getText();
					if (saveName.substring(-4) != ".ggb") saveName += ".ggb"; //It's not necessary if fileName.onChange() was running before.
					JavaScriptObject callback = MyGoogleApis.getPutFileCallback(saveName, description.getText(), _this);
					((geogebra.web.main.GgbAPI)app.getGgbApi()).getBase64(callback);
					//MyGoogleApis.putNewFileToGoogleDrive(fileName.getText(),description.getText(),FileMenu.temp_base64_BUNNY,_this);
				}
			}
				
		});
	    save.setEnabled(MyGoogleApis.signedInToGoogle());
	    
	    download.addClickHandler(new ClickHandler() {			
			public void onClick(ClickEvent event) {
				hide();
			}
		});
	    
	    uploadToGGT.addClickHandler(new ClickHandler() {			
			public void onClick(ClickEvent event) {
				app.uploadToGeoGebraTube();
				hide();
			}
		});
		
	    
	    addCloseHandler(new CloseHandler<PopupPanel>() {
			
			public void onClose(CloseEvent<PopupPanel> event) {
				app.setDefaultCursor();
				save.setEnabled(MyGoogleApis.signedInToGoogle());
				cancel.setEnabled(true);
				fileName.setEnabled(true);
				description.setEnabled(true);
				download.setEnabled(true);
				uploadToGGT.setEnabled(true);
			}
		});
	    center();
	    
	    //ggb file creating, and if ready, enabling of download-button.
	    setFilename("geogebra.ggb");
	    fileName.addChangeHandler(new ChangeHandler(){

			public void onChange(ChangeEvent event) {
				String newName = fileName.getText();
				if(newName.substring(-4) != ".ggb"){
					newName += ".ggb";
					fileName.setText(newName);
				}
				setFilename(newName);
            }
	    	
	    });
	    
    }
	
	
	@Override
    public void show(){
		// It creates new ggb file all time for download, all time when the
		// dialog opens.
		((GgbAPI) app.getGgbApi()).getGGB(true, this.download.getElement());
		save.setEnabled(MyGoogleApis.signedInToGoogle());
	    super.show();
	}
	
	public void setFilename(String newVal){
		if (newVal.equals("")) newVal = "geogebra.ggb";
        download.getElement().setAttribute("download", newVal);
	}

	public void saveSuccess(String fName, String desc, String fileCont) {
	    ((AppW) app).refreshCurrentFileDescriptors(fName,desc,fileCont);
    }
	
	public void setFileName(String fName) {
		fileName.setText(fName);
	}
	
	public void setDescription(String ds) {
		description.setText(ds);
	}
	
	

}
