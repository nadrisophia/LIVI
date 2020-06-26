package se.kry.codetest;

import io.vertx.core.Future;
import org.apache.http.client.fluent.Request;
import se.kry.codetest.model.Service;

import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

public class BackgroundPoller {

  public Future<List<String>> pollServices(DBConnector connector){
    // I am not sure aboput using parallel streams here, some requests might be long and blocking resources for all the stuff taht's happening.
    //i'm not sure how vertx works for these type of situations i need more research
    // Also i would probable use the poller as a different component (maybe in its own verticle? not sure..
    List<String> l = new ArrayList<>();
    connector.getAllServices().setHandler(asyncResult -> {
      if(asyncResult.succeeded()) {
        List<Service> services = asyncResult.result();
        for(Service service : services){
          String url = service.getUrl();
          String name = service.getName();
          int s = 0;
          try {
            s = Request.Get(url)
                    .connectTimeout(1000)
                    .socketTimeout(1000)
                    .execute().returnResponse().getStatusLine().getStatusCode();
          } catch (IOException e) {
            System.out.println("Poll failed for " + url);
            e.printStackTrace();
          }finally{
            connector.updateService(url, name, s == 200 ? "OK" : "FAILED" );
            l.add(s == 200 ? "OK" : "FAILED");
          }
        }
      }
    });

    return Future.succeededFuture(l);
  }
}
