package geogebra.touch.gui;

import geogebra.common.main.App;
import geogebra.common.move.ggtapi.models.Material;
import geogebra.html5.main.AppWeb;
import geogebra.html5.util.ggtapi.GeoGebraTubeAPI;
import geogebra.html5.util.ggtapi.JSONparserGGT;
import geogebra.touch.FileManagerM;
import geogebra.touch.TouchEntryPoint;
import geogebra.touch.gui.elements.StandardImageButton;
import geogebra.touch.gui.elements.ggt.MaterialListElement;
import geogebra.touch.gui.elements.ggt.SearchBar;
import geogebra.touch.gui.elements.ggt.SearchBar.SearchListener;
import geogebra.touch.gui.elements.ggt.VerticalMaterialPanel;
import geogebra.touch.gui.laf.DefaultResources;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * GeoGebraTube Search and Browse GUI
 * 
 */
public class BrowseGUI extends VerticalPanel
{
	private SearchBar searchBar;
	private FileManagerM fm;
	private AppWeb app;

	// HorizontalMaterialPanel featuredMaterials;
	VerticalMaterialPanel localFilePanel, tubeFilePanel;
	VerticalPanel localFileContainer, tubeFileContainer;

	private List<Material> localList = new ArrayList<Material>();
	List<Material> tubeList = new ArrayList<Material>();
	
	private Label headingMyProfile;
	private Label headingGeoGebraTube;
	
	private FlowPanel localFileControlPanel;
	private FlowPanel tubeFileControlPanel;
	
	private HorizontalPanel localFilePages;
	private HorizontalPanel tubeFilePages;
	
	private static DefaultResources LafIcons = TouchEntryPoint.getLookAndFeel().getIcons();
	private StandardImageButton prevLocalButton = new StandardImageButton(LafIcons.arrow_go_previous());
	private StandardImageButton nextLocalButton = new StandardImageButton(LafIcons.arrow_go_next());
	private StandardImageButton prevTubeButton = new StandardImageButton(LafIcons.arrow_go_previous());
	private StandardImageButton nextTubeButton = new StandardImageButton(LafIcons.arrow_go_next());
	
	private final static int HEADING_HEIGHT = 50;
	private final static int CONTROLS_HEIGHT = 50;

	/**
	 * Sets the viewport and other settings, creates a link element at the end of
	 * the head, appends the css file and initializes the GUI elements.
	 */
	/**
	 * 
	 */
	public BrowseGUI(AppWeb app, FileManagerM fm)
	{
		this.setStyleName("tubesearchgui");
		this.fm = fm;
		this.app = app;
		this.searchBar = new SearchBar(this.app.getLocalization(), this);
		this.searchBar.setWidth(Window.getClientWidth());
		this.searchBar.addSearchListener(new SearchListener()
		{
			@Override
			public void onSearch(String query)
			{
				displaySearchResults(query);
			}
		});

		// this.featuredMaterials = new HorizontalMaterialPanel();
		// this.featuredMaterials.setMaterials(new ArrayList<Material>());

		this.localFilePanel = new VerticalMaterialPanel(this.app, this.fm);
		this.tubeFilePanel = new VerticalMaterialPanel(this.app, this.fm);
		this.localFilePanel.setStyleName("filePanel");
		this.tubeFilePanel.setStyleName("filePanel");

		this.headingMyProfile = new Label();
		this.headingGeoGebraTube = new Label();
		this.headingMyProfile.setStyleName("filePanelTitle");
		this.headingGeoGebraTube.setStyleName("filePanelTitle");
		
		this.headingGeoGebraTube.setText("GeoGebraTube");
		
		this.localFileContainer = new VerticalPanel();
		this.localFileContainer.setStyleName("localFilePanel");
		this.localFileContainer.add(this.headingMyProfile);
		this.localFileContainer.add(this.localFilePanel);
		
		this.tubeFileContainer = new VerticalPanel();
		this.tubeFileContainer.setStyleName("tubeFilePanel");
		this.tubeFileContainer.add(this.headingGeoGebraTube);
		this.tubeFileContainer.add(this.tubeFilePanel);
		
		// Panel for page controls local files
		this.localFileControlPanel = new FlowPanel();
		this.localFileControlPanel.setStyleName("fileControlPanel");
		
		this.prevLocalButton.addStyleName("prevButton");
		this.localFileControlPanel.add(this.prevLocalButton);
		
		this.localFilePages = new HorizontalPanel();
		this.localFilePages.setStyleName("filePageControls");
		//TODO: add number buttons here
		
		this.localFileControlPanel.add(this.localFilePages);
		this.nextLocalButton.addStyleName("nextButton");
		this.localFileControlPanel.add(this.nextLocalButton);
		this.localFileContainer.add(this.localFileControlPanel);
		
		// Panel for page controls tube files
		this.tubeFileControlPanel = new FlowPanel();
		this.tubeFileControlPanel.setStyleName("fileControlPanel");
		
		this.prevTubeButton.addStyleName("prevButton");
		this.tubeFileControlPanel.add(this.prevTubeButton);
		
		this.tubeFilePages = new HorizontalPanel();
		this.tubeFilePages.setStyleName("filePageControls");
		//TODO: add number buttons here
		
		this.tubeFileControlPanel.add(this.tubeFilePages);
		this.nextTubeButton.addStyleName("nextButton");
		this.tubeFileControlPanel.add(this.nextTubeButton);
		this.tubeFileContainer.add(this.tubeFileControlPanel);

		
		this.add(this.searchBar);
		// this.add(this.featuredMaterials);

		this.localFilePanel.setHeight((Window.getClientHeight() - TouchEntryPoint.getLookAndFeel().getAppBarHeight()) - HEADING_HEIGHT - CONTROLS_HEIGHT + "px");
		this.tubeFilePanel.setHeight((Window.getClientHeight() - TouchEntryPoint.getLookAndFeel().getAppBarHeight()) - HEADING_HEIGHT - CONTROLS_HEIGHT + "px");
		HorizontalPanel fileList = new HorizontalPanel();
		fileList.add(this.localFileContainer);
		fileList.add(this.tubeFileContainer);
		this.add(fileList);

		loadFeatured();

		Window.addResizeHandler(new ResizeHandler()
		{
			@Override
			public void onResize(ResizeEvent event)
			{
				BrowseGUI.this.onResize(event);
			}
		});
	}

	public void displaySearchResults(String query)
	{
		this.localList = this.fm.search(query);
		(GeoGebraTubeAPI.getInstance(geogebra.common.move.ggtapi.models.GeoGebraTubeAPI.url)).search(query, new RequestCallback()
		{
			@Override
			public void onResponseReceived(com.google.gwt.http.client.Request request, Response response)
			{
				App.debug(response.getText());
				BrowseGUI.this.tubeList = JSONparserGGT.parseResponse(response.getText());
				updateGUI();
			}

			@Override
			public void onError(com.google.gwt.http.client.Request request, Throwable exception)
			{
				// TODO Handle error!
				updateGUI();
				exception.printStackTrace();
			}
		});

	}

	protected void updateGUI()
	{
		if (this.tubeList.isEmpty())
		{
			this.localFilePanel.setMaterials(2, this.localList);
			this.tubeFileContainer.setVisible(false);
			this.localFileContainer.setVisible(true);
		}
		else if (this.localList.isEmpty())
		{
			this.tubeFilePanel.setMaterials(2, this.tubeList);
			this.localFileContainer.setVisible(false);
			this.tubeFileContainer.setVisible(true);
		}
		else
		{
			this.localFilePanel.setMaterials(1, this.localList);
			this.tubeFilePanel.setMaterials(1, this.tubeList);
			this.tubeFileContainer.setVisible(true);
			this.localFileContainer.setVisible(true);
		}
	}

	protected void onResize(ResizeEvent event)
	{
		this.searchBar.onResize(event);

		// this.featuredMaterials.setWidth(Window.getClientWidth() + "px");

		this.localFilePanel.updateWidth();
		this.tubeFilePanel.updateWidth();
		int newHeight = Window.getClientHeight() - TouchEntryPoint.getLookAndFeel().getAppBarHeight() - HEADING_HEIGHT - CONTROLS_HEIGHT ;
		if (newHeight > 0)
		{
			this.localFilePanel.setHeight(newHeight + "px");
			this.tubeFilePanel.setHeight(newHeight + "px");
		}
	}

	/**
	 * Loads the featured materials
	 */
	public void reloadLocalFiles()
	{
		this.localList = this.fm.getAllFiles();
		updateGUI();
	}

	public void loadFeatured()
	{
		this.localList = this.fm.getAllFiles();
		(GeoGebraTubeAPI.getInstance(geogebra.common.move.ggtapi.models.GeoGebraTubeAPI.url)).getFeaturedMaterials(new RequestCallback()
		{
			@Override
			public void onResponseReceived(Request request, Response response)
			{
				BrowseGUI.this.tubeList = JSONparserGGT.parseResponse(response.getText());
				updateGUI();
			}

			@Override
			public void onError(Request request, Throwable exception)
			{
				updateGUI();
			}
		});
	}
	
	public MaterialListElement getChosenMaterial()
	{
		return this.tubeFilePanel.getChosenMaterial();
	}
	
	public void setLabels()
	{
		this.searchBar.setLabels();
		this.tubeFilePanel.setLabels();
		this.localFilePanel.setLabels();
		this.headingMyProfile.setText(this.app.getMenu("MyProfile"));
	}
}
