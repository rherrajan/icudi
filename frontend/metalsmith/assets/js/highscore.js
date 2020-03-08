
function requestHighscore() {
	var getCellsURL = createBackendURL("highscore");
		
	var xhttp = new XMLHttpRequest();
	xhttp.onreadystatechange = function() {
		if (this.readyState == 4) {
			if(this.status == 200){
			    var responseJsonData = JSON.parse(xhttp.responseText);
				showHighscore(responseJsonData);
			} else {
				alert("could not connect to database. http status: " + this.status);
			}
		};
	}
	
	xhttp.open("GET", getCellsURL, true);
	xhttp.send();
}



function showHighscore(data) {
  var highscoreDisplay = document.getElementsByClassName("highscore-display")[0];

  
  const markup = `
	<ul class="highscore">
	    ${data.map(element => `<li><font color="${getColor(element[0], 1)}">Spieler</font> hat ${element[1]} Punkte</li>`)}
	</ul>
  `;

  highscoreDisplay.innerHTML = markup;    
}

function appendScore(element, listContainer) {
	 var textnode = document.createTextNode("<li>" + element + "</li>");         // Create a text node
	 listContainer.appendChild(textnode);  
}


$(document).ready(requestHighscore)
