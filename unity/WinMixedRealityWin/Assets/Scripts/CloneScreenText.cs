using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;

public class CloneScreenText : MonoBehaviour {

    public Text clonedText;
    public string displayedTextCloned;
    public GameObject gameManager;
 
    private void Start()
    {
        if (gameManager == null)
        {
            gameManager = GameObject.Find("GameManager");
        }

        if (clonedText == null)
        {
            clonedText = GameObject.Find(transform.name + "/Cloned_Text").GetComponent<Text>();
        }        
    }

    public void CheckStatus ()
    {
        displayedTextCloned = gameManager.GetComponent<GameManager>().displayedText.GetComponent<Text>().text;
        clonedText.text = displayedTextCloned;
    }
}
