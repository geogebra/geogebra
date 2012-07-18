package geogebra.mobile;

import geogebra.mobile.gui.GeoGebraMobileFrame;
import geogebra.web.html5.ArticleElement;
import geogebra.web.main.AppW;

public class MobileApp extends AppW
{

	public MobileApp(ArticleElement article, GeoGebraMobileFrame geoGebraMobileFrame)
	{
		super(article, geoGebraMobileFrame);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void appSplashCanNowHide()
	{
		// We have no splash anymore!
	}
}
