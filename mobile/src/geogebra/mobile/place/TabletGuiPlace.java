package geogebra.mobile.place;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;

//public class HelloPlace extends ActivityPlace<HelloActivity>
public class TabletGuiPlace extends Place
{
	private String name;
	
	public TabletGuiPlace(String token)
	{
		this.name = token;
	}

	public String getHelloName()
	{
		return this.name;
	}

	public static class Tokenizer implements PlaceTokenizer<TabletGuiPlace>
	{

		@Override
		public String getToken(TabletGuiPlace place)
		{
			return place.getHelloName();
		}

		@Override
		public TabletGuiPlace getPlace(String token)
		{
			return new TabletGuiPlace(token);
		}

	}
	
}
