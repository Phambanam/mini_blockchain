package org.example;


import org.apache.commons.lang3.RandomStringUtils;
import org.example.block.Block;


public class Utils {
    public static final int LEN_DATA = 256;
    public final static int FIRST_PORT = 8000;
    public final static int SECOND_PORT = 8001;
    public final static int THIRD_PORT = 8002;
    public static String generateData() {
        return RandomStringUtils.random(LEN_DATA,true,true);
    }

    public static Block firstBlock(){
        String data = "data";
        String previousHAsh = "ROOT_HASH";
        String hash = "e6d010569df61ae45331991bd3faecd23925533c6eabcb10480b7c2ffc450000";
        Block block =   new Block(0,previousHAsh,data);
        block.setHash(hash);
        return block;
    }

}
