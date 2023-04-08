package com.thk.vaccinationcenter.di

import android.content.Context
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.qualifiers.ActivityContext

@Module
@InstallIn(ActivityComponent::class)
object MapModule {
    @Provides
    fun provideFusedLocationProviderClient(
        @ActivityContext context: Context
    ): FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)
}