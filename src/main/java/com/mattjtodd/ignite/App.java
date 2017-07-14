package com.mattjtodd.ignite;

import org.apache.ignite.Ignite;
import org.apache.ignite.Ignition;

/**
 * Hello world!
 */
public class App {

    public static void main(String[] args) throws Exception {
        Ignite ignite = Ignition.start(args[0]);
    }

}
