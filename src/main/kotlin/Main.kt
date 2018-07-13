import com.github.xy02.raas.nats.NatsNode
import io.vertx.core.Vertx
import io.vertx.core.buffer.Buffer
import io.vertx.core.http.HttpMethod
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.BodyHandler
import java.util.concurrent.TimeUnit

fun main(args: Array<String>) {
    println("Hello World!")
    val node = NatsNode()
    val vertx = Vertx.vertx()
    val router = Router.router(vertx)

    // This body handler will be called for all routes
    router.route().handler(BodyHandler.create())

    router.route(HttpMethod.POST, "/fn/:serviceName")
            .handler({ ctx ->
                val req = ctx.request()
                val serviceName = req.getParam("serviceName")
                node.callUnaryService(serviceName, ctx.body.bytes, 5, TimeUnit.SECONDS)
                        .doOnSuccess { data -> ctx.response().end(Buffer.buffer(data)) }
                        .subscribe()
            })

    vertx.createHttpServer()
            .requestHandler({ router.accept(it) })
            .listen(8080)
}

