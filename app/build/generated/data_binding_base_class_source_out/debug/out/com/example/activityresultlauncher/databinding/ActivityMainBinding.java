// Generated by view binder compiler. Do not edit!
package com.example.activityresultlauncher.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.example.activityresultlauncher.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class ActivityMainBinding implements ViewBinding {
  @NonNull
  private final ConstraintLayout rootView;

  @NonNull
  public final AppCompatButton btn;

  @NonNull
  public final AppCompatButton btnAction;

  @NonNull
  public final AppCompatButton btnG;

  @NonNull
  public final AppCompatImageView imageC;

  @NonNull
  public final AppCompatImageView imageG;

  @NonNull
  public final AppCompatImageView imgLocation;

  @NonNull
  public final LinearLayout layout;

  @NonNull
  public final AppCompatTextView title;

  private ActivityMainBinding(@NonNull ConstraintLayout rootView, @NonNull AppCompatButton btn,
      @NonNull AppCompatButton btnAction, @NonNull AppCompatButton btnG,
      @NonNull AppCompatImageView imageC, @NonNull AppCompatImageView imageG,
      @NonNull AppCompatImageView imgLocation, @NonNull LinearLayout layout,
      @NonNull AppCompatTextView title) {
    this.rootView = rootView;
    this.btn = btn;
    this.btnAction = btnAction;
    this.btnG = btnG;
    this.imageC = imageC;
    this.imageG = imageG;
    this.imgLocation = imgLocation;
    this.layout = layout;
    this.title = title;
  }

  @Override
  @NonNull
  public ConstraintLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static ActivityMainBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ActivityMainBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.activity_main, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ActivityMainBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.btn;
      AppCompatButton btn = ViewBindings.findChildViewById(rootView, id);
      if (btn == null) {
        break missingId;
      }

      id = R.id.btnAction;
      AppCompatButton btnAction = ViewBindings.findChildViewById(rootView, id);
      if (btnAction == null) {
        break missingId;
      }

      id = R.id.btn_g;
      AppCompatButton btnG = ViewBindings.findChildViewById(rootView, id);
      if (btnG == null) {
        break missingId;
      }

      id = R.id.image_c;
      AppCompatImageView imageC = ViewBindings.findChildViewById(rootView, id);
      if (imageC == null) {
        break missingId;
      }

      id = R.id.image_g;
      AppCompatImageView imageG = ViewBindings.findChildViewById(rootView, id);
      if (imageG == null) {
        break missingId;
      }

      id = R.id.imgLocation;
      AppCompatImageView imgLocation = ViewBindings.findChildViewById(rootView, id);
      if (imgLocation == null) {
        break missingId;
      }

      id = R.id.layout;
      LinearLayout layout = ViewBindings.findChildViewById(rootView, id);
      if (layout == null) {
        break missingId;
      }

      id = R.id.title;
      AppCompatTextView title = ViewBindings.findChildViewById(rootView, id);
      if (title == null) {
        break missingId;
      }

      return new ActivityMainBinding((ConstraintLayout) rootView, btn, btnAction, btnG, imageC,
          imageG, imgLocation, layout, title);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
