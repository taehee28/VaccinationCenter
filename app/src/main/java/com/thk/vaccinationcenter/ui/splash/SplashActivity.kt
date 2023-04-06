package com.thk.vaccinationcenter.ui.splash

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

    private var loadingBarJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /*lifecycleScope.launch {
            viewModel.cacheCenterData()
                .onStart {
                    loadingBarJob = launch {
                        while (binding.loadingBar.progress <= 80) {
                            binding.loadingBar.progress += 1
                            delay(25)
                        }
                    }
                }.collect { state ->

                }
        }*/

        ProgressBarAnimation(binding.loadingBar, 0f, 80f)
            .apply { duration = 2000 }
            .also { binding.loadingBar.startAnimation(it) }
    }
}

class ProgressBarAnimation(
    private val progressBar: ProgressBar,
    private val from: Float,
    private val to: Float
) : Animation() {
    override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
        super.applyTransformation(interpolatedTime, t)

        val value = from + (to - from) * interpolatedTime
        progressBar.progress = value.toInt()
    }
}