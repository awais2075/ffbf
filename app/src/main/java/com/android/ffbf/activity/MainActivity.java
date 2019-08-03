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

/*Main Activity
 * Implementing ViewPager inorder to show Fragments in Tabs*/
public class MainActivity extends BaseActivity implements View.OnClickListener, ViewPager.OnPageChangeListener {

    private Toolbar toolbar;
    private ViewPager viewPager;
    private TabLayout tabLayout;

    /*Tabs Title Array*/
    private String[] tabTitleList = new String[]{"Restaurants", "Street Stalls"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Util.showToast(this, user.getUserEmail() + " : " + user.getUserType());
    }

    /*Getting View associated with this Activity i.e. xml layout*/
    @Override
    protected int getView() {
        return R.layout.activity_main;
    }

    /*Initializing Views defined in xml associated with this Activity*/
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

    /*Implementing click listeners of Views that are consuming onClick Event*/
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            /*case R.id.button_signOut:
                auth.signOut();
                finish();
                break;*/
        }
    }

    /*Not Required for this Activity*/
    @Override
    public void onSuccess(List list) {

    }


    /*Not Required for this Activity*/
    @Override
    public void onFailure(String message) {

    }

    /*Not Required for this Project*/
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    /*Changing the Title of the toolbar of this Activity whenever user swipe tabs*/
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

    /*Not Required for this Project*/
    @Override
    public void onPageScrollStateChanged(int state) {

    }

    /*Option Menu specifically for this Activity containing options
     * Add Critic
     * View Profiles
     * SignOut*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        if (user.getUserType() == UserType.Admin) {
            menu.findItem(R.id.item_addCritic).setVisible(true);
        }
        return true;
    }

    /*Implementing method whenever user selects item form Option Menu*/
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
