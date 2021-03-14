package server.handler;

import server.request.Request;

import java.io.BufferedOutputStream;

@FunctionalInterface
public interface Handler {

    void handle(Request request, BufferedOutputStream out);
}