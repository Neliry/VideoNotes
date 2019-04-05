package com.neliry.banancheg.videonotes.activities

import android.annotation.SuppressLint
import android.app.Application
import android.view.View
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.TypedValue
import android.view.Gravity
import android.widget.*
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.*
import kotlin.math.roundToInt
import android.view.ViewGroup
import com.neliry.banancheg.videonotes.R
import com.neliry.banancheg.videonotes.models.BaseItem
import com.neliry.banancheg.videonotes.models.Conspectus
import com.neliry.banancheg.videonotes.models.Page
import com.neliry.banancheg.videonotes.repositories.FirebaseDatabaseRepository
import com.neliry.banancheg.videonotes.repositories.PageRepository
import com.neliry.banancheg.videonotes.utils.OnViewClickListener
import com.neliry.banancheg.videonotes.viewmodels.BaseNavigationDrawerViewModel
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer

class VideoViewModel (application: Application):BaseNavigationDrawerViewModel(application), OnViewClickListener {

    var isSeekBarInTouch = false
    var isVideoOnFocus = false
    var wait = 0
    var isPause = true
    var videoDuration = 0f
    var horizontalMax = -10
    var verticalPosition: Short = 0
    lateinit var youTubePlayer: YouTubePlayer
    lateinit var allNotes: MutableLiveData<List<Page>>
    private lateinit var currentClickedPage: Page

    override fun onViewClicked(view: View?, baseItem: BaseItem?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    init{
        @Suppress("UNCHECKED_CAST")
        repository = PageRepository() as FirebaseDatabaseRepository<BaseItem>

//        val note1 = Note ("Note 1", 10)
//        val note2 = Note ("Note 2", 100)
//        val note3 = Note ("Note 3", 200)
//        val note4 = Note ("Note 4", 300)
//        val note5 = Note ("Note 5", 400)
//        val note6 = Note ("Note 6", 500)
//        val notesList: List<Note> = listOf(note1, note2, note3, note4, note5, note6)
//        allNotes.value = notesList

    }

//    fun getAllNotes(): LiveData<List<Note>> {
//        return allNotes
//    }

    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    fun showPlayerUI(custom_ui: RelativeLayout, video_progressBar : ProgressBar, context: Context){
        if(!isVideoOnFocus){
            custom_ui.visibility = View.VISIBLE
            video_progressBar.visibility = View.GONE
            wait = 0
            waitBeforeCloseAsync(custom_ui, video_progressBar, context)
        }
    }

    private fun waitBeforeCloseAsync(custom_ui: RelativeLayout, video_progressBar : ProgressBar, context: Context)= GlobalScope.async { // this: CoroutineScope
        launch {
            while (wait < 4){
                if(!isSeekBarInTouch)
                    wait++
                delay(1000)
            }
            isVideoOnFocus = false
            (context as Activity).runOnUiThread {
                custom_ui.visibility = View.GONE
                video_progressBar.visibility = View.VISIBLE
            }
        }
    }

    fun setPause(pause_btn: ImageButton){
        if(!isPause){
            youTubePlayer.pause()
            pause_btn.setImageResource(R.drawable.ic_play_arrow)
            isPause = true
            wait = 0

        }
        else {
            youTubePlayer.play()
            pause_btn.setImageResource(R.drawable.ic_pause)
            isPause = false
            wait = 0
        }
    }

    fun setVideoDuration(video_seekBar: SeekBar, video_progressBar: ProgressBar, custom_ui: RelativeLayout, video_duration: TextView, duration: Float, marks_rl: RelativeLayout, notes_list_recycler_view: RecyclerView, pause_btn: ImageButton , context: Context){
        if(videoDuration == 0f){
            youTubePlayer.pause()
            video_seekBar.max = duration.toInt()
            video_progressBar.max = duration.toInt()
            convertTime (duration)
            video_duration.text = convertTime(duration)
            videoDuration = duration
            createMark(marks_rl, notes_list_recycler_view, pause_btn)
            showPlayerUI(custom_ui, video_progressBar, context)
        }

    }

    fun setCurrentSecond(video_seekBar : SeekBar, video_progressBar : ProgressBar, second : Float){
        if(!isSeekBarInTouch){
            video_seekBar.progress = second.toInt()
        }
        video_progressBar.progress = second.toInt()
//        setMark (tracker.currentSecond.toInt(), tracker.videoDuration, width)
    }

    internal fun convertTime (time: Float): String{
        val ours = (time/3600).toInt()
        var minutes = (time/60).toInt()
        val seconds = time.toInt() - 60*minutes
        minutes -=60*ours
        var string = ""
        if(ours != 0) {
            string = "$ours:"
            if(minutes < 10)
                string += 0
        }
        string = "$string$minutes:"
        if(seconds < 10)
            string += 0
        string = "$string$seconds"
        return string
    }

    fun load()= GlobalScope.async { // this: CoroutineScope
        launch {
            delay(100)
            isSeekBarInTouch = false
        }
    }

    fun seekBarChange(fromUser : Boolean){
        if(fromUser)
            wait = 0
    }

    fun setMark(time: Int, width: Int, timeMark: LinearLayout, markLine: ImageView) {

        val newWidth = width - dpToPx(12f, getApplication())
        val pxInSec = newWidth/videoDuration
        val params = timeMark.layoutParams as RelativeLayout.LayoutParams
        params.leftMargin = (pxInSec*time).roundToInt() - dpToPx(2.3f, getApplication())
        if(horizontalMax>=params.leftMargin){

            when ( verticalPosition){
                1.toShort() -> {
                    markLine.layoutParams.height = dpToPx(32f, getApplication())
                    verticalPosition = 3
                    sendViewToBack(timeMark)
                    return
                }
                2.toShort() -> {
                    markLine.layoutParams.height = dpToPx(2f, getApplication())
                    verticalPosition = 1
                    timeMark.bringToFront()
                    return
                }
                3.toShort() -> {
                    markLine.layoutParams.height = dpToPx(16f, getApplication())
                    verticalPosition = 2
                    return
                }
                else -> {
                    return
                }
            }
        }
        else verticalPosition = 2
        timeMark.layoutParams = params
        horizontalMax = params.leftMargin + dpToPx(16f, getApplication())
    }

    fun createMark (marks_rl: RelativeLayout, recyclerView: RecyclerView, pause_btn: ImageButton){
        if(videoDuration != 0f) {
            for (i in 0 until allNotes.value!!.size){
                val markLayout = LinearLayout(getApplication())
                val markLayoutParams = RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
                )
                markLayout.layoutParams = markLayoutParams
                markLayout.orientation = LinearLayout.VERTICAL
                markLayout.gravity = Gravity.CENTER_HORIZONTAL
                val markLine = ImageView(getApplication())
                val markLineParams =
                    RelativeLayout.LayoutParams(dpToPx(2f, getApplication()), dpToPx(18f, getApplication()))
                markLine.layoutParams = markLineParams
                markLine.setBackgroundResource(R.color.accent_lite)
                val markButton = ImageButton(getApplication())
                val markButtonParams =
                    RelativeLayout.LayoutParams(dpToPx(16f, getApplication()), dpToPx(16f, getApplication()))
                markButton.layoutParams = markButtonParams
                markButton.setBackgroundResource(R.drawable.note_mark_btn)
                markButton.setOnClickListener {
                    recyclerView.scrollToPosition(i)
                    youTubePlayer.seekTo(allNotes.value!![i].time!!.toFloat())
                    isPause = true
                    setPause (pause_btn)
                }

                markLayout.addView(markLine)
                markLayout.addView(markButton)
                marks_rl.addView(markLayout)

                setMark(allNotes.value!![i].time!!, marks_rl.width, markLayout, markLine)
            }

        }
    }

    internal fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.resources.displayMetrics).toInt()
    }

    fun sendViewToBack(child: View) {
        val parent = child.parent as ViewGroup
        if (null != parent) {
            parent.removeView(child)
            parent.addView(child, 0)
        }
    }


    fun parseIntent(intent: Intent, supportActionBar: androidx.appcompat.app.ActionBar): String?{
        if (intent.getSerializableExtra("currentConspectus") !=null) {
            val conspectus: Conspectus = intent.getSerializableExtra("currentConspectus") as Conspectus
            repository.setDatabaseReference("pages", conspectus.id.toString())
            supportActionBar.title = conspectus.name
            conspectus.video_url
            return conspectus.video_url
        }
        return  ""
    }
}