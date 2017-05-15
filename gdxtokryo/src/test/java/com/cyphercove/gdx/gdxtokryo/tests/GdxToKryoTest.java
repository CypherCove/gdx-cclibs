package com.cyphercove.gdx.gdxtokryo.tests;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Sphere;
import com.badlogic.gdx.utils.*;
import com.badlogic.gdx.utils.StringBuilder;
import com.cyphercove.gdx.gdxtokryo.GdxToKryo;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import junit.framework.TestCase;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Random;

public abstract class GdxToKryoTest extends TestCase {

    protected Kryo kryo;
    protected final Random random = new Random();

    protected void setUp() throws Exception {
        GdxNativesLoader.load();
        kryo = new Kryo();
        kryo.setRegistrationRequired(true);
        kryo.register(Object.class);
        kryo.register(Object[].class);
        GdxToKryo.registerAll(kryo, 50);
    }

    /** Check functional equality between two classes whose equals methods don't necessarily support field comparison. Many
     * LibGDX classes do not since this usually isn't practically useful.
     *
     * @return Whether the two objects are functionally equal.
     */
    static boolean equals (Object a, Object b){
        if (a == b)
            return true;
        if (a == null || b == null)
            return false;
        if (a.getClass() != b.getClass())
            return false;
        final Class type = a.getClass();
        if (type == float[].class){
            return Arrays.equals((float[])a, (float[])b);
        } else if (type == int[].class){
            return Arrays.equals((int[])a, (int[])b);
        } else if (a instanceof Object[]){
            Object[] _a = (Object[])a;
            Object[] _b = (Object[])b;
            if (_a.length != _b.length)
                return false;
            for (int i = 0; i < _a.length; i++) {
                if (!equals(_a[i], _b[i]))
                    return false;
            }
            return true;
        } else if (type == OrthographicCamera.class){
            OrthographicCamera camA = (OrthographicCamera)a;
            OrthographicCamera camB = (OrthographicCamera)b;
            return equals(camA.position, camB.position) && equals(camA.direction, camB.direction) && equals(camA.up, camB.up) &&
                    equals(camA.projection, camB.projection) && camA.near == camB.near && camA.far == camB.far && camA.viewportWidth == camB.viewportWidth &&
                    camA.viewportHeight == camB.viewportHeight && camA.zoom == camB.zoom;
        } else if (type == PerspectiveCamera.class){
            PerspectiveCamera camA = (PerspectiveCamera)a;
            PerspectiveCamera camB = (PerspectiveCamera)b;
            return equals(camA.position, camB.position) && equals(camA.direction, camB.direction) && equals(camA.up, camB.up) &&
                    equals(camA.projection, camB.projection) && camA.near == camB.near && camA.far == camB.far && camA.viewportWidth == camB.viewportWidth &&
                    camA.viewportHeight == camB.viewportHeight && camA.fieldOfView == camB.fieldOfView;
        } else if (type == Pixmap.class){
            ByteBuffer aPixels = ((Pixmap)a).getPixels();
            ByteBuffer bPixels = ((Pixmap)b).getPixels();
            return aPixels.equals(bPixels);
        } else if (type == Affine2.class){
            return a.toString().equals(b.toString());
        } else if (type == Bezier.class){
            return equals(((Bezier)a).points, ((Bezier)b).points);
        } else if (type == BSpline.class){
            BSpline _a = (BSpline)a;
            BSpline _b = (BSpline)b;
            return _a.spanCount == _b.spanCount && _a.continuous == _b.continuous && _a.degree == _b.degree &&
                    equals(_a.knots, _b.knots) && equals(_a.controlPoints, _b.controlPoints);
        } else if (type == CatmullRomSpline.class){
            CatmullRomSpline _a = (CatmullRomSpline)a;
            CatmullRomSpline _b = (CatmullRomSpline)b;
            return _a.spanCount == _b.spanCount && _a.continuous == _b.continuous && equals(_a.controlPoints, _b.controlPoints);
        } else if (type == Matrix3.class){
            return equals(((Matrix3)a).val, ((Matrix3)b).val);
        } else if (type == Matrix4.class){
            return equals(((Matrix4)a).val, ((Matrix4)b).val);
        } else if (type == Plane.class){
            Plane _a = (Plane)a;
            Plane _b = (Plane)b;
            return  _a.d == _b.d && equals(_a.normal, _b.normal);
        } else if (type == Polygon.class){
            Polygon _a = (Polygon)a;
            Polygon _b = (Polygon)b;
            return _a.getX() == _b.getX() && _a.getY() == _b.getY() && _a.getOriginX() == _b.getOriginX() &&
                    _a.getOriginY() == _b.getOriginY() && _a.getRotation() == _b.getRotation() &&
                    _a.getScaleX() == _b.getScaleX() && _a.getScaleY() == _b.getScaleY() &&
                    equals(_a.getVertices(), _b.getVertices());
        } else if (type == Polyline.class){
            Polyline _a = (Polyline)a;
            Polyline _b = (Polyline)b;
            return _a.getX() == _b.getX() && _a.getY() == _b.getY() && _a.getOriginX() == _b.getOriginX() &&
                    _a.getOriginY() == _b.getOriginY() && _a.getRotation() == _b.getRotation() &&
                    _a.getScaleX() == _b.getScaleX() && _a.getScaleY() == _b.getScaleY() &&
                    equals(_a.getVertices(), _b.getVertices());
        } else if (type == Quaternion.class){
            Quaternion _a = (Quaternion)a;
            Quaternion _b = (Quaternion)b;
            return _a.x == _b.x && _a.y == _b.y && _a.z == _b.z && _a.w == _b.w;
        } else if (type == RandomXS128.class){
            RandomXS128 _a = (RandomXS128)a;
            RandomXS128 _b = (RandomXS128)b;
            return _a.getState(0) == _b.getState(0) && _a.getState(1) == _b.getState(1) && _a.nextFloat() == _b.nextFloat();
        } else if (type == Rectangle.class) {
            Rectangle _a = (Rectangle) a;
            Rectangle _b = (Rectangle) b;
            return _a.x == _b.x && _a.y == _b.y && _a.width == _b.width && _a.height == _b.height;
        } else if (type == Vector2.class){
            Vector2 _a = (Vector2)a;
            Vector2 _b = (Vector2)b;
            return _a.x == _b.x && _a.y == _b.y;
        } else if (type == Vector3.class){
            Vector3 _a = (Vector3)a;
            Vector3 _b = (Vector3)b;
            return _a.x == _b.x && _a.y == _b.y && _a.z == _b.z;
        } else if (type == BoundingBox.class){
            BoundingBox _a = (BoundingBox)a;
            BoundingBox _b = (BoundingBox)b;
            return equals(_a.min, _b.min) && equals(_a.max, _b.max);
        } else if (type == ArrayMap.class){
            ArrayMap<?,?> _a = (ArrayMap<?,?>)a;
            ArrayMap<?,?> _b = (ArrayMap<?,?>)b;
            if (_a.size != _b.size)
                return false;
            if (_a.ordered != _b.ordered)
                return false;
            for (int i = 0; i < _a.size; i++) {
                if (!equals(_a.getKeyAt(i), _b.getKeyAt(i)) || !equals(_a.getValueAt(i), _b.getValueAt(i)))
                    return false;
            }
            return true;
        } else if (type == Array.class || type == DelayedRemovalArray.class || type == SnapshotArray.class){
            Array<?> _a = (Array<?>)a;
            Array<?> _b = (Array<?>)b;
            if (_a.size != _b.size)
                return false;
            for (int i = 0; i < _a.size; i++) {
                if (!equals(_a.get(i), _b.get(i)))
                    return false;
            }
            return true;
        } else if (type == IdentityMap.class){
            IdentityMap<?, ?> _a = (IdentityMap<?, ?>)a;
            IdentityMap<?, ?> _b = (IdentityMap<?, ?>)b;
            if (_a.size != _b.size)
                return false;
            outer:
            for (IdentityMap.Entry entryA : _a.entries()){
                for (IdentityMap.Entry entryB : _b.entries()){
                    if (equals(entryA.key, entryB.key) && equals(entryA.value, entryB.value))
                        continue outer;
                }
                return false;
            }
            return true;
        } else if (type == LongMap.class){
            LongMap<?> _a = (LongMap<?>)a;
            LongMap<?> _b = (LongMap<?>)b;
            if (_a.size != _b.size)
                return false;
            for (LongMap.Entry entry : _a.entries()){
                if (!_b.containsKey(entry.key))
                    return false;
                if (!equals(entry.value, _b.get(entry.key)))
                    return false;
            }
            return true;
        } else if (type == ObjectFloatMap.class){
            ObjectFloatMap<?> _a = (ObjectFloatMap<?>)a;
            ObjectFloatMap<?> _b = (ObjectFloatMap<?>)b;
            if (_a.size != _b.size)
                return false;
            for (ObjectFloatMap.Entry entry : _a.entries()){
                if (!_b.containsValue(entry.value))
                    return false;
                if (!equals(entry.key, _b.findKey(entry.value)))
                    return false;
            }
            return true;
        } else if (type == ObjectIntMap.class){
            ObjectIntMap<?> _a = (ObjectIntMap<?>)a;
            ObjectIntMap<?> _b = (ObjectIntMap<?>)b;
            if (_a.size != _b.size)
                return false;
            for (ObjectIntMap.Entry entry : _a.entries()){
                if (!_b.containsValue(entry.value))
                    return false;
                if (!equals(entry.key, _b.findKey(entry.value)))
                    return false;
            }
            return true;
        } else if (type == ObjectMap.class){
            ObjectMap<?, ?> _a = (ObjectMap<?, ?>)a;
            ObjectMap<?, ?> _b = (ObjectMap<?, ?>)b;
            if (_a.size != _b.size)
                return false;
            outer:
            for (ObjectMap.Entry entryA : _a.entries()){
                for (ObjectMap.Entry entryB : _b.entries()){
                    if (equals(entryA.key, entryB.key) && equals(entryA.value, entryB.value))
                        continue outer;
                }
                return false;
            }
            return true;
        } else if (type == ObjectSet.class){
            ObjectSet<?> _a = (ObjectSet<?>)a;
            ObjectSet<?> _b = (ObjectSet<?>)b;
            if (_a.size != _b.size)
                return false;
            outer:
            for (Object objectA : _a){
                for (Object objectB : _b){
                    if (equals(objectA, objectB))
                        continue outer;
                }
                return false;
            }
            return true;
        } else if (type == OrderedMap.class){
            OrderedMap<?, ?> _a = (OrderedMap<?, ?>)a;
            OrderedMap<?, ?> _b = (OrderedMap<?, ?>)b;
            if (_a.size != _b.size)
                return false;
            OrderedMap.Entries iteratorA = _a.iterator();
            OrderedMap.Entries iteratorB = _b.iterator();
            while (iteratorA.hasNext()){
                if (!iteratorB.hasNext())
                    return false;
                ObjectMap.Entry entryA = iteratorA.next();
                ObjectMap.Entry entryB = iteratorB.next();
                if (!equals(entryA.key, entryB.key) || !equals(entryA.value, entryB.value))
                    return false;
            }
            return true;
        } else if (type == OrderedSet.class){
            OrderedSet<?> _a = (OrderedSet<?>)a;
            OrderedSet<?> _b = (OrderedSet<?>)b;
            if (_a.size != _b.size)
                return false;
            OrderedSet.OrderedSetIterator iteratorA = _a.iterator();
            OrderedSet.OrderedSetIterator iteratorB = _b.iterator();
            while (iteratorA.hasNext()){
                if (!iteratorB.hasNext())
                    return false;
                if (!equals(iteratorA.next(), iteratorB.next()))
                    return false;
            }
            return true;
        } else if (type == Queue.class){
            Queue<?> _a = (Queue<?>)a;
            Queue<?> _b = (Queue<?>)b;
            if (_a.size != _b.size)
                return false;
            for (int i = 0; i < _a.size; i++) {
                if (!equals(_a.get(i), _b.get(i)))
                    return false;
            }
            return true;
        } else if (type == SortedIntList.class){
            SortedIntList<?> _a = (SortedIntList<?>)a;
            SortedIntList<?> _b = (SortedIntList<?>)b;
            if (_a.size() != _b.size())
                return false;
            for (int i = 0; i < _a.size(); i++) {
                if (!equals(_a.get(i), _b.get(i)))
                    return false;
            }
            return true;
        } else if (type == StringBuilder.class) {
            return a.toString().equals(b.toString());
        }
        return a.equals(b);
    }

    float[] generateRandomFloatArray (int length){
        float[] array = new float[length];
        for (int i = 0; i < length; i++) {
            array[i] = random.nextFloat();
        }
        return array;
    }

    int randSize (){
        return random.nextInt(20) + 2;
    }

    int randInt (){
        return random.nextInt(100) - 50;
    }

    float randFloat (){
        return random.nextFloat() * 100f - 50f;
    }

    boolean randBool (){
        return random.nextBoolean();
    }

    <T> T simpleRoundTrip (T object){
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        Output output = new Output(outStream);
        kryo.writeClassAndObject(output, object);
        output.close();
        byte[] outBytes = outStream.toByteArray();

        ByteArrayInputStream inStream = new ByteArrayInputStream(outBytes);
        Input input = new Input(inStream);
        T object1 = (T)kryo.readClassAndObject(input);
        input.close();

        assertTrue(equals(object, object1));
        return object1;
    }
}
