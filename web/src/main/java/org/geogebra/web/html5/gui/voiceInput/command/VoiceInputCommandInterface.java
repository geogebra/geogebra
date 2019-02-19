package org.geogebra.web.html5.gui.voiceInput.command;

import java.util.ArrayList;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.web.html5.gui.voiceInput.questResErr.QuestResErrInterface;
import org.geogebra.web.html5.main.AppW;

/**
 * @author Csilla
 *
 */
public interface VoiceInputCommandInterface {

	/**
	 * @return list of questions of the task
	 */
	ArrayList<QuestResErrInterface> getQuestResList();

	/**
	 * 
	 */
	void initQuestList();

	/**
	 * @param appW
	 *            see {@link AppW}
	 * @param inputList
	 *            list of needed parameters
	 * @return geoElement created from input list
	 */
	GeoElement createGeo(AppW appW, ArrayList<Double> inputList);
}
