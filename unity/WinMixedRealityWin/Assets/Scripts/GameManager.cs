using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;
using UnityEngine.XR.WSA.Input;


public class GameManager : MonoBehaviour {

    #region Members
    [HideInInspector]
    public CloneScreenText cloneScreenText;
    public GameObject cloneScreen;
    #endregion

    //Models to show
    [Header("Models")]
    [Space(5)]
    public GameObject basicModel;
    public GameObject footballModel;
    public GameObject functionModel;
    public GameObject kleinModel;
    public GameObject lissajousModel;
    public GameObject penroseModel;
    public GameObject ruledSurfaceModel;
    public GameObject sierpinskiModel;
    public GameObject tempModel;

    //Spawner point
    [Header("Spawn Point")]
    [Space(5)]
    public GameObject modelSpawner;

    [Header("Temp text")]
    [Space(5)]
    public GameObject tempText;
    public GameObject tempTextSecondScreen;
    public GameObject displayedText;

	// Use this for initialization
	void Start () {
        #region Find reference in case of missing referenece

        // Models
        if (basicModel == null)
            basicModel = GameObject.Find("Basic");

        if (footballModel == null)
            footballModel = GameObject.Find("Football");

        if (functionModel == null)
            functionModel = GameObject.Find("Function");

        if (kleinModel == null)
            kleinModel = GameObject.Find("Klein");

        if (lissajousModel == null)
            lissajousModel = GameObject.Find("Lissajous");

        if (penroseModel == null)
            penroseModel = GameObject.Find("Penrose");

        if (ruledSurfaceModel == null)
            ruledSurfaceModel = GameObject.Find("RuledSurface");

        if (sierpinskiModel == null)
            sierpinskiModel = GameObject.Find("Sierpinski");

        // SPAWN POINT
        if (modelSpawner == null)
            modelSpawner = GameObject.Find("ModelsSpawner");

        // Second Screen
        if (tempTextSecondScreen == null)
            tempTextSecondScreen = GameObject.Find("SecondScreenCanvas");

        // Clone screen
        if (cloneScreen == null)
            cloneScreen = GameObject.Find("SecondScreenCanvas");

        #endregion
    }
    
    public void OnButtonPressed ()
    {
        //Destroy all GameObjects with tag "Model"
        GameObject[] gameObjects;  
        gameObjects = GameObject.FindGameObjectsWithTag("Model");
        for (var i = 0; i < gameObjects.Length; i++)
        {
            Destroy(gameObjects[i]);
        }
        
        // Create GameObject
        Instantiate(tempModel, modelSpawner.transform.localPosition, Quaternion.identity);

        // Change Text on the screen
        if (displayedText == null)
        {
            displayedText = tempText;
            displayedText.GetComponent<Text>().enabled = true;
        } else
        {
            displayedText.GetComponent<Text>().enabled = false;
            displayedText = tempText;
            displayedText.GetComponent<Text>().enabled = true;
        }

        //Activate scecond screen
        cloneScreen.GetComponent<CloneScreenText>().CheckStatus();
        //print("the current model is " + TempModel);
    }

    public void CheckForItem(string ModelName)
    {
        Debug.Log(ModelName);
    }
}
