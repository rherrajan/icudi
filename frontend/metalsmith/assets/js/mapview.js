
boardCenterLat = 50.0075;
boardCenterLng = 8.266;

latTileDistance = 0.0005;
lngTileDistance = 0.001;

function loadMap(){

  // initialize map
  map = L.map('mapDiv', { zoomControl: false }).setView([boardCenterLat, boardCenterLng], 18);
  zoomControl = L.control.zoom();
  map.on('zoomend', onZoomed);
  activateZoom(false);

  var lc = L.control.locate({
    position: 'bottomright',
	flyTo: true,
	keepCurrentZoomLevel: true
  }).addTo(map);

  mapCells = L.layerGroup();
  mapCells.addTo(map);

  playerCells = L.layerGroup();
  playerCells.addTo(map);
  
  hitMarkers = L.layerGroup();
  hitMarkers.addTo(map);
  
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

function onZoomed() {
	if(map.getZoom() < 18){
	  mapCells.clearLayers();
	} else {
	  onDragEnd(map.getCenter());
	}
}

function activateZoom(activate) {
	if(activate){
	  zoomControl.addTo(map);
	  map.touchZoom.disable();
	  map.doubleClickZoom.disable();
	  map.scrollWheelZoom.disable();
	} else {
	  zoomControl.remove();
	  map.touchZoom.enable();
	  map.doubleClickZoom.enable();
	  map.scrollWheelZoom.enable();
	}
}

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
  if(map.getZoom() < 18){
    return;
  }

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
	var tileColor = getColor(owner, 1);   
	var fillOpacity = 0.2 + 0.1 * (1);
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
			    if(responseJsonData.success){
			    	console.log("foundItem: ", responseJsonData.foundItem);
			        L.marker([responseJsonData.foundItem.lat, responseJsonData.foundItem.lon]).addTo(hitMarkers);
			    	alert(responseJsonData.foundItem.title + " gefunden");
					var action_button_zoom = document.getElementsByClassName("action_button_zoom")[0];
					action_button_zoom.style.display = "inline"; 
			    }
				drawPlayerTiles(e.latlng.lat, e.latlng.lng, responseJsonData.cells);
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
				var hit = responseJsonData.query.geosearch[0];
				console.log(hit.title + ": https://de.wikipedia.org/?curid="+hit.pageid);
				questname.innerHTML=hit.title;

				var wikipediaLink = document.getElementsByClassName("wikipedia-link")[0];
				wikipediaLink.href="https://de.wikipedia.org/?curid="+hit.pageid;
				
			} else {
				alert("could not connect to database. http status: " + this.status);
			}
		};
	}
	
	xhttp.open("GET", getQuestsURL, true);
	xhttp.send();
}




