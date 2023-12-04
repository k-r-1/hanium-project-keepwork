package com.example.a23_hf069

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedSelectionViewModel : ViewModel() {
    var selectedJob: String? = ""
    var selectedRegion: String? = ""
    var selectedJobCode: String?= ""
    var keywordRegions: String =""

    private val __filteredList = MutableLiveData<List<WantedFilteringFragment.Wanted>>()

    val _filteredList: LiveData<List<WantedFilteringFragment.Wanted>> get() = __filteredList


    // 필터링된 리스트들을 업데이트하는 함수
    fun updateFilteredList(filteredList: List<WantedFilteringFragment.Wanted>) {
         __filteredList.postValue(filteredList)
    }

}