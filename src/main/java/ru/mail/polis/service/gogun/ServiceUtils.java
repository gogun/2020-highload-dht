package ru.mail.polis.service.gogun;

import one.nio.http.HttpServerConfig;
import one.nio.http.HttpSession;
import one.nio.http.Request;
import one.nio.http.Response;
import one.nio.server.AcceptorConfig;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.function.Supplier;

final class ServiceUtils {

    /**
     * Util class.
     */
    private ServiceUtils() {
    }

    public static ByteBuffer getBuffer(final byte[] bytes) {
        return ByteBuffer.wrap(bytes);
    }

    public static byte[] getArray(final ByteBuffer buffer) {
        byte[] body;
        if (buffer.hasRemaining()) {
            body = new byte[buffer.remaining()];
            buffer.get(body);
        } else {
            body = Response.EMPTY;
        }

        return body;
    }

    /**
     * Method provides config for HttpServer.
     *
     * @param port       port
     * @param numWorkers threads in executor service
     * @return built config
     */
    @NotNull
    public static HttpServerConfig makeConfig(final int port, final int numWorkers) {
        final AcceptorConfig acceptorConfig = new AcceptorConfig();
        acceptorConfig.port = port;
        acceptorConfig.deferAccept = true;
        acceptorConfig.reusePort = true;

        final HttpServerConfig config = new HttpServerConfig();
        config.acceptors = new AcceptorConfig[]{acceptorConfig};
        config.selectors = numWorkers;

        return config;
    }

    /**
     * Method provides selecting action for method type.
     *
     * @param putRequest    action for put request
     * @param getRequest    action for get request
     * @param deleteRequest action for delete request
     * @param method        request type
     * @param session       http session
     * @throws IOException send response exception
     */
    public static void selector(final Supplier<Response> putRequest,
                                final Supplier<Response> getRequest,
                                final Supplier<Response> deleteRequest,
                                final int method,
                                final HttpSession session) throws IOException {
        switch (method) {
            case Request.METHOD_PUT:
                session.sendResponse(putRequest.get());
                break;
            case Request.METHOD_GET:
                session.sendResponse(getRequest.get());
                break;
            case Request.METHOD_DELETE:
                session.sendResponse(deleteRequest.get());
                break;
            default:
                session.sendResponse(new Response(Response.INTERNAL_ERROR, Response.EMPTY));
                break;
        }
    }
}
