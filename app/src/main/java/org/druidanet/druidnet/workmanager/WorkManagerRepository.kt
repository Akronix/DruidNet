package org.druidanet.druidnet.workmanager

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.WorkRequest
import org.druidanet.druidnet.workers.DatabaseUpdateWorker
import java.util.concurrent.TimeUnit

class WorkManagerRepository(context: Context) {

    private val workManager = WorkManager.getInstance(context)

    // This LiveData can be used to update UI based on worker's progress/success/failure.
    val databaseUpdateWorkInfo: LiveData<List<WorkInfo>> =
        workManager.getWorkInfosByTagLiveData("DatabaseUpdateWorkerTag")

    fun startDatabaseUpdateWork() {
        Log.i("WorkManagerRepository", "Enqueueing DatabaseUpdateWorker to check and update database.")
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val databaseUpdateWorkRequest =
            OneTimeWorkRequestBuilder<DatabaseUpdateWorker>()
                .setConstraints(constraints)
                // Optionally, you can add a tag for easier observation or cancellation
                .addTag("DatabaseUpdateWorkerTag")
                .setBackoffCriteria(
                    BackoffPolicy.LINEAR,
                    WorkRequest.MIN_BACKOFF_MILLIS,
                    TimeUnit.MILLISECONDS)
                .build()

        workManager.enqueueUniqueWork(
            "DatabaseUpdateWorker", // Unique name for the work
            ExistingWorkPolicy.REPLACE, // Policy for existing work with the same name
            databaseUpdateWorkRequest
        )

    }

}