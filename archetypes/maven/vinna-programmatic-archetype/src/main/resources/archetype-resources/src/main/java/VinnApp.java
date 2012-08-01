package ${package};

import ${package}.controllers.HelloControler;

import vinna.Vinna;

public class VinnApp extends Vinna {

  @Override
  protected void routes() {
    get("/hello/{name}").withController(HelloControler.class).sayHello(param("name").asString());
  }
}