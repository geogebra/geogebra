XMLUtil = function() {
    this.parser = new DOMParser();
};

XMLUtil.prototype.removeTag = function(tag) {
    const tags = this.doc.getElementsByTagName(tag);
    while (tags.length > 0) {
        tags[0].remove();
    }
};

XMLUtil.prototype.getContent = function() {
    return this.doc.documentElement.outerHTML;
};

XMLUtil.prototype.setContent = function(text) {
	this.doc = this.parser.parseFromString(text,"image/svg+xml");
};

