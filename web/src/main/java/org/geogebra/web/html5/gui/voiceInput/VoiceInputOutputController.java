package org.geogebra.web.html5.gui.voiceInput;

import java.util.ArrayList;

import org.geogebra.common.util.ExternalAccess;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.gui.tooltip.ToolTipManagerW;
import org.geogebra.web.html5.gui.voiceInput.questResErr.QuestResErrConstants;
import org.geogebra.web.html5.gui.voiceInput.questResErr.QuestResErrInterface;
import org.geogebra.web.html5.main.AppW;

/**
 * @author Csilla
 *
 */
public class VoiceInputOutputController {

	private AppW appW;
	private VoiceInputDispatcher dispatcher;
	@ExternalAccess
	private String gotResult = "false";
	@ExternalAccess
	private int action = -1;
	private ArrayList<Double> results = new ArrayList<>();

	/**
	 * @param appW
	 *            see {@link AppW}
	 */
	public VoiceInputOutputController(AppW appW) {
		this.appW = appW;
		dispatcher = new VoiceInputDispatcher(this);
	}
	
	public AppW getAppW() {
		return appW;
	}

	/**
	 * @param isResult
	 *            false if speech recognition stopped unexpectedly
	 */
	public void setGotResult(String isResult) {
		this.gotResult = isResult;
	}

	/**
	 * @param action
	 *            id of the action
	 */
	public void setAction(int action) {
		this.action = action;
	}

	/**
	 * @param toSay
	 *            what to say
	 * @param outputType
	 *            type of the waited answer
	 */
	public native void initSpeechSynth(String toSay,
			int outputType) /*-{
		var synth = window.speechSynthesis;
		var that = this;
		if (!('webkitSpeechRecognition' in window)) {
			toSay = "Speech recognition not supported in this browser, please use chrome.";
			outputType = -1;
		}
		var action = outputType;

		if (synth.speaking) {
			console.error('speechSynthesis.speaking');
			return;
		}

		var utterThis = new SpeechSynthesisUtterance(toSay);
		utterThis.onend = function(event) {
			console.log('SpeechSynthesisUtterance.onend');
			if (action == 500 || action == -1) {
				return;
			}
			that.@org.geogebra.web.html5.gui.voiceInput.VoiceInputOutputController::initSpeechRec(I)(action);
			that.@org.geogebra.web.html5.gui.voiceInput.VoiceInputOutputController::playBeep()();
		}
		utterThis.onerror = function(event) {
			console.error('SpeechSynthesisUtterance.onerror');
		}
		utterThis.pitch = 1;
		utterThis.rate = 1;
		utterThis.lang = 'en-US';
		that.@org.geogebra.web.html5.gui.voiceInput.VoiceInputOutputController::showMessage(Ljava/lang/String;)(toSay);
		synth.speak(utterThis);
	}-*/;

	/**
	 * play a beep if user can start speaking
	 */
	public native void playBeep() /*-{
		var snd = new Audio();
		snd.src = "https://soundbible.com/mp3/Electronic_Chime-KevanGC-495939803.mp3";
		snd.play();
	}-*/;

	/**
	 * init speech recognition
	 * 
	 * @param actionID
	 *            action
	 */
	public native void initSpeechRec(int actionID) /*-{
		var that = this;
		var SpeechRecognition = SpeechRecognition || webkitSpeechRecognition
		var SpeechGrammarList = SpeechGrammarList || webkitSpeechGrammarList
		var SpeechRecognitionEvent = SpeechRecognitionEvent
				|| webkitSpeechRecognitionEvent

		var recognition = new SpeechRecognition();
		var speechRecognitionList = new SpeechGrammarList();
		speechRecognitionList.addFromString("create", 1);
		speechRecognitionList.addFromString("circle", 1);
		recognition.grammars = speechRecognitionList;
		recognition.lang = "en-US";
		recognition.interimResults = false;
		recognition.maxAlternatives = 1;
		that.@org.geogebra.web.html5.gui.voiceInput.VoiceInputOutputController::setAction(I)(actionID);

		recognition.start();
		console.log('SPEECH REC: Ready to receive a command.');

		recognition.onresult = function(event) {
			console.log('SPEECH REC: im in on result');
			that.@org.geogebra.web.html5.gui.voiceInput.VoiceInputOutputController::setGotResult(Ljava/lang/String;)("true");
			var actionType = that.@org.geogebra.web.html5.gui.voiceInput.VoiceInputOutputController::action;
			var last = event.results.length - 1;
			var result = event.results[last][0].transcript;

			console.log('SPEECH REC: Result received: ' + result + '.');
			console.log('SPEECH REC: Confidence: '
					+ event.results[0][0].confidence);

			that.@org.geogebra.web.html5.gui.voiceInput.VoiceInputOutputController::onResponse(ILjava/lang/String;)(actionType,result);
		}

		recognition.onspeechend = function() {
			that.@org.geogebra.web.html5.gui.voiceInput.VoiceInputOutputController::setGotResult(Ljava/lang/String;)("false");
			recognition.stop();
			console.log('SPEECH REC: Recognition stopped.');
			setTimeout(
					function() {
						var gotResult = that.@org.geogebra.web.html5.gui.voiceInput.VoiceInputOutputController::gotResult;
						var actionType = that.@org.geogebra.web.html5.gui.voiceInput.VoiceInputOutputController::action;
						if (gotResult === "false") {
							that.@org.geogebra.web.html5.gui.voiceInput.VoiceInputOutputController::onResponse(ILjava/lang/String;)(actionType,"");
						}
					}, 3000);

		}

		recognition.onnomatch = function(event) {
			console.log("SPEECH REC: I didn't recognise the text.");
		}

		recognition.onerror = function(event) {
			console.log('SPEECH REC: Error occurred in recognition: '
					+ event.error);
			var actionType = that.@org.geogebra.web.html5.gui.voiceInput.VoiceInputOutputController::action;
			that.@org.geogebra.web.html5.gui.voiceInput.VoiceInputOutputController::onResponse(ILjava/lang/String;)(actionType,"");
		}
	}-*/;

	/**
	 * @param actionType
	 *            action
	 * @param result
	 *            from speech recognition
	 */
	public void onResponse(int actionType, String result) {
		if (actionType == QuestResErrConstants.COMMAND) {
		
			// this.speechRecResultTxt = result;
			Log.debug("SPEECH REC: Result: " + result);
			if (result.contains("create")) {
				processCommandInput(result);
			} else {
				initSpeechSynth(
						"I couldn't interpret "
								+ ("".equals(result) ? "it" : result)
								+ ". Please repeat command. Must contain create.",
						QuestResErrConstants.COMMAND);
			}

		} else {
			validateResponse(result);
		}
	}
	
	private void validateResponse(String result) {
		QuestResErrInterface currQuest = dispatcher.getQuestList().get(0);
		currQuest.setResponse(result);
		String validityMsg = currQuest.checkValidity();
		if ("OK".equals(validityMsg)) {
			results.add(currQuest.getResponseAsNumber());
			dispatcher.getQuestList().remove(0);
			collectInput();
		} else {
			initSpeechSynth(validityMsg + currQuest.getQuestion(), currQuest.getID());
		}
	}

	private void processCommandInput(String result) {
		String[] txtArray = result.split("create ");
		int commandID = getCommandID(txtArray[1]);
		if (txtArray.length == 2
				&& "".equals(txtArray[0])
				&& commandID != QuestResErrConstants.NOT_SUPPORTED) {
			dispatcher.processCommand(commandID);
		} else {
			initSpeechSynth(
					"I couldn't interpret " + result
							+ ". Please repeat command. Must contain create and tool name.",
					QuestResErrConstants.COMMAND);
		}
	}

	private static int getCommandID(String command) {
		switch (command) {
		case "Point":
		case "point":
			return QuestResErrConstants.CREATE_POINT;
		case "Segment":
		case "segment":
			return QuestResErrConstants.CREATE_SEGMENT;
		case "Circle":
		case "circle":
			return QuestResErrConstants.CREATE_CIRCLE;

		default:
			return QuestResErrConstants.NOT_SUPPORTED;
		}
	}

	@ExternalAccess
	private void showMessage(String msg) {
		ToolTipManagerW.sharedInstance().showBottomMessage(msg, appW);
	}

	/**
	 * collect necessary input from the user by processing the question list
	 */
	public void collectInput() {
		if (dispatcher.getQuestList().isEmpty()) {
			initSpeechSynth("Object created.", -1);
			dispatcher.getCurrentCommand().createGeo(appW, results);
			results.clear();
		} else {
			QuestResErrInterface currQuest = dispatcher.getQuestList().get(0);
			initSpeechSynth(currQuest.getQuestion(), currQuest.getID());
		}
	}
}
