'use strict';

$(document).ready(function(){
	$('.hit-json').each(function(){
		console.log(" ");
		console.log($(this).text());
		var result = $.parseJSON($(this).text());
		if (result.type == "TweetView") {
			createTweetCard(result).appendTo($('#result-list'));
		}
		else {
			createBlogData(result).appendTo($('#result-list'));
		}
	});
});