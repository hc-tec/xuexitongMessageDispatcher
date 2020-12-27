package Handlers;

import java.util.ArrayList;
import java.util.HashMap;

import Blockchain.Blockchain;
import Blockchain.Transaction.Transaction;
import Store.Store;
import Store.User;
import com.alibaba.fastjson.JSONObject;

import Blockchain.Block;
import TcpServer.Response.BaseResponse;
import TcpServer.Response.ErrorResponse;

import TcpServer.BaseServerHandler;
import TcpServer.BaseRequest;
import TcpServer.Response.JsonResponse;
import config.Settings;
import config.Status;
import config.StatusCode;

import Main.Main;
import utils.Email.EmailSender;

public class MineHandler extends BaseServerHandler {

    /**
     * 挖矿 API
     * @param request 封装后的请求，
     *                提交的参数: {
     *                  "secretKey": String // 矿工的私钥
     *                  "publicKey": String // 消息接收者的公钥
     *                  "proof": Integer // 工作量证明
     *                  "hash": String // 此次交易的 hash
     *                  "msgId": String // 消息 id
     *                  "msgContent": String // 消息内容
     *                  "timestamp": String // 时间戳
     *                }
     * @return
     */
    public BaseResponse post(BaseRequest request) {

        HashMap<String, String> data = request.data;
        if(data == null) {
            return new ErrorResponse(request.exchange,
                    StatusCode.InvalidParams.code,
                    StatusCode.InvalidParams.msg);
        }
        String senderSecretKey = data.get("secretKey");
        String receiverPublicKey = data.get("publicKey");
        Integer proof = Integer.valueOf(data.get("proof"));
        String hash = data.get("hash");
        String msgId = data.get("msgId");
        String msgContent = data.get("msgContent");
        String timestamp = data.get("timestamp");
        Double coin = Settings.MINE_COIN;
        // 获取接收者信息, 验证消息是否最新
        User receiver = Store.getUserByPublicKey(receiverPublicKey);
        assert receiver != null;
        User sender = Store.getUserBySecretKey(senderSecretKey);
        assert sender != null;
        // 统一验证，对参数有效性、工作量证明、消息是否已经存在等情况统一验证
        Status status = this.totalValidate(senderSecretKey, receiverPublicKey,
                        proof, timestamp, hash, msgId, receiver);
        if(status != null) {
            return new ErrorResponse(request.exchange,
                    status.code, status.msg);
        }
        // 是否无矿可挖
        if(this.isScanTxn(receiverPublicKey)) {
            // 两次无矿可挖情况时间间隔是否符合
            if(this.isTimeValid(sender.lastMineTs)) {
                coin = Settings.SCAN_COIN;
            } else {
                return new ErrorResponse(request.exchange,
                        StatusCode.IntervalTooShort.code, StatusCode.IntervalTooShort.msg);
            }
        } else {
            // 向接收者 QQ 发送消息
            this.emailSend(receiver.qq, msgContent);
            // 更新消息列表
            receiver.addVisMsgId(msgId);
        }
        // 发送者获取币值激励
        sender.addAccount(coin);
        // 交易添加到区块链中
        this.addToChain(senderSecretKey, receiverPublicKey, msgId, proof);
        // 发送者时间戳更新
        sender.logTimestamp();
        JSONObject ret = new JSONObject();
        ret.put("account", sender.account);
        ret.put("mine_coin", coin);
        return new JsonResponse(request.exchange, ret);
    }

    /**
     * 参数是否有效
     */
    private Boolean isParamsValid(
            String senderSecretKey, String receiverPublicKey,
            Integer proof, String timestamp, String hash, String msgId) {
        return senderSecretKey != null &&
                receiverPublicKey != null &&
                proof != null &&
                timestamp != null &&
                hash != null &&
                msgId != null;
    }

    /**
     * 工作量证明
     * @param proof 矿工的工作量
     * @param timestamp 矿工提交的时间戳
     * @param hash 矿工计算出的 hash 值
     * @return 工作量是否有效
     */
    private Boolean isProofValid(Integer proof, String timestamp, String hash) {
        Block lastBlock = Main.blockchain.last_block();
        ArrayList<Transaction> txns = Main.blockchain.txns;
        return Block.valid_proof(lastBlock.hash, proof, timestamp, txns, hash);
    }

    /**
     * 是否为扫描式的请求，即扫描消息之后发现没有新消息时的无矿可挖的情况
     * 当 receiverPublicKey == "null" 时，将此种定义为无矿可挖，但矿工仍能获得激励的情况
     * @param receiverPublicKey 接受者公钥
     * @return 接受者公钥是否为 "null"
     */
    private Boolean isScanTxn(String receiverPublicKey) {
        return receiverPublicKey.equals("null");
    }

    /**
     * 无矿可挖时，矿工可每 SCAN_INTERVAL 分钟提交一次请求，证明其仍在耗费算力
     * 此时可以获取较少比值激励
     * @return 时间条件是否满足
     */
    private Boolean isTimeValid(long lastMineTs) {
        long curTimeStamp = System.currentTimeMillis();
        long interval = (curTimeStamp-lastMineTs) / (1000 * 60);
        return interval >= Settings.SCAN_INTERVAL;
    }

    /**
     * 统一验证，对参数有效性、工作量证明、消息是否已经存在等情况统一验证
     * @return Status, 当前状态
     */
    private Status totalValidate(
            String senderSecretKey, String receiverPublicKey,
            Integer proof, String timestamp, String hash, String msgId, User receiver) {
        // 验证参数是否有效
        if(!this.isParamsValid(senderSecretKey, receiverPublicKey, proof, timestamp, hash, msgId)) {
            return StatusCode.InvalidParams;
        }
        // 工作量证明
        if(!this.isProofValid(proof, timestamp, hash)) {
            return StatusCode.IncorrectProof;
        }
        if(!receiverPublicKey.equals("null") && receiver.isMsgIdExist(msgId)) {
            return StatusCode.MsgIdAlreadyExist;
        }
        return null;
    }

    /**
     * 邮箱转发
     * @param receiverQQ 接收者 QQ
     * @param msgContent 消息内容
     */
    private void emailSend(String receiverQQ, String msgContent) {
        EmailSender email = new EmailSender(Settings.emailSender, Settings.emailPassword);
        email.send("学习通新消息",
                msgContent, receiverQQ+"@qq.com");
    }

    /**
     * 将此次交易加入链中
     * @param sender 矿工
     * @param recipient 信息接收者
     * @param msgId 消息 id
     * @param proof 工作量
     */
    private void addToChain(
            String sender, String recipient,
            String msgId, Integer proof) {
        Transaction txn = new Transaction(sender, recipient, msgId, proof);
        Blockchain chain = Main.blockchain;
        chain.new_txn(txn);
    }
}
