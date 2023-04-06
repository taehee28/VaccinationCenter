package com.thk.vaccinationcenter.ui.splash

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

    private lateinit var progressBarAnimation: ProgressBarAnimation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycleScope.launch {
            viewModel.cacheCenterData()
                .onStart {
                    progressBarAnimation = ProgressBarAnimation(binding.loadingBar, 0f, 80f).apply { duration = 2000 }
                    binding.loadingBar.startAnimation(progressBarAnimation)
                }.collect { state ->
                    logd(">> state = $state")
                    if (state is RequestState.Success) {
                        logd(">> startOffset = ${binding.loadingBar.animation.startOffset}")
                        logd(">> compute = ${binding.loadingBar.animation.computeDurationHint()}")
                        binding.loadingBar.animation.cancel()

                        progressBarAnimation.to = 100f
                        if (binding.loadingBar.animation == null) {
                            progressBarAnimation.from = binding.loadingBar.progress.toFloat()
                            progressBarAnimation.duration = 400
                            binding.loadingBar.startAnimation(progressBarAnimation)
                        }
                    }
                }
        }

        /*ValueAnimator.ofFloat(0f, 100f).apply {
            duration = 2000
            start()

            addUpdateListener { updatedAnimation ->
                binding.loadingBar.progress = (updatedAnimation.animatedValue as Float).toInt()
            }
        }*/




    }
}

class ProgressBarAnimation(
    private val progressBar: ProgressBar,
    var from: Float,
    var to: Float
) : Animation() {
    override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
        super.applyTransformation(interpolatedTime, t)

        val value = from + (to - from) * interpolatedTime
        progressBar.progress = value.toInt()
    }
}