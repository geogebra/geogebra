using System.Collections;
using System.Collections.Generic;
using UnityEngine;
//using UnityEngine.UI;
using System.IO;
using HoloToolkit.Unity;

public class PhotoMechanic : MonoBehaviour
{

    public Texture[] Textures;
    public static int ArrayInt = 1;

    public Texture2D TestGalleryTex;
    private string screenshotName = "GalleryImage";

    public GameObject PhotoPreviewMain;

    public GameObject PhotoPreview1;
    public GameObject PhotoPreview2;
    public GameObject PhotoPreview3;
    public GameObject PhotoPreview4;
    public GameObject PhotoPreview5;

    public GameObject SelectedPhoto;

    public float TimeNextPhoto;
    public bool ReadyForNextPhoto;

    public GameObject GetSourceStat;
    GetControllerStates getControllerStates;




    // Use this for initialization
    void Start()
    {
        List<GameObject> PhotoPreviewList = new List<GameObject>();

        PhotoPreviewList.Add(PhotoPreview1);
        PhotoPreviewList.Add(PhotoPreview2);
        PhotoPreviewList.Add(PhotoPreview3);
        PhotoPreviewList.Add(PhotoPreview4);
        PhotoPreviewList.Add(PhotoPreview5);

        ReadyForNextPhoto = true;

        if (GetSourceStat == null) 
            GetSourceStat = GameObject.Find("GameManager");

        getControllerStates = GetSourceStat.GetComponent<GetControllerStates>();



    }

    // Update is called once per frame
    void Update()
    {


        if (getControllerStates.SelectPressed && ReadyForNextPhoto)
        {
            print("SelectPressed");
            StartCoroutine(UpScrnCoroutine());
        }

        if (Input.GetKeyDown(KeyCode.Space))
        {
            print("SelectPressed");
            StartCoroutine(UpScrnCoroutine());
        }

    }

    IEnumerator UpScrnCoroutine()
    {
        ReadyForNextPhoto = false;
        MakePhoto();

        yield return new WaitForSeconds(1f);
        UplodadScreenshots();

        yield return new WaitForSeconds(1f);
        //LoadTexture();

        yield return new WaitForEndOfFrame();
        ReadyForNextPhoto = true;
    }


    public void MakePhoto()
    {
        ScreenCapture.CaptureScreenshot(Application.dataPath + "/" + screenshotName + ArrayInt.ToString() + ".png");
        print("end of MakPhoto function");
        ArrayInt++;

        Camera.main.Render();


    }

 


    //Method for Loading Texture
    public static Texture2D LoadPNG(string filePath)
    {
        Texture2D tex = null;
        byte[] fileData;

        if (File.Exists(filePath))
        {
            fileData = File.ReadAllBytes(filePath);
            tex = new Texture2D(2, 2);
            tex.LoadImage(fileData); //..this will auto-resize the texture dimensions.
        }
        return tex;
    }

    public void PhotoSelection (GameObject selected)
    {
        PhotoPreviewMain.GetComponent<Renderer>().material.mainTexture = selected.GetComponent<Renderer>().material.mainTexture;
    }

    public void UplodadScreenshots()
    {
        int NumOfScreen = ArrayInt;

        GameObject First = PhotoPreview1;
        GameObject Second = PhotoPreview2;
        GameObject Third = PhotoPreview3;
        GameObject Fourth = PhotoPreview4;
        GameObject Fivth = PhotoPreview5;

        
        

        TestGalleryTex = LoadPNG(Application.dataPath + "/" + screenshotName + ArrayInt.ToString() + ".png");
        //PhotoPreviewMain.GetComponent<Renderer>().material.mainTexture = TestGalleryTex;
        NumOfScreen -= 1;
        print("NumOfScreen = " + NumOfScreen);

        if (NumOfScreen > 0)
        {
            PhotoPreview1.GetComponent<Renderer>().material.mainTexture =
                LoadPNG(Application.dataPath + "/" + screenshotName + NumOfScreen.ToString() + ".png");

            NumOfScreen -= 1;
            print("In the First if /n NumOfScreen = " + NumOfScreen);
        }

        if (NumOfScreen > 0)
        {
            PhotoPreview2.GetComponent<Renderer>().material.mainTexture =
                LoadPNG(Application.dataPath + "/" + screenshotName + NumOfScreen.ToString() + ".png");

            NumOfScreen -= 1;
            print("In the Second if /n NumOfScreen = " + NumOfScreen);

        }

        if (NumOfScreen > 0)
        {
            PhotoPreview3.GetComponent<Renderer>().material.mainTexture =
                LoadPNG(Application.dataPath + "/" + screenshotName + NumOfScreen.ToString() + ".png");

            NumOfScreen -= 1;
            print("In the Third if /n NumOfScreen = " + NumOfScreen);

        }

        if (NumOfScreen > 0)
        {
            PhotoPreview4.GetComponent<Renderer>().material.mainTexture =
                LoadPNG(Application.dataPath + "/" + screenshotName + NumOfScreen.ToString() + ".png");
            NumOfScreen -= 1;
            print("In the Fourth if /n NumOfScreen = " + NumOfScreen);
        }

        if (NumOfScreen > 0)
        {
            PhotoPreview5.GetComponent<Renderer>().material.mainTexture =
                LoadPNG(Application.dataPath + "/" + screenshotName + NumOfScreen.ToString() + ".png");
            NumOfScreen -= 1;
            print("In the Fivth if /n NumOfScreen = " + NumOfScreen);

        }

        PhotoSelection(First);


    }






}
