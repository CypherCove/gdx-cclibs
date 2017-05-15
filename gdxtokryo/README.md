# GdxToKryo
These are tools and serializers for using LibGDX with Kyro. Although most LibGDX classes could be automatically serialized by Kryo's FieldSerializer, using explicit serializers is more efficient, and it enables backwards compatibility if the fields of the LibGDX classes change. This means that if you save user data using Kryo, and later update your LibGDX version and this library version, the later version of your game or application will be able to read the old user data.

Forward compatibility with all future versions of LibGDX is not guaranteed, because that would require inefficient chunked encoding. If the LibGDX classes evolve to contain additional data, attempts will be made to offer options for writing the data in a forward compatible way.

#GdxToKryo is not fully tested yet, so it is not ready for use.
____

##Compatibility
The current version of GdxToKryo is compatible with Kryo 4.0.0+. If a Kryo major or minor version increase breaks anything, I'll determine at that time whether a separate branch of GdxToKryo should be created for supporting the newer Kryo.

GdxToKryo supports all versions of LibGDX 1.9.7 and up, and the goal is to update it so backward compatibility can be maintained indefinitely as LibGDX evolves.
``
Snapshot versions of GdxToKryo will support breaking changes in snapshot versions of LibGDX, but not snapshot versions of Kryo.

Release versions of GdxToKryo will not be built with snapshot versions of LibGDX.

##Usage
It is necessary that you first be familiar with the correct use of Kryo.

###Short-lived data
If you are not concerned with saving data that can be read by a later release of your application, you can easily add all of GdxToKryo's supported class registrations with:

    kryo = new Kryo();
    GdxToKryo.registerAll(kryo);
    
###Backward compatible data
If you want to preserve the ability to read old data in later revisions of your application, care must be taken to preserve backward compatibility. Class registration order in Kryo cannot be changed between releases of the application.

In this case, to automatically register all of GdxToKryo's supported classes and their serializers, use:

    kryo = new Kryo();
    GdxToKryo.registerGroup(kryo, 0);

The group 0 represents all the supported classes in the original release of GdxToKryo. If future versions of GdxToKryo support additional classes, additional groups will be added. This will allow you to register your own classes without concerning yourself with Kryo registration IDs. So if you Kryo is set up as follows:

    kryo = new Kryo();
    GdxToKryo.registerGroup(kryo, 0);
    kryo.register(MyClass.class);
    kryo.register(MyOtherClass.class);
    
and then if a new release of GdxToKryo supports additional classes you want to register, you can continue to add registrations safely:

    kryo = new Kryo();
    GdxToKryo.registerGroup(kryo, 0);
    kryo.register(MyClass.class);
    kryo.register(MyOtherClass.class);
    GdxToKryo.registerGroup(kryo, 1); // Register additional GdxToKryo classe
    kryo.register(MyNewClass.class);
    
As long as registration order is never changed, backward compatibiliy can be maintained.

The GraphHeader class is used by GdxToKryo to handle backward compatibility support. If you want to support backward compatibility for future versions of LibGDX, you must wrap your data in a GraphHeader before writing it:

    GraphHeader<MyObjectGraphRoot> graphHeader = new GraphHeader<MyObjectGraphRoot>();
    graphHeader.data = myObjectGraphRoot;
    kryo.writeObject(output, graphHeader);


