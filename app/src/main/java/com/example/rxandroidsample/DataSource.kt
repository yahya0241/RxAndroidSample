package com.example.rxandroidsample

import com.example.rxandroidsample.model.Task

class DataSource {
    private val taskList = ArrayList<Task>()

    init {
        createTaskList()
    }

    public fun getTaskList(): ArrayList<Task> {
        return taskList;
    }

    private fun createTaskList(): ArrayList<Task> {
        taskList.add(
            Task(
                1,
                "Take out the trash",
                true,
                3
            )
        )
        taskList.add(
            Task(
                2,
                "Walk the dog",
                false,
                2
            )
        )
        taskList.add(
            Task(
                2,
                "Walk the dog",
                false,
                2
            )
        )
        taskList.add(
            Task(
                3,
                "Make my bed",
                true,
                1
            )
        )
        taskList.add(
            Task(
                4,
                "Unload the dishwasher",
                false,
                0
            )
        )
        taskList.add(
            Task(
                5,
                "Make launch",
                true,
                2
            )
        )
        taskList.add(
            Task(
                6,
                "Make dinner",
                true,
                4
            )
        )
        taskList.add(
            Task(
                7,
                "take shower",
                false,
                3
            )
        )
        taskList.add(
            Task(
                8,
                "watch tv",
                true,
                1
            )
        )
        taskList.add(
            Task(
                9,
                "practice",
                false,
                5
            )
        )
        taskList.add(
            Task(
                10,
                "study",
                true,
                2
            )
        )
        return taskList
    }
}