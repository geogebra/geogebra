using UnityEngine;
using System.Collections;

class FacebookSampleScriptGT : MonoBehaviour
{
	public const string FacebookAppURL = "https://s3.amazonaws.com/CoherentLabs/Samples/facebook.html";
	public const string LocalAppURL = "coui://uiresources/FacebookGT/facebook.html";

	public void Start()
	{
		var viewComponent = GetComponent<CoherentUIGTView>();
		viewComponent.Listener.FinishLoad += OnFinishLoad;
	}

	void OnFinishLoad (string path, bool isMainView)
	{
		var viewComponent = GetComponent<CoherentUIGTView>();

		if (path.StartsWith (FacebookAppURL))
		{
			// change the url, keeping all parameters intact
			string redirectURL = LocalAppURL + path.Substring(FacebookAppURL.Length);
			viewComponent.View.LoadURL(redirectURL);
		}
	}
}
