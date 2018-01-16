using HoloToolkit.Unity.InputModule;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;



public class SelectScreen : MonoBehaviour,IFocusable, IInputClickHandler {

    public float HighlightedSize = 1.1f;
    private Vector3 _originTransform;

    [HideInInspector]
    public GameManager GameManager;
    public GameObject gameManager;

    private void Start()
    {

        gameManager = GameObject.Find("GameManager");
        _originTransform = this.transform.localScale;

    }

    public void OnFocusEnter()
    {

        this.transform.localScale = transform.localScale * HighlightedSize;
    }

    public void OnFocusExit()
    {
        this.transform.localScale = _originTransform;
        //throw new System.NotImplementedException();
    }

    public void OnInputClicked(InputClickedEventData eventData)
    {
        gameManager.GetComponent<PhotoMechanic>().SelectItem(this.gameObject);
        //throw new System.NotImplementedException();
    }
}
