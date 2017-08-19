package com.app.dmitryteplyakov.sportdiary;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.app.dmitryteplyakov.sportdiary.Core.Day.Day;
import com.app.dmitryteplyakov.sportdiary.Core.Day.DayStorage;
import com.app.dmitryteplyakov.sportdiary.Core.Exercise.ExerciseStorage;
import com.app.dmitryteplyakov.sportdiary.Core.NutritionDay.NutritionDay;
import com.app.dmitryteplyakov.sportdiary.Core.NutritionDay.NutritionDayStorage;
import com.app.dmitryteplyakov.sportdiary.Core.Training.TrainingStorage;
import com.app.dmitryteplyakov.sportdiary.Nutrition.NutritionDaysListFragment;
import com.app.dmitryteplyakov.sportdiary.Nutrition.NutritionListActivity;
import com.app.dmitryteplyakov.sportdiary.Overview.OverviewFragment;
import com.app.dmitryteplyakov.sportdiary.Programs.ProgramsListActivity;
import com.app.dmitryteplyakov.sportdiary.Settings.SettingsActivity;
import com.app.dmitryteplyakov.sportdiary.Training.DaysListFragment;
import com.app.dmitryteplyakov.sportdiary.Training.NewDayActivity;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class GeneralActivity extends AppCompatActivity {

    private BottomNavigationView mBottomNavigationView;
    private FloatingActionButton mFab;
    private static final int REQUEST_ADD_DAY = 2;
    private static final String SAVE_STATE = "com.app.generalactivity.save_state";
    private int TAB_STATE;
    private String[] mDrawerTitles;
    private ListView mDrawerListView;
    private DrawerLayout mDrawerLayout;
    private Toolbar mToolbar;
    private ActionBarDrawerToggle mDrawerToggle;

    protected Fragment createFragment() {
        return new OverviewFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        mDrawerTitles = getResources().getStringArray(R.array.drawer_general_array);
        mDrawerListView = (ListView) findViewById(R.id.drawer_general_list_view);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_general);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        mDrawerListView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mDrawerTitles));
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.app_name, R.string.app_name);
        mDrawerToggle.syncState();
        mFab = (FloatingActionButton) findViewById(R.id.activity_common_fab_add);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);
        if(fragment == null) {
            fragment = createFragment();
            fm.beginTransaction()
                    .add(R.id.fragment_container, fragment)
                    .commit();
        }
        mBottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation_view);
        mBottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()) {
                    case R.id.action_overview_tab:
                        onOverviewTab();
                        break;
                    case R.id.action_training_tab:
                        onTrainingTab();
                        break;
                    case R.id.action_nutrition_tab:
                        onNutritionTab();
                        break;
                }
                return true;
            }
        });
        //Todo: Fix drop list position after rotation
        if(savedInstanceState != null) {
            TAB_STATE = savedInstanceState.getInt(SAVE_STATE);
            if(TAB_STATE == 0)
                mBottomNavigationView.setSelectedItemId(R.id.action_overview_tab);
            else if(TAB_STATE == 1)
                mBottomNavigationView.setSelectedItemId(R.id.action_training_tab);
            else if(TAB_STATE == 2)
                mBottomNavigationView.setSelectedItemId(R.id.action_nutrition_tab);
        }
    }

    private void onOverviewTab() {
        TAB_STATE = 0;
        Fragment fragment = new OverviewFragment();
        mFab.hide();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    private void onTrainingTab() {
        TAB_STATE = 1;
        Fragment fragment = new DaysListFragment();
        mFab.show();
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("GA", "CALLED ONCLICKLISTENER!");
                if((TrainingStorage.get(GeneralActivity.this).getTrainings().size() == 0) && (ExerciseStorage.get(GeneralActivity.this).getExercises().size() == 0))
                    Snackbar.make(findViewById(R.id.snackbar_place), getString(R.string.training_is_empty_snackbar), Snackbar.LENGTH_LONG)
                            .setAction(R.string.add, new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(GeneralActivity.this, ProgramsListActivity.class);
                                    startActivity(intent);
                                }
                            }).show();
                else {
                    Date currentDate = new Date();
                    if (DayStorage.get(GeneralActivity.this).getDayByDate(currentDate) == null) {
                        Day day = new Day();
                        day.setTrainingId(UUID.randomUUID()); // Temporary trainingUUID as hack
                        DayStorage.get(GeneralActivity.this).addDay(day);
                        Intent intent = NewDayActivity.newIntent(GeneralActivity.this, day.getId());
                        startActivityForResult(intent, REQUEST_ADD_DAY);
                    } else
                        Snackbar.make(findViewById(R.id.snackbar_place), "Вы сегодня уже тренировались", Snackbar.LENGTH_LONG).show();
                }
            }
        });
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    private void onNutritionTab() {
        TAB_STATE = 2;
        Fragment fragment = new NutritionDaysListFragment();
        mFab.show();
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date currentDate = new Date();
                if(NutritionDayStorage.get(GeneralActivity.this).getNutritionDayByDate(currentDate) == null) {
                    NutritionDay nutritionDay = new NutritionDay(UUID.randomUUID());
                    NutritionDayStorage.get(GeneralActivity.this).addNutritionDay(nutritionDay);
                    Intent intent;
                    intent = NutritionListActivity.newIntent(GeneralActivity.this, nutritionDay.getId());
                    startActivity(intent);
                    //Todo: To strings.xml
                } else
                    Snackbar.make(findViewById(R.id.snackbar_place), "Этот день уже присутствует!", Snackbar.LENGTH_LONG).show();
            }
        });
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putInt(SAVE_STATE, TAB_STATE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actionbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = null;
        switch(item.getItemId()) {
            case R.id.settings:
                intent = new Intent(GeneralActivity.this, SettingsActivity.class);
                break;
            case R.id.programs:
                intent = new Intent(GeneralActivity.this, ProgramsListActivity.class);
                break;
            case android.R.id.home:
                mDrawerLayout.openDrawer(Gravity.START);
                break;
            default:
                //
        }
        if(intent != null)
            startActivity(intent);
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if(mDrawerLayout.isDrawerOpen(Gravity.START))
            mDrawerLayout.closeDrawer(Gravity.START);
        else
            super.onBackPressed();
    }
}
