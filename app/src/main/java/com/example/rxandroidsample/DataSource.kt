package com.example.rxandroidsample

import com.example.rxandroidsample.model.Task

class DataSource {
    companion object{
        private val taskList = ArrayList<Task>()

        fun createTaskList(): ArrayList<Task> {
            taskList.add(
                Task(
                    "Take out the trash",
                    true,
                    3
                )
            )
            taskList.add(
                Task(
                    "Walk the dog",
                    false,
                    2
                )
            )
            taskList.add(
                Task(
                    "Make my bed",
                    true,
                    1
                )
            )
            taskList.add(
                Task(
                    "Unload the dishwasher",
                    false,
                    0
                )
            )
            taskList.add(
                Task(
                    "Make dinner",
                    true,
                    5
                )
            )
            taskList.add(
                Task(
                    "Make dinner",
                    true,
                    5
                )
            )
            return taskList
        }

    }
}