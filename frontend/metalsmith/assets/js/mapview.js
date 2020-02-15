
boardCenterLat = 50.0075;
boardCenterLng = 8.266;

latTileDistance = 0.0005;
lngTileDistance = 0.001;

function loadMap(){

  // initialize map
  map = L.map('mapDiv', { zoomControl: false }).setView([boardCenterLat, boardCenterLng], 18);
  map.touchZoom.disable();
  map.doubleClickZoom.disable();
  map.scrollWheelZoom.disable();

  var lc = L.control.locate({
    position: 'bottomright',
	flyTo: true,
	keepCurrentZoomLevel: true
  }).addTo(map);

  mapCells = L.layerGroup();
  mapCells.addTo(map);

  playerCells = L.layerGroup();
  playerCells.addTo(map);
  
  // copyright
  L.tileLayer( 'https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
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
			    var responseJsonData = JSON.parse(xhttp.responseText);
				drawMap(responseJsonData, lat, lng);
			} else {
				alert("could not connect to database. http status: " + this.status);
			}
		};
	}
	
	xhttp.open("GET", getCellsURL, true);
	xhttp.send();
}

function onDragEnd(newCenter) {
	var lat = Math.floor(newCenter.lat / latTileDistance) * latTileDistance;
	var lng = Math.floor(newCenter.lng / lngTileDistance) * lngTileDistance;
	requestRedraw(lat, lng);
}

function drawMap(cellData, lat, lng) {
  drawTiles(lat, lng);
  drawPlayerTiles(lat, lng, cellData);
}

function drawTiles(lat, lng) {
  mapCells.clearLayers();
  for (x = -2; x <= +2; x++) {
    for (y = -6; y <= +6; y++) {
      drawTile(mapCells, lat+(x*latTileDistance), lng+(y*lngTileDistance));
    }
  }
}

function drawPlayerTiles(lat, lng, cellData) {
  playerCells.clearLayers();
  for(var cellid in cellData) {
    var cell = cellData[cellid];
    drawPlayerTile( boardCenterLat+(latTileDistance*cell.x), boardCenterLng+(lngTileDistance*cell.y), cell.owner, cell.value);
  }
}

function drawPlayerTile(lat, lng, owner, value) {
	var bounds = [[lat, lng], [lat+0.0005, lng+0.001]];
	var tileColor = getColor(owner, value);   
	var fillOpacity = 0.2 + 0.1 * (value-1);
	if (fillOpacity > 0.9) {
	  fillOpacity = 0.9
	} 
	
	var rect = L.rectangle(bounds, {color: tileColor, fillOpacity: fillOpacity}).on('click', function (e) {
	    this.setStyle({
		    color: 'white'
		});
		requestPlayerRedraw(e);
	}).addTo(playerCells);
	
}

function drawTile(mapCells, lat, lng) {
	var bounds = [[lat, lng], [lat+0.0005, lng+0.001]];
	var tileColor = 'grey'  
	var rect = L.rectangle(bounds, {color: tileColor, weight: 1}).on('click', function (e) {
	    this.setStyle({
		    color: 'white'
		});
		requestPlayerRedraw(e);
	}).addTo(mapCells);
}

function requestPlayerRedraw(e) {
	var clickDetectionURL = createBackendURL("click") + "?lat=" + e.latlng.lat + "&lng=" + e.latlng.lng + "&uuid=" + getUUID();
	
	var xhttp = new XMLHttpRequest();
	
	xhttp.onreadystatechange = function() {
		if (this.readyState == 4) {
			if(this.status == 200){
			    var responseJsonData = JSON.parse(xhttp.responseText);
				drawPlayerTiles(e.latlng.lat, e.latlng.lng, responseJsonData);
			} else {
				alert("could not connect to database. http status: " + this.status);
			}
		};
	}
	
	xhttp.open("GET", clickDetectionURL, true);
	xhttp.send();
}

function startGame() {
	
	var questname = document.getElementsByClassName("questname")[0];
	questname.innerHTML="...";
	
	var getQuestsURL = createBackendURL("getQuests") + "?uuid=" + getUUID() + "&lat=" + map.getCenter().lat + "&lng=" + map.getCenter().lng;
						
	var xhttp = new XMLHttpRequest();
	xhttp.onreadystatechange = function() {
		if (this.readyState == 4) {
			if(this.status == 200){
			    var responseJsonData = JSON.parse(xhttp.responseText);
				var questname = document.getElementsByClassName("questname")[0];
				questname.innerHTML=responseJsonData.query.geosearch[0].title;
			} else {
				alert("could not connect to database. http status: " + this.status);
			}
		};
	}
	
	xhttp.open("GET", getQuestsURL, true);
	xhttp.send();
}




