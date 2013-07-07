package geogebra.touch.gui;

import geogebra.common.move.ggtapi.models.Material;
import geogebra.html5.main.AppWeb;
import geogebra.html5.util.ggtapi.GeoGebraTubeAPI;
import geogebra.html5.util.ggtapi.JSONparserGGT;
import geogebra.touch.FileManagerM;
import geogebra.touch.TouchEntryPoint;
import geogebra.touch.gui.elements.StandardImageButton;
import geogebra.touch.gui.elements.ggt.SearchBar;
import geogebra.touch.gui.elements.ggt.SearchBar.SearchListener;
import geogebra.touch.gui.elements.ggt.VerticalMaterialPanel;

import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * GeoGebraTube Search and Browse GUI
 * 
 */
public class BrowseGUI extends VerticalPanel
{
	private SearchBar searchBar;
	private FileManagerM fm;
	//HorizontalMaterialPanel featuredMaterials;
	VerticalMaterialPanel resultsArea;
	private StandardImageButton backButton;

	/**
	 * Sets the viewport and other settings, creates a link element at the end of
	 * the head, appends the css file and initializes the GUI elements.
	 */
	/**
	 * 
	 */
	public BrowseGUI(AppWeb app)
	{
		this.setStyleName("BrowseGUI");
		this.fm = new FileManagerM();
		this.searchBar = new SearchBar();
		this.searchBar.setWidth(Window.getClientWidth());
		this.searchBar.addSearchListener(new SearchListener()
		{
			@Override
			public void onSearch(String query)
			{
				displaySearchResults(query);
			}
		});

		//this.featuredMaterials = new HorizontalMaterialPanel();
		//this.featuredMaterials.setMaterials(new ArrayList<Material>());

		this.resultsArea = new VerticalMaterialPanel(app, this.fm);

		this.backButton = new StandardImageButton(CommonResources.INSTANCE.back());
		this.backButton.addDomHandler(new ClickHandler()
		{
			@Override
			public void onClick(ClickEvent event)
			{
				TouchEntryPoint.showTabletGUI();
			}
		}, ClickEvent.getType());

		this.add(this.searchBar);
		//this.add(this.featuredMaterials);

		this.resultsArea.setHeight((Window.getClientHeight() - 120) + "px");
		this.add(this.resultsArea);
		this.add(this.backButton);

		loadAllFiles();

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
		List<Material> materialList = fm.search(query);
		BrowseGUI.this.resultsArea.setMaterials(materialList);
	}

	protected void onResize(ResizeEvent event)
	{
		this.searchBar.onResize(event);

		//this.featuredMaterials.setWidth(Window.getClientWidth() + "px");

		this.resultsArea.setWidth(event.getWidth() + "px");
		int newHeight = Window.getClientHeight() - 120;
		if (newHeight > 0)
		{
			this.resultsArea.setHeight(newHeight + "px");
		}
	}

	/**
	 * Loads the featured materials
	 */
	public void loadAllFiles()
	{
		List<Material> materialList = fm.getAllFiles();
		BrowseGUI.this.resultsArea.setMaterials(materialList);
	}
}
