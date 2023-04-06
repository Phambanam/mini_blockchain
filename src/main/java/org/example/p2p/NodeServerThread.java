package org.example.p2p;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import static org.example.p2p.Message.MESSAGE_TYPE.*;


public class NodeServerThread extends Thread {
    private final Socket client;
    private final Node node;

    NodeServerThread(final Node node, final Socket client) {
        super(node.getName() + System.currentTimeMillis());
        this.node = node;
        this.client = client;
    }

    @Override
    public void run() {
        try (
                ObjectOutputStream out = new ObjectOutputStream(client.getOutputStream());
                final ObjectInputStream in = new ObjectInputStream(client.getInputStream())) {
            Message message = new Message.MessageBuilder().withSender(node.getPort()).withType(READY).build();
            out.writeObject(message);
            Object fromClient;
            while ((fromClient = in.readObject()) != null) {
                if (fromClient instanceof Message) {
                    final Message msg = (Message) fromClient;

                    if (INFO_NEW_BLOCK == msg.type) {
                        if (msg.blocks.size() != 1) {
                            System.err.println("Invalid block received: " + msg.blocks);
                        }
                        synchronized (node) {
                            if (node.addBlock(msg.blocks.get(0))){
                                System.out.printf("%d received: %s%n", node.getPort(), fromClient);
                            };
                        }
                        break;
                    } else if (REQ_ALL_BLOCKS == msg.type) {
                        out.writeObject(new Message.MessageBuilder()
                                .withSender(node.getPort())
                                .withType(RSP_ALL_BLOCKS)
                                .withBlocks(node.getBlockchain())
                                .build());
                        break;
                    }
                }
            }
            client.close();
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
    }
}
