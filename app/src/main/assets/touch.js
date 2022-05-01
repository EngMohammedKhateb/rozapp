var mousePosition;
var offset = [0,0];
var div;
var isDown = false;


function activeTouch(){
div = document.getElementById("local-video");
 var width = (window.innerWidth > 0) ? window.innerWidth : screen.width;
//
var body = document.body,
    html = document.documentElement;

var height = Math.max( body.scrollHeight, body.offsetHeight,
                       html.clientHeight, html.scrollHeight, html.offsetHeight );




var positionInfo = div.getBoundingClientRect();
var ewidth = positionInfo.width;
var eheight = positionInfo.height;

div.addEventListener('mousedown', function(e) {
    isDown = true;
    offset = [
        div.offsetLeft - e.clientX,
        div.offsetTop - e.clientY
    ];
}, true);

document.addEventListener('mouseup', function() {
    isDown = false;
}, true);

document.addEventListener('mousemove', function(event) {
    event.preventDefault();
    if (isDown) {
        mousePosition = {

            x : event.clientX,
            y : event.clientY

        };

        var boun=document.getElementById("parent").offsetWidth-document.getElementById("local-video").offsetWidth;
//      if((aX>0)&&(aX<boun)&&(aY>0)&&(aY<boun))
        console.log("element height"+mousePosition.y);

        if(mousePosition.x + offset[0] >0   ){
             if(mousePosition.x < ( width - ewidth/2   ) ){
                  div.style.left = (mousePosition.x + offset[0]) + 'px';
             }else{
                  div.style.left = (width - (ewidth+ewidth/4)  ) + 'px';
             }

        }else{
            div.style.left = (0) + 'px';
        }

        if(mousePosition.y + offset[1] >0){
                if(mousePosition.y < height - (eheight+eheight/9)  ){
                          div.style.top = (mousePosition.y + offset[1]) + 'px';
                     }else{
                          div.style.top  = (height-(eheight+eheight/2)) + 'px';
                }
         }else{
            div.style.top  = (0) + 'px';
        }


    }
}, true);


}



function touchHandler(event) {
    var touch = event.changedTouches[0];

    var simulatedEvent = document.createEvent("MouseEvent");
        simulatedEvent.initMouseEvent({
        touchstart: "mousedown",
        touchmove: "mousemove",
        touchend: "mouseup"
    }[event.type], true, true, window, 1,
        touch.screenX, touch.screenY,
        touch.clientX, touch.clientY, false,
        false, false, false, 0, null);

    touch.target.dispatchEvent(simulatedEvent);
    event.preventDefault();
}
inita();
function inita() {
    document.addEventListener("touchstart", touchHandler, true);
    document.addEventListener("touchmove", touchHandler, true);
    document.addEventListener("touchend", touchHandler, true);
    document.addEventListener("touchcancel", touchHandler, true);
}