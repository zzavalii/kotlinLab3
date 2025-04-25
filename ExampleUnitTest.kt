import kotlinx.coroutines.*
import kotlin.math.round

class Student(name: String) {
    private var _name: String = name.trim().replaceFirstChar { it.uppercase() }
    private var _age: Int = 0
    private var _grades: List<Int> = listOf()

    init {
        println("Student created: $_name")
    }

    constructor(name: String, age: Int, grades: List<Int>) : this(name) {
        this.age = age
        this.grades = grades
    }

    var name: String
        get() = _name
        set(value) {
            _name = value.trim().replaceFirstChar { it.uppercase() }
        }

    var age: Int
        get() = _age
        set(value) {
            if (value >= 0) _age = value
        }

    var grades: List<Int>
        get() = _grades
        private set(value) {
            _grades = value
        }

    val isAdult: Boolean
        get() = _age >= 18

    val status: String by lazy {
        if (isAdult) "Adult" else "Minor"
    }

    fun getAverage(): Double {
        return if (_grades.isNotEmpty()) {
            round(_grades.average() * 10) / 10
        } else 0.0
    }
    fun processGrades(operation: (Int) -> Int) {
        _grades = _grades.map(operation)
    }

    fun updateGrades(newGrades: List<Int>) {
        _grades = newGrades
    }

    operator fun plus(other: Student): Student {
        val combinedGrades = this._grades + other._grades
        return Student(name = "${this.name} & ${other.name}", age = 0, grades = combinedGrades)
    }

    operator fun times(multiplier: Int): Student {
        val newGrades = _grades.map { it * multiplier }
        return Student(name = this.name, age = this.age, grades = newGrades)
    }

    override operator fun equals(other: Any?): Boolean {
        return other is Student && this.name == other.name && this.getAverage() == other.getAverage()
    }

    override fun toString(): String {
        return "Student(name='$name', age=$age, grades=$grades, avg=${getAverage()}, status=$status)"
    }
}

class Group(vararg students: Student) { //необмежена кількість
    private val studentList = students.toList()

    operator fun get(index: Int): Student {
        return studentList[index]
    }

    fun getTopStudent(): Student? {
        return studentList.maxByOrNull { it.getAverage() }
    }
}

suspend fun fetchGradesFromServer(): List<Int> {
    delay(2000)
    return listOf(90, 86, 60)
}

fun main() = runBlocking {
    val student1 = Student("vlad")
    val student2 = Student(name = "Igor", age = 19, grades = listOf(90, 86, 88))

    println(student1)
    println(student2)

    student1.age = 18
    println("${student1.name} is adult? ${student1.isAdult}, status: ${student1.status}")

    val fetchedGrades = async { fetchGradesFromServer() }
    val newGrades = fetchedGrades.await()
    student1.updateGrades(newGrades)

    println("Updated grades for ${student1.name}: ${student1.grades}, average: ${student1.getAverage()}")

    val mergedStudent = student1 + student2
    println("Merged student: $mergedStudent")

    val improvedStudent = student1 * 2
    println("Improved grades: ${improvedStudent.grades}")

    val group = Group(student1, student2, mergedStudent)
    println("Top student in the group: ${group.getTopStudent()}")
}
