package com.carlyu.pmxv.local.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.carlyu.pmxv.local.room.entity.AccountEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AccountDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAccount(account: AccountEntity): Long

    @Update
    suspend fun updateAccount(account: AccountEntity)

    @Delete
    suspend fun deleteAccount(account: AccountEntity)

    @Query("SELECT * FROM accounts WHERE id = :accountId")
    suspend fun getAccountById(accountId: Long): AccountEntity?

    @Query("SELECT * FROM accounts WHERE is_active = 1 LIMIT 1")
    fun getActiveAccountFlow(): Flow<AccountEntity?>

    @Query("SELECT * FROM accounts WHERE is_active = 1 LIMIT 1")
    suspend fun getActiveAccount(): AccountEntity?

    @Query("SELECT * FROM accounts ORDER BY name ASC")
    fun getAllAccountsFlow(): Flow<List<AccountEntity>>

    @Query("SELECT COUNT(*) FROM accounts")
    suspend fun getAccountsCount(): Int

    @Query("UPDATE accounts SET is_active = 0")
    suspend fun clearAllActiveFlags()

    @Transaction
    suspend fun setActiveAccount(accountId: Long) {
        clearAllActiveFlags()
        val account = getAccountById(accountId)
        account?.let {
            it.isActive = true
            it.lastLoginTimestamp = System.currentTimeMillis() // 更新登录时间
            updateAccount(it)
        }
    }

    @Transaction
    suspend fun insertAndSetAccountActive(account: AccountEntity): Long {
        clearAllActiveFlags()
        val newAccount = account.copy(
            isActive = true,
            lastLoginTimestamp = System.currentTimeMillis()
        )
        return insertAccount(newAccount)
    }
}