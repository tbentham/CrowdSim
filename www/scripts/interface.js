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
	var mainDiv = document.getElementById("main"); // main div handle
	var panel = document.getElementById("panel"); //control and options panel

	canvas.width = mainDiv.offsetWidth;
	canvas.height = mainDiv.offsetHeight;
	panel.height = mainDiv.offsetHeight;
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

// triggered when window is loaded, initial setup of canvas, list population etc.

// Does this need to be populated using a script? Couldn't this content just be written in the client.html source?
// By appending these Divs to the html page, the appended html can't be seen using "view-source". What is this black magic?!
$(window).ready(function() {
	resizeCanvas();
	createListItem("images/dragobjects/wall.png", "Wall", 0);
	createListItem("images/dragobjects/door.png", "Door", 1);
	createListItem("images/dragobjects/interest.png", "Point of Interest", 2);
});

// triggered when window is resized. Makes sure the canvas stays at the correct size.
$(window).resize(function() {
	resizeCanvas();
});