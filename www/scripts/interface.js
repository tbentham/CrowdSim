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

// appends a list item div with the thumbnail from filepath image and description of the string value of name to the panel div.
function createListItem(image, name, mode) {
	var htmlgen = new Array();

	htmlgen[0] = "<div class=\"dragobject\"><image src=\"";
	htmlgen[1] = image;
	htmlgen[2] = "\" onclick=\"drawMode(" + mode.toString() + ")\"/>";
	htmlgen[3] = name;
	htmlgen[4] = "</div>";

	var finalhtml = htmlgen[0].concat(htmlgen[1].concat(htmlgen[2].concat(htmlgen[3].concat(htmlgen[4])))); //messy as fuck. I know. Bite me.

	$("#dragItems").append(finalhtml);
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
