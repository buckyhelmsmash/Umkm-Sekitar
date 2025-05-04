package com.example.umkm_sekitar.di

import com.example.umkm_sekitar.data.repository.AuthRepository
import com.example.umkm_sekitar.data.source.FirebaseDataSource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFirebaseDatabase(): FirebaseDatabase {
        return FirebaseDatabase.getInstance()
    }

    @Provides
    @Singleton
    fun provideFirebaseDataSource(storeRef: DatabaseReference): FirebaseDataSource {
        return FirebaseDataSource(storeRef)
    }

    @Provides
    @Singleton
    fun provideFirebaseStoreDatabase(firebaseDatabase: FirebaseDatabase): DatabaseReference {
        return firebaseDatabase.getReference("store")
    }

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @Provides
    @Singleton
    fun provideAuthRepository(auth: FirebaseAuth): AuthRepository {
        return AuthRepository(auth)
    }
}


