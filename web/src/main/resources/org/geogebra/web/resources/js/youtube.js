var player_ready = false;

function onYouTubeIframeAPIReady() {
	player_ready = true;
	window.youtube_api_ready();
}