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
	if ($(curPar).height() < 30) {
		$(curPar).height(30);
	};
}

// update displayed text in an element
function updateVal(newVal, valName)
{
	document.getElementById(valName).innerHTML=newVal;
}

// triggered when window is loaded, initial setup of canvas, list population etc.
$(window).ready(function() {
    resizeCanvas();
});
