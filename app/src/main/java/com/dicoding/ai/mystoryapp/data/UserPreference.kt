package com.dicoding.ai.mystoryapp.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.dicoding.ai.mystoryapp.data.model.UserModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserPreference private constructor(private val dataStore: DataStore<Preferences>) {


    fun getUser(): Flow<UserModel> {
        return dataStore.data.map { pref ->
            UserModel(
                pref[ID_KEY] ?: "",
                pref[NAME_KEY] ?: "",
                pref[TOKEN_KEY] ?: "",
                pref[STATE_KEY] ?: false

            )
        }
    }

    suspend fun saveUser(user: UserModel) {
        dataStore.edit { pref ->
            pref[ID_KEY] = user.userId
            pref[NAME_KEY] = user.name
            pref[TOKEN_KEY] = user.token
            pref[STATE_KEY] = true
        }
    }

    suspend fun login() {
        dataStore.edit {
            it[STATE_KEY] = true
        }
    }

    suspend fun logout() {
        dataStore.edit {
            it[STATE_KEY] = false
        }
    }


    companion object {
        private var INSTANCE: UserPreference? = null

        private val NAME_KEY = stringPreferencesKey("name")
        private val ID_KEY = stringPreferencesKey("id")
        private val TOKEN_KEY = stringPreferencesKey("token")
        private val STATE_KEY = booleanPreferencesKey("state")

        fun getInstance(dataStore: DataStore<Preferences>): UserPreference {
            return INSTANCE ?: synchronized(this) {
                val instance = UserPreference(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}