package com.example.data.repository

import android.content.Context
import com.example.data.local.CivicDatabase
import com.example.data.local.CivicIssueEntity
import com.example.data.model.CivicIssueItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

/**
 * Repository pattern implementation for CivicFix issues.
 * Mediates between Room local database cache, offline queues, and UI StateFlows.
 */
class CivicRepository(context: Context) {
  private val dao = CivicDatabase.getDatabase(context).civicIssueDao()

  val allIssues: Flow<List<CivicIssueItem>> = dao.getAllIssues().map { entities ->
    entities.map { it.toModel() }
  }

  val totalIssuesCount: Flow<Int> = dao.getIssuesCount()

  fun getIssuesByCategory(category: String): Flow<List<CivicIssueItem>> {
    return if (category == "All") {
      allIssues
    } else {
      dao.getIssuesByCategory(category).map { entities -> entities.map { it.toModel() } }
    }
  }

  fun getIssuesByStatus(status: String): Flow<List<CivicIssueItem>> {
    return dao.getIssuesByStatus(status).map { entities -> entities.map { it.toModel() } }
  }

  suspend fun getIssueById(issueId: String): CivicIssueItem? {
    return dao.getIssueById(issueId)?.toModel()
  }

  fun getIssueByIdFlow(issueId: String): Flow<CivicIssueItem?> {
    return dao.getIssueByIdFlow(issueId).map { it?.toModel() }
  }

  fun searchIssues(query: String): Flow<List<CivicIssueItem>> {
    return dao.searchIssues(query).map { entities -> entities.map { it.toModel() } }
  }

  suspend fun insertIssue(issue: CivicIssueItem, isSynced: Boolean = true) {
    dao.insertIssue(CivicIssueEntity.fromModel(issue, isSynced))
  }

  suspend fun insertAll(issues: List<CivicIssueItem>) {
    dao.insertAll(issues.map { CivicIssueEntity.fromModel(it) })
  }

  suspend fun updateIssue(issue: CivicIssueItem) {
    dao.updateIssue(CivicIssueEntity.fromModel(issue))
  }

  suspend fun updateIssueStatus(issueId: String, newStatus: String) {
    dao.updateStatus(issueId, newStatus)
  }

  suspend fun upvoteIssue(issueId: String) {
    dao.incrementUpvote(issueId)
  }

  suspend fun deleteIssue(issueId: String) {
    dao.deleteIssue(issueId)
  }

  suspend fun getUnsyncedIssues(): List<CivicIssueItem> {
    return dao.getUnsyncedIssues().map { it.toModel() }
  }

  suspend fun markAsSynced(issueId: String) {
    dao.markAsSynced(issueId)
  }

  suspend fun populateInitialDataIfEmpty() {
    val existing = dao.getAllIssues().map { it.isNotEmpty() }
    val hasData = existing.firstOrNull() ?: false
    if (hasData) return

    val initialData = listOf(
      CivicIssueItem(
        id = "CFX-20260827-01",
        title = "Large Pothole Hazard",
        category = "Roads",
        timeAgo = "2h ago",
        description = "Deep cavity and collapsed tarmac on Main Street intersection, posing severe safety risks to two-wheelers.",
        imageUrl = "https://images.unsplash.com/photo-1584467541268-b040f83be3fd?q=80&w=600&auto=format&fit=crop",
        aiAccuracy = 96,
        severity = "Critical",
        status = "In Progress",
        upvotes = 24,
        isVerified = true,
        locationName = "Main Street, Sector 4",
        latitude = 17.3850,
        longitude = 78.4867,
        municipalDepartment = "Public Works Department (PWD)",
        createdAt = System.currentTimeMillis() - 7200000
      ),
      CivicIssueItem(
        id = "CFX-20260827-02",
        title = "Garbage Overflow & Dump",
        category = "Waste",
        timeAgo = "4h ago",
        description = "Public dumpsters overflowing onto pedestrian sidewalk, emitting foul odor and obstructing access.",
        imageUrl = "https://images.unsplash.com/photo-1611284446314-60a58ac0deb9?q=80&w=600&auto=format&fit=crop",
        aiAccuracy = 92,
        severity = "High",
        status = "Reported",
        upvotes = 11,
        isVerified = false,
        locationName = "Oakridge Park Ave",
        latitude = 17.3912,
        longitude = 78.4720,
        municipalDepartment = "Municipal Solid Waste Management",
        createdAt = System.currentTimeMillis() - 14400000
      ),
      CivicIssueItem(
        id = "CFX-20260827-03",
        title = "Open Drainage Hazard",
        category = "Drainage",
        timeAgo = "1d ago",
        description = "Missing concrete storm cover on pedestrian walkway near Central Metro Station. Severe danger at night.",
        imageUrl = "https://images.unsplash.com/photo-1541888946425-d0fbb180c5f5?q=80&w=600&auto=format&fit=crop",
        aiAccuracy = 98,
        severity = "Critical",
        status = "Verified",
        upvotes = 38,
        isVerified = true,
        locationName = "Central Metro Gate 2",
        latitude = 17.3789,
        longitude = 78.4910,
        municipalDepartment = "Urban Drainage & Sewerage Board",
        createdAt = System.currentTimeMillis() - 86400000
      ),
      CivicIssueItem(
        id = "CFX-20260827-04",
        title = "Broken Streetlight Mast",
        category = "Lighting",
        timeAgo = "3d ago",
        description = "High-voltage pole luminaire dark for four consecutive nights. Dim pathway near elementary school.",
        imageUrl = "https://images.unsplash.com/photo-1517457373958-b7bdd4587205?q=80&w=600&auto=format&fit=crop",
        aiAccuracy = 94,
        severity = "Medium",
        status = "Resolved",
        upvotes = 19,
        isVerified = true,
        locationName = "Parkside Blvd, Pole #42",
        latitude = 17.3820,
        longitude = 78.4800,
        municipalDepartment = "Electrical & Lighting Division",
        createdAt = System.currentTimeMillis() - 259200000
      )
    )

    initialData.forEach {
      insertIssue(it)
    }
  }
}

