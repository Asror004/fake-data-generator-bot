package dev.jlkesh.java_telegram_bots.dto;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.StringJoiner;

public class Dictionary<K, V> {
    private final Node<K, V>[] table;

    @SuppressWarnings("unchecked")
    public Dictionary(int capacity) {
        this.table = new Node[capacity];
    }

    public V put(K key, V value) {
        int hash = hash(key);
        int index = index(hash);
        var newNode = new Node<>(hash, key, value);
        var node = table[index];
        if ( node == null ) {
            table[index] = newNode;
            return null;
        }
        Node<K, V> prev = null;
        while ( node != null ) {
            if ( node.hash == newNode.hash && Objects.equals(node.key, key) ) {
                V oldValue = node.value;
                node.value = value;
                return oldValue;
            }
            prev = node;
            node = node.next;
        }
        prev.next = newNode;
        return null;
    }

    public V get(K key) {
        int hash = hash(key);
        int index = index(hash);
        var node = table[index];
        if ( node == null )
            return null;
        while ( node != null ) {
            if ( node.hash == hash && Objects.equals(node.key, key) )
                return node.value;
            node = node.next;
        }
        return null;
    }

    public V get(K key, V defaultValue) {
        return Objects.requireNonNullElse(get(key), defaultValue);
    }

    public Set<Entry<K, V>> entrySet() {
        var entrySet = new HashSet<Entry<K, V>>();
        for ( Node<K, V> node : table ) {
            while ( node != null ) {
                entrySet.add(new Entry<>(node.key, node.value));
                node = node.next;
            }
        }
        return entrySet;
    }

    private int hash(K key) {
        return key == null ? 0 : key.hashCode();
    }

    private int index(K key) {
        return hash(key) % table.length;
    }

    private int index(int hash) {
        return hash % table.length;
    }

    @Override
    public String toString() {
        var sj = new StringJoiner(", ", "{", "}");
        for ( Entry<K, V> kvEntry : entrySet() )
            sj.add(kvEntry.toString());
        return sj.toString();
    }

    public record Entry<K, V>(K key, V value) {
        @Override
        public String toString() {
            return "%s=%s".formatted(key, value);
        }
    }

    private static class Node<K, V> {
        int hash;
        K key;
        V value;
        Node<K, V> next;

        public Node(int hash, K key, V value) {
            this.hash = hash;
            this.key = key;
            this.value = value;
        }
    }

}
