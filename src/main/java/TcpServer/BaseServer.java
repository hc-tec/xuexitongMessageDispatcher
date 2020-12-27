package TcpServer;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;


public class BaseServer {

    public HttpServer server;

    public BaseServer (Integer port) throws IOException {
        this.server = HttpServer.create(new InetSocketAddress(port), 0);
    }

    public void register(HashMap<String, BaseServerHandler> patterns) {
        for(String url : patterns.keySet()) {
            this.server.createContext(url, patterns.get(url));
        }
        this.server.setExecutor(null);
        this.server.start();
    }

}
