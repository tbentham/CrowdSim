
var stage;
var lines = new Array();
var endOfArray = -1;  // TODO: Fix this hacky as hell "tail access"
var id = 0;
var click = false;
var angle = 0;
var cursorItem;

var drawModes = new Array(); // Check if JS requires this to be present for the script to load properly, otherwise, initialise and fill this at the end of script.

function init() {
    //Add some shit here about loading people.
    stage = new createjs.Stage("mainCanvas");
    drawMode(0);
    stage.update();
}

function drawMode(mode){
    console.log("Changing to draw mode " + mode.toString());
    if(mode == 0){
        stage.removeAllEventListeners();
        stage.addEventListener("stagemousedown", drawModes[mode][0]);
        stage.addEventListener("stagemousemove", drawModes[mode][1]);
        stage.addEventListener("stagemouseup", drawModes[mode][2]);   
    }
    else if(mode == 1){
        stage.removeAllEventListeners();
        stage.addEventListener("stagemousedown", drawDoor);
	    stage.addEventListener("stagemousemove", mouseDoor);
    }

}

// Cannot implement wall highlighting or checking since canvas walls are NOT vector objects. This might also become a pain when it comes to clipping.
function mouseDoor(e){

    if(cursorItem)cursorItem.graphics.clear();
    cursorItem = new createjs.Shape();

    //Find the coordinates of a door after it has been rotated, remember to add StageX and Y to translate from the origin to the mouse pointer.
    //TODO: consider a translation method, this way I can keep all the graphics transformations in one tidy place.
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

    door = new createjs.Shape();
    arc = new createjs.Shape();

    // This is just a rectangle, consider using premade Rectangle objects that may handle rotations and will fess up their vertices on demand.
    //TODO: consider a translation method, this way I can keep all the graphics transformations in one tidy place.
    var hinge = spin(new Coordinate(-25, 0), angle); hinge[0] = hinge[0] + e.stageX; hinge[1] = hinge[1] + e.stageY;
    var close = spin(new Coordinate(25, 0), angle); close[0] = close[0] + e.stageX; close[1] = close[1] + e.stageY;
    var open = spin(new Coordinate(-25, 50), angle); open[0] = open[0] + e.stageX; open[1] = open[1] + e.stageY;
    var arcthrough = spin(new Coordinate(25, 50), angle); arcthrough[0] = arcthrough[0] + e.stageX; arcthrough[1] = arcthrough[1] + e.stageY;

    if(e.nativeEvent.shiftKey){
        door.graphics.beginStroke("rgba(125,170,195,1)").moveTo(Math.round(hinge[0]/50)*50, Math.round(hinge[1]/50)*50).lineTo(Math.round(open[0]/50)*50, Math.round(open[1]/50)*50).endStroke();
        arc.graphics.beginStroke("rgba(125,170,195,1)").moveTo(Math.round(open[0]/50)*50, Math.round(open[1]/50)*50).arcTo(Math.round(arcthrough[0]/50)*50, Math.round(arcthrough[1]/50)*50, Math.round(close[0]/50)*50, Math.round(close[1]/50)*50, 50).endStroke();
    }
    else{
        door.graphics.beginStroke("rgba(125,170,195,1)").moveTo(hinge[0], hinge[1]).lineTo(open[0], open[1]).endStroke();
        arc.graphics.beginStroke("rgba(125,170,195,1)").moveTo(open[0], open[1]).arcTo(arcthrough[0], arcthrough[1], close[0], close[1], 50).endStroke();
    }
   
    stage.addChild(door);
    stage.addChild(arc);
    stage.update();
}

function startLine(e){

    if(stage.mouseInBounds){
        click = true;

        lines.push({line: new Feature(id, 0), lineShape: new createjs.Shape()});
        endOfArray++; id++;

        if(e.nativeEvent.shiftKey){
            console.log("Shift is down");
            lines[endOfArray]["line"].setFromCoords(Math.round(e.stageX/50)*50, Math.round(e.stageY/50)*50);
            lines[endOfArray]["line"].setToCoords(Math.round(e.stageX/50)*50, Math.round(e.stageY/50)*50);
        }
        else{
            lines[endOfArray]["line"].setFromCoords(e.stageX, e.stageY);
            lines[endOfArray]["line"].setToCoords(e.stageX, e.stageY);
        }

        stage.addChild(lines[endOfArray]["lineShape"]);
        stage.update();
    }
}

function endLine(e){

    //Check click here to ensure the click wasnt made outside the canvas - which would result in the last line being changed.
    if(click && stage.mouseInBounds){
        click = false;

        //Increases readability since they are accessed multiple times.
        var line = lines[endOfArray]["line"];
        var lineShape = lines[endOfArray]["lineShape"];

        if(e.nativeEvent.shiftKey){
            console.log("Shift is down");
            line.setToCoords(Math.round(e.stageX/50)*50, Math.round(e.stageY/50)*50);
        }
        else{
            line.setToCoords(e.stageX, e.stageY);
        }

        lineShape.graphics.clear();
	lineShape.graphics.beginStroke("black").setStrokeStyle(3.0).moveTo(line.getFromCoords()["x"], line.getFromCoords()["y"]).lineTo(line.getToCoords()["x"], line.getToCoords()["y"]).endStroke();
        stage.update();
    }
}

function drawLine(e){

    if(click && stage.mouseInBounds){

        //Increases readability since they are accessed multiple times.
        var line = lines[endOfArray]["line"];
        var lineShape = lines[endOfArray]["lineShape"];

        if(e.nativeEvent.shiftKey){
            console.log("Shift is down");
            line.setToCoords(Math.round(e.stageX/50)*50, Math.round(e.stageY/50)*50);
        }
        else{
            line.setToCoords(e.stageX, e.stageY);
        }
        
        lineShape.graphics.clear();
        lineShape.graphics.beginStroke("black").setStrokeStyle(3.0).moveTo(line.getFromCoords()["x"], line.getFromCoords()["y"]).lineTo(line.getToCoords()["x"], line.getToCoords()["y"]).endStroke();
        stage.update();
    }

}

function jsonDump(){
    $(".undercanvas").empty();
    for(var i = 0; i < lines.length; i++){
        $(".undercanvas").append("<br/>" + JSON.stringify(lines[i]["line"]));
    }
}

function clearCanvas(){

    for(var i = 0; i < stage.children.length; i++){
        stage.children[i].graphics.clear();
    }
    
    lines = new Array();
    endOfArray = -1;
    stage.update();
}

// 2D rotation of a pair of coordinates. Some graphics shit.
// This certainly needs a test.
function spin(coord, angle){
    //TODO: change this to return type COORDINATE - otherwise developers will begin to feel uncertain about things whilst pouring their coffee.
    return[Math.cos(angle)*coord.getX() +((-Math.sin(angle))*coord.getY()), Math.sin(angle)*coord.getX() + Math.cos(angle)*coord.getY()]
}

//Temporary function used by the canvas control button to spin shit.
//TODO: hotkey this and make it finer opposed to 90 degree jumps.
function rotate(){
    angle = (angle + Math.PI/2)%(Math.PI*2);
    console.log(angle.toString());
}

drawModes.push([startLine, drawLine, endLine]);
drawModes.push([startLine, drawLine, endLine]); //This will eventually be some other drawing mode, and won't always be a series of pushes for each drawing mode.

