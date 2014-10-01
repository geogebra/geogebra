var _gaq = _gaq || [];
_gaq.push(['_setAccount', 'UA-1780044-1']);

_gaq.push(['_setDomainName', ga_domainName]);
_gaq.push(['_setAllowLinker', true]);
_gaq.push(['_trackPageview']);

(function() {
    var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;
    ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
    var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);
})();

function gaTrackMaterialTeachersView(material_id) {
    _gaq.push(["_trackEvent", "material", "teachersView", material_id.toString(), ga_userId]);
}
function gaTrackCollectionTeachersView(collection_id) {
    _gaq.push(["_trackEvent", "collection", "teachersView", collection_id.toString(), ga_userId]);
}

var GA_MATERIAL_VIEW_SIMPLE = "view_studentWorksheet";
var GA_MATERIAL_VIEW_COLLECTION = "view_inCollection";
var GA_MATERIAL_VIEW_BOOK = "view_inBook";
var GA_MATERIAL_VIEW_EMBEDDED = "view_embedded";
function gaTrackMaterialView(material_id, type) {
    _gaq.push(["_trackEvent", "material_view", type, material_id.toString(), ga_userId]);
}
function gaTrackMaterialViewEmbedded(material_id, refURL) {
    _gaq.push(["_trackEvent", "material_view", GA_MATERIAL_VIEW_EMBEDDED, refURL, material_id]);
}

var GA_COLLECTION_VIEW_SIMPLE = "studentView";
var GA_COLLECTION_VIEW_BOOK = "bookView";
function gaTrackCollectionView(collection_id, type) {
    _gaq.push(["_trackEvent", "collection_view", type, collection_id.toString(), ga_userId]);
}

/** @param $action "like" or "unlike" */
function gaTrackLikeMaterial(action, material_id) {
    _gaq.push(["_trackEvent", "material", action, material_id.toString(), ga_userId]);
}

/** @param $action "favorite" or "unfavorite" */
function gaTrackFavoriteMaterial(action, material_id) {
    _gaq.push(["_trackEvent", "material", action, material_id.toString(), ga_userId]);
}

