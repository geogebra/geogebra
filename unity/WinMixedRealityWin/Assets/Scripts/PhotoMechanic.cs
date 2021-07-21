using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using System.IO;
using HoloToolkit.Unity;


//using UnityEngine.Windows;

// Added after Build from Unity
//using Windows.Storage;

public class PhotoMechanic : MonoBehaviour
{
    // Added after Build from Unity
    //StorageFolder storageFolder = KnownFolders.PicturesLibrary;

    // adding Example from Microsoft ****************************************************************
  //  public static StorageFolder PicturesLibrary { get; } = Windows.Storage.KnownFolders.PicturesLibrary;
    // adding Example from Microsoft ****************************************************************

    public Texture[] textures;
    public static int arrayInt = 1;

    public Texture2D testGalleryTex;
    private string screenshotName = "GalleryImage";

    public GameObject photoPreviewMain;

    public GameObject photoPreview1;
    public GameObject photoPreview2;
    public GameObject photoPreview3;
    public GameObject photoPreview4;

    public GameObject selectedPhoto;

    public float timeNextPhoto;
    public bool readyForNextPhoto;

    public GameObject getSourceStat;
    GetControllerStates getControllerStates;

    private GameObject firstObj;
    private GameObject secondObj;
    private GameObject thirdObj;
    private GameObject fourthObj;

    private string photoTemp;
    private string screen_Shot_File_Name;

    public GameObject gameManager;

    public string fileFolder = "GeoGebraMR";

    public bool testPhotoFromOutside = false;

    // Use this for initialization
    private void Start()
    {

        //assign Game manager
        if (gameManager == null)
        {
            gameManager = GameObject.Find("gameManager");
        }

        //Create GeoGebraMR folder if not exist

        // changing "Application.dataPath" for "PicturesLibrary"
        if (!Directory.Exists(Application.dataPath + "/GeoGebraMR"))
        {
            Directory.CreateDirectory(Application.dataPath + "/GeoGebraMR");
        }
        
        /* 
        if (!Directory.Exists(storageFolder + "/GeoGebraMR"))
        {
            Directory.CreateDirectory(storageFolder + "/GeoGebraMR");
        }
        */

        //Test the correct file folder
        //Debug.Log("File folder for saving pictures is " + Application.dataPath);
        //Debug.Log("File folder for saving pictures is " + Application.dataPath);

        List <GameObject> PhotoPreviewList = new List<GameObject>();

        PhotoPreviewList.Add(photoPreview1);
        PhotoPreviewList.Add(photoPreview2);
        PhotoPreviewList.Add(photoPreview3);
        PhotoPreviewList.Add(photoPreview4);

        readyForNextPhoto = true;

        if (getSourceStat == null)
            getSourceStat = GameObject.Find("GameManager");

        getControllerStates = getSourceStat.GetComponent<GetControllerStates>();       
    }

    public void GlobalMakePhoto()
    {
        StartCoroutine(UpScrnCoroutine());
    }

    // Update is called once per frame
    void Update()
    {     
        if (getControllerStates.TouchpadPressed && readyForNextPhoto)
        {
            StartCoroutine(UpScrnCoroutine());
        }

        //only for testing ********
        if (testPhotoFromOutside == true)
        {
            StartCoroutine(UpScrnCoroutine());
            testPhotoFromOutside = false;
        }
        //***********
    }

    string GetTemporaryFolder()
    {

#if ENABLE_WINMD_SUPPORT
         return Windows.Storage.KnownFolders.PicturesLibrary.ToString();
#else
        return "LocalFolder";
#endif
    }

    IEnumerator UpScrnCoroutine()
    {
        readyForNextPhoto = false;
        MakePhoto();

        yield return new WaitForSeconds(1f);
        UplodadScreenshots();

        yield return new WaitForSeconds(1f);
        //LoadTexture();

        yield return new WaitForEndOfFrame();
        readyForNextPhoto = true;
    }

    public void MakePhoto()
    {
        //Assign current ModelName from workspace to string for Name of model.
        string objName;

        if (gameManager.GetComponent<GameManager>().tempModel != null)
        {
            objName = gameManager.GetComponent<GameManager>().tempModel.name;
            print("objName is " + objName.ToString().ToUpper());

            screen_Shot_File_Name = objName + arrayInt + System.DateTime.Now.ToString("_yyyy-MM-dd-HHmmss") + ".png";
        }
        else
        {
            screen_Shot_File_Name = arrayInt + System.DateTime.Now.ToString("_yyyy-MM-dd-HHmmss") + ".png";
        }

        ScreenCapture.CaptureScreenshot(Application.dataPath + "/GeoGebraMR/" + screen_Shot_File_Name);

        print("end of MakPhoto function");
        arrayInt++;
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
        photoPreviewMain.GetComponent<Renderer>().material.mainTexture = selected.GetComponent<Renderer>().material.mainTexture;
    }

    public void UplodadScreenshots()
    {
        int NumOfScreen = arrayInt;

        firstObj = photoPreview1;
        secondObj = photoPreview2;
        thirdObj = photoPreview3;
        fourthObj = photoPreview4;

        // Swaping textures
        /*
        photoPreview4.GetComponent<Renderer>().material.mainTexture = 
            photoPreview3.GetComponent<Renderer>().material.mainTexture;

        photoPreview3.GetComponent<Renderer>().material.mainTexture =
             photoPreview2.GetComponent<Renderer>().material.mainTexture;

        photoPreview2.GetComponent<Renderer>().material.mainTexture =
            photoPreview1.GetComponent<Renderer>().material.mainTexture;

       //Making photo
                photoPreview1.GetComponent<Renderer>().material.mainTexture =
                LoadPNG(Application.dataPath + "/GeoGebraMR/" + screen_Shot_File_Name);

        PhotoSelection(firstObj);
        */
        photoPreview4.GetComponent<Renderer>().material = new Material(photoPreview3.GetComponent<Renderer>().material);
        photoPreview3.GetComponent<Renderer>().material = new Material(photoPreview2.GetComponent<Renderer>().material);
        photoPreview2.GetComponent<Renderer>().material = new Material(photoPreview1.GetComponent<Renderer>().material);
        photoPreview1.GetComponent<Renderer>().material = new Material(photoPreviewMain.GetComponent<Renderer>().material);
    }

    //Gallery Image Selesction from Game
    public void SelectItem(GameObject gameObject)
    {
        switch (gameObject.name)
        {
            case "Image1":
                PhotoSelection(firstObj);
                break;

            case "Image2":
                PhotoSelection(secondObj);
                break;

            case "Image3":
                PhotoSelection(thirdObj);
                break;

            case "Image4":
                PhotoSelection(fourthObj);
                break;
        }
    }    
}