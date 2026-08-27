package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.CivicDatabase
import com.example.data.local.CivicIssueDao
import com.example.data.local.CivicIssueEntity
import com.example.data.model.CivicIssueItem
import com.example.data.model.CivicUser
import com.example.ui.viewmodel.CivicViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  private lateinit var db: CivicDatabase
  private lateinit var dao: CivicIssueDao

  @Before
  fun createDb() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    db = Room.inMemoryDatabaseBuilder(context, CivicDatabase::class.java)
      .allowMainThreadQueries()
      .build()
    dao = db.civicIssueDao()
  }

  @After
  fun closeDb() {
    db.close()
  }

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("CivicFix AI", appName)
  }

  @Test
  fun `default civic user profile is initialized`() {
    val user = CivicUser()
    assertEquals("Sarah Jenkins", user.name)
    assertEquals("sarah.jenkins@gmail.com", user.email)
    assertEquals(true, user.isGoogleAuthenticated)
    assertTrue(user.points >= 0)
  }

  @Test
  fun `test civic view model category updates`() {
    val application = ApplicationProvider.getApplicationContext<android.app.Application>()
    val viewModel = CivicViewModel(application)
    assertEquals("All", viewModel.selectedCategory.value)
    viewModel.setCategory("Roads")
    assertEquals("Roads", viewModel.selectedCategory.value)
  }

  @Test
  fun `room dao caching and upvoting offline support`() = runBlocking {
    val sampleIssue = CivicIssueEntity(
      id = "TEST-01",
      title = "Pothole on 5th Ave",
      category = "Roads",
      timeAgo = "10m ago",
      description = "Large pothole affecting bike lane",
      imageUrl = "",
      aiAccuracy = 95,
      severity = "High",
      status = "Reported",
      upvotes = 3,
      isVerified = false,
      locationName = "5th Ave",
      latitude = 17.385,
      longitude = 78.486,
      municipalDepartment = "PWD",
      createdAt = System.currentTimeMillis(),
      isSynced = true
    )

    dao.insertIssue(sampleIssue)

    val list = dao.getAllIssues().first()
    assertEquals(1, list.size)
    assertEquals("Pothole on 5th Ave", list[0].title)

    dao.incrementUpvote("TEST-01")
    val updated = dao.getIssueById("TEST-01")
    assertNotNull(updated)
    assertEquals(4, updated?.upvotes)

    dao.updateStatus("TEST-01", "In Progress")
    val inProgressIssue = dao.getIssueById("TEST-01")
    assertEquals("In Progress", inProgressIssue?.status)
  }
}


