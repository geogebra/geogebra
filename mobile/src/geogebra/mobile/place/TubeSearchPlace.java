package geogebra.mobile.place;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;

public class TubeSearchPlace extends Place
{
	private String name;
	
	public TubeSearchPlace(String token)
	{
		this.name = token;
	}

	public String getHelloName()
	{
		return this.name;
	}

	public static class Tokenizer implements PlaceTokenizer<TubeSearchPlace>
	{

		@Override
		public String getToken(TubeSearchPlace place)
		{
			return place.getHelloName();
		}

		@Override
		public TubeSearchPlace getPlace(String token)
		{
			return new TubeSearchPlace(token);
		}

	}
	
}
