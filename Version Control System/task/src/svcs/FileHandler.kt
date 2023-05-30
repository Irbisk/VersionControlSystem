package svcs

import java.io.File

const val directory = "vcs\\"
const val commitDirectory = directory + "commits\\"
class FileHandler {


    companion object {

        fun createFile(fileName: String) {
            val file = File(fileName)
            file.createNewFile()
        }

        fun fileExists(fileName: String): Boolean {
            val file = File(fileName)
            return file.exists()
        }

        fun addToVCSFile(info: String, fileName: String) {
            val file = File(directory + fileName)
            if (fileName == "log.txt") {
                val text = readVCSFile("log.txt").joinToString("\n")
                file.writeText(info + "\n")
                file.appendText(text)
            } else {
                file.appendText("$info\n")
            }

        }

        fun changeVCSFile(info: String, fileName: String) {
            val file = File(directory + fileName)
            file.writeText(info)
        }
        fun createVCSFile(fileName: String) {
            val file = File(directory + fileName)
            if (!file.exists()) {
                file.createNewFile()
            }
        }

        fun readVCSFile(fileName: String): List<String> {
            val file = File(directory + fileName)
            return file.readLines()
        }

        fun getFile(fileName: String): File {
            return File(fileName)
        }

        fun createDirectory(typeOrCode: String) {
            var dir = File("")
            dir = when (typeOrCode) {
                "vcs" -> {
                    File(directory)
                }
                "commits" -> {
                    File(commitDirectory)
                }
                else -> {
                    File(commitDirectory + typeOrCode)
                }
            }
            if (!dir.exists()) {
                dir.mkdir()
            }
        }

    }
}