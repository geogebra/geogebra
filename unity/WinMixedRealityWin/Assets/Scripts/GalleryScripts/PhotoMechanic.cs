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
    //public GameObject PhotoPreview5;

    public GameObject SelectedPhoto;

    public float TimeNextPhoto;
    public bool ReadyForNextPhoto;

    public GameObject GetSourceStat;
    GetControllerStates getControllerStates;

    private GameObject First;
    private GameObject Second;
    private GameObject Third;
    private GameObject Fourth;



    // Use this for initialization
    IEnumerator Start()
    {
        List<GameObject> PhotoPreviewList = new List<GameObject>();

        PhotoPreviewList.Add(PhotoPreview1);
        PhotoPreviewList.Add(PhotoPreview2);
        PhotoPreviewList.Add(PhotoPreview3);
        PhotoPreviewList.Add(PhotoPreview4);
        //PhotoPreviewList.Add(PhotoPreview5);

        ReadyForNextPhoto = true;

        if (GetSourceStat == null)
            GetSourceStat = GameObject.Find("GameManager");

        getControllerStates = GetSourceStat.GetComponent<GetControllerStates>();


        yield return UploadPNG();

    }

    // Update is called once per frame
    void Update()
    {


        if ((getControllerStates.TouchpadPressed || getControllerStates.SelectPressed) && ReadyForNextPhoto)
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
        ScreenCapture.CaptureScreenshot(Application.dataPath + "/" + screenshotName + ArrayInt.ToString() + ".png" );
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

        First = PhotoPreview1;
        Second = PhotoPreview2;
        Third = PhotoPreview3;
        Fourth = PhotoPreview4;

     
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


        PhotoSelection(First);


    }


    // Take a shot immediately


    IEnumerator UploadPNG()
    {
        // We should only read the screen buffer after rendering is complete
        yield return new WaitForEndOfFrame();

        // Create a texture the size of the screen, RGB24 format
        int width = Screen.width;
        int height = Screen.height/4;

        print("Screen.width" + Screen.width);
        print("Screen.height" + Screen.height);

        //int width = 1920;
        //int height = 1080;
        Texture2D tex = new Texture2D(width, height, TextureFormat.RGB24, false);

        // Read screen contents into the texture
        tex.ReadPixels(new Rect(0, 0, width, height), 0, 0);
        tex.Apply();

        // Encode texture into PNG
        byte[] bytes = tex.EncodeToPNG();
        Object.Destroy(tex);

        // For testing purposes, also write to a file in the project folder
         File.WriteAllBytes(Application.dataPath + "/" + "SavedScreen.png", bytes);
        print("ScreenshotSaved");
    }

    public void SelectItem(GameObject gameObject)
    {
        switch (gameObject.name)
        {
            case "Image1":
                PhotoSelection(First);
                break;

            case "Image2":
                PhotoSelection(Second);
                break;

            case "Image3":
                PhotoSelection(Third);
                break;

            case "Image4":
                PhotoSelection(Fourth);
                break;
        }
    }






}
