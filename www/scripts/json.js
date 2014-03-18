//x, y tuple
function Coordinate(x, y, z){
	this.x = x;
	this.y = y;
	this.z = z;

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

	this.getZ = function(){
		return z;
	}
}

// "Feature" - wall, door, window etc.
// 2 is poi, 3 is evac point, 4 is staircase
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
	this.setFromCoords = function(x, y, z){
		this.from = new Coordinate(x, y, z);
	};

	this.setToCoords = function(x,y, z){
		this.to = new Coordinate(x, y, z);
	};

	this.getFromCoords = function(){
		return this.from;
	};

	this.getToCoords = function(){
		return this.to;
	};
}
