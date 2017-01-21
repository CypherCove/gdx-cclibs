package com.cyphercove.gdx.flexbatch.examples.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.cyphercove.gdx.flexbatch.examples.FlexBatchExamplesMain;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		new LwjglApplication(new FlexBatchExamplesMain(), config);
	}
}
