package com.miguelol.casualapp.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.miguelol.casualapp.data.repositories.AuthRepositoryImpl
import com.miguelol.casualapp.data.repositories.FriendRequestRepositoryImpl
import com.miguelol.casualapp.data.repositories.FriendsRepositoryImpl
import com.miguelol.casualapp.data.repositories.ImageRepositoryImpl
import com.miguelol.casualapp.data.repositories.PlanRepositoryImpl
import com.miguelol.casualapp.data.repositories.PlanRequestRepositoryImpl
import com.miguelol.casualapp.data.repositories.UserRepositoryImpl
import com.miguelol.casualapp.domain.repositories.AuthRepository
import com.miguelol.casualapp.domain.repositories.FriendRequestRepository
import com.miguelol.casualapp.domain.repositories.FriendsRepository
import com.miguelol.casualapp.domain.repositories.ImageRepository
import com.miguelol.casualapp.domain.repositories.PlanRepository
import com.miguelol.casualapp.domain.repositories.PlanRequestRepository
import com.miguelol.casualapp.domain.repositories.UserRepository
import com.miguelol.casualapp.domain.usecases.planRequests.AcceptPlanRequest
import com.miguelol.casualapp.domain.usecases.friendRequests.AcceptRequest
import com.miguelol.casualapp.domain.usecases.friends.AddFriend
import com.miguelol.casualapp.domain.usecases.planRequests.CreatePlanRequest
import com.miguelol.casualapp.domain.usecases.friendRequests.CreateRequest
import com.miguelol.casualapp.domain.usecases.friendRequests.DeclineRequest
import com.miguelol.casualapp.domain.usecases.friends.DeleteFriend
import com.miguelol.casualapp.domain.usecases.planRequests.DeclinePlanRequest
import com.miguelol.casualapp.domain.usecases.friendRequests.FriendRequestUseCases
import com.miguelol.casualapp.domain.usecases.friends.FriendUseCases
import com.miguelol.casualapp.domain.usecases.friends.GetFriend
import com.miguelol.casualapp.domain.usecases.friendRequests.GetFriendRequests
import com.miguelol.casualapp.domain.usecases.friendRequests.GetFriendState
import com.miguelol.casualapp.domain.usecases.friends.GetFriends
import com.miguelol.casualapp.domain.usecases.planRequests.GetPlanRequests
import com.miguelol.casualapp.domain.usecases.planRequests.GetRequestState
import com.miguelol.casualapp.domain.usecases.users.GetUser
import com.miguelol.casualapp.domain.usecases.users.IsUsernameTaken
import com.miguelol.casualapp.domain.usecases.planRequests.PlanRequestUseCases
import com.miguelol.casualapp.domain.usecases.users.SearchUsers
import com.miguelol.casualapp.domain.usecases.users.UpdateUser
import com.miguelol.casualapp.domain.usecases.users.UserUseCases
import com.miguelol.casualapp.domain.usecases.auth.AuthUseCases
import com.miguelol.casualapp.domain.usecases.auth.GetCurrentUser
import com.miguelol.casualapp.domain.usecases.auth.LogIn
import com.miguelol.casualapp.domain.usecases.auth.SignOut
import com.miguelol.casualapp.domain.usecases.images.ImageUseCases
import com.miguelol.casualapp.domain.usecases.images.SaveImage
import com.miguelol.casualapp.domain.usecases.plans.AddParticipant
import com.miguelol.casualapp.domain.usecases.plans.CreatePlan
import com.miguelol.casualapp.domain.usecases.plans.DeleteParticipant
import com.miguelol.casualapp.domain.usecases.plans.DeletePlan
import com.miguelol.casualapp.domain.usecases.plans.FilterPlans
import com.miguelol.casualapp.domain.usecases.plans.GetChat
import com.miguelol.casualapp.domain.usecases.plans.GetMyPlans
import com.miguelol.casualapp.domain.usecases.plans.GetParticipants
import com.miguelol.casualapp.domain.usecases.plans.GetPlan
import com.miguelol.casualapp.domain.usecases.plans.GetPlans
import com.miguelol.casualapp.domain.usecases.plans.PlanUseCases
import com.miguelol.casualapp.domain.usecases.plans.SendMessage
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
    @Provides
    fun providePlanRequestRepository(impl: PlanRequestRepositoryImpl): PlanRequestRepository = impl

    //USE CASES
    @Provides
    fun provideAuthUseCases(authRepository: AuthRepository) = AuthUseCases(
        getCurrentUser = GetCurrentUser(authRepository),
        logIn = LogIn(authRepository),
        signOut = SignOut(authRepository)
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
    fun providePlanUseCases(
        planRepository: PlanRepository,
        userUseCases: UserUseCases,
        friendUseCases: FriendUseCases,
        imageUseCases: ImageUseCases
    ) = PlanUseCases(
        getPlans = GetPlans(planRepository),
        getPlan = GetPlan(planRepository),
        getParticipants = GetParticipants(planRepository),
        getMyPlans = GetMyPlans(planRepository),
        filterPlans = FilterPlans(),
        createPlan = CreatePlan(planRepository, userUseCases, friendUseCases, imageUseCases),
        deletePlan = DeletePlan(planRepository),
        deleteParticipant = DeleteParticipant(planRepository),
        addParticipant = AddParticipant(planRepository, userUseCases),
        getChat = GetChat(planRepository),
        sendMessage = SendMessage(planRepository, userUseCases)
    )

    @Provides
    fun providePlanRequestsUseCases(
        requestRepository: PlanRequestRepository,
        planUseCases: PlanUseCases,
        userUseCases: UserUseCases
    ) = PlanRequestUseCases(
        getRequests = GetPlanRequests(requestRepository),
        getRequestState = GetRequestState(requestRepository, planUseCases),
        createRequest = CreatePlanRequest(requestRepository, userUseCases, planUseCases ),
        declineRequest = DeclinePlanRequest(requestRepository),
        acceptRequest = AcceptPlanRequest(requestRepository, planUseCases)
    )
}