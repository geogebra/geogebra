package org.geogebra.web.html5.gui.voiceInput.command;

import java.util.ArrayList;

import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoPoint;
import org.geogebra.web.html5.gui.voiceInput.questResErr.QuestResErrInterface;
import org.geogebra.web.html5.gui.voiceInput.questResErr.XCoordQuestResErr;
import org.geogebra.web.html5.gui.voiceInput.questResErr.YCoordQuestResErr;
import org.geogebra.web.html5.main.AppW;

/**
 * @author Csilla
 *
 */
public class VoiceInputPoint implements VoiceInputCommandInterface {

	private ArrayList<QuestResErrInterface> questResList;

	/**
	 * create voice input based point defined by list of question+result+error
	 */
	public VoiceInputPoint() {
		initQuestList();
	}

	@Override
	public ArrayList<QuestResErrInterface> getQuestResList() {
		return questResList;
	}

	@Override
	public void initQuestList() {
		questResList = new ArrayList<>();
		XCoordQuestResErr xCoord = new XCoordQuestResErr();
		YCoordQuestResErr yCoord = new YCoordQuestResErr();
		questResList.add(xCoord);
		questResList.add(yCoord);
	}

	@Override
	public GeoElement createGeo(AppW appW, ArrayList<Double> inputList) {
		double xCoord = inputList.get(0);
		double yCoord = inputList.get(1);
		return new GeoPoint(appW.getKernel().getConstruction(), "A", xCoord,
				yCoord,
				1.0);
	}
}
