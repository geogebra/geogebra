using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using MixedRealityToolkit.InputModule.InputHandlers;
using MixedRealityToolkit.InputModule.EventData;

public class CloseWebviewIcon : MonoBehaviour, IFocusable {

    public GameObject webviewManager;
    private WebviewManager webviewManagerScript;

    // Use this for initialization
    void Start () {
		if (webviewManager == null)
        {
            webviewManager = GameObject.Find("GGBKeyboardDisplayCanvas");
            webviewManagerScript = webviewManager.GetComponent<WebviewManager>();
        }
	}

    // Update is called once per frame
    void Update () {
		
	}

    public void OnInputClicked(InputClickedEventData eventData)
    {
        webviewManagerScript.DissapperWebview();
    }

    public void OnFocusEnter()
    {
        throw new System.NotImplementedException();
    }

    public void OnFocusExit()
    {
        throw new System.NotImplementedException();
    }
}
