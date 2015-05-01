'use strict';

function tag_input_init() {
	$('body').delegate('.tag-sel', 'click', function(){
	  $(this).toggleClass('btn-success')
	})
	$('body').delegate('.tag-plus','click', function(){
	  if (!window.tagTree) {
	    $.get('/tag/tree').done(function(resp){
	      window.tagTree = resp
	    }).fail(function(err){
	      console.error("/tag/tree fails: " + err)
	    })
	  }
    var $this = $(this)
    $this.popover({
			html: true,
			placement: 'bottom',
			trigger: 'manual',
			content: $('<div>')
    })

    if ($this.data('tree-on') == true) {
      $this.removeData('tree-on')
      $this.popover('hide')
    } else {
      $this.data('tree-on', true)
      $this.popover('show')
      var $po = $this.data('bs.popover').tip()
      $po.find('.tag-tree').remove()
      tag_tree(window.tagTree, {asTagInput: true}).appendTo($po)
    }
	})
	$('body').delegate('.tag-clear', "click", function(){
	  $(this).parents('.tag-input').find('.tag-sel').removeClass('btn-success')
	})
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