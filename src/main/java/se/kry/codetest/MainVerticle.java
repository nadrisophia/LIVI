package se.kry.codetest;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.sql.ResultSet;

import java.util.List;

public class MainVerticle extends AbstractVerticle {
  private DBConnector connector;
  private BackgroundPoller poller = new BackgroundPoller();

  @Override
  public void start(Future<Void> startFuture) {
    connector = new DBConnector(vertx);
    Router router = Router.router(vertx);
    router.route().handler(BodyHandler.create());
    vertx.setPeriodic(1000*60, timerId -> poller.pollServices(connector));
    setRoutes(router);
    vertx
        .createHttpServer()
        .requestHandler(router)
        .listen(8080, result -> {
          if (result.succeeded()) {
            System.out.println("KRY code test service started");
            startFuture.complete();
          } else {
            startFuture.fail(result.cause());
          }
        });
  }

  private void setRoutes(Router router){
    router.route("/*").handler(StaticHandler.create());
    router.get("/service").handler(req -> {
      Future<ResultSet> queryResult = connector.query("select * from service;");
      queryResult.setHandler(asyncResult -> {
        if(asyncResult.succeeded()) {
          List<JsonObject> jsonServices = asyncResult.result().getRows();
          req.response()
                  .putHeader("content-type", "application/json")
                  .end(new JsonArray(jsonServices).encode());
        }
      });

    });
    router.post("/service").handler(req -> {
      JsonObject jsonBody = req.getBodyAsJson();
      String url = jsonBody.getString("url");
      if(Util.isValidUrl(url)){
        String query = Util.getQueryBuilder(url, jsonBody.getString("name"), "Unknown");
        connector.query(query);
        req.response()
                .putHeader("content-type", "text/plain")
                .end("OK");
      }else{
        req.response()
                .putHeader("content-type", "text/plain")
                .setStatusCode(400)
                .end("Url not valid");
      }

    });
    router.post("/service/delete").handler(req -> {
      JsonObject jsonBody = req.getBodyAsJson();
      String query = Util.deleteQueryBuilder(jsonBody.getString("url"), jsonBody.getString("name"));
      connector.query(query);
      req.response()
              .putHeader("content-type", "text/plain")
              .end("OK");
    });
  }



}



