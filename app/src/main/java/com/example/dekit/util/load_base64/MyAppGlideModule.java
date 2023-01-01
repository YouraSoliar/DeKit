package com.example.dekit.util.load_base64;

import android.content.Context;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;
import java.nio.ByteBuffer;

@GlideModule
public class MyAppGlideModule extends AppGlideModule {
  @Override
  public void registerComponents(@NonNull Context context, @NonNull Glide glide, Registry registry) {
    registry.prepend(String.class, ByteBuffer.class, new Base64ModelLoaderFactory());
  }
}
