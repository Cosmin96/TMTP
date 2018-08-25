var TMTPChatSettings = (function(){
  function isAndroid(userAgent){
    userAgent = (userAgent || navigator.userAgent || '').toLowerCase();
    return userAgent.indexOf("android") > -1; // it goes 99% correct but fails to detect *some* android devices
  }
  function isIOS(userAgent){
    userAgent = (userAgent || navigator.userAgent || '').toLowerCase();
    return /ipad|iphone|ipod/.test(userAgent) && !window.MSStream;
  }

  return {
    requiresClickToPlayAudio: function(){
      return isIOS() && !isAndroid();
    },
    shouldHideAudioRecording: false/*function(){
      return isIOS() && !isAndroid();
    }*/,
    mustHideSelfMessage: false
  }
})();