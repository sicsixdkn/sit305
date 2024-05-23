package com.sicsix.talecraft.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sicsix.talecraft.utility.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userPreferences: UserPreferences
) : ViewModel() {
    // LiveData to observe the user's settings
    val useLargerReaderFont = MutableLiveData(userPreferences.getUseLargerReaderFont())
    val useLocalLLM = MutableLiveData(userPreferences.getUseLocalLLM())

    /**
     * Sets the user's preference for using a larger reader font.
     *
     * @param useLargerReaderFont The user's preference for using a larger reader font.
     */
    fun setUseLargerReaderFont(useLargerReaderFont: Boolean) {
        userPreferences.setUseLargerReaderFont(useLargerReaderFont)
        this.useLargerReaderFont.postValue(useLargerReaderFont)
    }

    /**
     * Sets the user's preference for using a local LLM.
     *
     * @param useLocalLLM The user's preference for using a local LLM.
     */
    fun setUseLocalLLM(useLocalLLM: Boolean) {
        userPreferences.setUseLocalLLM(useLocalLLM)
        this.useLocalLLM.postValue(useLocalLLM)
    }
}