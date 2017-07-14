package com.mattjtodd.ignite;

import org.apache.ignite.spi.communication.tcp.TcpCommunicationSpi;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ServicePeers {
   private final Set<String> peers;

   private final String myAddress;

   private final int port;

   public ServicePeers(String dns, int port) throws UnknownHostException, SocketException {
       Set<Inet4Address> networkAddresses = getNetworkAddresses();
       Set<Inet4Address> dnsLookup = getAllByName(dns);

       myAddress = networkAddresses
               .stream()
               .filter(dnsLookup::contains)
               .map(Inet4Address::getHostAddress)
               .findFirst()
               .orElseThrow(NoSuchElementException::new);

       peers = dnsLookup
               .stream()
               .map(Inet4Address::getHostAddress)
               .map(value -> value + ":" + port)
               .collect(Collectors.toSet());

       this.port = port;
   }

    static Set<Inet4Address> getAllByName(String dns) throws UnknownHostException {
        return Stream
                .of(InetAddress.getAllByName(dns))
                .filter(Inet4Address.class::isInstance)
                .map(Inet4Address.class::cast)
                .collect(Collectors.toSet());
    }

    static Set<Inet4Address> getNetworkAddresses() throws SocketException {
        return Collections
                .list(NetworkInterface.getNetworkInterfaces())
                .stream()
                .flatMap(networkInterface -> Collections.list(networkInterface.getInetAddresses()).stream())
                .filter(Inet4Address.class::isInstance)
                .map(Inet4Address.class::cast)
                .collect(Collectors.toSet());
    }

    public int getPort() {
        return port;
    }

    public Set<String> getPeers() {
        return peers;
    }

    public String getMyAddress() {
        return myAddress;
    }
}
