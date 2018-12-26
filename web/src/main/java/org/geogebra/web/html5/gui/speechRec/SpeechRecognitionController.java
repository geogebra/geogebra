package org.geogebra.web.html5.gui.speechRec;

import org.geogebra.common.kernel.algos.AlgoCirclePointRadius;
import org.geogebra.common.kernel.geos.GeoNumeric;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.common.util.debug.Log;
import org.geogebra.web.html5.gui.tooltip.ToolTipManagerW;
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
	private String speechRecResultTxt = "";
	private String action;
	private double xCoord = 0;
	private double yCoord = 0;
	private double radius = 0;

	/**
	 * @param app
	 *            see {@link AppW}
	 */
	public SpeechRecognitionController(AppW app) {
		this.appW = app;
		setLang(app.getLocalization().getLocaleStr());
	}

	/**
	 * @return see {@link AppW}
	 */
	public AppW getAppW() {
		return appW;
	}

	/**
	 * @return action is waited by the speech recognition
	 */
	public String getAction() {
		return action;
	}

	/**
	 * @param action
	 *            action which should be recognized
	 */
	public void setAction(String action) {
		this.action = action;
	}

	/**
	 * @param lang
	 *            language
	 */
	public void setLang(String lang) {
		if (lang == null) {
			this.lang = "en-US";
		} else {
			this.lang = lang;
		}
	}

	/**
	 * @param langID
	 *            national language code
	 */
	public native void setLanguage(String langID) /*-{
		this.@org.geogebra.web.html5.gui.speechRec.SpeechRecognitionController::setLang(Ljava/lang/String;)(langID);
	}-*/;

	/**
	 * @param actionStr
	 *            action
	 * @param result
	 *            from speech recognition
	 */
	public void onResponse(String actionStr, String result) {
		if ("command".equals(actionStr)) {
			this.speechRecResultTxt = result;
			Log.debug("SPEECH REC: Result: " + result);
			if (speechRecResultTxt.contains("create")) {
				processCommandSpeechText();
			} else {
				initSpeechSynth(
						"I couldn't interpret "
								+ ("".equals(speechRecResultTxt)
										? "it"
										: speechRecResultTxt)
								+ ". Please repeat command. Must contain create.",
						"command");
			}
		} else if ("xCoord".equals(actionStr)) {
			setXCoord(result);
		} else if ("yCoord".equals(actionStr)) {
			setYCoord(result);
		} else if ("radius".equals(actionStr)) {
			setRadius(result);
		}
	}

	/**
	 * @param xCoordStr
	 *            x coord
	 */
	public void setXCoord(String xCoordStr) {
		try {
			this.xCoord = Double.parseDouble(xCoordStr);
			getYCoord();
		} catch (NumberFormatException e) {
			initSpeechSynth(
					"Your input was "
							+ ("".equals(xCoordStr) ? "empty" : xCoordStr)
							+ ". The x coordinate must be a number. "
							+ "Please give the x coordinate of the center.",
					"xCoord");
		}
	}

	/**
	 * @param yCoordStr
	 *            y coord
	 */
	public void setYCoord(String yCoordStr) {
		try {
			this.yCoord = Double.parseDouble(yCoordStr);
			getRadius();
		} catch (NumberFormatException e) {
			initSpeechSynth(
					"Your input was "
							+ ("".equals(yCoordStr) ? "empty" : yCoordStr)
							+ ". The y coordinate must be a number. "
							+ "Please give the y coordinate of the center.",
					"yCoord");
		}
	}

	/**
	 * @param radiusStr
	 *            radius
	 */
	public void setRadius(String radiusStr) {
		try {
			this.radius = Double.parseDouble(radiusStr);
			if (radius <= 0) {
				initSpeechSynth(
						"The radius must be a positive number. Please give the radius.",
						"radius");
			} else {
				AlgoCirclePointRadius circleAlgo = new AlgoCirclePointRadius(
						appW.getKernel().getConstruction(), getPointGeo(),
						getRadiusGeo());
				circleAlgo.getCircle().setLabel("C");
				initSpeechSynth("Circle with center coordinates " + xCoord
						+ " and " + yCoord + " and radius " + radius
						+ " has been created.", "created");
			}
		} catch (NumberFormatException e) {
			initSpeechSynth(
					"Your input was "
							+ ("".equals(radiusStr) ? "empty" : radiusStr)
							+ ". The radius must be a number. Please give the radius.",
					"radius");
		}
	}

	private GeoPoint getPointGeo() {
		return new GeoPoint(appW.getKernel().getConstruction(), "M", xCoord,
				yCoord, 1.0);
	}

	private GeoNumeric getRadiusGeo() {
		return new GeoNumeric(appW.getKernel().getConstruction(), radius);
	}

	private void processCommandSpeechText() {
		String[] txtArray = speechRecResultTxt.split("create ");
		if (txtArray.length == 2 && "".equals(txtArray[0]) && appW.getKernel()
				.getAlgebraProcessor().isCommandAvailable(txtArray[1])) {
			String commandName = txtArray[1];
			Log.debug("SPEECH REC: found command: " + commandName);
			// TODO for now only circle, to do extension on other
			createCircle();
		} else {
			initSpeechSynth(
					"I couldn't interpret " + speechRecResultTxt
							+ ". Please repeat command. Must contain create and tool name.",
					"command");
		}
	}

	private void createCircle() {
		getXCoord();
	}

	/**
	 * get y coord from user
	 */
	public void getYCoord() {
		initSpeechSynth("Please give y coordinate of the center", "yCoord");
	}

	/**
	 * get x coord from user
	 */
	public void getXCoord() {
		initSpeechSynth("Please give x coordinate of the center", "xCoord");
	}

	/**
	 * get radius from user
	 */
	public void getRadius() {
		initSpeechSynth("Please give the radius", "radius");
	}

	/**
	 * @param toSay
	 *            what to say
	 * @param actionStr
	 *            action
	 */
	public native void initSpeechSynth(String toSay, String actionStr) /*-{
		var synth = window.speechSynthesis;
		var that = this;
		if (!('webkitSpeechRecognition' in window)) {
			toSay = "Speech recognition not supported in this browser, please use chrome.";
			actionStr = "";
		}
		var action = actionStr;

		if (synth.speaking) {
			console.error('speechSynthesis.speaking');
			return;
		}

		var utterThis = new SpeechSynthesisUtterance(toSay);
		utterThis.onend = function(event) {
			console.log('SpeechSynthesisUtterance.onend');
			if (action === "created" || action === "") {
				return;
			}
			that.@org.geogebra.web.html5.gui.speechRec.SpeechRecognitionController::initSpeechRec(Ljava/lang/String;)(action);
		}
		utterThis.onerror = function(event) {
			console.error('SpeechSynthesisUtterance.onerror');
		}
		utterThis.pitch = 1;
		utterThis.rate = 1;
		utterThis.lang = 'en-US';
		that.@org.geogebra.web.html5.gui.speechRec.SpeechRecognitionController::showMessage(Ljava/lang/String;)(toSay);
		synth.speak(utterThis);
	}-*/;

	/**
	 * init speech recognition
	 * 
	 * @param actionStr
	 *            action
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
		recognition.interimResults = false;
		recognition.maxAlternatives = 1;
		that.@org.geogebra.web.html5.gui.speechRec.SpeechRecognitionController::setAction(Ljava/lang/String;)(actionStr);

		recognition.start();
		console.log('SPEECH REC: Ready to receive a command.');

		recognition.onresult = function(event) {
			var actionStr = that.@org.geogebra.web.html5.gui.speechRec.SpeechRecognitionController::action;
			var last = event.results.length - 1;
			var result = event.results[last][0].transcript;

			console.log('SPEECH REC: Result received: ' + result + '.');
			console.log('SPEECH REC: Confidence: '
					+ event.results[0][0].confidence);

			that.@org.geogebra.web.html5.gui.speechRec.SpeechRecognitionController::onResponse(Ljava/lang/String;Ljava/lang/String;)(actionStr,result);
		}

		recognition.onspeechend = function() {
			recognition.stop();
			console.log('SPEECH REC: Recognition stopped.');
		}

		recognition.onnomatch = function(event) {
			console.log("SPEECH REC: I didn't recognise the text.");
		}

		recognition.onerror = function(event) {
			console.log('SPEECH REC: Error occurred in recognition: '
					+ event.error);
			var actionStr = that.@org.geogebra.web.html5.gui.speechRec.SpeechRecognitionController::action;
			that.@org.geogebra.web.html5.gui.speechRec.SpeechRecognitionController::onResponse(Ljava/lang/String;Ljava/lang/String;)(actionStr,"");
		}
	}-*/;

	private void showMessage(String msg) {
		ToolTipManagerW.sharedInstance().showBottomMessage(msg, true, appW);
	}
}
