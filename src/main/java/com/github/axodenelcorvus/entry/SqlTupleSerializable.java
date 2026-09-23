package com.github.axodenelcorvus.entry;

/**
 * Contract for objects that can be serialized into an SQL tuple string format for row construction,
 * but not necessarily the other way around through deserialization.
 */
public interface SqlTupleSerializable {
    String toSqlTuple(SqlStringResolver sqlStrResolution);
}
