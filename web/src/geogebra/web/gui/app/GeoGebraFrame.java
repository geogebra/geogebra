package geogebra.web.gui.app;

import geogebra.common.GeoGebraConstants;
import geogebra.web.gui.SplashDialog;
import geogebra.web.helper.UrlFetcherImpl;
import geogebra.web.helper.XhrFactory;
import geogebra.web.html5.ArticleElement;
import geogebra.web.html5.View;
import geogebra.web.main.Application;
import geogebra.web.presenter.LoadFilePresenter;

import java.util.ArrayList;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * The main frame containing every view / menu bar / ....
 */
public class GeoGebraFrame extends VerticalPanel {

    private static ArrayList<GeoGebraFrame> instances = new ArrayList<GeoGebraFrame>();
    private static GeoGebraFrame activeInstance;

    /** Loads file into active GeoGebraFrame */
    public static LoadFilePresenter fileLoader = new LoadFilePresenter(
            new UrlFetcherImpl(XhrFactory.getSupportedXhr(),
                    GeoGebraConstants.URL_PARAM_GGB_FILE,
                    GeoGebraConstants.URL_PARAM_PROXY,
                    GeoGebraConstants.PROXY_SERVING_LOCATION));

    /** The application */
    protected Application app;

    /** Creates new GeoGebraFrame */
    public GeoGebraFrame() {
        super();
        instances.add(this);
        activeInstance = this;
    }

    /**
     * Main entry points called by geogebra.web.Web.startGeoGebra()
     * @param geoGebraMobileTags
     *            list of &lt;article&gt; elements of the web page
     */
    public static void main(ArrayList<ArticleElement> geoGebraMobileTags) {
        init(geoGebraMobileTags);
    }


    private static void init(ArrayList<ArticleElement> geoGebraMobileTags) {

        for (ArticleElement articleElement : geoGebraMobileTags) {
            GeoGebraFrame inst = new GeoGebraFrame();
            Application app = inst.createApplication(articleElement
                    .getDataParamGui());
            inst.app = app;
            inst.createSplash(articleElement);
            inst.add(app.buildApplicationPanel());
            RootPanel.get(articleElement.getId()).add(inst);
            handleLoadFile(articleElement, app);

        }
    }


	private void createSplash(ArticleElement article) {
		this.app.splash = new SplashDialog();
		int splashWidth = 427;
		int splashHeight = 120;
		int width = article.getDataParamWidth();
	    int height = article.getDataParamHeight();
	    if (width > 0 && height > 0) {
	    	setWidth(width+"px");
	    	setHeight(height+"px");
	    	this.app.splash.addStyleName("splash");
	    	this.app.splash.getElement().getStyle().setTop((height/2)-(splashHeight/2), Unit.PX);
	    	this.app.splash.getElement().getStyle().setLeft((width/2)-(splashWidth/2), Unit.PX);
	    	
	    }
	    addStyleName("jsloaded");
	    add(this.app.splash);
    }

    private static void handleLoadFile(ArticleElement articleElement,
            Application app) {
        View view = new View(articleElement, app);
        fileLoader.setView(view);
        fileLoader.onPageLoad();
    }

    /**
     * @return the application
     */
    public Application getApplication() {
        return app;
    }

    /**
     * Sets the Application of the GeoGebraFrame
     * @param app
     *            the application
     */
    public void setApplication(Application app) {
        this.app = app;
    }

    /**
     * @param useFullGui
     *            if false only one euclidianView will be available (without
     *            menus / ...)
     * @return the newly created instance of Application
     */
    protected Application createApplication(boolean useFullGui) {
        return new Application(useFullGui);
    }

    /**
     * @return list of instances of GeogebraFrame
     */
    public static ArrayList<GeoGebraFrame> getInstances() {
        return instances;
    }

}
