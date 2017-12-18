using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.XR;
using UnityEngine.XR.WSA.Input;
using HoloToolkit.Unity.InputModule;


public class MenuModesl : MonoBehaviour, IInputClickHandler, IInputSource, ISourceStateHandler {

    InteractionSourceState[] interactionSourceStates;


	// Use this for initialization
	void Start () {
       

    }
	
	// Update is called once per frame
	void Update () {
         

        
    }

    private void OnMouseOver()
    {
        
    }

    public void OnInputClicked(InputClickedEventData eventData)
    {
        var interactionSourceStates = InteractionManager.GetCurrentReading();
       // if (interactionSourceState.touchpadTouched && interactionSourceState.touchpadPosition.x > 0.5)
        {

            // ...
        }
        throw new System.NotImplementedException();
    }

    public SupportedInputInfo GetSupportedInputInfo(uint sourceId)
    {
        throw new System.NotImplementedException();
    }

    public bool SupportsInputInfo(uint sourceId, SupportedInputInfo inputInfo)
    {
        throw new System.NotImplementedException();
    }

    public bool TryGetSourceKind(uint sourceId, out InteractionSourceInfo sourceKind)
    {
        throw new System.NotImplementedException();
    }

    public bool TryGetPointerPosition(uint sourceId, out Vector3 position)
    {
        throw new System.NotImplementedException();
    }

    public bool TryGetGripPosition(uint sourceId, out Vector3 position)
    {
        throw new System.NotImplementedException();
    }

    public bool TryGetPointerRotation(uint sourceId, out Quaternion rotation)
    {
        throw new System.NotImplementedException();
    }

    public bool TryGetGripRotation(uint sourceId, out Quaternion rotation)
    {
        throw new System.NotImplementedException();
    }

    public bool TryGetPointingRay(uint sourceId, out Ray pointingRay)
    {
        throw new System.NotImplementedException();
    }

    public bool TryGetThumbstick(uint sourceId, out bool isPressed, out Vector2 position)
    {
        throw new System.NotImplementedException();
    }

    public bool TryGetTouchpad(uint sourceId, out bool isPressed, out bool isTouched, out Vector2 position)
    {
 
        throw new System.NotImplementedException();
    }

    public bool TryGetSelect(uint sourceId, out bool isPressed, out double pressedValue)
    {
        throw new System.NotImplementedException();
    }

    public bool TryGetGrasp(uint sourceId, out bool isPressed)
    {
        throw new System.NotImplementedException();
    }

    public bool TryGetMenu(uint sourceId, out bool isPressed)
    {
        throw new System.NotImplementedException();
    }

    public void OnSourceDetected(SourceStateEventData eventData)
    {
        
        throw new System.NotImplementedException();
        
    }

    public void OnSourceLost(SourceStateEventData eventData)
    {
        throw new System.NotImplementedException();
    }
}
