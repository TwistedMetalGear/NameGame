package net.silentbyte.namegame.game;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import net.silentbyte.namegame.R;
import net.silentbyte.namegame.dagger.AppComponent;
import net.silentbyte.namegame.dagger.AppModule;
import net.silentbyte.namegame.dagger.DaggerAppComponent;
import net.silentbyte.namegame.databinding.FragmentNameGameBinding;

import java.util.ArrayList;

import javax.inject.Inject;

public class NameGameFragment extends Fragment {

    private static final String KEY_QUESTION_NUMBER = "question_number";
    private static final String KEY_QUESTION_COMPLETE = "question_complete";
    private static final String KEY_CORRECT_ANSWERS = "correct_answers";
    private static final String KEY_INCORRECT_ANSWERS = "incorrect_answers";
    private static final String KEY_TARGET_EMPLOYEE_ID = "target_employee_id";
    private static final String KEY_EMPLOYEE_IDS = "employee_ids";
    private static final String KEY_SELECTED_EMPLOYEE_ID = "selected_employee_id";
    private static final String KEY_ANSWER_CONFIRMED = "answer_confirmed";
    private static final String KEY_ERROR_CODE = "error_code";
    private static final String KEY_ERROR_TEXT = "error_text";

    private FragmentNameGameBinding binding;

    @Inject
    NameGameViewModel.Factory factory;

    public static NameGameFragment newInstance() {
        return new NameGameFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppComponent component = DaggerAppComponent.builder()
            .appModule(new AppModule(getActivity().getApplication()))
            .build();

        component.inject(this);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_name_game, container, false);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        NameGameViewModel viewModel = ViewModelProviders.of(this, factory).get(NameGameViewModel.class);
        binding.setViewModel(viewModel);

        binding.employeeRecycler.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        binding.employeeRecycler.setAdapter(new EmployeeAdapter(viewModel));

        if (savedInstanceState != null) {
            viewModel.questionNumber.set(savedInstanceState.getInt(KEY_QUESTION_NUMBER));
            viewModel.questionComplete.set(savedInstanceState.getBoolean(KEY_QUESTION_COMPLETE));
            viewModel.correctAnswers.set(savedInstanceState.getInt(KEY_CORRECT_ANSWERS));
            viewModel.incorrectAnswers.set(savedInstanceState.getInt(KEY_INCORRECT_ANSWERS));
            viewModel.setTargetEmployeeId(savedInstanceState.getString(KEY_TARGET_EMPLOYEE_ID));
            viewModel.setEmployeeIds(savedInstanceState.getStringArrayList(KEY_EMPLOYEE_IDS));
            viewModel.selectedEmployeeId.set(savedInstanceState.getString(KEY_SELECTED_EMPLOYEE_ID));
            viewModel.answerConfirmed.set(savedInstanceState.getBoolean(KEY_ANSWER_CONFIRMED));
            viewModel.errorCode.set(savedInstanceState.getInt(KEY_ERROR_CODE));
            viewModel.errorText.set(savedInstanceState.getString(KEY_ERROR_TEXT));
        }

        viewModel.initialize();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_QUESTION_NUMBER, binding.getViewModel().questionNumber.get());
        outState.putBoolean(KEY_QUESTION_COMPLETE, binding.getViewModel().questionComplete.get());
        outState.putInt(KEY_CORRECT_ANSWERS, binding.getViewModel().correctAnswers.get());
        outState.putInt(KEY_INCORRECT_ANSWERS, binding.getViewModel().incorrectAnswers.get());
        outState.putString(KEY_TARGET_EMPLOYEE_ID, binding.getViewModel().getTargetEmployeeId());
        outState.putStringArrayList(KEY_EMPLOYEE_IDS, (ArrayList<String>) binding.getViewModel().getEmployeeIds());
        outState.putString(KEY_SELECTED_EMPLOYEE_ID, binding.getViewModel().selectedEmployeeId.get());
        outState.putBoolean(KEY_ANSWER_CONFIRMED, binding.getViewModel().answerConfirmed.get());
        outState.putInt(KEY_ERROR_CODE, binding.getViewModel().errorCode.get());
        outState.putString(KEY_ERROR_TEXT, binding.getViewModel().errorText.get());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.options, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.new_game:
                binding.getViewModel().newGame();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
