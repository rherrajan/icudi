function createBackendURL(path){
	if(location.hostname==="localhost"){
		return "http://localhost:5000/" + path;
	} else if(isIpAddress(location.hostname)) {
		return "http://" + location.hostname + ":5000/" + path;
	} else {
		return location.protocol
		    + '//'+subdomain(location.hostname)
		    + '.herokuapp.com/'
		    + path;
	}
}

function callForResult(path, callback) {
	var pathURL = createBackendURL(path)				
	var xhttp = new XMLHttpRequest();
	xhttp.onreadystatechange = function() {
		if (this.readyState == 4) {
			if (this.status == 200) {
				if (typeof callback !== "function") {
					console.log("callback needs to be a function. But was: ", callback);
				}
				
				try {
				  var data = JSON.parse(xhttp.responseText);
				  callback(data);
				} catch (e) {
				   console.error("could not parse response. Error: " + e + ". Data: \n", data);
				}

			} else {
				console.log("error in called url. status=" + this.status + " url=" + path);
			}
		};
	}
	xhttp.open("GET", pathURL, true);
	xhttp.send();
}

function onBackendAvailablility(callback) {
	var pathURL = createBackendURL("systeminfo")				
	var xhttp = new XMLHttpRequest();
	xhttp.onreadystatechange = function() {
		if (this.readyState == 4) {
			if(this.status != 0){
				if (typeof callback === "function") {
				    callback();
				} else {
					alert("callback needs to be a function. But was: " + typeof callback);
				}
			} else{
			  console.log("backend currently unavailable. readyState=" + this.readyState + " status=" + this.status + " for url: " + pathURL);
			  setTimeout(function() {onBackendAvailablility(callback)}, 10000);
			}
		};
	}
	xhttp.open("GET", pathURL, true);
	xhttp.send();
}

function subdomain(host) {
    var part = host.split('.').reverse(),
        index = 0;

    while (part[index].length === 2 || !index) {
        ++index;
    }
    ++index;

    return part.length > index && part[index] !== 'www' ? part[index] : '';
}

function isIpAddress(ipaddress) {  
  if (/^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$/.test(ipaddress)) {  
    return (true)  
  }  
  return (false)  
}
