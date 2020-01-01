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
