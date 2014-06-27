'use strict';

function getTagChain(id, $parent) {
	$.get(webroot+'/tag/card/'+id, {})
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
		$tag.text(item.name).attr('href', webroot+'/public/'+item.id);
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
//			$tag.click(function(event) {
//			    event.preventDefault();
//			    gotoTag($(this).data('tagId'));
//			});
            var pleft = inc*(60+50) + 60;
			var $line = $('<div></div>').addClass('line').appendTo($tch);
			$line.css({width:	'50px',
					   height:	'5px',
					   background:	'#CCCCCC'})
			     .css({position: 'absolute',
					   left: pleft+'px', 
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
	   .attr('href', "/sage/public/"+tagLabel.id)
	   .click(function(event) {
	     event.preventDefault();
	     gotoTag($(this).data('tagId'));
	   });
	if (tagLabel.chainStr) {
		$tl.attr('title', tagLabel.chainStr);
	}
	return $tl;
}

function buildTagTree(funcCreatTag, $tagTree, tag, depth, isLastOne) {
    if (arguments.length < 3 || !(funcCreatTag instanceof Function)) {
        throw new Error("illegal argument");
    }
	if (!(depth>=0)) {
		depth = -1;
	}
	var indentValue = 20 * depth;
	var hasChildren = tag.children && tag.children.length > 0;

	if (depth >= 0) {
		var $tag = funcCreatTag(tag);
		$tag.css('margin-left', indentValue+'px')
			.appendTo($tagTree).after($('<br/>'));
		if ((depth==0 || isLastOne) && !hasChildren) {
			$tag.css('margin-bottom', '10px');
		}
	}
	if (hasChildren) {
		for (var i = 0; i < tag.children.length; i++) {
			var cur = tag.children[i];
			buildTagTree(funcCreatTag, $tagTree, cur, depth+1, i==tag.children.length-1);
		}
	}
}

function gotoTag (id) {
	window.open(webroot+'/public/'+id);
}