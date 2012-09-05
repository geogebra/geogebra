package geogebra.common.kernel.advanced;

import geogebra.common.kernel.Construction;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.algos.AlgoElement;
import geogebra.common.kernel.algos.Algos;
import geogebra.common.kernel.arithmetic.NumberValue;
import geogebra.common.kernel.cas.AlgoPrimeFactorization;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoList;
import geogebra.common.kernel.geos.GeoNumeric;
import geogebra.common.main.App;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class AlgoDivisorsList extends AlgoElement {

	GeoList result;
	private NumberValue number;
	private AlgoPrimeFactorization factors;
	private GeoList factorList;
	List<Long> factList = new ArrayList<Long>();
	
	public AlgoDivisorsList(Construction cons, String label, NumberValue number) {
		super(cons);
		this.number = number;
		factors = new AlgoPrimeFactorization(cons, number);
		factorList = factors.getResult();
		result = new GeoList(cons);
		setInputOutput();
		compute();
		result.setLabel(label);
	}

	@Override
	protected void setInputOutput() {
		setOnlyOutput(result);
		input = new GeoElement[]{number.toGeoElement()};
		setDependencies();

	}

	@Override
	public void compute() {
		if(!factorList.isDefined()||!Kernel.isInteger(number.getDouble())){
			result.setUndefined();
			return;
		}
		
		int oldLength = 1;
		factList.clear();
		factList.add(1L);
		for(int i=0;i<factorList.size();i++){
			GeoList pair = (GeoList) factorList.get(i);
			double exp = ((NumberValue)pair.get(1)).getDouble();
			double prime = ((NumberValue)pair.get(0)).getDouble();
			long power = Math.round(prime);
			for(int k=1;k<=exp;k++){
				for(int j=0;j<oldLength;j++){
					App.debug(factList.get(j)*power);
					factList.add(factList.get(j)*power);
				}
				power *= Math.round(prime);
			}
			oldLength = factList.size();
			
		}
		result.setDefined(true);
		result.clear();
		Set<Long> sortedSet= new TreeSet<Long>();
		sortedSet.addAll(factList);   
		 Iterator<Long> iterator = sortedSet.iterator();
	     
	        while (iterator.hasNext()) {
	     	   result.add(new GeoNumeric(cons,iterator.next()));
	        }      
	        

	}

	@Override
	public Algos getClassName() {
		return Algos.AlgoDivisorsList;
	}

	public GeoList getResult() {
		return result;
	}

	// TODO Consider locusequability

}
