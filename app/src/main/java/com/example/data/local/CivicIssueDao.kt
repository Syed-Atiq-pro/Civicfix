package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) for local Room caching of civic issues.
 * Provides reactive Flow streams, async modifications, and offline synchronization queries.
 */
@Dao
interface CivicIssueDao {
  @Query("SELECT * FROM civic_issues ORDER BY createdAt DESC")
  fun getAllIssues(): Flow<List<CivicIssueEntity>>

  @Query("SELECT * FROM civic_issues WHERE category = :category ORDER BY createdAt DESC")
  fun getIssuesByCategory(category: String): Flow<List<CivicIssueEntity>>

  @Query("SELECT * FROM civic_issues WHERE status = :status ORDER BY createdAt DESC")
  fun getIssuesByStatus(status: String): Flow<List<CivicIssueEntity>>

  @Query("SELECT * FROM civic_issues WHERE id = :issueId LIMIT 1")
  suspend fun getIssueById(issueId: String): CivicIssueEntity?

  @Query("SELECT * FROM civic_issues WHERE id = :issueId LIMIT 1")
  fun getIssueByIdFlow(issueId: String): Flow<CivicIssueEntity?>

  @Query("SELECT * FROM civic_issues WHERE title LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%' OR locationName LIKE '%' || :query || '%'")
  fun searchIssues(query: String): Flow<List<CivicIssueEntity>>

  @Query("SELECT COUNT(*) FROM civic_issues")
  fun getIssuesCount(): Flow<Int>

  @Query("SELECT * FROM civic_issues WHERE isSynced = 0")
  suspend fun getUnsyncedIssues(): List<CivicIssueEntity>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertIssue(issue: CivicIssueEntity)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(issues: List<CivicIssueEntity>)

  @Update
  suspend fun updateIssue(issue: CivicIssueEntity)

  @Query("UPDATE civic_issues SET status = :newStatus WHERE id = :issueId")
  suspend fun updateStatus(issueId: String, newStatus: String)

  @Query("UPDATE civic_issues SET upvotes = upvotes + 1 WHERE id = :issueId")
  suspend fun incrementUpvote(issueId: String)

  @Query("UPDATE civic_issues SET isSynced = 1 WHERE id = :issueId")
  suspend fun markAsSynced(issueId: String)

  @Query("DELETE FROM civic_issues WHERE id = :issueId")
  suspend fun deleteIssue(issueId: String)

  @Delete
  suspend fun delete(issue: CivicIssueEntity)

  @Query("DELETE FROM civic_issues")
  suspend fun clearAll()
}

