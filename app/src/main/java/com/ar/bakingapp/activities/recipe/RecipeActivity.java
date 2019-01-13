package com.ar.bakingapp.activities.recipe;

import android.appwidget.AppWidgetManager;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import com.ar.bakingapp.R;
import com.ar.bakingapp.activities.home.HomeActivity;
import com.ar.bakingapp.activities.media.MediaActivity;
import com.ar.bakingapp.fragments.PlayerFragment;
import com.ar.bakingapp.fragments.RecipeActivityFragment;
import com.ar.bakingapp.widget.BakingWidgetProvider;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecipeActivity extends AppCompatActivity implements RecipeActivityFragment.OnFragmentInteractionListener {
    public static final String RECIPE_ID = "recipe_id";
    public static final String SELECTED_POSITION = "selectedPosition";

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    boolean isTowPaneLayout;
    private RecipeViewModel viewModel;
    private RecipeActivityFragment stepFragment;
    private PlayerFragment mediaFragment;

    // Recipe ID: 1, 2, 3, 4
    public static void startRecipeActivity(Context context, int id) {
        Log.i("RecipeActivity", "1...");

        // save selected recipe id to SharedPrefs so that in the RemoteViewsFactory we
        // can retrieve it and then retrieve the corresponding ingredient list stored in Room
        SharedPreferences prefs = context.getSharedPreferences(
                "com.example.app", Context.MODE_PRIVATE);
        prefs.edit().putInt("selected_recipe", id).apply();

        //Update widgets
        Intent widgetIntent = new Intent(context, BakingWidgetProvider.class);
        widgetIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);

        // https://stackoverflow.com/a/7738687/3500752
        int[] ids = AppWidgetManager.getInstance(context).getAppWidgetIds(
                new ComponentName(context, BakingWidgetProvider.class)
        );
        widgetIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids); // this will trigger onUpdate from AppWidgetProvider
        context.sendBroadcast(widgetIntent);

        Intent intent = new Intent(context, RecipeActivity.class);

        intent.putExtra(RECIPE_ID, id);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);
        ButterKnife.bind(this);
        HomeActivity.getIdlingResource();
        HomeActivity.mIdlingResource.setIdleState(false);
        viewModel = ViewModelProviders.of(this).get(RecipeViewModel.class);
        viewModel.selectedRecipeId = getIntent().getIntExtra(RECIPE_ID, -1);
        if (viewModel.selectedRecipeId == -1) {
            doFinish();
            return;
        }

        init();
        setupObserver();

    }

    private void init() {
        if (findViewById(R.id.twoPaneLayout) != null)
            isTowPaneLayout = true;
        viewModel.init(viewModel.selectedRecipeId);
        stepFragment = (RecipeActivityFragment) getSupportFragmentManager().findFragmentById(R.id.stepFragment);

        if (isTowPaneLayout) {
            mediaFragment = (PlayerFragment) getSupportFragmentManager().findFragmentById(R.id.mediaFragment);
        }
    }

    private void doFinish() {
        Toast.makeText(this, getString(R.string.recipe_not_selected), Toast.LENGTH_LONG).show();
        finish();
    }

    private void setupObserver() {
        viewModel.getRecipes().observe(this, recipe -> {
            if (recipe == null) {
                doFinish();
                return;
            }

            setupToolbar(recipe.getName());
            stepFragment.setListofSteps(recipe.getSteps(), recipe.getIngredients());
            if (isTowPaneLayout) {
                mediaFragment.setStepInfo(recipe.getSteps().get(viewModel.selectedStepIndex));
                mediaFragment.setMediaViewPortion(0.8f);
            }
            HomeActivity.mIdlingResource.setIdleState(true);
        });
    }

    private void setupToolbar(String recipeName) {
        mToolbar.setTitle(recipeName);
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mToolbar.setNavigationOnClickListener(view -> onBackPressed());

    }

    @Override
    public void onFragmentInteraction(int position) {
        viewModel.selectedStepIndex = position;
        if (isTowPaneLayout)
            mediaFragment.setStepInfo(viewModel.getRecipes().getValue().getSteps().get(viewModel.selectedStepIndex));
        else
            MediaActivity.startMediaActivity(this, viewModel.selectedRecipeId, position);
    }
}
