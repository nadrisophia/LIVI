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
    created.appendChild(document.createTextNode("CREATED AT : " + service.createdAt));
    serviceInfo.appendChild(url);
    serviceInfo.appendChild(status);
    serviceInfo.appendChild(created)
    li.appendChild(serviceInfo);
    let deleteButton = document.createElement("button");
    deleteButton.innerHTML = "delete";
    deleteButton.name='delete';
    deleteButton.id = 'del_'+ service.id;
    deleteButton.addEventListener("click", evt =>{
        fetch('/service/' + service.id, {
            method: 'delete',
            headers: {
                'Accept': 'application/json, text/plain, */*',
                'Content-Type': 'application/json'
            }
        }).then(function(res){
              if(res.status == 204){
                  location.reload();
              }else{
                  let errorMessage = document.querySelector('#message');
                  errorMessage.appendChild(document.createTextNode(res.json().toString()));
              }
             });
    });
    li.appendChild(deleteButton);
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
    if(res.status == 201){
        location.reload();
    }else{
        let errorMessage = document.querySelector('#message');
        errorMessage.appendChild(document.createTextNode("Invalid URL"));
    }
});
}
