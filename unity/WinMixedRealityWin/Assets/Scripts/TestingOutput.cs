using System.Collections;
using System;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;
using System.IO;
using System.Linq;
#if ENABLE_WINMD_SUPPORT
using Windows.Storage;
#endif

namespace FilePath { 

public class TestingOutput : MonoBehaviour {

#if !UNITY_EDITOR
    bool haveFolderPath = false;
    StorageFolder picturesFolder;
    string tempFilePathAndName;
    string tempFileName;
#endif
    public Text pictureFolderPath;

    // Use this for initialization
    void Start()
    {
        //assign text
        pictureFolderPath.text = GettingFilePathToMyPictures();

#if !UNITY_EDITOR

        getFolderPath();
        while (!haveFolderPath)
        {
            Debug.Log("Waiting for folder path...");
        }
        Debug.Log("About to call CreateAsync");
#endif
    }

    // Update is called once per frame
    void Update()
        {
        // StartCoroutine(testOutput());

#if !UNITY_EDITOR
        getFolderPath();
#endif
    }

#if !UNITY_EDITOR

    async void getFolderPath()
    {
        StorageLibrary myPictures = await Windows.Storage.StorageLibrary.GetLibraryAsync(Windows.Storage.KnownLibraryId.Pictures);
        picturesFolder = myPictures.SaveFolder;
        pictureFolderPath.text = picturesFolder.Path;

        /*
        foreach (StorageFolder fodler in myPictures.Folders)
        {
            Debug.Log(fodler.Name);
        }

        Debug.Log("savePicturesFolder.Path is " + picturesFolder.Path);
        haveFolderPath = true;
        */
    }   
#endif
        string GettingFilePathToMyPictures()
        {
#if ENABLE_WINMD_SUPPORT
        
        //string picturePath = Windows.Storage.ApplicationData.Current.TemporaryFolder.Path;
        string picturePath = Windows.Storage.ApplicationData.Current.SharedLocalFolder.Path;          
        /*
        string picturePath = KnownFolders.PicturesLibrary.Path;

        if (picturePath == null)
        {
            picturePath = Windows.Storage.ApplicationData.Current.TemporaryFolder.Path;
        }
        */
#else
            string picturePath = System.Environment.GetFolderPath(System.Environment.SpecialFolder.MyPictures);
            Debug.Log(picturePath);
#endif
            return picturePath;
        }

        IEnumerator testOutput()
        {
            pictureFolderPath.text = GettingFilePathToMyPictures();

#if ENABLE_WINMD_SUPPORT
        pictureFolderPath.text = picturesFolder.Path;
#endif
            yield return new WaitForSeconds(5);
        }
    }
}