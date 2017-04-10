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
}
