package com.cyphercove.gdx.gdxtokryo.tests;

import com.badlogic.gdx.math.*;
import com.badlogic.gdx.math.collision.Sphere;
import com.badlogic.gdx.utils.*;
import com.badlogic.gdx.utils.StringBuilder;

public class UtilsTest extends GdxToKryoTest {

    public static void main(String[] args) {
        UtilsTest test = new UtilsTest();
        try {
            test.setUp();
        } catch (Exception e) {
            e.printStackTrace();
        }
        test.testCollections();
        test.testIdentityMap();
        test.testUtils();
    }

    public void testCollections (){
        Array topLevel = new Array();

        ArrayMap<GridPoint2, Vector2> arrayMap = new ArrayMap<GridPoint2, Vector2>();
        for (int i = 0, size = randSize(); i < size; i++) {
            arrayMap.put(new GridPoint2(randInt(), randInt()), new Vector2(randFloat(), randFloat()));
        }
        topLevel.add(arrayMap);

        Array<GridPoint3> array = new Array<GridPoint3>();
        for (int i = 0, size = randSize(); i < size; i++) {
            array.add(new GridPoint3(randInt(), randInt(), randInt()));
        }
        topLevel.add(array);

        Bits bits = new Bits();
        for (int i = 0, size = randSize(); i < size; i++) {
            if (randBool()) bits.set(i);
        }
        topLevel.add(bits);

        BooleanArray booleanArray = new BooleanArray();
        booleanArray.setSize(randSize());
        for (int i = 0; i < booleanArray.size; i++) {
            booleanArray.set(i, randBool());
        }
        topLevel.add(booleanArray);

        ByteArray byteArray = new ByteArray();
        for (int i = 0, size = randSize(); i < size; i++) {
            byteArray.add((byte)random.nextInt());
        }
        topLevel.add(byteArray);

        CharArray charArray = new CharArray();
        for (int i = 0, size = randSize(); i < size; i++) {
            charArray.add((char)random.nextInt());
        }
        topLevel.add(charArray);

        DelayedRemovalArray<Plane> delayedRemovalArray = new DelayedRemovalArray<Plane>();
        for (int i = 0, size = randSize(); i < size; i++) {
            delayedRemovalArray.add(new Plane(new Vector3(randFloat(), randFloat(), randFloat()), randFloat()));
        }
        topLevel.add(delayedRemovalArray);

        FloatArray floatArray = new FloatArray();
        for (int i = 0, size = randSize(); i < size; i++) {
            floatArray.add(randFloat());
        }
        topLevel.add(floatArray);

        IntArray intArray = new IntArray();
        for (int i = 0, size = randSize(); i < size; i++) {
            intArray.add(random.nextInt());
        }
        topLevel.add(intArray);

        IntFloatMap intFloatMap = new IntFloatMap();
        for (int i = 0, size = randSize(); i < size; i++) {
            intFloatMap.put(randInt(), randFloat());
        }
        topLevel.add(intFloatMap);

        IntIntMap intIntMap = new IntIntMap();
        for (int i = 0, size = randSize(); i < size; i++) {
            intIntMap.put(randInt(), randInt());
        }
        topLevel.add(intIntMap);

        IntMap<Rectangle> intMap = new IntMap<Rectangle>();
        for (int i = 0, size = randSize(); i < size; i++) {
            intMap.put(randInt(), new Rectangle(randFloat(), randFloat(), randFloat(), randFloat()));
        }
        topLevel.add(intMap);

        IntSet intSet = new IntSet();
        for (int i = 0, size = randSize(); i < size; i++) {
            intSet.add(random.nextInt());
        }
        topLevel.add(intSet);

        LongArray longArray = new LongArray();
        for (int i = 0, size = randSize(); i < size; i++) {
            longArray.add(random.nextLong());
        }
        topLevel.add(longArray);

        LongMap<Sphere> longMap = new LongMap<Sphere>();
        for (int i = 0, size = randSize(); i < size; i++) {
            longMap.put(random.nextLong(), new Sphere(new Vector3(randFloat(), randFloat(), randFloat()), randFloat()));
        }
        topLevel.add(longMap);

        ObjectFloatMap<GridPoint2> objectFloatMap = new ObjectFloatMap<GridPoint2>();
        for (int i = 0, size = randSize(); i < size; i++) {
            objectFloatMap.put(new GridPoint2(randInt(), randInt()), randFloat());
        }
        topLevel.add(objectFloatMap);

        ObjectIntMap<Vector2> objectIntMap = new ObjectIntMap<Vector2>();
        for (int i = 0, size = randSize(); i < size; i++) {
            objectIntMap.put(new Vector2(randFloat(), randFloat()), randInt());
        }
        topLevel.add(objectIntMap);

        ObjectMap<Quaternion, Matrix3> objectMap = new ObjectMap<Quaternion, Matrix3>();
        for (int i = 0, size = randSize(); i < size; i++) {
            objectMap.put(new Quaternion(randFloat(), randFloat(), randFloat(), 1f),
                    new Matrix3().rotate(randFloat()).scl(randFloat()).translate(randFloat(), randFloat()));
        }
        topLevel.add(objectMap);

        ObjectSet<Ellipse> objectSet = new ObjectSet<Ellipse>();
        for (int i = 0, size = randSize(); i < size; i++) {
            objectSet.add(new Ellipse(randFloat(), randFloat(), randFloat(), randFloat()));
        }
        topLevel.add(objectSet);

        OrderedMap<Circle, Affine2> orderedMap = new OrderedMap<Circle, Affine2>();
        for (int i = 0, size = randSize(); i < size; i++) {
            orderedMap.put(new Circle(randFloat(), randFloat(), randFloat()),
                    new Affine2().translate(randFloat(), randFloat()).scale(randFloat(), randFloat()).rotate(randFloat()));
        }
        topLevel.add(orderedMap);

        OrderedSet<Vector2> orderedSet = new OrderedSet<Vector2>();
        for (int i = 0, size = randSize(); i < size; i++) {
            orderedSet.add(new Vector2(randFloat(), randFloat()));
        }
        topLevel.add(orderedSet);

        Queue<GridPoint2> queue = new Queue<GridPoint2>();
        for (int i = 0, size = randSize(); i < size; i++) {
            queue.addLast(new GridPoint2(randInt(), randInt()));
        }
        topLevel.add(queue);

        ShortArray shortArray = new ShortArray();
        for (int i = 0, size = randSize(); i < size; i++) {
            shortArray.add((short)random.nextInt());
        }
        topLevel.add(shortArray);

        SnapshotArray<Matrix3> snapshotArray = new SnapshotArray<Matrix3>();
        for (int i = 0, size = randSize(); i < size; i++) {
            snapshotArray.add(new Matrix3().rotate(randFloat()).scale(randFloat(), randFloat()).translate(randFloat(), randFloat()));
        }
        topLevel.add(snapshotArray);

        SortedIntList<Vector3> sortedIntList = new SortedIntList<Vector3>();
        int size = randSize();
        IntArray indices = new IntArray();
        for (int i = 0; i < size; i++) {
            indices.add(i);
        }
        indices.shuffle();
        for (int i = 0; i < size; i++) {
            sortedIntList.insert(indices.get(i), new Vector3(randFloat(), randFloat(), randFloat()));
        }
        topLevel.add(sortedIntList);

        simpleRoundTrip(topLevel);
    }

    public void testIdentityMap (){
        Array topLevel = new Array();
        Circle valueCircle = new Circle(randFloat(), randFloat(), randFloat());
        topLevel.add(valueCircle);
        Circle keyCircle = new Circle(randFloat(), randFloat(), randFloat());
        topLevel.add(keyCircle);
        IdentityMap<Circle, Circle> identityMap = new IdentityMap<Circle, Circle>();

        for (int i = 0, size = randSize() + 5; i < size; i++) {
            if (i == 5){
                identityMap.put(keyCircle, valueCircle);
                continue;
            }
            identityMap.put(new Circle(randFloat(), randFloat(), randFloat()), new Circle(randFloat(), randFloat(), randFloat()));
        }
        topLevel.add(identityMap);

        Array returnedArray = simpleRoundTrip(topLevel);
        Circle returnedValueCircle = (Circle) returnedArray.get(0);
        Circle returnedKeyCircle = (Circle) returnedArray.get(1);
        IdentityMap<Circle, Circle> returnedMap = (IdentityMap<Circle, Circle>)returnedArray.get(2);
        assertTrue(returnedValueCircle == returnedMap.get(returnedKeyCircle));
    }

    public void testUtils (){
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0, size = randSize(); i < size; i++) {
            stringBuilder.append((char)randInt());
        }
        simpleRoundTrip(stringBuilder);
    }
}
