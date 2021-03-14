package server.request;

import java.io.*;
import java.util.*;

public class Request {
    public static final String GET = "GET";
    public static final String POST = "POST";
    private final String method;
    private final String path;
    private final Map<String, String> headers;
    private final InputStream in;

    private Request(String method, String path, Map<String, String> headers, InputStream in) {
        this.method = method;
        this.path = path;
        this.headers = headers;
        this.in = in;
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public InputStream getIn() {
        return in;
    }

    private static Optional<String> extractHeader(List<String> headers, String header) {
        return headers.stream()
                .filter(o -> o.startsWith(header))
                .map(o -> o.substring(o.indexOf(" ")))
                .map(String::trim)
                .findFirst();
    }

    private static void badRequest(BufferedOutputStream out) throws IOException {
        out.write((
                "HTTP/1.1 400 Bad Request\r\n" +
                        "Content-Length: 0\r\n" +
                        "Connection: close\r\n" +
                        "\r\n"
        ).getBytes());
        out.flush();
    }

    private static int indexOf(byte[] array, byte[] target, int start, int max) {
        outer:
        for (int i = start; i < max - target.length + 1; i++) {
            for (int j = 0; j < target.length; j++) {
                if (array[i + j] != target[j]) {
                    continue outer;
                }
            }
            return i;
        }
        return -1;
    }

    public static Request fromInputStream(InputStream in, BufferedOutputStream out) throws IOException {
        final var allowedMethods = List.of(GET, POST);
        var reader = new BufferedReader(new InputStreamReader(in));
        final var limit = 4096;

        in.mark(limit);
        final var buffer = new byte[limit];
        final var read = in.read(buffer);

        final var requestLineDelimiter = new byte[]{'\r', '\n'};
        final var requestLineEnd = indexOf(buffer, requestLineDelimiter, 0, read);
        if (requestLineEnd == -1) {
            badRequest(out);
        }

        final var requestLine = new String(Arrays.copyOf(buffer, requestLineEnd)).split(" ");
        if (requestLine.length != 3) {
            badRequest(out);
        }

        final var method = requestLine[0];
        if (!allowedMethods.contains(method)) {
            badRequest(out);
        }

        final var path = requestLine[1];
        if (!path.startsWith("/")) {
            badRequest(out);
        }

        final var headersDelimiter = new byte[]{'\r', '\n', '\r', '\n'};
        final var headersStart = requestLineEnd + requestLineDelimiter.length;
        final var headersEnd = indexOf(buffer, headersDelimiter, headersStart, read);
        if (headersEnd == -1) {
            badRequest(out);
        }

        in.reset();
        in.skip(headersStart);

        final var headersBytes = in.readNBytes(headersEnd - headersStart);
        final var headerss = Arrays.asList(new String(headersBytes).split("\r\n"));
        System.out.println(headerss);

//        final var parts = requestLine.split(" ");
//
//        if (parts.length != 3) {
//            throw new IOException("Server.request.Request is invalid");
//        }

//        var method = parts[0];
//        var path = parts[1];

        Map<String, String> headers = new HashMap<>();
        String headerLine;
        while (!(headerLine = reader.readLine()).equals("")) {
            var i = headerLine.indexOf(":");
            var headerName = headerLine.substring(0, i);
            var headerValue = headerLine.substring(i + 2);
            headers.put(headerName, headerValue);
        }

        if (!method.equals(GET)) {
            in.skip(headersDelimiter.length);
            final var contentLength = extractHeader(headerss, "Content-Length");
            if (contentLength.isPresent()) {
                final var length = Integer.parseInt(contentLength.get());
                final var bodyBytes = in.readNBytes(length);

                final var body = new String(bodyBytes);
                System.out.println(body);
            }
        }
        return new Request(method, path, headers, in);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Request.class.getSimpleName() + "[", "]")
                .add("method='" + method + "'")
                .add("path='" + path + "'")
                .add("headers=" + headers)
                .toString();
    }
}