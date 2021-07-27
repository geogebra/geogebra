using System.Collections;
using System.Collections.Generic;
using System.IO;
using UnityEngine;
using UnityEngine.UI;


public class OnPostRenderCamera : MonoBehaviour
{
    public Material mat;
    public bool grab;
    public GameObject appliedRenderer;
    public Texture2D renderedTexture;

    private IEnumerator coroutine;

    //for getting file path to my picture folder
    public Text getFilePath;
    public string filePath;

    // Use this for initialization
    void Start()
    {
        //for vieport Mixed Reality portal use 2200 2200
        //renderedTexture = new Texture2D(2200, 2200);

        // for Unity used Screen.width Screen.height
        //renderedTexture = new Texture2D(Screen.width, Screen.height);

        //for Immersivve headet outpu use 1440 1440
        //renderedTexture = new Texture2D(1440, 1440);

        // Test current resolution
        print("Screen.currentResolution = " + Screen.currentResolution);
        //renderedTexture = new Texture2D(Screen.currentResolution.width, Screen.currentResolution.height);
        renderedTexture = new Texture2D(1758, 1758);
        print("renderedTextured created " + renderedTexture.width + "x" + renderedTexture.height);

        mat.mainTexture = renderedTexture;

        //coroutine = TestScreenshot();
        //StartCoroutine(TestScreensthotFunction());
    }

    // Update is called once per frame
    void Update()
    {
        //filePath = getFilePath.ToString();
    }

    private void OnPostRender()
    {
        if (grab)
        {
            print("grab is true");
            //Rect rect = new Rect(0, 0, Screen.width, Screen.height);
            //Rect rect = new Rect(0, 0, Screen.currentResolution.width, Screen.currentResolution.height); 

            // resolution for immersive headset is 1760
            Rect rect = new Rect(0, 0, 1758, 1758);
            print("new Rect Created " + rect.width + "x" + rect.height);
            renderedTexture.ReadPixels(rect, 0, 0);
            renderedTexture.Apply();
            grab = false;
            //SaveTextureToPath(renderedTexture);
        }
    }

    IEnumerator TestScreensthotFunction()
    {
        bool test = true;
        int counter = 4;

        yield return new WaitForSeconds(3);
        grab = true;
        print("1 3 seconds");
        yield return new WaitForSeconds(3);
        grab = true;
        print("2 3 seconds");
        yield return new WaitForSeconds(3);
        grab = true;
        print("3 3 seconds");

        while (test)
        {
            yield return new WaitForSeconds(3);
            grab = true;
            print(counter + " 3 seconds");
            counter++;
        }
    }

    public void SaveTextureToPath(Texture2D tex)
    {
        byte[] bytes = tex.EncodeToPNG();
        File.WriteAllBytes("C:/Users/Bartolomej/Pictures" + "/SavedScreen.png", bytes);
    }
}