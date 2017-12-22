using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;
using UnityEngine.XR.WSA.Input;
using HoloToolkit.Unity.InputModule;


public class GameManager : MonoBehaviour {

    #region Members
    [HideInInspector]
    public CloneScreenText cloneScreenText;
    public GameObject cloneScreen;
    #endregion



    public GameObject BasicModel;
    public GameObject FootballModel;
    public GameObject FunctionModel;
    public GameObject KleinModel;
    public GameObject LissajousModel;
    public GameObject PenroseModel;
    public GameObject RuledSurfaceModel;
    public GameObject SierpinskiModel;
    public GameObject TempModel;

    public GameObject ModelSpawner;

    public GameObject TempText;
    public GameObject TempTextSecondScreen;
    public GameObject DisplayedText;

	// Use this for initialization
	void Start () {

        #region Find reference in case of missing referenece

        // Models
        if (BasicModel == null)
            BasicModel = GameObject.Find("Basic");

        if (FootballModel == null)
            FootballModel = GameObject.Find("Football");

        if (FunctionModel == null)
            FunctionModel = GameObject.Find("Function");

        if (KleinModel == null)
            KleinModel = GameObject.Find("Klein");

        if (LissajousModel == null)
            LissajousModel = GameObject.Find("Lissajous");

        if (PenroseModel == null)
            PenroseModel = GameObject.Find("Penrose");

        if (RuledSurfaceModel == null)
            RuledSurfaceModel = GameObject.Find("RuledSurface");

        if (SierpinskiModel == null)
            SierpinskiModel = GameObject.Find("Sierpinski");



        // SPAWN POINT
        if (ModelSpawner == null)
            ModelSpawner = GameObject.Find("ModelsSpawner");


        // Second Screen
        if (TempTextSecondScreen == null)
            TempTextSecondScreen = GameObject.Find("SecondScreenCanvas");


        #endregion

    }

    // Update is called once per frame
    void Update () {
     

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
        Instantiate(TempModel, ModelSpawner.transform.localPosition, Quaternion.identity);


        // Change Text on the screen
        if (DisplayedText == null)
        {
            DisplayedText = TempText;
            DisplayedText.GetComponent<Text>().enabled = true;
        } else
        {
            DisplayedText.GetComponent<Text>().enabled = false;
            DisplayedText = TempText;
            DisplayedText.GetComponent<Text>().enabled = true;
        }

        //Activate sceond screen
        cloneScreen.GetComponent<CloneScreenText>().CheckStatus();

    }

    public void CheckForItem(string ModelName)
    {
        Debug.Log(ModelName);
    }

    public void MakeScreenshot ()
    {

    }

    

}
