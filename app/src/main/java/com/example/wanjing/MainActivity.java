package com.example.wanjing;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.os.Bundle;
import com.example.wanjing.Adapter.MainAdapter;
import com.google.android.material.tabs.TabLayout;

public class MainActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tabLayout = findViewById(R.id.main_tab);
        viewPager = findViewById(R.id.viewPage_1);

        tabLayout.setupWithViewPager(viewPager);

        MainAdapter vpAdapter = new MainAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        vpAdapter.addfragment(new LoginFragment(), "LOGIN");
        vpAdapter.addfragment(new SignUpFragment(), "REGISTER");
        viewPager.setAdapter(vpAdapter);

    }
}