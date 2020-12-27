package Main;

import Blockchain.Blockchain;
import TcpServer.BaseServer;
import config.Settings;
import config.Urls;

import java.io.IOException;

public class Main {

    public static Blockchain blockchain = new Blockchain();

    public static void main(String[] args) throws IOException {
        BaseServer server = new BaseServer(Settings.port);
        server.register(Urls.patterns());
    }
}
