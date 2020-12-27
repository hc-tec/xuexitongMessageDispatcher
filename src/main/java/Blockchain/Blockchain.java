package Blockchain;

import java.util.ArrayList;

import Blockchain.Transaction.Transaction;
import config.Settings;

abstract class AbstractBlockchain {

    abstract public void new_txn(Transaction txn);

    abstract public void new_block(Integer proof);

    abstract public Block last_block();

}

public class Blockchain extends AbstractBlockchain {

    public ArrayList<Block> chain; // 区块链
    public ArrayList<Transaction> txns; // 当前交易，还未并入到区块中

    public Blockchain() {
        this.chain = new ArrayList<Block>();
        this.txns = new ArrayList<Transaction>();
        Block genesis_block = new Block(); // 创世区块
        this.chain.add(genesis_block);
    }

    /**
     * 更新当前的交易信息
     * @param txn Blockchain.Transaction 类型，表示当前的交易信息
     */
    @Override
    public void new_txn(Transaction txn) {
        this.txns.add(txn);
        // 当交易量达到 BLOCK_TXNS 个以上时，合成新区块
        if(this.txns.size() >= Settings.BLOCK_TXNS) {
            // 所有交易的工作量累加和
            final int[] proof = {0};
            this.txns.forEach((Transaction _txn) -> {
                proof[0] += _txn.proof;
            });
            this.new_block(proof[0]);
        }
    }

    /**
     * 根据交易信息及上一个区块合成当前区块
     * @param proof 把此区块所有交易的工作量的和当作区块的工作量
     */
    @Override
    public void new_block(Integer proof) {
        Block cur_block = new Block(this.last_block(), proof, this.txns);
        this.chain.add(cur_block);
        // 当前区块创建，清空交易信息
        this.txns.clear();
    }

    /**
     * 获取到区块链的最后一个区块
     * @return Block 类型，最后一个区块
     */
    @Override
    public Block last_block() {
        int length = this.chain.size();
        return this.chain.get(length - 1);
    }




}
