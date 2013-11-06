/* 
 * Interface JavaScript Functionality
 *
 */

// global variables

var objectList = new Array(); // list of drag-and-drop objects

function resizeCanvas() {
	var canvas = document.getElementById("mainCanvas"); // canvas handle
	var mainDiv = document.getElementById("main"); // main div handle

	canvas.width = mainDiv.offsetWidth;
	canvas.height = mainDiv.offsetHeight;
}

function simulate() {
	alert("Simulate has been called.");
}

function evacuate() {
	alert("Evacuate has been called.");
}

// appends a list item div with the thumbnail from filepath image and description of the string value of name to the panel div.
function createListItem(image, name, mode) {
	var htmlgen = new Array();

	htmlgen[0] = "<div class=\"dragobject\"><image src=\"";
	htmlgen[1] = image;
	htmlgen[2] = "\" onclick=\"javascript:drawMode(" + mode.toString() + ")\"/>";
	htmlgen[3] = name;
	htmlgen[4] = "</div>";

	var finalhtml = htmlgen[0].concat(htmlgen[1].concat(htmlgen[2].concat(htmlgen[3].concat(htmlgen[4])))); //messy as fuck. I know. Bite me.

	console.log("I am about to append")
	$("#panel").append(finalhtml);
}

// reads drag-n-drop object names and image filepaths from a file at given filepath and uses createItemList() function
// to create the draggable objects list in the panel div.
function populateList(filepath) {

}

// triggered when window is loaded, initial setup of canvas, list population etc.

// Does this need to be populated using a script? Couldn't this content just be written in the client.html source?
// By appending these Divs to the html page, the appended html can't be seen using "view-source". What is this black magic?!
$(window).ready(function() {
	resizeCanvas();
	createListItem("images/dragobjects/wall.png", "Wall", 0);
	createListItem("images/dragobjects/door.png", "Door", 1);

	// I would rather have this in client.html too.
	$("#panel").append("<div id=\"canvasControls\"><button class=\"canvasButton\" onclick=\"javascript:clearCanvas()\">Clear</button></div>");
	$("#panel").append("<div id=\"canvasControls\"><button class=\"canvasButton\" onclick=\"javascript:rotate()\">+90</button></div>");
	// $("#panel").append("<div id=\"canvasControls\"><button class=\"canvasButton\" onclick=\"javascript:spin()\">spinMEH</button></div>");
});

// triggered when window is resized. Makes sure the canvas stays at the correct size.
$(window).resize(function() {
	resizeCanvas();
});

// triggered when the simulate div is clicked.
$("#simulatebutton").click(function() {
  alert( "Handler for .click() called." );
});