let localVideo = document.getElementById("local-video")
let remoteVideo = document.getElementById("remote-video")
let timer_container = document.getElementById("timer_container")
var minutesLabel = document.getElementById("minutes");
var secondsLabel = document.getElementById("seconds");
var totalSeconds = 0;


remoteVideo.addEventListener("click",function(){
  //Interface.toggleButtons();
});

localVideo.addEventListener("click",function(){
  // Interface.toggleButtons();
});


localVideo.style.opacity = 0
remoteVideo.style.opacity = 0
timer_container.style.opacity = 0

localVideo.onplaying = () => { localVideo.style.opacity = 1 }
remoteVideo.onplaying = () => { remoteVideo.style.opacity = 1 }

let peer
let is_audio=true;
let localStream;
let mycall;
function otp(mode){
  return  {  audio: true,
     video: { facingMode: mode  }
    };
}
function init(userId) {
    peer = new Peer(userId, {
        host: '45.85.147.38',
        port: 9000,
        path: '/videocallapp'
    })

    listen()

    peer.on('open', function(id) {
        Interface.connected("peer connected");
    });

}

let time_started=false;

function listen() {
    peer.on('call', (call) => {

        navigator.getUserMedia(otp("user"), (stream) => {
            localVideo.srcObject = stream
            localStream = stream
            mycall=call;
            call.answer(stream)
            call.on('stream', (remoteStream) => {
                remoteVideo.srcObject = remoteStream

                remoteVideo.className = "primary-video"
                localVideo.className = "secondary-video"
                 activeTouch();
                   timer_container.style.opacity = 1
                   if(!time_started){
                         time_started=true;
                          setInterval(startTimerUp, 1000);
                   }

            })

        })

    })
}


function startTimerUp() {
  ++totalSeconds;
  secondsLabel.innerHTML = pad(totalSeconds % 60);
  minutesLabel.innerHTML = pad(parseInt(totalSeconds / 60));
}

function pad(val) {
  var valString = val + "";
  if (valString.length < 2) {
    return "0" + valString;
  } else {
    return valString;
  }
}


let toggle= false;
 function toggleCamera(otherUserId) {

        localStream.getTracks().forEach(function(track) {
           track.stop()
        });

        if(!toggle){
           let otpback = { audio: is_audio, video: { facingMode: { exact: "environment" } } }
           navigator.getUserMedia(otpback,
              function(stream) {
//                  let videoTrack = stream.getVideoTracks()[0];
//                  let audioTrack =stream.getAudioTracks()[0];


                     for(sender of mycall.peerConnection.getSenders()){
                                  if(sender.track.kind == "audio") {
                                      if(stream.getAudioTracks().length > 0){
                                          sender.replaceTrack(stream.getAudioTracks()[0]);
                                      }
                                  }
                                  if(sender.track.kind == "video") {
                                      if(stream.getVideoTracks().length > 0){
                                          sender.replaceTrack(stream.getVideoTracks()[0]);
                                      }
                                  }
                      }

//                   var sender = mycall.peerConnection.getSenders().find(function(s) {
//                       return s.track.kind == videoTrack.kind;
//                   });
//                   sender.replaceTrack(videoTrack);
//
//                   var senderb = mycall.peerConnection.getSenders().find(function(s) {
//                       return s.track.kind == audioTrack.kind;
//                   });
//                   senderb.replaceTrack(audioTrack);

                   localVideo.srcObject = stream
                   localStream= stream
                   toggle=!toggle;
              },
              function(err) {
                 console.log("The following error occurred: " + err.name);
              });

        }else{


           let otpfront={ audio: is_audio, video:true };
           navigator.getUserMedia(otpfront,
              function(stream) {

//                  let videoTrack = stream.getVideoTracks()[0];
//                        var sender = mycall.peerConnection.getSenders().find(function(s) {
//                       return s.track.kind == videoTrack.kind;
//                  });
//                  sender.replaceTrack(videoTrack);
//
//                  var senderb = mycall.peerConnection.getSenders().find(function(s) {
//                        return s.track.kind == audioTrack.kind;
//                  });
//                  senderb.replaceTrack(audioTrack);


                  for(sender of mycall.peerConnection.getSenders()){
                              if(sender.track.kind == "audio") {
                                  if(stream.getAudioTracks().length > 0){
                                      sender.replaceTrack(stream.getAudioTracks()[0]);
                                  }
                              }
                              if(sender.track.kind == "video") {
                                  if(stream.getVideoTracks().length > 0){
                                      sender.replaceTrack(stream.getVideoTracks()[0]);
                                  }
                              }
                  }



                  localVideo.srcObject=null;
                  localVideo.srcObject = stream
                  localStream= stream
                  toggle=!toggle;
              },
              function(err) {
                 console.log("The following error occurred: " + err.name);

              });

        }

}



function toggleVideo(b) {
    if (b == "true") {
        localStream.getVideoTracks()[0].enabled = true
    } else {
        localStream.getVideoTracks()[0].enabled = false
    }
}

function toggleAudio(b) {
          is_audio=!is_audio;
          localStream.getAudioTracks()[0].enabled = is_audio

}


