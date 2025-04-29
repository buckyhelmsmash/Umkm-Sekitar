package com.example.umkm_sekitar.di
//
//import com.example.umkm_sekitar.data.source.FirebaseDataSource
//import com.example.umkm_sekitar.data.repository.TokoRepository
//import dagger.Module
//import dagger.Provides
//import dagger.hilt.InstallIn
//import dagger.hilt.components.SingletonComponent
//import javax.inject.Singleton
//
//@Module
//@InstallIn(SingletonComponent::class)
//object AppModule {
//
//    @Provides
//    @Singleton
//    fun provideFirebaseDataSource(): FirebaseDataSource {
//        return FirebaseDataSource()
//    }
//
//    @Provides
//    @Singleton
//    fun provideTokoRepository(firebaseDataSource: FirebaseDataSource): TokoRepository {
//        return TokoRepository(firebaseDataSource)
//    }
//}
