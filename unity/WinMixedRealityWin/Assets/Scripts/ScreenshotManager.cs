using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using HoloToolkit.Unity;
using UnityEngine.XR.WSA.Input;
using HoloToolkit.Unity.Controllers;
using HoloToolkit.Unity.InputModule;


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

    private Dictionary<uint, ControllerState> controllers;

    private bool isReadyForNextPhoto = true;
    private Camera camera;
    OnPostRenderCamera onPostRenderCameraScript;

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

        if (camera == null)
        {
            camera = Camera.main;
        }

        onPostRenderCameraScript = camera.GetComponent<OnPostRenderCamera>();

        isReadyForNextPhoto = true;
        IsMakingPhoto = onPostRenderCameraScript.grab;
    }

    // Update is called once per frame
    void Update () {
    }

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
        return "";
    }

    public void SaveScreenshot(Texture2D tex, string path, string name)
    {

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

    public string GetGalleryFolderPath()
    {
        return "";
    }

    IEnumerator MakingPhoto()
    {
            isReadyForNextPhoto = false;
            IsMakingPhoto = true;
            yield return new WaitForEndOfFrame();
            Texture2D tempTex = GetInstanceOfRenderedTexture();

            //******** Save screenstho to file ***********
            //string screenshotName = GetScreenshotName();
            //string screenshotPath = GetGalleryFolderPath();
            //SaveScreenshot(tempTex, screenshotPath, screenshotName);
            //******** Save screenstho to file ***********

            UploadTexture(tempTex, photoPreviewMain);
            UpdateGalleryImages();
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
}