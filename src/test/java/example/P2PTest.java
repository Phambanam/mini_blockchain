package example;

import org.example.Utils;
import org.example.block.Block;
import org.example.p2p.Node;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class P2PTest {
    private Node node1;
    private Node node2;
    private Node node3;

    @BeforeEach
    void setUp(){
        String address = "localhost";
        node1 = new Node("first", address, Utils.FIRST_PORT);
       node2 = new Node("second", address,Utils.SECOND_PORT);
       node3 = new Node("third", address,Utils.THIRD_PORT);
       node1.startHost();
       node2.startHost();
       node3.startHost();
   }
   @AfterEach
    void stopServer(){
       node1.stopHost();
       node2.stopHost();
       node3.stopHost();
   }

   @Test
    void testFirstBlock(){
       Block firstBlock1 = node1.getBlockchain().get(0);
       Block firstBlock2 = node2.getBlockchain().get(0);
       Block firstBlock3 = node3.getBlockchain().get(0);
       assertEquals(firstBlock1,firstBlock2);
       assertEquals(firstBlock3,firstBlock2);
       assertEquals(firstBlock1,firstBlock3);
   }
   @Test
   void testGenerateBlock(){
       Block firstBlock1 =  node1.createBlock();
       Block firstBlock2 = node2.getBlockchain().get(1);
       Block firstBlock3 = node3.getBlockchain().get(1);
       assertEquals(firstBlock1,firstBlock2);
       assertEquals(firstBlock3,firstBlock2);
       assertEquals(firstBlock1,firstBlock3);
   }
   @Test
    void testJoinThread(){
       for (int i= 0; i<= 5; i++){
           node1.createBlock();
           node2.createBlock();
           node3.createBlock();
       }
       Block lastBlockNode1 = node1.getBlockchain().get( node1.getBlockchain().size() -1);
       Block lastBlockNode2 = node2.getBlockchain().get( node2.getBlockchain().size() -1);
       Block lastBlockNode3 = node3.getBlockchain().get( node3.getBlockchain().size() -1);
       assertTrue(lastBlockNode1.equals(lastBlockNode2));
       assertTrue(lastBlockNode1.equals(lastBlockNode3));
       assertTrue(lastBlockNode3.equals(lastBlockNode2));
   }


}
