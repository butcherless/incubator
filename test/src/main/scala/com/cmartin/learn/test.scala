package com.cmartin.learn

import java.time.{LocalDate, LocalTime}

import scala.concurrent.duration._

package object test {

  object Constants {
    val registrationLVL = "ec-lvl"
    val registrationMIG = "ec-mig"
    val registrationMNS = "ec-mns"
    val barajasIataCode = "MAD"

    val madDestinationCount = 4
    val tableCount          = 7

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

    val flightUx9059 = ("ux9059", "aea9059", LocalTime.of(7, 5), LocalTime.of(8, 55))
    val flightD85756 = ("d85756", "ibk6ty", LocalTime.of(8, 0), LocalTime.of(10, 25))
    val flightI23942 = ("i23942", "ibs3942", LocalTime.of(8, 40), LocalTime.of(10, 30))

    val journeyTime = (LocalTime.of(7, 19), LocalTime.of(8, 41))

  }

  //TODO refactor to common
  object TypeCodes {
    val AIRBUS_320     = "A320"
    val AIRBUS_330_200 = "A332"
    val AIRBUS_350_900 = "A359"
    val BOEING_737_800 = "B738"
    val BOEING_787_800 = "B788"
  }

}

/*
  _____         _     ____        _
 |_   _|__  ___| |_  |  _ \  __ _| |_ __ _
   | |/ _ \/ __| __| | | | |/ _` | __/ _` |
   | |  __/\__ \ |_  | |_| | (_| | || (_| |
   |_|\___||___/\__| |____/ \__,_|\__\__,_|

 */
