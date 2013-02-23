package geogebra.web.main;

import geogebra.common.euclidian.DrawEquationInterface;
import geogebra.common.factories.SwingFactory;
import geogebra.common.kernel.arithmetic.ExpressionNodeConstants.StringType;
import geogebra.common.main.App;
import geogebra.common.sound.SoundManager;
import geogebra.common.util.NormalizerMinimal;
import geogebra.web.sound.SoundManagerW;

import com.google.gwt.canvas.client.Canvas;

public abstract class AppWeb extends App {
	
	private DrawEquationWeb drawEquation;
	private SoundManager soundManager;
	private NormalizerMinimal normalizerMinimal;
	
	@Override
	public final DrawEquationInterface getDrawEquation() {
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
	
	public abstract Canvas getCanvas();
	
	@Override
	public final StringType getFormulaRenderingType() {
		return StringType.LATEX;
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

		
}
