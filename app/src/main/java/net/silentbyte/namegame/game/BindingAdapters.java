package net.silentbyte.namegame.game;

import android.databinding.BindingAdapter;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;

import net.silentbyte.namegame.R;
import net.silentbyte.namegame.data.source.local.ProfileEntity;
import net.silentbyte.namegame.glide.GlideApp;

import java.util.List;

public class BindingAdapters {

    @BindingAdapter("url")
    public static void loadImage(ImageView view, String url) {
        GlideApp.with(view.getContext())
            .load(url == null ? R.drawable.avatar : "http:" + url)
            .error(R.drawable.avatar)
            .apply(RequestOptions.circleCropTransform())
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(view);
    }

    @BindingAdapter("visibility_boolean")
    public static void setVisible(View view, boolean visible) {
        view.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    @BindingAdapter("employees")
    public static void setEmployees(RecyclerView view, List<ProfileEntity> profiles) {
        ((EmployeeAdapter) view.getAdapter()).setEmployees(profiles);
    }
}
