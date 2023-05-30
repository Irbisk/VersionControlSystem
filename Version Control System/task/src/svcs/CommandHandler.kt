package svcs

import java.io.File
import java.security.MessageDigest

class CommandHandler () {
    companion object {
        fun handle(input: String) {
            if (input == "--help" || input == "") {
                help()
            } else {
                process(input)
            }
        }
    }
}

private fun process(input: String) {
    val array = input.split(" ")
    val command = Command.values().find { array[0].replace("-", "") in it.name.lowercase() }
    var info = ""
    if (array.size > 1) {
        info = input.substringAfter(" ")
    }

    if (Command.values().contains(command)) {
        when(command) {
            Command.CONFIG -> command.config(info)
            Command.ADD -> command.add(info)
            Command.COMMIT -> command.commit(info)
            Command.LOG -> command.log()
            Command.CHECKOUT -> command.checkout(info)
            else -> println(command!!.description)

        }
    } else {
        println("'${array[0]}' is not a SVCS command.")
    }
}

private fun Command.commit(info: String) {
    if (info.isEmpty()) {
        println("Message was not passed.")
        return
    }
    val fileNamesList = FileHandler.readVCSFile("index.txt")
    var changed = false
    val commitsDir = File("vcs\\commits\\")
    if (commitsDir.listFiles()!!.isEmpty()) {
        for (fileName in fileNamesList) {
            val file = File(fileName)
            val commitName = getHashCode(file)
            val commitFolder = File("vcs\\commits\\$commitName\\")
                commitFolder.mkdir()
                fileNamesList.forEach {
                    File(it).copyTo(File(commitFolder.path + "\\" + it), true)
                }
                addToLog(commitName, info)
                changed = true
                break
        }
    } else {
        val lastCommit = FileHandler.readVCSFile("log.txt")[0].substringAfter(" ")
        val lastCommitFolder = File("vcs\\commits\\$lastCommit\\")
        for (fileName in fileNamesList) {
            val fileNew = File(fileName)
            val fileOld = File(lastCommitFolder.path + "\\" + fileName)
            if (getHashCode(fileOld) != getHashCode(fileNew)) {
                val commitName = getHashCode(fileNew)
                val commitFolder = File("vcs\\commits\\$commitName\\")
                commitFolder.mkdir()
                fileNamesList.forEach { File(it).copyTo(File(commitFolder.path + "\\" + it), true) }
                changed = true
                addToLog(commitName, info)
                break
            }
        }
    }
    if (changed) {
        println("Changes are committed.")
    } else {
        println("Nothing to commit.")
    }

}

private fun addToLog(commitName: String, info: String) {
    val log = FileHandler.readVCSFile("log.txt").toMutableList()
    log.add(0, "$info\n")
    log.add(0,"Author: ${FileHandler.readVCSFile("config.txt")[0]}")
    log.add(0, "commit $commitName")

    FileHandler.changeVCSFile(log.joinToString("\n"), "log.txt")

}

private fun Command.log() {
    val log = FileHandler.readVCSFile("log.txt")
    if (log.isEmpty()) {
        println("No commits yet.")
    } else {
        println(log.joinToString("\n"))
    }
}

private fun Command.checkout(commitName: String) {
    if (commitName.isEmpty()) {
        println("Commit id was not passed.")
        return
    }
    if (File("vcs\\commits\\").list()!!.contains(commitName)) {
        val commits = "vcs\\commits\\$commitName\\"
        val fileNamesList = FileHandler.readVCSFile("index.txt")
        for (fileName in fileNamesList) {
            File(commits + fileName).copyTo(File(fileName), true)
        }
        println("Switched to commit $commitName.")
    } else {
        println("Commit does not exist.")
    }


}

private fun Command.config(info: String) {
    when {
        info.isEmpty() -> {
            val content = FileHandler.readVCSFile("config.txt")
            if (content.isEmpty()) {
                println("Please, tell me who you are.")
            } else {
                println("The username is ${content[0]}.")
            }
        }
        else -> {
            FileHandler.changeVCSFile(info, "config.txt")
            println("The username is $info.")

        }
    }
}

private fun Command.add(info: String) {
    when {
        info.isEmpty() -> {
            val content = FileHandler.readVCSFile("index.txt")
            if (content.isEmpty()) {
                println(this.description)
            } else {
                println("Tracked files:\n${content.joinToString("\n")}")
            }
        }
        else -> {
            if (FileHandler.fileExists(info)) {
                FileHandler.addToVCSFile(info, "index.txt")
                println("The file '$info' is tracked.")
            } else {
                println("Can't find '$info'.")
            }
        }
    }
}

private fun help() {
    println("These are SVCS commands:\n" +
            "config     Get and set a username.\n" +
            "add        Add a file to the index.\n" +
            "log        Show commit logs.\n" +
            "commit     Save changes.\n" +
            "checkout   Restore a file.")
}

fun getHashCode(file: File): String {
    val md = MessageDigest.getInstance("SHA-1")
    val hash = md.digest(file.readBytes())
    return hash.joinToString("") { "%02x".format(it) }
}
