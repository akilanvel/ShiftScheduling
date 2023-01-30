# ShiftScheduling

This is a Java program that schedules shifts based on employee availabilities and types of employees. The University Emergency Medical Response group has these criteria.

There are three types of employees. The first tpye of employee is called an EMT-2. They have no shift restrictions. The second type of employee is called an FTO. The third type of employee is called an EMT-1. Each EMT-1 will be paired with an FTO and they both will be together for their shift. Shifts run from 8:20 AM to 11:20 PM on all 7 days of the week in three hour intervals. EMT-2s are assigned one shift per week. EMT-1 and FTO pairs are assigned two shifts (each lasting three hours) per week. 

This program takes into account each employee's availability to create an ideal pairings of EMT-1s and FTOs. It will then generate an ideal schedule using the EMT-2s' and EMT-1/FTO pairs' availabilities. It spreads out shifts as much as possible. Finally, the pairings that were generated and the full schedule is printed out. If the generated schedule is not desired, the program can be run again to output a slightly different but still ideal schedule.
