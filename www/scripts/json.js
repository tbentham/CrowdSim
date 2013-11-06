//x, y tuple
function Coordinate(x, y){
	this.x = x;
	this.y = y;

	this.setX = function(x){
		this.x = x;
	}

	this.setY = function(y){
		this.y = y;
	}

	this.getX = function(){
		return x;
	}

	this.getY = function(){
		return y;
	}
}

// "Feature" - wall, door, window etc.
// type ought to be predefined and understood by both server and client.
function Feature(id, type){
	this.id = id;
	this.type = type;

	this.getID = function(){
		return id;
	};

	this.getType = function(){
		return type;
	};

	// Is JS garbage collected?
	this.setFromCoords = function(x, y){
		this.from = new Coordinate(x, y);
	};

	this.setToCoords = function(x,y){
		this.to = new Coordinate(x, y);
	};

	this.getFromCoords = function(){
		return this.from;
	};

	this.getToCoords = function(){
		return this.to;
	};
}
