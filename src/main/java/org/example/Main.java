package org.example;

import org.example.p2p.Node;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        String name = args[0];
        int port = Integer.parseInt(args[1]);
        Node a = new Node(name, "localhost", port);
        a.startHost();
        while (true){
            a.createBlock();
        }
    }
}
