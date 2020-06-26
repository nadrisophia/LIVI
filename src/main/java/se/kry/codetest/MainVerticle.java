package se.kry.codetest;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;


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
    router.get("/service").handler(this::getAll);
    router.post("/service").handler(this::addOne);
    router.delete("/service/:id").handler(this::deleteOne);
  }

  private void getAll(RoutingContext routingContext) {
    connector.getAllServices().setHandler(asyncResult -> {
      if(asyncResult.succeeded()) {
        routingContext.response().setStatusCode(200)
                .putHeader("content-type", "application/json")
                .end(Json.encodePrettily(asyncResult.result()));
      }
    });
  }

  private void addOne(RoutingContext routingContext) {
    JsonObject jsonBody = routingContext.getBodyAsJson();
    String url = jsonBody.getString("url");
    String name = jsonBody.getString("name");
    if(Util.isValidUrl(url)){
      connector.addService(url, name).setHandler(asyncResult -> {
        if(asyncResult.succeeded()){
          routingContext.response()
                  .setStatusCode(201)
                  .putHeader("content-type", "text/plain")
                  .end("OK");
        }else{
          routingContext.response()
                  .putHeader("content-type", "text/plain")
                  .setStatusCode(500)
                  .end("Create failed");
        }
      });
    }else{
      routingContext.response()
              .putHeader("content-type", "text/plain")
              .setStatusCode(400)
              .end("Invalid URL");
    }
  }

  private void deleteOne(RoutingContext routingContext) {
    String id = routingContext.request().getParam("id");
    if (id == null) {
      routingContext.response().setStatusCode(400).end();
    } else {
      connector.deleteService(id).setHandler(asyncResult -> {
        if(asyncResult.succeeded()){
          routingContext.response()
                  .setStatusCode(204)
                  .putHeader("content-type", "text/plain")
                  .end("OK");
        }else{
          routingContext.response()
                  .putHeader("content-type", "text/plain")
                  .setStatusCode(500)
                  .end("Delete failed");
        }
      });
    }

  }



}



