package Blockchain.Transaction;

public class Transaction {
    public String senderSecretKey; // 矿工私钥
    public String recipientPublicKey; // 信息接收者公钥
    public String msgId; // 消息 id
    public Integer proof; // 此次交易工作量
    public long timestamp; // 交易完成时间戳

    public Transaction(String sender, String recipient, String msgId, Integer proof) {
        this.senderSecretKey = sender;
        this.recipientPublicKey = recipient;
        this.msgId = msgId;
        this.proof = proof;
        this.timestamp = System.currentTimeMillis();
    }
}
