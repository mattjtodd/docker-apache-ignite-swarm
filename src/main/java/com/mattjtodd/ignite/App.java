package com.mattjtodd.ignite;

import org.apache.ignite.Ignition;

/**
 * Ignition!
 */
public class App {

    public static void main(String[] args) throws Exception {
        Ignition.start(args[0]);
    }

}
