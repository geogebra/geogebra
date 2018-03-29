using UnityEngine;
using System.Collections;
using HoloToolkit.Unity.InputModule;

public class ObjectPickerGT : MonoBehaviour, IFocusable, IInputClickHandler, IPointerSpecificFocusable
{
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

    public void OnFocusEnter()
    {
        isFocused = true;
        Debug.Log("OnFocusEnter");
        //throw new System.NotImplementedException();

    }

    public void OnFocusEnter(PointerSpecificEventData eventData)
    {
        throw new System.NotImplementedException();
    }

    public void OnFocusExit()
    {
        isFocused = false;
        Debug.Log("OnFocusExit");
        //throw new System.NotImplementedException();
    }

    public void OnFocusExit(PointerSpecificEventData eventData)
    {
        throw new System.NotImplementedException();
    }

    public void OnInputClicked(InputClickedEventData eventData)
    {
        Debug.Log("OnInputClicked");
        throw new System.NotImplementedException();
    }

    void Start()
	{
		m_MainCamera = Camera.main;
		m_UISystem = CoherentUIGTSystem.CurrentUISystem;
        pointerCursor = GameObject.Find("DefaultCursor");
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
		//Original = if (Physics.Raycast(m_MainCamera.ScreenPointToRay(Input.mousePosition), out hitInfo))
        if (isFocused)
        {
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
                        (int)(hitInfo.textureCoord.y * viewComponent.Height));
                }
            }
        }
	}
}
