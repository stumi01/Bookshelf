package com.bencestumpf.bookshelf.domain.usecases

import io.reactivex.Single

abstract class ParameterizedUsecase<Parameter, ReturnType> : Usecase<ReturnType> {

    protected var parameter: Parameter? = null

    fun withParams(parameter: Parameter): ParameterizedUsecase<Parameter, ReturnType> {
        this.parameter = parameter
        return this
    }

}

interface Usecase<ReturnType> {

    fun getSubscribable(): Single<ReturnType>

}