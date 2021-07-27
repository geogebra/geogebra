using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;
#if ENABLE_WINMD_SUPPORT
using System.Runtime.InteropServices.WindowsRuntime;
using System.Linq;
using System.IO;
#endif

public class JsManager : MonoBehaviour
{
    public string msg;
    //when there are more string fields in this class, program is crashing.

    public GameObject gameManager;

    ChakraHost.ChakraHost host;

    void Start ()
    {
        #region Assigning Missing Components
        if (gameManager == null)
        {
            gameManager = GameObject.Find("GameManager");
        }
        #endregion

#if ENABLE_WINMD_SUPPORT
        host = new ChakraHost.ChakraHost();
        msg = host.init();
#endif
    }

    public string Execute(string jsString)
    {
#if ENABLE_WINMD_SUPPORT
        string result = host.runScript(jsString);
#else
        string result = "not ENABLED WINDMD SUPPORT";
#endif
        return result;
    }
}
