package com.example.data.model

data class CivicIssueItem(
  val id: String,
  val title: String,
  val category: String,
  val timeAgo: String,
  val description: String,
  val imageUrl: String,
  val aiAccuracy: Int = 95,
  val severity: String = "Medium", // Low, Medium, High, Critical
  val status: String = "Reported", // Reported, In Progress, Verified, Resolved
  val upvotes: Int = 0,
  val isVerified: Boolean = false,
  val locationName: String = "Main Street, Sector 4",
  val latitude: Double = 17.3850,
  val longitude: Double = 78.4867,
  val municipalDepartment: String = "Public Works Department (PWD)",
  val createdAt: Long = System.currentTimeMillis()
)

data class CivicUser(
  val id: String = "google_user_01",
  val name: String = "Sarah Jenkins",
  val email: String = "sarah.jenkins@gmail.com",
  val avatarUrl: String? = null,
  val points: Int = 1250,
  val level: String = "Guardian of the City (Level 4)",
  val reportsCount: Int = 12,
  val resolvedCount: Int = 8,
  val isGoogleAuthenticated: Boolean = true
)

data class ChatMessage(
  val id: String = java.util.UUID.randomUUID().toString(),
  val sender: MessageSender,
  val text: String,
  val timestamp: Long = System.currentTimeMillis()
)

enum class MessageSender {
  USER, GEMINI
}

data class CivicAiAnalysisResult(
  val title: String,
  val category: String,
  val severity: String,
  val department: String,
  val estimatedResolutionTime: String,
  val aiConfidence: Int,
  val formalDescription: String
)
