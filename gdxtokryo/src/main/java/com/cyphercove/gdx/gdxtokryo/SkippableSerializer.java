package com.cyphercove.gdx.gdxtokryo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.badlogic.gdx.utils.Disposable;

/** Serializer base intended for objects using leakable native memory or which may take significant time to instantiate.
 * If the read object is part of an obsolete branch of the object graph, this provides a means for skipping the bytes
 * without creating the object, or an opportunity to dispose of the object before returning it if it is known it will
 * not be used.
 * <p>
 * Object skipability is checked using {@link GdxToKryo#isInObsoleteBranch(Kryo)}.*/
public abstract class SkippableSerializer<T> extends Serializer<T> {
    {
        setAcceptsNull(true);
    }

    public final void write (Kryo kryo, Output output, T object){
        output.writeBoolean(object == null);
        if (object != null)
            writeNonNull(kryo, output, object);
    }

    /** Write the object as is usually done with {@code #write(Kryo, Output, T)}. The object will never be null. */
    public abstract void writeNonNull (Kryo kryo, Output output, T object);

    final public T read (Kryo kryo, Input input, Class<T> type){
        if (input.readBoolean())
            return null;
        return readOrSkip(kryo, input, type, GdxToKryo.isInObsoleteBranch(kryo));
    }

    /** Read and return the object as is usually done with {@link #read(Kryo, Input, Class)}. If {@code obsolete} is
     * true, it is acceptable to skip the appropriate bytes in input and return null.
     * <p>
     * If {@code obsolete} is true, the reference to any returned object will be lost, so it is necessary to release any
     * native memory prior to returning. For example, if the object is a {@link Disposable}, it must be disposed before
     * returning. */
    abstract public T readOrSkip (Kryo kryo, Input input, Class<T> type, boolean obsolete);
}
