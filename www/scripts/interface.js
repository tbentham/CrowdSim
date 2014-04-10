/* 
 * Interface JavaScript Functionality
 *
 */

// global variables
var objectList = {
    '0': "#wallControls",
    '1': "#doorControls"
};

function resizeCanvas() {

    var canvas = document.getElementById("mainCanvas"); // canvas handle

    canvas.width = 1000;
    canvas.height = 1000;
}

function modeSelected(mode) {

    var curDiv = objectList[mode];
    var curPar = $(curDiv).parent();

    $(curDiv).siblings().hide();
    $(curDiv).show();

    // minimum div height of 30, the size of one row of small buttons
    if ($(curPar).height() < 30)
	$(curPar).height(30);
}

// update displayed text in an element
function updateVal(newVal, valName) {

    document.getElementById(valName).innerHTML=newVal;
}

function togglePanel() {

    var panelDiv = document.getElementById('panel');
    var toggleDiv = document.getElementById('panelToggle');
    var mainDiv = document.getElementById('main');

    if ( panelDiv.style.visibility == 'hidden' ) {
	panelDiv.style.visibility = 'visible';
	toggleDiv.innerHTML= '<div id="toggleinner">&gt;&gt;</div>';
	toggleDiv.style.right = '250px';
	mainDiv.style.right = '280px';
    }
    else {
	panelDiv.style.visibility = 'hidden';
	toggleDiv.innerHTML= '<div id="toggleinner">&lt;&lt;</div>';
	toggleDiv.style.right = '0';
	mainDiv.style.right = '30px';
    }
}

function toggleOption(toggleId, elemId) {

    var elemDiv = document.getElementById(elemId);
    var toggleDiv = document.getElementById(toggleId);
    var innerh = toggleDiv.innerHTML;

    if ( elemDiv.style.visibility == 'collapse' ) {
	elemDiv.style.visibility = 'inherit';
	toggleDiv.innerHTML= innerh;
    }
    else {
	elemDiv.style.visibility = 'collapse';
	toggleDiv.innerHTML= innerh; 
    }
}

// triggered when window is loaded; initial setup of canvas, list population etc.
$(window).ready(function() {
    resizeCanvas();
});
