'use strict';

$(document).ready(function(){
	$.each(window.frontMap.followers, function(i_, uc){
		createUserCard(uc)
			.css('margin', '10px')
			.css('float', 'left')
			.appendTo($('#list'));
	});
});