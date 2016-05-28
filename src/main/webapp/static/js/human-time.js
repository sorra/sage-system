function humanTime_setup() {
  humanTime_show()
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
  var hours = date.getHours().toString()
  if (hours.length == 1) hours = '0' + hours
  var minutes = date.getMinutes().toString()
  if (minutes.length == 1) minutes = '0' + minutes

  var hm = hours + ':' + minutes
  if (now.getFullYear() == date.getFullYear() && now.getMonth() == date.getMonth() && now.getDate() == date.getDate()) {
    return hm
  }
  var mdhm = (date.getMonth()+1) + '/' + date.getDate() + ' ' + hm
  if (now.getFullYear() == date.getFullYear()) {
    return mdhm
  }
  return date.getFullYear() + ' ' + mdhm
}