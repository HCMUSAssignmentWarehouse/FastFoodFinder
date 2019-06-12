package com.iceteaviet.fastfoodfinder.ui.login

import com.google.firebase.auth.AuthCredential
import com.iceteaviet.fastfoodfinder.data.DataManager
import com.iceteaviet.fastfoodfinder.data.remote.user.model.User
import com.iceteaviet.fastfoodfinder.utils.exception.NotFoundException
import com.iceteaviet.fastfoodfinder.utils.getFakeUserStoreLists
import com.iceteaviet.fastfoodfinder.utils.rx.SchedulerProvider
import com.iceteaviet.fastfoodfinder.utils.rx.TrampolineSchedulerProvider
import com.nhaarman.mockitokotlin2.any
import io.reactivex.Single
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations

class LoginPresenterTest {
    @Mock
    private lateinit var loginView: LoginContract.View

    @Mock
    private lateinit var dataManager: DataManager

    private lateinit var loginPresenter: LoginPresenter

    private lateinit var schedulerProvider: SchedulerProvider

    @Before
    fun setupPresenter() {
        MockitoAnnotations.initMocks(this)
        schedulerProvider = TrampolineSchedulerProvider()

        loginPresenter = LoginPresenter(dataManager, schedulerProvider, loginView)
    }

    @Test
    fun subscribeTest_signedIn() {
        // Preconditions
        `when`(dataManager.isSignedIn()).thenReturn(true)

        loginPresenter.subscribe()

        verify(loginView).exit()
    }

    @Test
    fun subscribeTest_notSignedIn() {
        // Preconditions
        `when`(dataManager.isSignedIn()).thenReturn(false)

        loginPresenter.subscribe()

        verifyZeroInteractions(loginView)
    }

    @Test
    fun onSkipButtonClickTest() {
        loginPresenter.onSkipButtonClick()

        verify(loginView).showMainView()
    }

    @Test
    fun onLoginSuccessTest_getUserSuccess() {
        // Preconditions
        `when`(dataManager.getUser(USER_UID)).thenReturn(Single.just(userFull))

        loginPresenter.onLoginSuccess(user)

        verify(dataManager).setCurrentUser(userFull)
        verify(loginView).showMainView()
    }

    @Test
    fun onLoginSuccessTest_getUserError() {
        // Preconditions
        `when`(dataManager.getUser(USER_UID)).thenReturn(Single.error(NotFoundException()))

        loginPresenter.onLoginSuccess(user)

        verify(loginView).showGeneralErrorMessage()
        verify(dataManager).setCurrentUser(user)
        verify(loginView).showMainView()
    }

    @Test
    fun onRequestGoogleAccountSuccessTest_signInSuccess_getUserSuccess() {
        // Preconditions
        `when`(dataManager.signInWithCredential(any())).thenReturn(Single.just(user))
        `when`(dataManager.getUser(USER_UID)).thenReturn(Single.just(userFull))

        loginPresenter.onRequestGoogleAccountSuccess(mock(AuthCredential::class.java), false)

        verify(dataManager).insertOrUpdateUser(user)
        verify(dataManager).setCurrentUser(userFull)
        verify(loginView).showMainView()
    }

    @Test
    fun onRequestGoogleAccountSuccessTest_signInSuccess_getUserError() {
        // Preconditions
        `when`(dataManager.signInWithCredential(any())).thenReturn(Single.just(user))
        `when`(dataManager.getUser(USER_UID)).thenReturn(Single.error(NotFoundException()))

        loginPresenter.onRequestGoogleAccountSuccess(mock(AuthCredential::class.java), false)

        verify(dataManager).insertOrUpdateUser(user)
        verify(loginView).showGeneralErrorMessage()
        verify(dataManager).setCurrentUser(user)
        verify(loginView).showMainView()
    }

    @Test
    fun onRequestGoogleAccountSuccessTest_signInSuccess_fromLastSignIn_getUserSuccess() {
        // Preconditions
        `when`(dataManager.signInWithCredential(any())).thenReturn(Single.just(user))
        `when`(dataManager.getUser(USER_UID)).thenReturn(Single.just(userFull))

        loginPresenter.onRequestGoogleAccountSuccess(mock(AuthCredential::class.java), true)

        verify(dataManager, never()).insertOrUpdateUser(user)
        verify(dataManager).setCurrentUser(userFull)
        verify(loginView).showMainView()
    }

    @Test
    fun onRequestGoogleAccountSuccessTest_signInSuccess_fromLastSignIn_getUserError() {
        // Preconditions
        `when`(dataManager.signInWithCredential(any())).thenReturn(Single.just(user))
        `when`(dataManager.getUser(USER_UID)).thenReturn(Single.error(NotFoundException()))

        loginPresenter.onRequestGoogleAccountSuccess(mock(AuthCredential::class.java), true)

        verify(dataManager, never()).insertOrUpdateUser(user)
        verify(loginView).showGeneralErrorMessage()
        verify(dataManager).setCurrentUser(user)
        verify(loginView).showMainView()
    }


    @Test
    fun onRequestGoogleAccountSuccessTest_signInFailed() {
        // Preconditions
        `when`(dataManager.signInWithCredential(any())).thenReturn(Single.error(NotFoundException()))

        loginPresenter.onRequestGoogleAccountSuccess(mock(AuthCredential::class.java), false)

        verify(loginView).showSignInFailMessage()
    }

    companion object {
        private const val USER_UID = "123"
        private const val USER_NAME = "My name"
        private const val USER_EMAIL = "myemail@gmail.com"
        private const val USER_PHOTO_URL = "photourl.jpg"

        private val user = User(USER_UID, "", "", "", ArrayList())
        private val userFull = User(USER_UID, USER_NAME, USER_EMAIL, USER_PHOTO_URL, getFakeUserStoreLists())
    }
}