package com.parsanasekhi.store.di

import android.content.Context
import androidx.room.Room
import com.parsanasekhi.store.model.db.AppDatabase
import com.parsanasekhi.store.model.net.createApiService
import com.parsanasekhi.store.model.repository.cart.CartRepository
import com.parsanasekhi.store.model.repository.cart.CartRepositoryImpl
import com.parsanasekhi.store.model.repository.comment.CommentRepository
import com.parsanasekhi.store.model.repository.comment.CommentRepositoryImpl
import com.parsanasekhi.store.model.repository.product.ProductRepository
import com.parsanasekhi.store.model.repository.product.ProductRepositoryImpl
import com.parsanasekhi.store.model.repository.user.UserRepository
import com.parsanasekhi.store.model.repository.user.UserRepositoryImpl
import com.parsanasekhi.store.ui.features.cart.CartViewModel
import com.parsanasekhi.store.ui.features.category.CategoryViewModel
import com.parsanasekhi.store.ui.features.main.MainViewModel
import com.parsanasekhi.store.ui.features.product.ProductViewModel
import com.parsanasekhi.store.ui.features.profile.ProfileViewModel
import com.parsanasekhi.store.ui.features.signIn.SignInViewModel
import com.parsanasekhi.store.ui.features.signUp.SignUpViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val myModules = module {

    single { androidContext().getSharedPreferences("data", Context.MODE_PRIVATE) }
    single { createApiService() }
    single {
        Room.databaseBuilder(androidContext(), AppDatabase::class.java, "app_dataBase.db").build()
    }

    single<UserRepository> { UserRepositoryImpl(get(), get()) }
    single<ProductRepository> { ProductRepositoryImpl(get(), get<AppDatabase>().productDao()) }
    single<CommentRepository> { CommentRepositoryImpl(get()) }
    single<CartRepository> { CartRepositoryImpl(get(), get()) }

    viewModel { SignUpViewModel(get()) }
    viewModel { SignInViewModel(get()) }
    viewModel { (isInternetConnected: Boolean) -> MainViewModel(get(), get(), isInternetConnected) }
    viewModel { CategoryViewModel(get()) }
    viewModel { ProductViewModel(get(), get(), get()) }
    viewModel { ProfileViewModel(get()) }
    viewModel { CartViewModel(get(), get()) }

}