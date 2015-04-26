'use strict';
function getTagChain(id, $parent) {
	$.get('/tag/card/'+id, {})
	.done(function(resp){
		createTagChain(resp).appendTo($parent);
	})
	.fail(function(resp){
		console.log(resp);
	});
}

function createTagChain(tagCard) {
	var $tch = $('.proto > .tag-chain').clone().css({position: 'relative'});
	for (var i = tagCard.chainUp.length-1, inc = 0; i >= 0; i--, inc++) {
		var item = tagCard.chainUp[i];

		var $tag = $('<a></a>').addClass('tag btn').addClass('btn-info').appendTo($tch);
		$tag.data('tagId', item.id);
		$tag.text(item.name).attr('href', '/public/'+item.id);
		$tag.css({display:	'block',
				  width:	'58px',
				  height:	'23px',
				  padding:	'0',
				  margin:	'0'});
		var pleft = inc*(60+50);
		$tag.css({position:	'absolute',
				  left: pleft+'px',
				  top: '0px'});
		if (i == 0) {
			$tag.removeClass('btn-info').addClass('btn-success');
		}
		
		if (i > 0) {
      var pxleft = inc*(60+50) + 60;
			var $line = $('<div></div>').addClass('line').appendTo($tch);
			$line.css({width:	'50px',
					   height:	'5px',
					   background:	'#CCCCCC'})
			     .css({position: 'absolute',
					   left: pxleft+'px',
					   top: '10px'});
		}
	};
	return $tch;
}

function createTagLabel(tagLabel) {
	var $tl = $('.proto > .tag-label').clone();
	$tl.data('tagId', tagLabel.id);
	$tl.text(tagLabel.name)
	   .attr('tag-id', tagLabel.id)
	   .attr('href', '/public/'+tagLabel.id)
	   .click(function(event) {
	     event.preventDefault();
	     gotoTag($(this).data('tagId'));
	   });
	if (tagLabel.chainStr) {
		$tl.attr('title', tagLabel.chainStr);
	}
	return $tl;
}

function tag_tree(tag) {
  var $tree = $('<div class="tag-tree">')
	tag_node($tree, tag, 0)
	return $tree
}

function tag_node($tree, tag, depth) {
	var $tag = $('<a class="tag-label btn">').appendTo($tree)
		.attr({title: tag.chainStr, 'tag-id': tag.id, href: '/public/'+tag.id})
		.text(tag.name)
		.css('margin-left', (20*depth) + 'px')
	if (depth == 0) {
		$tag.css('margin-top', '10px')
	}
	$('<br>').appendTo($tree)
	for (var i in tag.children) {
		tag_node($tree, tag.children[i], depth+1)
	}
}

function gotoTag (id) {
	window.open('/public/'+id);
}