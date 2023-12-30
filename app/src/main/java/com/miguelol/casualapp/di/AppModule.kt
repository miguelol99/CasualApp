package com.miguelol.casualapp.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.miguelol.casualapp.data.repositories.AuthRepositoryImpl
import com.miguelol.casualapp.data.repositories.FriendRequestRepositoryImpl
import com.miguelol.casualapp.data.repositories.FriendsRepositoryImpl
import com.miguelol.casualapp.data.repositories.ImageRepositoryImpl
import com.miguelol.casualapp.data.repositories.PlanRepositoryImpl
import com.miguelol.casualapp.data.repositories.UserRepositoryImpl
import com.miguelol.casualapp.domain.repositories.AuthRepository
import com.miguelol.casualapp.domain.repositories.FriendRequestRepository
import com.miguelol.casualapp.domain.repositories.FriendsRepository
import com.miguelol.casualapp.domain.repositories.ImageRepository
import com.miguelol.casualapp.domain.repositories.PlanRepository
import com.miguelol.casualapp.domain.repositories.UserRepository
import com.miguelol.casualapp.domain.usecases.AcceptRequest
import com.miguelol.casualapp.domain.usecases.AddFriend
import com.miguelol.casualapp.domain.usecases.CreateRequest
import com.miguelol.casualapp.domain.usecases.DeclineRequest
import com.miguelol.casualapp.domain.usecases.DeleteFriend
import com.miguelol.casualapp.domain.usecases.FriendRequestUseCases
import com.miguelol.casualapp.domain.usecases.FriendUseCases
import com.miguelol.casualapp.domain.usecases.GetFriend
import com.miguelol.casualapp.domain.usecases.GetFriendRequest
import com.miguelol.casualapp.domain.usecases.GetFriendRequests
import com.miguelol.casualapp.domain.usecases.GetFriendState
import com.miguelol.casualapp.domain.usecases.GetFriends
import com.miguelol.casualapp.domain.usecases.GetUser
import com.miguelol.casualapp.domain.usecases.IsUsernameTaken
import com.miguelol.casualapp.domain.usecases.SearchUsers
import com.miguelol.casualapp.domain.usecases.UpdateUser
import com.miguelol.casualapp.domain.usecases.UserUseCases
import com.miguelol.casualapp.domain.usecases.auth.AuthUseCases
import com.miguelol.casualapp.domain.usecases.auth.GetCurrentUser
import com.miguelol.casualapp.domain.usecases.auth.LogIn
import com.miguelol.casualapp.domain.usecases.images.ImageUseCases
import com.miguelol.casualapp.domain.usecases.images.SaveImage
import com.miguelol.casualapp.domain.usecases.plans.CreatePlan
import com.miguelol.casualapp.domain.usecases.plans.FilterPlans
import com.miguelol.casualapp.domain.usecases.plans.GetAllPlans
import com.miguelol.casualapp.domain.usecases.plans.GetMyPlans
import com.miguelol.casualapp.domain.usecases.plans.PlanUseCases
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
object AppModule {

    //FIREBASE INSTANCES
    @Provides
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()
    @Provides
    fun provideFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()
    @Provides
    fun provideFirebaseStorage(): FirebaseStorage = FirebaseStorage.getInstance()

    //REPOSITORIES
    @Provides
    fun provideAuthRepository(impl: AuthRepositoryImpl): AuthRepository = impl
    @Provides
    fun provideUserRepository(impl: UserRepositoryImpl): UserRepository = impl
    @Provides
    fun provideFriendRepository(impl: FriendsRepositoryImpl): FriendsRepository = impl
    @Provides
    fun provideFriendRequestRepository(impl: FriendRequestRepositoryImpl): FriendRequestRepository = impl
    @Provides
    fun provideImageRepository(impl: ImageRepositoryImpl): ImageRepository = impl
    @Provides
    fun providePlanRepository(impl: PlanRepositoryImpl): PlanRepository = impl

    //USE CASES
    @Provides
    fun provideAuthUseCases(authRepository: AuthRepository) = AuthUseCases(
        getCurrentUser = GetCurrentUser(authRepository),
        logIn = LogIn(authRepository)
    )
    @Provides
    fun provideImageUseCases(imageRepo: ImageRepository) = ImageUseCases(
        saveImage = SaveImage(imageRepo)
    )

    @Provides
    fun provideUsersUseCases(
        userRepo: UserRepository,
        imageUseCases: ImageUseCases
    ) : UserUseCases =
        UserUseCases(
            getUser = GetUser(userRepo),
            isUsernameTaken = IsUsernameTaken(userRepo),
            updateUser = UpdateUser(userRepo, imageUseCases),
            searchUsers = SearchUsers(userRepo)
        )

    @Provides
    fun provideFriendRequestUseCases(
        requestRepo: FriendRequestRepository,
        friendUseCases: FriendUseCases,
        userUseCases: UserUseCases
    ) =
        FriendRequestUseCases(
            getFriendRequests = GetFriendRequests(requestRepo),
            getFriendRequest = GetFriendRequest(requestRepo),
            acceptRequest = AcceptRequest(requestRepo, friendUseCases),
            declineRequest = DeclineRequest(requestRepo),
            createRequest = CreateRequest(requestRepo, friendUseCases, userUseCases),
            getFriendState = GetFriendState(requestRepo, friendUseCases)
        )

    @Provides
    fun provideFriendUseCases(
        friendsRepo: FriendsRepository,
        userUseCases: UserUseCases
    ) = FriendUseCases(
        getFriends = GetFriends(friendsRepo),
        getFriend = GetFriend(friendsRepo),
        addFriend = AddFriend(friendsRepo, userUseCases),
        deleteFriend = DeleteFriend(friendsRepo)
    )

    @Provides
    fun providePlanUseCases(planRepository: PlanRepository, imageRepository: ImageRepository) = PlanUseCases(
        getAllPlans = GetAllPlans(planRepository),
        getMyPlans = GetMyPlans(planRepository),
        filterPlans = FilterPlans(),
        createPlan = CreatePlan(planRepository, imageRepository)
    )
}