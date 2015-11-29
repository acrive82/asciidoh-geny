package it.geny.asciidoh.verticles

import io.vertx.core.Future
import io.vertx.groovy.ext.web.Router
import io.vertx.groovy.redis.RedisClient
import io.vertx.lang.groovy.GroovyVerticle

import java.util.concurrent.TimeUnit

/**
 * Created by luigi on 29/11/15.
 */
class AsciiDocVerticle extends GroovyVerticle {

    def log = io.vertx.core.logging.LoggerFactory.getLogger(AsciiDocVerticle.class.name)
    def host = io.vertx.groovy.core.Vertx.currentContext().config()["host"]

    @Override
    void start(Future<Void> startFuture) throws Exception {
        super.start(startFuture)

        /* Routing */
        def router = Router.router(vertx)
        router.get("/book").handler(this.&handleBookRequest)

        /* Server */
        vertx.createHttpServer().requestHandler(router.&accept).listen(8080, { result ->
            if (result.succeeded()) {
                startFuture.complete
                log.info("AsciiDocVerticle - Started")
            } else {
                startFuture.fail(result.cause())
                log.error("oops!", result.cause())
            }
        })

    }

    @Override
    void stop(Future<Void> stopFuture) throws Exception {
        super.stop(stopFuture)
    }

    def handleBookRequest(routingContext) {

        def response = routingContext.response()

        /* Redis */
        if (host == null) {
            host = "127.0.0.1"
        }
        def client = RedisClient.create(vertx, [host: host])
        if (!client) {
            response.putHeader("content-type", "text/html").end("NO REDIS")
        }

        /* Let's go */
        def bookname = routingContext.request().getParam("book") ?: ""
        log.info("Check on redis for BOOK ${bookname}")
        client.get(bookname, { redisResult ->
            if (redisResult.succeeded()) {
                def result = redisResult.result()
                if (result) {
                    response.putHeader("content-type", "text/html").end(result)                                         // From REDIS
                } else {
                    produceAndStoreInRedis(bookname, response, client)                                                  // From FILE
                }
            } else {
                routingContext.response().putHeader("content-type", "text/html").end("Error")
            }
        })


    }

    def produceAndStoreInRedis(String name, response, RedisClient client) {
        def commandString = "asciidoc -o /home/luigi/tmp/output_${name}.html -b html5 -a icons -a toc2 -a theme=flask /home/luigi/${name}"
        def command = commandString.execute()
        command.waitForOrKill(5000L)
        if (command.exitValue()) {
            log.error(command.err.text)
        } else {
            def file = new File("/home/luigi/tmp/output_${name}.html")
            def text = file.text
            file.delete()
            client.set(name, text, { r ->
                if (r.succeeded()) {
                    log.info("Data stored in REDIS")
                } else {
                    log.error("WRONG")
                }
            })
            response.putHeader("content-type", "text/html").end(text)
        }
    }
}
