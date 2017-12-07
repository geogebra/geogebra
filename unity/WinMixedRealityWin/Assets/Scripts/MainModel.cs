using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.XR.WSA.Input;
using HoloToolkit.Unity.InputModule;



namespace UnityEngine.XR.WSA.Input
{ 
public class MainModel : MonoBehaviour, IControllerInputHandler, IInputClickHandler, IFocusable, IControllerTouchpadHandler
    {

        
        private float movementSpeedMultiplier = 0.001f;

        #region Members
        [HideInInspector]
        public InteractionSourceState InteractionSource;
        public GameObject interatctionSourceState;
        #endregion

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

        public void OnInputPositionUpdated()
        {

            Debug.Log("OnInputPosition updated");

        }
        // IControllerTouchpadHandler
        public void OnTouchpadReleased(InputEventData eventData)
        {
            Debug.Log("OnInputPositionChanged");
            //throw new System.NotImplementedException();
        }

        public void OnTouchpadTouched(InputEventData eventData)
        {
            Debug.Log("OnInputPositionChanged");
            //this.transform.localScale += this.transform.localScale + new Vector3(1, 1, 1) * movementSpeedMultiplier;
            Debug.Log("This.transform.localscale is: " + this.transform.localScale);
            //throw new System.NotImplementedException();



            //controllerState.TouchpadPosition.x = 1;

            //Vector2 primaryAxis = OVRInput.Get(OVRInput.Axis2D.PrimaryThumbstick);
            //InteractionSourceState.touchpadPostion;
            //InteractionSourceState.touchpad

            if (interatctionSourceState.GetComponent<InteractionSourceState>().touchpadPosition.x > 0.4f)
            {
                Debug.Log("Touchpad is aboe 0.4");
                transform.localPosition = transform.localPosition * 0.5f;
            }


            var interactionSourceStates = InteractionManager.GetCurrentReading();

            
    }



        void Start()
        {
            if (interatctionSourceState == null)
            {
                interatctionSourceState = GameObject.Find("GesturesInput");
            }
            
        }

  
        // Update is called once per frame
        void Update()
        {
            
        }



    }
}
