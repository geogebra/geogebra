package org.geogebra.web.html5.gui.speechRec;

import org.geogebra.web.html5.css.GuiResourcesSimple;
import org.geogebra.web.html5.gui.FastClickHandler;
import org.geogebra.web.html5.gui.util.StandardButton;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Panel containing the speech recognition button
 * 
 * @author csilla
 *
 */
public class SpeechRecognitionPanel extends FlowPanel {

	private SpeechRecognitionController specRecContr;

	/**
	 * @param app
	 *            see {@link AppW}
	 */
	public SpeechRecognitionPanel(AppW app) {
		specRecContr = new SpeechRecognitionController(app);
		buildGui(app);
	}

	private void buildGui(AppW app) {
		this.setStyleName("speechBtnPanel");
		StandardButton speechBtn = new StandardButton(
				GuiResourcesSimple.INSTANCE.record(), null, 24, app);
		speechBtn.setStyleName("speechBtn");
		speechBtn.addFastClickHandler(new FastClickHandler() {

			public void onClick(Widget source) {
				getSpecRecController().initSpeechRec("command");
			}
		});
		this.add(speechBtn);
	}

	/**
	 * @return the speech recognition controller
	 */
	public SpeechRecognitionController getSpecRecController() {
		return specRecContr;
	}
}
