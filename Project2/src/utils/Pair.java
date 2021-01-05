package utils;

import java.io.Serializable;

public class Pair<K, V> implements Serializable {
    private K key;
    private V value;

    public K getKey() {
        return this.key;
    }

    public V getValue() {
        return this.value;
    }

    public Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public String toString() {
        return this.key + "=" + this.value;
    }

    public int hashCode() {
        int prime = 7;
        int hash = 31 * prime + (this.key != null ? this.key.hashCode() : 0);
        hash = 31 * hash + (this.value != null ? this.value.hashCode() : 0);
        return hash;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (!(o instanceof Pair)) {
            return false;
        } else {
            Pair pair = (Pair) o;
            if (this.key != null) {
                if (!this.key.equals(pair.key)) {
                    return false;
                }
            } else if (pair.key != null) {
                return false;
            }

            if (this.value != null) {
                if (!this.value.equals(pair.value)) {
                    return false;
                }
            } else if (pair.value != null) {
                return false;
            }

            return true;
        }
    }
}
