package geogebra.touch.gui;

import geogebra.common.main.App;
import geogebra.common.move.ggtapi.models.Material;
import geogebra.html5.main.AppWeb;
import geogebra.html5.util.ggtapi.GeoGebraTubeAPI;
import geogebra.html5.util.ggtapi.JSONparserGGT;
import geogebra.touch.FileManagerM;
import geogebra.touch.TouchApp;
import geogebra.touch.TouchEntryPoint;
import geogebra.touch.gui.elements.ggt.FileContainer;
import geogebra.touch.gui.elements.ggt.MaterialListElement;
import geogebra.touch.gui.elements.ggt.SearchBar;
import geogebra.touch.gui.elements.ggt.SearchBar.SearchListener;
import geogebra.touch.gui.elements.ggt.VerticalMaterialPanel;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * GeoGebraTube Search and Browse GUI
 * 
 */
public class BrowseGUI extends VerticalPanel {
  private final SearchBar searchBar;
  private final FileManagerM fm;
  private final AppWeb app;

  // HorizontalMaterialPanel featuredMaterials;
  VerticalMaterialPanel localFilePanel, tubeFilePanel;
  VerticalPanel localFileContainer, tubeFileContainer;

  private List<Material> localList = new ArrayList<Material>();
  List<Material> tubeList = new ArrayList<Material>();

  private final Label headingMyProfile;
  private final Label headingGeoGebraTube;

  private final static int HEADING_HEIGHT = 50;
  public final static int CONTROLS_HEIGHT = 50;

  /**
   * Sets the viewport and other settings, creates a link element at the end of
   * the head, appends the css file and initializes the GUI elements.
   */
  /**
	 * 
	 */
  public BrowseGUI(AppWeb app) {
    this.setStyleName("tubesearchgui");
    this.fm = ((TouchApp) app).getFileManager();
    this.app = app;
    this.searchBar = new SearchBar(this.app.getLocalization(), this);
    this.searchBar.addSearchListener(new SearchListener() {
      @Override
      public void onSearch(String query) {
	BrowseGUI.this.displaySearchResults(query);
      }
    });

    // this.featuredMaterials = new HorizontalMaterialPanel();
    // this.featuredMaterials.setMaterials(new ArrayList<Material>());

    this.localFilePanel = new VerticalMaterialPanel(this.app);
    this.tubeFilePanel = new VerticalMaterialPanel(this.app);
    this.localFilePanel.setStyleName("filePanel");
    this.tubeFilePanel.setStyleName("filePanel");

    this.headingMyProfile = new Label();
    this.headingGeoGebraTube = new Label();
    this.headingMyProfile.setStyleName("filePanelTitle");
    this.headingGeoGebraTube.setStyleName("filePanelTitle");

    this.headingGeoGebraTube.setText("GeoGebraTube");

    this.localFileContainer = new FileContainer("localFilePanel", this.headingMyProfile, this.localFilePanel);

    this.tubeFileContainer = new FileContainer("tubeFilePanel", this.headingGeoGebraTube, this.tubeFilePanel);

    this.add(this.searchBar);
    // this.add(this.featuredMaterials);
    final int panelHeight = Window.getClientHeight() - TouchEntryPoint.getLookAndFeel().getAppBarHeight() - HEADING_HEIGHT - CONTROLS_HEIGHT;
    if (panelHeight > 0) {
      this.localFilePanel.setHeight(panelHeight + "px");
      this.tubeFilePanel.setHeight(panelHeight + "px");
    }
    final HorizontalPanel fileList = new HorizontalPanel();
    fileList.add(this.localFileContainer);
    fileList.add(this.tubeFileContainer);
    this.add(fileList);

    this.loadFeatured();

    Window.addResizeHandler(new ResizeHandler() {
      @Override
      public void onResize(ResizeEvent event) {
	BrowseGUI.this.onResize();
      }
    });
  }

  public void displaySearchResults(String query) {
    this.localList = this.fm.search(query);
    GeoGebraTubeAPI.getInstance(geogebra.common.move.ggtapi.models.GeoGebraTubeAPI.url).search(query, new RequestCallback() {
      @Override
      public void onError(com.google.gwt.http.client.Request request, Throwable exception) {
	// TODO Handle error!
	BrowseGUI.this.updateGUI();
	exception.printStackTrace();
      }

      @Override
      public void onResponseReceived(com.google.gwt.http.client.Request request, Response response) {
	App.debug(response.getText());
	BrowseGUI.this.tubeList = JSONparserGGT.parseResponse(response.getText());
	BrowseGUI.this.updateGUI();
      }
    });

  }

  public MaterialListElement getChosenMaterial() {
    return this.tubeFilePanel.getChosenMaterial();
  }

  public void loadFeatured() {
    this.localList = this.fm.getAllFiles();
    GeoGebraTubeAPI.getInstance(geogebra.common.move.ggtapi.models.GeoGebraTubeAPI.url).getFeaturedMaterials(new RequestCallback() {
      @Override
      public void onError(Request request, Throwable exception) {
	BrowseGUI.this.updateGUI();
      }

      @Override
      public void onResponseReceived(Request request, Response response) {
	BrowseGUI.this.tubeList = JSONparserGGT.parseResponse(response.getText());
	BrowseGUI.this.updateGUI();
      }
    });
  }

  public void onResize() {
    this.searchBar.onResize();

    // this.featuredMaterials.setWidth(Window.getClientWidth() + "px");

    this.localFilePanel.updateWidth();
    this.tubeFilePanel.updateWidth();
    final int newHeight = Window.getClientHeight() - TouchEntryPoint.getLookAndFeel().getAppBarHeight() - HEADING_HEIGHT - CONTROLS_HEIGHT;
    if (newHeight > 0) {
      this.localFilePanel.setHeight(newHeight + "px");
      this.tubeFilePanel.setHeight(newHeight + "px");
      this.localFilePanel.updateHeight();
      this.tubeFilePanel.updateHeight();
    }
  }

  /**
   * Loads the featured materials
   */
  public void reloadLocalFiles() {
    this.localList = this.fm.getAllFiles();
    this.updateGUI();
  }

  public void setLabels() {
    this.searchBar.setLabels();
    this.tubeFilePanel.setLabels();
    this.localFilePanel.setLabels();
    this.headingMyProfile.setText(this.app.getMenu("MyProfile"));
  }

  protected void updateGUI() {
    if (this.tubeList.isEmpty()) {
      this.localFilePanel.setMaterials(2, this.localList);
      this.tubeFileContainer.setVisible(false);
      this.localFileContainer.setVisible(true);
    } else if (this.localList.isEmpty()) {
      this.tubeFilePanel.setMaterials(2, this.tubeList);
      this.localFileContainer.setVisible(false);
      this.tubeFileContainer.setVisible(true);
    } else {
      this.localFilePanel.setMaterials(1, this.localList);
      this.tubeFilePanel.setMaterials(1, this.tubeList);
      this.tubeFileContainer.setVisible(true);
      this.localFileContainer.setVisible(true);
    }
  }
}
