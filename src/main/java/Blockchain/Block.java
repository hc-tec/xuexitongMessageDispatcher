package Blockchain;

import java.util.ArrayList;

import com.alibaba.fastjson.JSON;

import utils.Crypto;
import Blockchain.Transaction.Transaction;

public class Block {

    public Integer index; // 索引，第几个区块
    public String timestamp; // 当前时间戳
    public Integer proof; // 工作量证明
    public String hash; // 当前 hash
    public String prev_hash; // 上一个区块 hash
    public ArrayList<Transaction> txns; // 当前区块保存的交易

    /**
     * 创世区块生成
     */
    public Block() {
        // 创世区块
        this.index = 1;
        this.timestamp = String.valueOf(System.currentTimeMillis());
        this.proof = 0;
        this.hash = Block.hash(this.index.toString());
        this.prev_hash = "0";
        this.txns = new ArrayList<Transaction>();
    }

    /**
     * 非创世区块生成
     * @param prev_block 上一个区块
     * @param txns 当前交易信息
     */
    public Block(Block prev_block, Integer proof, ArrayList<Transaction> txns) {
        this.index = prev_block.index + 1;
        this.timestamp = String.valueOf(System.currentTimeMillis());
        this.proof = proof;
        this.prev_hash = prev_block.hash;
        this.txns = txns;
        this.hash = Block.hash(JSON.toJSONString(txns)); // 对当前的所有交易信息进行 hash
    }

    /**
     * SHA256 算法
     * @param str 被 hash 的字符串
     * @return hash 后的字符串
     */
    public static String hash(String str) {
        return Crypto.SHA256(str);
    }

    /**
     * 工作量证明
     * @param prev_hash 上一个区块的 hash
     * @param proof 工作量证明
     * @param timestamp 矿工提交的时间戳
     * @param txns 当前交易，还未并入到区块中
     * @param hash 矿工计算出的 hash
     * @return Boolean 类型，表示工作量是否有效
     */
    public static Boolean valid_proof(
            String prev_hash, Integer proof,
            String timestamp, ArrayList<Transaction> txns, String hash) {
        // 计算当前交易的 proof 之和
        int sum = 0;
        for (Transaction txn : txns) {
            sum += txn.proof;
        }
        String validHash = Block.hash(prev_hash+proof.toString()+timestamp+sum);
        // 如果 hash 值以 0000 开头，则工作量证明通过
        String proofFlag = "0000";
        return validHash.equals(hash) &&
                hash.startsWith(proofFlag);
    }
}
