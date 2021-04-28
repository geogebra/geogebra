package org.geogebra.common.kernel.advanced;

import java.util.ArrayList;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.algos.GetCommand;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoList;
import org.geogebra.common.kernel.geos.GeoText;

public class AlgoSplit extends AlgoElement {

	private GeoText inputText;
	private GeoList splitList;
	private GeoList outputList;

	/**
	 * constructor for split command
	 * @param cons construction
	 * @param inputText input text
	 * @param splitList list of texts to split by the input text
	 */
	public AlgoSplit(Construction cons, GeoText inputText, GeoList splitList) {
		super(cons);
		this.inputText = inputText;
		this.splitList = splitList;
		outputList = new GeoList(cons);
		setInputOutput();
		compute();
	}

	@Override
	protected void setInputOutput() {
		input = new GeoElement[]{inputText, splitList};
		setOnlyOutput(outputList);
		setDependencies();
	}

	@Override
	public void compute() {
		outputList.clear();
		if (!inputText.isDefined() || !splitList.isDefined()) {
			outputList.setUndefined();
			return;
		}
		ArrayList<String> results = new ArrayList<>();
		results.add(inputText.getTextString());

		for (int i = 0; i < splitList.size(); i++) {
			GeoElement geoElement = splitList.get(i);
			if (!geoElement.isGeoText() || !geoElement.isDefined()) {
				outputList.setUndefined();
				return;
			}
			String regex = ((GeoText) geoElement).getEscapedSpecialCharsString();
			results = split(results, regex);
		}

		for (String resultElem : results) {
			GeoText textGeo = new GeoText(cons, resultElem);
			outputList.add(textGeo);
		}
	}

	private ArrayList<String> split(ArrayList<String> inputs, String splitStr) {
		ArrayList<String> results = new ArrayList<>();

		for (String inputElem : inputs) {
			String[] splitResult = inputElem.split(splitStr);
			for (int j = 0; j < splitResult.length; j++) {
				if (splitResult[j] != null && !splitResult[j].isEmpty()) {
					results.add(splitResult[j]);
				}
			}
		}
		return results;
	}

	@Override
	public GetCommand getClassName() {
		return Commands.Split;
	}
}
