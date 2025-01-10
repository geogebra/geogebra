package org.geogebra.common.geogebra3D.kernel3D.algos;

import org.geogebra.common.geogebra3D.kernel3D.geos.GeoElement3D;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.geos.GeoElement;

/**
 * 
 * Super-class of algorithms creating {@link GeoElement3D}.
 * 
 * <h3>How to create a new algo</h3>
 * 
 * We'll call here our new element "AlgoNew3D" and create an new class
 * AlgoNew3D.
 * <ul>
 * <li>Create a member referring to the output(s)
 * <p>
 * <code>
      private GeoNew3D geoNew3D;
    </code></li>
 * <li>Create a constructor :
 * <p>
 * <code>
    public AlgoNew3D(Construction c, ... inputs) { <br> &nbsp;&nbsp;
      super(c); <br> &nbsp;&nbsp;
      geoNew3D = new GeoNew3D(....); <br> &nbsp;&nbsp;
      //other outputs <br> &nbsp;&nbsp;
      //eventually remember the inputs in special members <br> &nbsp;&nbsp;
      setInputOutput(new GeoElement[] {... inputs}, new GeoElement[] {... outputs}); <br> 
    }
    </code></li>
 * <li>Create a constructor with a label :
 * <p>
 * <code>
    public AlgoNew3D(Construction c, String label, ... inputs) { <br> &nbsp;&nbsp;
      this(c, ... inputs); <br> &nbsp;&nbsp;
      geoNew3D.setLabel(label); <br> 
    }
    </code></li>
 * <li>Explain how outputs are computed with the inputs :
 * <p>
 * <code>
    public void compute() { <br> &nbsp;&nbsp;
      // stuff <br> 
    }
    </code></li>
 * <li>Set the classname :
 * <p>
 * <code>
    protected Algos getClassName() { <br> &nbsp;&nbsp;
      return Algos.AlgoNew3D; <br> 
    }
    </code></li>
 * <li>Create a <code>getGeo()</code> method for each output you want the kernel
 * be able to catch</li>
 * </ul>
 */
abstract public class AlgoElement3D extends AlgoElement {

	/**
	 * Default constructor.
	 * 
	 * @param c
	 *            construction
	 */
	public AlgoElement3D(Construction c) {
		this(c, true);
	}

	/**
	 * constructor.
	 * 
	 * @param c
	 *            construction
	 * @param addToConstructionList
	 *            says if it has to be added to the construction list
	 */
	public AlgoElement3D(Construction c, boolean addToConstructionList) {
		super(c, addToConstructionList);
	}

	/**
	 * set the {@link GeoElement} in input and in output. call finally
	 * {@link #setInputOutput()}
	 * 
	 * @param a_input
	 *            elements in input
	 * @param a_output
	 *            elements in output
	 */
	protected void setInputOutput(GeoElement[] a_input, GeoElement[] a_output) {

		input = a_input;
		setOutput(a_output);
		setInputOutput(true);

	}

	/**
	 * set the {@link GeoElement} in input and in output. call finally
	 * {@link #setInputOutput()}
	 * 
	 * @param input
	 *            elements in input
	 * @param efficientInput
	 *            input used for updating
	 * @param singleOutput
	 *            single output element
	 */
	protected void setInputOutput(GeoElement[] input,
			GeoElement[] efficientInput, GeoElement singleOutput) {
		this.input = input;
		this.setOnlyOutput(singleOutput);
		setEfficientDependencies(input, efficientInput);
	}

	/**
	 * calls {@link AlgoElement#setDependencies()} and
	 * {@link AlgoElement#compute()}
	 */
	@Override
	protected void setInputOutput() {

		setInputOutput(true);

	}

	/**
	 * calls {@link AlgoElement#setDependencies()} and
	 * {@link AlgoElement#compute()}
	 * 
	 * @param setDependencies
	 *            says if the dependencies have to be set
	 */
	protected void setInputOutput(boolean setDependencies) {

		if (setDependencies) {
			setDependencies();
		}

		compute();

	}

}
