using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;

public class ModelButton : MonoBehaviour
{

    #region Members
    [HideInInspector]
    public GameManager GameManager;
    public GameObject gameManager;
    #endregion

    public GameObject ButtonModel;

    // Use this for initialization
    void Start()
    {
        gameManager = GameObject.Find("GameManager");


        Button btn = this.GetComponent<Button>();
        btn.onClick.AddListener(TaskOnClick);
    }

    // Update is called once per frame
    void Update()
    {

    }

    void TaskOnClick()
    {
        gameManager.GetComponent<GameManager>().TempModel = ButtonModel;
        gameManager.GetComponent<GameManager>().OnButtonPressed();
    }
}