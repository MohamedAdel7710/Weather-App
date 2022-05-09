package com.example.skyreference.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.skyreference.model.RepositoryInterface

class HomeViewModelFactory(private var _repo: RepositoryInterface) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            HomeViewModel(_repo) as T
        } else {
            throw IllegalArgumentException("ViewModel Class not found")
        }
    }
}