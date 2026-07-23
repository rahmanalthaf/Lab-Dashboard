package com.labdashboard.app

import android.app.Application
import com.labdashboard.app.data.AppDatabase
import com.labdashboard.app.data.seedIfEmpty
import com.labdashboard.app.repository.LabRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LabApplication : Application() {
    lateinit var repository: LabRepository
        private set

    override fun onCreate() {
        super.onCreate()
        val database = AppDatabase.getInstance(this)
        repository = LabRepository(database)

        CoroutineScope(Dispatchers.IO).launch {
            seedIfEmpty(database)
        }
    }
}
