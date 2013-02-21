package geogebra.web.main;

import geogebra.common.euclidian.DrawEquationInterface;
import geogebra.common.main.App;
import geogebra.common.sound.SoundManager;
import geogebra.web.sound.SoundManagerW;

import com.google.gwt.canvas.client.Canvas;

public abstract class AppWeb extends App {
	
	private DrawEquationWeb drawEquation;
	private SoundManager soundManager;
	
	@Override
	public final DrawEquationInterface getDrawEquation() {
		if (drawEquation == null) {
			drawEquation = new DrawEquationWeb(this);
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
		
}
