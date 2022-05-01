let localVideo = document.getElementById("local-video")
let remoteVideo = document.getElementById("remote-video")
let timer_container = document.getElementById("timer_container")
var minutesLabel = document.getElementById("minutes");
var secondsLabel = document.getElementById("seconds");
var totalSeconds = 0;

//remoteVideo.addEventListener("click",function(){
//    Interface.toggleButtons();
//});
//
//localVideo.addEventListener("click",function(){
//
//   Interface.toggleButtons();
//});

let error_connection=true;
let start_stream=false;
let other_id;

localVideo.style.opacity = 0
remoteVideo.style.opacity = 0
timer_container.style.opacity = 0

localVideo.onplaying = () => {

 localVideo.style.opacity = 1

 // startEnterVal();

 }

remoteVideo.onplaying = () => {

remoteVideo.style.opacity = 1
  timer_container.style.opacity = 1
            if(!time_started){
                time_started=true;
                setInterval(startTimerUp, 1000);
            }

  activeTouch();
  error_connection=false;
}

let peer
let is_audio=true;
let entvalrecall;

//function startEnterVal(){
// if(start_stream){
//  entvalrecall=setInterval(checkConnections,6000);
// }
//
//}
//
//function checkConnections(){
//
// if(error_connection){
//     startCall(other_id);
// }else{
//    clearInterval(entvalrecall);
// }
//}

function init(userId) {
    peer = new Peer(userId, {
        host: '45.85.147.38',
        port: 9000,
        path: '/videocallapp'
    })



    peer.on('open', function(id) {
        Interface.connected("peer connected");
    });

}

let localStream


let call;

let time_started=false;
let alerter=0;


function startCall(otherUserId) {

    other_id=otherUserId;
    navigator.getUserMedia(otp("user"), (stream) => {

        localVideo.srcObject = stream
        localStream = stream

        call = peer.call(otherUserId, stream)
        call.on('stream', (remoteStream) => {
            remoteVideo.srcObject = remoteStream
            remoteVideo.className = "primary-video"
            localVideo.className = "secondary-video"
            start_stream=true;
        })

    })
}



function startTimerUp() {
  ++totalSeconds;
  ++alerter;
  secondsLabel.innerHTML = pad(totalSeconds % 60);
  minutesLabel.innerHTML = pad(parseInt(totalSeconds / 60));

  if(parseInt(alerter / 60)){
    alerter=0;
    Interface.proccessFromJs();
  }

}

function pad(val) {
  var valString = val + "";
  if (valString.length < 2) {
    return "0" + valString;
  } else {
    return valString;
  }
}



function otp(mode){
  return  {  audio: true,
     video: { facingMode: mode  }
    };
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

//                             let videoTrack = stream.getVideoTracks()[0];
//                             var sender = call.peerConnection.getSenders().find(function(s) {
//                               return s.track.kind == videoTrack.kind;
//                             });
//                             sender.replaceTrack(videoTrack);

                                for(sender of call.peerConnection.getSenders()){
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

//                             let videoTrack = stream.getVideoTracks()[0];
//                              var sender = call.peerConnection.getSenders().find(function(s) {
//                               return s.track.kind == videoTrack.kind;
//                             });
//                             sender.replaceTrack(videoTrack);


                                for(sender of call.peerConnection.getSenders()){
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

        localStream.getAudioTracks()[0].enabled = !is_audio
        is_audio=!is_audio;

}


