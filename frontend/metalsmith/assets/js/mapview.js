
boardCenterLat = 50.0075;
boardCenterLng = 8.266;

latTileDistance = 0.0005;
lngTileDistance = 0.001;

function loadMap(){

  // initialize map
  var map = L.map('mapDiv', { zoomControl: false }).setView([boardCenterLat, boardCenterLng], 18);
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
  
  requestRedraw(boardCenterLat, boardCenterLng);
	
  //Dragend event of map for update marker position
  map.on('dragend', function(e) {
    onDragEnd(map.getCenter());
  });
};

function requestRedraw(lat, lng) {
	var getCellsURL = createBackendURL("getCells") + "?uuid=" + getUUID() + "&lat=" + lat + "&lng=" + lng;
		
	var xhttp = new XMLHttpRequest();
	xhttp.onreadystatechange = function() {
		if (this.readyState == 4) {
			if(this.status == 200){
				drawMap(JSON.parse(xhttp.responseText), lat, lng);
			} else {
				alert("could not connect to database. http status: " + this.status);
			}
		};
	}
	
	xhttp.open("GET", getCellsURL, true);
	xhttp.send();
}

function onDragEnd(newCenter) {
	console.log(" ---  onDragEnd: ", newCenter);
	var lat = Math.floor(newCenter.lat / latTileDistance) * latTileDistance;
	var lng = Math.floor(newCenter.lng / lngTileDistance) * lngTileDistance;
	requestRedraw(lat, lng);
}

function drawMap(cellData, lat, lng) {
  drawTiles(mapCells, lat, lng, cellData);
}

function drawTiles(mapCells, lat, lng, cellData) {

  mapCells.clearLayers();
  
  for (x = -2; x <= +1; x++) {
    for (y = -5; y <= +4; y++) {
      drawTile(mapCells, lat+(x*latTileDistance), lng+(y*lngTileDistance));
    }
  }
  
  for(var cellid in cellData) {
   var cell = cellData[cellid];
   drawTile(mapCells, boardCenterLat+(latTileDistance*cell.x), boardCenterLng+(lngTileDistance*cell.y), cell.owner);
  }
	
}

function drawTile(mapCells, lat, lng, owner) {
	var bounds = [[lat, lng], [lat+0.0005, lng+0.001]];
	
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
				drawMap(JSON.parse(xhttp.responseText), lat, lng);
			} else {
				alert("could not connect to database. http status: " + this.status);
			}
		};
	}
	
	xhttp.open("GET", clickDetectionURL, true);
	xhttp.send();
	
	}).addTo(mapCells);
}