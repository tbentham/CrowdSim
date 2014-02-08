var WALL_MODE = 0;
var DOOR_MODE = 1;
var INTEREST_MODE = 2;
var KILL_MODE = 99;

var stage;

//These two arrays are counterparts.
//The features array consists of custom objects defining the items a user has drawn on the canvas.
//The canvasFeatures array consists of the canvas objects which display the features on screen.
var features = new Array();
var canvasFeatures = new Array();
var endOfArray = -1;  //For "tail" access

var featureID = 0;
var click = false;
var trace = false;
var debug = false;
var angle = 0;
var time = -1; // For the "step" button - eventually for use with a slider
var interval;
var cursorItem;
var cursorItemPixel; // This needs another tidy session

var people = new Array();
var canvasPeople;
var canvasPeople_colours = new Array();
var canvasTraces = new Array();

function init() {

    stage = new createjs.Stage("mainCanvas");
    drawMode(0);
    stage.update();
}

// Need some connection shit before this happens, the JSON needs to come from the server. It is currently in a file.
function populate(time, clear){

    if(time >= people[0].length){
        return false;
    }

    if(!canvasPeople){
        canvasPeople = new Array();

        for(var i = 0; i < people.length; i++)
        {
            s = new createjs.Shape(); canvasPeople.push(s);
            canvasPeople_colours.push("rgba(" + String(Math.floor(Math.random()*255))+ "," + String(Math.floor(Math.random()*255)) + "," + String(Math.floor(Math.random()*255)) + ",1)")
            stage.addChild(s);
        }
    }
    else{
        for(var i = 0; i < canvasPeople.length; i++){
            canvasPeople[i].graphics.clear();
        }
        for(var i = 0; i < canvasTraces.length; i++){
            canvasTraces[i].graphics.clear();
        }
    }

    for(var i = 0; i < canvasPeople.length; i++){ 
        canvasPeople[i].graphics.beginFill(canvasPeople_colours[i]).drawCircle(people[i][time].x*10, people[i][time].y*10, 5); 
    }

    if(trace){
    for(var i = 0; i < people.length; i++){
        for(var j = 0; j < time + 1; j++){
            if (j > 0){
                s = new createjs.Shape(); canvasTraces.push(s);
                s.graphics.beginStroke(canvasPeople_colours[i]).setStrokeStyle(1.0).moveTo(people[i][j-1].x, people[i][j-1].y).lineTo(people[i][j].x, people[i][j].y).endStroke();
                stage.addChild(s); 
                }
            }
        }
    }

    //Update the timestep number
   
    num =  (time * 0.1);
    $("#timestep")[0].textContent = num.toFixed(2);
    stage.update();
}
    
//This whole drawmodes thing is silly, because I still have to manually define which events are attached to what. Investigate the event listeners and see if they can be stored and added to the stage as objects without calling this method.
function drawMode(mode){

    if(mode == WALL_MODE){
        stage.removeAllEventListeners();
        stage.addEventListener("stagemousedown", startLine);
        stage.addEventListener("stagemousemove", drawLine);
        stage.addEventListener("stagemouseup", endLine);   
    }
    else if(mode == DOOR_MODE){
        stage.removeAllEventListeners();
        stage.addEventListener("stagemousedown", drawDoor);
	    stage.addEventListener("stagemousemove", mouseDoor);
    }
    else if(mode == KILL_MODE){
        stage.removeAllEventListeners();

        for(var i = 0; i < canvasFeatures.length; i++){
            canvasFeatures[i].addEventListener("click", removeItem);
        }
    }
    else if(mode == INTEREST_MODE){
        stage.removeAllEventListeners();
        stage.addEventListener("stagemousedown", drawInterest);
        stage.addEventListener("stagemousemove", mouseInterest);
    }
}

function removeItem(e){

    for(var i = 0; i < canvasFeatures.length; i++){
        if(canvasFeatures[i].id == e.currentTarget.id){
            stage.removeChild(canvasFeatures[i]);
        }
    }
    stage.update();
}

// Can maybe do wall highlighting, with some computation.
function mouseDoor(e){

    if(cursorItem)cursorItem.graphics.clear();
    cursorItem = new createjs.Shape();

    //Find the coordinates of a door after it has been rotated, remember to add StageX and Y to translate from the origin to the mouse pointer.
    //TODO: consider a translation method, this way we can keep all the graphics transformations in one tidy place.
    var from = spin(new Coordinate(-25, 0), angle); from[0] = from[0] + e.stageX; from[1] = from[1] + e.stageY;
    var to = spin(new Coordinate(25, 0), angle); to[0] = to[0] + e.stageX; to[1] = to[1] + e.stageY;

    if(e.nativeEvent.shiftKey){
	   cursorItem.graphics.beginStroke("rgba(125,170,195,1)").setStrokeStyle(3.0).moveTo(Math.round(from[0]/50)*50, Math.round(from[1]/50)*50).lineTo(Math.round(to[0]/50)*50, Math.round(to[1]/50)*50).endStroke();
    }
    else{
	   cursorItem.graphics.beginStroke("rgba(125,170,195,1)").setStrokeStyle(3.0).moveTo(from[0], from[1]).lineTo(to[0], to[1]).endStroke();
    }
    stage.addChild(cursorItem);
    stage.update();
}

function drawDoor(e){

    //If we can draw the door in one go without multiple canvas objects, it can deleted in one go.

    door = new createjs.Shape();
    arc = new createjs.Shape();

    // This is just a rectangle, consider using premade Rectangle objects that may handle rotations and will fess up their vertices on demand.
    //TODO: consider a translation method, this way I can keep all the graphics transformations in one tidy place.
    var hinge = spin(new Coordinate(-25, 0), angle); hinge[0] = hinge[0] + e.stageX; hinge[1] = hinge[1] + e.stageY;
    var close = spin(new Coordinate(25, 0), angle); close[0] = close[0] + e.stageX; close[1] = close[1] + e.stageY;
    var open = spin(new Coordinate(-25, 50), angle); open[0] = open[0] + e.stageX; open[1] = open[1] + e.stageY;
    var arcthrough = spin(new Coordinate(25, 50), angle); arcthrough[0] = arcthrough[0] + e.stageX; arcthrough[1] = arcthrough[1] + e.stageY;

    if(e.nativeEvent.shiftKey){
        // Call the cops.
        door.graphics.beginStroke("rgba(125,170,195,1)").moveTo(Math.round(hinge[0]/50)*50, Math.round(hinge[1]/50)*50).lineTo(Math.round(open[0]/50)*50, Math.round(open[1]/50)*50).arcTo(Math.round(arcthrough[0]/50)*50, Math.round(arcthrough[1]/50)*50, Math.round(close[0]/50)*50, Math.round(close[1]/50)*50, 50).endStroke();
    }
    else{
        door.graphics.beginStroke("rgba(125,170,195,1)").moveTo(hinge[0], hinge[1]).lineTo(open[0], open[1]).arcTo(arcthrough[0], arcthrough[1], close[0], close[1], 50).endStroke();
    }
   
    d = new Feature(featureID, 1); featureID++;
    d.setFromCoords(hinge[0], hinge[1]); 
    d.setToCoords(close[0], close[1]);

    features.push(d);
    canvasFeatures.push(door);

    stage.addChild(door);
    stage.update();
}

function startLine(e){

    if(stage.mouseInBounds){
        click = true;

        features.push(new Feature(featureID, 0));
        canvasFeatures.push(new createjs.Shape());
        endOfArray++; featureID++;

        if(e.nativeEvent.shiftKey){
            features[endOfArray].setFromCoords(Math.round(e.stageX/50)*50, Math.round(e.stageY/50)*50);
            features[endOfArray].setToCoords(Math.round(e.stageX/50)*50, Math.round(e.stageY/50)*50);
        }
        else{
            features[endOfArray].setFromCoords(e.stageX, e.stageY);
            features[endOfArray].setToCoords(e.stageX, e.stageY);
        }

        stage.addChild(canvasFeatures[endOfArray]);
        stage.update();
    }
}

function endLine(e){

    //Check click here to ensure the click wasnt made outside the canvas - which would result in the last line being changed.
    if(click && stage.mouseInBounds){
        click = false;

        //Increases readability since they are accessed multiple times.
        var line = features[endOfArray];
        var canvasLine = canvasFeatures[endOfArray];

        //Drawing
        if(e.nativeEvent.shiftKey){
            line.setToCoords(Math.round(e.stageX/50)*50, Math.round(e.stageY/50)*50);
        }
        else{
            line.setToCoords(e.stageX, e.stageY);
        }

        //Only draw non point walls, and remove walls which are points.
        if(line.getFromCoords().x == line.getToCoords().x && line.getFromCoords().y == line.getToCoords().y ){
            features.pop(); canvasFeatures.pop(); endOfArray--;
        }
        else{     
            canvasLine.graphics.clear();
            canvasLine.graphics.beginStroke("black").setStrokeStyle(3.0).moveTo(line.getFromCoords()["x"], line.getFromCoords()["y"]).lineTo(line.getToCoords()["x"], line.getToCoords()["y"]).endStroke();
            stage.update();
        }
    }
}

function drawLine(e){

    if(click && stage.mouseInBounds){

        //Increases readability since they are accessed multiple times.
        var line = features[endOfArray];
        var canvasLine = canvasFeatures[endOfArray];

        if(e.nativeEvent.shiftKey){
            line.setToCoords(Math.round(e.stageX/50)*50, Math.round(e.stageY/50)*50);
        }
        else{
            line.setToCoords(e.stageX, e.stageY);
        }
        
        canvasLine.graphics.clear();
        canvasLine.graphics.beginStroke("black").setStrokeStyle(3.0).moveTo(line.getFromCoords()["x"], line.getFromCoords()["y"]).lineTo(line.getToCoords()["x"], line.getToCoords()["y"]).endStroke();
        stage.update();
    }

}

function jsonDump(){
    var s = "[";

    for(var i = 0; i < features.length; i++){
        s = s.concat(JSON.stringify(features[i]));

        if(i < features.length -1){
            s = s.concat(", ");
        }
    }

    return s.concat("]");
}

function clearCanvas(){

    for(var i = 0; i < stage.children.length; i++){
        stage.children[i].graphics.clear();
    }
    
    features = new Array();
    canvasFeatures = new Array();
    endOfArray = -1;
    stage.update();
}

// 2D rotation of a pair of coordinates. Some graphics shit.
function spin(coord, angle){
    //TODO: change this to return type COORDINATE - otherwise developers will begin to feel uncertain about things whilst pouring their coffee.
    return[Math.cos(angle)*coord.getX() +((-Math.sin(angle))*coord.getY()), Math.sin(angle)*coord.getX() + Math.cos(angle)*coord.getY()]
}

// Function used by the html to increment the angle variable, to change the way objects appear on the canvas. This has nothing to do with the rotation of coordinates.
//TODO: hotkey this and make it finer opposed to 90 degree jumps.
function rotate(){
    angle = (angle + Math.PI/2)%(Math.PI*2);
    console.log(angle.toString());
}

//These functions are responsible for GET-ing and POST-ing.
function sendFeatures(){
    $.post("/", {objects: jsonDump()});
}

function hand(data){ // Turn json into objects
    people = JSON.parse(data.toString());
}

function getPeople(){
    $.get("/people.json", function(data){
        people = JSON.parse(data.toString().trim());
        $(".slider").slider({max: people[0].length, min: 0});
        $(".slider").slider({slide: function( event, ui ) { populate(ui.value)}});
        populate(0);
    });
}

function traceToggle(){
    if(trace){
        trace = false;
    }
    else{
        trace = true;
    }
}

function cursorPixelToggle(){
    if(debug){
        stage.removeEventListener("stagemousemove", cursorPixels);
        stage.removeChild(cursorItemPixel);
        stage.update();
        debug = false;
    }
    else{
        debug = stage.addEventListener("stagemousemove", cursorPixels);
        cursorItemPixel = new createjs.Text("0, 0", "15px Arial", "#000");
        cursorItemPixel.x = 0;
        stage.addChild(cursorItemPixel);
    }
}

function cursorPixels(e){

    cursorItemPixel.x = e.stageX + 25; cursorItemPixel.y = e.stageY + 25;
    cursorItemPixel.text = e.stageX + ", " + e.stageY;
    stage.update();
}

function simulate(option){

    //Start from beginning.
    if(option == 1){

        time = 0;

        if(interval){ // Has already been started, therefore needs to be started from the beginning with no traces and new arrays
            window.clearInterval(interval);
            //This now causes a lag spike - All of this people drawing stuff needs to be reworked.
            if(canvasTraces){
                for(var i = 0; i < canvasTraces.length; i++){
                    stage.removeChild(canvasTraces[i]);
                }
                stage.update();
            }
            canvasTraces = new Array();
        }
        interval = window.setInterval(function(){populate(time);time++}, 100);
    }
    //Continue from where we currently are.
    else if (option ==2){
        window.clearInterval(interval);
        interval = window.setInterval(function(){populate(time);time++}, 100);
    }
    //Pause/Stop.
    else{
        window.clearInterval(interval);
    }
}

function drawInterest(e){
    
    if(stage.mouseInBounds){

        //Increases readability since they are accessed multiple times.
        features.push(new Feature(featureID, 2)); canvasFeatures.push(new createjs.Shape());
        endOfArray++; featureID++;

        var canvasCircle = canvasFeatures[endOfArray];
        var circle = features[endOfArray];

        //Drawing
        if(e.nativeEvent.shiftKey){
            circle.setToCoords(Math.round(e.stageX/50)*50, Math.round(e.stageY/50)*50);
            circle.setFromCoords(Math.round(e.stageX/50)*50, Math.round(e.stageY/50)*50);
        }
        else{
            circle.setToCoords(e.stageX, e.stageY);
            circle.setFromCoords(e.stageX, e.stageY);
        }

        canvasCircle.graphics.setStrokeStyle(3).beginStroke("red").drawCircle(circle.getFromCoords()["x"], circle.getFromCoords()["y"], 15).endStroke(); 

        stage.addChild(canvasCircle);
        stage.update();
    }
}

//TODO: Consider mouseObject method which handles all possible cursor objects
function mouseInterest(e){

    if(cursorItem)cursorItem.graphics.clear();
    cursorItem = new createjs.Shape();

    if(e.nativeEvent.shiftKey){
       cursorItem.graphics.setStrokeStyle(3).beginStroke("red").drawCircle(Math.round(e.stageX/50)*50, Math.round(e.stageY/50)*50, 15).endStroke(); 
    }
    else{
       cursorItem.graphics.setStrokeStyle(3).beginStroke("red").drawCircle(e.stageX, e.stageY, 15).endStroke(); 
    }
    stage.addChild(cursorItem);
    stage.update();
}

function showBlockages(){

}

/*
client todo list.

mouse scroll - cba
drawable areas of interest
COMPLETELY REDESIGN how people are being drawn on the canvas. (to reduce lag)

kill mode bug

feedback from the server (only useful when server webserver component has been remodelled.)
*/