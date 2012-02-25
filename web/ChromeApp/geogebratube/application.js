/* for now only modfiy the url to mobile
 * because this script run only on GeoGebraTube, we can assume that url already contains nice url.
 * */

if (location.href.indexOf("mobile=true") === -1) {
    location.href = location.href+"?mobile=true";
}
