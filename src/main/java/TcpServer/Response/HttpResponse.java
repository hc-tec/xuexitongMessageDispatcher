package TcpServer.Response;

import com.sun.net.httpserver.HttpExchange;

public class HttpResponse extends BaseResponse {

    public HttpResponse(HttpExchange exchange, String responseString) {
        super(exchange, responseString);
    }

}


