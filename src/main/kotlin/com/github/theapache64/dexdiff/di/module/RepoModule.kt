package com.github.theapache64.dexdiff.di.module

import com.github.theapache64.dexdiff.data.repo.AppRepo
import com.github.theapache64.dexdiff.data.repo.AppRepoImpl
import dagger.Binds
import dagger.Module

@Module
abstract class RepoModule {

    @Binds
    abstract fun bindAppRepo(appRepo: AppRepoImpl) : AppRepo
}