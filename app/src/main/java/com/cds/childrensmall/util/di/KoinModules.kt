package com.cds.childrensmall.util.di

import com.cds.childrensmall.model.datasource.AsrAudioToTextDatasource
import com.cds.childrensmall.model.datasource.GetConfigDatasource
import com.cds.childrensmall.model.datasource.GetReadScoreDatasource
import com.cds.childrensmall.model.datasource.GetSmsCodeDatasource
import com.cds.childrensmall.model.datasource.LoginDatasource
import com.cds.childrensmall.model.datasource.NextQuestionDatasource
import com.cds.childrensmall.model.datasource.StartQuestionDatasource
import com.cds.childrensmall.model.repository.AsrRepository
import com.cds.childrensmall.model.repository.DataRepository
import com.cds.childrensmall.model.repository.LoginRepository
import com.cds.childrensmall.model.repository.QuestionRepository
import com.cds.childrensmall.viewmodel.LoginViewModel
import com.cds.childrensmall.viewmodel.MainViewModel
import com.cds.childrensmall.viewmodel.QuestionViewModel
import com.cds.childrensmall.viewmodel.ReadViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

/**
 * File Name: KoinModules.kt
 * Description: koin 依赖注入的 module
 * Author: cpf
 */
val appModule = module {
    //DataSource
    factory {
        GetSmsCodeDatasource()
    }
    factory {
        LoginDatasource()
    }
    factory {
        GetConfigDatasource()
    }
    factory {
        AsrAudioToTextDatasource()
    }
    factory {
        GetReadScoreDatasource()
    }
    factory {
        StartQuestionDatasource()
    }
    factory {
        NextQuestionDatasource()
    }

    //Repository
    factory {
        LoginRepository(get(),get())
    }
    factory {
        DataRepository(get())
    }
    factory {
        AsrRepository(get(),get())
    }
    factory {
        QuestionRepository(get(),get())
    }

    viewModel{
        LoginViewModel(get(),get())
    }

    viewModel{
        MainViewModel(get())
    }

    viewModel{
        ReadViewModel(get())
    }
    viewModel{
        QuestionViewModel(get())
    }
}