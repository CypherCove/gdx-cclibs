# GdxToKryo
These are tools and serializers for using LibGDX with Kyro. Although most LibGDX classes could be automatically serialized by Kryo's FieldSerializer, using explicit serializers is more efficient, and it enables backwards compatibility if the fields of the LibGDX classes change. This means that if you save user data using Kryo, and later update your LibGDX version and this library version, the later version of your game or application will be able to read the old user data.

Forward compatibility with all future versions of LibGDX is not guaranteed, because that would require inefficient chunked encoding. If the LibGDX classes evolve to contain additional data, attempts will be made to offer options for writing the data in a forward compatible way.

#GdxToKryo is not fully tested yet, so it is not ready for use.
I plan to release the initial version 1.1.0 very soon after the LibGDX 1.9.7 release.

##Project dependency
GdxToKryo is available via Maven Central. You can add it to your LibGDX project's base `build.gradle` under the `core` module's dependencies:

    compile "com.cyphercove.gdx:flexbatch:1.1.0-SNAPSHOT"

##Compatibility
The current version of GdxToKryo is compatible with Kryo 4.0.0+. If a Kryo major or minor version increase breaks anything, I'll determine at that time whether a separate branch of GdxToKryo should be created for supporting the newer Kryo.

GdxToKryo supports all versions of LibGDX 1.9.7 and up, and the goal is to update it so backward compatibility can be maintained indefinitely as LibGDX evolves.

Snapshot versions of GdxToKryo will support breaking changes in snapshot versions of LibGDX, but not snapshot versions of Kryo.

Release versions of GdxToKryo will not be built with snapshot versions of LibGDX.

##Usage
It is necessary that you first be familiar with the correct use of Kryo.

###Short-lived data
If you are not concerned with saving data that can be read by a later release of your application, you can easily add all of GdxToKryo's supported class registrations with:

    kryo = new Kryo();
    kryo.setRegistrationRequired(true); // recommended
    GdxToKryo.registerAll(kryo);
    
###Backward compatible data
If you want to preserve the ability to read old data in later revisions of your application, care must be taken to preserve backward compatibility. Class registration order in Kryo cannot be changed between releases of the application.

####Class registration
To automatically register all of GdxToKryo's supported classes and their serializers, use:

    kryo = new Kryo();
    kryo.setRegistrationRequired(true); // recommended
    GdxToKryo.registerGroup(kryo, 0);

The group 0 represents all the supported classes in the original release of GdxToKryo. The group's classes and their registration order will never be changed. If future versions of GdxToKryo support additional classes, additional groups will be added. This will allow you to register your own classes without concerning yourself with Kryo registration IDs. So if the Kryo instance is set up as follows:

    kryo = new Kryo();
    kryo.setRegistrationRequired(true); // recommended
    GdxToKryo.registerGroup(kryo, 0);
    kryo.register(MyClass.class);
    kryo.register(MyOtherClass.class);
    
and then if a new release of GdxToKryo supports additional classes you want to register, you can continue to add registrations safely:

    kryo = new Kryo();
    kryo.setRegistrationRequired(true); // recommended
    GdxToKryo.registerGroup(kryo, 0);
    kryo.register(MyClass.class);
    kryo.register(MyOtherClass.class);
    GdxToKryo.registerGroup(kryo, 1); // Register additional GdxToKryo classes
    kryo.register(MyNewClass.class);
    
As long as registration order is never changed, backward compatibility can be maintained. Note that the most recent group may be changed in between releases of Kryo.

####GraphHeader
The GraphHeader class is used by GdxToKryo to handle backward compatibility support. If you want to support backward compatibility for future versions of LibGDX, you must wrap your data in a GraphHeader before writing it:

    GraphHeader<MyObjectGraphRootClass> graphHeader = new GraphHeader<MyObjectGraphRootClass>();
    graphHeader.data = myObjectGraphRoot;
    kryo.writeObject(output, graphHeader);
    
The serializers of GdxToKryo use data from the graph header to determine version information for supporting backward compatibility. For non-LibGDX classes, you may use Kryo's TaggedFieldSerializer or CompatibleFieldSerializer to achieve backward compatibility. 

Alternatively, you can make your classes KryoSerializable or write their own Serializers, and use the GraphHeader to store the necessary data for your `read` methods to correctly deserialize the data. This is done by setting a value for `GraphHeader.currentReadWriteVersion` before writing any data. Every time you release a new version of your application in which any classes are written differently than in a previous version, you can increment the value you use for `GraphHeader.currentReadWriteVersion`. Your serializers can access the value that was used to write the data using `GraphHeader.getWrittenVersion(Kryo)`.

####Obsolete fields
Some classes in LibGDX, such as Pixmap, utilize native memory and will cause memory leaks if they are not disposed before dropping their references. If a class changes such that it no longer uses a certain field, later versions of the application must now handle reading the data without leaking data. With Kryo's CompatibleFieldSerializer, this is not an issue, because the bytes of data are simply skipped in the input stream without instantiating anything. But TaggedFieldSerializer reads deprecated fields, so there may be an obsolete branch of the object graph containing some objects that must be disposed. In this case, care must be taken to handle any disposable objects in these deprecated branches of your object graph.

If you are using custom Serializers or KryoSerializable, you can use `GdxToKryo.skipObsoleteObject(Kryo, Input, Class)` on deprecated fields as an alternative to `kryo.readObject(Input, Class)` to hint to serializers such as PixmapSerializer to skip the appropriate number of bytes without instantiating disposable objects.


