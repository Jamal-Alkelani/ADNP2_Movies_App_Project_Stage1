package com.example.gamal.adnp2_movies_app_project_stage1;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.Explode;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.BounceInterpolator;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;


public class Details extends AppCompatActivity {
    public static final String TITLE_TAG = "title";
    public static final String DATE_TAG = "movieDate";
    public static final String IMAGE_TAG = "image";
    public static final String DESC_TAG = "desc";
    public static final String VOTEAVG_TAG = "voteAvg";

    private String title;
    private String date;
    private String imageURL;
    private String voteAverage;
    private String desc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();

        if (intent != null) {
            title = intent.getStringExtra(TITLE_TAG);
            date = intent.getStringExtra(DATE_TAG);
            imageURL = intent.getStringExtra(IMAGE_TAG);
            voteAverage = intent.getStringExtra(VOTEAVG_TAG);
            desc = intent.getStringExtra(DESC_TAG);
            setTitle(title);
        }

        RatingBar ratingBar = (RatingBar) findViewById(R.id.ratingBar_Detail);
        LayerDrawable stars = (LayerDrawable) ratingBar.getProgressDrawable();
        stars.getDrawable(2).setColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_ATOP);

        populateInfo();
        initExplodeTrans();
    }

    private void populateInfo() {
        RatingBar ratingBar = findViewById(R.id.ratingBar_Detail);
        final ScrollView image = findViewById(R.id.imageView_Details);
        TextView voteAvg = findViewById(R.id.voteAvg);
        TextView description = findViewById(R.id.desc_Details);
        TextView title_details = findViewById(R.id.MovieTitle_Detail);
        Picasso.with(this).load(imageURL).into(new Target() {

            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                image.setBackground(new BitmapDrawable(getApplicationContext().getResources(), bitmap));
            }

            @Override
            public void onBitmapFailed(final Drawable errorDrawable) {
                Log.d("TAG", "FAILED");
            }

            @Override
            public void onPrepareLoad(final Drawable placeHolderDrawable) {
                Log.d("TAG", "Prepare Load");
            }
        });

        voteAvg.setText(voteAverage);
        description.setText(desc);
        float rating = Float.parseFloat(voteAverage) / 2;
        ratingBar.setRating(rating);
        title_details.setText(title);

    }


    private void initExplodeTrans() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            Transition enterTransXML= TransitionInflater.from(this).inflateTransition(R.transition.transition);
            enterTransXML.setInterpolator(new BounceInterpolator());
            getWindow().setEnterTransition(enterTransXML);

        }

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBackPressed() {
        finishAfterTransition();
    }

    @Override
    public boolean onSupportNavigateUp() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            finishAfterTransition();
        }
        return true;
    }
}
