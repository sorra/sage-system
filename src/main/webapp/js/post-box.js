'use strict';

function tag_input(params) {

}

function createTagSel(tagLabel) {
	return createTagLabel(tagLabel).addClass('tag-sel').addClass('btn btn-small').off('click').click(
      function(event) {
        event.preventDefault();
        $(this).toggleClass('btn-success');
      });
}

function buildTagSels(tags) {
	$.each(tags, function(idx, item){
		createTagSel(item).insertBefore($('.tag-plus'));
	});
}

function buildTagPlus() {
	var $tagTree = $('<div>');
	
	tag_tree(window.tagTree).appendTo($tagTree)

	$('.tag-plus').popover({
			html: true,
			placement: 'bottom',
			trigger: 'manual',
			selector: '#tag-tree-popover',
			content: $tagTree
	}).popover('show');
	$('.tag-plus').data('bs.popover').tip().hide();

	$('.tag-plus').click(function(){
		if ($(this).data('show-popover')) {
			$(this).data('show-popover', false)
				   .data('bs.popover').tip().hide();
		}
		else {
			$(this).data('show-popover', true)
				   .data('bs.popover').tip().show();
		}
	});

	$('.tag-clear').click(function (){
		$(this).parent().find('.tag-sel').removeClass('btn-success')
		$tagTree.find('.tag-sel').removeClass('btn-success')
	})
}