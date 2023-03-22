# ICBC Driving Test Appointment Checker

## The Story Behind
"One can't simply book an appointment to their preference with ICBC". Unfortunately aforementioned Canadian agency doesn't provide any means of notifications about new appointments. This led to the development of this simple tool that does the following:

- Authenticates with ICBC
- Requests appointments for the specified locations and date range
- Sends them to a Telegram chat

## Implementation Details
Initially it started as a full-blown enterprise project, but later I decided to simplify it.
You can find all logic in <code>IcbcAppointmentChecker</code> class.


## How it Ended?
During one of the test launches the app managed to find an appointment that suits me perfectly, so I went ahead and booked it.
Now, if I continued building a spaceship for such a simple tool, I might've missed my ideal appointment date. This is a good example of complexity vs feasibility.