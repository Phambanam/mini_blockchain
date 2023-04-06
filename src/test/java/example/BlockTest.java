package example;

import org.example.Utils;
import org.example.block.Block;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class BlockTest {
    Block block;
    @BeforeEach
    void setUp(){
        String data  = Utils.generateData();
        String preHash = "000000asdfasdf00000";
        int index = 0;
        block = new Block(index,preHash,data);
    }
    @Test
    @DisplayName("Test: Hash of block must contain 00000 in the end")
    void testHash(){
        String withEnd = "0000";
        assertTrue(block.getHash().endsWith(withEnd));
    }
    @Test
    @DisplayName("Test: Length of data  block")
    void testLengthData(){
        assertEquals(block.getData().length(),Utils.LEN_DATA);
    }

}
