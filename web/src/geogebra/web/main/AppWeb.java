package geogebra.web.main;

import geogebra.common.euclidian.DrawEquation;
import geogebra.common.factories.SwingFactory;
import geogebra.common.kernel.Construction;
import geogebra.common.kernel.arithmetic.ExpressionNodeConstants.StringType;
import geogebra.common.main.App;
import geogebra.common.main.CasType;
import geogebra.common.plugin.ScriptManager;
import geogebra.common.sound.SoundManager;
import geogebra.common.util.NormalizerMinimal;
import geogebra.web.euclidian.EuclidianViewWeb;
import geogebra.web.io.MyXMLioW;
import geogebra.web.sound.SoundManagerW;

import com.google.gwt.canvas.client.Canvas;

public abstract class AppWeb extends App {
	
	public static final String DEFAULT_APPLET_ID = "ggbApplet";
	private DrawEquationWeb drawEquation;
	private SoundManager soundManager;
	private NormalizerMinimal normalizerMinimal;
	private GgbAPI ggbapi;
	
	@Override
	public final DrawEquation getDrawEquation() {
		if (drawEquation == null) {
			drawEquation = new DrawEquationWeb();
		}

		return drawEquation;
	}
	
	@Override
	public final SoundManager getSoundManager() {
		if (soundManager == null) {
			soundManager = new SoundManagerW(this);
		}
		return soundManager;
	}
	
	@Override
	public geogebra.web.main.GgbAPI getGgbApi() {
		if (ggbapi == null) {
			ggbapi = new geogebra.web.main.GgbAPI(this);
		}
		return ggbapi;
	}
	
	public abstract Canvas getCanvas();
	
	@Override
	public final StringType getPreferredFormulaRenderingType() {
		return StringType.MATHML;
	}
	
	@Override
	public final NormalizerMinimal getNormalizer() {
		if (normalizerMinimal == null) {
			normalizerMinimal = new NormalizerMinimal();
		}

		return normalizerMinimal;
	}
	
	@Override
    public final SwingFactory getSwingFactory() {
	    return SwingFactory.getPrototype();
    }
	
	protected static void initFactories()
	{
		geogebra.common.factories.FormatFactory.prototype = new geogebra.web.factories.FormatFactoryW();
		geogebra.common.factories.AwtFactory.prototype = new geogebra.web.factories.AwtFactoryW();
		geogebra.common.euclidian.EuclidianStatic.prototype = new geogebra.web.euclidian.EuclidianStaticW();
		geogebra.common.factories.SwingFactory.setPrototype(new geogebra.web.factories.SwingFactoryW());
		geogebra.common.util.StringUtil.prototype = new geogebra.common.util.StringUtil();
		geogebra.common.euclidian.clipping.DoubleArrayFactory.prototype = new geogebra.common.euclidian.clipping.DoubleArrayFactoryImpl();

	}
	
	private GlobalKeyDispatcherW globalKeyDispatcher;

	@Override
	final public GlobalKeyDispatcherW getGlobalKeyDispatcher() {
		if (globalKeyDispatcher == null) {
			globalKeyDispatcher = newGlobalKeyDispatcher();
		}
		return globalKeyDispatcher;
	}

	protected GlobalKeyDispatcherW newGlobalKeyDispatcher() {
		return new GlobalKeyDispatcherW(this);
	}
	
	@Override
	public EuclidianViewWeb getEuclidianView1() {
		return (EuclidianViewWeb) euclidianView;
	}
	private TimerSystemW timers;
	public TimerSystemW getTimerSystem() {
		if (timers == null) {
			timers = new TimerSystemW(this);
		}
		return timers;
	}

	public abstract void showMessage(String error);
	
	public abstract ViewManager getViewManager();

	public void syncAppletPanelSize(int width, int height, int evNo) {
	    // TODO Auto-generated method stub
	    
    }
	@Override
	public ScriptManager getScriptManager() {
		if (scriptManager == null) {
			scriptManager = new ScriptManagerW(this);
		}
		return scriptManager;
	}

	@Override
	public CasType getCASType() {
		return CasType.GIAC;
	}
	
	// ================================================
		// NATIVE JS
		// ================================================

		

		public native void evalScriptNative(String script) /*-{
			$wnd.eval(script);
		}-*/;

		public native void callNativeJavaScript(String funcname) /*-{
			if ($wnd[funcname]) {
				$wnd[funcname]();
			}
		}-*/;

		public native void callNativeJavaScript(String funcname, String arg) /*-{
			if ($wnd[funcname]) {
				$wnd[funcname](arg);
			}
		}-*/;

		public static native void ggbOnInit() /*-{
			if (typeof $wnd.ggbOnInit === 'function')
				$wnd.ggbOnInit();
		}-*/;

		public static native void ggbOnInit(String arg) /*-{
			if (typeof $wnd.ggbOnInit === 'function')
				$wnd.ggbOnInit(arg);
		}-*/;
		
		@Override
		public void callAppletJavaScript(String fun, Object[] args) {
			if (args == null || args.length == 0) {
				callNativeJavaScript(fun);
			} else if (args.length == 1) {
				App.debug("calling function: " + fun + "(" + args[0].toString()
				        + ")");
				callNativeJavaScript(fun, args[0].toString());
			} else {
				debug("callAppletJavaScript() not supported for more than 1 argument");
			}

		}

		public String getDataParamId() {
	        return DEFAULT_APPLET_ID;
        }
		
		private MyXMLioW xmlio;

		@Override
		public boolean loadXML(String xml) throws Exception {
			getXMLio().processXMLString(xml, true, false);
			return true;
		}

		@Override
		public MyXMLioW getXMLio() {
			if (xmlio == null) {
				xmlio = createXMLio(kernel.getConstruction());
			}
			return xmlio;
		}

		@Override
		public MyXMLioW createXMLio(Construction cons) {
			return new MyXMLioW(cons.getKernel(), cons);
		}
		
}
