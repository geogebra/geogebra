using System.Collections;
using System.Collections.Generic;
using UnityEngine;

// using HoloToolkit.Unity.Controllers; missing reference

#if UNITY_WSA && UNITY_2017_2_OR_NEWER
using UnityEngine.XR.WSA.Input;
#endif


public class SourcePadDetection : MonoBehaviour
{
    GameObject DetectionObject;
    GameObject GetInput;

    // Use this for initialization
    void Start () {
        if (DetectionObject == null)
        {
            DetectionObject = GameObject.Find("BasicStatus");
        }

       // GetInput = DetectionObject.GetComponent<ControllerStates>();
    }
	
	// Update is called once per frame
	void Update () {
  
    }

    private void InteractionSourceUpdated(InteractionSourceUpdatedEventArgs obj)
    {
        if ( obj.state.touchpadTouched)
        {
            print("Touchpad touched.");            
        }
    }

}
