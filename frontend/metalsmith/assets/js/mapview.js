function loadMap(){

	var getCellsURL = createBackendURL("getCells") + "?uuid=" + getUUID();

	var xhttp = new XMLHttpRequest();
	xhttp.onreadystatechange = function() {
		if (this.readyState == 4) {
			if(this.status == 200){
				drawMap(JSON.parse(xhttp.responseText));
			} else {
				alert("could not connect to database. http status: " + this.status);
			}
		};
	}

	xhttp.open("GET", getCellsURL, true);
	xhttp.send();
};

function drawMap(cellData) {
  
  var lat = 50.0075;
  var lon = 8.266;
  
  // initialize map
  map = L.map('mapDiv').setView([lat, lon], 18);
  drawTiles(map, lat, lon, cellData);
  
  // copyright
  L.tileLayer( 'http://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
	  attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a>',
	  subdomains: ['a','b','c']
  }).addTo( map );

}

function changeMap(cellData) {
  var lat = 50.0075;
  var lon = 8.266;
  drawTiles(map, lat, lon, cellData);
}


function drawTiles(map, lat, lon, cellData) {

  drawTile(map, lat-0.0005, lon-0.001);
  drawTile(map, lat-0.0005, lon);
  drawTile(map, lat-0.0005, lon+0.001);
  
  drawTile(map, lat, lon-0.001);
  drawTile(map, lat, lon);
  drawTile(map, lat, lon+0.001);
  
  drawTile(map, lat+0.0005, lon-0.001);
  drawTile(map, lat+0.0005, lon);
  drawTile(map, lat+0.0005, lon+0.001);
  
  
  console.log(" --- cellData: ", cellData);
  
  for(var cellid in cellData) {
   var cell = cellData[cellid];
   drawTile(map, lat+(0.0005*cell.x), lon+(0.001*cell.y), cell.owner);
  }
	
}

function drawTile(map, lat, lon, owner) {
	var bounds = [[lat, lon], [lat+0.0005, lon+0.001]];
	
	var tileColor;
	if(owner){
	  tileColor = 'red';
	} else {
	  tileColor = 'grey';
	}
	    
	var rect = L.rectangle(bounds, {color: tileColor, weight: 1}).on('click', function (e) {
	    // There event is event object
	    // there e.type === 'click'
	    // there e.lanlng === L.LatLng on map
	    // there e.target.getLatLngs() - your rectangle coordinates
	    // but e.target !== rect
	    
	    //console.info(e);
	    
	    console.info(" --- e: " + e, e);
	    

	    this.setStyle({
		    color: 'white'
		});
		
		var clickDetectionURL = createBackendURL("click") + "?lat=" + e.latlng.lat + "&lng=" + e.latlng.lng + "&uuid=" + getUUID();



	var xhttp = new XMLHttpRequest();

	xhttp.onreadystatechange = function() {
		if (this.readyState == 4) {
			if(this.status == 200){
				changeMap(JSON.parse(xhttp.responseText));
			} else {
				alert("could not connect to database. http status: " + this.status);
			}
		};
	}
	
	xhttp.open("GET", clickDetectionURL, true);
	xhttp.send();
	
	}).addTo(map);
}
