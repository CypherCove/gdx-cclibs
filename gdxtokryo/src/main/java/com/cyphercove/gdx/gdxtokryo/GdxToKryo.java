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

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.math.collision.*;
import com.badlogic.gdx.utils.*;
import com.badlogic.gdx.utils.IdentityMap;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.StringBuilder;
import com.cyphercove.gdx.gdxtokryo.gdxserializers.graphics.*;
import com.cyphercove.gdx.gdxtokryo.gdxserializers.math.*;
import com.cyphercove.gdx.gdxtokryo.gdxserializers.math.collision.*;
import com.cyphercove.gdx.gdxtokryo.gdxserializers.utils.*;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;

/**
 * Utility methods for setting up GdxToKryo.
 */
public class GdxToKryo {

    public static final int GROUP_COUNT = 1;
    public static final Object OBSOLETE_BRANCH_KEY = new Object();

    /** Registers {@link GraphHeader} and all of the LibGDX classes supported by GdxToKryo with serializers, using the
     * next consecutively available IDs in the Kryo instance. The value of <code>groupID</code> must be &gt;=0 and
     * {@link #GROUP_COUNT}. To register all of GdxToKryo's serializers, this method should be used repeatedly with all
     * values in this range.
     * <p>
     * When the GdxToKryo library adds additional serializers, a new group ID will become available for each release
     * version of GdxToKryo, to support registering the new serializers. This allows backward compatibility, because
     * the original registration order is maintained.
     * <p>
     * For example, your Kryo setup code may look like
     * <pre>
     *     kryo = new Kryo();
     *     GdxToKryo.register(kryo, 0);
     *     kryo.register(MyClass.class);
     *     kryo.register(MyOtherClass.class);
     * </pre>
     * If you update this library to a later version and want to maintain backward compatibility (the ability to read
     * data from the previous version of your application), you cannot change the registration order, but you can add
     * the new group(s) of registrations for new classes. For example
     * <pre>
     *     kryo = new Kryo();
     *     GdxToKryo.register(kryo, 0);
     *     kryo.register(MyClass.class);
     *     kryo.register(MyOtherClass.class);
     *     GdxToKryo.register(kryo, 1);
     *     kryo.register(MyNewClass.class);
     * </pre>
     * Alternatively, you can use {@link #registerAll(Kryo, int)} to register the complete set of GdxToKryo's serializers
     * with a single method call.
     * @param kryo
     * @param groupID
     */
    public static void registerGroup (Kryo kryo, int groupID){
        if (groupID < 0 || groupID >= GROUP_COUNT)
            throw new RuntimeException("Invalid group ID: " + groupID);
        switch(groupID){
            case 0:
                kryo.register(Object.class); // Required because of use for backing arrays in some classes.
                kryo.register(GraphHeader.class);
                kryo.register(Color.class, new ColorSerializer());
                kryo.register(OrthographicCamera.class, new OrthographicCameraSerializer());
                kryo.register(PerspectiveCamera.class, new PerspectiveCameraSerializer());
                kryo.register(Pixmap.class, new PixmapSerializer());
                kryo.register(Affine2.class, new Affine2Serializer());
                kryo.register(Bezier.class, new BezierSerializer());
                kryo.register(BSpline.class, new BSplineSerializer());
                kryo.register(CatmullRomSpline.class, new CatmullRomSplineSerializer());
                kryo.register(Circle.class, new CircleSerializer());
                kryo.register(Ellipse.class, new EllipseSerializer());
                kryo.register(GridPoint2.class, new GridPoint2Serializer());
                kryo.register(GridPoint3.class, new GridPoint3Serializer());
                kryo.register(Matrix3.class, new Matrix3Serializer());
                kryo.register(Matrix4.class, new Matrix4Serializer());
                kryo.register(Plane.class, new PlaneSerializer());
                kryo.register(Polygon.class, new PolygonSerializer());
                kryo.register(Polyline.class, new PolylineSerializer());
                kryo.register(Quaternion.class, new QuaternionSerializer());
                kryo.register(RandomXS128.class, new RandomXS128Serializer());
                kryo.register(Rectangle.class, new RectangleSerializer());
                kryo.register(Vector2.class, new Vector2Serializer());
                kryo.register(Vector3.class, new Vector3Serializer());
                kryo.register(BoundingBox.class, new BoundingBoxSerializer());
                kryo.register(Ray.class, new RaySerializer());
                kryo.register(Segment.class, new SegmentSerializer());
                kryo.register(Sphere.class, new SphereSerializer());
                kryo.register(ArrayMap.class, new ArrayMapSerializer());
                kryo.register(Array.class, new ArraySerializer());
                kryo.register(Bits.class, new BitsSerializer());
                kryo.register(BooleanArray.class, new BooleanArraySerializer());
                kryo.register(ByteArray.class, new ByteArraySerializer());
                kryo.register(CharArray.class, new CharArraySerializer());
                kryo.register(DelayedRemovalArray.class, new DelayedRemovalArraySerializer());
                kryo.register(FloatArray.class, new FloatArraySerializer());
                kryo.register(IdentityMap.class, new IdentityMapSerializer());
                kryo.register(IntArray.class, new IntArraySerializer());
                kryo.register(IntFloatMap.class, new IntFloatMapSerializer());
                kryo.register(IntIntMap.class, new IntIntMapSerializer());
                kryo.register(IntMap.class, new IntMapSerializer());
                kryo.register(IntSet.class, new IntSetSerializer());
                kryo.register(LongArray.class, new LongArraySerializer());
                kryo.register(LongMap.class, new LongMapSerializer());
                kryo.register(ObjectFloatMap.class, new ObjectFloatMapSerializer());
                kryo.register(ObjectIntMap.class, new ObjectIntMapSerializer());
                kryo.register(ObjectMap.class, new ObjectMapSerializer());
                kryo.register(ObjectSet.class, new ObjectSetSerializer());
                kryo.register(OrderedMap.class, new OrderedMapSerializer());
                kryo.register(OrderedSet.class, new OrderedSetSerializer());
                kryo.register(Queue.class, new QueueSerializer());
                kryo.register(ShortArray.class, new ShortArraySerializer());
                kryo.register(SnapshotArray.class, new SnapshotArraySerializer());
                kryo.register(SortedIntList.class, new SortedIntListSerializer());
                kryo.register(StringBuilder.class, new StringBuilderSerializer());
                break;
        }
    }

    /** Registers {@link GraphHeader} and all of the LibGDX classes supported by GdxToKryo with serializers.
     * <p>
     * This registration method must not be used if backward compatibility is necessary for future versions of LibGDX,
     * because it will make it impossible to guarantee the same registration IDs when this library is updated.
     * @param kryo
     */
    public static void registerAll (Kryo kryo){
        kryo.register(Object.class); // Required because of use for backing arrays in some classes.
        kryo.register(GraphHeader.class);
        kryo.register(Color.class, new ColorSerializer());
        kryo.register(OrthographicCamera.class, new OrthographicCameraSerializer());
        kryo.register(PerspectiveCamera.class, new PerspectiveCameraSerializer());
        kryo.register(Pixmap.class, new PixmapSerializer());
        kryo.register(Affine2.class, new Affine2Serializer());
        kryo.register(Bezier.class, new BezierSerializer());
        kryo.register(BSpline.class, new BSplineSerializer());
        kryo.register(CatmullRomSpline.class, new CatmullRomSplineSerializer());
        kryo.register(Circle.class, new CircleSerializer());
        kryo.register(Ellipse.class, new EllipseSerializer());
        kryo.register(GridPoint2.class, new GridPoint2Serializer());
        kryo.register(GridPoint3.class, new GridPoint3Serializer());
        kryo.register(Matrix3.class, new Matrix3Serializer());
        kryo.register(Matrix4.class, new Matrix4Serializer());
        kryo.register(Plane.class, new PlaneSerializer());
        kryo.register(Polygon.class, new PolygonSerializer());
        kryo.register(Polyline.class, new PolylineSerializer());
        kryo.register(Quaternion.class, new QuaternionSerializer());
        kryo.register(RandomXS128.class, new RandomXS128Serializer());
        kryo.register(Rectangle.class, new RectangleSerializer());
        kryo.register(Vector2.class, new Vector2Serializer());
        kryo.register(Vector3.class, new Vector3Serializer());
        kryo.register(BoundingBox.class, new BoundingBoxSerializer());
        kryo.register(Ray.class, new RaySerializer());
        kryo.register(Segment.class, new SegmentSerializer());
        kryo.register(Sphere.class, new SphereSerializer());
        kryo.register(ArrayMap.class, new ArrayMapSerializer());
        kryo.register(Array.class, new ArraySerializer());
        kryo.register(Bits.class, new BitsSerializer());
        kryo.register(BooleanArray.class, new BooleanArraySerializer());
        kryo.register(ByteArray.class, new ByteArraySerializer());
        kryo.register(CharArray.class, new CharArraySerializer());
        kryo.register(DelayedRemovalArray.class, new DelayedRemovalArraySerializer());
        kryo.register(FloatArray.class, new FloatArraySerializer());
        kryo.register(IdentityMap.class, new IdentityMapSerializer());
        kryo.register(IntArray.class, new IntArraySerializer());
        kryo.register(IntFloatMap.class, new IntFloatMapSerializer());
        kryo.register(IntIntMap.class, new IntIntMapSerializer());
        kryo.register(IntMap.class, new IntMapSerializer());
        kryo.register(IntSet.class, new IntSetSerializer());
        kryo.register(LongArray.class, new LongArraySerializer());
        kryo.register(LongMap.class, new LongMapSerializer());
        kryo.register(ObjectFloatMap.class, new ObjectFloatMapSerializer());
        kryo.register(ObjectIntMap.class, new ObjectIntMapSerializer());
        kryo.register(ObjectMap.class, new ObjectMapSerializer());
        kryo.register(ObjectSet.class, new ObjectSetSerializer());
        kryo.register(OrderedMap.class, new OrderedMapSerializer());
        kryo.register(OrderedSet.class, new OrderedSetSerializer());
        kryo.register(Queue.class, new QueueSerializer());
        kryo.register(ShortArray.class, new ShortArraySerializer());
        kryo.register(SnapshotArray.class, new SnapshotArraySerializer());
        kryo.register(SortedIntList.class, new SortedIntListSerializer());
        kryo.register(StringBuilder.class, new StringBuilderSerializer());
    }

    /** Registers {@link GraphHeader} and all of the LibGDX classes supported by GdxToKryo with serializers, starting at
     * the given ID and incrementing up from there. Currently, 51 classes are registered, including {@link Object}.
     * <p>
     * If backward compatibility is desired, it is recommended to reserve a block of unused registration IDs to support
     * future classes/serializers that GdxToKryo will support, perhaps <code>(startingID + 100)</code>. Alternatively,
     * {@link #registerGroup(Kryo, int)} can be used to register the classes without the need for reserving IDs.
     * @param kryo
     * @param startingID
     */
    public static void registerAll (Kryo kryo, int startingID){
        int i = startingID;
        kryo.register(Object.class, i++); // Required because of use for backing arrays in some classes.
        kryo.register(GraphHeader.class, i++);
        kryo.register(Color.class, new ColorSerializer(), i++);
        kryo.register(OrthographicCamera.class, new OrthographicCameraSerializer(), i++);
        kryo.register(PerspectiveCamera.class, new PerspectiveCameraSerializer(), i++);
        kryo.register(Pixmap.class, new PixmapSerializer(), i++);
        kryo.register(Affine2.class, new Affine2Serializer(), i++);
        kryo.register(Bezier.class, new BezierSerializer(), i++);
        kryo.register(BSpline.class, new BSplineSerializer(), i++);
        kryo.register(CatmullRomSpline.class, new CatmullRomSplineSerializer(), i++);
        kryo.register(Circle.class, new CircleSerializer(), i++);
        kryo.register(Ellipse.class, new EllipseSerializer(), i++);
        kryo.register(GridPoint2.class, new GridPoint2Serializer(), i++);
        kryo.register(GridPoint3.class, new GridPoint3Serializer(), i++);
        kryo.register(Matrix3.class, new Matrix3Serializer(), i++);
        kryo.register(Matrix4.class, new Matrix4Serializer(), i++);
        kryo.register(Plane.class, new PlaneSerializer(), i++);
        kryo.register(Polygon.class, new PolygonSerializer(), i++);
        kryo.register(Polyline.class, new PolylineSerializer(), i++);
        kryo.register(Quaternion.class, new QuaternionSerializer(), i++);
        kryo.register(RandomXS128.class, new RandomXS128Serializer(), i++);
        kryo.register(Rectangle.class, new RectangleSerializer(), i++);
        kryo.register(Vector2.class, new Vector2Serializer(), i++);
        kryo.register(Vector3.class, new Vector3Serializer(), i++);
        kryo.register(BoundingBox.class, new BoundingBoxSerializer(), i++);
        kryo.register(Ray.class, new RaySerializer(), i++);
        kryo.register(Segment.class, new SegmentSerializer(), i++);
        kryo.register(Sphere.class, new SphereSerializer(), i++);
        kryo.register(ArrayMap.class, new ArrayMapSerializer(), i++);
        kryo.register(Array.class, new ArraySerializer(), i++);
        kryo.register(Bits.class, new BitsSerializer(), i++);
        kryo.register(BooleanArray.class, new BooleanArraySerializer(), i++);
        kryo.register(ByteArray.class, new ByteArraySerializer(), i++);
        kryo.register(CharArray.class, new CharArraySerializer(), i++);
        kryo.register(DelayedRemovalArray.class, new DelayedRemovalArraySerializer(), i++);
        kryo.register(FloatArray.class, new FloatArraySerializer(), i++);
        kryo.register(IdentityMap.class, new IdentityMapSerializer(), i++);
        kryo.register(IntArray.class, new IntArraySerializer(), i++);
        kryo.register(IntFloatMap.class, new IntFloatMapSerializer(), i++);
        kryo.register(IntIntMap.class, new IntIntMapSerializer(), i++);
        kryo.register(IntMap.class, new IntMapSerializer(), i++);
        kryo.register(IntSet.class, new IntSetSerializer(), i++);
        kryo.register(LongArray.class, new LongArraySerializer(), i++);
        kryo.register(LongMap.class, new LongMapSerializer(), i++);
        kryo.register(ObjectFloatMap.class, new ObjectFloatMapSerializer(), i++);
        kryo.register(ObjectIntMap.class, new ObjectIntMapSerializer(), i++);
        kryo.register(ObjectMap.class, new ObjectMapSerializer(), i++);
        kryo.register(ObjectSet.class, new ObjectSetSerializer(), i++);
        kryo.register(OrderedMap.class, new OrderedMapSerializer(), i++);
        kryo.register(OrderedSet.class, new OrderedSetSerializer(), i++);
        kryo.register(Queue.class, new QueueSerializer(), i++);
        kryo.register(ShortArray.class, new ShortArraySerializer(), i++);
        kryo.register(SnapshotArray.class, new SnapshotArraySerializer(), i++);
        kryo.register(SortedIntList.class, new SortedIntListSerializer(), i++);
        kryo.register(StringBuilder.class, new StringBuilderSerializer(), i++);
    }

    /** Convenience method for retriving the ColorSerializer if it has already been registered with Kryo.
     * @return The ColorSerializer registered for {@link Color}, or null if none has been registered or the registered
     * serializer is not a {@link ColorSerializer}.
     */
    public static ColorSerializer getColorSerializer (Kryo kryo){
        Serializer serializer = kryo.getSerializer(Color.class);
        if (serializer != null && serializer instanceof ColorSerializer)
            return (ColorSerializer)serializer;
        return null;
    }

    /** Convenience method for retriving the PixmapSerializer if it has already been registered with Kryo.
     * @return The PixmapSerializer registered for {@link Pixmap}, or null if none has been registered or the registered
     * serializer is not a {@link PixmapSerializer}.
     */
    public static PixmapSerializer getPixmapSerializer (Kryo kryo){
        Serializer serializer = kryo.getSerializer(Pixmap.class);
        if (serializer != null && serializer instanceof PixmapSerializer)
            return (PixmapSerializer)serializer;
        return null;
    }

    /** Reads an object using the registered serializer, but does not return it. If the object type is a {@link Disposable},
     * uses native memory, or is time consuming to instantiate, it should use an implementation of {@link SkippableSerializer}
     * to avoid memory leaks or wasted instantiation time. */
    public static <T> void skipObsoleteObject (Kryo kryo, Input input, Class<T> type){
        com.esotericsoftware.kryo.util.ObjectMap graphContext = kryo.getGraphContext();
        final int branchDepth = graphContext.containsKey(OBSOLETE_BRANCH_KEY) ? (Integer)graphContext.get(OBSOLETE_BRANCH_KEY) : 0;
        graphContext.put(OBSOLETE_BRANCH_KEY, branchDepth + 1);

        kryo.readObject(input, type);

        graphContext.put(OBSOLETE_BRANCH_KEY, branchDepth);
    }

    /** Reads an object using the specified serializer, but does not return it. If the object type is a {@link Disposable},
     * uses native memory, or is time consuming to instantiate, it should use an implementation of {@link SkippableSerializer}
     * to avoid memory leaks or wasted instantiation time. */
    public static <T> void skipObsoleteObject (Kryo kryo, Input input, Class<T> type, Serializer serializer){
        com.esotericsoftware.kryo.util.ObjectMap graphContext = kryo.getGraphContext();
        final int branchDepth = graphContext.containsKey(OBSOLETE_BRANCH_KEY) ? (Integer)graphContext.get(OBSOLETE_BRANCH_KEY) : 0;
        graphContext.put(OBSOLETE_BRANCH_KEY, branchDepth + 1);

        kryo.readObject(input, type, serializer);

        graphContext.put(OBSOLETE_BRANCH_KEY, branchDepth);
    }

    /** Reads an object or null using the registered serializer, but does not return it. If the object type is a {@link Disposable},
     * uses native memory, or is time consuming to instantiate, it should use an implementation of {@link SkippableSerializer}
     * to avoid memory leaks or wasted instantiation time. */
    public static <T> void skipObsoleteObjectOrNull (Kryo kryo, Input input, Class<T> type){
        com.esotericsoftware.kryo.util.ObjectMap graphContext = kryo.getGraphContext();
        final int branchDepth = graphContext.containsKey(OBSOLETE_BRANCH_KEY) ? (Integer)graphContext.get(OBSOLETE_BRANCH_KEY) : 0;
        graphContext.put(OBSOLETE_BRANCH_KEY, branchDepth + 1);

        kryo.readObjectOrNull(input, type);

        graphContext.put(OBSOLETE_BRANCH_KEY, branchDepth);
    }

    /** Reads an object or null using the specified serializer, but does not return it. If the object type is a {@link Disposable},
     * uses native memory, or is time consuming to instantiate, it should use an implementation of {@link SkippableSerializer}
     * to avoid memory leaks or wasted instantiation time. */
    public static <T> void skipObsoleteObjectOrNull (Kryo kryo, Input input, Class<T> type, Serializer serializer){
        com.esotericsoftware.kryo.util.ObjectMap graphContext = kryo.getGraphContext();
        final int branchDepth = graphContext.containsKey(OBSOLETE_BRANCH_KEY) ? (Integer)graphContext.get(OBSOLETE_BRANCH_KEY) : 0;
        graphContext.put(OBSOLETE_BRANCH_KEY, branchDepth + 1);

        kryo.readObjectOrNull(input, type, serializer);

        graphContext.put(OBSOLETE_BRANCH_KEY, branchDepth);
    }

    /**
     * @return Whether the data being read is currently in an obsolete branch of the object graph, as marked by
     * {@link #skipObsoleteObject(Kryo, Input, Class)} or similar.
     */
    public static boolean isInObsoleteBranch (Kryo kryo){
        com.esotericsoftware.kryo.util.ObjectMap graphContext = kryo.getGraphContext();
        if (graphContext.containsKey(OBSOLETE_BRANCH_KEY))
            return (Integer)graphContext.get(OBSOLETE_BRANCH_KEY) > 0;
        return false;
    }
}
