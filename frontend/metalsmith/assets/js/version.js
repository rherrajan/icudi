
function requestVersion() {
	var getCellsURL = createBackendURL("systeminfo");
		
	var xhttp = new XMLHttpRequest();
	xhttp.onreadystatechange = function() {
		if (this.readyState == 4) {
			if(this.status == 200){
			    var responseJsonData = JSON.parse(xhttp.responseText);
				showVersion(responseJsonData);
			} else {
				alert("could not connect to database. http status: " + this.status);
			}
		};
	}
	
	xhttp.open("GET", getCellsURL, true);
	xhttp.send();
}

function showVersion(data) {
  var versionDisplay = document.getElementsByClassName("version-display")[0];
  versionDisplay.innerHTML = "buildtime: " + data.buildtime + "<br/>";
}

$(document).ready(requestVersion)
