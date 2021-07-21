using UnityEngine;
using System.Collections;
using MixedRealityToolkit.InputModule.EventData;
using HoloToolkit.Unity;
using UnityStandardAssets.CrossPlatformInput;
using MixedRealityToolkit.InputModule.Focus;
using MixedRealityToolkit.InputModule.InputHandlers;

public class ObjectPickerGT : MonoBehaviour, IFocusable, IInputClickHandler, IPointerSpecificFocusable
{
    private string flip = "x";
	private Camera m_MainCamera;
	private CoherentUIGTSystem m_UISystem;
    public bool isFocused;
    public GameObject pointerCursor;
    public Vector3 GetCursorPosition
    {
        get
        {
            return pointerCursor.transform.position;
        }
    }
    public IPointingSource Pointer
    {
        get
        {
            throw new System.NotImplementedException();
        }
    }
    public bool invertedWidth = false;
    public bool invertedHeight = false;

    private int invW
    {
        get
        {
            if (invertedWidth)
            {
                return -1;
            }
            else
            {
                return 1;
            }
        }
    }

    private int invH
    {
        get
        {
            if (invertedHeight)
            {
                return -1;
            }
            else
            {
                return 1;
            }
        }
    }

    public bool isClicked;
    public MouseOperation mouseOperation;

    public GetControllerStates getControllerStates;
    public GameObject gameManager;
    public bool isClickedTracking;
    public bool IsTriggerDown
    {
        get
        {
            return getControllerStates.SelectPressed;
        }
    }
    public bool isReadyForNextClick = true;

    public Vector2 mousePositionSim;

    public void OnFocusEnter()
    {
        isFocused = true;
        isClickedTracking = true;
        m_UISystem.m_IsGazerOnView = true;
    }

    public void OnFocusEnter(PointerSpecificEventData eventData)
    {
        //throw new System.NotImplementedException();
    }

    public void OnFocusExit()
    {
        isClickedTracking = false;
        isFocused = false;
        m_UISystem.m_IsGazerOnView = false;
    }

    public void OnFocusExit(PointerSpecificEventData eventData)
    {
        //throw new System.NotImplementedException();
    }
    float time;
    bool toogle = true;
    public void OnInputClicked(InputClickedEventData eventData)
    {
        //Debug.Log("Time in the click");
        //string timestamp = System.DateTime.UtcNow.ToString("yyyy-MM-dd HH:mm:ss.fff",
                                            //System.Globalization.CultureInfo.InvariantCulture);
        //Debug.Log(timestamp);

        if (isReadyForNextClick)
        {
            StartCoroutine("LeftMouseClick");
        }
    }

    public IEnumerator LeftMouseClick()
    {
        //Debug.Log("In is ready for next click");
        isReadyForNextClick = false;
        m_UISystem.IsMouseLeftDowSimulation = true;
        yield return new WaitForEndOfFrame();
        //yield return new WaitForSeconds(0.1f);
        m_UISystem.IsMouseLeftDowSimulation = false;
        isReadyForNextClick = true;
    }

    public bool GetSelectPressed
    {
        get
        {
            return getControllerStates.SelectPressed;
        }
    }

    void Start()
	{
        m_MainCamera = Camera.main;
		m_UISystem = CoherentUIGTSystem.CurrentUISystem;
        pointerCursor = GameObject.Find("DefaultCursor");
        getControllerStates = GameObject.Find("GameManager").GetComponent<GetControllerStates>();
        gameManager = GameObject.Find("GameManager");
        mouseOperation = gameManager.GetComponent<MouseOperation>();

        getControllerStates = gameManager.GetComponent<GetControllerStates>();

        isReadyForNextClick = true;
    }

    void Update()
	{
            if (m_UISystem.HasFocusedView)
		{
			return;
		}

		// Reset input processing for all views
		for (int i = 0; i < m_UISystem.UIViews.Count; i++)
		{
			if (!m_UISystem.UIViews[i].ClickToFocus)
			{
				m_UISystem.UIViews[i].ReceivesInput = false;
			}
		}

		// Activate input processing for the view below the mouse cursor
		RaycastHit hitInfo;

        if (isFocused)
        {
            //CoherentUIGTSystem.CurrentUISystem.cursorPosition = GetCursorPosition;

            if (Physics.Raycast(
            Camera.main.transform.position,
            GetCursorPosition - Camera.main.transform.position,
            out hitInfo,
            20.0f,
            Physics.DefaultRaycastLayers
            ))
            {
                CoherentUIGTView viewComponent = null;

                Transform hitTransform = hitInfo.collider.transform;

                //added
                //viewComponent.SetMousePosition(
                        //(int)(hitInfo.textureCoord.x * viewComponent.Width),
                        //(int)(hitInfo.textureCoord.y * viewComponent.Height));

                for (int i = 0; i < m_UISystem.UIViews.Count; i++)
                {
                    if (m_UISystem.UIViews[i].transform.IsChildOf(hitTransform))
                    {
                        viewComponent = m_UISystem.UIViews[i];
                    }
                }

                if (viewComponent != null && !viewComponent.ClickToFocus)
                {
                    viewComponent.ReceivesInput = true;
                    viewComponent.SetMousePosition(
               (int)(hitInfo.textureCoord.x * viewComponent.Width),
               (int)(hitInfo.textureCoord.y * viewComponent.Height), flip);
               
                    /*viewComponent.SetMousePosition(
                        (int)(hitInfo.textureCoord.x * viewComponent.Width*invW),
                        (int)(hitInfo.textureCoord.y * viewComponent.Height*invH));
                        */
                    /*  
                  int hitInfox;
                  int hitInfoy;
                  if (invertedWidth)
                  {
                      hitInfox = (int)(viewComponent.Width - (hitInfo.textureCoord.x * viewComponent.Width));
                  }
                  else
                  {
                      hitInfox = (int)hitInfo.textureCoord.x * viewComponent.Width;
                  }

                  if (invertedHeight)
                  {
                      hitInfoy = (int)(viewComponent.Height - (hitInfo.textureCoord.y * viewComponent.Height));
                  }
                  else
                  {
                      hitInfoy = (int)hitInfo.textureCoord.y * viewComponent.Height;
                  }

                  viewComponent.SetMousePosition(
                      hitInfox,
                      hitInfoy, "y");
                  */

                    viewComponent.ReceivesInput = true;      
                        
 
                }
            }
        }
	}
}
