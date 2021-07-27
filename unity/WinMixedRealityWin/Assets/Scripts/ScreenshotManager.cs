using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using HoloToolkit.Unity;
using UnityEngine.XR.WSA.Input;
using HoloToolkit.Unity.Controllers;
using System.IO;
using System;

#if ENABLE_WINMD_SUPPORT
using Windows.Storage;
#endif


public class ScreenshotManager : MonoBehaviour {

    private class ControllerState
    {
        public InteractionSourceHandedness Handedness;
        public Vector3 PointerPosition;
        public Quaternion PointerRotation;
        public Vector3 GripPosition;
        public Quaternion GripRotation;
        public bool Grasped;
        public bool MenuPressed;
        public bool SelectPressed;
        public float SelectPressedAmount;
        public bool ThumbstickPressed;
        public Vector2 ThumbstickPosition;
        public bool TouchpadPressed;
        public bool TouchpadTouched;
        public Vector2 TouchpadPosition;
    }

#if !UNITY_EDITOR
    bool haveFolderPath = false;
    StorageFolder picturesFolder;
    string tempFilePathAndName;
    string tempFileName;
    string pictureFolderPath;
#endif

    private Dictionary<uint, ControllerState> controllers;

    private bool isReadyForNextPhoto = true;
    private Camera camera;
    OnPostRenderCamera onPostRenderCameraScript;

    public GameObject photoPreviewMain;
    public GameObject photoPreview1;
    public GameObject photoPreview2;
    public GameObject photoPreview3;
    public GameObject photoPreview4;

    public GameObject gameManager;

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
        #region Assigning missing reference
        if (gameManager == null)
        {
            gameManager = GameObject.Find("GameManager");
        }
        if (camera == null)
        {
            camera = Camera.main;
        }
        if (photoPreviewMain == null)
            photoPreviewMain = GameObject.Find("Image_Main");
        if (photoPreview1 == null)
            photoPreview1 = GameObject.Find("Image1");
        if (photoPreview2 == null)
            photoPreview2 = GameObject.Find("Image2");
        if (photoPreview3 == null)
            photoPreview3 = GameObject.Find("Image3");
        if (photoPreview4 == null)
            photoPreview4 = GameObject.Find("Image4");

        #endregion

        onPostRenderCameraScript = camera.GetComponent<OnPostRenderCamera>();

        isReadyForNextPhoto = true;
        IsMakingPhoto = onPostRenderCameraScript.grab;

#if ENABLE_WINMD_SUPPORT
        StartCoroutine(syncFolderPath());
#endif
    }

    // Update is called once per frame
    void Update () {
    }

#if ENABLE_WINMD_SUPPORT

    async void getFolderPath()
    {
        StorageLibrary myPictures = await Windows.Storage.StorageLibrary.GetLibraryAsync(Windows.Storage.KnownLibraryId.Pictures);
        picturesFolder = myPictures.SaveFolder;
        pictureFolderPath = picturesFolder.Path;
    }
#endif

#region InteractionSource
    private void Awake()
    {
#if UNITY_WSA && UNITY_2017_2_OR_NEWER
        controllers = new Dictionary<uint, ControllerState>();

        InteractionManager.InteractionSourceDetected += InteractionManager_InteractionSourceDetected;
        InteractionManager.InteractionSourceLost += InteractionManager_InteractionSourceLost;
        InteractionManager.InteractionSourceUpdated += InteractionManager_InteractionSourceUpdated;
#endif
    }

    private void InteractionManager_InteractionSourceDetected(InteractionSourceDetectedEventArgs obj)
    {
        //Debug.LogFormat("{0} {1} Detected", obj.state.source.handedness, obj.state.source.kind);

        if (obj.state.source.kind == InteractionSourceKind.Controller && !controllers.ContainsKey(obj.state.source.id))
        {
            controllers.Add(obj.state.source.id, new ControllerState { Handedness = obj.state.source.handedness });
        }
    }

    private void InteractionManager_InteractionSourceLost(InteractionSourceLostEventArgs obj)
    {
        Debug.LogFormat("{0} {1} Lost", obj.state.source.handedness, obj.state.source.kind);

        controllers.Remove(obj.state.source.id);
    }

    private void InteractionManager_InteractionSourceUpdated(InteractionSourceUpdatedEventArgs obj)
    {
        ControllerState controllerState;
        if (controllers.TryGetValue(obj.state.source.id, out controllerState))
        {
            obj.state.sourcePose.TryGetPosition(out controllerState.PointerPosition, InteractionSourceNode.Pointer);
            obj.state.sourcePose.TryGetRotation(out controllerState.PointerRotation, InteractionSourceNode.Pointer);
            obj.state.sourcePose.TryGetPosition(out controllerState.GripPosition, InteractionSourceNode.Grip);
            obj.state.sourcePose.TryGetRotation(out controllerState.GripRotation, InteractionSourceNode.Grip);

            controllerState.Grasped = obj.state.grasped;
            controllerState.MenuPressed = obj.state.menuPressed;
            controllerState.SelectPressed = obj.state.selectPressed;
            controllerState.SelectPressedAmount = obj.state.selectPressedAmount;
            controllerState.ThumbstickPressed = obj.state.thumbstickPressed;
            controllerState.ThumbstickPosition = obj.state.thumbstickPosition;
            controllerState.TouchpadPressed = obj.state.touchpadPressed;
            controllerState.TouchpadTouched = obj.state.touchpadTouched;
            controllerState.TouchpadPosition = obj.state.touchpadPosition;
        }

        if (controllerState.TouchpadPressed && isReadyForNextPhoto)
        {
            IsMakingPhoto = true;
            print("Touchpad touched");
            StartCoroutine(MakingPhoto());
        }
    }

    protected void OnAttachToController()
    {
        InteractionManager.InteractionSourceUpdated += InteractionSourceUpdated;
    }

    private void InteractionSourceUpdated(InteractionSourceUpdatedEventArgs obj)
    {
        print("interactionSourceUpdated - we are inside");
        if(obj.state.touchpadTouched)
        {
            print("Inside if(obj.state.touchpadTouched)");
            IsMakingPhoto = true;
        }
    }

#endregion

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
        print("in screenshotname");
        string objName;
        string screenshotName;
        string titleName = "GeoGebraMR";

        if (gameManager.GetComponent<GameManager>().tempModel != null)
        {
            objName = gameManager.GetComponent<GameManager>().tempModel.name;
            print("objName is " + objName.ToString().ToUpper());
            screenshotName = titleName + "_" + objName + System.DateTime.Now.ToString("_yyyy-MM-dd-HHmmss") + ".png";
        }
        else
        {
            screenshotName = titleName + System.DateTime.Now.ToString("_yyyy-MM-dd-HHmmss") + ".png";
        }
        return screenshotName;
    }

    public void SaveScreenshot(Texture2D tex, string path, string name)
    {
        string allPath = path + name;
        byte[] bytes = tex.EncodeToPNG();
        File.WriteAllBytes(allPath, bytes);
    }

    public void UpdateGalleryImages()
    {
        photoPreview4.GetComponent<Renderer>().material = new Material(photoPreview3.GetComponent<Renderer>().material);
        photoPreview3.GetComponent<Renderer>().material = new Material(photoPreview2.GetComponent<Renderer>().material);
        photoPreview2.GetComponent<Renderer>().material = new Material(photoPreview1.GetComponent<Renderer>().material);
        photoPreview1.GetComponent<Renderer>().material = new Material(photoPreviewMain.GetComponent<Renderer>().material);
    }

    public void SelectPhotoInGallery(GameObject selected)
    {
        photoPreviewMain.GetComponent<Renderer>().material.mainTexture = selected.GetComponent<Renderer>().material.mainTexture;
    }
    #if UNITY_EDITOR
    public string GetGalleryFolderPath()
    {
        string picturePath = System.Environment.GetFolderPath(System.Environment.SpecialFolder.MyPictures);
        Debug.Log(picturePath);
        return picturePath;
    }
    #endif

    IEnumerator MakingPhoto()
    {
        // Uploading Gallery in the scene
        isReadyForNextPhoto = false;
        IsMakingPhoto = true;
        yield return new WaitForEndOfFrame();
        Texture2D tempTex = GetInstanceOfRenderedTexture();
        UploadTexture(tempTex, photoPreviewMain);
        UpdateGalleryImages();

        // Saving screenshot
#if ENABLE_WINMD_SUPPORT
        string screenshotPath = pictureFolderPath.Replace(@"\", "/");
#else
        string screenshotPath = GetGalleryFolderPath();
#endif
        string screenshotName = GetScreenshotName();
        SaveScreenshot(tempTex, screenshotPath + "/", screenshotName);

        yield return new WaitForSeconds(1);
        isReadyForNextPhoto = true;
    }

    public void SelectItem(GameObject gameObject)
    {
        GameObject firstObj = photoPreview1;
        GameObject secondObj = photoPreview2;
        GameObject thirdObj = photoPreview3;
        GameObject fourthObj = photoPreview4;

        switch (gameObject.name)
        {
            case "Image1":
                SelectPhotoInGallery(firstObj);
                break;

            case "Image2":
                SelectPhotoInGallery(secondObj);
                break;

            case "Image3":
                SelectPhotoInGallery(thirdObj);
                break;

            case "Image4":
                SelectPhotoInGallery(fourthObj);
                break;
        }
    }

#if ENABLE_WINMD_SUPPORT
    IEnumerator syncFolderPath()
    {
        bool loop = true;
        while (loop)
        {
            getFolderPath();
            yield return new WaitForSeconds(5);
        }
    }
#endif
}