using MixedRealityToolkit.InputModule.InputHandlers;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class CursorSizeOnFocuse : MonoBehaviour, IFocusable {


    public GameObject cursor;
    public Vector3 cursorDefaultScale;
    public Vector3 cursorWebviewScale;

    // Use this for initialization
    void Start () {
        cursor = GameObject.Find("CursorVisual");
        cursorDefaultScale = cursor.transform.localScale;
    }
	
	// Update is called once per frame
	void Update () {
		
	}

    public void OnFocusEnter()
    {
        cursor.transform.localScale = new Vector3(0.02f, 0.02f, 0.02f);
    }

    public void OnFocusExit()
    {
        cursor.transform.localScale = cursorDefaultScale;
    }
}
