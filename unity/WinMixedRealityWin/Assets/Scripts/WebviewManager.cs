using System.Collections;
using System.Collections.Generic;
using UnityEngine.XR.WSA;
using UnityEngine;
using MixedRealityToolkit.InputModule.EventData;
using MixedRealityToolkit.InputModule.InputHandlers;

public class WebviewManager : MonoBehaviour, IFocusable, IInputClickHandler
{

    public static bool isWebviewVisible = true;

    public GameObject GGBApplet;
    public GameObject Webview;
    public GameObject Cross;
    public Renderer webviewRenderer;
    public MeshCollider webviewCollider;
    public MeshCollider crossCollider;
    public Renderer crossRenderer;
    public Camera camera;

    public Color VisibleCol;
    public Color InvisibleCol;

    private bool isChangedShader = false;

    // Use this for initialization
    void Start()
    {
        VisibleCol = Color.white;
        VisibleCol.a = 255f;

        InvisibleCol = Color.white;
        InvisibleCol.a = 0f;

        if (GGBApplet == null)
        {
            GGBApplet = GameObject.Find("GGBApplet");
        }
        if (Webview == null)
        {
            Webview = GameObject.Find("Webview");
        }
        if (Cross == null)
        {
            Cross = GameObject.Find("Cross");
        }
        webviewRenderer = Webview.GetComponent<Renderer>();
        webviewCollider = Webview.GetComponent<MeshCollider>();
        crossRenderer = Cross.GetComponent<Renderer>();
        crossCollider = Cross.GetComponent<MeshCollider>();


        if (camera == null)
        {
            camera = Camera.main;

        }      
    }

    // Update is called once per frame
    void Update()
    {

    }

    public void OnFocusEnter()
    {
        if (!isChangedShader)
        {
            webviewRenderer.material.shader = Shader.Find("Coherent/TransparentDiffuse");
            webviewRenderer.material.color = InvisibleCol;
            isChangedShader = true;
        }
    }

    public void OnFocusExit()
    {
    }

    public void OnInputClicked(InputClickedEventData eventData)
    {
        isWebviewVisible = !isWebviewVisible;

        if (isWebviewVisible)
        {
            AppearWebview();
        }
        else
        {
            DissapperWebview();
        }
    }

    public void AppearWebview()
    {
        webviewCollider.enabled = true;
        crossCollider.enabled = true;

        GGBApplet.transform.position = camera.transform.position;
        GGBApplet.transform.rotation = camera.transform.rotation;
        Vector3 v = GGBApplet.transform.rotation.eulerAngles;
        GGBApplet.transform.rotation = Quaternion.Euler(0, v.y, v.z);

        StartCoroutine(FadeIn());
    }

    public void DissapperWebview()
    {
        webviewCollider.enabled = false;
        crossCollider.enabled = false;
        StartCoroutine(FadeOut());
    }

    IEnumerator FadeIn()
    {
        StopCoroutine(FadeOut());
        float alphaMax = 1;
        float alphaStart = 0;
        Color color;
        color = webviewRenderer.material.color;
        color.a = alphaStart;

        while (color.a < alphaMax)
        {
            yield return new WaitForEndOfFrame();
            color.a = color.a + 2f * Time.deltaTime;
            webviewRenderer.material.color = color;
            crossRenderer.material.color = color;

            if (color.a > alphaMax)
            {
                color.a = alphaMax;
                webviewRenderer.material.color = color;
                crossRenderer.material.color = color;
            }
        }
        color.a = 1;
        webviewRenderer.material.color = color;
    }

    IEnumerator FadeOut()
    {
        StopCoroutine(FadeIn());
        webviewCollider.enabled = false;

        float alphaMin = 0;
        float alphaStart = 1;
        Color color;
        color = webviewRenderer.material.color;
        color.a = alphaStart;

        while (color.a > alphaMin)
        {
            yield return new WaitForEndOfFrame();
            color.a = color.a - 2f * Time.deltaTime;
            webviewRenderer.material.color = color;
            crossRenderer.material.color = color;

            if (color.a < alphaMin)
            {
                color.a = alphaMin;
                webviewRenderer.material.color = color;
                crossRenderer.material.color = color;
            }
        }
        color.a = 0;
        webviewRenderer.material.color = color;
    }
}
