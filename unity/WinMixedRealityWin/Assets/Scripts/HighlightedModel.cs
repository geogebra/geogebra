using HoloToolkit.Unity.InputModule;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;

public class HighlightedModel : MonoBehaviour, IFocusable, IInputClickHandler {

    public Color NormalColor;
    public Color HiglitColor;

    public float HighlightedSize = 1.2f;
    private Vector3 _originTransform;

    public GameObject WorkModel;
    public GameObject HihlightedText;

    public GameObject InstructionText;

    #region Members
    [HideInInspector]
    public GameManager GameManager;
    public GameObject gameManager;
    #endregion

    private void Start()
    {
        // Assign GameManager
        gameManager = GameObject.Find("GameManager");

        _originTransform = this.transform.localScale;
        HihlightedText.SetActive(false);
        
    }
    public void OnFocusEnter()
    {
        this.transform.localScale = transform.localScale * HighlightedSize;
        HihlightedText.SetActive(true);
    }

    public void OnFocusExit()
    {
        this.transform.localScale = _originTransform;
        HihlightedText.SetActive(false);
    }

    public void OnInputClicked(InputClickedEventData eventData)
    {
        Debug.Log("You clicked on me");
        gameManager.GetComponent<GameManager>().TempModel = WorkModel;
        gameManager.GetComponent<GameManager>().TempText = InstructionText;
        gameManager.GetComponent<GameManager>().OnButtonPressed();
        

    }

    void TaskOnClick()
    {
        Debug.Log("You clicked on me");
        gameManager.GetComponent<GameManager>().TempModel = WorkModel;
        gameManager.GetComponent<GameManager>().OnButtonPressed();
        
    }
}
