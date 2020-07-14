window.copyGraphicsToClipboardExternal = function(image) {
	function dataURItoBlob(dataurl) {
        var arr = dataurl.split(','), mime = arr[0].match(/:(.*?);/)[1],
            bstr = atob(arr[1]), n = bstr.length, u8arr = new Uint8Array(n);
        while(n--){
            u8arr[n] = bstr.charCodeAt(n);
        }
        return new Blob([u8arr], {type:mime});
    }

     var imageBlob = dataURItoBlob(image);
	 var item = (new ClipboardItem({"image/png":imageBlob}));
	 navigator.clipboard.write([item]);
}

