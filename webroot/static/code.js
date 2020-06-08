const listContainer = document.querySelector('#service-list');
let servicesRequest = new Request('/service');
fetch(servicesRequest)
.then(function(response) { return response.json(); })
.then(function(serviceList) {
  serviceList.forEach(service => {
    var li = document.createElement("li");
    li.appendChild(document.createTextNode(service.name));
    var serviceInfo = document.createElement("ul");
    var url = document.createElement("li");
    url.appendChild(document.createTextNode("URL : " + service.url));
    var status = document.createElement("li");
    status.appendChild(document.createTextNode("STATUS : " + service.status));
    var created = document.createElement("li");
    created.appendChild(document.createTextNode("CREATED AT : " + service.created));
    serviceInfo.appendChild(url);
    serviceInfo.appendChild(status);
    serviceInfo.appendChild(created)
    li.appendChild(serviceInfo);
    listContainer.appendChild(li);
  });
});

const saveButton = document.querySelector('#post-service');
saveButton.onclick = evt => {
    let name = document.querySelector('#name').value;
    let url = document.querySelector('#url').value;
    fetch('/service', {
    method: 'post',
    headers: {
    'Accept': 'application/json, text/plain, */*',
    'Content-Type': 'application/json'
    },
  body: JSON.stringify({url:url, name:name})
}).then(function(res){
    if(res.statusText == "OK"){
        location.reload();
    }
   });
}


const deleteButton = document.querySelector('#delete-service');
deleteButton.onclick = evt => {
    let name = document.querySelector('#name').value;
    let url = document.querySelector('#url').value;
    fetch('/service/delete', {
    method: 'post',
    headers: {
    'Accept': 'application/json, text/plain, */*',
    'Content-Type': 'application/json'
    },
  body: JSON.stringify({url:url, name:name})
}).then(res=> location.reload());
}