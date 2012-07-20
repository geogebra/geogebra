package geogebra.mobile;

import geogebra.common.main.App;
import geogebra.mobile.gui.GeoGebraMobileFrame;
import geogebra.web.euclidian.EuclidianControllerW;
import geogebra.web.html5.ArticleElement;
import geogebra.web.main.AppW;

public class MobileApp extends AppW
{

	public MobileApp(ArticleElement article, GeoGebraMobileFrame geoGebraMobileFrame)
	{
		super(article, geoGebraMobileFrame);

		App.debug("MobileApp initialized!");
	}

	@Override
	public void appSplashCanNowHide()
	{
		// We have no splash anymore!
	}

	public void resizeToParent(int width, int height)
	{
		this.getSettings().getEuclidian(1).setPreferredSize(geogebra.common.factories.AwtFactory.prototype.newDimension(width, height));
		this.getEuclidianView1().setDisableRepaint(false);
		this.getEuclidianView1().synCanvasSize();
		this.getEuclidianView1().repaintView();

		((EuclidianControllerW) this.getActiveEuclidianView().getEuclidianController()).updateOffsets();

	}
}
