package se.kry.codetest;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.sql.ResultSet;
import io.vertx.ext.sql.SQLClient;
import se.kry.codetest.model.Service;
import se.kry.codetest.model.ServiceStatus;

import java.util.List;
import java.util.stream.Collectors;

public class DBConnector {

  private final String DB_PATH = "poller.db";
  private final SQLClient client;

  public DBConnector(Vertx vertx){
    JsonObject config = new JsonObject()
        .put("url", "jdbc:sqlite:" + DB_PATH)
        .put("driver_class", "org.sqlite.JDBC")
        .put("max_pool_size", 30);

    client = JDBCClient.createShared(vertx, config);
  }

  public Future<ResultSet> query(String query) {
    return query(query, new JsonArray());
  }


  public Future<ResultSet> query(String query, JsonArray params) {
    if(query == null || query.isEmpty()) {
      return Future.failedFuture("Query is null or empty");
    }
    if(!query.endsWith(";")) {
      query = query + ";";
    }

    Future<ResultSet> queryResultFuture = Future.future();

    client.queryWithParams(query, params, result -> {
      if(result.failed()){
        queryResultFuture.fail(result.cause());
      } else {
        queryResultFuture.complete(result.result());
      }
    });
    return queryResultFuture;
  }

  public Future<List<Service>> getAllServices() {
    Future<List<Service>> resultFuture = Future.future();
    query("select ROWID, S.* from service S;").setHandler( resultSet ->
                    resultFuture.complete(resultSet.result().getRows(true).stream()
                            .map(row -> {
                              return new Service(
                                      row.getInteger("rowid"),
                                      row.getString("name"),
                                      row.getString("url"),
                                      ServiceStatus.valueOf(row.getString("status")),
                                      row.getInstant("created"));
                            })
                            .collect(Collectors.toList()))
            );
    return resultFuture;
  }

  public Future<ResultSet> addService(String url, String name){
    String query = Util.getQueryBuilder(url, name, "UNKNOWN");
    Future<ResultSet> resultFuture = Future.future();
    query(query).setHandler(resultSet -> resultFuture.complete(resultSet.result()));
    return resultFuture;

  }

  public Future<ResultSet> deleteService(String id){
    String query = Util.deleteQueryBuilder(id);
    Future<ResultSet> resultFuture = Future.future();
    query(query).setHandler(resultSet -> resultFuture.complete(resultSet.result()));
    return resultFuture;

  }

  public Future<ResultSet> updateService(String url, String name, String status){
    String query = Util.updateQueryBuilder(url, name, status);
    Future<ResultSet> resultFuture = Future.future();
    query(query).setHandler(resultSet -> resultFuture.complete(resultSet.result()));
    return resultFuture;

  }


}
