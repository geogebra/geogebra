using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;
using MixedRealityToolkit.InputModule.EventData;
using MixedRealityToolkit.InputModule.InputHandlers;
#if UNITY_EDITOR
using UnityEditor;
#endif

public class HighlightedModel : MonoBehaviour, IFocusable, IInputClickHandler {

    public Color normalColor;
    public Color highlightColor;

    public float highlightedSize = 1.2f;
    private Vector3 _originTransform;
    public GameObject workModel;
    public GameObject hihlightedText;
    public GameObject instructionText;

    #region Members
    [HideInInspector]
    public GameManager gameManager;
    public GameObject gameManagerObj;
    #endregion

    private void Start()
    {
        //Assign missing components
        if (hihlightedText == null)
        {
            hihlightedText = GameObject.Find(transform.parent.name + "/" + "Canvas");
        }

        if (instructionText == null)
        {
            string tempName = name + "_Text";
            instructionText = GameObject.Find(transform.root.name + "/Room01/TV/Canvas/" + tempName);
        }
        #if UNITY_EDITOR
        if (workModel == null)
        {
            workModel = (GameObject)AssetDatabase.LoadAssetAtPath("Assets/Prefabs/ShownModels/" + transform.parent.name + ".prefab", typeof(GameObject));
        }
        #endif

            // Assign GameManager
            gameManagerObj = GameObject.Find("GameManager");

        _originTransform = this.transform.localScale;
        hihlightedText.SetActive(false);      
    }
    public void OnFocusEnter()
    {
        this.transform.localScale = transform.localScale * highlightedSize;
        hihlightedText.SetActive(true);
    }

    public void OnFocusExit()
    {
        this.transform.localScale = _originTransform;
        hihlightedText.SetActive(false);
    }

    public void OnInputClicked(InputClickedEventData eventData)
    {
        gameManagerObj.GetComponent<GameManager>().tempModel = workModel;
        gameManagerObj.GetComponent<GameManager>().tempText = instructionText;
        gameManagerObj.GetComponent<GameManager>().OnButtonPressed();
    }

    void TaskOnClick()
    {
        gameManagerObj.GetComponent<GameManager>().tempModel = workModel;
        gameManagerObj.GetComponent<GameManager>().OnButtonPressed();      
    }
}
