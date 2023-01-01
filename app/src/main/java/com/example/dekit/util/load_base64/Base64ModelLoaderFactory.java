package com.example.dekit.util.load_base64;

import androidx.annotation.NonNull;

import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.ModelLoaderFactory;
import com.bumptech.glide.load.model.MultiModelLoaderFactory;
import java.nio.ByteBuffer;

public class Base64ModelLoaderFactory implements ModelLoaderFactory<String, ByteBuffer> {

  @NonNull
  @Override
  public ModelLoader<String, ByteBuffer> build(MultiModelLoaderFactory multiFactory) {
    return new Base64ModelLoader();
  }

  @Override
  public void teardown() {
    // Do nothing.
  }
}
