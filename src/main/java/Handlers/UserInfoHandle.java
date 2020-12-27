package Handlers;

import Store.Store;
import Store.User;
import TcpServer.BaseRequest;
import TcpServer.Response.BaseResponse;
import TcpServer.BaseServerHandler;
import TcpServer.Response.JsonResponse;
import com.alibaba.fastjson.JSONArray;
import utils.Serializer.Serializer;

import java.util.ArrayList;

public class UserInfoHandle extends BaseServerHandler {

    /**
     * 订阅(注册)用户的认证信息
     * @return e.g
     * [
     *     {
     *         "cookie": {
     *             "fid": "2xx",
     *             "UID": "103194xxx",
     *             "_d": "160908408xxxx",
     *             "vc3": "WrxxxxxxWn1miuyNgJ3fpWAsy50XJwR6OVu0PHFN0D9%2B00HtXnkS131RwjpgNUL%2ByrAYPWEdEDgWzfVUNgzTgTKWqYJ6ujOL7e1j2pBSaAlZ2sNf6E%2BTYFTfxB%2FFahV6XoBv%2FYJpPwO0gxvHl3bkIXYBftdlZWWq5pXLK1b%2FZA%3De9f7a6d73f9e863ffbf70a8721e3613d"
     *         },
     *         "publicKey": "a9d548792999424ea653349e900db5de"
     *     }
     * ]
     */
    public BaseResponse post(BaseRequest request) {
        ArrayList<User> userInfo = Store.userInfo;
        Serializer serializer = new Serializer(userInfo);
        String[] includeFields = new String[]{"cookie", "publicKey"};
        JSONArray userInfoJsonObj = serializer.serializeToArray(includeFields);
        return new JsonResponse(request.exchange, userInfoJsonObj);
    }

}
