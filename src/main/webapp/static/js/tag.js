function tag_setup() {
  tag_input_init()
}

function tag_tree(tagTree, params) {
  var $tree = $('<div class="tag-tree">')
  tag_node($tree, tagTree, -1, params)
  return $tree
}

function tag_node($tree, tag, depth, params) {
  if (depth >= 0) {
    var $tag = $('<a class="tag-label btn btn-default btn-sm">').appendTo($tree)
      .attr({title: tag.chainStr, 'tag-id': tag.id, 'href': '/tags/'+tag.id})
      .text(tag.name)
      .css('margin-left', (30*depth) + 'px')
    if (depth <= 0) {
      $tag.css('margin-top', '10px')
    }
    if (params && params.asTagInput) {
      $tag.removeClass('tag-label').addClass('tag-sel').removeAttr('href')
    }
    $('<br>').appendTo($tree)
  }

  for (var i in tag.children) {
    tag_node($tree, tag.children[i], depth+1, params)
  }
}

function hideTagTreeInput($tagPlus){
  $tagPlus.removeData('tree-on')
  $tagPlus.data('bs.popover').tip().find('.tag-sel').removeClass('btn-success')
  $tagPlus.popover('hide')
}

function tag_input_init() {
  function tagSelClick() {
    var $this = $(this)
    var tagId = parseInt($this.attr('tag-id'))
    var $tagInput = $this.parents('.tag-input')
    var $brothers = $tagInput.find('.tag-sel[tag-id='+tagId+']')
    var selections = $tagInput.data('selections')

    if (!$this.hasClass('btn-success')) {
      selections.push(tagId)
      $brothers.addClass('btn-success')
    } else {
      arrayRemoveValue(selections, tagId)
      $brothers.removeClass('btn-success')
    }
  }
  $(document).delegate('.tag-sel', 'click', tagSelClick)

  var selections = $('.tag-input').data('selections')
  if (selections) {
    $('.tag-sel').each(function(){
      var tagId = parseInt($(this).attr('tag-id'))
      if (selections.indexOf(tagId) >= 0) tagSelClick.apply(this)
    })
  }

  $('.tag-plus').popover({
    html: true,
    trigger: 'manual',
    content: $('<div>')
  }).each(function(){
    var $po = $(this).data('bs.popover').tip()
    $po.find('.tag-tree').remove()
    tag_tree(window.tagTree, {asTagInput: true}).appendTo($po)
  })

  $(document).delegate('.tag-plus','click', function(){
    if (!window.tagTree) {
      $.get('/tag/tree').done(function(resp){
        window.tagTree = resp
      }).fail(function(err){
        console.error("/tag/tree fails: " + err)
      })
    }
    var $this = $(this)
    if ($this.data('tree-on') == true) {
      hideTagTreeInput($this)
    } else {
      $this.data('tree-on', true)
      $this.popover('show')
    }
  })

  $(document).delegate('.tag-clear', "click", function(){
    $(this).parents('.tag-input').find('.tag-sel').removeClass('btn-success')
  })

  $('.tag-complete').each(tagCompleteInitFunc(function(tag){
    var $tagInput = $(this).parents('.tag-input')
    var $selsBeenThere = $tagInput.find('.tag-sel-list .tag-sel[tag-id=' + tag.id + ']')
    if ($selsBeenThere.length == 0) {
      tag_createTagSel(tag).appendTo($tagInput.find('.added-tags')).each(tagSelClick)
    } else {
      $selsBeenThere.each(tagSelClick)
    }
  }))
  $(document).delegate('.tag-complete', 'input', tagCompleteHandlerOnInput)
}

function tagCompleteInitFunc(callback){
  return function() {
    var comp = new Awesomplete(this, {
      minChars: 1,
      list: [],
      replace: function (chosen) {
        this.input.value = ''
        var $input = $(this.input)
        var list = $input.data('comp')._list
        var tag = tag_findValueByLabel(chosen, list)
        if (tag) {
          callback.apply(this.input, [tag])
        }
      }
    })
    $(this).data('comp', comp)
  }
}

function tagCompleteHandlerOnInput(){
  var $input = $(this)
  var list = $input.data('comp')._list
  var inputVal = $input.val().trim()
  if (inputVal && inputVal.length > 0 && inputVal != $input.data('q')) {
    $input.data('q', inputVal)
    $.get('/tag/suggestions', {q: inputVal}).done(function(tags){
      for (var i in tags) {
        var tag = tags[i]
        if (!tag_findValueByLabel(tag.chainStr, list)) {
          list.push({label: tag.chainStr, value: tag})
        }
      }
      $input.data('comp').evaluate()
    }).fail(function(msg){
      tipover($input, '发生异常: '+msg)
    })
  }
}

function tag_findValueByLabel(label, list) {
  for (var i in list) {
    if (list[i].label == label) return list[i].value
  }
  return null
}

function tag_createTagSel(tagLabel) {
  var $e = $('<a class="tag-sel btn btn-sm btn-default"></a>')
  $e.text(tagLabel.name).attr('tag-id', tagLabel.id)
  if (tagLabel.chainStr) {
    $e.attr('title', tagLabel.chainStr)
  }
  return $e
}