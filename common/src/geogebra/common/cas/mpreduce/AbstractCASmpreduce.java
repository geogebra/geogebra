package geogebra.common.cas.mpreduce;

import java.util.Map;

import geogebra.common.cas.CASgeneric;
import geogebra.common.cas.CASparser;

public abstract class AbstractCASmpreduce extends CASgeneric{

	public AbstractCASmpreduce(CASparser casParser) {
		super(casParser);
	}

	public abstract String evaluateMPReduce(String exp);
	
	@Override
	public String translateFunctionDeclaration(String label, String parameters,
			String body) {
		StringBuilder sb = new StringBuilder();
		sb.append(" procedure ");
		sb.append(label);
		sb.append("(");
		sb.append(parameters);
		sb.append("); begin return ");
		sb.append(body);
		sb.append(" end ");

		return sb.toString();
	}
	@Override
	public Map<String, String> initTranslationMap() {
		return new Ggb2MPReduce().getMap();
	}

}
