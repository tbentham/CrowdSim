function NodeRecord(){

	this.connection = false;

	this.setNode = function(num){
		this.node = num
		return true
	}

	this.getNode = function(){
		return this.node
	}

	this.setConn = function(node){
		this.connection = node;
		return true
	}

	this.getConn = function(){
		return this.connection;
	}

	this.setCostSoFar = function(cost){
		this.csf = cost;
		return true
	}

	this.getCostSoFar = function(){
		return this.csf
	}
}

function Graph(size){ // size is currently the number of nodes.

	//Create node lookup table
	this.nodes = new Array(size);

	//Make each node's index an array to store connections.
	for(var i=0; i < this.nodes.length; i++){
		this.nodes[i] = new Array();
	}

	this.getConns = function(node){
		return this.nodes[node.getNode()]
	}

	this.addConn = function(conn){
		this.nodes[conn.getFromNode()].push(conn);
		return true;
	}

	this.dumpNodes = function(){
		console.log(JSON.stringify(this.nodes));
	}
}

function Connection(from, to, cost){

	this.fromNode = from;
	this.toNode = to;
	this.cost = cost;

	this.getFromNode = function(){
		return this.fromNode
	}

	this.getToNode = function(){
		return this.toNode
	}

	this.getCost = function(){
		return this.cost
	}
}

function PathfindingList(){

	this.list = new Array(); // Horrible implementation for testing purposes, will eventually make this some lovely JS structure with quick access speeds and wank.

	this.add = function(node){
		this.list.push(node);
	}

	this.remove = function(node){
		//remove the node from the list.
		// Add some failsafe shit to all this array access.
		this.list.splice(this.list.indexOf(node), 1);
	}

	this.getLength = function(){
		return this.list.length
	}

	this.smallestElement = function(){
		//This is where the magic happens which determines how efficient this whole thing is.

		//Another horrible implementation for proof of concept purposes.
		var a = this.list[0];
		var r;

		for(var i = 0; i < this.list.length; i++){
			if(this.list[i].getCostSoFar() < a.getCostSoFar()){
				a = this.list[i];
			} 
		}
		return a
	}

	this.contains = function(nodeNum){
		//Moar efficiency magic - consider using existing js data structure
		for(var i = 0; i < this.list.length; i++){
			if(nodeNum == this.list[i].getNode())return true;
		}
		return false;
	}

	this.fetch = function(node){
		return this.list[this.list.indexOf(node)]
	}
}

// This is the only thing that changes when you change the "start" value in the dijkstras function
// This should really be seen as the result - the map defining shortest paths to that start pooint.

function NodeList(start, size){

	this.start = start;
	this.list = new Array(size);

	for(var i=0; i < this.list.length; i++){
		this.list[i] = new NodeRecord();
	}

	this.fetch = function(nodeNum){
		return this.list[nodeNum]
	}

	this.print = function(){

		for(var i = 0; i < this.list; i++){
			console.log(JSON.stringify(this.list[i]));
		}
	}
}

function pathfindDijkstra(graph, start){ // no Goal

	//Initialize the record for the start node
	var startRecord = new NodeRecord();
	startRecord.setNode(start); // Set it to node 1 (or zero)
	startRecord.setConn(false);
	startRecord.setCostSoFar(0);

	//Initialize the open and closed lists
	var open = new PathfindingList();
	//Adding a nodeRecord here
	open.add(startRecord);
	var closed = new PathfindingList();

	// # Iterate through processing each node
	while(open.getLength() > 0){
		console.log("I am iterating");

		//Find the smallest element in the open list
		//This should return a nodeRecord
		var current = open.smallestElement();

		//Otherwise get its outgoing connections
		var connections = graph.getConns(current);
		if(!connections)continue;

		// # Loop through each connection in turn
		for (var i=0; i < connections.length; i++){

			//Get the cost estimate for the end node
			var endNode = connections[i].getToNode();
			var endNodeCost = current.getCostSoFar() + connections[i].getCost();

			//Skip if the node is closed
			if(closed.contains(endNode))continue;

			// if it's open and we’ve found a worse route
			else if(open.contains(endNode)){
				// Here we find the record in the open list
				// corresponding to the endNode.
				var endNodeRecord = open.fetch(endNode);

				if(endNodeRecord.getCostSoFar() <= endNodeCost)continue; // This is correct so far
				endNodeRecord.setConn(connections[i]);
				endNodeRecord.setCostSoFar(endNodeCost);
			}

				// Otherwise we know we’ve got an unvisited node, so make a record for it
			else{
				var endNodeRecord = new NodeRecord();
				endNodeRecord.setNode(endNode);
				// We’re here if we need to update the node
				// Update the cost and connection
				endNodeRecord.sStCostSoFar(endNodeCost);
				endNodeRecord.setConn(connections[i]);
				// And add it to the open list
				if(!open.contains(endNode)){
					open.add(endNodeRecord);
				}
			}
		}
		open.remove(current);
		closed.add(current);
	}
		// We’ve finished looking at the connections for
		// the current node, so add it to the closed list
		// and remove it from the open list
}


g = new Graph(5);

g.addConn(new Connection(0, 1, 5));
g.addConn(new Connection(0, 2, 4));
g.addConn(new Connection(1, 2, 4));
g.addConn(new Connection(2, 3, 3));
g.addConn(new Connection(3, 4, 1));


document.write("done");