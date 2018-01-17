using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using System.IO;
using HoloToolkit.Unity;
//using UnityEngine.Windows;




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

    public GameObject SelectedPhoto;

    public float TimeNextPhoto;
    public bool ReadyForNextPhoto;

    public GameObject GetSourceStat;
    GetControllerStates getControllerStates;

    private GameObject First;
    private GameObject Second;
    private GameObject Third;
    private GameObject Fourth;

    private string photoTemp;
    private string screen_Shot_File_Name;

    public GameObject gameManager;

    public string FileFolder = "GeoGebraMR";


    // Use this for initialization
    private void Start()
    {
        //assign Game manager
        if (gameManager == null)
        {
            gameManager = GameObject.Find("gameManager");
        }

        //Create GeoGebraMR folder if not exist

        if (!Directory.Exists(Application.dataPath + "/GeoGebraMR"))
        {
            Directory.CreateDirectory(Application.dataPath + "/GeoGebraMR");
        }


        //Test the correct file folder
        Debug.Log("File folder for saving pictures is " + Application.dataPath);

        List <GameObject> PhotoPreviewList = new List<GameObject>();

        PhotoPreviewList.Add(PhotoPreview1);
        PhotoPreviewList.Add(PhotoPreview2);
        PhotoPreviewList.Add(PhotoPreview3);
        PhotoPreviewList.Add(PhotoPreview4);

        ReadyForNextPhoto = true;

        if (GetSourceStat == null)
            GetSourceStat = GameObject.Find("GameManager");

        getControllerStates = GetSourceStat.GetComponent<GetControllerStates>();       
    }

    // Update is called once per frame
    void Update()
    {     
        if (getControllerStates.TouchpadPressed && ReadyForNextPhoto)
        {
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
        //Assign current ModelName from workspace to string for Name of model.
        string objName;

        if (gameManager.GetComponent<GameManager>().TempModel != null)
        {
            objName = gameManager.GetComponent<GameManager>().TempModel.name;
            print("objName is " + objName.ToString().ToUpper());

            screen_Shot_File_Name = objName + ArrayInt + System.DateTime.Now.ToString("_yyyy-MM-dd-HHmmss") + ".png";
        } else
        {
            screen_Shot_File_Name = ArrayInt + System.DateTime.Now.ToString("_yyyy-MM-dd-HHmmss") + ".png";
        }



        ScreenCapture.CaptureScreenshot(Application.dataPath + "/GeoGebraMR/" + screen_Shot_File_Name);

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


        // Swaping textures
        PhotoPreview4.GetComponent<Renderer>().material.mainTexture = 
            PhotoPreview3.GetComponent<Renderer>().material.mainTexture;

        PhotoPreview3.GetComponent<Renderer>().material.mainTexture =
             PhotoPreview2.GetComponent<Renderer>().material.mainTexture;

        PhotoPreview2.GetComponent<Renderer>().material.mainTexture =
            PhotoPreview1.GetComponent<Renderer>().material.mainTexture;

       //Making photo
                PhotoPreview1.GetComponent<Renderer>().material.mainTexture =
                LoadPNG(Application.dataPath + "/GeoGebraMR/" + screen_Shot_File_Name);

        PhotoSelection(First);
    }


    //Gallery Image Selesction from Game
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
