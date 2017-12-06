using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;
using HoloToolkit.Unity.InputModule;

public class PlusButton : MonoBehaviour, IXboxControllerHandler, IControllerTouchpadHandler {

    public void OnGamePadDetected(GamePadEventData eventData)
    {
        throw new System.NotImplementedException();
    }

    public void OnGamePadLost(GamePadEventData eventData)
    {
        throw new System.NotImplementedException();
    }

    public void OnInputPositionChanged(InputPositionEventData eventData)
    {
        throw new System.NotImplementedException();
    }


    //Touchpad
    public void OnTouchpadReleased(InputEventData eventData)
    {
        throw new System.NotImplementedException();
    }

    public void OnTouchpadTouched(InputEventData eventData)
    {
        throw new System.NotImplementedException();
    }

    public void OnXboxAxisUpdate(XboxControllerEventData eventData)
    {
        throw new System.NotImplementedException();
    }

    


    // Use this for initialization
    void Start () {
        



	}
	
	// Update is called once per frame
	void Update () {
		



	}


}
