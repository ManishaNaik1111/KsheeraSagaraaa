package com.ksheera.sagaraaa.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntry)

    @Query("SELECT * FROM users WHERE username = :username AND password = :password LIMIT 1")
    suspend fun loginUser(username: String, password: String): UserEntry?

    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    suspend fun getUserByUsername(username: String): UserEntry?

    @Query("SELECT * FROM users LIMIT 1")
    fun getCurrentUser(): Flow<UserEntry?>

    @Query("UPDATE users SET password = :newPassword WHERE username = :username")
    suspend fun updatePassword(username: String, newPassword: String)

    @Query("DELETE FROM users")
    suspend fun deleteAllUsers()
}