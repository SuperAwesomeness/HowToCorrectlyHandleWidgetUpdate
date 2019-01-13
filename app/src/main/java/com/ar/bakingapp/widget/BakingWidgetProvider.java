package com.ar.bakingapp.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.RemoteViews;

import com.ar.bakingapp.R;
import com.ar.bakingapp.activities.home.HomeActivity;
import com.ar.bakingapp.activities.recipe.RecipeActivity;
import com.ar.bakingapp.network.model.IngredientsItem;

import java.util.List;

import static com.ar.bakingapp.activities.recipe.RecipeActivity.RECIPE_ID;


public class BakingWidgetProvider extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_grid_view);

        // Set the GridWidgetService intent to act as the adapter for the GridView
        Intent intent = new Intent(context, GridWidgetService.class);
        views.setRemoteAdapter(R.id.widget_grid_view, intent);

        //at init click on widget redirect to homeActivity
        Intent appIntent = new Intent(context, HomeActivity.class);
        PendingIntent appPendingIntent = PendingIntent.getActivity(context, 0, appIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.empty_view, appPendingIntent);

        //if widget has ingredient list and clicking on gridview redirect to recipeActivity
        Intent recipeIntent = new Intent(context, RecipeActivity.class);
        SharedPreferences prefs = context.getSharedPreferences(
                "com.example.app", Context.MODE_PRIVATE);
        int recipeId = prefs.getInt("selected_recipe", -1);
        recipeIntent.putExtra(RECIPE_ID, recipeId);
        PendingIntent recipePendingIntent = PendingIntent.getActivity(context, 1, recipeIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setPendingIntentTemplate(R.id.widget_grid_view, recipePendingIntent);

        views.setEmptyView(R.id.widget_grid_view, R.id.empty_view);
        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);

    }

    public static void updateBakingWidgets(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.i("WidgetProvider", "onUpdate called...");
        updateBakingWidgets(context, appWidgetManager, appWidgetIds);

        // https://stackoverflow.com/a/18631161/3500752
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_grid_view); // this triggers onDatasetChanged from RemoteViewsService.RemoteViewsFactory
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

}

