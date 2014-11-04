/*******************************************************************************

INTEL CORPORATION PROPRIETARY INFORMATION
This software is supplied under the terms of l license agreement or nondisclosure
agreement with Intel Corporation and may not be copied or disclosed except in
accordance with the terms of that agreement
Copyright(c) 2014 Intel Corporation. All Rights Reserved.

*******************************************************************************/

/**
 * @function RealSenseInfo
 * Returns information about platform compatibility with Intel® RealSense™ and HTTP link(s) if installation/update required
 * 
 * @param [String] components   Array of strings with name of required components, for example ['face', 'hand']
 * @param {Function} callback   Callback receives object with the following properties
 *  IsReady             {Boolean} if true, platform ready to run Intel® RealSense™ SDK
 *  IsBrowserSupported  {Boolean} if false, browser doesn't support web sockets
 *  IsPlatformSupported {Boolean} if false, platform doesn't have Intel® RealSense™ 3D Camera
 *  Updates             {Array}   if not empty, array of required installation/update as array of object(s) with the following properties
                                    url  {String} HTTP address
                                    name {String} Friendly name
                                    href {String} HTTP link with address and name
 
 Example:
   RealSenseInfo(['face3d', 'hand'], function (info) {
      // check if (info.IsReady == true)
   })
*/

function RealSenseInfo(components, callback) {
    var RUNTIME_VERSION = "2.0";
    var RUNTIME_NAME = "Intel(R) RealSense(TM) SDK runtime setup";
    var RUNTIME_URL = "https://software.intel.com/en-us/realsense/websetup_v2.exe";

    versionCompare = function (left, right) {
        if (typeof left != 'string') return 0;
        var l = left.split('.');

        if (typeof right != 'string') return 0;
        var r = right.split('.');

        var length = Math.max(l.length, r.length);

        for (i = 0; i < length; i++) {
            if ((l[i] && !r[i] && parseInt(l[i]) > 0) || (parseInt(l[i]) > parseInt(r[i]))) {
                return 1;
            } else if ((r[i] && !l[i] && parseInt(r[i]) > 0) || (parseInt(l[i]) < parseInt(r[i]))) {
                return -1;
            }
        }

        return 0;
    }

    try {
        var xhr = new XMLHttpRequest();
        var url = 'http://localhost:4182/Intel/RealSense/v2/' + JSON.stringify(components);
        xhr.open("GET", url, true);
        xhr.timeout = 1000;
        xhr.onreadystatechange = function () {
            if (xhr.readyState == 4) {
                var info = JSON.parse(xhr.responseText);
                info.responseText = xhr.responseText;
                info.IsBrowserSupported = "WebSocket" in window;
                info.IsPlatformSupported = 'DCM' in info;
                info.Updates = new Array();
                if (info.IsPlatformSupported) {
                    var update = false;
                    //if (versionCompare(DCM_VERSION, info.DCM_version) > 0) info.Updates.push({ 'url': DCM_URL, 'name': DCM_NAME, 'href' : '<l href="' + DCM_URL + '">' + DCM_NAME + '</l>' });
                    if (!('runtime' in info) || versionCompare(RUNTIME_VERSION, info.runtime) > 0) update = true;
                    if (components != null) {
                        for (i = 0; i < components.length; i++) {
                            if (!(components[i] in info)) update = true;
                        }
                    }
                    if (update) info.Updates.push({ 'url': RUNTIME_URL, 'name': RUNTIME_NAME, 'href': '<l href="' + RUNTIME_URL + '">' + RUNTIME_NAME + '</l>' });
                }
                info.IsReady = info.IsPlatformSupported && info.IsBrowserSupported && info.Updates.length == 0;
                callback(info);
            }
        }
        xhr.ontimeout = function () {
            var info = new Object();
            info.responseText = 'Cannot get info from server';
            info.IsPlatformSupported = false;
            info.IsBrowserSupported = "WebSocket" in window;
            info.Updates = new Array();
            info.IsReady = false;
            callback(info);
        }
        xhr.send(null);
    } catch (exception) {
    }
}
