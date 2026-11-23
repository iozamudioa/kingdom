package dev.ioza.kingdom.bastion.adapters.out.postern;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;

/** Strict literal IP and CIDR matching; no DNS resolution of hostnames. */
public record IpRange(byte[] network, int prefixLength) {

    public static IpRange parse(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("IP range must not be blank");
        }
        String[] parts = value.trim().split("/", -1);
        if (parts.length > 2) {
            throw new IllegalArgumentException("Invalid IP range: " + value);
        }
        byte[] bytes = literal(parts[0]);
        int bits = bytes.length * 8;
        int prefix = parts.length == 1 ? bits : Integer.parseInt(parts[1]);
        if (prefix < 0 || prefix > bits) {
            throw new IllegalArgumentException("Invalid CIDR prefix: " + value);
        }
        byte[] masked = Arrays.copyOf(bytes, bytes.length);
        for (int i = 0; i < masked.length; i++) {
            int remaining = Math.max(0, Math.min(8, prefix - (i * 8)));
            masked[i] = (byte) (masked[i] & (0xff << (8 - remaining)));
        }
        return new IpRange(masked, prefix);
    }

    public boolean contains(InetAddress address) {
        byte[] candidate = address.getAddress();
        if (candidate.length != network.length) {
            return false;
        }
        for (int i = 0; i < network.length; i++) {
            int remaining = Math.max(0, Math.min(8, prefixLength - (i * 8)));
            int mask = (0xff << (8 - remaining)) & 0xff;
            if ((candidate[i] & mask) != (network[i] & mask)) {
                return false;
            }
        }
        return true;
    }

    public static InetAddress parseAddress(String ip) {
        try {
            return InetAddress.getByAddress(literal(ip));
        } catch (UnknownHostException ex) {
            throw new IllegalArgumentException("Invalid IP literal", ex);
        }
    }

    private static byte[] literal(String value) {
        if (value.matches("(?:\\d{1,3}\\.){3}\\d{1,3}")) {
            String[] octets = value.split("\\.");
            byte[] result = new byte[4];
            for (int i = 0; i < 4; i++) {
                int octet = Integer.parseInt(octets[i]);
                if (octet > 255) throw new IllegalArgumentException("Invalid IPv4: " + value);
                result[i] = (byte) octet;
            }
            return result;
        }
        if (value.contains(":") && value.matches("[0-9a-fA-F:]+")) {
            try {
                byte[] ip = InetAddress.getByName(value).getAddress();
                if (ip.length == 16) return ip;
            } catch (UnknownHostException ignored) {
                // Not a valid IPv6 literal.
            }
        }
        throw new IllegalArgumentException("Invalid IP literal: " + value);
    }
}
