package com.shciri.rosapp.ui.control;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.shciri.rosapp.R;
import com.shciri.rosapp.databinding.FragmentChooseTaskBinding;
import com.shciri.rosapp.ui.TaskControlActivity;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class ChooseTaskFragment extends Fragment {

    private View mOpenDrawer;
    private View mHealthDialog;
    private FragmentChooseTaskBinding binding;
    private OnBackPressedCallback mBackPressedCallback;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentChooseTaskBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mOpenDrawer = binding.openDrawerIv;

        // Set up the user interaction to manually show or hide the system UI.
        mOpenDrawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((TaskControlActivity)getActivity()).openFrawerLayout();
            }
        });

        mHealthDialog = binding.healthIv;
        mHealthDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HealthDialog healthDialog = new HealthDialog(getActivity());
                healthDialog.setCancelable(false);//是否可以点击DialogView外关闭Dialog
                healthDialog.setCanceledOnTouchOutside(false);//是否可以按返回按钮关闭Dialog
                healthDialog.show();
            }
        });
        binding.taskReportIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(),"onClick task report",Toast.LENGTH_SHORT).show();
            }
        });
        binding.taskReportIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate(R.id.action_nav_home_to_taskReportActivity);
            }
        });
        binding.manualControlIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate(R.id.action_nav_home_to_manuaControlFragment);
            }
        });
        binding.taskBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(view).navigate(R.id.action_nav_home_to_taskDetailFragment);
            }
        });

//        mBackPressedCallback = new OnBackPressedCallback(true /* enabled by default */) {
//            @Override
//            public void handleOnBackPressed() {
//                Navigation.findNavController(view).navigateUp();
//            }
//        };
//        requireActivity().getOnBackPressedDispatcher().addCallback(getActivity(), mBackPressedCallback);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        mBackPressedCallback.remove();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}