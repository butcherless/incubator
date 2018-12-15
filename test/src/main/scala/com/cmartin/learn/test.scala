package com.cmartin.learn

import java.time.LocalDate

import scala.concurrent.duration._

package object test {

  object Constants {
    val registrationLVL = "ec-lvl"
    val registrationMIG = "ec-mig"
    val registrationMNS = "ec-mns"
    val barajasIataCode = "MAD"

    val madDestinationCount = 4
    val tableCount = 7

    val waitTimeout = 5.second

    val esCountry = ("Spain", "ES")
    val noCountry = ("Norway", "NO")
    val ukCountry = ("United Kingdom", "UK")
    val brCountry = ("Brasil", "BR")

    val madAirport = ("Madrid Barajas", barajasIataCode, "LEMD")
    val tfnAirport = ("Tenerife Norte", "TFN", "GXCO")
    val bcnAirport = ("Barcelona International", "BCN", "LEBL")
    val lhrAirport = ("London Heathrow", "LHR", "EGLL")
    val lgwAirport = ("London Gatwick", "LGW", "EGKK")
    val bsbAirport = ("Presidente Juscelino Kubistschek International", "BSB", "SBBR")
    val ssaAirport = ("Deputado Luiz Eduardo Magalhães International", "SSA", "SBSV")
    val gigAirport = ("Tom Jobim International Airport", "GIG", "SBGL")

    val aeaAirline = ("Air Europa", LocalDate.of(1986, 11, 21))
    val ibsAirline = ("Iberia Express", LocalDate.of(2011, 10, 6))
    val ibkAirline = ("Norwegian Air International", LocalDate.of(1993, 1, 22))

    val ecMigAircraft = (TypeCodes.BOEING_787_800, Constants.registrationMIG)
    val ecLvlAircraft = (TypeCodes.AIRBUS_330_200, Constants.registrationLVL)

  }

  //TODO refactor to common
  object TypeCodes {
    val AIRBUS_320 = "A320"
    val AIRBUS_330_200 = "A332"
    val AIRBUS_350_900 = "A359"
    val BOEING_737_800 = "B738"
    val BOEING_787_800 = "B788"
  }

}
