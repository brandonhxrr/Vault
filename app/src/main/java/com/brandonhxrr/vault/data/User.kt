package com.brandonhxrr.vault.data

class User {
    var id: String = ""
    var name: String = ""
    var email: String = ""
    var photoURL: String = ""
    var publicKey: String = ""

    constructor()

    constructor(id: String, name: String, email: String, photoURL: String, publicKey: String) {
        this.id = id
        this.name = name
        this.email = email
        this.photoURL = photoURL
        this.publicKey = publicKey
    }
}