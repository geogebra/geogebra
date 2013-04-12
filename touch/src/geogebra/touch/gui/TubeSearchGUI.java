package geogebra.touch.gui;

import geogebra.touch.TouchEntryPoint;
import geogebra.touch.gui.elements.StandardImageButton;
import geogebra.touch.gui.elements.ggt.HorizontalMaterialPanel;
import geogebra.touch.gui.elements.ggt.SearchBar;
import geogebra.touch.gui.elements.ggt.SearchBar.SearchListener;
import geogebra.touch.gui.elements.ggt.VerticalMaterialPanel;
import geogebra.web.util.ggtapi.GeoGebraTubeAPI;
import geogebra.web.util.ggtapi.JSONparserGGT;
import geogebra.web.util.ggtapi.Material;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * GeoGebraTube Search and Browse GUI
 * 
 */
public class TubeSearchGUI extends VerticalPanel
{
	private SearchBar searchBar;
	HorizontalMaterialPanel featuredMaterials;
	VerticalMaterialPanel resultsArea;
	private StandardImageButton backButton;

	/**
	 * Sets the viewport and other settings, creates a link element at the end of
	 * the head, appends the css file and initializes the GUI elements.
	 */
	/**
	 * 
	 */
	public TubeSearchGUI()
	{
		this.setPixelSize(Window.getClientWidth(), Window.getClientHeight());

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

		
		this.featuredMaterials = new HorizontalMaterialPanel();
		this.featuredMaterials.setWidth(Window.getClientWidth() + "px");
		this.featuredMaterials.setMaterials(new ArrayList<Material>());

		this.resultsArea = new VerticalMaterialPanel();

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
		this.add(this.featuredMaterials);

		this.resultsArea.setHeight(Window.getClientHeight() - this.searchBar.getOffsetHeight() - this.featuredMaterials.getOffsetHeight()
		    - this.backButton.getOffsetHeight() + "px");
		this.add(this.resultsArea);
		this.add(this.backButton);
		setCellVerticalAlignment(this.backButton, HasVerticalAlignment.ALIGN_BOTTOM);

		loadFeatured();

		Window.addResizeHandler(new ResizeHandler()
		{
			@Override
			public void onResize(ResizeEvent event)
			{
				TubeSearchGUI.this.onResize(event);
			}
		});
	}
	
	public void displaySearchResults(String query){
		GeoGebraTubeAPI.getInstance().search(query, new RequestCallback()
		{
			@Override
			public void onResponseReceived(com.google.gwt.http.client.Request request, Response response)
			{
				List<Material> materialList = JSONparserGGT.parseResponse(response.getText());

				if (materialList != null)
				{
					TubeSearchGUI.this.resultsArea.setMaterials(materialList);
				}
			}

			@Override
			public void onError(com.google.gwt.http.client.Request request, Throwable exception)
			{
				// TODO Handle error!
				exception.printStackTrace();
			}
		});
	}

	protected void onResize(ResizeEvent event)
	{
		this.searchBar.onResize(event);

		this.featuredMaterials.setWidth(Window.getClientWidth() + "px");

		this.resultsArea.setWidth(event.getWidth() + "px");
		int newHeight = Window.getClientHeight() - this.searchBar.getOffsetHeight() - this.featuredMaterials.getOffsetHeight()
			    - this.backButton.getOffsetHeight();
		if(newHeight > 0){
			this.resultsArea.setHeight(newHeight + "px");
		}
	}

	/**
	 * Loads the featured materials
	 */
	private void loadFeatured()
	{
		GeoGebraTubeAPI.getInstance().getFeaturedMaterials(new RequestCallback()
		{
			@Override
			public void onResponseReceived(Request request, Response response)
			{
				List<Material> materialList = JSONparserGGT.parseResponse(response.getText());

				TubeSearchGUI.this.featuredMaterials.setMaterials(materialList);
			}

			@Override
			public void onError(Request request, Throwable exception)
			{
				// TODO Auto-generated method stub
			}
		});
	}
}
