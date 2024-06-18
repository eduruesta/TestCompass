package com.example.compasstest

// Create a new file within the test folder of your project, Calendar.kt. You can paste this entire comment into
// that file so you can easily refer to it throughout the exercise.
//
// Implement a class `Calendar` that is responsible for maintaining a
// collection of appointments. Each Appointment should contain:
//
// - an appointment name
// - a start time
// - an end time
// - a client (the person with whom the agent will meet)
//
// The calendar should define two methods:
//
// 1. a method to add new appointments. Appointments may not overlap.
//    Examples:
//    - We try to add the appointments 10am-11am and 11am-12pm:
//      -> Both are added successfully
//    - We try to add the appointments 10am-11am and 10:30am-11:30pm
//      -> Only the first one is added successfully
// 2. a method to return a list of appointments between a given start and
// end time, sorted by start time
//    Example:
//      3 appointments have been added already:
//      - 10am-11am
//      - 11am-12pm
//      - 12pm-1pm
//      If the date range given is 10:30am to 12pm, the following appointment
//      should be returned:
//        - 10am-11am
//        - 11am-12pm
//
// You should validate your implementation by writing unit tests.
//
// Hint: you can easily create fake dates for your unit tests like this:
//
//   val date1 = Date(0)
//   val date2 = Date(100)

import org.junit.Before
import org.junit.Test
import java.util.Date
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue


data class Appointment(
    val appointmentName: String,
    val startTime: Date,
    val endTime: Date,
    val client: String
)

class Calendar {
    private val appointments = mutableListOf<Appointment>()

    fun addAppointment(appointment: Appointment): Boolean {
        for (existingAppointment in appointments) {
            if (appointment.startTime < existingAppointment.endTime && existingAppointment.startTime < appointment.endTime) {
                return false
            }
        }
        appointments.add(appointment)
        return true
    }

    fun appointmentBetweenGivenDate(startTime: Date, endTime: Date): List<Appointment> {
        return appointments.filter { it.startTime >= startTime && it.endTime <= endTime }
            .sortedBy { it.startTime }
    }
}


// Crearia un test donde pueda agregar un appointment si la lista esta vacia
// Un test donde no pueda agregar un elemento si los horarios se superponen
// Un test donde pueda agregar un appointment si no hay algun horario en la lista que se superponga con el appointment que quiero agregar

class CalendarTest {

    private lateinit var calendar: Calendar

    @Before
    fun setup() {
        calendar = Calendar()
    }

    @Test
    fun `should add appointment if list is empty`() {
        val appointment = Appointment("Meeting", Date(0), Date(100), "Client")
        val result = calendar.addAppointment(appointment)
        assertTrue(result)
    }

    @Test
    fun `should not add appointment if times overlap`() {
        val appointment1 = Appointment("Meeting 1", Date(0), Date(100), "Client 1")
        val appointment2 = Appointment("Meeting 2", Date(50), Date(150), "Client 2")
        calendar.addAppointment(appointment1)
        val result = calendar.addAppointment(appointment2)
        assertFalse(result)
    }

    @Test
    fun `should add appointment if no overlapping times in list`() {
        val appointment1 = Appointment("Meeting 1", Date(0), Date(100), "Client 1")
        val appointment2 = Appointment("Meeting 2", Date(100), Date(200), "Client 2")
        calendar.addAppointment(appointment1)
        val result = calendar.addAppointment(appointment2)
        assertTrue(result)
    }
}
