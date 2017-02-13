# CoveTools
This is a set of tools I use to speed up some tasks in LibGDX.

## Project Dependency
Cove Tools is available via Maven Central. The first version isn't yet released, so there is only the 1.0-SNAPSHOT. You can add it to your LibGDX project's base `build.gradle` under the `core` module's dependencies:

    compile "com.cyphercove.gdx:covetools:1.0-SNAPSHOT"
    
Cove Tools is compatible with LibGDX 1.9.5+. The SNAPSHOT version will be kept up-to-date with any breaking changes in the LibGDX SNAPSHOT as quickly as possible.

## AssignmentAssetManager

AssignmentAssetManager is an AssetManager that is designed to reduce typing and redundancy as much as possible. You set up a container class for your assets by making fields for each asset:

	private static class Stage1Assets {
	    @Asset("egg.png") public Texture eggTexture;
	    @Asset("tree.png") public Texture treeTexture;
	    @Asset("pack") public TextureAtlas atlas;
	    @Assets({"arial-15.fnt", "arial-32.fnt", "arial-32-pad.fnt"}) public BitmapFont[] fonts;
	}

Precede each asset to be loaded with the `@Asset` annotation, providing a file path for the value. Then you can load all of the assets of your container class by passing an instance of the container. You can use `@Assets` to specify an array.

    stage1Assets = new Stage1Assets();
    assignmentAssetManager.loadAssetFields(stage1Assets);
    
Once the asset manager is finished loading (with `finishLoading()` or enough calls to `update()`), all the fields of the container are automatically assigned references to the loaded assets. You can immediately start using them.

    batch.draw(stage1Assets.eggTexture, 0, 0);
    
You can unload all the assets of a container with

    assignmentAssetManager.unloadAssetFields(stage1Assets);
    
The assets that are unique to the container will be unloaded, and all fields will be marked null. If an asset is part of some other loaded container, it will not be unloaded. This allows you to make container for multiple stages of your game and safely load and unload stages without unnecessary reloading of anything that is shared.

You can also specify an AssetLoader parameter by field name in the annotation:

	private static class Stage1Assets {
        TextureParameter mipmapParams = new TextureParameter(){{
            genMipMaps = true;
        }};
    	
        @Asset(value = "egg.png", parameter = "mipmapParams") public Texture eggTexture;
	}
	
The parameter must be a field of the container class, or can be a static member of any class if a fully qualified name is given.

Finally, you can optionally implement the AssetContainer interface to specify a directory for all the assets, or to get a callback once they're loaded and assigned:

    private static class Stage1Assets implements AssetContainer {
	    @Asset("atlas.pack") public TextureAtlas atlas;
	    public TextureRegion eggTextureRegion;
        
		@Override
		public String getAssetPathPrefix() {
			return "stage1/";
		}
		
		@Override
		public void onAssetsLoaded() {
			eggTextureRegion = atlas.findRegion("egg");
		}
    }