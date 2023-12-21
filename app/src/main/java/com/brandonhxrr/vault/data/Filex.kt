package com.brandonhxrr.vault.data

public class SharedFile {
    var id = ""
    var name = ""
    var date = ""
    var authorPublicKey = ""
    var fileURL = ""
    var type = ""
    var signature = ""
    var author = ""
    var authorId = ""

    constructor()
    constructor(
        id: String,
        name: String,
        date: String,
        authorPublicKey: String,
        fileURL: String,
        type: String,
        signature: String,
        author: String,
        authorId: String
    ) {
        this.id = id
        this.name = name
        this.date = date
        this.authorPublicKey = authorPublicKey
        this.fileURL = fileURL
        this.type = type
        this.signature = signature
        this.author = author
        this.authorId = authorId
    }


}