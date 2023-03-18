package com.cmartin.learn.test

import java.time.{ LocalDate, LocalTime }
import scala.concurrent.duration._

object AviationData {
  object Constants {
    val registrationLVL = "ec-lvl"
    val registrationMIG = "ec-mig"
    val registrationMNS = "ec-mns"
    val barajasIataCode = "MAD"

    val madDestinationCount = 4
    val tableCount          = 7

    val waitTimeout: FiniteDuration = 5.seconds

    val esCountry: (String, String) = ("Spain", "ES")
    val noCountry: (String, String) = ("Norway", "NO")
    val ukCountry: (String, String) = ("United Kingdom", "UK")
    val brCountry: (String, String) = ("Brasil", "BR")

    val madAirport: (String, String, String) = ("Madrid Barajas", barajasIataCode, "LEMD")
    val tfnAirport: (String, String, String) = ("Tenerife Norte", "TFN", "GXCO")
    val bcnAirport: (String, String, String) = ("Barcelona International", "BCN", "LEBL")
    val lhrAirport: (String, String, String) = ("London Heathrow", "LHR", "EGLL")
    val lgwAirport: (String, String, String) = ("London Gatwick", "LGW", "EGKK")
    val bsbAirport: (String, String, String) = ("Presidente Juscelino Kubistschek International", "BSB", "SBBR")
    val ssaAirport: (String, String, String) = ("Deputado Luiz Eduardo Magalhães International", "SSA", "SBSV")
    val gigAirport: (String, String, String) = ("Tom Jobim International Airport", "GIG", "SBGL")

    val aeaAirline: (String, LocalDate) = ("Air Europa", LocalDate.of(1986, 11, 21))
    val ibsAirline: (String, LocalDate) = ("Iberia Express", LocalDate.of(2011, 10, 6))
    val ibkAirline: (String, LocalDate) = ("Norwegian Air International", LocalDate.of(1993, 1, 22))

    val ecMigAircraft: (String, String) = (TypeCodes.BOEING_787_800, Constants.registrationMIG)
    val ecLvlAircraft: (String, String) = (TypeCodes.AIRBUS_330_200, Constants.registrationLVL)

    val flightUx9059: (String, String, LocalTime, LocalTime) =
      ("ux9059", "aea9059", LocalTime.of(7, 5), LocalTime.of(8, 55))
    val flightD85756: (String, String, LocalTime, LocalTime) =
      ("d85756", "ibk6ty", LocalTime.of(8, 0), LocalTime.of(10, 25))
    val flightI23942: (String, String, LocalTime, LocalTime) =
      ("i23942", "ibs3942", LocalTime.of(8, 40), LocalTime.of(10, 30))

    val journeyTime: (LocalTime, LocalTime) = (LocalTime.of(7, 19), LocalTime.of(8, 41))

    val madTotfnDistance: Double = 957.0
  }

  // TODO refactor to common
  object TypeCodes {
    val AIRBUS_320     = "A320"
    val AIRBUS_330_200 = "A332"
    val AIRBUS_350_900 = "A359"
    val BOEING_737_800 = "B738"
    val BOEING_787_800 = "B788"
  }
}
