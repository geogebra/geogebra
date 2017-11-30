using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class CursorVisibility : MonoBehaviour
{
    void OnLevelWasLoaded(int level)
    {
        if (FindObjectOfType<CharacterController>() != null)
        {
            Cursor.visible = false;
        }
        else
        {
            Cursor.visible = true;
        }
    }
}
