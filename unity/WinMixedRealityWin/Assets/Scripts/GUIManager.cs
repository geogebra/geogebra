using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;

public class GUIManager : MonoBehaviour {

    #region Members
    [HideInInspector]
    public GameManager GameManager;
    GameObject PopUpMenu; 
    #endregion


    // Use this for initialization
    void Start () {
        PopUpMenu = GameObject.Find("PopUpMenu");
        GameObject PlusButton = GameObject.Find("PlusButton");

        PopUpMenu.SetActive(false);

        PlusButton.GetComponent<Button>().onClick.AddListener(PopUpMenuClick);
        
	}
	
	// Update is called once per frame
	void Update () {
		
	}

    void PopUpMenuClick()
    {
        Debug.Log("In Pop UP Menu Function");
        PopUpMenu.SetActive(!PopUpMenu.activeInHierarchy);
        
    }
}
