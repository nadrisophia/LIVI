package se.kry.codetest;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.ResultSet;
import org.apache.http.client.fluent.Request;

import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

public class BackgroundPoller {

  public Future<List<String>> pollServices(DBConnector connector){

    Future<ResultSet> queryResult = connector.query("select * from service;");
    List<String> l = new ArrayList<>();
    queryResult.setHandler(asyncResult -> {
      if(asyncResult.succeeded()) {
        List<JsonObject> jsonServices = asyncResult.result().getRows();
        for(JsonObject service : jsonServices){
          String url = service.getString("url");
          String name = service.getString("name");
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
            String query = Util.updateQueryBuilder(url, name, s == 200 ? "OK" : "FAILED" );
            connector.query(query);
            l.add(s == 200 ? "OK" : "FAILED");
          }
        }
      }
    });

    return Future.succeededFuture(l);
  }
}
