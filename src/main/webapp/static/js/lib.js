'use strict';

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
  $('form.search').submit(function(event){
    event.preventDefault();
    var q = $('form.search > *[name=q]').val();
    console.log(encodeURI(q));
    window.open('/search?q='+encodeURI(q));
  });
}

function humanTime_setup() {
  window.setInterval(humanTime_show(), 60000)
}

function humanTime_show(selector) {
  (selector ? $(selector+' .human-time') : $('.human-time')).each(function() {
    var $this = $(this)
    var millis = parseInt($this.data('time'))
    $this.text(humanTime_compute(millis))
  })
}

function humanTime_compute(millis) {
  var now = new Date()
  var nowSeconds = now.getTime() / 1000
  var seconds = millis / 1000

  var diffSeconds = nowSeconds - seconds
  if (diffSeconds < 1) return '刚才'
  if (diffSeconds < 60) return diffSeconds.toFixed(0) + '秒前'

  var diffMinutes = diffSeconds / 60
  if (diffMinutes < 60) return diffMinutes.toFixed(0) + '分钟前'

  var date = new Date(millis)
  var hm = date.getHours() + ':' + date.getMinutes()
  if (now.getFullYear() == date.getFullYear() && now.getMonth() == date.getMonth() && now.getDate() == date.getDate()) {
    return hm
  }
  var mdhm = (date.getMonth()+1) + '/' + date.getDate() + ' ' + hm
  if (now.getFullYear() == date.getFullYear()) {
    return mdhm
  }
  return date.getFullYear() + ' ' + mdhm
}

function arrayRemoveValue(value) {
  for (var i = 0; i < this.length; i++) {
    if (this[i] === value) {// Strict comparation
      this.splice(i, 1)
      return i
    }
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