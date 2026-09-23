package com.github.axodenelcorvus.entry;

/**
 * Contract for objects in program that can be serialized into a tuple string format, but can't be
 * deserialized from tuple string to object.
 */
public interface SqlTupleSerializable {
    String toSqlTuple();
}
