package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.CivicIssueItem

/**
 * Room Database Entity representing a cached civic issue.
 * Supports offline storage, local mutations, and synchronization states.
 */
@Entity(tableName = "civic_issues")
data class CivicIssueEntity(
  @PrimaryKey val id: String,
  val title: String,
  val category: String,
  val timeAgo: String,
  val description: String,
  val imageUrl: String,
  val aiAccuracy: Int,
  val severity: String,
  val status: String,
  val upvotes: Int,
  val isVerified: Boolean,
  val locationName: String,
  val latitude: Double,
  val longitude: Double,
  val municipalDepartment: String,
  val createdAt: Long,
  val isSynced: Boolean = true
) {
  fun toModel(): CivicIssueItem = CivicIssueItem(
    id = id,
    title = title,
    category = category,
    timeAgo = timeAgo,
    description = description,
    imageUrl = imageUrl,
    aiAccuracy = aiAccuracy,
    severity = severity,
    status = status,
    upvotes = upvotes,
    isVerified = isVerified,
    locationName = locationName,
    latitude = latitude,
    longitude = longitude,
    municipalDepartment = municipalDepartment,
    createdAt = createdAt
  )

  companion object {
    fun fromModel(model: CivicIssueItem, isSynced: Boolean = true): CivicIssueEntity = CivicIssueEntity(
      id = model.id,
      title = model.title,
      category = model.category,
      timeAgo = model.timeAgo,
      description = model.description,
      imageUrl = model.imageUrl,
      aiAccuracy = model.aiAccuracy,
      severity = model.severity,
      status = model.status,
      upvotes = model.upvotes,
      isVerified = model.isVerified,
      locationName = model.locationName,
      latitude = model.latitude,
      longitude = model.longitude,
      municipalDepartment = model.municipalDepartment,
      createdAt = model.createdAt,
      isSynced = isSynced
    )
  }
}

