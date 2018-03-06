using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;

public class CloneScreenText : MonoBehaviour {

    [Header("Cloned Display Text")]
    public Text basicCloneText;
    public Text footballCloneText;
    public Text functionCloneText;
    public Text kleinCloneText;
    public Text lissajousCloneText;
    public Text penroseCloneText;
    public Text ruledSurfaceCloneText;
    public Text sierpinskiCloneText;
    public Text tempCloneText;
    [Space (15)]
    [Header("Main display")]
    public Text basicText;
    public Text footballText;
    public Text functionText;
    public Text kleinText;
    public Text lissajousText;
    public Text penroseText;
    public Text ruledSurfaceText;
    public Text sierpinskiText;
    public Text tempText;

    public void CheckStatus ()
    {
        basicCloneText.enabled = basicText.enabled;
        footballCloneText.enabled = footballText.enabled;
        functionCloneText.enabled = functionText.enabled;
        kleinCloneText.enabled = kleinText.enabled;
        lissajousCloneText.enabled = lissajousText.enabled;
        penroseCloneText.enabled = penroseText.enabled;
        ruledSurfaceCloneText.enabled = ruledSurfaceText.enabled;
        sierpinskiCloneText.enabled = sierpinskiText.enabled;
        //TempCloneText.enabled = TempText.enabled;
    }
}
