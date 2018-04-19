using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using HoloToolkit.Unity.InputModule;

public class CloseWebviewIcon : MonoBehaviour, IFocusable, IInputClickHandler {

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

    public void OnFocusEnter()
    {
    }

    public void OnFocusExit()
    {
    }

    public void OnInputClicked(InputClickedEventData eventData)
    {
        webviewManagerScript.DissapperWebview();
    }

    // Update is called once per frame
    void Update () {
		
	}
}
