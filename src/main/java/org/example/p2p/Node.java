package org.example.p2p;

import org.example.Utils;
import org.example.block.Block;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import static org.example.p2p.Message.MESSAGE_TYPE.*;
public class Node {

    private final String name;
    private final String address;
    private final int port;
    private List<Integer> peers;
    private List<Block> blockchain = new ArrayList<>();

    private ServerSocket serverSocket;
    private final ScheduledThreadPoolExecutor executor
            = new ScheduledThreadPoolExecutor(10);

    private boolean listening = true;


    public Node(final String name, final String address, final int port) {
        this.name = name;
        this.address = address;
        this.port = port;
         setPeers();
        blockchain.add(Utils.firstBlock());
    }

    public String getName() {
        return name;
    }



    public int getPort() {
        return port;
    }

    public List<Block> getBlockchain() {
        return blockchain;
    }

    public Block createBlock() {
        if (blockchain.isEmpty()) {
            return null;
        }
        Block previousBlock = getLatestBlock();
        if (previousBlock == null) {
            return null;
        }
        final int index = previousBlock.getIndex() + 1;
        String data = Utils.generateData();
        final Block block = new Block(index, previousBlock.getHash(), data);
        broadcast(INFO_NEW_BLOCK, block);
        return block;
    }

    boolean addBlock(Block block) {
        if (isBlockValid(block)) {
            blockchain.add(block);
            return true;
        }
        return false;
    }

    public void startHost() {
        executor.execute(() -> {
            try {
                serverSocket = new ServerSocket(port);
                System.out.printf("Server %s started%n", serverSocket.getLocalPort());
                listening = true;
                while (listening) {
                    final NodeServerThread thread = new NodeServerThread(Node.this, serverSocket.accept());
                    thread.start();
                }
                serverSocket.close();
            } catch (IOException e) {
                System.err.println("Could not listen to port " + port);
            }
        });
        broadcast(REQ_ALL_BLOCKS, null);
    }

    public void stopHost() {
        listening = false;
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Block getLatestBlock() {
        if (blockchain.isEmpty()) {
            return null;
        }
        return blockchain.get(blockchain.size() - 1);
    }

    private boolean isBlockValid(final Block block) {
        final Block latestBlock = getLatestBlock();
        if (latestBlock == null) {
            return false;
        }
        final int expected = latestBlock.getIndex() + 1;
        if (block.getIndex() != expected) {
//            System.out.println(String.format("Invalid index. Expected: %s Actual: %s", expected, block.getIndex()));
            return false;
        }
        if (!Objects.equals(block.getPreviousHash(), latestBlock.getHash())) {
//            System.out.println("Unmatched hash code");
            return false;
        }
        return true;
    }

    private void broadcast(Message.MESSAGE_TYPE type, final Block block) {
        peers.forEach(peer -> sendMessage(type, address, peer, block));
    }

    private void sendMessage(Message.MESSAGE_TYPE type, String host, int port, Block... blocks) {
        try (
                final Socket peer = new Socket(host, port);
                final ObjectOutputStream out = new ObjectOutputStream(peer.getOutputStream());
                final ObjectInputStream in = new ObjectInputStream(peer.getInputStream())) {
            Object fromPeer;
            while ((fromPeer = in.readObject()) != null) {
                if (fromPeer instanceof Message) {
                    final Message msg = (Message) fromPeer;
//                    System.out.printf("%d received: %s%n", this.port, msg.toString());
                    if (READY == msg.type) {
                        out.writeObject(new Message.MessageBuilder()
                                .withType(type)
                                .withReceiver(port)
                                .withSender(this.port)
                                .withBlocks(Arrays.asList(blocks)).build());
                    } else if (RSP_ALL_BLOCKS == msg.type) {
                        if (!msg.blocks.isEmpty() && this.blockchain.size() == 1) {
                            blockchain = new ArrayList<>(msg.blocks);
                        }
                        break;
                    }
                }
            }
        } catch (UnknownHostException e) {
            System.err.printf("Unknown host %s %d%n", host, port);
        } catch (IOException e) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    private void setPeers(){
        peers = Arrays.asList(Utils.FIRST_PORT,Utils.SECOND_PORT,Utils.THIRD_PORT);
    }

}
