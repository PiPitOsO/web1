import server.Server;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args) {
        var server = new Server(64);

        server.addHandler("GET", "/classic.html", (request, out) -> {
            try {
                final var filePath = Path.of("C:", "web1", request.getPath());
                final var mineType = Files.probeContentType(filePath);

                final var template = Files.readString(filePath);
                final var content = template.replace(
                        "{time}",
                        LocalDateTime.now().toString()
                ).getBytes();
                out.write((
                        "HTTP/1.1 200 OK\r\n" +
                                "Content-Type: " + mineType + "\r\n" +
                                "Content-Length: " + content.length + "\r\n" +
                                "Connection: close\r\n" +
                                "\r\n"
                ).getBytes());
                out.write(content);
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        server.addHandler("GET", "/index.html", (request, out) -> {
            try {
                final var filePath = Path.of("C:", "web1", request.getPath());
                final var mineType = Files.probeContentType(filePath);

                final var template = Files.readString(filePath);
                final var content = template.getBytes();
                out.write((
                        "HTTP/1.1 200 OK\r\n" +
                                "Content-Type: " + mineType + "\r\n" +
                                "Content-Length: " + content.length + "\r\n" +
                                "Connection: close\r\n" +
                                "\r\n"
                ).getBytes());
                out.write(content);
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        //основная
        server.addHandler("GET", "/messages.html", (request, out) -> {
            try {
                final var filePath = Path.of("C:", "web1", request.getPath());
                final var mineType = Files.probeContentType(filePath);

                final var template = Files.readString(filePath);
                final var content = template.getBytes();
                out.write((
                        "HTTP/1.1 200 OK\r\n" +
                                "Content-Type: " + mineType + "\r\n" +
                                "Content-Length: " + content.length + "\r\n" +
                                "Connection: close\r\n" +
                                "\r\n"
                ).getBytes());
                out.write(content);
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        server.listen(9999);
    }
}