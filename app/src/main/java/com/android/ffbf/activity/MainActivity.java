package com.android.ffbf.activity;

import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.android.ffbf.R;
import com.android.ffbf.adapter.ViewPagerAdapter;
import com.android.ffbf.enums.UserType;
import com.android.ffbf.fragment.RestaurantFragment;
import com.android.ffbf.fragment.StreetFragment;
import com.android.ffbf.util.Util;
import com.google.android.material.tabs.TabLayout;

import java.util.List;

public class MainActivity extends BaseActivity implements View.OnClickListener, ViewPager.OnPageChangeListener {


    private Toolbar toolbar;
    private ViewPager viewPager;
    private TabLayout tabLayout;

    private String[] tabTitleList = new String[]{"Restaurants", "Street Stalls"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        Util.showToast(this, user.getUserEmail() + " : " + user.getUserType());
    }

    @Override
    protected int getView() {
        return R.layout.activity_main;
    }

    @Override
    protected void initViews() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewPager = findViewById(R.id.viewPager);
        setUpViewPager(viewPager);

        tabLayout = findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);

    }

    private void setUpViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new RestaurantFragment(fireBaseDb, user), tabTitleList[0]);
        adapter.addFragment(new StreetFragment(fireBaseDb, user), tabTitleList[1]);

        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            /*case R.id.button_signOut:
                auth.signOut();
                finish();
                break;*/
        }
    }


    @Override
    public void onSuccess(List list) {

    }

    @Override
    public void onFailure(String message) {

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        switch (position) {
            case 0:
                toolbar.setTitle(tabTitleList[position]);
                break;
            case 1:
                toolbar.setTitle(tabTitleList[position]);
                break;
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        if (user.getUserType() == UserType.Admin) {
            menu.findItem(R.id.item_addCritic).setVisible(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_viewProfiles:
                startActivity(new Intent(this, ProfileActivity.class));
                break;
            case R.id.item_addCritic:
                startActivity(new Intent(this, SignUpActivity.class).putExtra("isAdmin", true));
                break;
            case R.id.item_signOut:
                auth.signOut();
                startActivity(new Intent(this, SignInActivity.class));
                finish();
                break;
        }
        return true;
    }
}
