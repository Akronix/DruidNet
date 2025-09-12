package org.druidanet.druidnet.workmanager

import android.content.Context
import android.util.Log
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import org.druidanet.druidnet.workers.DatabaseUpdateWorker

class WorkManagerRepository(context: Context) {

    private val workManager = WorkManager.getInstance(context)

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
                .build()

        workManager.enqueueUniqueWork(
            "DatabaseUpdateWorker", // Unique name for the work
            ExistingWorkPolicy.REPLACE, // Policy for existing work with the same name
            databaseUpdateWorkRequest
        )
        // You can observe the work status using:
        // workManager.getWorkInfoForUniqueWorkLiveData("DatabaseUpdateWorker")
        // or for a specific ID: workManager.getWorkInfoByIdLiveData(databaseUpdateWorkRequest.id)
        // This LiveData can be used to update UI based on worker's progress/success/failure.

    }

}