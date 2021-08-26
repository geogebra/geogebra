using MixedRealityToolkit.InputModule.EventData;
using MixedRealityToolkit.InputModule.InputHandlers;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;



public class SelectScreen : MonoBehaviour, IFocusable, IInputClickHandler {

    public float highlightedSize = 1.1f;
    private Vector3 _originTransform;

    [HideInInspector]
    public GameManager gameManager;
    public GameObject gameManagerObj;

    private void Start()
    {

        gameManagerObj = GameObject.Find("GameManager");
        _originTransform = this.transform.localScale;

    }

    public void OnFocusEnter()
    {
        this.transform.localScale = transform.localScale * highlightedSize;
    }

    public void OnFocusExit()
    {
        this.transform.localScale = _originTransform;
        //throw new System.NotImplementedException();
    }

    public void OnInputClicked(InputClickedEventData eventData)
    {
        gameManagerObj.GetComponent<ScreenshotManager>().SelectItem(this.gameObject);
        //throw new System.NotImplementedException();
    }
}
