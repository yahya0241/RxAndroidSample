package com.example.rxandroidsample.model

class Task(var description :String, var isComplete: Boolean, var priority:Int) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Task

        if (description != other.description) return false
        if (isComplete != other.isComplete) return false
        if (priority != other.priority) return false

        return true
    }

    override fun hashCode(): Int {
        var result = description.hashCode()
        result = 31 * result + isComplete.hashCode()
        result = 31 * result + priority
        return result
    }
}