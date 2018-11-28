package com.bencestumpf.bookshelf.ui.common

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bencestumpf.bookshelf.domain.usecases.Usecase
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

abstract class LCEViewModel<M> : ViewModel() {

    var compositeDisposable: CompositeDisposable = CompositeDisposable()

    var content: MutableLiveData<LCEModel<M>> = MutableLiveData()

    override fun onCleared() {
        compositeDisposable.clear()
        content = MutableLiveData()
        super.onCleared()
    }

    protected fun setLoading() {
        content.value = LCEModel(status = LCEStatus.LOADING)
    }

    public fun <T> Usecase<T>.execute(
        success: (T) -> Unit,
        error: (Throwable) -> Unit = this@LCEViewModel::onError
    ) {
        compositeDisposable.add(
            this.getSubscribable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(success, error)
        )
    }

    private fun onError(throwable: Throwable) {
        Log.d("LCEViewModel", "Error during usecase execute", throwable)
        content.value = LCEModel(status = LCEStatus.ERROR, error = throwable)
    }


}