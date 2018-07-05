package com.example.cleanarchitecture.ui.main

import android.arch.lifecycle.MutableLiveData
import android.os.Handler
import android.view.View
import com.example.cleanarchitecture.base.BaseViewModel
import com.example.cleanarchitecture.domain.usecase.user.FindUserUseCase
import com.example.cleanarchitecture.model.UserItem
import com.example.cleanarchitecture.model.UserItemMapper
import com.example.cleanarchitecture.rx.SchedulerProvider
import javax.inject.Inject

class MainViewModel @Inject constructor(
        private val mUserUseCase: FindUserUseCase,
        private val mSchedulerProvider: SchedulerProvider,
        private val mMapper: UserItemMapper
) : BaseViewModel<MainNavigator>(mUserUseCase) {

    val user = MutableLiveData<UserItem>()
    val userId = MutableLiveData<String>()

    fun load(view: View) {
        navigator!!.showLoading()
        Handler().postDelayed({
            navigator!!.hideLoading()
        }, 1500)
    }

    fun searchUser() {
        userId.value?.let {
            if (it.isNotBlank()) {
                compositeDisposable.add(mUserUseCase.createObservable(FindUserUseCase.Params(it, true))
                        .subscribeOn(mSchedulerProvider.io())
                        .observeOn(mSchedulerProvider.ui())
                        .map { mMapper.mapToPresentation(it) }
                        .subscribe({ user.postValue(it) }, {})
                )
            }
        }
    }

}
