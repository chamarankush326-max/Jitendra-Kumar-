package com.example

import com.example.data.model.OperatorType
import com.example.data.model.PlanCategory
import com.example.data.model.RechargePlan
import com.example.data.repository.AppLanguage
import com.example.data.repository.LanguageManager
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {

  @Test
  fun testLanguageManagerTranslations() {
    val enAppName = LanguageManager.string("app_name", AppLanguage.ENGLISH)
    val hiAppName = LanguageManager.string("app_name", AppLanguage.HINDI)

    assertEquals("Apna Social", enAppName)
    assertEquals("अपना सोशल", hiAppName)

    val enRecharge = LanguageManager.string("recharge_title", AppLanguage.ENGLISH)
    val hiRecharge = LanguageManager.string("recharge_title", AppLanguage.HINDI)

    assertEquals("Mobile Recharge", enRecharge)
    assertEquals("मोबाइल रिचार्ज", hiRecharge)
  }

  @Test
  fun testRechargePlanData() {
    val plan = RechargePlan(
      id = "test_1",
      operator = OperatorType.JIO,
      category = PlanCategory.RECOMMENDED,
      price = 299,
      validityDays = 28,
      dataDescription = "1.5GB/day",
      voiceCalls = "Unlimited",
      sms = "100 SMS/day"
    )

    assertEquals(OperatorType.JIO, plan.operator)
    assertEquals(299, plan.price)
    assertEquals(28, plan.validityDays)
  }

  @Test
  fun testAllTelecomOperatorsSupported() {
    val operators = OperatorType.values()
    assertTrue(operators.contains(OperatorType.JIO))
    assertTrue(operators.contains(OperatorType.AIRTEL))
    assertTrue(operators.contains(OperatorType.VI))
    assertTrue(operators.contains(OperatorType.BSNL))
  }
}
