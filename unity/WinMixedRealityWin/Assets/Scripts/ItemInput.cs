using HoloToolkit.Unity.InputModule;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;

public class ItemInput : MonoBehaviour, IInputClickHandler
{

    #region Members
    [HideInInspector]
    public GameManager GameManager;
    public GameObject gameManager;
    #endregion

    public GameObject buttonModel;

    private void Start()
    {
        gameManager = GameObject.Find("GameManager");
    }

    public void OnInputClicked(InputClickedEventData eventData)
    {
        Debug.Log("OnInputClick");
       // gameManager.GetComponent<GameManager>().CheckForItem(itemName);       
    }

    /* void TaskOnClick()
    {
        gameManager.GetComponent<GameManager>().TempModel = ButtonModel;
        gameManager.GetComponent<GameManager>().OnButtonPressed();
    }
    */
}



