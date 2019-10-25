package com.kingleoners.infosinema;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase, "en"));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tabLayout = (TabLayout)findViewById(R.id.tablayout);
        viewPager = (ViewPager)findViewById(R.id.viewpager);

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.AddFragment(new FilmFragment(),getString(R.string.menu_film));
        adapter.AddFragment(new MovieFragment(), getString(R.string.menu_movie));

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        //Change Language
        Paper.init(this);

        String language = Paper.book().read("language");
        if (language == null)
            Paper.book().write("language", "en");

        updateView((String) Paper.book().read("language"));

    }

    private void updateView(String language) {

        Context context = LocaleHelper.setLocale(this, language);
        Resources resources = context.getResources();

        tabLayout.setTag(resources.getString(R.string.name));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_setting:
                Intent intent = new Intent(this, SettingActivity.class);
                startActivity(intent);
                return true;
            case R.id.language_en:
                Paper.book().write("language","en");
                updateView((String) Paper.book().read("language"));

                Intent changeLanguage = new Intent(this,MainActivity.class);
                changeLanguage.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(changeLanguage);
                return true;

            case R.id.language_in:
                Paper.book().write("language","in");
                updateView((String) Paper.book().read("language"));

                Intent changeLanguageIn = new Intent(this,MainActivity.class);
                changeLanguageIn.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(changeLanguageIn);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
