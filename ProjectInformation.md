## Project Information
In this file you will find information about the issues that I have worked on with a description of my thinking process.

Please note that I have spent exactly 4 hours on this project and I couldn't focus on writing tests.

### Critical Issues
#### Whenever the server is restarted, any added services disappear

When I received the project, the services were stored in a Hash map which meant that everytime we restarted the service, we started from a clean HashMap.

To rectify this I used a DB. The Get request will query all services from the DB and the POST request will add to the DB.

With more time I would have prefered to keep the Hash map and use it as a cache, or implement a more appropriate cache.

#### There's no way to delete individual services
I created a new route ```service/delete``` to delete a service from the DB.

I have added a button ```delete```. You have to enter the url and name of the service you want to delete.

With more time I would have prefered to add a selection process where you can select all the services you want to delete, and press ```delete```.

#### We want to be able to name services and remember when they were added
I modified the ```DBMigration.java``` file to create a table ```service``` with  url, name, status, and created columns.

The created column has a default value of ```CURRENT_TIMESTAMP```

I also added an input text field in the form to add the name of the service.

#### The HTTP poller is not implemented

The poller was supposed to take the services hash map as a parameter but I thought that we can just query the services from the DB and update the status.

Because we have a simple get request for each URL I decide to use APache http client fluent API.

I have decided that only a status code of 200 will be a success, the rest of the codes will be considered as fails. This of course can be modified.

### Backend extra issues

#### Service URL's are not validated in any way ("sdgf" is probably not a valid service)

For this issue I decided to use the ready URLValidator from Apache.

We only accepte urls with a prefix ```http``` or ```https```.

If a URL is not valid, I return a status code ```400(Bad Request)```.

#### Protect the poller from misbehaving services (for example answering really slowly)

For this issue I have added a timeout to the poller.
