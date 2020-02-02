function loadMap(){

/*
	var mapURL = createBackendURL("databaseMap");
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

	xhttp.open("GET", mapURL, true);
	xhttp.send();
*/
	
	drawMap();
	
};

function drawMap() {
  
  var lat = 50.0075;
  var lon = 8.266;
  
  // initialize map
  map = L.map('mapDiv').setView([lat, lon], 18);
  drawTiles(map, lat, lon);
  
  // copyright
  L.tileLayer( 'http://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
	  attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a>',
	  subdomains: ['a','b','c']
  }).addTo( map );


}

function drawTiles(map, lat, lon) {
  drawTile(map, lat-0.0005, lon-0.001);
  drawTile(map, lat-0.0005, lon);
  drawTile(map, lat-0.0005, lon+0.001);
  
  drawTile(map, lat, lon-0.001);
  drawTile(map, lat, lon);
  drawTile(map, lat, lon+0.001);
  
  drawTile(map, lat+0.0005, lon-0.001);
  drawTile(map, lat+0.0005, lon);
  drawTile(map, lat+0.0005, lon+0.001);
}

function drawTile(map, lat, lon) {
	var bounds = [[lat, lon], [lat+0.0005, lon+0.001]];
	var rect = L.rectangle(bounds, {color: 'grey', weight: 1}).on('click', function (e) {
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
	/*
	xhttp.onreadystatechange = function() {

		if (this.readyState == 4) {

			if(this.status == 200){
				drawMap(JSON.parse(xhttp.responseText));
			} else {
				alert("could not connect to database. http status: " + this.status);
			}
		};
	}
	*/
	
	xhttp.open("GET", clickDetectionURL, true);
	xhttp.send();
	
	}).addTo(map);
}
