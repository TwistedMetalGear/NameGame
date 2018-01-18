package net.silentbyte.namegame.game;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.databinding.ObservableList;

import net.silentbyte.namegame.R;
import net.silentbyte.namegame.data.Employee;
import net.silentbyte.namegame.data.NameGameRepository;
import net.silentbyte.namegame.data.source.local.ProfileEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class NameGameViewModel extends AndroidViewModel {

    public static final int ERROR_CODE_NO_ERROR = 0;
    public static final int ERROR_CODE_RETRIEVE_FAILED = 1;
    public static final int ERROR_CODE_RESTORE_FAILED = 2;
    public static final int ERROR_CODE_INSUFFICIENT_EMPLOYEE_COUNT = 3;

    private final NameGameRepository repository;

    public final ObservableList<ProfileEntity> employees = new ObservableArrayList<>();
    public final ObservableField<String> targetEmployeeName = new ObservableField<>();
    public final ObservableInt score = new ObservableInt(0);
    public final ObservableBoolean loading = new ObservableBoolean(false);

    // The following data will be persisted in the savedInstanceState bundle. While the ViewModel survives
    // configuration changes, it does not survive the process being destroyed in a low memory situation.
    // So we still need to persist some data to be able to restore the previous state.
    public final ObservableInt questionNumber = new ObservableInt(0);
    public final ObservableBoolean questionComplete = new ObservableBoolean(false);
    public final ObservableInt correctAnswers = new ObservableInt(0);
    public final ObservableInt incorrectAnswers = new ObservableInt(0);
    public final ObservableField<String> selectedEmployeeId = new ObservableField<>(null);
    public final ObservableBoolean answerConfirmed = new ObservableBoolean(false);
    public final ObservableInt errorCode = new ObservableInt(ERROR_CODE_NO_ERROR);
    public final ObservableField<String> errorText = new ObservableField<>(null);

    // The following data is also persisted in the savedInstanceState bundle. These are used to restore the
    // employees that were previously displayed after the process has been destroyed in a low memory situation.
    // Another option is to save the employees in the bundle as parcelables, but why store excess data in the
    // bundle if it is not necessary? The employees are already stored in the local database. If the process
    // gets destroyed, we only need the ids to be able to query the database and restore the employees.
    private String targetEmployeeId;
    private List<String> employeeIds = new ArrayList<>();

    private final Random random = new Random();

    private final CompositeDisposable disposables = new CompositeDisposable();

    public NameGameViewModel(Application application, NameGameRepository repository) {
        super(application);
        this.repository = repository;
    }

    public void initialize() {
        if (loading.get() || errorCode.get() != ERROR_CODE_NO_ERROR) {
            return;
        }

        if (questionNumber.get() == 0) {
            // First load of the app, load a new question.
            loadNextQuestion();
        }
        else if (employees.isEmpty()) {
            // Process was destroyed, need to query database to restore employees.
            loading.set(true);

            disposables.add(repository.getProfiles(employeeIds)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(profiles -> {
                        loading.set(false);
                        employees.addAll(profiles);

                        // Restore the name of the employee to guess.
                        for (ProfileEntity profile : profiles) {
                            if (profile.getId().equals(targetEmployeeId)) {
                                targetEmployeeName.set(profile.getFullName());
                            }
                        }

                        updateScore();
                    },
                    throwable -> {
                        loading.set(false);
                        setError(ERROR_CODE_RESTORE_FAILED,
                            getApplication().getString(R.string.employee_retrieval_error));
                    }));
        }
    }

    public void onEmployeeClick(Employee employee) {
        if (questionComplete.get()) {
            return;
        }

        answerConfirmed.set(false);
        selectedEmployeeId.set(employee.getId());
    }

    public void onActionButtonClick() {
        if (questionComplete.get()) {
            loadNextQuestion();
        }
        else {
            answerConfirmed.set(true);

            if (selectedEmployeeId.get().equals(targetEmployeeId)) {
                correctAnswers.set(correctAnswers.get() + 1);
                questionComplete.set(true);
            }
            else {
                incorrectAnswers.set(incorrectAnswers.get() + 1);
                selectedEmployeeId.set(null);
            }

            updateScore();
        }
    }

    public void onRetryClick() {
        int code = errorCode.get();
        setError(ERROR_CODE_NO_ERROR, null);

        if (code == ERROR_CODE_RETRIEVE_FAILED) {
            loadNextQuestion();
        }
        else { // Restore failed
            initialize();
        }

    }

    public void newGame() {
        clearAllData();
        initialize();
    }

    public String getTargetEmployeeId() {
        return targetEmployeeId;
    }

    public void setTargetEmployeeId(String targetEmployeeId) {
        this.targetEmployeeId = targetEmployeeId;
    }

    public List<String> getEmployeeIds() {
        return employeeIds;
    }

    public void setEmployeeIds(List<String> employeeIds) {
        this.employeeIds = employeeIds;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        disposables.dispose();
    }

    private void updateScore() {
        score.set((int) ((float) correctAnswers.get() / (correctAnswers.get() + incorrectAnswers.get()) * 100));
    }

    private void loadNextQuestion() {
        if (loading.get() || errorCode.get() != ERROR_CODE_NO_ERROR) {
            return;
        }

        clearQuestionData();
        loading.set(true);

        disposables.add(repository.getRandomProfiles()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(profiles -> {
                    loading.set(false);

                    if (profiles.size() < GameConstants.NUM_CHOICES) {
                        // Profiles were queried successfully but there is less than the minimum required to play.
                        setError(ERROR_CODE_INSUFFICIENT_EMPLOYEE_COUNT,
                            getApplication().getString(R.string.insufficient_employee_count));

                        return;
                    }

                    employees.addAll(profiles);

                    for (ProfileEntity profile : profiles) {
                        employeeIds.add(profile.getId());
                    }

                    // Choose a random employee to have the user guess.
                    int index = random.nextInt(GameConstants.NUM_CHOICES);
                    targetEmployeeId = employeeIds.get(index);
                    targetEmployeeName.set(employees.get(index).getFullName());

                    questionNumber.set(questionNumber.get() + 1);
                },
                throwable -> {
                    loading.set(false);
                    errorCode.set(ERROR_CODE_RETRIEVE_FAILED);
                    errorText.set(getApplication().getString(R.string.employee_retrieval_error));
                }));
    }

    private void clearAllData() {
        disposables.clear();
        employees.clear();
        targetEmployeeName.set(null);
        score.set(0);
        loading.set(false);
        questionNumber.set(0);
        questionComplete.set(false);
        correctAnswers.set(0);
        incorrectAnswers.set(0);
        selectedEmployeeId.set(null);
        answerConfirmed.set(false);
        errorCode.set(ERROR_CODE_NO_ERROR);
        errorText.set(null);
        targetEmployeeId = null;
        employeeIds.clear();
    }

    private void clearQuestionData() {
        employees.clear();
        employeeIds.clear();
        targetEmployeeId = null;
        targetEmployeeName.set(null);
        selectedEmployeeId.set(null);
        answerConfirmed.set(false);
        questionComplete.set(false);
    }

    private void setError(int errorCode, String errorText) {
        this.errorCode.set(errorCode);
        this.errorText.set(errorText);
    }

    @Singleton
    public static class Factory extends ViewModelProvider.NewInstanceFactory {
        private final Application application;
        private final NameGameRepository repository;

        @Inject
        public Factory(Application application, NameGameRepository repository) {
            this.application = application;
            this.repository = repository;
        }

        @Override
        public <T extends ViewModel> T create(Class<T> modelClass) {
            return (T) new NameGameViewModel(application, repository);
        }
    }
}
