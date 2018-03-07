using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using HoloToolkit.Unity;

public class ScreenshotManager : MonoBehaviour {

    private bool isReadyForNextPhoto;
    private Camera camera;
    OnPostRenderCamera onPostRenderCameraScript;

    public GameObject getSourceStat;
    GetControllerStates getControllerStates;

    public GameObject photoPreviewMain;
    public GameObject photoPreview1;
    public GameObject photoPreview2;
    public GameObject photoPreview3;
    public GameObject photoPreview4;

    private bool IsMakingPhoto
    {
        get
        {
            return onPostRenderCameraScript.grab;
        }
        set
        {
            onPostRenderCameraScript.grab = value;
        }
    }

    // Use this for initialization
    void Start () {
        //Assign miising componenets
        if (getSourceStat == null)
            getSourceStat = GameObject.Find("GameManager");

        getControllerStates = getSourceStat.GetComponent<GetControllerStates>();

        if (camera == null)
        {
            camera = Camera.main;
        }

        onPostRenderCameraScript = camera.GetComponent<OnPostRenderCamera>();

        isReadyForNextPhoto = true;
        IsMakingPhoto = onPostRenderCameraScript.grab;

        //************* only for testing******************
        StartCoroutine(TestScreensthotFunction());
        //**************** only for testing **************
    }

    // Update is called once per frame
    void Update () {

        if (getControllerStates.TouchpadPressed && isReadyForNextPhoto)
        {
            StartCoroutine(MakingPhoto());
        }
    }

    public void MakeScreenshot()
    {

    }

    public Texture2D GetInstanceOfRenderedTexture()
    {
        Texture2D tex = Instantiate(Camera.main.GetComponent<OnPostRenderCamera>().renderedTexture) as Texture2D;
        return tex;
    }

    public void UploadTexture(Texture2D tex, GameObject gameObj)
    {
        gameObj.GetComponent<Renderer>().material.mainTexture = tex;
    }

    public string GetScreenshotName()
    {
        return "";
    }

    public void SaveScreenshot(Texture2D tex, string path, string name)
    {

    }

    public void UpdateGalleryImages()
    {

    }

    public string GetGalleryFolderPath()
    {
        return "";
    }

    IEnumerator MakingPhoto()
    {
        IsMakingPhoto = true;
        yield return new WaitForSeconds(1f);
        Texture2D tempTex = GetInstanceOfRenderedTexture();

        //******** Save screenstho to file ***********
        //string screenshotName = GetScreenshotName();
        //string screenshotPath = GetGalleryFolderPath();
        //SaveScreenshot(tempTex, screenshotPath, screenshotName);
        //******** Save screenstho to file ***********

        UploadTexture(tempTex, photoPreviewMain);
        UpdateGalleryImages();
    }


    //***************OnlyForTesting*********************
    IEnumerator TestScreensthotFunction()
    {
        bool test = true;
        int counter = 1;

        while (test)
        {
            yield return new WaitForSeconds(3);
            //IsMakingPhoto = true;
            StartCoroutine(MakingPhoto());
            //onPostRenderCameraScript.grab = true;
            print(counter + " 3 seconds");
            counter++;
        }
    }
    //***************OnlyForTesting*********************

}
