package org.geogebra.web.html5.gui.speechRec;

import org.geogebra.common.kernel.algos.AlgoCirclePointRadius;
import org.geogebra.common.kernel.arithmetic.Command;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.main.AppW;

/**
 * Controller for the speech recognition functionality
 * 
 * @author csilla
 *
 */
public class SpeechRecognitionController {
	private AppW appW;
	private String lang = null;
	private String speechRecResultTxt;
	private String action;
	private int xCoord = 0;
	private int yCoord = 0;
	private int radius = 0;

	/**
	 * @param app
	 *            see {@link AppW}
	 */
	public SpeechRecognitionController(AppW app) {
		this.appW = app;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

	public native void setLanguage(String langID) /*-{
		this.@org.geogebra.web.html5.gui.speechRec.SpeechRecognitionController::setLang(Ljava/lang/String;)(langID);
	}-*/;

	public void setResult(String result) {
		this.speechRecResultTxt = result;
		// speechRecResultTxt = "create Circle";
		Log.debug("SPEECH REC: Result: " + result);
		if (speechRecResultTxt.contains("create")) {
			processCommandSpeechText();
		}
	}

	public void setXCoord(String xCoordStr) {
		this.xCoord = Integer.valueOf(xCoordStr);
		Log.debug("SPEECH REC: xCoord: " + xCoord);
		getYCoord();
	}

	public void setYCoord(String yCoordStr) {
		this.yCoord = Integer.valueOf(yCoordStr);
		Log.debug("SPEECH REC: yCoord: " + yCoord);
		getRadius();
	}

	public void setRadius(String radiusStr) {
		this.radius = Integer.valueOf(radiusStr);
		Log.debug("SPEECH REC: radius: " + radiusStr);
		AlgoCirclePointRadius circleAlgo = new AlgoCirclePointRadius(
				appW.getKernel().getConstruction(), getPointGeo(),
				getRadiusGeo());
		circleAlgo.getCircle().setLabel("C");
	}

	private GeoPoint getPointGeo() {
		return new GeoPoint(appW.getKernel().getConstruction(), "A",
				xCoord, yCoord, 1.0);
	}

	private GeoNumeric getRadiusGeo() {
		return new GeoNumeric(appW.getKernel().getConstruction(), radius);
	}

	private void processCommandSpeechText() {
		String[] txtArray = speechRecResultTxt.split("create ");
		for (int i = 0; i < txtArray.length; i++) {
			Log.debug("SPEECH REC: text:" + txtArray[i]);
		}
		if (txtArray.length == 2 && "".equals(txtArray[0]) && 
				appW.getKernel().getAlgebraProcessor()
						.isCommandAvailable(txtArray[1])) {
			String commandName = txtArray[1];
			Log.debug("SPEECH REC: found command: " + commandName);
			java.util.List<String> commandList = appW.getCommandDictionary()
					.getCompletions(commandName);
			switch (commandList.get(0)) {
			case "Circle":
				createCircle(commandList.get(0));
				break;
			case "Point":
			default:
				break;
			}
		}
	}

	private void createCircle(String commandName) {
		Command circleCmd = new Command(appW.getKernel(), commandName, false);
		getXCoord();
		/*
		 * int xCoord = Integer.valueOf(speechRecResultTxt);
		 * initSpeechSynth("Please give y coordinate of the middle point"); int
		 * yCoord = Integer.valueOf(speechRecResultTxt); circleCmd.addArgument(
		 * new GeoPoint(appW.getKernel().getConstruction(), xCoord, yCoord,
		 * 1).evaluate(StringTemplate.algebraTemplate).wrap());
		 * initSpeechSynth("Please give radius"); // initSpeechRec(); int radius
		 * = Integer.valueOf(speechRecResultTxt); circleCmd.addArgument(new
		 * ExpressionNode(appW.getKernel(), radius));
		 * appW.getKernel().getAlgebraProcessor().processCommand(circleCmd, new
		 * EvalInfo(true));
		 */
	}

	public void getYCoord() {
		initSpeechSynth("Please give y coordinate of the middle point",
				"yCoord");
	}

	public void getXCoord() {
		initSpeechSynth("Please give x coordinate of the middle point",
				"xCoord");
	}

	public void getRadius() {
		initSpeechSynth("Please give the radius",
				"radius");
	}

	public native void initSpeechSynth(String toSay, String actionStr) /*-{
		console.log("SPEECH initSpeechSynth");
		var synth = window.speechSynthesis;
		var voices = synth.getVoices();
		var that = this;
		var action = actionStr;
		var say = toSay;

		if (synth.speaking) {
			console.error('speechSynthesis.speaking');
			return;
		}
		//if (inputTxt.value !== '') {
		var utterThis = new SpeechSynthesisUtterance(toSay);
		utterThis.onend = function(event) {
			console.log('SpeechSynthesisUtterance.onend');
			that.@org.geogebra.web.html5.gui.speechRec.SpeechRecognitionController::initSpeechRec(Ljava/lang/String;)(action);
		}
		utterThis.onerror = function(event) {
			console.error('SpeechSynthesisUtterance.onerror');
		}
		var selectedOption = 'Google US English';
		for (i = 0; i < voices.length; i++) {
			if (voices[i].name === selectedOption) {
				utterThis.voice = voices[i];
			}
		}
		utterThis.pitch = 1;
		utterThis.rate = 1;
		utterThis.lang = 'en-US';
		synth.speak(utterThis);
		//}
	}-*/;

	/**
	 * init speech recognition
	 */
	public native void initSpeechRec(String actionStr) /*-{
		var that = this;
		var SpeechRecognition = SpeechRecognition || webkitSpeechRecognition
		var SpeechGrammarList = SpeechGrammarList || webkitSpeechGrammarList
		var SpeechRecognitionEvent = SpeechRecognitionEvent
				|| webkitSpeechRecognitionEvent

		var recognition = new SpeechRecognition();
		var speechRecognitionList = new SpeechGrammarList();
		recognition.grammars = speechRecognitionList;
		recognition.lang = this.@org.geogebra.web.html5.gui.speechRec.SpeechRecognitionController::lang;
		console.log('SPEECH REC: Language set to');
		console.log(recognition.lang);
		recognition.interimResults = false;
		recognition.maxAlternatives = 1;
		that.@org.geogebra.web.html5.gui.speechRec.SpeechRecognitionController::setAction(Ljava/lang/String;)(actionStr);

		recognition.start();
		console.log('SPEECH REC: Ready to receive a command.');

		recognition.onresult = function(event) {
			// The SpeechRecognitionEvent results property returns a SpeechRecognitionResultList object
			// The SpeechRecognitionResultList object contains SpeechRecognitionResult objects.
			// It has a getter so it can be accessed like an array
			// The [last] returns the SpeechRecognitionResult at the last position.
			// Each SpeechRecognitionResult object contains SpeechRecognitionAlternative objects that contain individual results.
			// These also have getters so they can be accessed like arrays.
			// The [0] returns the SpeechRecognitionAlternative at position 0.
			// We then return the transcript property of the SpeechRecognitionAlternative object
			var actionStr = that.@org.geogebra.web.html5.gui.speechRec.SpeechRecognitionController::action;
			var last = event.results.length - 1;
			var result = event.results[last][0].transcript;

			console.log('SPEECH REC: Result received: ' + result + '.');
			console.log('SPEECH REC: Confidence: '
					+ event.results[0][0].confidence);

			if (actionStr === "command")
				that.@org.geogebra.web.html5.gui.speechRec.SpeechRecognitionController::setResult(Ljava/lang/String;)(result);
			else if (actionStr === "xCoord")
				that.@org.geogebra.web.html5.gui.speechRec.SpeechRecognitionController::setXCoord(Ljava/lang/String;)(result);
			else if (actionStr === "yCoord")
				that.@org.geogebra.web.html5.gui.speechRec.SpeechRecognitionController::setYCoord(Ljava/lang/String;)(result);
			else if (actionStr === "radius")
				that.@org.geogebra.web.html5.gui.speechRec.SpeechRecognitionController::setRadius(Ljava/lang/String;)(result);
		}

		recognition.onspeechend = function() {
			recognition.stop();
		}

		recognition.onnomatch = function(event) {
			console.log("SPEECH REC: I didn't recognise the text.");
		}

		recognition.onerror = function(event) {
			console.log('SPEECH REC: Error occurred in recognition: '
					+ event.error);
		}

	}-*/;

	/**
	 * get recognized text in English for now as log msg
	 */
	public native void runSpeechRec() /*-{
		$wnd.speechRec();
	}-*/;
}
