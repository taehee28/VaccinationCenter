package com.thk.vaccinationcenter.ui.splash

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.Transformation
import android.widget.ProgressBar
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.thk.vaccinationcenter.R
import com.thk.vaccinationcenter.data.utils.RequestState
import com.thk.vaccinationcenter.databinding.ActivitySplashBinding
import com.thk.vaccinationcenter.utils.logd
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private val viewModel: SplashViewModel by viewModels()

    private lateinit var animator: ObjectAnimator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycleScope.launch {
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

                        // progress가 80%일 때 일시정지
                        addUpdateListener { valueAnimator ->
                            if (valueAnimator.animatedValue as Int >= 80) {
                                pause()
                            }
                        }
                    }
                }.collect { state ->
                    when (state) {
                        RequestState.Success -> {
                            // 1. 80%가 되기 전에 Success 도착 -> 80%에서 일시정지하는 Listener 제거
                            // 2. 80%에서 멈춘 후 Success 도착 -> 애니메이션 재개
                            animator.removeAllUpdateListeners()
                            if (animator.isPaused) animator.resume()
                        }
                        RequestState.NetworkError -> {
                            // 이전에 저장한 데이터가
                            // 1. 있으면 -> 업데이트 필요 안내 다이얼로그 표시 후 Map으로 이동
                            // 2. 없으면 -> 데이터 다운로드 필요 안내 다이얼로 표시 후 앱 종료
                        }
                        RequestState.ServerError -> {
                            // 서버 통신 이슈 안내 다이얼로그 표시 후 앱 종료 
                        }
                    }
                }
        }
    }
}