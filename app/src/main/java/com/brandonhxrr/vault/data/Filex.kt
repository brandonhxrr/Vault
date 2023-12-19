package com.brandonhxrr.vault.data

public class SharedFile {
    var id = ""
    var name = ""
    var date = ""
    var author = ""
    var fileURL = ""
    var type = ""

    constructor()

    constructor(id: String, name: String, date: String, author: String, fileURL: String, type: String) {
        this.id = id
        this.name = name
        this.date = date
        this.author = author
        this.fileURL = fileURL
        this.type = type
    }
}