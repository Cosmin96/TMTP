var TMTPChat = (function(){
  function TMTPChat(id, total){
    this.id = id;
    this.total = total;
    this.recording = false;
    this.autoStopRecording = null;
  }

  TMTPChat.prototype = {
    init: function(){
      this.buttonRec = document.getElementById('recordButton');
      this.messageList = document.getElementById('chat-' + this.id);
      this.loadMoreButton = document.getElementById('load-more');
      this.messageTemplate = document.getElementById('chat-message-template');
      this.sendMessageButton = document.getElementById('send-message-id');
      this.messageInput = document.getElementById('message-text');

      this.installViewListeners();
      this.initPusher();

      this.start = Math.max(0, this.total - TMTPChat.START_MAX_MESSAGES);
      if(this.start > 0)
        $(this.loadMoreButton).show();

      $(function(){
        $('.chat-message-author-pic').each(function(i, e){
          $.get($(e).data('location'), function(data){
            $(e).prop('src', data);
          }, 'text');
        });
      });
      
      this.scrollToBottom();
    },
    installViewListeners: function(){
      var self = this;
      $(this.buttonRec).click(function(){
        if(!self.recording)
          self.startRecording();
        else
          self.stopRecording();
      });

      $(this.loadMoreButton).click(function(){
        self.loadMore();
      });

      $(this.sendMessageButton).click(function(){
        self.sendMessage();
      });

      $(this.messageInput).keyup(function(event){
        if(event.keyCode === 13)
          self.sendMessage();
      });
    },
    initPusher: function(){
      var self = this;
      this.pusher = new Pusher(TMTPChat.PUSHER_ID, {
        cluster: 'eu',
        encrypted: true
      });
      this.channel = this.pusher.subscribe('chat-' + this.id);
      this.channel.bind('new-message', function(data){
        self.addMessage(data.message, data.username, data.type === "Audio");
        self.scrollToBottom();
      });
    },
    scrollToBottom: function(){
      $(this.messageList).animate({
        scrollTop: this.messageList.scrollHeight
      }, 500);
    },
    onHistoryLoaded: function(data, newStart){
      this.start = newStart;
      var fragment = document.createDocumentFragment();
      for (var i = 0; i < data.length; ++i)
        fragment.appendChild(this.createMessageNode(data[i].text, data[i].username, data[i].type === "Audio"));

      this.messageList.insertBefore(fragment, self.querySelector('.chat-message'));
      if(this.start <= 0){
        this.loadMoreButton.parentNode.remove(); // TODO this refers to <center> element, should remove itself, not the parent
      }
    },
    loadMore: function(){
      if(this.start <= 0)
        return;

      var startToSend = Math.max(0, start - TMTPChat.START_MAX_MESSAGES);
      var endToSend = start;
      var self = this;

      $.ajax({
        url: '/get-next-messages/' + ('chat-' + league) + '/' + startToSend + '/' + endToSend,
        success: function(data){
          self.onHistoryLoaded(data, startToSend);
        },
        error: function(){
          console.log("Error!");
        }
      });
    },
    createMessageNode: function(message, username, isAudio){
      var node = document.importNode(this.messageTemplate.content, true);
      node.querySelector('.chat-message-author-a').innerHTML = '&nbsp;' + username + '&nbsp;';
      node.querySelector('.chat-message-author-a').href = '/profile/' + username;
      if(isAudio){
        node.querySelector('.chat-message-content').remove();
        node.querySelector('audio source').src = message;
      }else{
        node.querySelector('.chat-message-content').textContent = message;
        node.querySelector('audio').remove();
      }
      //let the node be operatable before image URL actually loads
      this.getAndProcessImageUrl(username, function(data){
        node.querySelector('.chat-message-author-pic').src = userPic;
      });
      return node;
    },
    addMessage: function(message, username, isAudio){
      this.messageList.appendChild(this.createMessageNode(message, username, isAudio));
    },
    sendMessage: function(){
      var textToSend = this.messageInput.value;
      if(textToSend.length > 0){
        if(textToSend.length > 1000){
          window.alert("The text can be maximum 1000 characters long!");
          return;
        }
        this.messageInput.value = '';
        // no callbacks here - everything will be reflected by pusher
        $.ajax({
          type: 'POST',
          url: '/new-chat-message/' + Base64.encode(textToSend) + '/' + ('chat-' + this.id)
        });
      }

    },
    startRecording: function(){
      this.recording = true;
      $("#notRecording").hide();
      $("#Recording").show();
      toggleRecording(this.buttonRec, this.id);
      this.autoStopRecording = setTimeout(this.stopRecording.bind(this), TMTPChat.AUTO_STOP_RECORDING);
    },
    stopRecording: function(){
      this.recording = false;
      if(this.autoStopRecording){
        clearTimeout(this.autoStopRecording);
        this.autoStopRecording = null;
      }
      $("#Recording").hide();
      $("#notRecording").show();
      toggleRecording(this.buttonRec, this.id);
    },
    getAndProcessImageUrl: function(username, callback){
      $.get("/getProfilePic/" + username, function(data){
        callback(data);
      }, 'text');
    }
  }

  TMTPChat.AUTO_STOP_RECORDING = 10000; //10 sec before automatically stop recording
  TMTPChat.START_MAX_MESSAGES = 30;
  TMTPChat.PUSHER_ID = '6cf8829fd3f2bc2ca22f';
  return TMTPChat;
})()
