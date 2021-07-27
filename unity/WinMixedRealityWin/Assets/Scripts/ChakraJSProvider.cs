using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;

public class ChakraJSProvider : MonoBehaviour {

    public Text jsInput;
    public Text jsOutput;

    public GameObject gameManager;
    public JsManager jsManager;

	// Use this for initialization
	void Start () {
        #region Assigning Missing Components
        if (gameManager == null) {
            gameManager = GameObject.Find("GameManager");
        }

        if (jsManager == null) {
            jsManager = gameManager.GetComponent<JsManager>();
        }

        if (jsInput == null)
        {
            jsInput = GameObject.Find("JsInputText").GetComponent<Text>();
        }

        if (jsOutput == null)
        {
            jsOutput = GameObject.Find("JsOutputText").GetComponent<Text>();
        }
        #endregion
	}

    public void ExecuteJsButton()
    {
        jsOutput.text = jsManager.Execute(jsInput.text);
    }
}
