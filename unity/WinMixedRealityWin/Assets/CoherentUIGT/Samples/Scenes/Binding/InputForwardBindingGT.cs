using UnityEngine;
using System.Collections;

public class InputForwardBindingGT : MonoBehaviour
{
	// Use this for initialization
	void Start ()
	{
		CoherentUIGTView view = GetComponent<CoherentUIGTView>();

		if (view == null)
		{
			Debug.LogError("The GameObject must have a CoherentGTView component!");
			return;
		}
		// This is the only view in the sample and we'll always forward the input to it
		view.ReceivesInput = true;
	}
}
