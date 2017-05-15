/*******************************************************************************
 * Copyright 2017 See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.cyphercove.gdx.gdxtokryo;

import com.badlogic.gdx.Version;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoSerializable;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer;
import com.esotericsoftware.kryo.serializers.TaggedFieldSerializer;
import com.esotericsoftware.kryo.serializers.VersionFieldSerializer;

/**
 * An instance of GraphHeader should be used to wrap a data graph if backward compatibility is desired. Backward
 * compatibility is the ability for data written in a previous version of an application to be read by a newer version
 * of the application even if the fields, constructors, and methods of classes in the graph have changed.
 * <p>
 * In the case of the GdxToKryo serializers, the information in the header is used to determine which version of LibGDX
 * was used when the data was written, so it can be correctly deserialized in applications with later versions of LibGDX.
 * <p>
 * When GraphHeader is used, changes to LibGDX classes are handled automatically, so they are backward compatible. For
 * other classes, {@link TaggedFieldSerializer} or {@link VersionFieldSerializer} may be used to manage a class's backward
 * compatibility. But for custom serializers, the value of {@link #currentReadWriteVersion} can be used to keep track of changes
 * affecting compatibility between releases of an application. Custom serializers' <code>read</code> methods may call
 * {@link #getWrittenVersion(Kryo)} to retrieve the value of {@link #currentReadWriteVersion} that was used when the
 * graph was written.
 * <p>
 * The serializers in GdxToKryo are not necessarily forward compatible with all future versions of LibGDX. This is also
 * likely the case for other classes whose serializers do not use chunked encoding, such as {@link CompatibleFieldSerializer}.
 * When it is known that something in the object graph has changed such that it can no longer be read in a previous release
 * version of the application, not only should the {@link #currentReadWriteVersion} should be incremented, but also
 * {@link #minimumReadVersion} should be updated to match it. This will cause the GraphHeader serializer to skip reading
 * of the {@link #data}, leaving it null. After reading, an application can check if {@link #data} is null, and if so,
 * use the values of {@link #minimumReadVersion} and {@link #minimumReadVersionString} to react accordingly, such as
 * showing the user a prompt message to update the application. Since the data is not read, subsequent data in the
 * input stream cannot be read either.
 * <p>
 * Subclasses of GraphHeader may use {@link #writeExtra(Kryo, Output)} and {@link #readExtra(Kryo, Input)} to handle writing
 * and reading of extra information in the subclass. These are called immediately before writing or reading the {@link #data}.
 *
 * @param <T> The type of the data to be written, referenced in {@link #data}.
 */
public class GraphHeader<T> implements KryoSerializable{

    /** The current version of the GraphHeader class. */
    private static final int GRAPH_HEADER_VERSION = 0;
    private static final Object GRAPH_HEADER_KEY = new Object();

    /**
     * If reading an object graph wrapped in an instance of GraphHeader, serializers can call this to obtain the
     * value of {@link #writtenVersion} in that GraphHeader in their <code>read</code> methods.
     * @param kryo The Kryo instance currently reading data.
     * @return The value of {@link #writtenVersion} in the GraphHeader that wrapped the data being read in the graph. If
     * a GraphHeader did not wrap the data being read, 0 is returned.
     */
    public static int getWrittenVersion(Kryo kryo){
        if (kryo.getGraphContext().containsKey(GRAPH_HEADER_KEY))
            return ((GraphHeader)kryo.getGraphContext().get(GRAPH_HEADER_KEY)).writtenVersion;
        return currentReadWriteVersion;
    }

    /**
     * If reading an object graph that started with an instance of GraphHeader, serializers can call this in their
     * <code>read</code> methods to determine if the LibGDX version of the data being read is from a version of LibGDX
     * higher or equal to a given specific revision.
     * @param kryo The Kryo instance currently reading data.
     * @return Whether the LibGDX version used to write the data being read is higher than or equal to the given version.
     * If a GraphHeader did not precede the data being read, the currently used LibGDX version is assumed as the version
     * that wrote the data.
     */
    public static boolean isWrittenGdxVersionAtLeast (Kryo kryo, int major, int minor, int revision) {
        if (kryo.getGraphContext().containsKey(GRAPH_HEADER_KEY)){
            GraphHeader<?> graphHeader = (GraphHeader)kryo.getGraphContext().get(GRAPH_HEADER_KEY);
            if (graphHeader.gdxMajorVersion != major)
                return graphHeader.gdxMajorVersion > major;
            if (graphHeader.gdxMinorVersion != minor)
                return graphHeader.gdxMinorVersion > minor;
            return graphHeader.gdxRevisionVersion >= revision;
        }
        return Version.isHigherEqual(major, minor, revision);
    }

    /**
     * Used by {@link com.cyphercove.gdx.gdxtokryo.gdxserializers.graphics.ColorSerializer} to determine whether the
     * data in the graph should use compact color (32-bit int) encoding. See {@link #useCompactColor}.
     * @param kryo The Kryo instance currently reading data.
     * @return The value of {@link #useCompactColor} in the GraphHeader that preceded the data being read in the graph.
     * If a GraphHeader did not precede the data being written or read, null is returned.
     */
    public static Boolean isUseCompactColor (Kryo kryo){
        if (kryo.getGraphContext().containsKey(GRAPH_HEADER_KEY))
            return ((GraphHeader)kryo.getGraphContext().get(GRAPH_HEADER_KEY)).useCompactColor;
        return null;
    }

    /**
     * Used by {@link com.cyphercove.gdx.gdxtokryo.gdxserializers.graphics.PixmapSerializer} to determine whether the
     * data in the graph should include pixmap drawing params. See {@link #includePixmapDrawingParams}.
     * @param kryo The Kryo instance currently reading data.
     * @return The value of {@link #includePixmapDrawingParams} in the GraphHeader that preceded the data being read in
     * the graph. If a GraphHeader did not precede the data being written or read, null is returned.
     */
    public static Boolean isIncludePixmapDrawingParams (Kryo kryo){
        if (kryo.getGraphContext().containsKey(GRAPH_HEADER_KEY))
            return ((GraphHeader)kryo.getGraphContext().get(GRAPH_HEADER_KEY)).includePixmapDrawingParams;
        return null;
    }

    /**
     * A representative value that can be used by custom serializers to support backward compatibility. If any of
     * the classes in an object graph change as compared to a previously released version, this value should be incremented
     * so custom serializers can use it to read data accordingly.
     * <p>
     * If there are multiple types of object graphs in this application that share some serializers, the data version used
     * is shared by all, so it should be incremented if any serializers across all of possible graphs is changed.
     * <p>
     * Default is 0. Must be >= 0. The correct value must be set before writing any objects. When the object graph is
     * being read, custom serializers can access the data version that was used to write the data using
     * {@link GraphHeader#getWrittenVersion(Kryo)} in their <code>read</code> methods.
     */
    public static int currentReadWriteVersion;

    // Gdx version fields are only relevant during reading of the graph.
    int gdxMajorVersion, gdxMinorVersion, gdxRevisionVersion;

    /** The value of {@link #currentReadWriteVersion} when the graph was written. This value is changed to {@link #currentReadWriteVersion}
     * when the graph is written. Serializers may access it during reading using {@link GraphHeader#getWrittenVersion(Kryo)}
     * in their <code>read</code> methods. */
    protected int writtenVersion;

    /** Represents the oldest version of the application whose serializers are still capable of deserializing data
     * written in this version. Corresponds to the {@link #currentReadWriteVersion} that was used with that release of
     * the application.
     * <p>
     * If a version of the application that uses a lower {@link #currentReadWriteVersion} attempts to read the graph,
     * the data will not be read, but instead {@link #data} will remain null. At that point {@link #minimumReadVersionString}
     * may be used to show the user an appropriate message about what version of the application is required to read
     * the data. Subsequent data in the input stream cannot be read because there will be an unknown number of bytes to
     * skip.
     */
    public int minimumReadVersion;

    /** A value intended for use when a version of the application attempts to read data from a future version of the
     * application that it is not able to, as determined by {@link #minimumReadVersion}.
     */
    public String minimumReadVersionString;

    /**Whether the written data should use 32-bit integers to represent {@link Color} objects, rather than maintaining
     * complete equivalence by using four floats. 32-bit integers are adequate for most uses and satisfy
     * {@link Color#equals(Object)}. The default is true.
     */
    public boolean useCompactColor = true;

    /**Whether Pixmap drawing parameters (i.e. {@link Pixmap#setColor(Color)}, {@link Pixmap#getBlending()}, and
     * {@link Pixmap#getFilter()}) should be written and restored when read or copied. */
    public boolean includePixmapDrawingParams = false;

    /** The top level object of the object graph. */
    public T data;

    /** Called immediately before writing the {@link #data} and after writing all other fields of GraphHeader. */
    protected void writeExtra (Kryo kryo, Output output) {}

    /** Called immediately before reading the {@link #data} and after reading all other fields of GraphHeader. */
    protected void readExtra (Kryo kryo, Input input) {}

    @Override
    public final void write (Kryo kryo, Output output) {
        Class type = data == null ? null : data.getClass();
        if (currentReadWriteVersion < 0 || minimumReadVersion < 0)
            throw new RuntimeException("currentReadWriteVersion and minimumReadVersion must not be less than 0.");
        if (currentReadWriteVersion < minimumReadVersion)
            throw new RuntimeException("currentReadWriteVersion cannot be lower than minimumReadVersion");

        output.writeInt(GRAPH_HEADER_VERSION, true);
        kryo.writeClass(output, type);
        output.writeInt(Version.MAJOR, true);
        output.writeInt(Version.MINOR, true);
        output.writeInt(Version.REVISION, true);
        output.writeInt(currentReadWriteVersion, true);
        output.writeInt(minimumReadVersion, true);
        output.writeString(minimumReadVersionString);
        output.writeBoolean(useCompactColor);
        output.writeBoolean(includePixmapDrawingParams);
        writeExtra(kryo, output);
        if (data != null)
            kryo.writeObject(output, data);
    }

    @Override
    public final void read (Kryo kryo, Input input) {
        input.readInt(true); //if this class ever evolves, version can be used for backward compatibility
        Class dataType = kryo.readClass(input).getType();
        gdxMajorVersion = input.readInt(true);
        gdxMinorVersion = input.readInt(true);
        gdxRevisionVersion = input.readInt(true);
        writtenVersion = input.readInt(true);
        minimumReadVersion = input.readInt(true);
        minimumReadVersionString = input.readString();
        useCompactColor = input.readBoolean();
        includePixmapDrawingParams = input.readBoolean();
        readExtra(kryo, input);
        if (dataType != null && minimumReadVersion >= currentReadWriteVersion){
            data = (T)kryo.readObject(input, dataType);
        }
    }

}
