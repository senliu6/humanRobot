package com.shciri.rosapp.ui.addtask;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.shciri.rosapp.databinding.FragmentAddNewTaskBinding;


public class AddNewTaskFragment extends Fragment {

    private FragmentAddNewTaskBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentAddNewTaskBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.returnLl.setOnClickListener(view1 -> Navigation.findNavController(view1).navigateUp());
        binding.addTaskCancelBtn.setOnClickListener(v -> Navigation.findNavController(view).navigateUp());
    }
}
