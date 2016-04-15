package com.example.acer.myfairlady.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.acer.myfairlady.Menu.Item;
import com.example.acer.myfairlady.Menu.MenuAdapter;
import com.example.acer.myfairlady.R;
import com.example.acer.myfairlady.fragment.AlbumFragment;
import com.example.acer.myfairlady.fragment.ArtistFragment;
import com.example.acer.myfairlady.fragment.SonglistFragment;

import net.simonvt.menudrawer.MenuDrawer;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends android.support.v4.app.FragmentActivity implements android.support.v4.app.FragmentManager.OnBackStackChangedListener {

    private MenuDrawer mDawer;
    private android.support.v4.view.ViewPager mViewPager;
    private android.support.v4.app.FragmentPagerAdapter mFragmentPagerAdapter;
    private android.support.v4.app.FragmentManager mFragmentManager;
    private List<android.support.v4.app.Fragment> mFragments;
    private List<Item> mItems = new ArrayList<Item>();
    private ListView mListview;
    private TextView tab_songlist;
    private TextView tab_artsit;
    private TextView tab_album;
    private ImageView tabline;
    private int mScreen1_3;
    private int mCurrentPageIndex;
    private int mBackStackEntryCount = 0;
    /**
     * 手势检测
     */
    private GestureDetector mDetector = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportFragmentManager().addOnBackStackChangedListener(this);

        mBackStackEntryCount = getSupportFragmentManager()
                .getBackStackEntryCount();


        initView();

        loadMenuListView();

        // 设置滑动手势
        mDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2,
                                   float velocityX, float velocityY) {
                // 从左向右滑动
                if (e1 != null && e2 != null) {
                    if (e1.getX() - e2.getX() > 120) {
                        switchToPlayer();
                        return true;
                    }
                }
                return false;
            }
        });


    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return this.mDetector.onTouchEvent(event);
    }

    private void inittabline() {

        tabline = (ImageView) findViewById(R.id.iv_tabline);
        Display display = getWindow().getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);
        mScreen1_3 = outMetrics.widthPixels / 3;

        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) tabline.getLayoutParams();
        layoutParams.width = mScreen1_3;
        tabline.setLayoutParams(layoutParams);
    }


    private void initView() {

        Log.i("log", "initview" + System.currentTimeMillis());

        mDawer = MenuDrawer.attach(this);
        mDawer.setContentView(R.layout.activity_main);
        mDawer.setMenuView(R.layout.menu);
        tab_album = (TextView) findViewById(R.id.tab_album);
        tab_artsit = (TextView) findViewById(R.id.tab_artist);
        tab_songlist = (TextView) findViewById(R.id.tab_song);

        inittabline();

        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams)
                        tabline.getLayoutParams();
                if (mCurrentPageIndex == 0 && position == 0) {
                    layoutParams.leftMargin = (int) (positionOffset * mScreen1_3 +
                            mCurrentPageIndex * mScreen1_3);
                } else if (mCurrentPageIndex == 1 && position == 0) {
                    layoutParams.leftMargin = (int) ((positionOffset - 1) * mScreen1_3 +
                            mCurrentPageIndex * mScreen1_3);
                } else if (mCurrentPageIndex == 1 && position == 1) {
                    layoutParams.leftMargin = (int) (positionOffset * mScreen1_3 +
                            mCurrentPageIndex * mScreen1_3);
                } else if (mCurrentPageIndex == 2 && position == 1) {
                    layoutParams.leftMargin = (int) ((positionOffset - 1) * mScreen1_3 +
                            mCurrentPageIndex * mScreen1_3);
                }

                tabline.setLayoutParams(layoutParams);
            }

            @Override
            public void onPageSelected(int position) {
                reSetTextView();
                switch (position) {
                    case 0:
                        tab_songlist.setTextColor(Color.parseColor("#FFD700"));
                        break;
                    case 1:
                        tab_artsit.setTextColor(Color.parseColor("#87CEFA"));
                        break;
                    case 2:
                        tab_album.setTextColor(Color.parseColor("#E0FFFF"));
                        break;
                }

                mCurrentPageIndex = position;
            }


            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mFragments = new ArrayList<android.support.v4.app.Fragment>();
        SonglistFragment songlistFragment = new SonglistFragment();
        ArtistFragment artistFragment = new ArtistFragment();
        AlbumFragment albumFragment = new AlbumFragment();
        mFragments.add(songlistFragment);
        mFragments.add(artistFragment);
        mFragments.add(albumFragment);

        mFragmentPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public android.support.v4.app.Fragment getItem(int position) {
                return mFragments.get(position);
            }

            @Override
            public int getCount() {
                return mFragments.size();
            }
        };

        mViewPager.setAdapter(mFragmentPagerAdapter);


    }

    public void switchToPlayer() {
        startActivity(new Intent(getApplicationContext(), PlayerActivity.class));
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }

    private void reSetTextView() {
        tab_songlist.setTextColor(Color.BLACK);
        tab_artsit.setTextColor(Color.BLACK);
        tab_album.setTextColor(Color.BLACK);
    }

    private void loadMenuListView() {
        Log.i("log", "loadmenulistview" + System.currentTimeMillis());
        Item mItem1 = new Item("哈", 0);
        Item mItem2 = new Item("哈哈", 0);
        Item mItem3 = new Item("哈哈哈", 0);

        mItems.add(mItem1);
        mItems.add(mItem2);
        mItems.add(mItem3);

        mListview = (ListView) findViewById(R.id.menulv);
        mListview.setOnItemClickListener(mItemClickListener);
        mListview.setAdapter(new MenuAdapter(this, mItems));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            mDawer.setActiveView(view, position);
            switch (position) {
                case 0:
//                    view.setBackground(getDrawable(R.drawable.beatles));
                    parent.setBackground(getDrawable(R.drawable.beatles));
                case 2:
//                    view.setBackground(getDrawable(R.drawable.dosthbad));
            }

        }
    };


    @Override
    public void onBackStackChanged() {

        // 如果后退栈条目数目增加了
        if (mBackStackEntryCount < getSupportFragmentManager()
                .getBackStackEntryCount()) {
            mBackStackEntryCount++;

        } else {// 如果后退栈条目数目减少了
            mBackStackEntryCount--;
        }
    }
}
