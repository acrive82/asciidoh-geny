package it.geny.asciidoh.verticles

import io.vertx.core.Future
import io.vertx.groovy.ext.web.Router
import io.vertx.lang.groovy.GroovyVerticle

/**
 * Created by luigi on 29/11/15.
 */
class SampleVerticle extends GroovyVerticle {


    def logger = io.vertx.core.logging.LoggerFactory.getLogger(SampleVerticle.class.name)



    @Override
    void start(Future<Void> future) throws Exception {

        def router = Router.router(vertx)
        router.get("/").handler(this.&defaultHandler)
        router.get("/hello").handler(this.&handleHelloRequest)
        vertx.createHttpServer().requestHandler(router.&accept).listen(8080, { result ->
            if (result.succeeded()) {
                future.complete
                logger.info("SampleVerticle - Started")
            } else {
                future.fail(result.cause())
                logger.error("oops!", result.cause())

            }
        })
    }

    @Override
    void stop() throws Exception {
        println "stopping"
    }


    def handleHelloRequest(routingContext) {
        def name = routingContext.request().getParam("name") ?: "world"
        def response = routingContext.response()
        response.putHeader("content-type", "text/html").end("<h2>Sciao, ${name}</h2>")

    }

    def defaultHandler(routingContext) {
        def response = routingContext.response()
        response.end("<h1>Vert.x 3 Spaccaaaa!</h1>")
    }
}

