package org.example.block;

import com.google.common.hash.Hashing;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;

public class Block implements Serializable {
    private static final long serialVersionUID = 1L;

    private final int index;
    private String hash;
    private final String previousHash;
    private final String data;
    private Long nonce = 0L;


    public Block(int index, String preHash, String data) {
        this.index = index;
        this.previousHash = preHash;
        this.data = data;
        hash = calculateHash(index + previousHash + data );
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public int getIndex() {
        return index;
    }


    public String getHash() {
        return hash;
    }

    public String getPreviousHash() {
        return previousHash;
    }

    private String calculateHash(String text) {
        String sha256hex = "";
        while(!sha256hex.endsWith("0000")){
            sha256hex = Hashing.sha256()
                    .hashString(text + nonce.toString(), StandardCharsets.UTF_8)
                    .toString();
            nonce ++;
        }
        return  sha256hex;

    }

    public String getData() {
        return data;
    }

    @Override
    public String toString() {
        return "Block{" +
                "index=" + index +
                ", nonce=" + nonce +
                ", data=" + data +
                ", hash='" + hash + '\'' +
                ", previousHash='" + previousHash + '\'' +
                '}';
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Block block = (Block) o;
        return index == block.index
                && nonce.equals(block.nonce)
                && hash.equals(block.hash)
                && previousHash.equals(block.previousHash)
                && data.equals(block.data);
    }
}