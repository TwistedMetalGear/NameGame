package net.silentbyte.namegame.game;

import android.app.Application;
import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.databinding.Observable;
import android.databinding.ObservableBoolean;

import net.silentbyte.namegame.data.Employee;
import net.silentbyte.namegame.data.NameGameRepository;
import net.silentbyte.namegame.data.source.local.ProfileEntity;
import net.silentbyte.namegame.util.TestHelper;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Single;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class NameGameViewModelTest {

    // The ViewModel under test.
    private NameGameViewModel viewModel;

    // A set of employee ids to test with.
    private List<String> employeeIds;

    // Tracks changes to the ViewModel's loading flag.
    private List<Boolean> loadingValues;

    @Mock
    private Application application;
    @Mock
    private NameGameRepository repository;

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();
    @Rule
    public InstantTaskExecutorRule rule = new InstantTaskExecutorRule();

    @Before
    public void createViewModel() {
        viewModel = new NameGameViewModel(application, repository);
    }

    @Before
    public void createEmployeeIds() {
        employeeIds = new ArrayList<>();

        for (int i = 0; i < 6; i++) {
            employeeIds.add(String.valueOf(i));
        }
    }

    @Before
    public void observeLoading() {
        loadingValues = new ArrayList<>();
        observeBoolean(viewModel.loading, loadingValues);
    }

    @Before
    public void initializeRxSchedulers() {
        TestHelper.initializeRxSchedulers();
    }

    @Test
    public void initialize_loadNextQuestion() {
        Single<List<ProfileEntity>> single = Single.create(emitter -> emitter.onSuccess(TestHelper.getProfiles()));
        when(repository.getRandomProfiles()).thenReturn(single);
        viewModel.initialize();
        verifyLoadNextQuestion(2);
    }

    @Test
    public void initialize_loadNextQuestion_insufficientEmployeeCount() {
        // We will create a profiles list with one less profile than expected.
        List<ProfileEntity> profiles = TestHelper.getProfiles();
        profiles.remove(profiles.size() - 1);

        Single<List<ProfileEntity>> single = Single.create(emitter -> emitter.onSuccess(profiles));
        when(repository.getRandomProfiles()).thenReturn(single);

        viewModel.initialize();
        verify(repository, times(1)).getRandomProfiles();

        // Verify that errorCode and errorText have been set.
        assertEquals(NameGameViewModel.ERROR_CODE_INSUFFICIENT_EMPLOYEE_COUNT, viewModel.errorCode.get());
        assertNotNull(viewModel.errorText);

        // Verify that the employees and employeeIds lists have not been populated.
        assertEquals(0, viewModel.employees.size());
        assertEquals(0, viewModel.getEmployeeIds().size());

        // Verify that targetEmployeeId and targetEmployeeName have not been set.
        assertNull(viewModel.getTargetEmployeeId());
        assertNull(viewModel.targetEmployeeName.get());

        // Verify that question number has not been set.
        assertEquals(viewModel.questionNumber.get(), 0);

        // Verify that loading was set twice, first to true, then to false.
        assertEquals(2, loadingValues.size());
        assertTrue(loadingValues.get(0));
        assertFalse(loadingValues.get(1));
    }

    @Test
    public void initialize_loadNextQuestion_throwsException() {
        Single<List<ProfileEntity>> single = Single.create(emitter -> emitter.onError(new Exception()));
        when(repository.getRandomProfiles()).thenReturn(single);

        viewModel.initialize();
        verify(repository, times(1)).getRandomProfiles();

        // Verify that errorCode and errorText have been set.
        assertEquals(NameGameViewModel.ERROR_CODE_RETRIEVE_FAILED, viewModel.errorCode.get());
        assertNotNull(viewModel.errorText);

        // Verify that the employees and employeeIds lists have not been populated.
        assertEquals(0, viewModel.employees.size());
        assertEquals(0, viewModel.getEmployeeIds().size());

        // Verify that targetEmployeeId and targetEmployeeName have not been set.
        assertNull(viewModel.getTargetEmployeeId());
        assertNull(viewModel.targetEmployeeName.get());

        // Verify that question number has not been set.
        assertEquals(viewModel.questionNumber.get(), 0);

        // Verify that loading was set twice, first to true, then to false.
        assertEquals(2, loadingValues.size());
        assertTrue(loadingValues.get(0));
        assertFalse(loadingValues.get(1));
    }

    @Test
    public void initialize_restoreData() {
        restoreDataPreconditions();
        viewModel.initialize();
        verifyRestoreData();
    }

    @Test
    public void initialize_restoreData_throwsException() {
        List<String> employeeIds = new ArrayList<>();

        for (int i = 0; i < 6; i++) {
            employeeIds.add(String.valueOf(i));
        }

        // Set viewModel state in such a way that it triggers a restoration of data upon calling initialize.
        restoreDataPreconditions();

        Single<List<ProfileEntity>> single = Single.create(emitter -> emitter.onError(new Exception()));
        when(repository.getProfiles(employeeIds)).thenReturn(single);

        viewModel.initialize();
        verify(repository, times(1)).getProfiles(employeeIds);

        // Verify that errorCode and errorText have been set.
        assertEquals(NameGameViewModel.ERROR_CODE_RESTORE_FAILED, viewModel.errorCode.get());
        assertNotNull(viewModel.errorText);

        // Verify that the employees list has not been populated.
        assertEquals(0, viewModel.employees.size());

        // Verify that targetEmployeeName has not been set.
        assertNull(viewModel.targetEmployeeName.get());

        // Verify that score has not been set.
        assertEquals(0, viewModel.score.get());

        // Verify that loading was set twice, first to true, then to false.
        assertEquals(2, loadingValues.size());
        assertTrue(loadingValues.get(0));
        assertFalse(loadingValues.get(1));
    }

    @Test
    public void initialize_loadingInProgress() {
        viewModel.loading.set(true);
        viewModel.initialize();
        verify(repository, never()).getRandomProfiles();
        verify(repository, never()).getProfiles(anyList());
    }

    @Test
    public void initialize_errorShowing() {
        viewModel.errorCode.set(NameGameViewModel.ERROR_CODE_RETRIEVE_FAILED);
        viewModel.initialize();
        verify(repository, never()).getRandomProfiles();
        verify(repository, never()).getProfiles(anyList());
    }

    @Test
    public void onEmployeeClick() {
        ProfileEntity employee = new ProfileEntity();
        employee.setId("1");

        viewModel.answerConfirmed.set(true);
        viewModel.onEmployeeClick(employee);

        assertFalse(viewModel.answerConfirmed.get());
        assertEquals("1", viewModel.selectedEmployeeId.get());
    }

    @Test
    public void onEmployeeClick_questionComplete() {
        ProfileEntity employee = new ProfileEntity();
        employee.setId("1");

        viewModel.answerConfirmed.set(true);
        viewModel.questionComplete.set(true);

        viewModel.onEmployeeClick(employee);
        assertTrue(viewModel.answerConfirmed.get());
        assertNull(viewModel.selectedEmployeeId.get());
    }

    @Test
    public void onActionButtonClick_correctAnswer() {
        viewModel.setTargetEmployeeId("3");
        viewModel.selectedEmployeeId.set("3");
        viewModel.onActionButtonClick();

        assertTrue(viewModel.answerConfirmed.get());
        assertEquals(1, viewModel.correctAnswers.get());
        assertTrue(viewModel.questionComplete.get());
        assertEquals(100, viewModel.score.get());
    }

    @Test
    public void onActionButtonClick_incorrectAnswer() {
        viewModel.setTargetEmployeeId("4");
        viewModel.selectedEmployeeId.set("3");
        viewModel.onActionButtonClick();

        assertTrue(viewModel.answerConfirmed.get());
        assertEquals(1, viewModel.incorrectAnswers.get());
        assertEquals(0, viewModel.score.get());
    }

    @Test
    public void onActionButtonClick_loadNextQuestion() {
        viewModel.questionComplete.set(true);

        Single<List<ProfileEntity>> single = Single.create(emitter -> emitter.onSuccess(TestHelper.getProfiles()));
        when(repository.getRandomProfiles()).thenReturn(single);

        viewModel.onActionButtonClick();
        assertFalse(viewModel.answerConfirmed.get());
        assertFalse(viewModel.questionComplete.get());
        assertEquals(0, viewModel.correctAnswers.get());
        assertEquals(0, viewModel.incorrectAnswers.get());
        assertEquals(0, viewModel.score.get());

        verifyLoadNextQuestion(2);
    }

    @Test
    public void onRetryClick_loadNextQuestion() {
        viewModel.errorCode.set(NameGameViewModel.ERROR_CODE_RETRIEVE_FAILED);
        viewModel.errorText.set("Retrieve Failed");

        Single<List<ProfileEntity>> single = Single.create(emitter -> emitter.onSuccess(TestHelper.getProfiles()));
        when(repository.getRandomProfiles()).thenReturn(single);

        viewModel.onRetryClick();

        assertEquals(NameGameViewModel.ERROR_CODE_NO_ERROR, viewModel.errorCode.get());
        assertNull(viewModel.errorText.get());
        verifyLoadNextQuestion(2);
    }

    @Test
    public void onRetryClick_restoreData() {
        viewModel.errorCode.set(NameGameViewModel.ERROR_CODE_RESTORE_FAILED);
        viewModel.errorText.set("Restore Failed");

        restoreDataPreconditions();
        viewModel.onRetryClick();

        assertEquals(NameGameViewModel.ERROR_CODE_NO_ERROR, viewModel.errorCode.get());
        assertNull(viewModel.errorText.get());
        verifyRestoreData();
    }

    @Test
    public void newGame() {
        // Set some data to verify that it clears after a new game is started.
        viewModel.score.set(50);
        viewModel.loading.set(true);
        viewModel.questionComplete.set(true);
        viewModel.correctAnswers.set(1);
        viewModel.incorrectAnswers.set(1);
        viewModel.selectedEmployeeId.set("1");
        viewModel.answerConfirmed.set(true);
        viewModel.errorCode.set(NameGameViewModel.ERROR_CODE_RETRIEVE_FAILED);
        viewModel.errorText.set("Retrieve Failed");

        Single<List<ProfileEntity>> single = Single.create(emitter -> emitter.onSuccess(TestHelper.getProfiles()));
        when(repository.getRandomProfiles()).thenReturn(single);

        viewModel.newGame();

        // Verify data is cleared.
        assertEquals(0, viewModel.score.get());
        assertFalse(viewModel.loading.get());
        assertFalse(viewModel.questionComplete.get());
        assertEquals(0, viewModel.correctAnswers.get());
        assertEquals(0, viewModel.incorrectAnswers.get());
        assertNull(viewModel.selectedEmployeeId.get());
        assertFalse(viewModel.answerConfirmed.get());
        assertEquals(NameGameViewModel.ERROR_CODE_NO_ERROR, viewModel.errorCode.get());
        assertNull(viewModel.errorText.get());

        verifyLoadNextQuestion(4);
    }

    private void verifyLoadNextQuestion(int expectedLoadingChanges) {
        verify(repository, times(1)).getRandomProfiles();

        // Verify that the employees and employeeIds lists were populated with the expected number of employees.
        assertEquals(6, viewModel.employees.size());
        assertEquals(6, viewModel.getEmployeeIds().size());

        // Verify that the employees and employeeIds lists were populated with the expected employees and ids.
        for (int i = 0; i < 6; i++) {
            assertEquals(String.valueOf(i + 1), viewModel.employees.get(i).getId());
            assertEquals(String.valueOf(i + 1), viewModel.getEmployeeIds().get(i));
        }

        boolean found = false;

        // Verify that targetEmployeeId was set to one of the ids within the retrieved employees.
        for (Employee employee : viewModel.employees) {
            if (employee.getId().equals(viewModel.getTargetEmployeeId())) {
                found = true;
                break;
            }
        }

        assertTrue(found);
        found = false;

        // Verify that targetEmployeeName was set to one of names within the retrieved employees.
        for (Employee employee : viewModel.employees) {
            if (employee.getFullName().equals(viewModel.targetEmployeeName.get())) {
                found = true;
                break;
            }
        }

        assertTrue(found);

        // Verify that question number has been set to 1.
        assertEquals(viewModel.questionNumber.get(), 1);

        // Verify that loading was set twice, first to true, then to false.
        assertEquals(expectedLoadingChanges, loadingValues.size());
        assertTrue(loadingValues.get(0));
        assertFalse(loadingValues.get(1));
    }

    private void restoreDataPreconditions() {
        // Set viewModel state in such a way that it triggers a restoration of data upon calling initialize.
        viewModel.questionNumber.set(1);
        viewModel.correctAnswers.set(1);
        viewModel.incorrectAnswers.set(1);
        viewModel.setTargetEmployeeId("3");
        viewModel.setEmployeeIds(employeeIds);

        Single<List<ProfileEntity>> single = Single.create(emitter -> emitter.onSuccess(TestHelper.getProfiles()));
        when(repository.getProfiles(employeeIds)).thenReturn(single);
    }

    private void verifyRestoreData() {
        verify(repository, times(1)).getProfiles(employeeIds);

        // Verify that the employees list was populated with the expected number of employees.
        assertEquals(6, viewModel.employees.size());

        // Verify that the employees list was populated with the expected employees.
        for (int i = 0; i < 6; i++) {
            assertEquals(String.valueOf(i + 1), viewModel.employees.get(i).getId());
        }

        // Verify that targetEmployeeName was restored.
        assertEquals(viewModel.targetEmployeeName.get(), viewModel.employees.get(2).getFullName());

        // Verify that score was restored.
        assertEquals(50, viewModel.score.get());

        // Verify that loading was set twice, first to true, then to false.
        assertEquals(2, loadingValues.size());
        assertTrue(loadingValues.get(0));
        assertFalse(loadingValues.get(1));
    }

    /**
     * Initializes an observer that observes the specified ObservableBoolean. Any time the
     * boolean value is updated, its value will be added to the specified values list.
     */
    private void observeBoolean(ObservableBoolean observable, List<Boolean> values) {
        observable.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable observable, int i) {
                values.add(((ObservableBoolean) observable).get());
            }
        });
    }
}