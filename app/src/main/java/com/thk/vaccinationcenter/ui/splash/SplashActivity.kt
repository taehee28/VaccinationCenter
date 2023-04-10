package com.thk.vaccinationcenter.ui.splash

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.animation.ValueAnimator.AnimatorUpdateListener
import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.Transformation
import android.widget.ProgressBar
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.animation.addListener
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.thk.vaccinationcenter.R
import com.thk.vaccinationcenter.data.utils.RequestState
import com.thk.vaccinationcenter.databinding.ActivitySplashBinding
import com.thk.vaccinationcenter.ui.map.MapActivity
import com.thk.vaccinationcenter.utils.logd
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlin.system.exitProcess

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private val viewModel: SplashViewModel by viewModels()

    private lateinit var animator: ObjectAnimator
    private val updateListener = AnimatorUpdateListener { valueAnimator ->
        // progress가 80%일 때 일시정지
        if (valueAnimator.animatedValue as Int >= 80) {
            valueAnimator.pause()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        requestCacheData()
    }

    /**
     * 데이터 캐싱 요청 
     */
    private fun requestCacheData() = lifecycleScope.launch {
        viewModel.cacheCenterData()
            .onStart {
                // 시작 시 ProgressBar 애니메이션 시작
                animator = ObjectAnimator.ofInt(
                    binding.loadingBar,
                    "progress",
                    0,
                    100
                ).apply {
                    duration = 2000
                    start()

                    // progressBar 애니메이션 멈추는 listener
                    addUpdateListener(updateListener)
                    // progressBar의 progress 값을 textView에 표시하는 listener
                    addUpdateListener { valueAnimator ->
                        binding.tvLoadingValue.text = (valueAnimator.animatedValue as Int).toString()
                    }
                }
            }.collect { state ->
                logd(">> state = $state")
                handleRequestState(state)
            }
    }

    /**
     * 데이터 캐싱 결과를 처리
     */
    private suspend fun handleRequestState(state: RequestState) {
        when (state) {
            RequestState.Success -> {
                // 1. 80%가 되기 전에 Success 도착 -> 80%에서 일시정지하는 Listener 제거
                // 2. 80%에서 멈춘 후 Success 도착 -> 애니메이션 재개
                with(animator) {
                    addListener(onEnd = { moveToMap() })
                    removeUpdateListener(updateListener)
                    if (isPaused) resume()
                }
            }
            RequestState.NetworkError -> {
                // 이전에 저장한 데이터가
                // 1. 있으면 -> 업데이트 필요 안내 다이얼로그 표시 후 Map으로 이동
                // 2. 없으면 -> 데이터 다운로드 필요 안내 다이얼로 표시 후 앱 종료
                val message = if (viewModel.isCachingCompleted()) {
                    R.string.dialog_message_network_error_need_update
                } else {
                    R.string.dialog_message_network_error_no_data
                }

                val onClick = if (viewModel.isCachingCompleted()) {
                    { moveToMap() }
                } else {
                    { exitApp() }
                }

                animator.pause()
                getDialogBuilder()
                    .setTitle(R.string.dialog_title_network_error)
                    .setMessage(message)
                    .setPositiveButton(R.string.dialog_button_confirm) { _, _ ->
                        onClick()
                    }
                    .show()
            }
            RequestState.ServerError -> {
                // 서버 통신 이슈 안내 다이얼로그 표시 후 앱 종료
                animator.pause()
                getDialogBuilder()
                    .setTitle(R.string.dialog_title_server_error)
                    .setMessage(R.string.dialog_message_server_error)
                    .setPositiveButton(R.string.dialog_button_confirm) { _, _ -> exitApp() }
                    .show()
            }
        }
    }

    /**
     * 공통으로 필요한 옵션을 설정한 AlertDialogBuiler를 리턴
     */
    private fun getDialogBuilder() = AlertDialog
        .Builder(this@SplashActivity)
        .setCancelable(false)

    /**
     * [MapActivity]로 이동
     */
    private fun moveToMap() {
        MapActivity.getIntent(this@SplashActivity).also { startActivity(it) }
        finish()
    }

    /**
     * 앱 종료
     */
    private fun exitApp() {
        finishAffinity()
        System.runFinalization()
        exitProcess(0)
    }
}