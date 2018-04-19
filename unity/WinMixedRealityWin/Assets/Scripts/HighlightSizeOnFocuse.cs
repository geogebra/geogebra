using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using HoloToolkit.Unity.InputModule;


public class HighlightSizeOnFocuse : MonoBehaviour, IFocusable {

    public float highlightedSize = 1.2f;
    private Vector3 _originTransform;

    // Use this for initialization
    void Start () {
        _originTransform = this.transform.localScale;
    }
	
    public void OnFocusEnter()
    {
        this.transform.localScale = transform.localScale * highlightedSize;
    }

    public void OnFocusExit()
    {
        this.transform.localScale = _originTransform;
    }
}
