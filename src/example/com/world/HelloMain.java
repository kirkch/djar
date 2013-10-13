package com.world;


import java.io.IOException;
import java.util.Arrays;

import org.junit.Assert;

/**
 *
 */
public class HelloMain {

    public static void main( String[] args ) throws IOException {
        System.out.println( "Hello World: " + Arrays.asList(args) );


        Assert.assertEquals( 1, args.length );
    }

}
