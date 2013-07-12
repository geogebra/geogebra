package geogebra.touch.gui;

import geogebra.common.move.ggtapi.models.Material;
import geogebra.html5.main.AppWeb;
import geogebra.html5.util.ggtapi.GeoGebraTubeAPI;
import geogebra.html5.util.ggtapi.JSONparserGGT;
import geogebra.touch.FileManagerM;
import geogebra.touch.TouchEntryPoint;
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
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * GeoGebraTube Search and Browse GUI
 * 
 */
public class BrowseGUI extends VerticalPanel
{
	private SearchBar searchBar;
	private FileManagerM fm;
	// HorizontalMaterialPanel featuredMaterials;
	VerticalMaterialPanel localFilePanel, tubeFilePanel;

	private List<Material> localList = new ArrayList<Material>();
	List<Material> tubeList = new ArrayList<Material>();

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
		this.searchBar = new SearchBar(app.getLocalization());
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

		this.localFilePanel = new VerticalMaterialPanel(app, this.fm);
		this.localFilePanel.setStyleName("localFilePanel");
		this.tubeFilePanel = new VerticalMaterialPanel(app, this.fm);
		this.tubeFilePanel.getElement().getStyle().setBackgroundColor("rgb(200,200,200)");

		

		this.add(this.searchBar);
		// this.add(this.featuredMaterials);

		this.localFilePanel.setHeight((Window.getClientHeight() - TouchEntryPoint.getLookAndFeel().getAppBarHeight()) + "px");
		this.tubeFilePanel.setHeight((Window.getClientHeight() - TouchEntryPoint.getLookAndFeel().getAppBarHeight()) + "px");
		FlowPanel fileList = new FlowPanel();
		fileList.add(this.localFilePanel);
		fileList.add(this.tubeFilePanel);
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
			this.tubeFilePanel.setVisible(false);
			this.localFilePanel.setVisible(true);
		}
		else if (this.localList.isEmpty())
		{
			this.tubeFilePanel.setMaterials(2, this.tubeList);
			this.localFilePanel.setVisible(false);
			this.tubeFilePanel.setVisible(true);
		}
		else
		{
			this.localFilePanel.setMaterials(1, this.localList);
			this.tubeFilePanel.setMaterials(1, this.tubeList);
			this.tubeFilePanel.setVisible(true);
			this.localFilePanel.setVisible(true);
		}
	}

	protected void onResize(ResizeEvent event)
	{
		this.searchBar.onResize(event);

		// this.featuredMaterials.setWidth(Window.getClientWidth() + "px");

		this.localFilePanel.updateWidth();
		this.tubeFilePanel.updateWidth();
		int newHeight = Window.getClientHeight() - TouchEntryPoint.getLookAndFeel().getAppBarHeight();
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

	private void loadFeatured()
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
}
