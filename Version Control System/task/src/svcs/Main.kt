package svcs

fun main(args: Array<String>) {
    FileHandler.createDirectory("vcs")
    FileHandler.createDirectory("commits")
    FileHandler.createVCSFile("log.txt")
    FileHandler.createVCSFile("config.txt")
    FileHandler.createVCSFile("index.txt")
    CommandHandler.handle(args.joinToString(" "))



}

fun check() {
    FileHandler.createDirectory("vcs")
    FileHandler.createDirectory("commits")
    FileHandler.createVCSFile("log.txt")
    FileHandler.createVCSFile("config.txt")
    FileHandler.createVCSFile("index.txt")
    while (true) {
        CommandHandler.handle(readln())
    }
}