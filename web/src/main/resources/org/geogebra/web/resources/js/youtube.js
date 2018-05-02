var player_ready = false;

function onYouTubeIframeAPIReady() {
	player_ready = true;
	window.console.log('ezlefutezaszar?');
	window.youtube_api_ready();
}