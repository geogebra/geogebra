using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;

public class CloneScreenText : MonoBehaviour {


    [Header("Cloned Display Text")]
    public Text BasicCloneText;
    public Text FootballCloneText;
    public Text FunctionCloneText;
    public Text KleinCloneText;
    public Text LissajousCloneText;
    public Text PenroseCloneText;
    public Text RuledSurfaceCloneText;
    public Text SierpinskiCloneText;
    public Text TempCloneText;
    [Space (15)]
    [Header("Main display")]
    public Text BasicText;
    public Text FootballText;
    public Text FunctionText;
    public Text KleinText;
    public Text LissajousText;
    public Text PenroseText;
    public Text RuledSurfaceText;
    public Text SierpinskiText;
    public Text TempText;
    


    // Use this for initialization
    void Start () {
        
		
	}
	
	// Update is called once per frame
	void Update () {


		
	}

    public void CheckStatus ()
    {
        BasicCloneText.enabled = BasicText.enabled;
        FootballCloneText.enabled = FootballText.enabled;
        FunctionCloneText.enabled = FunctionText.enabled;
        KleinCloneText.enabled = KleinText.enabled;
        LissajousCloneText.enabled = LissajousText.enabled;
        PenroseCloneText.enabled = PenroseText.enabled;
        RuledSurfaceCloneText.enabled = RuledSurfaceText.enabled;
        SierpinskiCloneText.enabled = SierpinskiText.enabled;
        //TempCloneText.enabled = TempText.enabled;

}
}
