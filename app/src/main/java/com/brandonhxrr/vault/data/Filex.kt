package com.brandonhxrr.vault.data

public class SharedFile {
    var id = ""
    var name = ""
    var date = ""
    var author_public_key = ""
    var fileURL = ""
    var type = ""
    var signature = ""
    var author = ""

    constructor()
    constructor(
        id: String,
        name: String,
        date: String,
        author_public_key: String,
        fileURL: String,
        type: String,
        signature: String,
        author: String
    ) {
        this.id = id
        this.name = name
        this.date = date
        this.author_public_key = author_public_key
        this.fileURL = fileURL
        this.type = type
        this.signature = signature
        this.author = author
    }


}