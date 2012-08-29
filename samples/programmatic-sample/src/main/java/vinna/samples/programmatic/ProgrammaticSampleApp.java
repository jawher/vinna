package vinna.samples.programmatic;

import vinna.Vinna;

import java.lang.Object;
import java.lang.String;
import java.util.Map;

public class ProgrammaticSampleApp extends Vinna {

    @Override
    protected void routes(Map<String, Object> config) {
        get("/user/{id: \\d+}")
                .withController(Controller.class)
                .test(param("id").asInt());

        get("/user/{id: \\d+}/{name}")
                .withController(Controller.class)
                .test2(param("name").asString(), param("id").asInt());

        get("/hello/{hello: true|false}/{id}/{name}")
                .withController(Controller.class)
                .test2(param("name").asString(), param("hello").asBoolean(), param("id").asInt());

        get("/hello/{hello}/{id}/{name}")
                .withController(Controller.class)
                .printHeaders(req.headers());

        get("/constant/{test}")
                .withController(Controller.class)
                .test2(param("test").asString(), constant(17));

        get("/header")
                .withController(Controller.class)
                .writeHeader(req.header("Accept").asString());

        get("/headers")
                .withController(Controller.class)
                .writeHeaders(req.header("Accept").asCollection(String.class));

        get("/allheaders")
                .withController(Controller.class)
                .printHeaders(req.headers());

        get("/allparams")
                .withController(Controller.class)
                .printHeaders(req.params());

        get("/paramsAsCollection")
                .withController(Controller.class)
                .paramCollection(req.param("a").asCollection(String.class));

        get("/mandatoryParam")
                .hasParam("goldMedal")
                .withController(Controller.class)
                .writeHeader(req.param("goldMedal").asString());

        get("/params")
                .withController(Controller.class)
                .params(req.param("a").asString(), req.param("b").asString(), req.param("c").asInt());

        post("post")
                .withController(Controller.class)
                .post(req.param("title").asString(), req.param("description").asString());
    }
}
