'use strict';
var webroot = '/sage';
$.fn.outerHTML = function(s) {
    if (s) {
      if (this.length > 0) {this[0].outerHTML = s; return this}
      else {return undefined}
    } else {
      return this.length > 0 ? this[0].outerHTML : undefined
    }
};

$.fn.warnEmpty = function() {
    if (this.length == 0) {console.warn('Empty NodeList for '+this.selector+'!');}
    return this;
};

$.fn.getCursorPosition = function() {
    var el = $(this).get(0);
    var pos = 0;
    if('selectionStart' in el) {
        pos = el.selectionStart;
    } else if('selection' in document) {
        el.focus();
        var Sel = document.selection.createRange();
        var SelLength = document.selection.createRange().text.length;
        Sel.moveStart('character', -el.value.length);
        pos = Sel.text.length - SelLength;
    }
    return pos;
};

$.fn.setCursorPosition = function(pos) {
    if ($(this).get(0).setSelectionRange) {
      $(this).get(0).setSelectionRange(pos, pos);
    } else if ($(this).get(0).createTextRange) {
      var range = $(this).get(0).createTextRange();
      range.collapse(true);
      range.moveEnd('character', pos);
      range.moveStart('character', pos);
      range.select();
    }
};

function buildNavTagTree($lnk, tagTree) {
    var $navTagTree = $('<div>');
    var $createTag = $('<btn>').text('新建').addClass('btn btn-warning').css('display', 'block').appendTo($navTagTree);
    
//    var $dialog = $('<div class="new-tag-dialog modal">')
//        .css({
//            width : '300px',
//            minHeight : '100px',
//            borderRadius : '10px'
//        });
//    $('<div class="modal-header">').text('新的标签').appendTo($dialog);
//    var $body = $('<div class="modal-body">').appendTo($dialog);
//    $('<input id="name">').appendTo($body);
//    $('<input id="parent-id">').appendTo($body);
    var $dialog = $('.proto > .new-tag-dialog').warnEmpty().clone();
    
    var $footer = $dialog.find('.modal-footer');
    $('<button class="btn btn-primary">').text('确定').css({float: 'right'}).appendTo($footer)
        .click(function() {
            $.post(webroot+'/tag/new', {
                name: $dialog.find('#name').val(),
                parentId: $dialog.find('#parent-id').val()
            });
            $dialog.find('#name').val('');
            $dialog.find('#parent-id').val('');
            $dialog.modal('hide');
        });
    $createTag.click(function(){
        $dialog.modal('show');
    });
    
    buildTagTree(function(tag){
        return createTagLabel(tag).addClass('btn')
            .click(function(){
                $lnk.popover('hide');
            });
    }, $navTagTree, tagTree);
    $lnk.popover({
            html: true,
            placement: 'bottom',
            trigger: 'manual',
            selector: '#tag-tree-popover',
            content: $navTagTree
    });
}

/*
 * common tip function
 */
function tipover($node, text, duration) {
    if (!duration) duration = 1000;
    
    if (!$node.data('tooltip')) {
        $node.tooltip({placement: 'top', trigger: 'manual'});
    }
    $node.data('tooltip').options.title = text;
    $node.tooltip('show');
    window.setTimeout(function(){$node.tooltip('hide');}, duration);   
}

function commonConfirmPopover($node, action, message, placement) {
    var $block = $('<div>')
    $('<button class="btn">').text('是').appendTo($block).click(function(){
        action.apply($node)
        $node.popover('hide')
    })
    $('<button class="btn">').text('否').appendTo($block).click(function(){
        $node.popover('hide')
    })
    $node.popover({
        html: true,
        title: message,
        placement: placement,
        content: $block
    })
}

function limitStrLen(str, maxLen) {
  if (str.length > maxLen+3) {
    return str.substr(0, maxLen) + '...'
  } else {
    return str
  }
}

function userLinkAttrs(id) {
  return {uid: id, href: webroot+'/private/'+id};
}

template.helper('webroot', function(){return webroot})
template.helper('showTime', function(time){
  return new Date(parseInt(time)).toLocaleString();
})

$(document).ready(function(){
  if ($('#front-map').length > 0) {
    window.frontMap = $.parseJSON($('#front-map').text());
  } else {
    window.frontMap = {};
  }

  if ($('#user-self-json').length > 0) {
      window.userSelf = $.parseJSON($('#user-self-json').text());
  }

  if ($('#tag-tree-json').length > 0) {
      window.tagTree = $.parseJSON($('#tag-tree-json').text());
      var $lnk = $('#nav-tags')
        .click(function(event){
          event.preventDefault();
          $(this).popover('toggle');
        });
      buildNavTagTree($lnk, window.tagTree);
  }
});

function escapeHtml(str) {
  return String(str).replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;').replace(/"/g, '&quot;').replace(/'/g, '&#39;');
}

function unescapeHtml(str) {
  return String(str).replace(/&amp;/g, '&').replace(/&lt;/g, '<').replace(/&gt;/g, '>').replace(/&quot;/g, '"').replace(/&#39;/g, '\'');
};

function textareaAutoResize() {
  var height = Math.max(this.scrollHeight, this.clientHeight);
  if (height > this.clientHeight) {
    $(this).css('height', height+'px');
  }
}
