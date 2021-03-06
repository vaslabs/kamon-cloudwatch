package kamon.cloudwatch

import java.time.Instant
import java.util.Date

import com.amazonaws.services.cloudwatch.model.StandardUnit

import kamon.metric.MeasurementUnit

import org.scalatest.{FlatSpec, Matchers}

class DatumConversionSpec extends FlatSpec with Matchers {

  "datums" must "ignore distributions without samples" in {
    val snapshot = PeriodSnapshotBuilder()
      .rangeSampler("foo", Map.empty)
      .build()

    val convertedDatums = datums(snapshot)

    convertedDatums shouldBe Vector.empty
  }

  it must "use the 'to' instant as the timestamp of the datum" in {
    val givenInstant = Instant.ofEpochMilli(98439)

    val snapshot = PeriodSnapshotBuilder()
      .to(givenInstant)
      .counter("foo", Map.empty, 2)
      .build()

    val convertedDatums = datums(snapshot)
    convertedDatums.size shouldBe 1
    convertedDatums(0).getTimestamp shouldBe Date.from(givenInstant)
  }

  it must "populate percentages" in {
    val snapshot = PeriodSnapshotBuilder()
      .counter("foo", Map.empty, 39, MeasurementUnit.percentage)
      .build()

    val convertedDatums = datums(snapshot)
    convertedDatums.size shouldBe 1
    convertedDatums(0).getUnit shouldBe StandardUnit.Percent.toString
  }

}
