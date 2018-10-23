package com.mattjtodd.ignite;

import java.net.*;
import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toSet;

public final class ServicePeers {

   private final Set<String> peers;

   private final String myAddress;

   private final int port;

   private ServicePeers(String dns, int port) throws UnknownHostException, SocketException {
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
               .collect(toSet());

       this.port = port;
   }

   public static ServicePeers search(String dns, int port) throws UnknownHostException, SocketException {
       return new ServicePeers(dns, port);
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

    private static Set<Inet4Address> getAllByName(String dns) throws UnknownHostException {
        return Stream
                .of(InetAddress.getAllByName(dns))
                .filter(Inet4Address.class::isInstance)
                .map(Inet4Address.class::cast)
                .collect(toSet());
    }

    private static Set<Inet4Address> getNetworkAddresses() throws SocketException {
        return Collections
                .list(NetworkInterface.getNetworkInterfaces())
                .stream()
                .flatMap(networkInterface -> Collections.list(networkInterface.getInetAddresses()).stream())
                .filter(Inet4Address.class::isInstance)
                .map(Inet4Address.class::cast)
                .collect(toSet());
    }

    @Override
    public String toString() {
        return "ServicePeers{" +
                "peers=" + peers +
                ", myAddress='" + myAddress + '\'' +
                ", port=" + port +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ServicePeers that = (ServicePeers) o;

        if (port != that.port) return false;
        if (peers != null ? !peers.equals(that.peers) : that.peers != null) return false;
        return myAddress != null ? myAddress.equals(that.myAddress) : that.myAddress == null;
    }

    @Override
    public int hashCode() {
        int result = peers != null ? peers.hashCode() : 0;
        result = 31 * result + (myAddress != null ? myAddress.hashCode() : 0);
        result = 31 * result + port;
        return result;
    }

    public static void main(String[] args) throws SocketException, UnknownHostException {
        ServicePeers hello = search("localhost", 5000);
    }
}
