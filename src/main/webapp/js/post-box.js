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