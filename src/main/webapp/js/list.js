"use strict";
function createResourleList(rl) {
  var $rl = $('.proto .resource-list').clone()
  var $rinfoProt = $rl.find('.rinfo').detach()
  for (var i in rl.list) {
    var rinfo = rl.list[i]
    var $rinfo = $rinfoProt.clone().appendTo($rl)
    $rinfo.find('.rinfo-desc').text(rinfo.desc)
    $rinfo.find('.rinfo-link').attr('href', rinfo.link).text(limitStrLen(rinfo.link, 50))
  }
  return $rl
}

function createFollowList(fl) {
  var $fl = $('.proto .follow-list').clone()

}
