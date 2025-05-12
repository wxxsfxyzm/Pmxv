package com.carlyu.pmxv.local.room.repository

import com.carlyu.pmxv.local.room.dao.AccountDao
import com.carlyu.pmxv.local.room.entity.AccountEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AccountRepository @Inject constructor(
    private val accountDao: AccountDao
) {

    suspend fun addAccount(account: AccountEntity): Long {
        return if (accountDao.getAccountsCount() == 0) {
            // 如果是第一个账户，自动设为激活
            accountDao.insertAndSetAccountActive(account)
        } else {
            accountDao.insertAccount(account)
        }
    }

    suspend fun updateAccount(account: AccountEntity) {
        accountDao.updateAccount(account)
    }

    suspend fun deleteAccount(account: AccountEntity) {
        accountDao.deleteAccount(account)
    }

    suspend fun getAccountById(id: Long): AccountEntity? {
        return accountDao.getAccountById(id)
    }

    fun getActiveAccountFlow(): Flow<AccountEntity?> = accountDao.getActiveAccountFlow()

    suspend fun getActiveAccount(): AccountEntity? = accountDao.getActiveAccount()

    fun getAllAccountsFlow(): Flow<List<AccountEntity>> = accountDao.getAllAccountsFlow()

    suspend fun setActiveAccount(accountId: Long) {
        accountDao.setActiveAccount(accountId)
    }

    suspend fun hasAccounts(): Boolean = accountDao.getAccountsCount() > 0
}