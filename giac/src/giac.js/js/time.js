mergeInto(LibraryManager.library,{
    emcctime: function() {
	return Math.floor(Date.now());
    }
});
