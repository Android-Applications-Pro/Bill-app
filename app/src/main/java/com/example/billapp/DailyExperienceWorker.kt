import android.content.Context
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.billapp.viewModel.MainViewModel

class DailyExperienceWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params), ViewModelStoreOwner {

    override val viewModelStore = ViewModelStore()

    override suspend fun doWork(): Result {
        // Use the application context to get the ViewModel
        val viewModel = ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(applicationContext as android.app.Application))
            .get(MainViewModel::class.java)

        val userId = viewModel.getCurrentUserID()
        viewModel.dailyExperienceIncrease(userId)
        return Result.success()
    }
}
