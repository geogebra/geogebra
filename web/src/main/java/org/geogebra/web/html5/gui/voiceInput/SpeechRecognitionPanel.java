package org.geogebra.web.html5.gui.voiceInput;

import org.geogebra.common.gui.AccessibilityGroup;
import org.geogebra.web.full.gui.layout.GUITabs;
import org.geogebra.web.html5.css.GuiResourcesSimple;
import org.geogebra.web.html5.gui.FastClickHandler;
import org.geogebra.web.html5.gui.TabHandler;
import org.geogebra.web.html5.gui.view.button.StandardButton;
import org.geogebra.web.html5.gui.voiceInput.questResErr.QuestResErrConstants;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Panel containing the speech recognition button
 *
 * @author csilla
 *
 */
public class SpeechRecognitionPanel extends FlowPanel implements TabHandler {

	private SpeechRecognitionController specRecContr;
	private VoiceInputOutputController controller;
	private StandardButton speechBtn;
	private int viewID;

	/**
	 * @param app
	 *            see {@link AppW}
	 * @param viewID
	 *            id of view
	 */
	public SpeechRecognitionPanel(AppW app, int viewID) {
		specRecContr = new SpeechRecognitionController(app);
		controller = new VoiceInputOutputController(app);
		this.viewID = viewID;
		buildGui(app);
	}

	private void buildGui(AppW app) {
		this.setStyleName("speechBtnPanel");
		speechBtn = new StandardButton(
				GuiResourcesSimple.INSTANCE.record(), null, 24, app);
		speechBtn.setStyleName("speechBtn");
		speechBtn.setTabIndex(GUITabs.SPEECH_REC + app.getActiveEuclidianView().getViewID());
		speechBtn.addTabHandler(this);
		speechBtn.setTitle(
				"Speech recognition button.");
		speechBtn.setAltText(
				"Speech recognition button. Press enter to give command."
						+ " Press tab to select next object");
		speechBtn.addFastClickHandler(new FastClickHandler() {

			@Override
			public void onClick(Widget source) {
				getController()
						.initSpeechSynth(
						"Please give the command.",
								QuestResErrConstants.COMMAND);
			}
		});
		this.add(speechBtn);
	}

	/**
	 * @return controller
	 */
	public VoiceInputOutputController getController() {
		return controller;
	}

	/**
	 * @return the speech recognition controller
	 */
	public SpeechRecognitionController getSpecRecController() {
		return specRecContr;
	}

	/** Sets focus to speech rec btn */
	public void focusSpeechRec() {
		if (speechBtn != null) {
			speechBtn.getElement().focus();
		}
	}

	@Override
	public boolean onTab(Widget source, boolean shiftDown) {
		if (shiftDown) {
			specRecContr.getAppW().getAccessibilityManager()
					.focusPrevious(AccessibilityGroup.SPEECH, getViewID());
			return true;
		}
		specRecContr.getAppW().getAccessibilityManager()
				.focusNext(AccessibilityGroup.SPEECH, getViewID());
		return true;
	}

	/**
	 * @return view id
	 */
	public int getViewID() {
		return viewID;
	}
}
