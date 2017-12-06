using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.XR.WSA.Input;
using HoloToolkit.Unity.InputModule;

public class MainModel : MonoBehaviour, IControllerInputHandler, IInputClickHandler, IFocusable, IControllerTouchpadHandler
{


    private float movementSpeedMultiplier = 0.001f;


    public void OnFocusEnter()
    {
        Debug.Log("you focused on Main Model");
    }

    public void OnFocusExit()
    {
        throw new System.NotImplementedException();
    }



    public void OnInputClicked(InputClickedEventData eventData)
    {
        //throw new System.NotImplementedException();
        Debug.Log("You clicked on me");
    }

    public void OnInputPositionChanged(InputPositionEventData eventData)
    {
 //       Debug.Log("OnInputPositionChanged");
 //       this.transform.localScale += this.transform.localScale + new Vector3(1,1,1)*movementSpeedMultiplier;
 //       Debug.Log("This.transform.localscale is: " + this.transform.localScale);
    }
    
    public void OnInputPositionUpdated ()
    {
        
        Debug.Log("OnInputPosition updated");

    }
    // IControllerTouchpadHandler
    public void OnTouchpadReleased(InputEventData eventData)
    {
        throw new System.NotImplementedException();
    }

    public void OnTouchpadTouched(InputEventData eventData)
    {
        Debug.Log("OnInputPositionChanged");
        this.transform.localScale += this.transform.localScale + new Vector3(1, 1, 1) * movementSpeedMultiplier;
        Debug.Log("This.transform.localscale is: " + this.transform.localScale);
        //throw new System.NotImplementedException();
    }




    // Use this for initialization
    void Start () {
		
	}
	
	// Update is called once per frame
	void Update () {
		
	}
    
}
