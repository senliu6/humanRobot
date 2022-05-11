package com.shciri.rosapp.ui.taskdetail;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.shciri.rosapp.R;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class TaskDetailFragment extends Fragment {

    private ViewPager viewPager;
    TabLayout tabs;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_task_detail, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewPager = view.findViewById(R.id.task_detail_view_pager);
        TasksDetailPagerAdapter tasksDetailAdapter = new TasksDetailPagerAdapter( getParentFragmentManager());
        viewPager.setAdapter(tasksDetailAdapter);
        tabs = view.findViewById(R.id.tabs);;
        tabs.setupWithViewPager(viewPager);
        view.findViewById(R.id.return_ll).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigateUp();
            }
        });

//        setupTabIcons();
//        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
//            @Override
//            public void onTabSelected(TabLayout.Tab tab) {
//                if (tab.getCustomView() != null) {
//                    tab.pagerAdapter.getPageTitle(i);
//                    ImageView tab_iv = tab.getCustomView().findViewById(R.id.title_bg);
//                    tab_iv.setVisibility(VISIBLE);
//                }
//            }
//
//            @Override
//            public void onTabUnselected(TabLayout.Tab tab) {
//                if (tab.getCustomView() != null) {
//                    ImageView tab_iv = tab.getCustomView().findViewById(R.id.title_bg);
//                    tab_iv.setVisibility(INVISIBLE);
//                }
//            }
//
//            @Override
//            public void onTabReselected(TabLayout.Tab tab) {
//
//            }
//        });
    }

//    public View getTabView(int position) {
//        View view = LayoutInflater.from(getActivity()).inflate(R.layout.item_tab, null);
//        TextView title_tv = (TextView) view.findViewById(R.id.title_tv);
//        title_tv.setText(titles.get(position));
//        ImageView img_title = (ImageView) view.findViewById(R.id.img_title);
//        img_title.setImageResource(tabIcons[position]);
//
//        if (position == 0) {
//            title_tv.setTextColor(Color.YELLOW);
//            img_title.setImageResource(tabIconsPressed[position]);
//        } else {
//            title_tv.setTextColor(Color.WHITE);
//            img_title.setImageResource(tabIcons[position]);
//        }
//        return view;
//    }
//
//    private void setupTabIcons() {
//        tabs.getTabAt(0).setCustomView(getTabView(0));
//        tabs.getTabAt(1).setCustomView(getTabView(1));
//        tabs.getTabAt(2).setCustomView(getTabView(2));
//    }
}