using MixedRealityToolkit.InputModule.EventData;
using MixedRealityToolkit.InputModule.InputHandlers;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.XR.WSA;

public class WebviewTrigger : MonoBehaviour, IFocusable, IInputClickHandler {

    private GameObject Webview;
    private Renderer webviewRenderer;
    private Collider webviewCollider;
    public Camera camera;
    public GameObject cursor;
    public Vector3 cursorDefaultScale;
    public Vector3 cursorWebviewScale;

    public Color VisibleCol;
    public Color InvisibleCol;
    public float timeFade = 1.5f;
    private float t = 0;

    public bool isVisibleVW = false;
    private bool isChangedShader = false;

    private bool fadeOutProcess = false;
    private bool fadeInProcess = false;
    private bool isReadyForClick = true;

    // Use this for initialization
    void Start () {

        VisibleCol = Color.white;
        VisibleCol.a = 255f;

        InvisibleCol = Color.white;
        InvisibleCol.a = 0f;


        if (Webview == null)
        {
            Webview = GameObject.Find("Webview");
        }

        webviewRenderer = Webview.GetComponent<Renderer>();
        webviewCollider = Webview.GetComponent<Collider>();

        if (camera == null)
        {
            camera = Camera.main;

        }
        cursor = GameObject.Find("CursorVisual");
        cursorDefaultScale = cursor.transform.localScale;

        webviewRenderer.material.color = InvisibleCol;
    }
	
	// Update is called once per frame
	void Update () {
		
        if (fadeInProcess)
        {
            isReadyForClick = false;

            if (t >= 0)
            {
                fadeInProcess = false;
                isReadyForClick = true;
            }
        }


    }

    public void OnFocusEnter()
    {
        if (!isChangedShader)
        {
            //webviewRenderer.material.shader = Shader.Find("Coherent/TransparentDiffuse");
            isChangedShader = true;
        }
}

    public void OnFocusExit()
    {
    }

    public void OnInputClicked(InputClickedEventData eventData)
    {
        
        isVisibleVW = !isVisibleVW;

        if (isVisibleVW)
        {
            Webview.transform.position = camera.transform.position + new Vector3( 0f, -0.3f, 0.9f); //( 0f, -0.03f, 1.34f); works good ( 0f, -0.06f, 0.9f);
            //Webview.transform.eulerAngles = new Vector3(0f, Camera.main.transform.rotation.y, 0f);
            //Webview.transform.LookAt(Camera.main.transform);

            //StartCoroutine(FadeIn());
            StopCoroutine(FadeOutLerp());
            StartCoroutine(FadeInLerp());
        }
        else
        {
            //StartCoroutine(FadeOut());
            StopCoroutine(FadeInLerp());
            StartCoroutine(FadeOutLerp());
        }
    }
    
    IEnumerator FadeIn()
    {
        webviewCollider.enabled = true;
        Debug.Log("In FadeIn ");
        float alphaMax = 1;
        float alphaStart = 0;
        Color color;
        color = webviewRenderer.material.color;
        color.a = alphaStart;

        while (color.a < alphaMax)
        {
            yield return new WaitForEndOfFrame();
            color.a = color.a + 2f*Time.deltaTime;
            webviewRenderer.material.color = color;

            if (color.a > alphaMax)
            {
                color.a = alphaMax;
                webviewRenderer.material.color = color;
            }                
        }
        color.a = 1;
        webviewRenderer.material.color = color;
    }

    IEnumerator FadeOut()
    {
        webviewCollider.enabled = false;

        Debug.Log("In fadeout");
        float alphaMin = 0;
        float alphaStart = 1;
        Color color;
        color = webviewRenderer.material.color;
        color.a = alphaStart;

        while (color.a > alphaMin)
        {
            yield return new WaitForEndOfFrame();
            color.a = color.a - 2f*Time.deltaTime;
            webviewRenderer.material.color = color;
            Debug.Log("Fade out couroutine");
            if (color.a < alphaMin)
            {
                color.a = alphaMin;
                webviewRenderer.material.color = color;
            }
        }
        Debug.Log("After while");
        color.a = 0;
        webviewRenderer.material.color = color;
    }

    IEnumerator FadeInLerp()
    {
        webviewCollider.enabled = true;
        float _t = 0;
        while (_t <= 1)
        {
            yield return new WaitForEndOfFrame();
            webviewRenderer.material.color = Color.Lerp(InvisibleCol, VisibleCol, _t);
            _t += Time.deltaTime;
            Debug.Log("In Fade In Lerp loop");
            Debug.Log("In fade in lerp coroutint, _t is " + _t);
        }
        webviewRenderer.material.color = VisibleCol;
    }

    IEnumerator FadeOutLerp()
    {
        webviewCollider.enabled = false;
        float _t = 1;
        while (_t >= 0)
        {
            yield return new WaitForEndOfFrame();
            webviewRenderer.material.color = Color.Lerp(InvisibleCol, VisibleCol, _t);
            _t -= Time.deltaTime;
            Debug.Log("In Fade Out Lerp loop");
            Debug.Log("In fade Out lerp coroutint, _t is " + _t);

        }
        webviewRenderer.material.color = InvisibleCol;
    }
}
