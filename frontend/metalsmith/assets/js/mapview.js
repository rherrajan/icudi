function loadMap(){

  centerLat = 50.0075;
  centerLng = 8.266;
  
  // initialize map
  var map = L.map('mapDiv', { zoomControl: false }).setView([centerLat, centerLng], 18);
  map.touchZoom.disable();
  map.doubleClickZoom.disable();
  map.scrollWheelZoom.disable();
    
  mapCells = L.layerGroup();
  mapCells.addTo(map);
  
  // copyright
  L.tileLayer( 'http://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
	  attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a>',
	  subdomains: ['a','b','c']
  }).addTo( map );
  
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
  drawTiles(mapCells, centerLat, centerLng, cellData);
}

function changeMap(cellData) {
  var lat = 50.0075;
  var lon = 8.266;
  drawTiles(mapCells, lat, lon, cellData);
}


function drawTiles(mapCells, lat, lon, cellData) {

  mapCells.clearLayers();
  
  drawTile(mapCells, lat-0.0005, lon-0.001);
  drawTile(mapCells, lat-0.0005, lon);
  drawTile(mapCells, lat-0.0005, lon+0.001);
  
  drawTile(mapCells, lat, lon-0.001);
  drawTile(mapCells, lat, lon);
  drawTile(mapCells, lat, lon+0.001);
  
  drawTile(mapCells, lat+0.0005, lon-0.001);
  drawTile(mapCells, lat+0.0005, lon);
  drawTile(mapCells, lat+0.0005, lon+0.001);
  
  for(var cellid in cellData) {
   var cell = cellData[cellid];
   drawTile(mapCells, lat+(0.0005*cell.x), lon+(0.001*cell.y), cell.owner);
  }
	
}

function drawTile(mapCells, lat, lon, owner) {
	var bounds = [[lat, lon], [lat+0.0005, lon+0.001]];
	
	var tileColor;
	if(owner){
	  tileColor = getColor(owner);
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
	    
	    this.setStyle({
		    color: 'white'
		});
		
		var clickDetectionURL = createBackendURL("click") + "?lat=" + e.latlng.lat + "&lng=" + e.latlng.lng + "&uuid=" + getUUID();



	var xhttp = new XMLHttpRequest();

	xhttp.onreadystatechange = function() {
		if (this.readyState == 4) {
			if(this.status == 200){
			    //mapCells.clearLayers();
			    
				changeMap(JSON.parse(xhttp.responseText));
			} else {
				alert("could not connect to database. http status: " + this.status);
			}
		};
	}
	
	xhttp.open("GET", clickDetectionURL, true);
	xhttp.send();
	
	}).addTo(mapCells);
}