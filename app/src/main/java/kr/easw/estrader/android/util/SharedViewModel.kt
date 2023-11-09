package kr.easw.estrader.android.util

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {
    val districtLiveData = MutableLiveData<String>()
}