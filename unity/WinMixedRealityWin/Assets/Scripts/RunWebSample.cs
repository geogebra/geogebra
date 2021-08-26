using Adept;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.WSA;

public class RunWebSample : MonoBehaviour  {

	// Use this for initialization
	void Start () {
		
	}
	
	// Update is called once per frame
	void Update () {
		
	}

    public void RunWeb()
    {
        var contentPage = AppViewManager.Views["ContentPage"];
        if (contentPage != null)
        {
            contentPage.Switch(contentPage);
            //contentPage.SwitchAndConsolidate(contentPage);
        }
    }
    /*
    public void OnInputClicked(InputClickedEventData eventData)
    {
        var contentPage = AppViewManager.Views["ContentPage"];
        if (contentPage != null)
        {
            contentPage.Switch();
        }
    }
    */
}
