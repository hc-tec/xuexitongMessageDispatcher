package config;

import java.util.HashMap;

import Handlers.*;
import TcpServer.BaseServerHandler;

public class Urls {

    public static HashMap<String, BaseServerHandler> patterns() {
        HashMap<String, BaseServerHandler> urlPatterns = new HashMap<String, BaseServerHandler>();

        urlPatterns.put("/mine", new MineHandler()); // 挖矿
        urlPatterns.put("/blockchain", new BlockchainHandle()); // 当前区块信息
        urlPatterns.put("/user", new UserInfoHandle()); // 用户信息
        urlPatterns.put("/lastBlock", new LastBlockHandle()); // 最后一个区块信息
        urlPatterns.put("/subscribeUser", new SubscribeUserHandle()); // 订阅接口，相当于注册
        urlPatterns.put("/transaction", new TransactionHandle()); // 当前交易信息

        return urlPatterns;
    }

}
