'use strict'
Vue.component('human-time', {
  template: '<span class="human-time" :data-time="time">{{compute()}}</span>',
  props: ['time'],
  methods: {
    compute: function () {
      return humanTime_compute(this.time)
    }
  }
})

function setup(/*functions*/) {
  var args = arguments
  $(document).ready(function() {
    common_setup()
    for (var i = 0; i < args.length; i++) {
      args[i]()
    }
  })
}

function common_setup() {
  marked.setOptions({
    sanitize: true,
    highlight: function (code) {
      return hljs.highlightAuto(code).value;
    }
  })

  // Setup all simple tooltips
  $('[data-toggle="tooltip"]').tooltip()
  // Prevent form submitting on enter
  $(document).on('keypress keydown keyup', 'form:not(#search):not(#login):not(#register) input:not(textarea)', function(e) {
    if(e.which == 13) {
      e.preventDefault()
      return false
    }
  })
  // Setup the search box
  $('#search').submit(function(event){
    event.preventDefault()
    var q = $('#search input[name=q]').val()
    if(q) {
      window.open(this.getAttribute('action') + '?q=' + encodeURI(q))
    }
  });
}

function arrayRemoveValue(ary, value) {
  for (var i = 0; i < ary.length; i++) {
    if (ary[i] === value) {// Strict comparation
      ary.splice(i, 1)
      return i
    }
  }
}

function redirect(url) {
  window.location = url
}

function funcFormSubmitError($parent) {
  return function (resp) {
    popAlert(errorMsg(resp))
  }
}

function errorMsg(resp) {
  try {
    return (resp.responseText && JSON.parse(resp.responseText).errorMsg) || '网络错误'
  } catch (exception) {
    return '异常: ' + exception.message
  }
}

/**
 * pops an alert in the fixed alerts-holder
 */
function popAlert(text, level, duration) {
  level = level || 'danger'
  duration = duration || 2000
  var $alert = $('<div class="alert-wrapper alert alert-' + level + '">').text(text)
  setTimeout(function () {
    $alert.fadeOut(1000).remove()
  }, duration)
  $('#alerts-holder').prepend($alert)
}

/**
 * common tip function
 */
function tipover($node, text, duration) {
    if (!duration) duration = 1000;
    if ($node.length == 0) {
      console.error($node.selector + ' matches no element!')
      return
    }

    if (!$node.data('bs.tooltip')) {
        $node.tooltip({placement: 'top', trigger: 'manual'})
    }
    $node.data('bs.tooltip').options.title = text
    $node.tooltip('show')
    if (duration > 0) {
      window.setTimeout(function(){$node.tooltip('hide');}, duration)
    }
}

$.prototype.tipover = function (text, duration) {
  tipover(this, text, duration)
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

// textarea-helper.js
(function ($) {
  var caretClass   = 'textarea-helper-caret'
    , dataKey      = 'textarea-helper'

  // Styles that could influence size of the mirrored element.
    , mirrorStyles = [
      // Box Styles.
      'box-sizing', 'height', 'width', 'padding-bottom'
      , 'padding-left', 'padding-right', 'padding-top'

      // Font stuff.
      , 'font-family', 'font-size', 'font-style'
      , 'font-variant', 'font-weight'

      // Spacing etc.
      , 'word-spacing', 'letter-spacing', 'line-height'
      , 'text-decoration', 'text-indent', 'text-transform'

      // The direction.
      , 'direction'
    ];

  var TextareaHelper = function (elem) {
    if (elem.nodeName.toLowerCase() !== 'textarea') return;
    this.$text = $(elem);
    this.$mirror = $('<div/>').css({ 'position'    : 'absolute'
      , 'overflow'    : 'auto'
      , 'white-space' : 'pre-wrap'
      , 'word-wrap'   : 'break-word'
      , 'top'         : 0
      , 'left'        : -9999
    }).insertAfter(this.$text);
  };

  (function () {
    this.update = function () {

      // Copy styles.
      var styles = {};
      for (var i = 0, style; style = mirrorStyles[i]; i++) {
        styles[style] = this.$text.css(style);
      }
      this.$mirror.css(styles).empty();

      // Update content and insert caret.
      var caretPos = this.getOriginalCaretPos()
        , str      = this.$text.val()
        , pre      = document.createTextNode(str.substring(0, caretPos))
        , post     = document.createTextNode(str.substring(caretPos))
        , $car     = $('<span/>').addClass(caretClass).css('position', 'absolute').html('&nbsp;');
      this.$mirror.append(pre, $car, post)
        .scrollTop(this.$text.scrollTop());
    };

    this.destroy = function () {
      this.$mirror.remove();
      this.$text.removeData(dataKey);
      return null;
    };

    this.caretPos = function () {
      this.update();
      var $caret = this.$mirror.find('.' + caretClass)
        , pos    = $caret.position();
      if (this.$text.css('direction') === 'rtl') {
        pos.right = this.$mirror.innerWidth() - pos.left - $caret.width();
        pos.left = 'auto';
      }

      return pos;
    };

    this.height = function () {
      this.update();
      this.$mirror.css('height', '');
      return this.$mirror.height();
    };

    // XBrowser caret position
    // Adapted from http://stackoverflow.com/questions/263743/how-to-get-caret-position-in-textarea
    this.getOriginalCaretPos = function () {
      var text = this.$text[0];
      if (text.selectionStart) {
        return text.selectionStart;
      } else if (document.selection) {
        text.focus();
        var r = document.selection.createRange();
        if (r == null) {
          return 0;
        }
        var re = text.createTextRange()
          , rc = re.duplicate();
        re.moveToBookmark(r.getBookmark());
        rc.setEndPoint('EndToStart', re);
        return rc.text.length;
      }
      return 0;
    };

  }).call(TextareaHelper.prototype);

  $.fn.textareaHelper = function (method) {
    this.each(function () {
      var $this    = $(this)
        , instance = $this.data(dataKey);
      if (!instance) {
        instance = new TextareaHelper(this);
        $this.data(dataKey, instance);
      }
    });
    if (method) {
      var instance = this.first().data(dataKey);
      return instance[method]();
    } else {
      return this;
    }
  };

})(jQuery);
